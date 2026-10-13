package es.gob.afirma.keystores.callbacks;

import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/callbacks/NullPasswordCallback.class */
public final class NullPasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = -5926953046433722802L;
    private static final NullPasswordCallback INSTANCE = new NullPasswordCallback();

    private NullPasswordCallback() {
        super(">", false);
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        return null;
    }

    public static NullPasswordCallback getInstance() {
        return INSTANCE;
    }
}
