package org.apache.pdfbox.filter;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.graphics.color.PDJPXColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/DecodeResult.class */
public final class DecodeResult {
    public static final DecodeResult DEFAULT = new DecodeResult(new COSDictionary());
    private final COSDictionary parameters;
    private PDJPXColorSpace colorSpace;

    DecodeResult(COSDictionary parameters) {
        this.parameters = parameters;
    }

    DecodeResult(COSDictionary parameters, PDJPXColorSpace colorSpace) {
        this.parameters = parameters;
        this.colorSpace = colorSpace;
    }

    public COSDictionary getParameters() {
        return this.parameters;
    }

    public PDJPXColorSpace getJPXColorSpace() {
        return this.colorSpace;
    }

    void setColorSpace(PDJPXColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }
}
