package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import java.util.Iterator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/IslandSchema.class */
public interface IslandSchema {
    IslandVerifier createNewVerifier(String str, ElementDecl[] elementDeclArr);

    ElementDecl getElementDeclByName(String str);

    Iterator iterateElementDecls();

    ElementDecl[] getElementDecls();

    AttributesDecl getAttributesDeclByName(String str);

    Iterator iterateAttributesDecls();

    AttributesDecl[] getAttributesDecls();

    AttributesVerifier createNewAttributesVerifier(String str, AttributesDecl[] attributesDeclArr);

    void bind(SchemaProvider schemaProvider, ErrorHandler errorHandler) throws SAXException;
}
