package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import javax.imageio.ImageTypeSpecifier;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/IndexedImageTypeSpecifier.class */
final class IndexedImageTypeSpecifier extends ImageTypeSpecifier {
    IndexedImageTypeSpecifier(ColorModel colorModel) {
        super((ColorModel) Validate.notNull(colorModel, "colorModel"), colorModel.createCompatibleSampleModel(1, 1));
    }

    public BufferedImage createBufferedImage(int i, int i2) {
        try {
            return new BufferedImage(this.colorModel, this.colorModel.createCompatibleWritableRaster(i, i2), this.colorModel.isAlphaPremultiplied(), (Hashtable) null);
        } catch (NegativeArraySizeException e) {
            throw new IllegalArgumentException("Array size > Integer.MAX_VALUE!");
        }
    }
}
