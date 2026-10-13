package com.aowagie.text.xml.xmp;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/XmpBasicSchema.class */
class XmpBasicSchema extends XmpSchema {
    private static final long serialVersionUID = -2416613941622479298L;
    private static final String DEFAULT_XPATH_ID = "xmp";
    private static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/xap/1.0/";
    private static final String CREATEDATE = "xmp:CreateDate";
    private static final String CREATORTOOL = "xmp:CreatorTool";
    private static final String IDENTIFIER = "xmp:Identifier";
    private static final String METADATADATE = "xmp:MetadataDate";
    private static final String MODIFYDATE = "xmp:ModifyDate";
    private static final String NICKNAME = "xmp:Nickname";

    public XmpBasicSchema() {
        super("xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"");
    }

    void addCreatorTool(String creator) {
        setProperty(CREATORTOOL, creator);
    }

    void addCreateDate(String date) {
        setProperty(CREATEDATE, date);
    }

    void addModDate(String date) {
        setProperty(MODIFYDATE, date);
    }
}
