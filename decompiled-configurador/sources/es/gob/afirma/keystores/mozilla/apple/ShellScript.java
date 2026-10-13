package es.gob.afirma.keystores.mozilla.apple;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/apple/ShellScript.class */
public class ShellScript {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final String script;
    private final File scriptFile;
    private boolean deleteFile;

    public ShellScript(String str) {
        this.deleteFile = false;
        if (str == null) {
            throw new IllegalArgumentException("El script de entrada no puede ser nulo");
        }
        this.script = str;
        this.scriptFile = null;
    }

    public ShellScript(File file, boolean z) {
        this.deleteFile = false;
        if (file == null) {
            throw new IllegalArgumentException("El fichero script de entrada no puede ser nulo");
        }
        this.script = null;
        this.scriptFile = file;
        this.deleteFile = z;
    }

    public String run() throws InterruptedException, IOException {
        return run(false);
    }

    public String runAsAdministrator() throws InterruptedException, IOException {
        return run(true);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00e3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String run(boolean r6) throws java.lang.InterruptedException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 487
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.keystores.mozilla.apple.ShellScript.run(boolean):java.lang.String");
    }
}
