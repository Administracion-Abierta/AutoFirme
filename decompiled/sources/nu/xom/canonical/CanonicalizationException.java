package nu.xom.canonical;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/canonical/CanonicalizationException.class */
public class CanonicalizationException extends RuntimeException {
    private static final long serialVersionUID = 6935623053373600014L;
    private Throwable cause;
    private boolean causeSet;

    public CanonicalizationException(String str) {
        super(str);
        this.causeSet = false;
    }

    @Override // java.lang.Throwable
    public final Throwable getCause() {
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
