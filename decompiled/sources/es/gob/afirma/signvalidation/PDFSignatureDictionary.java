package es.gob.afirma.signvalidation;

import com.aowagie.text.pdf.PdfDictionary;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/PDFSignatureDictionary.class */
public class PDFSignatureDictionary implements Serializable, Comparable<PDFSignatureDictionary> {
    private static final long serialVersionUID = -1252456113292585014L;
    private Integer revision;
    private PdfDictionary dictionary;
    private String name;

    public PDFSignatureDictionary(int i, PdfDictionary pdfDictionary, String str) {
        this.revision = Integer.valueOf(i);
        this.dictionary = pdfDictionary;
        this.name = str;
    }

    public final Integer getRevision() {
        return this.revision;
    }

    public final void setRevision(Integer num) {
        this.revision = num;
    }

    public final PdfDictionary getDictionary() {
        return this.dictionary;
    }

    public final void setDictionary(PdfDictionary pdfDictionary) {
        this.dictionary = pdfDictionary;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String str) {
        this.name = str;
    }

    @Override // java.lang.Comparable
    public final int compareTo(PDFSignatureDictionary pDFSignatureDictionary) {
        return this.revision.compareTo(pDFSignatureDictionary.getRevision());
    }
}
