package no.jervell.awt;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class ImageLabel implements Paintable
{
  private Image img;
  private Label lbl;

  public ImageLabel( Image img, Label lbl )
  {
    this.img = img;
    this.lbl = lbl;
  }

  public double getPreferredWidth( Graphics g )
  {
    return img.getPreferredWidth( g ) + lbl.getPreferredWidth( g );
  }

  public double getPreferredHeight( Graphics g )
  {
    return Math.max( img.getPreferredHeight( g ), lbl.getPreferredHeight( g ) );
  }

  public Rectangle2D getAreaCovered( Graphics g, Rectangle2D area )
  {
    return area;
  }

  public void paint( Graphics g, Rectangle2D loc )
  {
    double imgW = paintImage( g, loc );
    Rectangle2D labelPlacement = new Rectangle2D.Double( loc.getX() + imgW,
                                                         loc.getY(),
                                                         loc.getWidth() - imgW,
                                                         loc.getHeight() );
    paintLabel( g, labelPlacement );
  }

  private double paintImage( Graphics g, Rectangle2D loc )
  {
    Rectangle2D imageAreaCovered = img.getAreaCovered( g, loc );
    Rectangle2D imagePlacement   = new Rectangle2D.Double( loc.getX(), loc.getY(),
                                                           imageAreaCovered.getWidth(), imageAreaCovered.getHeight());
    img.paint( g, imagePlacement );
    return imageAreaCovered.getWidth();
  }

  private void paintLabel(Graphics g, Rectangle2D loc)
  {
    if ( lbl.getScaling() == Scaling.STRETCH )
    {
      double prefW = lbl.getPreferredWidth( g );
      double prefH = lbl.getPreferredHeight( g );
      double scale = loc.getHeight() / prefH;
      double newW  = Math.min( prefW * scale, loc.getWidth() );
      loc = new Rectangle2D.Double( loc.getX(), loc.getY(), newW, loc.getHeight() );
    }
    lbl.paint( g, loc );
  }

  public Label getLabel()
  {
    return lbl;
  }
}
