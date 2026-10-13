package es.gob.afirma.core.misc.http;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/UrlHttpManagerFactory.class */
public abstract class UrlHttpManagerFactory {
    private static UrlHttpManager staticUrlManager = null;

    public static void install(UrlHttpManager urlHttpManager) {
        staticUrlManager = urlHttpManager;
    }

    public static UrlHttpManager getInstalledManager() {
        if (staticUrlManager == null) {
            staticUrlManager = new UrlHttpManagerImpl();
        }
        return staticUrlManager;
    }
}
