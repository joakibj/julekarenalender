package no.jervell.view.awt;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class BoundingBox extends Rectangle2D.Double
{
  /**
   * Create a bounding box for <code>paintable</code>.
   * @param paintable paintable object
   * @param g graphics context
   * @param x position x-coordinate
   * @param y position y-coordinate
   */
  public BoundingBox( Paintable paintable, Graphics g, double x, double y )
  {
    initialize( paintable, g, x, y, null, null );
  }

  /**
   * Create a bounding box for <code>paintable</code>.
   * @param paintable paintable object
   * @param g graphics context
   * @param x position x-coordinate
   * @param y position y-coordinate
   * @param w width. If <code>null</code>, a default value will be computed.
   * @param h height. If <code>null</code>, a default value will be computed.
   */
  public BoundingBox( Paintable paintable, Graphics g, double x, double y, Number w, Number h )
  {
    initialize( paintable, g, x, y, w, h );
  }

  /**
   * Create a bounding box for <code>paintable</code>.
   * @param paintable paintable object
   * @param g graphics context
   * @param position position. If <code>null</code>, then <code>(0,0)</code> is used.
   */
  public BoundingBox( Paintable paintable, Graphics g, Point2D position )
  {
    this( paintable, g, position, null );
  }

  /**
   * Create a bounding box for <code>paintable</code>.
   * @param paintable paintable object
   * @param g graphics context
   * @param position position. If <code>null</code>, then <code>(0,0)</code> is used.
   * @param size size. If size is <code>null</code>, then dimensions are automatically calculated.
   *             Also, any negative components (width and/or height) will be calculated automatically.
   */
  public BoundingBox( Paintable paintable, Graphics g, Point2D position, Dimension2D size )
  {
    double x = position == null ? 0 : position.getX();
    double y = position == null ? 0 : position.getY();
    Number w = size == null || size.getWidth()  < 0 ? null : size.getWidth();
    Number h = size == null || size.getHeight() < 0 ? null : size.getHeight();
    initialize( paintable, g, x, y, w, h );
  }

  private void initialize( Paintable paintable, Graphics g, double x, double y, Number w, Number h )
  {
    if ( w == null || h == null )
    {
      Dimension2D dim = computeUnspecifiedDimensions( w, h, paintable.getPreferredWidth( g ), paintable.getPreferredHeight( g ) );
      w = dim.getWidth();
      h = dim.getHeight();
    }
    setRect( x, y, w.doubleValue(), h.doubleValue() );
  }

  static Dimension2D computeUnspecifiedDimensions( Number w, Number h, double defaultWidth, double defaultHeight )
  {
    Dim2D dim = new Dim2D( defaultWidth, defaultHeight );
    if ( w == null && h != null )
    {
      dim.width = defaultWidth/defaultHeight * h.doubleValue();
      dim.height = h.doubleValue();
    }
    else if ( h == null && w != null )
    {
      dim.width = w.doubleValue();
      dim.height = defaultHeight/defaultWidth * w.doubleValue();
    }
    else if ( w != null && h != null )
    {
      dim.width = w.doubleValue();
      dim.height = h.doubleValue();
    }
    return dim;
  }

}
