package no.jervell.awt;

import java.awt.geom.Dimension2D;

/**
 * @author Arne C. Jervell (arne@jervell.no)
*/
public class Dim2D extends Dimension2D
{
  double width;
  double height;

  public Dim2D( double width, double height )
  {
    setSize( width, height );
  }

  @Override
  public double getWidth()
  {
    return width;
  }

  @Override
  public double getHeight()
  {
    return height;
  }

  @Override
  public void setSize( double width, double height )
  {
    this.width = width;
    this.height = height;
  }
}
