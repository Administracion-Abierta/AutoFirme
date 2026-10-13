package com.aowagie.text;

import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ImgRaw.class */
public class ImgRaw extends Image {
    ImgRaw(Image image) {
        super(image);
    }

    public ImgRaw(int width, int height, int components, int bpc, byte[] data) throws BadElementException {
        super((URL) null);
        this.type = 34;
        this.scaledHeight = height;
        setTop(this.scaledHeight);
        this.scaledWidth = width;
        setRight(this.scaledWidth);
        if (components != 1 && components != 3 && components != 4) {
            throw new BadElementException("Components must be 1, 3, or 4.");
        }
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8) {
            throw new BadElementException("Bits-per-component must be 1, 2, 4, or 8.");
        }
        this.colorspace = components;
        this.bpc = bpc;
        this.rawData = data;
        this.plainWidth = getWidth();
        this.plainHeight = getHeight();
    }
}
