package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/MatcherBundle.class */
class MatcherBundle extends Matcher {
    protected Matcher[] children;
    private int depth;

    protected final int getDepth() {
        return this.depth;
    }

    protected MatcherBundle(IDConstraintChecker owner) {
        super(owner);
        this.depth = 0;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
    protected void startElement(String namespaceURI, String localName) throws SAXException {
        this.depth++;
        for (int i = 0; i < this.children.length; i++) {
            this.children[i].startElement(namespaceURI, localName);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
    protected void onAttribute(String namespaceURI, String localName, String value, Datatype type) throws SAXException {
        for (int i = 0; i < this.children.length; i++) {
            this.children[i].onAttribute(namespaceURI, localName, value, type);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
    protected void endElement(Datatype type) throws SAXException {
        for (int i = 0; i < this.children.length; i++) {
            this.children[i].endElement(type);
        }
        int i2 = this.depth;
        this.depth = i2 - 1;
        if (i2 == 0) {
            this.owner.remove(this);
            onRemoved();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
    protected void characters(char[] buf, int start, int len) throws SAXException {
        for (int i = 0; i < this.children.length; i++) {
            this.children[i].characters(buf, start, len);
        }
    }

    protected void onRemoved() throws SAXException {
    }
}
