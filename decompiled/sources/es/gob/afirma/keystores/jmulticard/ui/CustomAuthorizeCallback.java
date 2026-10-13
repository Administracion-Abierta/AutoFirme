package es.gob.afirma.keystores.jmulticard.ui;

import javax.security.auth.callback.Callback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/CustomAuthorizeCallback.class */
public final class CustomAuthorizeCallback implements Callback {
    private boolean authorized = false;

    public boolean isAuthorized() {
        return this.authorized;
    }

    public void setAuthorized(boolean z) {
        this.authorized = z;
    }
}
