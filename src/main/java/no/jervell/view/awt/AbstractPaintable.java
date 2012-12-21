package no.jervell.view.awt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * A convenient base class for {@link Paintable} objects
 * that supports automatic rotation, scaling and alignment,
 * as well as graphical decorators like background etc.
 * Subclasses need only implement painting at a fixed scale,
 * orientation and position.
 *
 * @author Arne C. Jervell (arne@jervell.no)
 */
public abstract class AbstractPaintable implements Paintable
{
  private Rotation _rotation = Rotation.ZERO;
  private Scaling  _scaling  = Scaling.UNIFORM;
  private Anchor   _anchor   = Anchor.TOP_LEFT;

  public Rotation getRotation()
  {
    return _rotation==null ? Rotation.ZERO : _rotation;
  }

  public void setRotation( Rotation rotation )
  {
    this._rotation = rotation;
  }

  public Scaling getScaling()
  {
    return _scaling==null ? Scaling.NONE : _scaling;
  }

  public void setScaling( Scaling scaling )
  {
    this._scaling = scaling;
  }

  public Anchor getAnchor()
  {
    return _anchor==null ? Anchor.TOP_LEFT : _anchor;
  }

  public void setAnchor( Anchor anchor )
  {
    this._anchor = anchor;
  }

  /**
   * Get the actual area covered by this paintable element given a maximum area and a graphics context.
   * @param area maximum coverage
   * @param g    graphics context
   * @return the area actually covered by paintable element after scaling, rotation and alignment has been performed.
   */
  public Rectangle2D getAreaCovered( Graphics g, Rectangle2D area )
  {
    Rectangle2D.Double result = new Rectangle2D.Double();

    double      prefW   = getPreferredWidth( g );
    double      prefH   = getPreferredHeight( g );
    Dim2D       scale   = getScaling().getScaleFactors( area.getWidth(), area.getHeight(), prefW, prefH );
    double      actualW = prefW * scale.getWidth();
    double      actualH = prefH * scale.getHeight();
    Rectangle2D aligned = getAnchor().align( area.getX(), area.getY(), actualW-area.getWidth(), actualH-area.getHeight() );

    result.setFrame( aligned.getX(), aligned.getY(), area.getWidth()+aligned.getWidth(), area.getHeight()+aligned.getHeight() );

    return result;
  }

  interface FontCharacterFixer
  {
    public String fix( String s );
  }

  // TODO: Find a better solution to this character mapping issue
  private final static Map<String, FontCharacterFixer> fontCharacterFixers;
  static
  {
    fontCharacterFixers = new HashMap<String, FontCharacterFixer>();
    fontCharacterFixers.put( "Futura-Thin", new FontCharacterFixer()
    {
      public String fix( String s )
      {
        s = s.replace( "Æ", "Ã" );
        s = s.replace( "Ø", "Õ" );
        s = s.replace( "æ", "œ" );
        s = s.replace( "ø", "ª" );
        return s;
      }
    } );
  }

  public static String getFixedText( String text, Font font )
  {
    FontCharacterFixer fixer = fontCharacterFixers.get( font==null ? null : font.getFontName() );
    return fixer==null ? text : fixer.fix( text );
  }

  public final double getPreferredWidth( Graphics g )
  {
    switch( getRotation() )
    {
      case ZERO:
      case CW_180:
        return getWidth( g );

      default:
        return getHeight( g );
    }
  }

  public final double getPreferredHeight( Graphics g )
  {
    switch( getRotation() )
    {
      case ZERO:
      case CW_180:
        return getHeight( g );

      default:
        return getWidth( g );
    }
  }

  public final void paint( Graphics g, Rectangle2D loc, Anchor anchor )
  {
    paint( g, anchor.align( loc ) );
  }

  public final void paint( Graphics g, Rectangle2D loc )
  {
    if ( g instanceof Graphics2D )
    {
      Graphics2D g2 = (Graphics2D)g;
      AffineTransform orgT = g2.getTransform();

      AffineTransform newT = createTransformationMatrix( g2, loc, getRotation(), getScaling(), getAnchor() );
      g2.setTransform( newT );
      paint( g2 );
      g2.setTransform( orgT );
    }
    else
    {
      throw new IllegalArgumentException( "Graphics not instance of Graphics2D: " + g );
    }
  }

  protected AffineTransform createTransformationMatrix( Graphics2D g, Rectangle2D loc, Rotation rotation, Scaling scaling, Anchor anchor )
  {
    double          prefW  = getPreferredWidth( g );
    double          prefH  = getPreferredHeight( g );
    AffineTransform xform  = createTransformationMatrix( prefW, prefH, loc, rotation, scaling, anchor );
    AffineTransform result = new AffineTransform( g.getTransform() );
    result.concatenate( xform );
    return result;
  }

  /**
   * Create and return a transformation matrix that
   * transforms from <code>Graphics</code>-space to <code>Paintable</code>-space.
   * @param prefW    the preferred width of the object to be painted
   * @param prefH    the preferred height of the object to be painted
   * @param loc      the target location, i.e. the position and size where object will be painted
   * @param rotation rotation amount
   * @param scaling  scaling policy
   * @param anchor   alignment anchor
   * @return affine transformation
   */
  public static AffineTransform createTransformationMatrix( double prefW, double prefH, Rectangle2D loc, Rotation rotation, Scaling scaling, Anchor anchor )
  {
    Dim2D       scale   = scaling.getScaleFactors( loc.getWidth(), loc.getHeight(), prefW, prefH );
    double      actualW = prefW * scale.width;
    double      actualH = prefH * scale.height;
    Point2D     origin  = rotation.getOrigin( loc.getX(), loc.getY(), actualW, actualH );
    Rectangle2D aligned = anchor.align( origin.getX(), origin.getY(), actualW-loc.getWidth(), actualH-loc.getHeight() );

    AffineTransform newT = new AffineTransform();
    newT.translate( aligned.getX(), aligned.getY() );
    newT.scale( scale.getWidth(), scale.getHeight() );
    newT.rotate( rotation.getRadians() );
    return newT;
  }

  /**
   * Get paintable's width on given graphics.
   * @return width in local frame coordinates
   * @param g target graphics
   */
  protected abstract double getWidth( Graphics g );

  /**
   * Get paintable's height on given graphics.
   * @return height in local frame coordinates
   * @param g target graphics
   */
  protected abstract double getHeight( Graphics g );

  /**
   * Paint this object in it's local frame.
   * Painting is performed with the local upper-left corner at <code>(0,0)</code>.
   * The size (and lower-right corner) of the painting is equal to the values returned from
   * {@link #getWidth(java.awt.Graphics)} and {@link #getHeight(java.awt.Graphics)}.
   * @param g graphics on which to paint
   */
  protected abstract void paint( Graphics2D g );

}
