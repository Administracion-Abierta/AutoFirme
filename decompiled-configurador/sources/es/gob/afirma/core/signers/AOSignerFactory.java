package es.gob.afirma.core.signers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOSignerFactory.class */
public final class AOSignerFactory {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Map<String, AOSigner> SIGNERS = new HashMap(20);
    private static final String SIGNER_CLASS_CADES = "es.gob.afirma.signers.cades.AOCAdESSigner";
    private static final String SIGNER_CLASS_CADES_TRI = "es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner";
    private static final String SIGNER_CLASS_CADES_ASIC_S = "es.gob.afirma.signers.cades.asic.AOCAdESASiCSSigner";
    private static final String SIGNER_CLASS_CADES_ASIC_S_TRI = "es.gob.afirma.signers.cadestri.client.asic.AOCAdESASiCSTriPhaseSigner";
    private static final String SIGNER_CLASS_CMS = "es.gob.afirma.signers.cms.AOCMSSigner";
    private static final String SIGNER_CLASS_FACTURAE = "es.gob.afirma.signers.xades.AOFacturaESigner";
    private static final String SIGNER_CLASS_FACTURAE_TRI = "es.gob.afirma.signers.xadestri.client.AOFacturaETriPhaseSigner";
    private static final String SIGNER_CLASS_XADES = "es.gob.afirma.signers.xades.AOXAdESSigner";
    private static final String SIGNER_CLASS_XADES_TRI = "es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner";
    private static final String SIGNER_CLASS_XADES_ASIC_S = "es.gob.afirma.signers.xades.asic.AOXAdESASiCSSigner";
    private static final String SIGNER_CLASS_XADES_ASIC_S_tri = "es.gob.afirma.signers.xadestri.client.asic.AOXAdESASiCSTriPhaseSigner";
    private static final String SIGNER_CLASS_XMLDSIG = "es.gob.afirma.signers.xmldsig.AOXMLDSigSigner";
    private static final String SIGNER_CLASS_PADES = "es.gob.afirma.signers.pades.AOPDFSigner";
    private static final String SIGNER_CLASS_PADES_TRI = "es.gob.afirma.signers.padestri.client.AOPDFTriPhaseSigner";
    private static final String SIGNER_CLASS_ODF = "es.gob.afirma.signers.odf.AOODFSigner";
    private static final String SIGNER_CLASS_OOXML = "es.gob.afirma.signers.ooxml.AOOOXMLSigner";
    private static final String SIGNER_CLASS_PKCS1 = "es.gob.afirma.core.signers.AOPkcs1Signer";
    private static final String SIGNER_CLASS_PKCS1_TRI = "es.gob.afirma.core.signers.AOPkcs1TriPhaseSigner";
    private static final String[][] SIGNERS_CLASSES = {new String[]{"CAdES", SIGNER_CLASS_CADES, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_CADES_TRI, SIGNER_CLASS_CADES_TRI, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_CADES_ASIC_S, SIGNER_CLASS_CADES_ASIC_S, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_CADES_ASIC_S_TRI, SIGNER_CLASS_CADES_ASIC_S_TRI, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_CMS, SIGNER_CLASS_CMS, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_FACTURAE, SIGNER_CLASS_FACTURAE, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_FACTURAE_ALT1, SIGNER_CLASS_FACTURAE, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_FACTURAE_TRI, SIGNER_CLASS_FACTURAE_TRI, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES, SIGNER_CLASS_XADES, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES_DETACHED, SIGNER_CLASS_XADES, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED, SIGNER_CLASS_XADES, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING, SIGNER_CLASS_XADES, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES_TRI, SIGNER_CLASS_XADES_TRI, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES_ASIC_S, SIGNER_CLASS_XADES_ASIC_S, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XADES_ASIC_S_TRI, SIGNER_CLASS_XADES_ASIC_S_tri, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XMLDSIG, SIGNER_CLASS_XMLDSIG, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XMLDSIG_DETACHED, SIGNER_CLASS_XMLDSIG, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED, SIGNER_CLASS_XMLDSIG, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING, SIGNER_CLASS_XMLDSIG, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_PDF, SIGNER_CLASS_PADES, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_PDF_TRI, SIGNER_CLASS_PADES_TRI, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_PADES, SIGNER_CLASS_PADES, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_PADES_TRI, SIGNER_CLASS_PADES_TRI, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_ODF, SIGNER_CLASS_ODF, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_ODF_ALT1, SIGNER_CLASS_ODF, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_OOXML, SIGNER_CLASS_OOXML, Boolean.TRUE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_OOXML_ALT1, SIGNER_CLASS_OOXML, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_PKCS1, SIGNER_CLASS_PKCS1, Boolean.FALSE.toString()}, new String[]{AOSignConstants.SIGN_FORMAT_PKCS1_TRI, SIGNER_CLASS_PKCS1_TRI, Boolean.FALSE.toString()}};

    private AOSignerFactory() {
    }

    public static AOSigner getSigner(byte[] bArr) throws IOException {
        AOSigner aOSigner;
        if (bArr == null) {
            throw new IllegalArgumentException("No se han indicado datos de firma");
        }
        for (String[] strArr : SIGNERS_CLASSES) {
            if (Boolean.parseBoolean(strArr[2])) {
                if (SIGNERS.get(strArr[0]) == null) {
                    try {
                        SIGNERS.put(strArr[0], (AOSigner) Class.forName(strArr[1]).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
                        aOSigner = SIGNERS.get(strArr[0]);
                        if (aOSigner != null && aOSigner.isSign(bArr)) {
                            return aOSigner;
                        }
                    } catch (Exception e) {
                        LOGGER.warning("No se ha podido instanciar un manejador para el formato de firma '" + strArr[0] + "': " + e);
                    }
                } else {
                    aOSigner = SIGNERS.get(strArr[0]);
                    if (aOSigner != null) {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public static AOSigner getSigner(byte[] bArr, Properties properties) throws IOException {
        AOSigner aOSigner;
        if (bArr == null) {
            throw new IllegalArgumentException("No se han indicado datos de firma");
        }
        for (String[] strArr : SIGNERS_CLASSES) {
            if (Boolean.parseBoolean(strArr[2])) {
                if (SIGNERS.get(strArr[0]) == null) {
                    try {
                        SIGNERS.put(strArr[0], (AOSigner) Class.forName(strArr[1]).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
                        aOSigner = SIGNERS.get(strArr[0]);
                        if (aOSigner != null && aOSigner.isSign(bArr, properties)) {
                            return aOSigner;
                        }
                    } catch (Exception e) {
                        LOGGER.warning("No se ha podido instanciar un manejador para el formato de firma '" + strArr[0] + "': " + e);
                    }
                } else {
                    aOSigner = SIGNERS.get(strArr[0]);
                    if (aOSigner != null) {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public static AOSigner getSigner(String str) {
        String str2 = null;
        for (String[] strArr : SIGNERS_CLASSES) {
            if (strArr[0].equalsIgnoreCase(str)) {
                str2 = strArr[1];
                break;
            }
        }
        if (str2 == null) {
            LOGGER.warning("El formato de firma '" + str + "' no esta soportado, se devolvera null");
            return null;
        }
        if (SIGNERS.get(str) == null) {
            try {
                SIGNERS.put(str, (AOSigner) Class.forName(str2).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (Exception e) {
                LOGGER.severe("No se ha podido instanciar un manejador para el formato de firma '" + str + "', se devolvera null: " + e);
            }
        }
        return SIGNERS.get(str);
    }

    public static String[] getSupportedFormats() {
        int i = 0;
        String[] strArr = new String[SIGNERS_CLASSES.length];
        for (String[] strArr2 : SIGNERS_CLASSES) {
            int i2 = i;
            i++;
            strArr[i2] = strArr2[0];
        }
        return strArr;
    }

    public static String getSignFormat(AOSigner aOSigner) {
        String name = aOSigner.getClass().getName();
        for (String[] strArr : SIGNERS_CLASSES) {
            if (name.equals(strArr[1])) {
                return strArr[0];
            }
        }
        return null;
    }
}
