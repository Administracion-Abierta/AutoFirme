package es.gob.afirma.signers.cadestri.client;

import com.aowagie.text.html.HtmlTags;
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

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cadestri/client/PreSigner.class */
final class PreSigner {
    private PreSigner() {
    }

    static byte[] preSign(String str, String str2, Certificate[] certificateArr, String str3, String str4, UrlHttpManager urlHttpManager, URL url, Properties properties) throws IOException, CertificateEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append(Config.DEFAULT_GLOBAL_SECTION_NAME).append(es.gob.afirma.core.misc.protocol.ProtocolConstants.OPERATION_PARAM).append("=").append(HtmlTags.PRE).append("&").append("cop").append("=").append(str3).append("&").append(XAdESExtraParams.FORMAT).append("=").append(str).append("&").append("algo").append("=").append(str2).append("&").append("cert").append("=").append(TriphaseUtil.prepareCertChainParam(certificateArr, properties)).append("&").append("doc").append("=").append(str4);
        properties.remove("serverUrl");
        properties.remove("documentId");
        if (properties.size() > 0) {
            sb.append("&").append("params").append("=").append(AOUtil.properties2Base64(properties));
        }
        SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
        try {
            return urlHttpManager.readUrl(sb.toString(), UrlHttpMethod.POST, sSLErrorProcessor);
        } catch (IOException e) {
            if (sSLErrorProcessor.isCancelled()) {
                Logger.getLogger("es.gob.afirma").info("El usuario no permite la importacion del certificado SSL de confianza del servicio de firma trifasica: " + LoggerUtil.getTrimStr(url.toString()));
            }
            throw e;
        }
    }
}
