package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDeveloperExtension.class */
public class PdfDeveloperExtension {
    private final PdfName prefix;
    private final PdfName baseversion;
    private final int extensionLevel;

    public PdfDeveloperExtension(PdfName prefix, PdfName baseversion, int extensionLevel) {
        this.prefix = prefix;
        this.baseversion = baseversion;
        this.extensionLevel = extensionLevel;
    }

    public PdfName getPrefix() {
        return this.prefix;
    }

    public PdfName getBaseversion() {
        return this.baseversion;
    }

    public int getExtensionLevel() {
        return this.extensionLevel;
    }

    public PdfDictionary getDeveloperExtensions() {
        PdfDictionary developerextensions = new PdfDictionary();
        developerextensions.put(PdfName.BASEVERSION, this.baseversion);
        developerextensions.put(PdfName.EXTENSIONLEVEL, new PdfNumber(this.extensionLevel));
        return developerextensions;
    }
}
