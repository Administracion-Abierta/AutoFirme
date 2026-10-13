package es.gob.afirma.keystores.jmulticard.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/DnieCallbackHandler.class */
public final class DnieCallbackHandler implements CallbackHandler {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbackArr) throws UnsupportedCallbackException {
        if (callbackArr != null) {
            for (Callback callback : callbackArr) {
                if (callback != null) {
                    if ("es.gob.jmulticard.callback.CustomTextInputCallback".equals(callback.getClass().getName()) || "javax.security.auth.callback.TextInputCallback".equals(callback.getClass().getName())) {
                        LOGGER.info("Solicitamos codigo CAN del DNIe");
                        try {
                            callback.getClass().getMethod("setText", String.class).invoke(callback, new String(new UIPasswordCallbackCan(Messages.getString("CanPasswordCallback.0"), null, Messages.getString("CanPasswordCallback.0"), Messages.getString("CanPasswordCallback.2")).getPassword()));
                        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                            throw new UnsupportedCallbackException(callback, "No se ha podido invocar al metodo 'setText' de la callback: " + e);
                        }
                    } else if (callback instanceof CustomAuthorizeCallback) {
                        LOGGER.info("Solicitamos autorizacion para el uso del DNIe");
                        DialogBuilder.showSignatureConfirmDialog((CustomAuthorizeCallback) callback);
                    } else if (callback instanceof PasswordCallback) {
                        LOGGER.info("Solicitamos codigo PIN del DNIe");
                        ((PasswordCallback) callback).setPassword(new CommonPasswordCallback(((PasswordCallback) callback).getPrompt(), Messages.getString("CommonPasswordCallback.1"), true).getPassword());
                    } else {
                        throw new UnsupportedCallbackException(callback);
                    }
                }
            }
            return;
        }
        LOGGER.warning("Se ha recibido un array de Callbacks nulo");
    }
}
