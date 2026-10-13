package es.gob.afirma.keystores.jmulticard.ui;

import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/SmartcardCallbackHandler.class */
public final class SmartcardCallbackHandler implements CallbackHandler {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbackArr) throws UnsupportedCallbackException {
        if (callbackArr != null) {
            for (Callback callback : callbackArr) {
                if (callback != null) {
                    if (callback instanceof PasswordCallback) {
                        ((PasswordCallback) callback).setPassword(new CommonPasswordCallback(((PasswordCallback) callback).getPrompt(), Messages.getString("CommonPasswordCallback.2"), false).getPassword());
                        return;
                    }
                    LOGGER.severe("Callback no soportada: " + callback.getClass().getName());
                }
            }
        } else {
            LOGGER.warning("Se ha recibido un array de Callbacks nulo");
        }
        throw new UnsupportedCallbackException(null);
    }
}
