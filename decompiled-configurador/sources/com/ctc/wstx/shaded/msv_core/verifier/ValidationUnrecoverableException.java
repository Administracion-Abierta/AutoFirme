package com.ctc.wstx.shaded.msv_core.verifier;

import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/ValidationUnrecoverableException.class */
public class ValidationUnrecoverableException extends SAXParseException {
    public ValidationUnrecoverableException(SAXParseException vv) {
        super(vv.getMessage(), vv.getPublicId(), vv.getSystemId(), vv.getLineNumber(), vv.getColumnNumber(), vv);
    }
}
