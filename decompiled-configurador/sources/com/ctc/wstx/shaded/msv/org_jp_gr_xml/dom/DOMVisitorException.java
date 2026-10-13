package com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/dom/DOMVisitorException.class */
public class DOMVisitorException extends RuntimeException {
    private Exception cause_;

    public DOMVisitorException(String s) {
        super(s);
        this.cause_ = null;
    }

    public DOMVisitorException(Exception exception) {
        super(exception.getMessage());
        this.cause_ = null;
        this.cause_ = exception;
    }

    public DOMVisitorException(String s, Exception exception) {
        super(s);
        this.cause_ = null;
        this.cause_ = exception;
    }

    public Exception getException() {
        if (this.cause_ != null) {
            return this.cause_;
        }
        return this;
    }

    public Exception getCauseException() {
        return this.cause_;
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
        if (this.cause_ != null) {
            printwriter.println();
            printwriter.println("StackTrace of Original Exception:");
            this.cause_.printStackTrace(printwriter);
        }
    }
}
