package es.gob.afirma.signers.tsp.pkcs7;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/tsp/pkcs7/TsaParams.class */
public final class TsaParams {
    private static final String DEFAULT_DIGEST_ALGO = "SHA-512";
    private static final String DEFAULT_POLICY = "0.4.0.2023.1.1";
    public static final String TS_SIGN = "1";
    public static final String TS_DOC = "2";
    public static final String TS_SIGN_DOC = "3";
    private final boolean tsaRequireCert;
    private final String tsaPolicy;
    private final URI tsaURL;
    private final String tsaUsr;
    private final String tsaPwd;
    private final TsaRequestExtension[] extensions;
    private final String tsaHashAlgorithm;
    private final byte[] sslKeyStore;
    private final String sslKeyStorePassword;
    private final String sslKeyStoreType;
    private final byte[] sslTrustStore;
    private final String sslTrustStorePassword;
    private final String sslTrustStoreType;
    private final boolean verifyHostname;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public TsaParams(boolean z, String str, URI uri, String str2, String str3, TsaRequestExtension[] tsaRequestExtensionArr, String str4, byte[] bArr, String str5, String str6, byte[] bArr2, String str7, String str8, boolean z2) {
        if (uri == null) {
            throw new IllegalArgumentException("La URL del servidor de sello de tiempo no puede ser nula");
        }
        this.tsaURL = uri;
        this.tsaPolicy = str != null ? str : DEFAULT_POLICY;
        this.tsaUsr = str2;
        this.tsaPwd = str3;
        this.extensions = tsaRequestExtensionArr != null ? (TsaRequestExtension[]) tsaRequestExtensionArr.clone() : null;
        this.tsaHashAlgorithm = str4 != null ? str4 : "SHA-512";
        this.sslKeyStore = bArr != null ? (byte[]) bArr.clone() : null;
        this.sslKeyStorePassword = str5;
        this.sslKeyStoreType = str6;
        this.sslTrustStore = bArr2 != null ? (byte[]) bArr2.clone() : null;
        this.sslTrustStorePassword = str7;
        this.sslTrustStoreType = str8;
        this.tsaRequireCert = z;
        this.verifyHostname = z2;
    }

    public TsaParams(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("La propiedades de configuracion de la TSA no pueden ser nulas");
        }
        String property = properties.getProperty("tsaURL");
        if (property == null) {
            throw new IllegalArgumentException("La URL del servidor de sello de tiempo no puede ser nula");
        }
        try {
            this.tsaURL = new URI(property);
            this.tsaPolicy = properties.containsKey("tsaPolicy") ? properties.getProperty("tsaPolicy") : DEFAULT_POLICY;
            this.tsaHashAlgorithm = properties.containsKey("tsaHashAlgorithm") ? AOSignConstants.getDigestAlgorithmName(properties.getProperty("tsaHashAlgorithm")) : "SHA-512";
            this.tsaRequireCert = !Boolean.FALSE.toString().equalsIgnoreCase(properties.getProperty("tsaRequireCert"));
            this.tsaUsr = properties.getProperty("tsaUsr");
            this.tsaPwd = properties.getProperty("tsaPwd");
            String property2 = properties.getProperty("tsaSslKeyStore");
            if (property2 != null) {
                try {
                    this.sslKeyStore = Base64.decode(property2);
                } catch (Exception e) {
                    throw new IllegalArgumentException("No se ha proporcionado en el parametro 'tsaSslKeyStore' el almacen de claves del SSL de la TSA en base 64: " + e, e);
                }
            } else {
                this.sslKeyStore = null;
            }
            this.sslKeyStorePassword = properties.getProperty("tsaSslKeyStorePassword", "");
            this.sslKeyStoreType = properties.getProperty("tsaSslKeyStoreType", "PKCS12");
            String property3 = properties.getProperty("tsaSslTrustStore");
            if (property3 != null) {
                try {
                    this.sslTrustStore = Base64.decode(property3);
                } catch (Exception e2) {
                    throw new IllegalArgumentException("No se ha proporcionado en el parametro 'tsaSslTrustStore' el almacen de confianza del SSL de la TSA en base 64: " + e2, e2);
                }
            } else {
                this.sslTrustStore = null;
            }
            this.sslTrustStorePassword = properties.getProperty("tsaSslTrustStorePassword", "");
            this.sslTrustStoreType = properties.getProperty("tsaSslTrustStoreType", "PKCS12");
            try {
                this.extensions = getExtensions(properties);
                this.verifyHostname = Boolean.parseBoolean(properties.getProperty("verifyHostname", Boolean.TRUE.toString()));
            } catch (IOException e3) {
                throw new IllegalArgumentException("Las extensiones del sello de tiempo no estan adecuadamente codificadas: " + e3, e3);
            }
        } catch (Exception e4) {
            throw new IllegalArgumentException("Se ha indicado una URL de TSA invalida (" + property + "): " + e4, e4);
        }
    }

    boolean doTsaRequireCert() {
        return this.tsaRequireCert;
    }

    String getTsaPolicy() {
        return this.tsaPolicy;
    }

    public URI getTsaUrl() {
        return this.tsaURL;
    }

    String getTsaUsr() {
        return this.tsaUsr;
    }

    String getTsaPwd() {
        return this.tsaPwd;
    }

    TsaRequestExtension[] getExtensions() {
        return this.extensions;
    }

    private static TsaRequestExtension[] getExtensions(Properties properties) throws IOException {
        String property = properties.getProperty("tsaExtensionOid");
        String property2 = properties.getProperty("tsaExtensionValueBase64");
        boolean z = Boolean.parseBoolean(properties.getProperty("tsaExtensionCritical", Boolean.FALSE.toString()));
        if (property == null && property2 == null) {
            return null;
        }
        if (property != null && property2 == null) {
            LOGGER.warning("Se ignorara el parametro 'tsaExtensionOid' ya que no se configuro el parametro 'tsaExtensionValueBase64'");
            return null;
        }
        if (property == null && property2 != null) {
            LOGGER.warning("Se ignorara el parametro 'tsaExtensionValueBase64' ya que no se configuro el parametro 'tsaExtensionOid'");
            return null;
        }
        return new TsaRequestExtension[]{new TsaRequestExtension(property, z, Base64.decode(property2))};
    }

    public String getTsaHashAlgorithm() {
        return this.tsaHashAlgorithm;
    }

    public byte[] getSslKeyStore() {
        return this.sslKeyStore;
    }

    public String getSslKeyStorePassword() {
        return this.sslKeyStorePassword;
    }

    public String getSslKeyStoreType() {
        return this.sslKeyStoreType;
    }

    public byte[] getSslTrustStore() {
        return this.sslTrustStore;
    }

    public String getSslTrustStorePassword() {
        return this.sslTrustStorePassword;
    }

    public String getSslTrustStoreType() {
        return this.sslTrustStoreType;
    }

    public boolean isVerifyHostname() {
        return this.verifyHostname;
    }

    public Properties getExtraParams() {
        Properties properties = new Properties();
        if (getTsaUrl() != null) {
            properties.put("tsaURL", getTsaUrl().toString());
        }
        if (getTsaUsr() != null && !getTsaUsr().isEmpty()) {
            properties.put("tsaUsr", getTsaUsr().toString());
        }
        if (getTsaPwd() != null && !getTsaPwd().isEmpty()) {
            properties.put("tsaPwd", getTsaPwd().toString());
        }
        if (getTsaPolicy() != null && !getTsaPolicy().isEmpty()) {
            properties.put("tsaPolicy", getTsaPolicy().toString());
        }
        if (getExtensions() != null && getExtensions().length > 0) {
            properties.put("tsaExtensionOid", getExtensions()[0].getOid());
            properties.put("tsaExtensionValueBase64", Base64.encode(getExtensions()[0].getValue()));
            properties.put("tsaExtensionCritical", Boolean.toString(getExtensions()[0].isCritical()));
        }
        if (getTsaHashAlgorithm() != null && !getTsaHashAlgorithm().isEmpty()) {
            properties.put("tsaHashAlgorithm", getTsaHashAlgorithm());
        }
        if (getSslKeyStore() != null && getSslKeyStore().length > 0) {
            properties.put("tsaSslKeyStore", Base64.encode(getSslKeyStore()));
            properties.put("tsaSslKeyStorePassword", getSslKeyStorePassword());
            properties.put("tsaSslKeyStoreType", getSslKeyStoreType());
        }
        if (getSslTrustStore() != null && getSslTrustStore().length > 0) {
            properties.put("tsaSslTrustStore", Base64.encode(getSslTrustStore()));
            properties.put("tsaSslTrustStorePassword", getSslTrustStorePassword());
            properties.put("tsaSslTrustStoreType", getSslTrustStoreType());
        }
        properties.put("verifyHostname", Boolean.toString(isVerifyHostname()));
        return properties;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (this.extensions != null) {
            for (TsaRequestExtension tsaRequestExtension : this.extensions) {
                sb.append(tsaRequestExtension);
                sb.append("; ");
            }
        }
        sb.append("]");
        return ("Parametros TSA [URL=" + getTsaUrl() + "; User=" + getTsaUsr() + ": Policy=" + getTsaPolicy() + "; Extensions" + sb.toString() + "; Digest=" + getTsaHashAlgorithm() + "; SSLKeyStore=" + (getSslKeyStore() != null ? "Yes" : "No") + "; SSLKeyStorePwd=" + getSslKeyStorePassword() + "SSLKeyStoreType=" + getSslKeyStoreType() + "SSLTrustStore=" + (getSslTrustStore() != null ? "Yes" : "No") + "; SSLTrustStorePwd=" + getSslTrustStorePassword() + "SSLTrustStoreType=" + getSslTrustStoreType() + "VerifyHostname=" + isVerifyHostname() + "]").replace("]; ]", "]]");
    }
}
