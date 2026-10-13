package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/EmbeddedSchema.class */
public class EmbeddedSchema {
    private String systemId;
    private Element schemaElement;

    public EmbeddedSchema(String systemId, Element schemaElement) {
        this.systemId = systemId;
        this.schemaElement = schemaElement;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public Element getSchemaElement() {
        return this.schemaElement;
    }
}
