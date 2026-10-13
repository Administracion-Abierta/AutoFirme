package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/RuntimeConfigNeededException.class */
public class RuntimeConfigNeededException extends AOException {
    private static final long serialVersionUID = 6782826789825877195L;
    private final RequestType requestType;
    private final String requestorText;
    private final String param;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/RuntimeConfigNeededException$RequestType.class */
    public enum RequestType {
        CONFIRM,
        PASSWORD
    }

    public RuntimeConfigNeededException(String str, RequestType requestType, String str2, String str3) {
        super(str);
        this.requestType = requestType;
        this.requestorText = str2;
        this.param = str3;
    }

    public RuntimeConfigNeededException(String str, RequestType requestType, String str2, String str3, Throwable th) {
        super(str, th);
        this.requestType = requestType;
        this.requestorText = str2;
        this.param = str3;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public String getRequestorText() {
        return this.requestorText;
    }

    public String getParam() {
        return this.param;
    }
}
