package no.jervell.util;

import com.github.julekarenalender.log.Logger$;
import no.jervell.view.awt.Anchor;
import no.jervell.view.awt.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ImageFactory {
    private static final Logger$ logger = Logger$.MODULE$;
    private static String localImageFolder = "." + File.separator + "images";
    private static String staticImageResourceFolder = "static/images/";
    private static Color blankColor = Color.white;
    public final static Image BLANK = createBlankImage();

    public static Image createStaticImage(String name) {
        try {
            URI uri = ImageFactory.class.getClassLoader().getResource(staticImageResourceFolder + name).toURI();
            return loadGenericImage(uri);
        } catch (Exception e) {
            logger.error("Could not load image: " + name);
            return BLANK;
        }
    }

    public static Image createImage(String name) {
        return loadGenericImage(new File(localImageFolder, name).toURI());
    }

    private static Image loadGenericImage(URI uri) {
        try {
            Image image = new Image(uri);
            image.setAnchor(Anchor.CENTER);
            return image;
        } catch (IOException e) {
            return BLANK;
        }
    }

    public static Image createBlankImage() {
        int w = 10;
        int h = 10;
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = bufferedImage.getGraphics();
        g.setColor(blankColor);
        g.fillRect(0, 0, w, h);
        return new Image(bufferedImage);
    }
}
