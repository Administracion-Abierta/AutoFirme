package com.ctc.wstx.shaded.msv_core.verifier;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/ValidityViolation.class */
public class ValidityViolation extends SAXParseException {
    private ErrorInfo errorInfo;

    public ErrorInfo getErrorInfo() {
        return this.errorInfo;
    }

    public ValidityViolation(Locator loc, String msg, ErrorInfo ei) {
        super(msg, loc);
        this.errorInfo = ei;
    }
}
