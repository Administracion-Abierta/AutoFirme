package nu.xom.xslt;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/xslt/XSLException.class */
public class XSLException extends Exception {
    private static final long serialVersionUID = -8605437693812807627L;
    private Throwable cause;
    private boolean causeSet;

    public XSLException(String str, Throwable th) {
        super(str);
        this.causeSet = false;
        initCause(th);
    }

    public XSLException(String str) {
        super(str);
        this.causeSet = false;
    }

    @Override // java.lang.Throwable
    public final Throwable initCause(Throwable th) {
        if (this.causeSet) {
            throw new IllegalStateException("Can't overwrite cause");
        }
        if (th == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.cause = th;
        this.causeSet = true;
        return this;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
