package es.gob.afirma.signers.cadestri.client;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.signers.TriphaseUtil;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Properties;
import java.util.logging.Logger;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cadestri/client/PostSigner.class */
final class PostSigner {
    private static final String OPERATION_POSTSIGN = "post";

    private PostSigner() {
    }

    static byte[] postSign(String str, String str2, Certificate[] certificateArr, String str3, String str4, Properties properties, boolean z, UrlHttpManager urlHttpManager, URL url, String str5) throws IOException, CertificateEncodingException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url).append(Config.DEFAULT_GLOBAL_SECTION_NAME).append(es.gob.afirma.core.misc.protocol.ProtocolConstants.OPERATION_PARAM).append("=").append("post").append("&").append("cop").append("=").append(str3).append("&").append(XAdESExtraParams.FORMAT).append("=").append(str).append("&").append("algo").append("=").append(str2).append("&").append("cert").append("=").append(TriphaseUtil.prepareCertChainParam(certificateArr, properties));
        if (properties.size() > 0) {
            stringBuffer.append("&").append("params").append("=").append(AOUtil.properties2Base64(properties));
        }
        stringBuffer.append("&").append("session").append("=").append(str5);
        if (z) {
            stringBuffer.append("&").append("doc").append("=").append(str4);
        }
        SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
        try {
            return urlHttpManager.readUrl(stringBuffer.toString(), UrlHttpMethod.POST, sSLErrorProcessor);
        } catch (IOException e) {
            if (sSLErrorProcessor.isCancelled()) {
                Logger.getLogger("es.gob.afirma").info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma trifasica: " + LoggerUtil.getTrimStr(url.toString()));
            }
            throw e;
        }
    }
}
