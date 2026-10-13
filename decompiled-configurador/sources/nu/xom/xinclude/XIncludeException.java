package nu.xom.xinclude;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xinclude/XIncludeException.class */
public class XIncludeException extends Exception {
    private static final long serialVersionUID = 969926723618314277L;
    private String uri;
    private Throwable cause;
    private boolean causeSet;

    public XIncludeException(String str) {
        super(str);
        this.causeSet = false;
    }

    public XIncludeException(String str, Throwable th) {
        super(str);
        this.causeSet = false;
        initCause(th);
    }

    public XIncludeException(String str, String str2) {
        super(str);
        this.causeSet = false;
        this.uri = str2;
    }

    public String getURI() {
        return this.uri;
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
}
