package es.gob.afirma.signers.padestri.client;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOTriphaseException;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.pades.common.BadPdfPasswordException;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pades.common.PdfFormModifiedException;
import es.gob.afirma.signers.pades.common.PdfHasUnregisteredSignaturesException;
import es.gob.afirma.signers.pades.common.PdfIsCertifiedException;
import es.gob.afirma.signers.pades.common.PdfIsPasswordProtectedException;
import es.gob.afirma.signers.pades.common.SuspectedPSAException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/padestri/client/AOPDFTriPhaseSigner.class */
public final class AOPDFTriPhaseSigner implements AOSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String PROPERTY_NAME_SIGN_SERVER_URL = "serverUrl";
    private static final String PDF_FILE_HEADER = "%PDF-";
    private static final String PDF_FILE_SUFFIX = ".pdf";
    private static final String ERROR_PREFIX = "ERR-";
    private static final String CONFIG_NEEDED_ERROR_PREFIX = "ERR-21:";
    private static final int PDF_MIN_FILE_SIZE = 70;

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        if (properties == null) {
            throw new IllegalArgumentException("Se necesitan parametros adicionales");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("Es necesario proporcionar la clave privada de firma");
        }
        if (certificateArr == null || certificateArr.length == 0) {
            throw new IllegalArgumentException("Es necesario proporcionar el certificado de firma");
        }
        if (bArr == null) {
            throw new IllegalArgumentException("No se ha proporcionado el identificador de documento a firmar");
        }
        Properties extraParams = getExtraParams(properties);
        try {
            URL url = new URL(extraParams.getProperty(PROPERTY_NAME_SIGN_SERVER_URL));
            String strEncode = Base64.encode(bArr, true);
            byte[] bArrDoPresign = PDFTriPhaseSignerUtil.doPresign(url, str, certificateArr, strEncode, extraParams);
            if (bArrDoPresign.length > 8 && new String(Arrays.copyOf(bArrDoPresign, 8), StandardCharsets.UTF_8).startsWith(ERROR_PREFIX)) {
                String str2 = new String(bArrDoPresign, StandardCharsets.UTF_8);
                LOGGER.warning("Error durante la prefirma: " + str2);
                throw buildInternalException(str2, extraParams);
            }
            byte[] bArrDoPostSign = PDFTriPhaseSignerUtil.doPostSign(Base64.encode(PDFTriPhaseSignerUtil.doSign(bArrDoPresign, str, privateKey, certificateArr, extraParams), true), url, str, certificateArr, strEncode, extraParams);
            if (bArrDoPostSign.length > 8 && new String(Arrays.copyOf(bArrDoPostSign, 8), StandardCharsets.UTF_8).startsWith(CONFIG_NEEDED_ERROR_PREFIX)) {
                String str3 = new String(bArrDoPostSign, StandardCharsets.UTF_8);
                LOGGER.warning("Error durante la postfirma: " + str3);
                throw buildInternalException(str3, extraParams);
            }
            return bArrDoPostSign;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se ha proporcionado una URL valida para el servidor de firma: " + extraParams.getProperty(PROPERTY_NAME_SIGN_SERVER_URL), e);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return sign(bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return sign(bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        throw new UnsupportedOperationException("No se soportan contrafirmas en PAdES");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        throw new UnsupportedOperationException("No soportado para firmas trifasicas");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        throw new UnsupportedOperationException("No soportado para firmas trifasicas");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        return false;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return isPdfFile(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        String str3 = str2 != null ? str2 : "";
        if (str == null) {
            return "signed.pdf";
        }
        if (str.toLowerCase(Locale.ENGLISH).endsWith(PDF_FILE_SUFFIX)) {
            return str.substring(0, str.length() - PDF_FILE_SUFFIX.length()) + str3 + PDF_FILE_SUFFIX;
        }
        return str + str3 + PDF_FILE_SUFFIX;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOException {
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("El documento introducido no contiene una firma valida");
        }
        return bArr;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return new AOSignInfo(AOSignConstants.SIGN_FORMAT_PDF);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }

    private static boolean isPdfFile(byte[] bArr) {
        if (bArr == null || bArr.length < 70) {
            return false;
        }
        byte[] bArr2 = new byte[PDF_FILE_HEADER.length()];
        try {
            new ByteArrayInputStream(bArr).read(bArr2);
            if (!PDF_FILE_HEADER.equals(new String(bArr2))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.warning("El contenido parece corrupto o truncado: " + e);
            return false;
        }
    }

    private static AOException buildInternalException(String str, Properties properties) {
        AOException aOException = null;
        int iIndexOf = str.indexOf(":");
        if (str.startsWith(CONFIG_NEEDED_ERROR_PREFIX)) {
            int iIndexOf2 = str.indexOf(":", iIndexOf + 1);
            String strSubstring = str.substring(iIndexOf + 1, iIndexOf2);
            String strSubstring2 = str.substring(iIndexOf2 + 1);
            if (PdfIsCertifiedException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = new PdfIsCertifiedException(strSubstring2);
            } else if (PdfHasUnregisteredSignaturesException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = new PdfHasUnregisteredSignaturesException(strSubstring2);
            } else if (PdfFormModifiedException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = new PdfFormModifiedException(strSubstring2);
            } else if (SuspectedPSAException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = new SuspectedPSAException(strSubstring2);
            } else if (PdfIsPasswordProtectedException.REQUESTOR_MSG_CODE.equals(strSubstring) || BadPdfPasswordException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = (properties == null || !(properties.containsKey(PdfExtraParams.OWNER_PASSWORD_STRING) || properties.containsKey(PdfExtraParams.USER_PASSWORD_STRING))) ? new PdfIsPasswordProtectedException(strSubstring2) : new BadPdfPasswordException(strSubstring2);
            }
        }
        if (aOException == null) {
            int iIndexOf3 = str.indexOf(":", iIndexOf + 1);
            if (iIndexOf3 > 0) {
                aOException = AOTriphaseException.parseException(str.substring(iIndexOf3 + 1).trim());
            } else {
                aOException = new AOException(str);
            }
        }
        return aOException;
    }

    private static Properties getExtraParams(Properties properties) {
        return (Properties) properties.clone();
    }
}
