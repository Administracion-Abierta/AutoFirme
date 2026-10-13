package es.gob.afirma.standalone.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ScalablePane.class */
final class ScalablePane extends JPanel {
    private static final long serialVersionUID = 2907355327204790682L;
    private final Image master;
    private boolean toFit;
    private Image scaled;

    ScalablePane(Image image) {
        this(image, true);
    }

    ScalablePane(Image image, boolean z) {
        this.master = image;
        setToFit(z);
    }

    public Dimension getPreferredSize() {
        return this.master == null ? super.getPreferredSize() : new Dimension(this.master.getWidth(this), this.master.getHeight(this));
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Image image = null;
        if (this.scaled != null) {
            image = this.scaled;
        } else if (this.master != null) {
            image = this.master;
        }
        if (image != null) {
            graphics.drawImage(image, (getWidth() - image.getWidth(this)) / 2, (getHeight() - image.getHeight(this)) / 2, this);
        }
    }

    public void invalidate() {
        generateScaledInstance();
        super.invalidate();
    }

    public boolean isToFit() {
        return this.toFit;
    }

    public void setToFit(boolean z) {
        if (z != this.toFit) {
            this.toFit = z;
            invalidate();
        }
    }

    protected void generateScaledInstance() {
        this.scaled = null;
        Dimension size = getSize();
        if (size == null || ((int) size.getWidth()) == 0 || ((int) size.getHeight()) == 0) {
            this.scaled = this.master;
        } else if (isToFit()) {
            this.scaled = getScaledInstanceToFit(this.master, getSize());
        } else {
            this.scaled = getScaledInstanceToFill(this.master, getSize());
        }
    }

    protected BufferedImage toBufferedImage(Image image) {
        BufferedImage bufferedImageCreateCompatibleImage = createCompatibleImage(new Dimension(image.getWidth(this), image.getHeight(this)));
        Graphics2D graphics2DCreateGraphics = bufferedImageCreateCompatibleImage.createGraphics();
        graphics2DCreateGraphics.drawImage(image, 0, 0, this);
        graphics2DCreateGraphics.dispose();
        return bufferedImageCreateCompatibleImage;
    }

    Image getScaledInstanceToFit(Image image, Dimension dimension) {
        return getScaledInstance(toBufferedImage(image), getScaleFactorToFit(new Dimension(image.getWidth(this), image.getHeight(this)), dimension), RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
    }

    Image getScaledInstanceToFill(Image image, Dimension dimension) {
        return getScaledInstance(toBufferedImage(image), getScaleFactorToFill(new Dimension(image.getWidth(this), image.getHeight(this)), dimension), RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
    }

    public static Dimension getSizeToFit(Dimension dimension, Dimension dimension2) {
        double scaleFactorToFit = getScaleFactorToFit(dimension, dimension2);
        Dimension dimension3 = new Dimension(dimension);
        dimension3.width = (int) (((double) dimension3.width) * scaleFactorToFit);
        dimension3.height = (int) (((double) dimension3.height) * scaleFactorToFit);
        return dimension3;
    }

    public static Dimension getSizeToFill(Dimension dimension, Dimension dimension2) {
        double scaleFactorToFill = getScaleFactorToFill(dimension, dimension2);
        Dimension dimension3 = new Dimension(dimension);
        dimension3.width = (int) (((double) dimension3.width) * scaleFactorToFill);
        dimension3.height = (int) (((double) dimension3.height) * scaleFactorToFill);
        return dimension3;
    }

    private static double getScaleFactor(int i, int i2) {
        return ((double) i2) / ((double) i);
    }

    private static double getScaleFactorToFit(Dimension dimension, Dimension dimension2) {
        double dMin = 1.0d;
        if (dimension != null && dimension2 != null) {
            dMin = Math.min(getScaleFactor(dimension.height, dimension2.height), getScaleFactor(dimension.width, dimension2.width));
        }
        return dMin;
    }

    private static double getScaleFactorToFill(Dimension dimension, Dimension dimension2) {
        return Math.max(getScaleFactor(dimension.height, dimension2.height), getScaleFactor(dimension.width, dimension2.width));
    }

    private BufferedImage createCompatibleImage(Dimension dimension) {
        return createCompatibleImage(dimension.width, dimension.height);
    }

    private BufferedImage createCompatibleImage(int i, int i2) {
        GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
        if (graphicsConfiguration == null) {
            graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        BufferedImage bufferedImageCreateCompatibleImage = graphicsConfiguration.createCompatibleImage(i, i2, 3);
        bufferedImageCreateCompatibleImage.coerceData(true);
        return bufferedImageCreateCompatibleImage;
    }

    protected static BufferedImage getScaledInstance(BufferedImage bufferedImage, double d, Object obj, boolean z) {
        BufferedImage scaledUpInstance;
        int iRound = (int) Math.round(((double) bufferedImage.getWidth()) * d);
        int iRound2 = (int) Math.round(((double) bufferedImage.getHeight()) * d);
        if (d <= 1.0d) {
            scaledUpInstance = getScaledDownInstance(bufferedImage, iRound, iRound2, obj, z);
        } else {
            scaledUpInstance = getScaledUpInstance(bufferedImage, iRound, iRound2, obj, z);
        }
        return scaledUpInstance;
    }

    protected static BufferedImage getScaledDownInstance(BufferedImage bufferedImage, int i, int i2, Object obj, boolean z) {
        int width;
        int height;
        int i3 = bufferedImage.getTransparency() == 1 ? 1 : 2;
        BufferedImage bufferedImage2 = bufferedImage;
        if (i2 > 0 || i > 0) {
            if (z) {
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
            } else {
                width = i;
                height = i2;
            }
            while (true) {
                if (z && width > i) {
                    width /= 2;
                    if (width < i) {
                        width = i;
                    }
                }
                if (z && height > i2) {
                    height /= 2;
                    if (height < i2) {
                        height = i2;
                    }
                }
                BufferedImage bufferedImage3 = new BufferedImage(Math.max(width, 1), Math.max(height, 1), i3);
                Graphics2D graphics2DCreateGraphics = bufferedImage3.createGraphics();
                graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, obj);
                graphics2DCreateGraphics.drawImage(bufferedImage2, 0, 0, width, height, (ImageObserver) null);
                graphics2DCreateGraphics.dispose();
                bufferedImage2 = bufferedImage3;
                if (width == i && height == i2) {
                    break;
                }
            }
        } else {
            bufferedImage2 = new BufferedImage(1, 1, i3);
        }
        return bufferedImage2;
    }

    protected static BufferedImage getScaledUpInstance(BufferedImage bufferedImage, int i, int i2, Object obj, boolean z) {
        int width;
        int height;
        BufferedImage bufferedImage2 = bufferedImage;
        if (z) {
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
        } else {
            width = i;
            height = i2;
        }
        while (true) {
            if (z && width < i) {
                width *= 2;
                if (width > i) {
                    width = i;
                }
            }
            if (z && height < i2) {
                height *= 2;
                if (height > i2) {
                    height = i2;
                }
            }
            BufferedImage bufferedImage3 = new BufferedImage(width, height, 2);
            Graphics2D graphics2DCreateGraphics = bufferedImage3.createGraphics();
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, obj);
            graphics2DCreateGraphics.drawImage(bufferedImage2, 0, 0, width, height, (ImageObserver) null);
            graphics2DCreateGraphics.dispose();
            bufferedImage2 = bufferedImage3;
            if (width == i && height == i2) {
                return bufferedImage2;
            }
        }
    }

    public Image getMaster() {
        return this.master;
    }
}
