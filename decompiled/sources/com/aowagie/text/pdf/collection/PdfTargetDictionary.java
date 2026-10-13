package com.aowagie.text.pdf.collection;

import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfString;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/collection/PdfTargetDictionary.class */
public class PdfTargetDictionary extends PdfDictionary {
    public void setEmbeddedFileName(String target) {
        put(PdfName.N, new PdfString(target, null));
    }

    public void setFileAttachmentPagename(String name) {
        put(PdfName.P, new PdfString(name, null));
    }

    public void setFileAttachmentPage(int page) {
        put(PdfName.P, new PdfNumber(page));
    }

    public void setFileAttachmentName(String name) {
        put(PdfName.A, new PdfString(name, PdfObject.TEXT_UNICODE));
    }

    public void setFileAttachmentIndex(int annotation) {
        put(PdfName.A, new PdfNumber(annotation));
    }

    public void setAdditionalPath(PdfTargetDictionary nested) {
        put(PdfName.T, nested);
    }
}
