package com.aowagie.text.xml.xmp;

import com.aowagie.text.Document;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/PdfSchema.class */
class PdfSchema extends XmpSchema {
    private static final long serialVersionUID = -1541148669123992185L;
    private static final String DEFAULT_XPATH_ID = "pdf";
    private static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/pdf/1.3/";
    private static final String KEYWORDS = "pdf:keywords";
    private static final String PRODUCER = "pdf:Producer";

    public PdfSchema() {
        super("xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\"");
        addProducer(Document.getVersion());
    }

    public void addKeywords(String keywords) {
        setProperty(KEYWORDS, keywords);
    }

    public void addProducer(String producer) {
        setProperty(PRODUCER, producer);
    }
}
