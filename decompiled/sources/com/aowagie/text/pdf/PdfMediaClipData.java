package com.aowagie.text.pdf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfMediaClipData.class */
class PdfMediaClipData extends PdfDictionary {
    PdfMediaClipData(String file, PdfFileSpecification fs, String mimeType) throws IOException {
        put(PdfName.TYPE, new PdfName("MediaClip"));
        put(PdfName.S, new PdfName("MCD"));
        put(PdfName.N, new PdfString("Media clip for " + file));
        put(new PdfName("CT"), new PdfString(mimeType));
        PdfDictionary dic = new PdfDictionary();
        dic.put(new PdfName("TF"), new PdfString("TEMPACCESS"));
        put(new PdfName("P"), dic);
        put(PdfName.D, fs.getReference());
    }
}
