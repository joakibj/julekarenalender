package no.jervell.view.awt;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * A simple interface to be implemented by objects that can be painted
 * onto a graphics object.
 * @author Arne C. Jervell (arne@jervell.no)
 */
public interface Paintable
{
  /**
   * Get paintable's preferred width on given graphics.
   * @return default width, in <code>g</code>'s coordinates.
   * @param g target graphics
   */
  public double getPreferredWidth( Graphics g );
  
  /**
   * Get paintable's preferred height on given graphics.
   * @return default height, in <code>g</code>'s coordinates.
   * @param g target graphics
   */
  public double getPreferredHeight( Graphics g );

  /**
   * Get the actual area covered by this paintable element given a maximum area and a graphics context.
   * This area should normally be equal to or less than <code>area</code>.
   * @param area maximum coverage
   * @param g    graphics context
   * @return the area actually covered by paintable element after scaling, rotation and alignment has been performed.
   */
  public Rectangle2D getAreaCovered( Graphics g, Rectangle2D area );

  /**
   * Render object.
   * @param g graphics on which to paint
   * @param loc target position and size, given in <code>g</code>'s coordinates.
   */
  public void paint( Graphics g, Rectangle2D loc );
}
