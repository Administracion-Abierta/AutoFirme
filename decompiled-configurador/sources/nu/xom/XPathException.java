package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/XPathException.class */
public class XPathException extends RuntimeException {
    private static final long serialVersionUID = 6362087755031657439L;
    private String expression;
    private Throwable cause;
    private boolean causeSet;

    public XPathException(String str) {
        super(str);
        this.causeSet = false;
    }

    public XPathException(String str, Throwable th) {
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
            throw new IllegalStateException("Can't overwrite cause");
        }
        if (th == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.cause = th;
        this.causeSet = true;
        return this;
    }

    void setXPath(String str) {
        this.expression = str;
    }

    public String getXPath() {
        return this.expression;
    }
}
