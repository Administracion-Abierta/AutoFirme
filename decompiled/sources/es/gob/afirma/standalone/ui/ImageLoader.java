package es.gob.afirma.standalone.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ImageLoader.class */
public class ImageLoader {
    public static final int XLARGE_ICON = 4;
    public static final int LARGE_ICON = 3;
    public static final int MEDIUM_ICON = 2;
    public static final int SMALL_ICON = 1;

    public static ImageIcon loadIcon(String str) {
        ImageIcon imageIcon = null;
        BufferedImage bufferedImageLoadImage = loadImage(str);
        if (bufferedImageLoadImage != null) {
            imageIcon = new ImageIcon(bufferedImageLoadImage);
        }
        return imageIcon;
    }

    public static ImageIcon loadIcon(String str, int i) {
        ImageIcon imageIcon = null;
        BufferedImage bufferedImageLoadImage = loadImage(str);
        if (bufferedImageLoadImage != null) {
            Dimension dimension = getDimension(i);
            if (dimension != null) {
                bufferedImageLoadImage = resize(bufferedImageLoadImage, dimension);
            }
            imageIcon = new ImageIcon(bufferedImageLoadImage);
        }
        return imageIcon;
    }

    public static BufferedImage loadImage(String str) {
        try {
            InputStream resourceAsStream = ImageLoader.class.getResourceAsStream("/resources/" + str);
            Throwable th = null;
            try {
                try {
                    BufferedImage bufferedImage = ImageIO.read(resourceAsStream);
                    if (resourceAsStream != null) {
                        if (0 != 0) {
                            try {
                                resourceAsStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            resourceAsStream.close();
                        }
                    }
                    return bufferedImage;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (resourceAsStream != null) {
                    if (th != null) {
                        try {
                            resourceAsStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        resourceAsStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("No se pudo cargar la imagen '" + str + "': " + e);
            return null;
        }
    }

    private static Dimension getDimension(int i) {
        Dimension dimension;
        switch (i) {
            case 1:
                dimension = new Dimension(32, 32);
                break;
            case 2:
                dimension = new Dimension(64, 64);
                break;
            case 3:
                dimension = new Dimension(128, 128);
                break;
            case 4:
                dimension = new Dimension(256, 256);
                break;
            default:
                dimension = null;
                break;
        }
        return dimension;
    }

    private static BufferedImage resize(BufferedImage bufferedImage, Dimension dimension) {
        Image scaledInstance = bufferedImage.getScaledInstance((int) dimension.getWidth(), (int) dimension.getHeight(), 4);
        BufferedImage bufferedImage2 = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), 2);
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        graphics2DCreateGraphics.drawImage(scaledInstance, 0, 0, (ImageObserver) null);
        graphics2DCreateGraphics.dispose();
        return bufferedImage2;
    }
}
