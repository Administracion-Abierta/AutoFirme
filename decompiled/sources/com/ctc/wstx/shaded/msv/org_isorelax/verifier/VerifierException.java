package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/VerifierException.class */
public class VerifierException extends SAXException {
    public VerifierException(String s) {
        super(s);
    }

    public VerifierException(Exception exception) {
        super(exception);
    }

    public VerifierException(String s, Exception exception) {
        super(s, exception);
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(new PrintWriter((OutputStream) System.err, true));
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream printstream) {
        printStackTrace(new PrintWriter(printstream));
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter printwriter) {
        if (printwriter == null) {
            printwriter = new PrintWriter((OutputStream) System.err, true);
        }
        super.printStackTrace(printwriter);
        Exception exception = super.getException();
        if (exception != null) {
            printwriter.println();
            printwriter.println("StackTrace of Original Exception:");
            exception.printStackTrace(printwriter);
        }
    }
}
