package es.gob.afirma.core.misc.protocol;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/ProtocolInvocationUriParserUtil.class */
public final class ProtocolInvocationUriParserUtil {
    static final String DEFAULT_URL_ENCODING = StandardCharsets.UTF_8.name();
    private static final String DEFAULT_OPERATION = "SIGN";

    private ProtocolInvocationUriParserUtil() {
    }

    public static UrlParametersToSave getParametersToSave(Map<String, String> map, boolean z) throws ParameterException {
        UrlParametersToSave urlParametersToSave = new UrlParametersToSave(z);
        urlParametersToSave.setCommonParameters(map);
        urlParametersToSave.setSaveParameters(map);
        return urlParametersToSave;
    }

    public static UrlParametersForBatch getParametersToBatch(Map<String, String> map, boolean z) throws ParameterException {
        UrlParametersForBatch urlParametersForBatch = new UrlParametersForBatch(z);
        urlParametersForBatch.setCommonParameters(map);
        urlParametersForBatch.setBatchParameters(map);
        return urlParametersForBatch;
    }

    public static UrlParametersToLoad getParametersToLoad(Map<String, String> map) throws ParameterException {
        UrlParametersToLoad urlParametersToLoad = new UrlParametersToLoad();
        urlParametersToLoad.setCommonParameters(map);
        urlParametersToLoad.setLoadParameters(map);
        return urlParametersToLoad;
    }

    public static Map<String, String> parseXml(byte[] bArr) throws ParameterException {
        HashMap map = new HashMap();
        try {
            Element documentElement = SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
            map.put(ProtocolConstants.OPERATION_PARAM, ProtocolConstants.OPERATION_PARAM.equalsIgnoreCase(documentElement.getNodeName()) ? DEFAULT_OPERATION : documentElement.getNodeName());
            NodeList childNodes = documentElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node nodeItem = childNodes.item(i);
                if (!"e".equals(nodeItem.getNodeName())) {
                    throw new ParameterException("El XML no tiene la forma esperada");
                }
                NamedNodeMap attributes = nodeItem.getAttributes();
                Node namedItem = attributes.getNamedItem("k");
                Node namedItem2 = attributes.getNamedItem("v");
                if (namedItem == null || namedItem2 == null) {
                    throw new ParameterException("El XML no tiene la forma esperada");
                }
                try {
                    map.put(namedItem.getNodeValue(), URLDecoder.decode(namedItem2.getNodeValue(), DEFAULT_URL_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    Logger.getLogger("es.gob.afirma").warning("Codificacion no soportada para la URL (" + DEFAULT_URL_ENCODING + "): " + e);
                    map.put(namedItem.getNodeValue(), namedItem2.getNodeValue());
                }
            }
            return map;
        } catch (Exception e2) {
            throw new ParameterException("Error grave durante el analisis del XML: " + e2, e2);
        }
    }

    public static UrlParametersToSign getParametersToSign(Map<String, String> map, boolean z) throws ParameterException {
        UrlParametersToSign urlParametersToSign = new UrlParametersToSign(z);
        urlParametersToSign.setCommonParameters(map);
        urlParametersToSign.setSignParameters(map);
        urlParametersToSign.setAnotherParams(map);
        return urlParametersToSign;
    }

    public static UrlParametersToSignAndSave getParametersToSignAndSave(Map<String, String> map, boolean z) throws ParameterException {
        UrlParametersToSignAndSave urlParametersToSignAndSave = new UrlParametersToSignAndSave(z);
        urlParametersToSignAndSave.setCommonParameters(map);
        urlParametersToSignAndSave.setSignAndSaveParameters(map);
        return urlParametersToSignAndSave;
    }

    public static UrlParametersToSelectCert getParametersToSelectCert(Map<String, String> map, boolean z) throws ParameterException {
        UrlParametersToSelectCert urlParametersToSelectCert = new UrlParametersToSelectCert(z);
        urlParametersToSelectCert.setCommonParameters(map);
        urlParametersToSelectCert.setSelectCertParameters(map);
        return urlParametersToSelectCert;
    }
}
