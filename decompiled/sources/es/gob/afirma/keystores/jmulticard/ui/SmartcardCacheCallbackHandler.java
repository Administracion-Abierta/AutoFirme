package es.gob.afirma.keystores.jmulticard.ui;

import java.util.Timer;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/SmartcardCacheCallbackHandler.class */
public final class SmartcardCacheCallbackHandler implements CallbackHandler, CacheElement {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long CACHE_TIMEOUT = 3600000;
    private static final String PREFERENCE_KEY_USE_CACHE = "useCacheCeres";
    private transient char[] cachedPassword = null;
    private transient Timer timer = null;

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbackArr) throws UnsupportedCallbackException {
        char[] password;
        if (callbackArr != null) {
            for (Callback callback : callbackArr) {
                if (callback != null) {
                    if (!(callback instanceof PasswordCallback)) {
                        throw new UnsupportedCallbackException(callback);
                    }
                    synchronized (LOGGER) {
                        if (this.cachedPassword == null) {
                            boolean zLoadUseCachePreference = loadUseCachePreference();
                            CommonPasswordCallback commonPasswordCallback = new CommonPasswordCallback(((PasswordCallback) callback).getPrompt(), Messages.getString("CommonPasswordCallback.2"), false, true, zLoadUseCachePreference);
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
                        this.timer.schedule(new ResetCacheTimerTask(this), 3600000L);
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
        }
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }

    private static boolean loadUseCachePreference() {
        try {
            return Preferences.userNodeForPackage(SmartcardCacheCallbackHandler.class).getBoolean(PREFERENCE_KEY_USE_CACHE, false);
        } catch (Exception e) {
            LOGGER.warning("No se puede acceder a la configuracion del cacheo del PIN de la tarjeta: " + e);
            return false;
        }
    }

    private static void setUseCachePreference(boolean z) {
        try {
            Preferences.userNodeForPackage(SmartcardCacheCallbackHandler.class).putBoolean(PREFERENCE_KEY_USE_CACHE, z);
        } catch (Exception e) {
            LOGGER.warning("No se pudo guardar la configuracion del cacheo del PIN de la tarjeta: " + e);
        }
    }
}
