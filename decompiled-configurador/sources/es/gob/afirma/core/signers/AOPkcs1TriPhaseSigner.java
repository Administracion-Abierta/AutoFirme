package es.gob.afirma.core.signers;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.util.tree.AOTreeModel;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOPkcs1TriPhaseSigner.class */
public class AOPkcs1TriPhaseSigner implements AOSigner {
    protected static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String PROPERTY_NAME_SIGN_SERVER_URL = "serverUrl";
    private static final String OPERATION_PRESIGN = "pre";
    private static final String OPERATION_POSTSIGN = "post";
    private static final String CRYPTO_OPERATION_SIGN = "sign";
    private static final String PARAMETER_NAME_OPERATION = "op";
    private static final String PARAMETER_NAME_CRYPTO_OPERATION = "cop";
    private static final String HTTP_CGI = "?";
    private static final String HTTP_EQUALS = "=";
    private static final String HTTP_AND = "&";
    private static final String PARAMETER_NAME_DOCID = "doc";
    private static final String PARAMETER_NAME_ALGORITHM = "algo";
    private static final String PARAMETER_NAME_FORMAT = "format";
    private static final String PARAMETER_NAME_CERT = "cert";
    private static final String PARAMETER_NAME_SESSION_DATA = "session";
    private static final String PROPERTY_NAME_PRESIGN = "PRE";
    private static final String PROPERTY_NAME_PKCS1_SIGN = "PK1";
    private static final String SUCCESS = "OK";
    private final String signFormat;

    protected AOPkcs1TriPhaseSigner(String str) {
        this.signFormat = str;
    }

    public AOPkcs1TriPhaseSigner() {
        this(AOSignConstants.SIGN_FORMAT_PKCS1);
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation(this.signFormat, CRYPTO_OPERATION_SIGN, bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public final byte[] getData(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws AOInvalidFormatException {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        throw new UnsupportedOperationException("No se soporta la multifirma de firmas NONE");
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        throw new UnsupportedOperationException("No se soporta la multifirma de firmas NONE");
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        throw new UnsupportedOperationException("No se soporta la multifirma de firmas NONE");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public final AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        return isSign(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return false;
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
        return str + (str2 != null ? str2 : "") + ".p1";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) {
        throw new UnsupportedOperationException("No se soporta en firma trifasica");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
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
        if (bArr == null) {
            throw new IllegalArgumentException("No se ha proporcionado el identificador de documento a firmar");
        }
        try {
            URL url = new URL(properties.getProperty(PROPERTY_NAME_SIGN_SERVER_URL));
            String strEncode = Base64.encode(bArr, true);
            UrlHttpManager installedManager = UrlHttpManagerFactory.getInstalledManager();
            try {
                String strEncode2 = Base64.encode(certificateArr[0].getEncoded(), true);
                SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(url).append(HTTP_CGI).append("op").append(HTTP_EQUALS).append(OPERATION_PRESIGN).append(HTTP_AND).append(PARAMETER_NAME_CRYPTO_OPERATION).append(HTTP_EQUALS).append(str2).append(HTTP_AND).append("format").append(HTTP_EQUALS).append(str).append(HTTP_AND).append(PARAMETER_NAME_ALGORITHM).append(HTTP_EQUALS).append(str3).append(HTTP_AND).append(PARAMETER_NAME_CERT).append(HTTP_EQUALS).append(strEncode2).append(HTTP_AND).append(PARAMETER_NAME_DOCID).append(HTTP_EQUALS).append(strEncode);
                    String string = stringBuffer.toString();
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("Se llamara por POST a la siguiente URL:\n" + LoggerUtil.getTrimStr(string));
                    }
                    byte[] url2 = installedManager.readUrl(string, UrlHttpMethod.POST, sSLErrorProcessor);
                    stringBuffer.setLength(0);
                    try {
                        TriphaseData rVar = TriphaseData.parser(Base64.decode(url2, 0, url2.length, true));
                        TriphaseData.TriSign sign = rVar.getSign(0);
                        try {
                            sign.addProperty(PROPERTY_NAME_PKCS1_SIGN, Base64.encode(new AOPkcs1Signer().sign(Base64.decode(sign.getProperty(PROPERTY_NAME_PRESIGN)), str3, privateKey, certificateArr, properties)));
                            String strEncode3 = Base64.encode(rVar.toString().getBytes(), true);
                            try {
                                StringBuffer stringBuffer2 = new StringBuffer();
                                stringBuffer2.append(url).append(HTTP_CGI).append("op").append(HTTP_EQUALS).append(OPERATION_POSTSIGN).append(HTTP_AND).append(PARAMETER_NAME_CRYPTO_OPERATION).append(HTTP_EQUALS).append(str2).append(HTTP_AND).append("format").append(HTTP_EQUALS).append(str).append(HTTP_AND).append(PARAMETER_NAME_ALGORITHM).append(HTTP_EQUALS).append(str3).append(HTTP_AND).append(PARAMETER_NAME_CERT).append(HTTP_EQUALS).append(strEncode2).append(HTTP_AND).append(PARAMETER_NAME_DOCID).append(HTTP_EQUALS).append(strEncode).append(HTTP_AND).append(PARAMETER_NAME_SESSION_DATA).append(HTTP_EQUALS).append(strEncode3);
                                String strTrim = new String(installedManager.readUrl(stringBuffer2.toString(), UrlHttpMethod.POST)).trim();
                                if (!strTrim.startsWith(SUCCESS)) {
                                    throw new AOException("La firma trifasica no ha finalizado correctamente: " + strTrim);
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
                        } catch (IOException e3) {
                            LOGGER.severe("Error al decodificar la prefirma de los datos: " + e3);
                            throw new AOException("Error al decodificar la prefirma de los datos", e3);
                        }
                    } catch (Exception e4) {
                        LOGGER.severe("Error al analizar la prefirma enviada por el servidor: " + e4);
                        throw new AOException("Error al analizar la prefirma enviada por el servidor: " + e4, e4);
                    }
                } catch (IOException e5) {
                    if (sSLErrorProcessor.isCancelled()) {
                        LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma trifasica: " + LoggerUtil.getTrimStr(url.toString()));
                    }
                    throw new AOException("Error en la llamada de prefirma al servidor: " + e5, e5);
                }
            } catch (CertificateEncodingException e6) {
                throw new AOException("Error decodificando la cadena de certificados: " + e6, e6);
            }
        } catch (Exception e7) {
            throw new IllegalArgumentException("No se ha proporcionado una URL valida para el servidor de firma: " + properties.getProperty(PROPERTY_NAME_SIGN_SERVER_URL), e7);
        }
    }
}
