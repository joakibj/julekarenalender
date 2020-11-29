package no.jervell.view.swing;


import no.jervell.view.animation.impl.FrameCounter;
import no.jervell.view.awt.Paintable;
import no.jervell.view.gfx.ImageFilter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import static com.github.julekarenalender.JulekarenalenderKt.logger;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class WheelView extends Component {


    // Model data
    private java.util.List<Row> rows;

    // Image buffer
    ColorModel colorModel = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
    MemoryImageSource memImgSrc;
    Image memImg;

    private BufferedImage bufferedImage;
    private int[] pix;

    // Lighting
    private int ambient;
    private int[] diffuse;
    private int[] specular;

    // Texture
    private int[] textureIndices;

    // View parameters
    private int visibleRowCount = 2;
    private double viewAngle = degToRad(63);
    private double yOffset = 0;
    private double rowSpacingPct = .05;
    private double pixelScale = 1.2;   // How many virtual pixels contribute to one actual pixel
    private Paint backgroundPaint;

    private ImageFilter filter;

    // FPS checker
    private FrameCounter frameCounter;

    public WheelView() {
        setPreferredSize(new Dimension(275, 120));
    }

    public void setFilter(ImageFilter filter) {
        this.filter = filter;
    }

    public void setFrameCounter(FrameCounter frameCounter) {
        this.frameCounter = frameCounter;
    }

    public void setRows(java.util.List<Row> rows) {
        this.rows = rows;
    }

    public java.util.List<Row> getRows() {
        return rows;
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    public int getRowCount() {
        return rows.size();
    }

    public void setVisibleRows(int visibleRowCount) {
        this.visibleRowCount = visibleRowCount;
    }

    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public void setViewAngle(double viewAngle) {
        this.viewAngle = viewAngle;
    }

    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getYOffset() {
        return toYRange(yOffset);
    }

    public double toYRange(double y) {
        double result = y;
        if (y < 0) {
            result += ((-(int) y / rows.size()) + 1) * rows.size();
        }
        return result % rows.size();

    }

    public double getRawYOffset() {
        return yOffset;
    }

    public int getIndex(Object object) {
        for (int i = 0; i < rows.size(); ++i) {
            Row row = rows.get(i);
            if ((object == null && row.getData() == null) || (object != null && object.equals(row.getData()))) {
                return i;
            }
        }
        return -1;
    }

    public void setBackground(Paint paint) {
        this.backgroundPaint = paint;
    }

    @Override
    public void paint(Graphics g) {
        int width = getWidth();
        int viewHeight = getHeight();
        int textureHeight = (int) (viewHeight * pixelScale);

        Graphics2D g2 = getGraphicsBuffer(width, viewHeight, textureHeight);
        paintRows(g2, width, textureHeight);

        grabPixels(bufferedImage, 0, 0, width, textureHeight, pix);
        if (filter != null) {
            filter.apply(pix, width, textureHeight);
        }
        applyShading(pix, width, viewHeight);
        memImgSrc.newPixels(0, 0, width, viewHeight);

        g.drawImage(memImg, 0, 0, null);

        if (frameCounter != null && frameCounter.frame()) {
            logger.debug("" + frameCounter);
        }
    }

    private void grabPixels(Image image, int x, int y, int w, int h, int[] pix) {
        PixelGrabber grabber = new PixelGrabber(image, x, y, w, h, pix, 0, w);
        try {
            grabber.grabPixels();
        } catch (InterruptedException e) {
            // Ouch, we were interrupted while grabbing. Oh well, not much we can do about that...
            logger.info("Interrupted while grabbing pixels: " + e);
        }

        // Pixel are grabbed now, but make sure there were no problems
        if ((grabber.getStatus() & ImageObserver.ABORT) != 0) {
            // Doh, it seems the grabbing of the pixels was aborted...
            logger.info("Pixel grabbing aborted.");
        }
    }

    private void paintRows(Graphics2D g2, int w, int h) {
        double rowHeight = (double) h / visibleRowCount;
        double fadeHeight = (h - rowHeight) / 2.;

        double yOffset = getYOffset();
        int selectedIndex = (int) yOffset;
        int firstRowDelta = visibleRowCount / 2;
        int currentRowIndex = selectedIndex + rows.size() * visibleRowCount - firstRowDelta;

        double y = -(yOffset - selectedIndex + firstRowDelta) * rowHeight + fadeHeight;
        double spacing = rowSpacingPct * rowHeight;
        while (y < h) {
            Paintable row = rows.get(currentRowIndex++ % rows.size()).paintable;
            row.paint(g2, new Rectangle2D.Double(0, y + spacing / 2, w, rowHeight - spacing));
            y += rowHeight;
        }
    }

    private void applyShading(int[] pix, int w, int h) {
        int ambientLight = ambient;
        int rgb, R, G, B;
        int dst = 0;

        for (int y = 0; y < h; ++y) {
            int src = textureIndices[y] * w;
            int diffuseLight = diffuse[y];
            int specularLight = specular[y];
            for (int x = 0; x < w; ++x) {
                rgb = pix[src++];
                R = ((((rgb & 0xff0000) >> 16) * (ambientLight + diffuseLight)) >> 8) + specularLight;
                G = ((((rgb & 0x00ff00) >> 8) * (ambientLight + diffuseLight)) >> 8) + specularLight;
                B = ((((rgb & 0x0000ff)) * (ambientLight + diffuseLight)) >> 8) + specularLight;
                if (R > 255) R = 255;
                if (G > 255) G = 255;
                if (B > 255) B = 255;
                pix[dst++] = (R << 16) | (G << 8) | B;
            }
        }
    }


    private Graphics2D getGraphicsBuffer(int width, int viewHeight, int textureHeight) {
        Graphics2D g2 = ensureGraphicsBuffer(width, viewHeight, textureHeight);
        clear(g2, width, textureHeight);
        return g2;
    }

    private Graphics2D ensureGraphicsBuffer(int width, int viewHeight, int textureHeight) {
        if (pix == null || pix.length != width * textureHeight) {
            pix = new int[width * textureHeight];
            bufferedImage = new BufferedImage(width, textureHeight, BufferedImage.TYPE_INT_RGB);

            memImgSrc = new MemoryImageSource(width, viewHeight, colorModel, pix, 0, width);
            memImgSrc.setAnimated(true);
            memImg = createImage(memImgSrc);
            System.gc();
        }
        ensureLighting(viewHeight);
        ensureTextureCoordinates(viewHeight, textureHeight);
        return (Graphics2D) bufferedImage.getGraphics();
    }

    private void clear(Graphics2D g2, int w, int h) {
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fillRect(0, 0, w, h);
        }
    }

    private void ensureTextureCoordinates(int viewHeight, int textureHeight) {
        if (textureIndices == null || textureIndices.length != viewHeight) {
            precomputeTextureIndices(viewHeight, textureHeight);
            System.gc();
        }
    }

    private void ensureLighting(int h) {
        if (diffuse == null || h != diffuse.length) {
            precomputeLighting(h);
            System.gc();
        }
    }

    private void precomputeTextureIndices(int viewHeight, int textureHeight) {
        textureIndices = new int[viewHeight];

        for (int y = 0; y < viewHeight; ++y) {
            double dy = viewHeight / 2. - y;
            double dx = viewHeight / (2. * Math.cos(Math.PI / 2 - viewAngle));
            double textureY = textureHeight * (viewAngle + Math.acos(dy / dx) - Math.PI / 2) / (2. * viewAngle);

            textureIndices[y] = Math.max(0, Math.min(textureHeight - 1, (int) Math.round(textureY)));
        }
    }

    private void precomputeLighting(int h) {
        ambient = 0;
        diffuse = new int[h];
        specular = new int[h];
        addDiffuseAndSpecular(h, degToRad(35), 700, 225, 166);
        addDiffuseAndSpecular(h, degToRad(-40), 700, 65, 0);
    }

    private void addDiffuseAndSpecular(int viewHeight, double lightAngle, int specularExponent, int maxDiffuse, int maxSpecular) {
        double r = viewHeight / 2.0;
        double R = r / Math.cos(Math.PI / 2 - viewAngle);
        double[] V = {0, 0, 1};
        double[] L = {0, Math.sin(lightAngle), Math.cos(lightAngle)};
        double[] H = {(V[0] + L[0]) / 2, (V[1] + L[1]) / 2, (V[2] + L[2]) / 2};
        double[] N = {0, 0, 0};
        normalize(H);
        for (int i = 0; i < viewHeight; ++i) {
            double h = r - i;
            double sin = h / R;
            double cos = Math.sqrt(1 - (h * h) / (R * R));
            N[0] = 0;
            N[1] = sin;
            N[2] = cos;
            double L_dot_N = L[0] * N[0] + L[1] * N[1] + L[2] * N[2];
            double N_dot_H = N[0] * H[0] + N[1] * H[1] + N[2] * H[2];
            diffuse[i] += (int) (Math.max(0, L_dot_N) * maxDiffuse);
            specular[i] += (int) (Math.pow(Math.max(0, N_dot_H), specularExponent) * maxSpecular);
        }
    }

    private void normalize(double[] v) {
        double len = 0;
        for (double val : v) {
            len += val * val;
        }
        len = Math.sqrt(len);
        for (int i = 0; i < v.length; ++i) {
            v[i] /= len;
        }
    }

    private static double radToDeg(double rad) {
        return rad * 180 / Math.PI;
    }

    private static double degToRad(double deg) {
        return deg * Math.PI / 180.;
    }

    public static class Row {
        private Object data;
        private Paintable paintable;

        public Row(Object data, Paintable paintable) {
            this.data = data;
            this.paintable = paintable;
        }

        public Object getData() {
            return data;
        }

        public Paintable getPaintable() {
            return paintable;
        }
    }
}
