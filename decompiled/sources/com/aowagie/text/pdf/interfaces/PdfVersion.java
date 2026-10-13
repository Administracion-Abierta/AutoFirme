package com.aowagie.text.pdf.interfaces;

import com.aowagie.text.pdf.PdfDeveloperExtension;
import com.aowagie.text.pdf.PdfName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/interfaces/PdfVersion.class */
public interface PdfVersion {
    void setPdfVersion(char c);

    void setAtLeastPdfVersion(char c);

    void setPdfVersion(PdfName pdfName);

    void addDeveloperExtension(PdfDeveloperExtension pdfDeveloperExtension);
}
