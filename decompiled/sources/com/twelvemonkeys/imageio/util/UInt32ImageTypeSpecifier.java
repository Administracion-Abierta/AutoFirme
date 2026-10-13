package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.imageio.color.UInt32ColorModel;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import javax.imageio.ImageTypeSpecifier;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/UInt32ImageTypeSpecifier.class */
final class UInt32ImageTypeSpecifier extends ImageTypeSpecifier {
    private UInt32ImageTypeSpecifier(ColorSpace colorSpace, boolean z, boolean z2, SampleModel sampleModel) {
        super(new UInt32ColorModel(colorSpace, z, z2), sampleModel);
    }

    static ImageTypeSpecifier createInterleaved(ColorSpace colorSpace, int[] iArr, boolean z, boolean z2) {
        return new UInt32ImageTypeSpecifier(colorSpace, z, z2, new PixelInterleavedSampleModel(3, 1, 1, colorSpace.getNumComponents() + (z ? 1 : 0), colorSpace.getNumComponents() + (z ? 1 : 0), iArr));
    }

    static ImageTypeSpecifier createBanded(ColorSpace colorSpace, int[] iArr, int[] iArr2, boolean z, boolean z2) {
        return new UInt32ImageTypeSpecifier(colorSpace, z, z2, new BandedSampleModel(3, 1, 1, 1, iArr, iArr2));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UInt32ImageTypeSpecifier)) {
            return false;
        }
        UInt32ImageTypeSpecifier uInt32ImageTypeSpecifier = (UInt32ImageTypeSpecifier) obj;
        return this.colorModel.equals(uInt32ImageTypeSpecifier.colorModel) && this.sampleModel.equals(uInt32ImageTypeSpecifier.sampleModel);
    }
}
