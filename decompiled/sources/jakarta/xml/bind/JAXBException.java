package jakarta.xml.bind;

import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXBException.class */
public class JAXBException extends Exception {
    private String errorCode;
    private volatile Throwable linkedException;
    static final long serialVersionUID = -5621384651494307979L;

    public JAXBException(String message) {
        this(message, null, null);
    }

    public JAXBException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public JAXBException(Throwable exception) {
        this(null, null, exception);
    }

    public JAXBException(String message, Throwable exception) {
        this(message, null, exception);
    }

    public JAXBException(String message, String errorCode, Throwable exception) {
        super(message);
        this.errorCode = errorCode;
        this.linkedException = exception;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Throwable getLinkedException() {
        return this.linkedException;
    }

    public void setLinkedException(Throwable exception) {
        this.linkedException = exception;
    }

    @Override // java.lang.Throwable
    public String toString() {
        if (this.linkedException == null) {
            return super.toString();
        }
        return super.toString() + "\n - with linked exception:\n[" + this.linkedException.toString() + "]";
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.linkedException;
    }
}
