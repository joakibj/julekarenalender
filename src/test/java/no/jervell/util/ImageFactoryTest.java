package no.jervell.util;

import no.jervell.view.awt.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ImageFactoryTest {
    private static final String STATIC_IMAGE = "top.jpg";
    private static final String TEST_IMAGES_DIR = "images/";
    private static final String TEST_IMAGE = "Arne.jpg";
    private static final String LOCAL_DIRECTORY_VARIABLE_NAME = "localImageFolder";

    private static String oldLocalDirectory;
    private static Field currentLocalDirectory;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        currentLocalDirectory = ImageFactory.class.getDeclaredField(LOCAL_DIRECTORY_VARIABLE_NAME);
        currentLocalDirectory.setAccessible(true);
        oldLocalDirectory = (String) currentLocalDirectory.get(ImageFactory.class);
    }

    @Before
    public void setUp() throws Exception {
        overWriteFactoryLocalDirectoryVariable(getTestResourceImagePath());
    }

    private void overWriteFactoryLocalDirectoryVariable(String value) throws Exception {
        currentLocalDirectory.set(ImageFactory.class, value);
    }

    private String getTestResourceImagePath() throws Exception {
        return this.getClass().getClassLoader().getResource(TEST_IMAGES_DIR).toURI().getPath();
    }

    @Test
    public void shouldCreateAnImageFromStaticImageLocation() {
        Image image = ImageFactory.createStaticImage(STATIC_IMAGE);

        assertNotNull(image);
    }

    @Test
    public void shouldReturnBlankImageWhenTryingToCreateStaticImageThatDoesNotExist() {
        Image image = ImageFactory.createStaticImage("fake.jpg");

        assertThat(image, is(ImageFactory.BLANK));
    }

    @Test
    public void shouldCreateBlankImage() {
        Image image = ImageFactory.createBlankImage();

        //TODO: Can we test that this image is blank somehow?
        assertNotNull(image);
    }

    @Test
    public void shouldCreateImageFromResource() {
        Image image = ImageFactory.createImage(TEST_IMAGE);

        assertNotNull(image);
    }

    @Test
    public void shouldCreateBlankImageFromResourceIfImagesDoesNotExist() {
        Image image = ImageFactory.createImage("Horse.jpg");

        assertThat(image, is(ImageFactory.BLANK));
    }

    @After
    public void cleanUp() throws Exception {
        overWriteFactoryLocalDirectoryVariable(oldLocalDirectory);
    }
}
