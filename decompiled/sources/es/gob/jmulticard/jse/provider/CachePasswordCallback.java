package es.gob.jmulticard.jse.provider;

import javax.security.auth.callback.PasswordCallback;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/CachePasswordCallback.class */
public final class CachePasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = 816457144215238935L;

    public CachePasswordCallback(char[] password) {
        super(">", false);
        setPassword(password);
    }

    public String toString() {
        return "PasswordCallback con contrasena '" + new String(getPassword()) + OperatorName.SHOW_TEXT_LINE;
    }
}
