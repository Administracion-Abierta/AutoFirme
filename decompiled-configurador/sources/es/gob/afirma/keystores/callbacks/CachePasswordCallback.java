package es.gob.afirma.keystores.callbacks;

import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/callbacks/CachePasswordCallback.class */
public final class CachePasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = 816457144215238935L;

    public CachePasswordCallback(char[] cArr) {
        super(">", false);
        setPassword(cArr);
    }

    public String toString() {
        return "PasswordCallback con contrasena '" + new String(getPassword()) + "'";
    }
}
