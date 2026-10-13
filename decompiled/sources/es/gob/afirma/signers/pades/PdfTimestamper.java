package es.gob.afirma.signers.pades;

import com.aowagie.text.DocumentException;
import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.pdf.PdfDate;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfSignature;
import com.aowagie.text.pdf.PdfSignatureAppearance;
import com.aowagie.text.pdf.PdfStamper;
import com.aowagie.text.pdf.PdfString;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pades.common.PdfIsPasswordProtectedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfTimestamper.class */
public final class PdfTimestamper {
    private static final String TIMESTAMP_SUBFILTER = "ETSI.RFC3161";
    private static final int CSIZE = 27000;
    private static final int UNDEFINED = -1;
    public static final String TS_LEVEL_SIGN = "1";
    public static final String TS_LEVEL_DOC = "2";
    public static final String TS_LEVEL_SIGN_DOC = "3";
    private static final int PDF_MAX_VERSION = 7;
    private static final int PDF_MIN_VERSION = 2;
    private static final int PDF_MIN_COMPRESABLE_VERSION = 5;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static Class<?> cmsTimestamperClass = null;
    private static Class<?> tsaParamsClass = null;
    private static boolean initialized = false;

    private PdfTimestamper() {
    }

    private static void initialize() {
        if (initialized) {
            return;
        }
        try {
            cmsTimestamperClass = Class.forName("es.gob.afirma.signers.tsp.pkcs7.CMSTimestamper", false, PdfTimestamper.class.getClassLoader());
            tsaParamsClass = Class.forName("es.gob.afirma.signers.tsp.pkcs7.TsaParams");
        } catch (Throwable th) {
            LOGGER.info("No se encuentran las clases de sellado de tiempo CAdES: " + th);
            cmsTimestamperClass = null;
            tsaParamsClass = null;
        }
        initialized = true;
    }

    public static byte[] timestampPdf(byte[] bArr, Properties properties, Calendar calendar) throws IOException, AOException {
        int i;
        char c;
        if (!isAvailable()) {
            return bArr;
        }
        if (properties != null) {
            String property = properties.getProperty("tsaURL");
            String property2 = properties.getProperty(PdfExtraParams.TS_TYPE);
            if (property != null && ("2".equals(property2) || "3".equals(property2))) {
                PdfReader pdfReader = PdfUtil.getPdfReader(bArr, properties, Boolean.parseBoolean(properties.getProperty("headless")));
                PdfUtil.checkPdfCertification(pdfReader.getCertificationLevel(), properties);
                try {
                    i = properties.getProperty(PdfExtraParams.PDF_VERSION) != null ? Integer.parseInt(properties.getProperty(PdfExtraParams.PDF_VERSION).trim()) : 7;
                } catch (Exception e) {
                    LOGGER.warning("Error en el establecimiento de la version PDF, se usara 7: " + e);
                    i = 7;
                }
                if (i != -1 && (i < 2 || i > 7)) {
                    LOGGER.warning("Se ha establecido un valor invalido para version, se ignorara: " + i);
                    i = -1;
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Throwable th = null;
                try {
                    if (i == -1) {
                        c = 0;
                    } else {
                        try {
                            c = Integer.toString(i).toCharArray()[0];
                        } catch (DocumentException e2) {
                            throw new AOException("Error de formato en el PDF de entrada: " + e2, e2);
                        } catch (BadPasswordException e3) {
                            throw new PdfIsPasswordProtectedException(e3);
                        }
                    }
                    PdfStamper pdfStamperCreateSignature = PdfStamper.createSignature(pdfReader, byteArrayOutputStream, c, null, PdfUtil.getAppendMode(properties, pdfReader), calendar);
                    PdfSignatureAppearance signatureAppearance = pdfStamperCreateSignature.getSignatureAppearance();
                    if (i >= 5 && !"false".equalsIgnoreCase(properties.getProperty(PdfExtraParams.COMPRESS_PDF))) {
                        pdfStamperCreateSignature.setFullCompression();
                    }
                    PdfUtil.enableLtv(pdfStamperCreateSignature);
                    signatureAppearance.setAcro6Layers(true);
                    signatureAppearance.setRender(0);
                    signatureAppearance.setSignDate(calendar);
                    PdfSignature pdfSignature = new PdfSignature(new PdfName("DocTimeStamp"), PdfName.ADOBE_PPKLITE, new PdfName(TIMESTAMP_SUBFILTER));
                    pdfSignature.setDate(new PdfDate(calendar));
                    signatureAppearance.setCryptoDictionary(pdfSignature);
                    try {
                        signatureAppearance.preClose(PdfSessionManager.reserveSignSizes(properties), calendar, null);
                        InputStream rangeStream = signatureAppearance.getRangeStream();
                        Throwable th2 = null;
                        try {
                            try {
                                byte[] dataFromInputStream = AOUtil.getDataFromInputStream(rangeStream);
                                if (rangeStream != null) {
                                    if (0 != 0) {
                                        try {
                                            rangeStream.close();
                                        } catch (Throwable th3) {
                                            th2.addSuppressed(th3);
                                        }
                                    } else {
                                        rangeStream.close();
                                    }
                                }
                                byte[] tspToken = getTspToken(properties, dataFromInputStream, calendar);
                                byte[] bArr2 = new byte[CSIZE];
                                if (tspToken.length > CSIZE) {
                                    throw new AOException("El tamano del sello de tiempo (" + tspToken.length + ") supera el maximo permitido para un PDF (" + CSIZE + ")");
                                }
                                PdfDictionary pdfDictionary = new PdfDictionary();
                                System.arraycopy(tspToken, 0, bArr2, 0, tspToken.length);
                                pdfDictionary.put(PdfName.CONTENTS, new PdfString(bArr2).setHexWriting(true));
                                try {
                                    signatureAppearance.close(pdfDictionary);
                                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                                    if (byteArrayOutputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                byteArrayOutputStream.close();
                                            } catch (Throwable th4) {
                                                th.addSuppressed(th4);
                                            }
                                        } else {
                                            byteArrayOutputStream.close();
                                        }
                                    }
                                    return byteArray;
                                } catch (Exception e4) {
                                    byteArrayOutputStream.close();
                                    throw new AOException("Error al cerrar el PDF para finalizar el proceso de firma", e4);
                                }
                            } catch (Throwable th5) {
                                th2 = th5;
                                throw th5;
                            }
                        } catch (Throwable th6) {
                            if (rangeStream != null) {
                                if (th2 != null) {
                                    try {
                                        rangeStream.close();
                                    } catch (Throwable th7) {
                                        th2.addSuppressed(th7);
                                    }
                                } else {
                                    rangeStream.close();
                                }
                            }
                            throw th6;
                        }
                    } catch (DocumentException e5) {
                        throw new AOException("Error en el procesado del PDF: " + e5, e5);
                    }
                } catch (Throwable th8) {
                    if (byteArrayOutputStream != null) {
                        if (0 != 0) {
                            try {
                                byteArrayOutputStream.close();
                            } catch (Throwable th9) {
                                th.addSuppressed(th9);
                            }
                        } else {
                            byteArrayOutputStream.close();
                        }
                    }
                    throw th8;
                }
            }
        }
        return bArr;
    }

    private static byte[] getTspToken(Properties properties, byte[] bArr, Calendar calendar) throws AOException {
        try {
            Object objBuildTsaParams = buildTsaParams(properties);
            Object objBuildCmsTimeStamper = buildCmsTimeStamper(objBuildTsaParams);
            String hashAlgorithm = getHashAlgorithm(objBuildTsaParams);
            return (byte[]) cmsTimestamperClass.getMethod("getTimeStampToken", byte[].class, String.class, Calendar.class).invoke(objBuildCmsTimeStamper, MessageDigest.getInstance(hashAlgorithm).digest(bArr), hashAlgorithm, calendar);
        } catch (InvocationTargetException e) {
            throw new AOException("Error al generar los sellos de tiempo", e);
        } catch (Exception e2) {
            throw new AOException("No se ha podido generar el sello de tiempo", e2);
        }
    }

    public static boolean isAvailable() {
        initialize();
        return cmsTimestamperClass != null;
    }

    public static byte[] addCmsTimeStamp(byte[] bArr, Properties properties, Calendar calendar) {
        Object objBuildTsaParams;
        try {
            objBuildTsaParams = buildTsaParams(properties);
        } catch (Exception e) {
            LOGGER.warning("Se ha pedido aplicar sello de tiempo, pero falta informacion necesaria: " + e);
            objBuildTsaParams = null;
        }
        byte[] bArr2 = bArr;
        if (objBuildTsaParams != null) {
            try {
                bArr2 = (byte[]) cmsTimestamperClass.getMethod("addTimestamp", byte[].class, String.class, Calendar.class).invoke(buildCmsTimeStamper(objBuildTsaParams), bArr, getHashAlgorithm(objBuildTsaParams), calendar);
            } catch (Exception e2) {
                LOGGER.warning("No se ha podido actualizar la firma: " + e2);
                bArr2 = bArr;
            }
        }
        return bArr2;
    }

    private static Object buildTsaParams(Properties properties) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, IllegalArgumentException, InvocationTargetException {
        return tsaParamsClass.getConstructor(Properties.class).newInstance(properties);
    }

    private static String getHashAlgorithm(Object obj) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        return (String) tsaParamsClass.getMethod("getTsaHashAlgorithm", new Class[0]).invoke(obj, new Object[0]);
    }

    private static Object buildCmsTimeStamper(Object obj) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, IllegalArgumentException, InvocationTargetException {
        return cmsTimestamperClass.getConstructor(tsaParamsClass.getClass()).newInstance(obj);
    }
}
