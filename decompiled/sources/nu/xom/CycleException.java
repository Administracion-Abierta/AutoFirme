package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/CycleException.class */
public class CycleException extends IllegalAddException {
    private static final long serialVersionUID = 6690940405434358625L;

    public CycleException(String str) {
        super(str);
    }

    public CycleException(String str, Throwable th) {
        super(str, th);
    }
}
