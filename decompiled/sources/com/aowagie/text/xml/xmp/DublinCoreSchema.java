package com.aowagie.text.xml.xmp;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/DublinCoreSchema.class */
class DublinCoreSchema extends XmpSchema {
    private static final long serialVersionUID = -4551741356374797330L;
    private static final String DEFAULT_XPATH_ID = "dc";
    private static final String DEFAULT_XPATH_URI = "http://purl.org/dc/elements/1.1/";
    private static final String CREATOR = "dc:creator";
    private static final String DESCRIPTION = "dc:description";
    private static final String FORMAT = "dc:format";
    private static final String SUBJECT = "dc:subject";
    private static final String TITLE = "dc:title";

    public DublinCoreSchema() {
        super("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        setProperty(FORMAT, "application/pdf");
    }

    public void addTitle(String title) {
        XmpArray array = new XmpArray("rdf:Alt");
        array.add(title);
        setProperty(TITLE, array);
    }

    public void addDescription(String desc) {
        XmpArray array = new XmpArray("rdf:Alt");
        array.add(desc);
        setProperty(DESCRIPTION, array);
    }

    void addSubject(String subject) {
        XmpArray array = new XmpArray(XmpArray.UNORDERED);
        array.add(subject);
        setProperty(SUBJECT, array);
    }

    void addAuthor(String author) {
        XmpArray array = new XmpArray(XmpArray.ORDERED);
        array.add(author);
        setProperty(CREATOR, array);
    }
}
