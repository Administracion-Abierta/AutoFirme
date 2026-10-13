package es.gob.afirma.core.signers;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/ExtraParamsProcessor.class */
public final class ExtraParamsProcessor {
    private static final int SIZE_1MB = 1048576;
    private static final String ETSI_CADES_DETACHED = "ETSI.CAdES.detached";
    private static final String EXPANDIBLE_POLICY_KEY = "expPolicy";
    private static final int MAX_PATH_SIZE = 255;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private ExtraParamsProcessor() {
    }

    public static Properties convertToProperties(String str) {
        Properties properties = new Properties();
        if (str == null) {
            return properties;
        }
        try {
            properties.load(new ByteArrayInputStream(str.getBytes()));
        } catch (Exception e) {
            LOGGER.warning("Se han encontrado entradas no validas en la configuracion de la operacion: " + e);
        }
        return properties;
    }

    public static Properties expandProperties(Properties properties) throws IncompatiblePolicyException {
        return expandProperties(properties, null, null);
    }

    public static Properties expandProperties(Properties properties, byte[] bArr, String str) throws IncompatiblePolicyException {
        Properties properties2 = new Properties();
        for (String str2 : (String[]) properties.keySet().toArray(new String[properties.size()])) {
            properties2.setProperty(str2, properties.getProperty(str2));
        }
        expandPolicyKeys(properties2, bArr, str);
        return properties2;
    }

    private static void expandPolicyKeys(Properties properties, byte[] bArr, String str) throws IncompatiblePolicyException {
        if (!properties.containsKey(EXPANDIBLE_POLICY_KEY)) {
            return;
        }
        String property = properties.getProperty(EXPANDIBLE_POLICY_KEY);
        if (!isSupportedPolicy(property)) {
            properties.remove(EXPANDIBLE_POLICY_KEY);
            throw new IncompatiblePolicyException("No se soporta la expansion de atributos para la politica: " + property);
        }
        String strNormalizeFormat = normalizeFormat(str);
        if (strNormalizeFormat.equals("CAdES") && ("FirmaAGE".equals(property) || "FirmaAGE18".equals(property))) {
            setCAdESPolicyAGEAttributes(property, properties, bArr);
        } else if (strNormalizeFormat.equals(AOSignConstants.SIGN_FORMAT_XADES) && ("FirmaAGE".equals(property) || "FirmaAGE18".equals(property))) {
            setXAdESPolicyAGEAttributes(property, properties);
        } else if (strNormalizeFormat.equals(AOSignConstants.SIGN_FORMAT_PADES) && "FirmaAGE".equals(property)) {
            setPAdESPolicyAGEAttributes(property, properties);
        } else {
            properties.remove(EXPANDIBLE_POLICY_KEY);
            throw new IncompatiblePolicyException(String.format("El formato de firma %1s no esta soportado por la politica %2s", str, property));
        }
        properties.remove(EXPANDIBLE_POLICY_KEY);
    }

    private static String normalizeFormat(String str) {
        String str2 = null;
        if (str.equalsIgnoreCase("CAdES") || str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_CADES_TRI)) {
            str2 = "CAdES";
        } else if (str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES) || str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_TRI) || str.toLowerCase(Locale.US).startsWith(AOSignConstants.SIGN_FORMAT_XADES.toLowerCase(Locale.US) + " ")) {
            str2 = AOSignConstants.SIGN_FORMAT_XADES;
        } else if (str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_PDF) || str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_PADES) || str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_PDF_TRI) || str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_PADES_TRI)) {
            str2 = AOSignConstants.SIGN_FORMAT_PADES;
        }
        return str2 != null ? str2 : str;
    }

    private static boolean isSupportedPolicy(String str) {
        return "FirmaAGE".equals(str) || "FirmaAGE18".equals(str);
    }

    private static void setCAdESPolicyAGEAttributes(String str, Properties properties, byte[] bArr) {
        if (!properties.containsKey("mode") && bArr != null) {
            properties.setProperty("mode", bArr.length < 1048576 ? AOSignConstants.SIGN_MODE_IMPLICIT : "explicit");
        }
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(properties.getProperty(XAdESExtraParams.PROFILE))) {
            LOGGER.warning("Se ignora la configuracion del parametro 'profile' en favor de los requisitos establecidos por la politica de firma");
        }
        properties.setProperty(XAdESExtraParams.PROFILE, "advanced");
        AdESPolicyPropertiesManager.setProperties(properties, str, "CAdES");
    }

    private static void setXAdESPolicyAGEAttributes(String str, Properties properties) {
        String property = properties.getProperty(XAdESExtraParams.FORMAT);
        if (!AOSignConstants.SIGN_FORMAT_XADES_DETACHED.equalsIgnoreCase(property) && !AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equalsIgnoreCase(property)) {
            if (property != null) {
                LOGGER.warning("Se ignora la configuracion del parametro 'format' en favor de los requisitos establecidos por la politica de firma");
            }
            properties.setProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
        }
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(properties.getProperty(XAdESExtraParams.PROFILE))) {
            LOGGER.warning("Se ignora la configuracion del parametro 'profile' en favor de los requisitos establecidos por la politica de firma");
        }
        properties.setProperty(XAdESExtraParams.PROFILE, "advanced");
        AdESPolicyPropertiesManager.setProperties(properties, str, AOSignConstants.SIGN_FORMAT_XADES);
    }

    private static void setPAdESPolicyAGEAttributes(String str, Properties properties) throws IncompatiblePolicyException {
        if (properties.containsKey("signatureSubFilter") && !"ETSI.CAdES.detached".equals(properties.getProperty("signatureSubFilter"))) {
            throw new IncompatiblePolicyException("En PAdES con politica firma AGE debe usarse siempre el filtro 'ETSI.CAdES.detached'");
        }
        properties.setProperty("signatureSubFilter", "ETSI.CAdES.detached");
        AdESPolicyPropertiesManager.setProperties(properties, str, AOSignConstants.SIGN_FORMAT_PADES);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/ExtraParamsProcessor$IncompatiblePolicyException.class */
    public static final class IncompatiblePolicyException extends Exception {
        private static final long serialVersionUID = -6420193548487585455L;

        IncompatiblePolicyException(String str) {
            super(str);
        }
    }

    public static void configAutoFormat(AOSigner aOSigner, byte[] bArr, Properties properties) {
        if (aOSigner.getClass().getName().equals("es.gob.afirma.signers.pades.AOPDFSigner")) {
            try {
                aOSigner.getClass().getMethod("configureRespectfulProperties", byte[].class, Properties.class).invoke(null, bArr, properties);
            } catch (Exception e) {
                LOGGER.warning("Error al configurar una firma PAdES igual a las existentes: " + e);
            }
        }
    }

    public static byte[] loadByteArrayFromExtraParams(Properties properties, String str, boolean z) throws IOException {
        byte[] bArrDecode;
        String property = properties.getProperty(str);
        if (property == null) {
            throw new IOException("La propiedad '" + str + "' no se encuentra en el extraParams");
        }
        if (!z && property.length() > 0 && property.length() < 255 && !Base64.isBase64(property)) {
            try {
                InputStream inputStreamLoadFile = AOUtil.loadFile(AOUtil.createURI(property));
                Throwable th = null;
                try {
                    try {
                        bArrDecode = AOUtil.getDataFromInputStream(inputStreamLoadFile);
                        if (inputStreamLoadFile != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamLoadFile.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                inputStreamLoadFile.close();
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (inputStreamLoadFile != null) {
                        if (th != null) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                    throw th4;
                }
            } catch (Exception e) {
                throw new IOException("El propiedad '" + str + "' no contiene una ruta valida a un recurso", e);
            }
        } else {
            bArrDecode = Base64.decode(property);
        }
        return bArrDecode;
    }
}
