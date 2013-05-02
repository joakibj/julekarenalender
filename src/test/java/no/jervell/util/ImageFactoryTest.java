package no.jervell.util;

import no.jervell.view.awt.Image;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.net.URISyntaxException;

public class ImageFactoryTest {
    private static final String STATIC_IMAGE = "top.jpg";

    @Test
    public void shouldCreateAnImageFromStaticImageLocation() {
        Image image = ImageFactory.createStaticImage(STATIC_IMAGE);

        assertNotNull(image);
    }

    @Test(expected = URISyntaxException.class)
    public void shouldThrowExceptionWhenTryingToCreateStaticImageThatDoesNotExist() {
        Image image = ImageFactory.createStaticImage("fake.jpg");
    }

    @Test
    public void shouldCreateBlankImage() {
        Image image = ImageFactory.createBlankImage();

        //TODO: Can we test that this image is blank somehow?
        assertNotNull(image);
    }
}
