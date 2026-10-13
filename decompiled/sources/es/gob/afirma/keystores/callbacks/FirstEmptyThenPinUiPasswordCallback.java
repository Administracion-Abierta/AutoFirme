package es.gob.afirma.keystores.callbacks;

import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/callbacks/FirstEmptyThenPinUiPasswordCallback.class */
public final class FirstEmptyThenPinUiPasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = -6862690043430780707L;
    private static final boolean isFirstTime = true;
    private final UIPasswordCallback uiPasswordCallback;

    public FirstEmptyThenPinUiPasswordCallback(String str, Object obj) {
        super(str, false);
        this.uiPasswordCallback = new UIPasswordCallback(str, obj);
    }

    public FirstEmptyThenPinUiPasswordCallback(String str) {
        super(str, false);
        this.uiPasswordCallback = new UIPasswordCallback(str);
    }

    public void setParent(Object obj) {
        this.uiPasswordCallback.setParent(obj);
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        return new char[0];
    }
}
