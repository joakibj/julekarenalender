package no.jervell.view.awt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URI;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class Image extends AbstractPaintable
{
  private BufferedImage image;

  public Image( URI uri ) throws IOException
  {
    this( uri.toURL() );
  }

  public Image( URL url ) throws IOException
  {
    this.image = ImageIO.read( url );
  }

  public Image( BufferedImage image )
  {
    this.image = image;
  }

  protected double getWidth( Graphics g )
  {
    return image.getWidth();
  }

  protected double getHeight( Graphics g )
  {
    return image.getHeight();
  }

  protected void paint( Graphics2D g )
  {
    g.drawImage( image, null, 0, 0 );
  }
}
