package es.gob.afirma.keystores.jmulticard.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/DnieCacheCallbackHandler.class */
public final class DnieCacheCallbackHandler implements CallbackHandler, CacheElement {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long CACHE_TIMEOUT = 3600000;
    private static final String PREFERENCE_KEY_USE_CACHE = "useCacheDni";
    private transient char[] cachedPassword = null;
    private transient boolean confirmed = false;
    private transient Timer timer = null;

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbackArr) throws UnsupportedCallbackException {
        char[] password;
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
                        if (!this.confirmed) {
                            LOGGER.info("Solicitamos autorizacion para el uso del DNIe");
                            DialogBuilder.showSignatureConfirmDialog((CustomAuthorizeCallback) callback);
                            this.confirmed = ((CustomAuthorizeCallback) callback).isAuthorized();
                        }
                        ((CustomAuthorizeCallback) callback).setAuthorized(this.confirmed);
                    } else if (callback instanceof PasswordCallback) {
                        synchronized (LOGGER) {
                            if (this.cachedPassword == null) {
                                LOGGER.info("Solicitamos codigo PIN del DNIe");
                                boolean zLoadUseCachePreference = loadUseCachePreference();
                                CommonPasswordCallback commonPasswordCallback = new CommonPasswordCallback(((PasswordCallback) callback).getPrompt(), Messages.getString("CommonPasswordCallback.1"), true, true, zLoadUseCachePreference);
                                password = commonPasswordCallback.getPassword();
                                boolean zIsUseCacheChecked = commonPasswordCallback.isUseCacheChecked();
                                if (zIsUseCacheChecked) {
                                    LOGGER.info("Guardamos en cache la contrasena de la tarjeta");
                                    this.cachedPassword = password;
                                }
                                if (zLoadUseCachePreference != zIsUseCacheChecked) {
                                    setUseCachePreference(zIsUseCacheChecked);
                                }
                            } else {
                                password = this.cachedPassword;
                            }
                            ((PasswordCallback) callback).setPassword(password);
                        }
                        if (this.timer == null) {
                            this.timer = new Timer();
                            this.timer.schedule(new ResetCacheTimerTask(this), CACHE_TIMEOUT);
                        }
                    } else {
                        throw new UnsupportedCallbackException(callback);
                    }
                }
            }
            return;
        }
        LOGGER.warning("Se ha recibido un array de Callbacks nulo");
    }

    @Override // es.gob.afirma.keystores.jmulticard.ui.CacheElement
    public void reset() {
        LOGGER.info("Eliminamos de cache la contrasena de la tarjeta");
        synchronized (LOGGER) {
            this.cachedPassword = null;
            this.confirmed = false;
        }
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }

    private static boolean loadUseCachePreference() {
        try {
            return Preferences.userNodeForPackage(DnieCacheCallbackHandler.class).getBoolean(PREFERENCE_KEY_USE_CACHE, false);
        } catch (Exception e) {
            LOGGER.warning("No se puede acceder a la configuracion del cacheo del PIN de la tarjeta: " + e);
            return false;
        }
    }

    private static void setUseCachePreference(boolean z) {
        try {
            Preferences.userNodeForPackage(DnieCacheCallbackHandler.class).putBoolean(PREFERENCE_KEY_USE_CACHE, z);
        } catch (Exception e) {
            LOGGER.warning("No se pudo guardar la configuracion del cacheo del PIN de la tarjeta: " + e);
        }
    }
}
