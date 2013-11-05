package no.jervell.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import no.jervell.view.awt.Image;


/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class ImageView extends Component {
    private Image img;

    public ImageView(Image img) {
        this.img = img;
        setPreferredSize(new Dimension((int) img.getPreferredWidth(null), (int) img.getPreferredHeight(null)));
    }

    @Override
    public void paint(Graphics g) {
        img.paint(g, new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
    }
}