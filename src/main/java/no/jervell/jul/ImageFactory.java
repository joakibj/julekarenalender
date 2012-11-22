package no.jervell.jul;

import no.jervell.awt.Anchor;
import no.jervell.awt.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ImageFactory {
    private static String localImageFolder = "." + File.separator + "img";
    private static String staticImageResourceFolder = "static/images/";
    private static Color blankColor = Color.white;
    public final static Image BLANK = createBlankImage();

    public static Image createStaticImage(String name) {
        try {
            URI uri = ImageFactory.class.getClassLoader().getResource(staticImageResourceFolder + name).toURI();
            return loadGenericImage(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
