package com.twelvemonkeys.image;

import com.twelvemonkeys.lang.Validate;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.Icon;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/BufferedImageIcon.class */
public class BufferedImageIcon implements Icon {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final boolean fast;

    public BufferedImageIcon(BufferedImage bufferedImage) {
        this(bufferedImage, bufferedImage != null ? bufferedImage.getWidth() : 0, bufferedImage != null ? bufferedImage.getHeight() : 0);
    }

    public BufferedImageIcon(BufferedImage bufferedImage, int i, int i2) {
        this(bufferedImage, i, i2, bufferedImage.getWidth() == i && bufferedImage.getHeight() == i2);
    }

    public BufferedImageIcon(BufferedImage bufferedImage, int i, int i2, boolean z) {
        this.image = (BufferedImage) Validate.notNull(bufferedImage, "image");
        this.width = ((Integer) Validate.isTrue(i > 0, Integer.valueOf(i), "width must be positive: %d")).intValue();
        this.height = ((Integer) Validate.isTrue(i2 > 0, Integer.valueOf(i2), "height must be positive: %d")).intValue();
        this.fast = z;
    }

    public int getIconHeight() {
        return this.height;
    }

    public int getIconWidth() {
        return this.width;
    }

    public void paintIcon(Component component, Graphics graphics, int i, int i2) {
        if (this.fast || !(graphics instanceof Graphics2D)) {
            graphics.drawImage(this.image, i, i2, this.width, this.height, (ImageObserver) null);
            return;
        }
        Graphics2D graphics2D = (Graphics2D) graphics;
        AffineTransform translateInstance = AffineTransform.getTranslateInstance(i, i2);
        translateInstance.scale(((double) this.width) / ((double) this.image.getWidth()), ((double) this.height) / ((double) this.image.getHeight()));
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(this.image, translateInstance, (ImageObserver) null);
    }
}
