package es.gob.afirma.standalone;

import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpManagerImpl;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.IOException;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/HttpManager.class */
public class HttpManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final UrlHttpManager urlManager = UrlHttpManagerFactory.getInstalledManager();

    public HttpManager() {
        setSecureConnections(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_SECURE_CONNECTIONS));
        setSecureDomains(PreferencesManager.get("secureDomainsList"));
    }

    public byte[] readUrl(String str, UrlHttpMethod urlHttpMethod) throws IOException {
        SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor();
        try {
            return this.urlManager.readUrl(str, urlHttpMethod, sSLErrorProcessor);
        } catch (IOException e) {
            if (sSLErrorProcessor.isCancelled()) {
                LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza para el acceso a " + LoggerUtil.getTrimStr(str.toString()));
            }
            throw e;
        }
    }

    public byte[] readUrl(StringBuilder sb, UrlHttpMethod urlHttpMethod) throws IOException {
        return this.urlManager.readUrl(sb.toString(), urlHttpMethod);
    }

    public static void setSecureConnections(boolean z) {
        System.setProperty(UrlHttpManagerImpl.JAVA_PARAM_DISABLE_SSL_CHECKS, Boolean.toString(!z));
    }

    public static void setSecureDomains(String str) {
        if (str != null && !str.trim().isEmpty()) {
            System.setProperty("secureDomainsList", str);
        } else {
            System.clearProperty("secureDomainsList");
        }
    }
}
