package com.aowagie.text.pdf.interfaces;

import com.aowagie.text.pdf.PdfAcroForm;
import com.aowagie.text.pdf.PdfAnnotation;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/interfaces/PdfAnnotations.class */
public interface PdfAnnotations {
    PdfAcroForm getAcroForm();

    void addAnnotation(PdfAnnotation pdfAnnotation);

    void setSigFlags(int i);
}
