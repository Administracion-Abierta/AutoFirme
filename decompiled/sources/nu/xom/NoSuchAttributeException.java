package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/NoSuchAttributeException.class */
public class NoSuchAttributeException extends XMLException {
    private static final long serialVersionUID = -7472517723464699452L;

    public NoSuchAttributeException(String str) {
        super(str);
    }

    public NoSuchAttributeException(String str, Throwable th) {
        super(str);
        initCause(th);
    }
}
