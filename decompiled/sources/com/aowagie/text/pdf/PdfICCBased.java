package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import java.awt.color.ICC_Profile;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfICCBased.class */
class PdfICCBased extends PdfStream {
    PdfICCBased(ICC_Profile profile, int compressionLevel) {
        try {
            int numberOfComponents = profile.getNumComponents();
            switch (numberOfComponents) {
                case 1:
                    put(PdfName.ALTERNATE, PdfName.DEVICEGRAY);
                    break;
                case 2:
                default:
                    throw new PdfException(numberOfComponents + " component(s) is not supported in PDF1.4");
                case 3:
                    put(PdfName.ALTERNATE, PdfName.DEVICERGB);
                    break;
                case 4:
                    put(PdfName.ALTERNATE, PdfName.DEVICECMYK);
                    break;
            }
            put(PdfName.N, new PdfNumber(numberOfComponents));
            this.bytes = profile.getData();
            flateCompress(compressionLevel);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
