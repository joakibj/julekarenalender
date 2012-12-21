package no.jervell.view.awt;

import java.awt.*;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class Label extends AbstractPaintable implements Cloneable
{
  private String _text;
  private Paint paint = null;
  private Font font = null;


  public Label()
  {
  }

  public Label( String text )
  {
    this._text = text;
  }

  public String getText()
  {
    return getText( font );
  }

  protected String getText( Font font )
  {
    return getFixedText( _text, font );
  }

  public void setText( String text )
  {
    this._text = text;
  }

  public Paint getPaint()
  {
    return paint;
  }

  public void setPaint( Paint paint )
  {
    this.paint = paint;
  }

  public Font getFont()
  {
    return font;
  }

  public void setFont( Font font )
  {
    this.font = font;
  }

  private Font determineFont( Graphics g )
  {
    return this.font != null ? this.font : g.getFont();
  }

  public double getWidth( Graphics g )
  {
    Font        font = determineFont( g );
    FontMetrics fm   = g.getFontMetrics( font );

    return fm.stringWidth( getText( font ) );
  }

  public double getHeight( Graphics g )
  {
    Font        font = determineFont( g );
    FontMetrics fm   = g.getFontMetrics( font );
    
    return fm.getMaxAscent() + fm.getMaxDescent();
  }

  protected void paint( Graphics2D g )
  {
    if ( paint != null )
    {
      g.setPaint( paint );
    }
    if ( font != null )
    {
      g.setFont( font );
    }

    FontMetrics fm = g.getFontMetrics();
    g.drawString( getText( g.getFont() ), 0, fm.getMaxAscent() );
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}
