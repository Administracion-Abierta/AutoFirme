package nu.xom.xinclude;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xinclude/XPointerException.class */
class XPointerException extends Exception {
    private static final long serialVersionUID = -533329510409448717L;
    private Throwable cause;
    private boolean causeSet;

    XPointerException(String str) {
        super(str);
        this.cause = null;
        this.causeSet = false;
    }

    XPointerException(String str, Throwable th) {
        super(str);
        this.cause = null;
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
}
