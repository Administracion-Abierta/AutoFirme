package es.gob.afirma.signers.batch.client;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.misc.http.HttpError;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.core.signers.TriphaseDataSigner;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.ini4j.Config;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Marker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/BatchSigner.class */
public final class BatchSigner {
    private static final String BATCH_XML_PARAM = "xml";
    private static final String BATCH_JSON_PARAM = "json";
    private static final String BATCH_CRT_PARAM = "certs";
    private static final String BATCH_TRI_PARAM = "tridata";
    private static final String EQU = "=";
    private static final String AMP = "&";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private BatchSigner() {
    }

    public static String signXML(String str, String str2, String str3, Certificate[] certificateArr, PrivateKey privateKey) throws IOException, AOException, CertificateEncodingException {
        return signXML(str, str2, str3, certificateArr, privateKey, null);
    }

    public static String signXML(String str, String str2, String str3, Certificate[] certificateArr, PrivateKey privateKey, Properties properties) throws IOException, AOException, CertificateEncodingException {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("El lote de firma no puede ser nulo ni vacio");
        }
        if (str2 == null || str2.isEmpty()) {
            throw new IllegalArgumentException("La URL de preproceso de lotes no puede se nula ni vacia");
        }
        if (str3 == null || str3.isEmpty()) {
            throw new IllegalArgumentException("La URL de postproceso de lotes no puede ser nula ni vacia");
        }
        if (certificateArr == null || certificateArr.length < 1) {
            throw new IllegalArgumentException("La cadena de certificados del firmante no puede ser nula ni vacia");
        }
        SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
        String strReplace = str.replace(Marker.ANY_NON_NULL_MARKER, "-").replace("/", "_");
        try {
            try {
                return new String(UrlHttpManagerFactory.getInstalledManager().readUrl(str3 + Config.DEFAULT_GLOBAL_SECTION_NAME + "xml" + EQU + strReplace + AMP + BATCH_CRT_PARAM + EQU + getCertChainAsBase64(certificateArr) + AMP + BATCH_TRI_PARAM + EQU + Base64.encode(TriphaseDataSigner.doSign(new AOPkcs1Signer(), getAlgorithmForXML(str), privateKey, certificateArr, TriphaseData.parser(UrlHttpManagerFactory.getInstalledManager().readUrl(str2 + Config.DEFAULT_GLOBAL_SECTION_NAME + "xml" + EQU + strReplace + AMP + BATCH_CRT_PARAM + EQU + getCertChainAsBase64(certificateArr), UrlHttpMethod.POST, sSLErrorProcessor)), null).toString().getBytes(DEFAULT_CHARSET), true), UrlHttpMethod.POST), DEFAULT_CHARSET);
            } catch (HttpError e) {
                LOGGER.warning("El servicio de firma devolvio un  error durante la postfirma: " + e.getResponseDescription());
                throw e;
            }
        } catch (HttpError e2) {
            LOGGER.warning("El servicio de firma devolvio un error durante la prefirma del lote XML: " + e2);
            throw e2;
        } catch (IOException e3) {
            if (sSLErrorProcessor.isCancelled()) {
                LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma de lote: " + LoggerUtil.getTrimStr(str2));
            }
            throw e3;
        }
    }

    public static String signJSON(String str, String str2, String str3, Certificate[] certificateArr, PrivateKey privateKey) throws IOException, AOException, CertificateEncodingException {
        return signJSON(str, str2, str3, certificateArr, privateKey, null);
    }

    public static String signJSON(String str, String str2, String str3, Certificate[] certificateArr, PrivateKey privateKey, Properties properties) throws IOException, AOException, CertificateEncodingException {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("El lote de firma no puede ser nulo ni vacio");
        }
        if (str2 == null || str2.isEmpty()) {
            throw new IllegalArgumentException("La URL de preproceso de lotes no puede se nula ni vacia");
        }
        if (str3 == null || str3.isEmpty()) {
            throw new IllegalArgumentException("La URL de postproceso de lotes no puede ser nula ni vacia");
        }
        if (certificateArr == null || certificateArr.length < 1) {
            throw new IllegalArgumentException("La cadena de certificados del firmante no puede ser nula ni vacia");
        }
        String strReplace = str.replace(Marker.ANY_NON_NULL_MARKER, "-").replace("/", "_");
        SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
        try {
            PresignBatch fromJSON = JSONPreSignBatchParser.parseFromJSON(UrlHttpManagerFactory.getInstalledManager().readUrl(str2 + Config.DEFAULT_GLOBAL_SECTION_NAME + BATCH_JSON_PARAM + EQU + strReplace + AMP + BATCH_CRT_PARAM + EQU + getCertChainAsBase64(certificateArr), UrlHttpMethod.POST, sSLErrorProcessor));
            TriphaseData triphaseData = fromJSON.getTriphaseData();
            List<BatchDataResult> errors = fromJSON.getErrors();
            if (triphaseData == null && errors == null) {
                return JSONBatchInfoParser.buildEmptyResult().toString();
            }
            if (triphaseData == null) {
                return JSONBatchInfoParser.buildResult(errors).toString();
            }
            if (errors != null) {
                BatchInfo batchInfo = JSONBatchInfoParser.parse(Base64.decode(str));
                batchInfo.updateResults(errors);
                strReplace = Base64.encode(batchInfo.getInfoString().getBytes(StandardCharsets.UTF_8), true);
            }
            try {
                return new String(UrlHttpManagerFactory.getInstalledManager().readUrl(str3 + Config.DEFAULT_GLOBAL_SECTION_NAME + BATCH_JSON_PARAM + EQU + strReplace + AMP + BATCH_CRT_PARAM + EQU + getCertChainAsBase64(certificateArr) + AMP + BATCH_TRI_PARAM + EQU + Base64.encode(TriphaseDataParser.triphaseDataToJsonString(TriphaseDataSigner.doSign(new AOPkcs1Signer(), getAlgorithmForJSON(str), privateKey, certificateArr, triphaseData, null)).getBytes(DEFAULT_CHARSET), true), UrlHttpMethod.POST), DEFAULT_CHARSET);
            } catch (HttpError e) {
                LOGGER.warning("El servicio de firma devolvio un  error durante la postfirma: " + e);
                throw e;
            }
        } catch (HttpError e2) {
            LOGGER.warning("El servicio de firma devolvio un error durante la prefirma del lote JSON: " + e2);
            throw e2;
        } catch (IOException e3) {
            if (sSLErrorProcessor.isCancelled()) {
                LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma de lote: " + LoggerUtil.getTrimStr(str2));
            }
            throw e3;
        }
    }

    private static String getCertChainAsBase64(Certificate[] certificateArr) throws CertificateEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Certificate certificate : certificateArr) {
            sb.append(Base64.encode(certificate.getEncoded(), true));
            sb.append(PolicyIdFilter.OID_SEPARATOR);
        }
        String string = sb.toString();
        return string.substring(0, string.length() - 1);
    }

    private static String getAlgorithmForXML(String str) throws IOException {
        byte[] bArrDecode = Base64.decode(str.replace("-", Marker.ANY_NON_NULL_MARKER).replace("_", "/"));
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArrDecode);
            Throwable th = null;
            try {
                try {
                    Document document = SecureXmlBuilder.getSecureDocumentBuilder().parse(byteArrayInputStream);
                    if (byteArrayInputStream != null) {
                        if (0 != 0) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            byteArrayInputStream.close();
                        }
                    }
                    Element documentElement = document.getDocumentElement();
                    if (!"signbatch".equalsIgnoreCase(documentElement.getNodeName())) {
                        throw new IllegalArgumentException("No se encontro el nodo 'signbatch' en el XML proporcionado");
                    }
                    NamedNodeMap attributes = documentElement.getAttributes();
                    if (attributes != null) {
                        Node namedItem = attributes.getNamedItem("algorithm");
                        if (namedItem != null) {
                            return namedItem.getNodeValue();
                        }
                        throw new IllegalArgumentException("El nodo 'signbatch' debe contener al manos el atributo de algoritmo");
                    }
                    throw new IllegalArgumentException("El nodo 'signbatch' debe contener al manos el atributo de algoritmo");
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (byteArrayInputStream != null) {
                    if (th != null) {
                        try {
                            byteArrayInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        byteArrayInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            LOGGER.severe("Error al cargar el fichero XML de lote: " + e + "\n" + LoggerUtil.getTrimBytes(bArrDecode));
            throw new IOException("Error al cargar el fichero XML de lote: " + e, e);
        }
    }

    private static String getAlgorithmForJSON(String str) throws IOException {
        try {
            JSONObject jSONObject = new JSONObject(new String(Base64.decode(str), DEFAULT_CHARSET));
            if (jSONObject.has("algorithm")) {
                return jSONObject.getString("algorithm");
            }
            throw new IllegalArgumentException("El nodo 'signbatch' debe contener al manos el atributo de algoritmo");
        } catch (JSONException e) {
            LOGGER.severe("Error al parsear JSON");
            throw new JSONException("El JSON de definicion de lote de firmas no esta formado correctamente");
        }
    }
}
