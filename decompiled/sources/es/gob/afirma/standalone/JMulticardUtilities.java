package es.gob.afirma.standalone;

import es.gob.afirma.keystores.KeyStoreUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/JMulticardUtilities.class */
public class JMulticardUtilities {
    public static boolean isJMulticardConfigurateBySystem() {
        return (System.getenv(KeyStoreUtilities.DISABLE_DNIE_NATIVE_DRIVER_ENV) == null && System.getenv(KeyStoreUtilities.DISABLE_CERES_NATIVE_DRIVER_ENV) == null) ? false : true;
    }

    public static void configureJMulticard(boolean z) {
        if (z) {
            System.clearProperty(KeyStoreUtilities.DISABLE_DNIE_NATIVE_DRIVER);
            System.clearProperty(KeyStoreUtilities.DISABLE_CERES_NATIVE_DRIVER);
        } else {
            System.setProperty(KeyStoreUtilities.DISABLE_DNIE_NATIVE_DRIVER, Boolean.TRUE.toString());
            System.setProperty(KeyStoreUtilities.DISABLE_CERES_NATIVE_DRIVER, Boolean.TRUE.toString());
        }
    }
}
