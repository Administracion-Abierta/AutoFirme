package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/XMLException.class */
public class XMLException extends RuntimeException {
    private static final long serialVersionUID = -4497254051626978523L;
    private Throwable cause;
    private boolean causeSet;

    public XMLException(String str, Throwable th) {
        super(str);
        this.causeSet = false;
        initCause(th);
    }

    public XMLException(String str) {
        super(str);
        this.causeSet = false;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
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
}
