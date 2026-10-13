package com.aowagie.text.pdf.interfaces;

import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfObject;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/interfaces/PdfViewerPreferences.class */
public interface PdfViewerPreferences {
    void setViewerPreferences(int i);

    void addViewerPreference(PdfName pdfName, PdfObject pdfObject);
}
