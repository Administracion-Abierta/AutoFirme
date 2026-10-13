package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.imageio.color.Int16ComponentColorModel;
import java.awt.color.ColorSpace;
import java.awt.image.PixelInterleavedSampleModel;
import javax.imageio.ImageTypeSpecifier;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/Int16ImageTypeSpecifier.class */
final class Int16ImageTypeSpecifier extends ImageTypeSpecifier {
    Int16ImageTypeSpecifier(ColorSpace colorSpace, int[] iArr, boolean z, boolean z2) {
        super(new Int16ComponentColorModel(colorSpace, z, z2), new PixelInterleavedSampleModel(2, 1, 1, colorSpace.getNumComponents() + (z ? 1 : 0), colorSpace.getNumComponents() + (z ? 1 : 0), iArr));
    }
}
