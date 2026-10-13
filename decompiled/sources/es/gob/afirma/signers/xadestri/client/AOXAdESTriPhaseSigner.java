package es.gob.afirma.signers.xadestri.client;

import es.gob.afirma.core.AGEPolicyIncompatibilityException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOTriphaseException;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.OptionalDataInterface;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.core.signers.TriphaseDataSigner;
import es.gob.afirma.core.signers.TriphaseUtil;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xadestri/client/AOXAdESTriPhaseSigner.class */
public class AOXAdESTriPhaseSigner implements AOSigner, OptionalDataInterface {
    protected static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final String DSIGNNS = "http://www.w3.org/2000/09/xmldsig#";
    protected static final String XML_SIGNATURE_PREFIX = "ds";
    public static final String SIGNATURE_TAG = "Signature";
    private static final String PROPERTY_NAME_SIGN_SERVER_URL = "serverUrl";
    private static final String PROPERTY_VALIDATE_PKCS1 = "validatePkcs1";
    private static final String OPERATION_PRESIGN = "pre";
    private static final String OPERATION_POSTSIGN = "post";
    private static final String CRYPTO_OPERATION_SIGN = "sign";
    private static final String CRYPTO_OPERATION_COSIGN = "cosign";
    private static final String CRYPTO_OPERATION_COUNTERSIGN = "countersign";
    private static final String PARAMETER_NAME_OPERATION = "op";
    private static final String PARAMETER_NAME_CRYPTO_OPERATION = "cop";
    private static final String HTTP_CGI = "?";
    private static final String HTTP_EQUALS = "=";
    private static final String HTTP_AND = "&";
    private static final String PARAMETER_NAME_DOCID = "doc";
    private static final String PARAMETER_NAME_ALGORITHM = "algo";
    private static final String PARAMETER_NAME_FORMAT = "format";
    private static final String PARAMETER_NAME_CERT = "cert";
    private static final String PARAMETER_NAME_EXTRA_PARAM = "params";
    private static final String PARAMETER_NAME_SESSION_DATA = "session";
    private static final String ERROR_PREFIX = "ERR-";
    private static final String CONFIG_NEEDED_ERROR_PREFIX = "ERR-21:";
    private static final String SUCCESS = "OK";
    private static final String COUNTERSIGN_TARGET_KEY = "target";
    public static final String COUNTERSIGN_TARGET_LEAFS = "leafs";
    public static final String COUNTERSIGN_TARGET_TREE = "tree";
    private static final String EXTRAPARAM_FORMAT = "format";
    private static final String EXTRAPARAM_USE_MANIFEST = "useManifest";
    private final String signFormat;

    protected AOXAdESTriPhaseSigner(String str) {
        this.signFormat = str;
    }

    public AOXAdESTriPhaseSigner() {
        this(AOSignConstants.SIGN_FORMAT_XADES);
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation(this.signFormat, "sign", bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOException {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public final byte[] getData(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation(this.signFormat, "cosign", bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return cosign(null, bArr, str, privateKey, certificateArr, properties);
    }

    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        if (counterSignTarget == null) {
            throw new IllegalArgumentException("No se han indicado los nodos objetivo de la contrafirma");
        }
        if (counterSignTarget != CounterSignTarget.TREE && counterSignTarget != CounterSignTarget.LEAFS) {
            throw new IllegalArgumentException("El objetivo indicado para la contrafirma no esta soportado: " + counterSignTarget);
        }
        Properties properties2 = properties != null ? properties : new Properties();
        properties2.setProperty(COUNTERSIGN_TARGET_KEY, counterSignTarget.toString());
        return triPhaseOperation(this.signFormat, "countersign", bArr, str, privateKey, certificateArr, properties2);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public final AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
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
        try {
            Element documentElement = SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
            ArrayList arrayList = new ArrayList();
            if ("Signature".equals(documentElement.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(documentElement.getNamespaceURI())) {
                arrayList.add(documentElement);
            }
            NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                arrayList.add(elementsByTagNameNS.item(i));
            }
            if (arrayList.size() == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return true;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".xsig";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    protected static byte[] triPhaseOperation(String str, String str2, byte[] bArr, String str3, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        if (properties == null) {
            throw new IllegalArgumentException("Se necesitan parametros adicionales");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("Es necesario proporcionar la clave privada de firma");
        }
        if (certificateArr == null || certificateArr.length == 0) {
            throw new IllegalArgumentException("Es necesario proporcionar el certificado de firma");
        }
        if (bArr == null && isDataMandatory(str2, properties)) {
            throw new IllegalArgumentException("No se ha proporcionado el identificador de documento a firmar");
        }
        try {
            URL url = new URL(properties.getProperty(PROPERTY_NAME_SIGN_SERVER_URL));
            Properties properties2 = (Properties) properties.clone();
            properties2.remove("validatePkcs1");
            properties2.remove(PROPERTY_NAME_SIGN_SERVER_URL);
            String strEncode = bArr != null ? Base64.encode(bArr, true) : null;
            UrlHttpManager installedManager = UrlHttpManagerFactory.getInstalledManager();
            try {
                String strPrepareCertChainParam = TriphaseUtil.prepareCertChainParam(certificateArr, properties2);
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(url).append("?").append("op").append(HTTP_EQUALS).append("pre").append(HTTP_AND).append(PARAMETER_NAME_CRYPTO_OPERATION).append(HTTP_EQUALS).append(str2).append(HTTP_AND).append(XAdESExtraParams.FORMAT).append(HTTP_EQUALS).append(str).append(HTTP_AND).append(PARAMETER_NAME_ALGORITHM).append(HTTP_EQUALS).append(str3).append(HTTP_AND).append(PARAMETER_NAME_CERT).append(HTTP_EQUALS).append(strPrepareCertChainParam);
                    if (strEncode != null) {
                        stringBuffer.append(HTTP_AND).append(PARAMETER_NAME_DOCID).append(HTTP_EQUALS).append(strEncode);
                    }
                    if (properties2.size() > 0) {
                        stringBuffer.append(HTTP_AND).append(PARAMETER_NAME_EXTRA_PARAM).append(HTTP_EQUALS).append(AOUtil.properties2Base64(properties2));
                    }
                    String string = stringBuffer.toString();
                    SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
                    try {
                        byte[] url2 = installedManager.readUrl(string, UrlHttpMethod.POST, sSLErrorProcessor);
                        stringBuffer.setLength(0);
                        if (url2.length > 8) {
                            if (new String(Arrays.copyOf(url2, 8), StandardCharsets.UTF_8).startsWith(ERROR_PREFIX)) {
                                String str4 = new String(url2, StandardCharsets.UTF_8);
                                LOGGER.warning("Error durante la prefirma: " + str4);
                                throw buildInternalException(str4, properties);
                            }
                            try {
                                String strEncode2 = Base64.encode(TriphaseDataSigner.doSign(new AOPkcs1Signer(), str3, privateKey, certificateArr, TriphaseData.parser(Base64.decode(url2, 0, url2.length, true)), properties).toString().getBytes(), true);
                                try {
                                    StringBuffer stringBuffer2 = new StringBuffer();
                                    stringBuffer2.append(url).append("?").append("op").append(HTTP_EQUALS).append("post").append(HTTP_AND).append(PARAMETER_NAME_CRYPTO_OPERATION).append(HTTP_EQUALS).append(str2).append(HTTP_AND).append(XAdESExtraParams.FORMAT).append(HTTP_EQUALS).append(str).append(HTTP_AND).append(PARAMETER_NAME_ALGORITHM).append(HTTP_EQUALS).append(str3).append(HTTP_AND).append(PARAMETER_NAME_CERT).append(HTTP_EQUALS).append(strPrepareCertChainParam).append(HTTP_AND).append(PARAMETER_NAME_SESSION_DATA).append(HTTP_EQUALS).append(strEncode2);
                                    if (strEncode != null) {
                                        stringBuffer2.append(HTTP_AND).append(PARAMETER_NAME_DOCID).append(HTTP_EQUALS).append(strEncode);
                                    }
                                    if (properties2.size() > 0) {
                                        stringBuffer2.append(HTTP_AND).append(PARAMETER_NAME_EXTRA_PARAM).append(HTTP_EQUALS).append(AOUtil.properties2Base64(properties2));
                                    }
                                    byte[] url3 = installedManager.readUrl(stringBuffer2.toString(), UrlHttpMethod.POST);
                                    if (url3.length > 8 && new String(Arrays.copyOf(url3, 8), StandardCharsets.UTF_8).startsWith(CONFIG_NEEDED_ERROR_PREFIX)) {
                                        String str5 = new String(url3, StandardCharsets.UTF_8);
                                        LOGGER.warning("Error durante la postfirma: " + str5);
                                        throw buildInternalException(str5, properties);
                                    }
                                    String strTrim = new String(url3).trim();
                                    if (!strTrim.startsWith(SUCCESS)) {
                                        throw new AOException("La firma trifasica no ha finalizado correctamente: " + new String(url3));
                                    }
                                    try {
                                        return Base64.decode(strTrim.substring("OK NEWID=".length()), true);
                                    } catch (IOException e) {
                                        LOGGER.warning("El resultado de NEWID del servidor no estaba en Base64: " + e);
                                        throw new AOException("El resultado devuelto por el servidor no es correcto: " + e, e);
                                    }
                                } catch (IOException e2) {
                                    throw new AOException("Error en la llamada de postfirma al servidor: " + e2, e2);
                                }
                            } catch (Exception e3) {
                                LOGGER.severe("Error al analizar la prefirma enviada por el servidor: " + e3);
                                throw new AOException("Error al analizar la prefirma enviada por el servidor: " + e3, e3);
                            }
                        }
                        LOGGER.warning("No se han obtenido datos de la prefirma: " + new String(url2, StandardCharsets.UTF_8));
                        throw new AOException("No se han obtenido datos de la prefirma");
                    } catch (IOException e4) {
                        if (sSLErrorProcessor.isCancelled()) {
                            LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma trifasica: " + LoggerUtil.getTrimStr(url.toString()));
                        }
                        throw new AOException("Error en la llamada de prefirma al servidor: " + e4, e4);
                    }
                } catch (IOException e5) {
                    throw new AOException("Error en la llamada de prefirma al servidor: " + e5, e5);
                }
            } catch (CertificateEncodingException e6) {
                throw new AOException("Error decodificando la cadena de certificados: " + e6, e6);
            }
        } catch (Exception e7) {
            throw new IllegalArgumentException("No se ha proporcionado una URL valida para el servidor de firma: " + properties.getProperty(PROPERTY_NAME_SIGN_SERVER_URL), e7);
        }
    }

    private static AOException buildInternalException(String str, Properties properties) {
        AOException aOException = null;
        int iIndexOf = str.indexOf(":");
        if (str.startsWith(CONFIG_NEEDED_ERROR_PREFIX)) {
            int iIndexOf2 = str.indexOf(":", iIndexOf + 1);
            String strSubstring = str.substring(iIndexOf + 1, iIndexOf2);
            String strSubstring2 = str.substring(iIndexOf2 + 1);
            if (SigningLTSException.REQUESTOR_MSG_CODE.equals(strSubstring)) {
                aOException = new SigningLTSException(strSubstring2);
            } else if (SigningLTSException.REQUESTOR_POSSIBLE_MSG_CODE.equals(strSubstring)) {
                aOException = new SigningLTSException(strSubstring2, true);
            } else if (AGEPolicyIncompatibilityException.REQUESTOR_SIGN_MSG_CODE.equals(strSubstring)) {
                aOException = new AGEPolicyIncompatibilityException(strSubstring2, 1);
            } else if (AGEPolicyIncompatibilityException.REQUESTOR_COSIGN_MSG_CODE.equals(strSubstring)) {
                aOException = new AGEPolicyIncompatibilityException(strSubstring2, 2);
            } else if (AGEPolicyIncompatibilityException.REQUESTOR_COUNTERSIGN_MSG_CODE.equals(strSubstring)) {
                aOException = new AGEPolicyIncompatibilityException(strSubstring2, 3);
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

    @Override // es.gob.afirma.core.signers.OptionalDataInterface
    public boolean needData(String str, Properties properties) {
        return isDataMandatory(str, properties);
    }

    private static boolean isDataMandatory(String str, Properties properties) {
        return ("sign".equalsIgnoreCase(str) && properties != null && (AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED.equals(properties.getProperty(XAdESExtraParams.FORMAT)) || Boolean.parseBoolean(properties.getProperty("useManifest")))) ? false : true;
    }
}
