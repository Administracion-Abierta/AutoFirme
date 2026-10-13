package nu.xom.jaxen.saxpath;

import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/SAXPathException.class */
public class SAXPathException extends Exception {
    private static final long serialVersionUID = 4826444568928720706L;
    private static double javaVersion;
    private Throwable cause;
    private boolean causeSet;

    public SAXPathException(String str) {
        super(str);
        this.causeSet = false;
    }

    public SAXPathException(Throwable th) {
        super(th.getMessage());
        this.causeSet = false;
        initCause(th);
    }

    public SAXPathException(String str, Throwable th) {
        super(str);
        this.causeSet = false;
        initCause(th);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public Throwable initCause(Throwable th) {
        if (this.causeSet) {
            throw new IllegalStateException("Cause cannot be reset");
        }
        if (th == this) {
            throw new IllegalArgumentException("Exception cannot be its own cause");
        }
        this.causeSet = true;
        this.cause = th;
        return this;
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        if (javaVersion >= 1.4d || getCause() == null) {
            return;
        }
        printStream.print("Caused by: ");
        getCause().printStackTrace(printStream);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (javaVersion >= 1.4d || getCause() == null) {
            return;
        }
        printWriter.print("Caused by: ");
        getCause().printStackTrace(printWriter);
    }

    static {
        javaVersion = 1.4d;
        try {
            javaVersion = Double.valueOf(System.getProperty("java.version").substring(0, 3)).doubleValue();
        } catch (Exception e) {
        }
    }
}
