package es.gob.afirma.core.signers;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/AdESPolicy.class */
public final class AdESPolicy {
    private static final String DEFAULT_HASH_ALGORITHM = "SHA-512";
    private String policyIdentifier = null;
    private String policyIdentifierHash = null;
    private String policyIdentifierHashAlgorithm = null;
    private URL policyQualifier = null;

    public AdESPolicy(String str, String str2, String str3, String str4) {
        setValues(str, str2, str3, str4);
    }

    private void setValues(String str, String str2, String str3, String str4) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("El identificador de politica no puede ser nulo ni vacio");
        }
        this.policyIdentifier = str;
        if (str2 == null || "0".equals(str2)) {
            this.policyIdentifierHashAlgorithm = str3 == null ? "SHA-512" : AOSignConstants.getDigestAlgorithmName(str3);
            try {
                InputStream inputStreamOpenStream = new URL(str).openStream();
                Throwable th = null;
                try {
                    try {
                        this.policyIdentifierHash = Base64.encode(MessageDigest.getInstance(this.policyIdentifierHashAlgorithm).digest(AOUtil.getDataFromInputStream(inputStreamOpenStream)));
                        if (inputStreamOpenStream != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamOpenStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                inputStreamOpenStream.close();
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (inputStreamOpenStream != null) {
                        if (th != null) {
                            try {
                                inputStreamOpenStream.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            inputStreamOpenStream.close();
                        }
                    }
                    throw th4;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Si no se especifica la huella digital de la politica es necesario que el identificador sea una URL accesible universalmente: " + e, e);
            }
        } else {
            if (str3 == null || str3.isEmpty()) {
                throw new IllegalArgumentException("Si se indica la huella digital del identificador de politica es obligatorio indicar tambien el algoritmo");
            }
            if (!Base64.isBase64(str2.getBytes())) {
                throw new IllegalArgumentException("La huella digital de la politica debe estar en formato Base64");
            }
            this.policyIdentifierHash = str2;
            this.policyIdentifierHashAlgorithm = AOSignConstants.getDigestAlgorithmName(str3);
        }
        if (str4 != null && !str4.isEmpty()) {
            try {
                this.policyQualifier = new URL(str4);
            } catch (Exception e2) {
                throw new IllegalArgumentException("El calificador de la politica debe ser una URL valida", e2);
            }
        }
    }

    public String getPolicyIdentifier() {
        return this.policyIdentifier;
    }

    public String getPolicyIdentifierHash() {
        return this.policyIdentifierHash;
    }

    public String getPolicyIdentifierHashAlgorithm() {
        return this.policyIdentifierHashAlgorithm;
    }

    public URL getPolicyQualifier() {
        return this.policyQualifier;
    }

    public static AdESPolicy buildAdESPolicy(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Es necesario proporciona las propiedades de la politica");
        }
        String property = properties.getProperty("policyIdentifier");
        if (property == null) {
            return null;
        }
        return new AdESPolicy(property, properties.getProperty("policyIdentifierHash"), properties.getProperty("policyIdentifierHashAlgorithm"), properties.getProperty("policyQualifier"));
    }

    public Properties asExtraParams() {
        Properties properties = new Properties();
        properties.put("policyIdentifier", getPolicyIdentifier());
        if (getPolicyIdentifierHash() != null) {
            properties.put("policyIdentifierHash", getPolicyIdentifierHash());
        }
        if (getPolicyIdentifierHashAlgorithm() != null) {
            properties.put("policyIdentifierHashAlgorithm", getPolicyIdentifierHashAlgorithm());
        }
        if (getPolicyQualifier() != null) {
            properties.put("policyQualifier", getPolicyQualifier());
        }
        return properties;
    }

    public String toString() {
        return "Politica de firma: identificador='" + getPolicyIdentifier() + "', huella='" + getPolicyIdentifierHash() + "', algoritmo de huella='" + getPolicyIdentifierHashAlgorithm() + "', calificador='" + getPolicyQualifier();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AdESPolicy)) {
            return false;
        }
        AdESPolicy adESPolicy = (AdESPolicy) obj;
        return adESPolicy.getPolicyIdentifier().equals(getPolicyIdentifier()) && adESPolicy.getPolicyIdentifierHash().equals(getPolicyIdentifierHash()) && adESPolicy.getPolicyIdentifierHashAlgorithm().equals(getPolicyIdentifierHashAlgorithm());
    }

    public int hashCode() {
        return getPolicyIdentifier().hashCode();
    }
}
