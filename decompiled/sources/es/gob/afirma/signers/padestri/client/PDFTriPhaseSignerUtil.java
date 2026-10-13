package es.gob.afirma.signers.padestri.client;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.core.signers.TriphaseDataSigner;
import es.gob.afirma.core.signers.TriphaseUtil;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/padestri/client/PDFTriPhaseSignerUtil.class */
final class PDFTriPhaseSignerUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String OPERATION_PRESIGN = "pre";
    private static final String OPERATION_POSTSIGN = "post";
    private static final String PARAMETER_NAME_OPERATION = "op";
    private static final String CRYPTO_OPERATION_SIGN = "sign";
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
    private static final String PADES_FORMAT = "pades";
    private static final String SUCCESS = "OK";

    private PDFTriPhaseSignerUtil() {
    }

    static byte[] doPresign(URL url, String str, Certificate[] certificateArr, String str2, Properties properties) throws AOException {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url).append("?").append("op").append(HTTP_EQUALS).append("pre").append(HTTP_AND).append(PARAMETER_NAME_CRYPTO_OPERATION).append(HTTP_EQUALS).append("sign").append(HTTP_AND).append("format").append(HTTP_EQUALS).append("pades").append(HTTP_AND).append(PARAMETER_NAME_ALGORITHM).append(HTTP_EQUALS).append(str).append(HTTP_AND).append(PARAMETER_NAME_CERT).append(HTTP_EQUALS).append(TriphaseUtil.prepareCertChainParam(certificateArr, properties)).append(HTTP_AND).append(PARAMETER_NAME_DOCID).append(HTTP_EQUALS).append(str2);
            if (properties.size() > 0) {
                stringBuffer.append(HTTP_AND).append(PARAMETER_NAME_EXTRA_PARAM).append(HTTP_EQUALS).append(AOUtil.properties2Base64(properties));
            }
            SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
            try {
                return UrlHttpManagerFactory.getInstalledManager().readUrl(stringBuffer.toString(), UrlHttpMethod.POST, sSLErrorProcessor);
            } catch (IOException e) {
                if (sSLErrorProcessor.isCancelled()) {
                    LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma trifasica: " + LoggerUtil.getTrimStr(url.toString()));
                }
                throw new AOException("Error en la llamada de prefirma al servidor: " + e, e);
            }
        } catch (IOException e2) {
            throw new AOException("Error en la llamada de prefirma al servidor: " + e2, e2);
        } catch (CertificateEncodingException e3) {
            throw new AOException("Error decodificando el certificado del firmante: " + e3, e3);
        }
    }

    static byte[] doSign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        try {
            return TriphaseDataSigner.doSign(new AOPkcs1Signer(), str, privateKey, certificateArr, TriphaseData.parser(Base64.decode(bArr, 0, bArr.length, true)), properties).toString().getBytes();
        } catch (Exception e) {
            LOGGER.severe("Error al analizar la prefirma enviada por el servidor: " + e);
            throw new AOException("Error al analizar la prefirma enviada por el servidor", e);
        }
    }

    static byte[] doPostSign(String str, URL url, String str2, Certificate[] certificateArr, String str3, Properties properties) throws AOException {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url).append("?").append("op").append(HTTP_EQUALS).append("post").append(HTTP_AND).append(PARAMETER_NAME_CRYPTO_OPERATION).append(HTTP_EQUALS).append("sign").append(HTTP_AND).append("format").append(HTTP_EQUALS).append("pades").append(HTTP_AND).append(PARAMETER_NAME_ALGORITHM).append(HTTP_EQUALS).append(str2).append(HTTP_AND).append(PARAMETER_NAME_CERT).append(HTTP_EQUALS).append(TriphaseUtil.prepareCertChainParam(certificateArr, properties)).append(HTTP_AND).append(PARAMETER_NAME_DOCID).append(HTTP_EQUALS).append(str3).append(HTTP_AND).append(PARAMETER_NAME_SESSION_DATA).append(HTTP_EQUALS).append(str);
            if (properties.size() > 0) {
                stringBuffer.append(HTTP_AND).append(PARAMETER_NAME_EXTRA_PARAM).append(HTTP_EQUALS).append(AOUtil.properties2Base64(properties));
            }
            SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
            try {
                byte[] url2 = UrlHttpManagerFactory.getInstalledManager().readUrl(stringBuffer.toString(), UrlHttpMethod.POST, sSLErrorProcessor);
                stringBuffer.setLength(0);
                String strTrim = new String(url2).trim();
                if (!strTrim.startsWith(SUCCESS)) {
                    throw new AOException("La firma trifasica no ha finalizado correctamente: " + new String(url2));
                }
                try {
                    return Base64.decode(strTrim.substring("OK NEWID=".length()), true);
                } catch (IOException e) {
                    LOGGER.warning("El resultado de NEWID del servidor no estaba en Base64: " + e);
                    throw new AOException("El resultado devuelto por el servidor no es correcto", e);
                }
            } catch (IOException e2) {
                if (sSLErrorProcessor.isCancelled()) {
                    LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma trifasica: " + LoggerUtil.getTrimStr(url.toString()));
                }
                throw new AOException("Error en la llamada de postfirma al servidor: " + e2, e2);
            }
        } catch (IOException e3) {
            throw new AOException("Error en la llamada de postfirma al servidor: " + e3, e3);
        } catch (CertificateEncodingException e4) {
            throw new AOException("Error decodificando el certificado del firmante: " + e4, e4);
        }
    }
}
