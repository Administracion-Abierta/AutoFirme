package es.gob.afirma.signers.pades;

import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfSignatureAppearance;
import com.aowagie.text.pdf.PdfString;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.SignEnhancer;
import es.gob.afirma.signers.cades.CAdESParameters;
import es.gob.afirma.signers.cades.CAdESTriPhaseSigner;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.GregorianCalendar;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PAdESTriPhaseSigner.class */
public final class PAdESTriPhaseSigner {
    public static final int LAST_PAGE = -1;

    private PAdESTriPhaseSigner() {
    }

    public static PdfSignResult preSign(String str, byte[] bArr, Certificate[] certificateArr, GregorianCalendar gregorianCalendar, Properties properties, boolean z) throws IOException, AOException {
        Properties properties2 = properties != null ? properties : new Properties();
        PdfTriPhaseSession sessionData = PdfSessionManager.getSessionData(bArr, certificateArr, gregorianCalendar, properties2, z);
        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(sessionData.getSAP().getRangeStream());
        CAdESParameters cAdESParametersLoad = CAdESParameters.load(null, str, properties2);
        try {
            cAdESParametersLoad.setDataDigest(MessageDigest.getInstance(cAdESParametersLoad.getDigestAlgorithm()).digest(dataFromInputStream));
            cAdESParametersLoad.setMetadata(null);
            cAdESParametersLoad.setContentTypeOid(null);
            cAdESParametersLoad.setContentDescription(null);
            cAdESParametersLoad.setSigningTime(null);
            return new PdfSignResult(sessionData.getFileID(), CAdESTriPhaseSigner.preSign(certificateArr, gregorianCalendar.getTime(), cAdESParametersLoad), null, gregorianCalendar, properties2);
        } catch (NoSuchAlgorithmException e) {
            throw new AOException("El algoritmo de huella digital no es valido: " + e, e);
        }
    }

    public static byte[] postSign(String str, byte[] bArr, Certificate[] certificateArr, byte[] bArr2, PdfSignResult pdfSignResult, SignEnhancer signEnhancer, Properties properties, boolean z) throws NoSuchAlgorithmException, IOException, AOException {
        return insertSignatureOnPdf(bArr, certificateArr, generatePdfSignature(str, certificateArr, pdfSignResult.getExtraParams(), bArr2, pdfSignResult.getSign(), pdfSignResult.getFileID(), pdfSignResult.getTimestamp(), pdfSignResult.getSignTime(), signEnhancer, properties), z);
    }

    private static PdfSignResult generatePdfSignature(String str, Certificate[] certificateArr, Properties properties, byte[] bArr, byte[] bArr2, String str2, byte[] bArr3, GregorianCalendar gregorianCalendar, SignEnhancer signEnhancer, Properties properties2) throws IOException, AOException {
        byte[] bArrPostSign = CAdESTriPhaseSigner.postSign(str, null, certificateArr, bArr, bArr2);
        Properties properties3 = properties != null ? properties : new Properties();
        if (PdfTimestamper.isAvailable() && !"2".equals(properties3.getProperty(PdfExtraParams.TS_TYPE)) && properties3.getProperty("tsaURL") != null) {
            bArrPostSign = PdfTimestamper.addCmsTimeStamp(bArrPostSign, properties3, gregorianCalendar);
        }
        if (signEnhancer != null) {
            bArrPostSign = signEnhancer.enhance(bArrPostSign, properties2 != null ? properties2 : properties3);
        }
        return new PdfSignResult(str2, bArrPostSign, bArr3, gregorianCalendar, properties != null ? properties : new Properties());
    }

    private static byte[] insertSignatureOnPdf(byte[] bArr, Certificate[] certificateArr, PdfSignResult pdfSignResult, boolean z) throws IOException, AOException {
        int reservedSignatureSized = PdfSessionManager.getReservedSignatureSized(pdfSignResult.getExtraParams());
        byte[] bArr2 = new byte[reservedSignatureSized];
        if (pdfSignResult.getSign().length > reservedSignatureSized) {
            throw new AOException("El tamano de la firma (" + pdfSignResult.getSign().length + ") supera el maximo permitido para un PDF (" + reservedSignatureSized + ")");
        }
        PdfDictionary pdfDictionary = new PdfDictionary();
        System.arraycopy(pdfSignResult.getSign(), 0, bArr2, 0, pdfSignResult.getSign().length);
        pdfDictionary.put(PdfName.CONTENTS, new PdfString(bArr2).setHexWriting(true));
        try {
            PdfTriPhaseSession sessionData = PdfSessionManager.getSessionData(bArr, certificateArr, pdfSignResult.getSignTime(), pdfSignResult.getExtraParams(), z);
            PdfSignatureAppearance sap = sessionData.getSAP();
            ByteArrayOutputStream baos = sessionData.getBAOS();
            Throwable th = null;
            try {
                String fileID = sessionData.getFileID();
                try {
                    sap.close(pdfDictionary);
                    byte[] bytes = new String(baos.toByteArray(), StandardCharsets.ISO_8859_1).replace(fileID, pdfSignResult.getFileID()).getBytes(StandardCharsets.ISO_8859_1);
                    if (baos != null) {
                        if (0 != 0) {
                            try {
                                baos.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            baos.close();
                        }
                    }
                    return bytes;
                } catch (Exception e) {
                    baos.close();
                    throw new AOException("Error al cerrar el PDF para finalizar el proceso de firma", e);
                }
            } catch (Throwable th3) {
                if (baos != null) {
                    if (0 != 0) {
                        try {
                            baos.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        baos.close();
                    }
                }
                throw th3;
            }
        } catch (InvalidPdfException e2) {
            throw new IOException(e2);
        }
    }
}
