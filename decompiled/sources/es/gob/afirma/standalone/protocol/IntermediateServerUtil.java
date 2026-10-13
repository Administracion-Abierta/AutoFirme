package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.standalone.HttpManager;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/IntermediateServerUtil.class */
public class IntermediateServerUtil {
    private static final String METHOD_OP_PUT = "put";
    private static final String METHOD_OP_GET = "get";
    private static final String SYNTAX_VERSION = "1_0";
    private static Object semaphore = null;

    public static void sendData(CharSequence charSequence, String str, String str2) throws IOException {
        send(new StringBuilder(str).append("?op=").append(METHOD_OP_PUT).append("&v=").append(SYNTAX_VERSION).append("&id=").append(str2).append("&dat=").append(charSequence));
    }

    public static byte[] retrieveData(String str, String str2) throws IOException {
        return send(new StringBuilder(str).append("?op=").append(METHOD_OP_GET).append("&v=").append(SYNTAX_VERSION).append("&id=").append(str2));
    }

    private static byte[] send(StringBuilder sb) throws IOException {
        return new HttpManager().readUrl(sb.toString(), UrlHttpMethod.POST);
    }

    public static Object getUniqueSemaphoreInstance() {
        if (semaphore == null) {
            semaphore = new Object();
        }
        return semaphore;
    }
}
