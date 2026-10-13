package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/Matcher.class */
public abstract class Matcher {
    protected final IDConstraintChecker owner;

    protected abstract void startElement(String str, String str2) throws SAXException;

    protected abstract void onAttribute(String str, String str2, String str3, Datatype datatype) throws SAXException;

    protected abstract void endElement(Datatype datatype) throws SAXException;

    Matcher(IDConstraintChecker owner) {
        this.owner = owner;
    }

    protected void characters(char[] buf, int start, int len) throws SAXException {
    }
}
