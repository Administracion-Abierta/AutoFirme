package org.apache.pdfbox.pdmodel.interactive.pagenavigation;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/pagenavigation/PDTransitionDirection.class */
public enum PDTransitionDirection {
    LEFT_TO_RIGHT(0),
    BOTTOM_TO_TOP(90),
    RIGHT_TO_LEFT(180),
    TOP_TO_BOTTOM(TIFF.TAG_IMAGE_DESCRIPTION),
    TOP_LEFT_TO_BOTTOM_RIGHT(TIFF.TAG_ARTIST),
    NONE(0) { // from class: org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransitionDirection.1
        @Override // org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransitionDirection
        public COSBase getCOSBase() {
            return COSName.NONE;
        }
    };

    private final int degrees;

    PDTransitionDirection(int degrees) {
        this.degrees = degrees;
    }

    public COSBase getCOSBase() {
        return COSInteger.get(this.degrees);
    }
}
