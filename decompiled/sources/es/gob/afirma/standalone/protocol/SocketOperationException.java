package es.gob.afirma.standalone.protocol;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/SocketOperationException.class */
class SocketOperationException extends Exception {
    private static final long serialVersionUID = -1031351741046263364L;
    private String errorCode;

    SocketOperationException(String str) {
        setErrorCode(str);
    }

    SocketOperationException(String str, Throwable th) {
        super(th != null ? th.getMessage() : null, th);
        setErrorCode(str);
    }

    SocketOperationException(String str, String str2, Throwable th) {
        super(str2 != null ? str2 : th != null ? th.getMessage() : null, th);
        setErrorCode(str);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String str) {
        this.errorCode = str;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : this.errorCode;
    }
}
