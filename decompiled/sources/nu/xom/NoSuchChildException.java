package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/NoSuchChildException.class */
public class NoSuchChildException extends XMLException {
    private static final long serialVersionUID = 1944673590646036964L;

    public NoSuchChildException(String str) {
        super(str);
    }

    public NoSuchChildException(String str, Throwable th) {
        super(str);
        initCause(th);
    }
}
