package com.aowagie.text.xml.xmp;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/PdfA2Schema.class */
class PdfA2Schema extends XmpSchema {
    private static final long serialVersionUID = 5300646133692948168L;
    private static final String DEFAULT_XPATH_ID = "pdfaid";
    private static final String DEFAULT_XPATH_URI = "http://www.aiim.org/pdfa/ns/id/";
    private static final String PART = "pdfaid:part";
    private static final String CONFORMANCE = "pdfaid:conformance";

    public PdfA2Schema() {
        super("xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\"");
        addPart("2");
    }

    private void addPart(String part) {
        setProperty(PART, part);
    }

    public void addConformance(String conformance) {
        setProperty(CONFORMANCE, conformance);
    }
}
