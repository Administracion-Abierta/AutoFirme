package es.gob.afirma.signers.pades;

import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.pdf.AcroFields;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfPKCS7;
import com.aowagie.text.pdf.PdfReader;
import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOConfigurableContext;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.SignEnhancer;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pades.common.PdfIsPasswordProtectedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/AOPDFSigner.class */
public final class AOPDFSigner implements AOSigner, AOConfigurableContext {
    private static final String PDF_FILE_SUFFIX = ".pdf";
    private static final String PDF_FILE_HEADER = "%PDF-";
    private static final int PDF_MIN_FILE_SIZE = 70;
    private boolean secureMode = true;
    private static SignEnhancer enhancer;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final PdfName PDFNAME_ETSI_RFC3161 = new PdfName("ETSI.RFC3161");
    public static final PdfName PDFNAME_DOCTIMESTAMP = new PdfName("DocTimeStamp");
    private static Properties enhancerConfig = new Properties();

    static {
        enhancer = null;
        try {
            InputStream resourceAsStream = AOPDFSigner.class.getResourceAsStream("/enhancer.properties");
            Throwable th = null;
            try {
                enhancerConfig.load(resourceAsStream);
                String property = enhancerConfig.getProperty("enhancerClassFile");
                if (property != null) {
                    enhancer = (SignEnhancer) Class.forName(property).getConstructor(new Class[0]).newInstance(new Object[0]);
                    LOGGER.info("Se usara el siguiente mejorador de firmas: " + property);
                }
                if (resourceAsStream != null) {
                    if (0 != 0) {
                        try {
                            resourceAsStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        resourceAsStream.close();
                    }
                }
            } catch (Throwable th3) {
                if (resourceAsStream != null) {
                    if (0 != 0) {
                        try {
                            resourceAsStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        resourceAsStream.close();
                    }
                }
                throw th3;
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Se ha configurado la clase de mejora '" + ((String) null) + "', pero esta no se encuentra: " + e);
        } catch (Exception e2) {
            LOGGER.info("No hay un mejorador de firmas correctamente instalado: " + e2);
        }
        for (String str : new String[]{"SunEC", BouncyCastleProvider.PROVIDER_NAME, "SC"}) {
            Provider provider = Security.getProvider(str);
            if (provider != null) {
                provider.put("Alg.Alias.Signature.SHA224with1.2.840.10045.4.3.2", AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA);
                provider.put("Alg.Alias.Signature.SHA256with1.2.840.10045.4.3.2", AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA);
                provider.put("Alg.Alias.Signature.SHA384with1.2.840.10045.4.3.2", AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA);
                provider.put("Alg.Alias.Signature.SHA512with1.2.840.10045.4.3.2", AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA);
            }
        }
    }

    public static SignEnhancer getSignEnhancer() {
        return enhancer;
    }

    public static Properties getSignEnhancerConfig() {
        if (enhancerConfig != null) {
            return (Properties) enhancerConfig.clone();
        }
        return null;
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        String str2 = str != null ? str : "SHA512withRSA";
        Properties extraParams = getExtraParams(properties);
        checkParams(str2, extraParams);
        Certificate[] certificateArr2 = Boolean.parseBoolean(extraParams.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString())) ? new X509Certificate[]{(X509Certificate) certificateArr[0]} : certificateArr;
        GregorianCalendar signTime = PdfUtil.getSignTime(extraParams.getProperty(PdfExtraParams.SIGN_TIME));
        byte[] bArrTimestampPdf = bArr;
        if (PdfTimestamper.isAvailable()) {
            bArrTimestampPdf = PdfTimestamper.timestampPdf(bArrTimestampPdf, extraParams, signTime);
        }
        try {
            PdfSignResult pdfSignResultPreSign = PAdESTriPhaseSigner.preSign(str2, bArrTimestampPdf, certificateArr2, signTime, extraParams, this.secureMode);
            try {
                try {
                    return PAdESTriPhaseSigner.postSign(str2, bArrTimestampPdf, certificateArr2, new AOPkcs1Signer().sign(pdfSignResultPreSign.getSign(), str2, privateKey, certificateArr2, extraParams), pdfSignResultPreSign, getSignEnhancer(), getSignEnhancerConfig(), this.secureMode);
                } catch (NoSuchAlgorithmException e) {
                    throw new AOException("Error el en algoritmo de firma: " + e, e);
                }
            } catch (AOCancelledOperationException e2) {
                throw e2;
            } catch (AOException e3) {
                throw e3;
            } catch (Exception e4) {
                throw new AOException("Error al generar la firma PKCS#1 de la firma PAdES: " + e4, e4);
            }
        } catch (InvalidPdfException e5) {
            throw e5;
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return sign(bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return sign(bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No es posible realizar contrafirmas de ficheros PDF");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        String str3 = str2 != null ? str2 : "";
        if (str == null) {
            return "signed.pdf";
        }
        if (str.toLowerCase(Locale.US).endsWith(PDF_FILE_SUFFIX)) {
            return str.substring(0, str.length() - PDF_FILE_SUFFIX.length()) + str3 + PDF_FILE_SUFFIX;
        }
        return str + str3 + PDF_FILE_SUFFIX;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) {
        AOTreeNode aOTreeNode = new AOTreeNode("Datos");
        if (!isPdfFile(bArr)) {
            return new AOTreeModel(aOTreeNode);
        }
        boolean z2 = false;
        if (properties != null) {
            try {
                if (properties.containsKey("headless")) {
                    z2 = Boolean.parseBoolean(properties.getProperty("headless"));
                }
            } catch (BadPasswordException e) {
                LOGGER.info("El PDF necesita contrasena. Se devolvera el arbol vacio: " + e);
                return new AOTreeModel(aOTreeNode);
            } catch (PdfIsPasswordProtectedException e2) {
                LOGGER.info("El PDF necesita contrasena." + e2);
                return new AOTreeModel(aOTreeNode);
            } catch (Exception e3) {
                LOGGER.severe("No se ha podido leer el PDF, se devolvera un arbol vacio: " + e3);
                return new AOTreeModel(aOTreeNode);
            }
        }
        try {
            AcroFields acroFields = PdfUtil.getPdfReader(bArr, properties, z2).getAcroFields();
            for (String str : acroFields.getSignatureNames()) {
                PdfDictionary signatureDictionary = acroFields.getSignatureDictionary(str);
                if (!PDFNAME_ETSI_RFC3161.equals(signatureDictionary.get(PdfName.SUBFILTER)) && !PDFNAME_DOCTIMESTAMP.equals(signatureDictionary.get(PdfName.SUBFILTER))) {
                    try {
                        PdfPKCS7 pdfPKCS7VerifySignature = acroFields.verifySignature(str);
                        if (z) {
                            X509Certificate[] x509CertificateArr = new X509Certificate[pdfPKCS7VerifySignature.getSignCertificateChain().length];
                            for (int i = 0; i < x509CertificateArr.length; i++) {
                                x509CertificateArr[i] = (X509Certificate) pdfPKCS7VerifySignature.getSignCertificateChain()[i];
                            }
                            AOSimpleSignInfo aOSimpleSignInfo = new AOSimpleSignInfo(x509CertificateArr, pdfPKCS7VerifySignature.getSignDate() != null ? pdfPKCS7VerifySignature.getSignDate().getTime() : null);
                            byte[] pkcs1 = pdfPKCS7VerifySignature.getPkcs1();
                            if (pkcs1 != null) {
                                aOSimpleSignInfo.setPkcs1(pkcs1);
                            }
                            String digestAlgorithm = pdfPKCS7VerifySignature.getDigestAlgorithm();
                            if (digestAlgorithm != null) {
                                aOSimpleSignInfo.setSignAlgorithm(digestAlgorithm);
                            }
                            aOTreeNode.add(new AOTreeNode(aOSimpleSignInfo));
                        } else {
                            aOTreeNode.add(new AOTreeNode(AOUtil.getCN(pdfPKCS7VerifySignature.getSigningCertificate())));
                        }
                    } catch (Exception e4) {
                        LOGGER.log(Level.SEVERE, "El PDF contiene una firma corrupta o con un formato desconocido (" + str + "), se continua con las siguientes si las hubiese: " + e4, (Throwable) e4);
                    }
                }
            }
            return new AOTreeModel(aOTreeNode);
        } catch (Exception e5) {
            LOGGER.severe("No se ha podido obtener la informacion de los firmantes del PDF, se devolvera un arbol vacio: " + e5);
            return new AOTreeModel(aOTreeNode);
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        if (!isPdfFile(bArr)) {
            return false;
        }
        Object root = getSignersStructure(bArr, properties, false).getRoot();
        if (root instanceof AOTreeNode) {
            if (AOTreeModel.getChildCount(root) > 0) {
                return true;
            }
            Properties properties2 = System.getProperties();
            try {
                if (PdfUtil.pdfHasUnregisteredSignatures(bArr, properties2) && Boolean.TRUE.toString().equalsIgnoreCase(properties2.getProperty(PdfExtraParams.ALLOW_COSIGNING_UNREGISTERED_SIGNATURES))) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                LOGGER.severe("No se han podido comprobar las firmas no registradas del PDF: " + e);
                return false;
            }
        }
        return false;
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
            try {
                new PdfReader(bArr);
                return true;
            } catch (BadPasswordException e) {
                LOGGER.warning("El PDF esta protegido con contrasena, se toma como PDF valido: " + e);
                return true;
            } catch (Exception e2) {
                return false;
            }
        } catch (Exception e3) {
            LOGGER.warning("El contenido parece corrupto o truncado: " + e3);
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return isPdfFile(bArr);
    }

    public static String getSignedName(String str) {
        if (str == null) {
            return "signed.pdf";
        }
        if (str.endsWith(PDF_FILE_SUFFIX)) {
            return str.replace(PDF_FILE_SUFFIX, ".signed.pdf");
        }
        if (str.endsWith(".PDF")) {
            return str.replace(".PDF", ".signed.pdf");
        }
        return str + ".signed.pdf";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws AOInvalidFormatException {
        if (!isSign(bArr, properties)) {
            throw new AOInvalidFormatException("El documento introducido no contiene una firma valida");
        }
        return bArr;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws AOInvalidFormatException {
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("El documento introducido no contiene una firma valida");
        }
        return bArr;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr, properties)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return new AOSignInfo(AOSignConstants.SIGN_FORMAT_PDF);
    }

    public static void configureRespectfulProperties(byte[] bArr, Properties properties) {
        if (properties != null && !properties.containsKey(PdfExtraParams.SIGNATURE_SUBFILTER)) {
            try {
                String firstSupportedSignSubFilter = PdfUtil.getFirstSupportedSignSubFilter(bArr, properties);
                if (firstSupportedSignSubFilter != null) {
                    properties.setProperty(PdfExtraParams.SIGNATURE_SUBFILTER, firstSupportedSignSubFilter.substring(firstSupportedSignSubFilter.indexOf(47) + 1));
                }
            } catch (Exception e) {
                LOGGER.warning("Error al configurar la firma PDF para que sea igual a las existentes: " + e);
            }
        }
    }

    private static Properties getExtraParams(Properties properties) {
        return properties != null ? (Properties) properties.clone() : new Properties();
    }

    private static void checkParams(String str, Properties properties) {
        if (str.toUpperCase(Locale.US).startsWith("MD")) {
            throw new IllegalArgumentException("PAdES no permite huellas digitales MD2 o MD5 (Decision 130/2011 CE)");
        }
        String property = properties.getProperty("profile");
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(property)) {
            if (AOSignConstants.isSHA1SignatureAlgorithm(str)) {
                LOGGER.warning("El algoritmo '" + str + "' no esta recomendado para su uso en las firmas baseline");
            }
            if (properties.containsKey(PdfExtraParams.SIGNATURE_SUBFILTER)) {
                LOGGER.warning("Se ignorara el valor establecido en el parametro 'signatureSubFilter' ya que en las firmas baseline el subfiltro siempre sera ETSI.CAdES.detached");
                properties.remove(PdfExtraParams.SIGNATURE_SUBFILTER);
            }
        }
        if (properties.containsKey("commitmentTypeIndications") && !AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(property) && !properties.containsKey("policyIdentifier")) {
            LOGGER.warning("Se ignoraran los commitment type indications establecidos por no estar permitidos en las firmas PAdES-EPES");
            properties.remove("commitmentTypeIndications");
        }
        if (properties.containsKey(PdfExtraParams.SIGN_REASON) && properties.containsKey("policyIdentifier")) {
            LOGGER.warning("Se ignorara la razon de firma establecida por haberse indicado una politica de firma");
            properties.remove(PdfExtraParams.SIGN_REASON);
        }
        if (properties.containsKey(PdfExtraParams.SIGN_REASON) && properties.containsKey("commitmentTypeIndications")) {
            LOGGER.warning("Se ignoraran los commitment type indications establecidos por haberse indicado una razon de firma");
            properties.remove("commitmentTypeIndications");
        }
    }

    @Override // es.gob.afirma.core.signers.AOConfigurableContext
    public void setSecureMode(boolean z) {
        this.secureMode = z;
    }

    @Override // es.gob.afirma.core.signers.AOConfigurableContext
    public boolean isSecureMode() {
        return this.secureMode;
    }
}
