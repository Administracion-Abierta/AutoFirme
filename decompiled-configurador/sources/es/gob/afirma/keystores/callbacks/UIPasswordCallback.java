package es.gob.afirma.keystores.callbacks;

import es.gob.afirma.core.ui.AOUIFactory;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/callbacks/UIPasswordCallback.class */
public final class UIPasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = 1719174318602363633L;
    private String childPrompt;
    private Object parent;

    public UIPasswordCallback(String str, Object obj) {
        super(str, false);
        this.childPrompt = null;
        this.parent = null;
        this.parent = obj;
    }

    public UIPasswordCallback(String str) {
        super(str, false);
        this.childPrompt = null;
        this.parent = null;
    }

    public void setParent(Object obj) {
        this.parent = obj;
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        return AOUIFactory.getPassword(this.childPrompt == null ? super.getPrompt() : this.childPrompt, this.parent);
    }

    public void setPrompt(String str) {
        this.childPrompt = str;
    }
}
