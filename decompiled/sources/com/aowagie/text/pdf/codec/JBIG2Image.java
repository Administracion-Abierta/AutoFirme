package com.aowagie.text.pdf.codec;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.ImgJBIG2;
import com.aowagie.text.pdf.RandomAccessFileOrArray;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/JBIG2Image.class */
public class JBIG2Image {
    public static Image getJbig2Image(RandomAccessFileOrArray ra, int page) {
        if (page < 1) {
            throw new IllegalArgumentException("The page number must be >= 1.");
        }
        try {
            JBIG2SegmentReader sr = new JBIG2SegmentReader(ra);
            sr.read();
            JBIG2SegmentReader.JBIG2Page p = sr.getPage(page);
            Image img = new ImgJBIG2(p.pageBitmapWidth, p.pageBitmapHeight, p.getData(true), sr.getGlobal(true));
            return img;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
