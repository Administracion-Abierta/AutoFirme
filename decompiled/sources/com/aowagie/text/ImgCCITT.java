package com.aowagie.text;

import com.aowagie.text.pdf.codec.TIFFFaxDecoder;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ImgCCITT.class */
public class ImgCCITT extends Image {
    ImgCCITT(Image image) {
        super(image);
    }

    ImgCCITT(int width, int height, boolean reverseBits, int typeCCITT, int parameters, byte[] data) throws BadElementException {
        super((URL) null);
        if (typeCCITT != 256 && typeCCITT != 257 && typeCCITT != 258) {
            throw new BadElementException("The CCITT compression type must be CCITTG4, CCITTG3_1D or CCITTG3_2D");
        }
        if (reverseBits) {
            TIFFFaxDecoder.reverseBits(data);
        }
        this.type = 34;
        this.scaledHeight = height;
        setTop(this.scaledHeight);
        this.scaledWidth = width;
        setRight(this.scaledWidth);
        this.colorspace = parameters;
        this.bpc = typeCCITT;
        this.rawData = data;
        this.plainWidth = getWidth();
        this.plainHeight = getHeight();
    }
}
