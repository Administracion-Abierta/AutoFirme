package com.ctc.wstx.shaded.msv_core.driver.textui;

import com.ctc.wstx.shaded.msv_core.verifier.ValidationUnrecoverableException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/driver/textui/ReportErrorHandler.class */
public class ReportErrorHandler implements ErrorHandler {
    private int counter = 0;
    public boolean hadError = false;
    public static final String MSG_TOO_MANY_ERRORS = "ReportErrorHandler.TooManyErrors";
    public static final String MSG_ERROR = "ReportErrorHandler.Error";
    public static final String MSG_WARNING = "ReportErrorHandler.Warning";
    public static final String MSG_FATAL = "ReportErrorHandler.Fatal";

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException e) throws SAXException {
        this.hadError = true;
        countCheck(e);
        printSAXParseException(e, MSG_ERROR);
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException e) throws SAXException {
        this.hadError = true;
        printSAXParseException(e, MSG_FATAL);
        throw new ValidationUnrecoverableException(e);
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException e) {
        printSAXParseException(e, MSG_WARNING);
    }

    protected static void printSAXParseException(SAXParseException spe, String prop) {
        System.out.println(Driver.localize(prop, new Object[]{new Integer(spe.getLineNumber()), new Integer(spe.getColumnNumber()), spe.getSystemId(), spe.getLocalizedMessage()}));
    }

    private void countCheck(SAXParseException e) throws ValidationUnrecoverableException {
        int i = this.counter;
        this.counter = i + 1;
        if (i < 20) {
            return;
        }
        System.out.println(Driver.localize(MSG_TOO_MANY_ERRORS));
        throw new ValidationUnrecoverableException(e);
    }
}
