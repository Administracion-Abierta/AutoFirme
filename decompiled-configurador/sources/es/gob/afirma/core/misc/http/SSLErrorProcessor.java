package es.gob.afirma.core.misc.http;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.CoreMessages;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/http/SSLErrorProcessor.class */
public class SSLErrorProcessor implements HttpErrorProcessor {
    private static final String PROPERTY_HEADLESS = "headless";
    private static final String PROPERTY_SSL_HEADLESS = "sslOmitImportationDialog";
    static final String TRUSTED_KS_PWD = "changeit";
    private boolean initialized;
    private boolean cancelled;
    private boolean headless;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static boolean showingConfirmDialog = false;

    public SSLErrorProcessor() {
        this.initialized = false;
        this.cancelled = false;
        this.headless = false;
    }

    public SSLErrorProcessor(Properties properties) {
        boolean z;
        this.initialized = false;
        this.cancelled = false;
        this.headless = properties != null && (Boolean.parseBoolean(properties.getProperty("headless")) || Boolean.parseBoolean(properties.getProperty(PROPERTY_SSL_HEADLESS)));
        if (!this.headless) {
            try {
                z = !hasGraphicsEnvironment();
            } catch (Throwable th) {
                z = true;
            }
            this.headless = z;
        }
    }

    private void initilize() {
        boolean z;
        if (!this.initialized) {
            if (!this.headless) {
                try {
                    z = !hasGraphicsEnvironment();
                } catch (Throwable th) {
                    z = true;
                }
                this.headless = z;
            }
            this.initialized = true;
        }
    }

    private static boolean hasGraphicsEnvironment() {
        boolean zBooleanValue;
        try {
            Object objInvoke = Class.forName("java.awt.GraphicsEnvironment").getMethod("isHeadless", new Class[0]).invoke(null, new Object[0]);
            zBooleanValue = objInvoke instanceof Boolean ? ((Boolean) objInvoke).booleanValue() : true;
        } catch (Throwable th) {
            LOGGER.warning("No se pudo comprobar si hay entorno gr&aacute;fico: " + th);
            zBooleanValue = true;
        }
        return !zBooleanValue;
    }

    public SSLErrorProcessor(boolean z) {
        this.initialized = false;
        this.cancelled = false;
        this.headless = z;
    }

    @Override // es.gob.afirma.core.misc.http.HttpErrorProcessor
    public byte[] processHttpError(IOException iOException, UrlHttpManager urlHttpManager, String str, int i, UrlHttpMethod urlHttpMethod, Properties properties) throws IOException {
        int iShowConfirmDialog;
        if (!(iOException instanceof SSLHandshakeException)) {
            LOGGER.info("El error no es de tipo SSLHandShake y no la tratamos");
            throw iOException;
        }
        initilize();
        if (this.headless) {
            LOGGER.info("No se confia en el certificado SSL, pero no se pueden mostrar dialogos para importarlo en el almacen de confianza");
            throw iOException;
        }
        if (showingConfirmDialog) {
            LOGGER.info("Ya se esta mostrando un dialogo de consulta para la importacion del certificado de confianza. Se omitira este.");
            throw iOException;
        }
        synchronized (LOGGER) {
            try {
                try {
                    showingConfirmDialog = true;
                    iShowConfirmDialog = AOUIFactory.showConfirmDialog(null, CoreMessages.getString("SSLRequestPermissionDialog.2", new URL(str).getHost()), CoreMessages.getString("SSLRequestPermissionDialog.1"), AOUIFactory.YES_NO_OPTION, AOUIFactory.WARNING_MESSAGE);
                    showingConfirmDialog = false;
                } catch (AOCancelledOperationException e) {
                    this.cancelled = true;
                    throw iOException;
                } catch (Exception e2) {
                    throw iOException;
                }
            } catch (Throwable th) {
                showingConfirmDialog = false;
                throw th;
            }
        }
        if (iShowConfirmDialog != AOUIFactory.YES_OPTION) {
            LOGGER.info("El usuario no importo el certificado en el almacen de confianza");
            this.cancelled = true;
            throw iOException;
        }
        try {
            X509Certificate[] x509CertificateArrDownloadFromRemoteServer = downloadFromRemoteServer(str);
            for (X509Certificate x509Certificate : x509CertificateArrDownloadFromRemoteServer) {
                LOGGER.info("Se importa en caliente en el almacen de confianza el certificado con el numero de serie: " + AOUtil.hexify(x509Certificate.getSerialNumber().toByteArray(), false));
            }
            try {
                TrustStoreManager.getInstance().importCerts(x509CertificateArrDownloadFromRemoteServer);
                try {
                    SslSecurityManager.configureAfirmaTrustManagers();
                    return urlHttpManager.readUrl(str, i, urlHttpMethod, properties);
                } catch (Exception e3) {
                    LOGGER.severe("Error reconfigurando el contexto SSL con los nuevos certificados: " + e3);
                    throw new IOException(e3);
                }
            } catch (Exception e4) {
                LOGGER.severe("Error al importar los certificados SSL en el almacen de confianza: " + e4);
                throw new IOException(e4);
            }
        } catch (Exception e5) {
            LOGGER.severe("Error al descargar certificados SSL del servidor: " + e5);
            throw new IOException(e5);
        }
    }

    private static X509Certificate[] downloadFromRemoteServer(String str) throws Exception {
        X509Certificate[] x509CertificateArr;
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(str).openConnection();
        SslSecurityManager.disableSslChecks(httpsURLConnection);
        httpsURLConnection.connect();
        Certificate[] serverCertificates = httpsURLConnection.getServerCertificates();
        httpsURLConnection.disconnect();
        if (serverCertificates.length > 1) {
            x509CertificateArr = new X509Certificate[serverCertificates.length - 1];
            for (int i = 1; i < serverCertificates.length; i++) {
                x509CertificateArr[i - 1] = (X509Certificate) serverCertificates[i];
            }
        } else {
            x509CertificateArr = new X509Certificate[]{(X509Certificate) serverCertificates[0]};
        }
        return x509CertificateArr;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}
