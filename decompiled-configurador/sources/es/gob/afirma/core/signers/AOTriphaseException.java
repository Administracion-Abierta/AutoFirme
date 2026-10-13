package es.gob.afirma.core.signers;

import es.gob.afirma.core.AOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOTriphaseException.class */
public class AOTriphaseException extends AOException {
    private static final long serialVersionUID = -6210469965589338895L;
    private String serverExceptionClassname;

    public AOTriphaseException(String str) {
        super(str);
        this.serverExceptionClassname = null;
    }

    public AOTriphaseException(Throwable th) {
        super(th);
        this.serverExceptionClassname = null;
    }

    public AOTriphaseException(String str, Throwable th) {
        super(str, th);
        this.serverExceptionClassname = null;
    }

    public void setServerExceptionClassname(String str) {
        this.serverExceptionClassname = str;
    }

    public String getServerExceptionClassname() {
        return this.serverExceptionClassname;
    }

    public static AOTriphaseException parseException(String str) {
        AOTriphaseException aOTriphaseException;
        int iIndexOf = str.indexOf(":");
        if (iIndexOf > 0) {
            aOTriphaseException = new AOTriphaseException(str.substring(iIndexOf + 1).trim());
            aOTriphaseException.setServerExceptionClassname(str.substring(0, iIndexOf).trim());
        } else {
            aOTriphaseException = new AOTriphaseException(str.trim());
        }
        return aOTriphaseException;
    }
}
