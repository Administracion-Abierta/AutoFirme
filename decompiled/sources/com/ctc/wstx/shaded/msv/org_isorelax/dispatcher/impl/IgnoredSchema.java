package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import java.util.Iterator;
import java.util.Vector;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/impl/IgnoredSchema.class */
public class IgnoredSchema implements IslandSchema {
    private static final ElementDecl[] theElemDecl = {new ElementDecl() { // from class: com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl.IgnoredSchema.1
        @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
        public String getName() {
            return "$$any$$";
        }

        @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
        public Object getProperty(String propertyName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(propertyName);
        }

        @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
        public boolean getFeature(String featureName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(featureName);
        }
    }};
    private static final AttributesDecl[] theAttDecl = {new AttributesDecl() { // from class: com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl.IgnoredSchema.2
        @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl
        public String getName() {
            return "$$any$$";
        }

        @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl
        public Object getProperty(String propertyName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(propertyName);
        }

        @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl
        public boolean getFeature(String featureName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(featureName);
        }
    }};

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public ElementDecl getElementDeclByName(String name) {
        return theElemDecl[0];
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public ElementDecl[] getElementDecls() {
        return theElemDecl;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public Iterator iterateElementDecls() {
        Vector vec = new Vector();
        vec.add(theElemDecl[0]);
        return vec.iterator();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public IslandVerifier createNewVerifier(String namespaceURI, ElementDecl[] rules) {
        return new IgnoreVerifier(namespaceURI, rules);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public AttributesDecl getAttributesDeclByName(String name) {
        return theAttDecl[0];
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public AttributesDecl[] getAttributesDecls() {
        return theAttDecl;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public Iterator iterateAttributesDecls() {
        Vector vec = new Vector();
        vec.add(theAttDecl[0]);
        return vec.iterator();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public AttributesVerifier createNewAttributesVerifier(String namespaceURI, AttributesDecl[] decls) {
        throw new Error("not implemented yet");
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema
    public void bind(SchemaProvider provider, ErrorHandler handler) {
    }
}
