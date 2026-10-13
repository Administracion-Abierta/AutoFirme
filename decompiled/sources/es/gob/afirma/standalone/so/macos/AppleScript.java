package es.gob.afirma.standalone.so.macos;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/so/macos/AppleScript.class */
public class AppleScript {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final String script;

    public AppleScript(String str) {
        if (str == null) {
            throw new IllegalArgumentException("El script de entrada no puede ser nulo");
        }
        this.script = str;
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0099 */
    /* JADX WARN: Bottom block not found for handler: all -> 0x0147 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String run() throws java.lang.InterruptedException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 389
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.so.macos.AppleScript.run():java.lang.String");
    }
}
