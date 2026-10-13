package nu.xom.xinclude;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xinclude/NoIncludeLocationException.class */
public class NoIncludeLocationException extends XIncludeException {
    private static final long serialVersionUID = -2839892819780144596L;

    public NoIncludeLocationException(String str) {
        super(str);
    }

    public NoIncludeLocationException(String str, Throwable th) {
        super(str);
        initCause(th);
    }

    public NoIncludeLocationException(String str, String str2) {
        super(str, str2);
    }
}
