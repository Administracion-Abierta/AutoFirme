package es.gob.afirma.core.misc.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/protocol/ProtocolInvocationUriParser.class */
public final class ProtocolInvocationUriParser {
    private ProtocolInvocationUriParser() {
    }

    public static UrlParametersToSign getParametersToSign(String str) throws ParameterException {
        return getParametersToSign(str, false);
    }

    public static UrlParametersToSign getParametersToSign(String str, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSign(parserUri(str), z);
    }

    public static UrlParametersToSign getParametersToSign(byte[] bArr) throws ParameterException {
        return getParametersToSign(bArr, false);
    }

    public static UrlParametersToSign getParametersToSign(byte[] bArr, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSign(ProtocolInvocationUriParserUtil.parseXml(bArr), z);
    }

    public static UrlParametersToSignAndSave getParametersToSignAndSave(String str) throws ParameterException {
        return getParametersToSignAndSave(str, false);
    }

    public static UrlParametersToSignAndSave getParametersToSignAndSave(String str, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSignAndSave(parserUri(str), z);
    }

    public static UrlParametersToSignAndSave getParametersToSignAndSave(byte[] bArr) throws ParameterException {
        return getParametersToSignAndSave(bArr, false);
    }

    public static UrlParametersToSignAndSave getParametersToSignAndSave(byte[] bArr, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSignAndSave(ProtocolInvocationUriParserUtil.parseXml(bArr), z);
    }

    public static UrlParametersToSelectCert getParametersToSelectCert(String str) throws ParameterException {
        return getParametersToSelectCert(str, false);
    }

    public static UrlParametersToSelectCert getParametersToSelectCert(String str, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSelectCert(parserUri(str), z);
    }

    public static UrlParametersToSelectCert getParametersToSelectCert(byte[] bArr) throws ParameterException {
        return getParametersToSelectCert(bArr, false);
    }

    public static UrlParametersToSelectCert getParametersToSelectCert(byte[] bArr, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSelectCert(ProtocolInvocationUriParserUtil.parseXml(bArr), z);
    }

    public static UrlParametersToSave getParametersToSave(byte[] bArr) throws ParameterException {
        return getParametersToSave(bArr, false);
    }

    public static UrlParametersToSave getParametersToSave(byte[] bArr, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSave(ProtocolInvocationUriParserUtil.parseXml(bArr), z);
    }

    public static UrlParametersToSave getParametersToSave(String str) throws ParameterException {
        return getParametersToSave(str, false);
    }

    public static UrlParametersToSave getParametersToSave(String str, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToSave(parserUri(str), z);
    }

    public static UrlParametersForBatch getParametersToBatch(byte[] bArr) throws ParameterException {
        return getParametersToBatch(bArr, false);
    }

    public static UrlParametersForBatch getParametersToBatch(byte[] bArr, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToBatch(ProtocolInvocationUriParserUtil.parseXml(bArr), z);
    }

    public static UrlParametersForBatch getParametersToBatch(String str) throws ParameterException {
        return getParametersToBatch(str, false);
    }

    public static UrlParametersForBatch getParametersToBatch(String str, boolean z) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToBatch(parserUri(str), z);
    }

    public static UrlParametersToLoad getParametersToLoad(String str) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToLoad(parserUri(str));
    }

    public static UrlParametersToLoad getParametersToLoad(byte[] bArr) throws ParameterException {
        return ProtocolInvocationUriParserUtil.getParametersToLoad(ProtocolInvocationUriParserUtil.parseXml(bArr));
    }

    private static Map<String, String> parserUri(String str) {
        HashMap map = new HashMap();
        for (String str2 : str.substring(str.indexOf(63) + 1).split("&")) {
            int iIndexOf = str2.indexOf(61);
            if (iIndexOf > 0) {
                try {
                    map.put(str2.substring(0, iIndexOf), iIndexOf == str2.length() - 1 ? "" : URLDecoder.decode(str2.substring(iIndexOf + 1), ProtocolInvocationUriParserUtil.DEFAULT_URL_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    map.put(str2.substring(0, iIndexOf), iIndexOf == str2.length() - 1 ? "" : str2.substring(iIndexOf + 1));
                }
            }
        }
        Logger.getLogger("es.gob.afirma").info("URI recibida: " + (str.length() <= 300 ? str : str.substring(0, 300) + "..."));
        String strSubstring = str.substring(str.indexOf("://") + "://".length(), str.indexOf(63) != -1 ? str.indexOf(63) : str.length());
        if (strSubstring.endsWith("/")) {
            strSubstring = strSubstring.substring(0, strSubstring.length() - 1);
        }
        map.put(ProtocolConstants.OPERATION_PARAM, strSubstring.indexOf("/") == -1 ? strSubstring : strSubstring.substring(0, strSubstring.indexOf("/")));
        return map;
    }
}
