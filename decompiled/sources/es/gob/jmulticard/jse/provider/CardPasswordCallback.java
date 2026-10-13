package es.gob.jmulticard.jse.provider;

import java.security.KeyStore;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/CardPasswordCallback.class */
public final class CardPasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = -2511696590746468782L;
    private final transient KeyStore.PasswordProtection passp;

    public CardPasswordCallback(KeyStore.PasswordProtection pp, String prompt) {
        super(prompt, false);
        if (pp == null) {
            throw new IllegalArgumentException("El PasswordProtection no puede ser nulo");
        }
        this.passp = pp;
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        return this.passp.getPassword();
    }
}
