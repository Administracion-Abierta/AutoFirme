package es.gob.afirma.keystores.jmulticard.ui;

import es.gob.jmulticard.CancelledOperationException;
import java.io.Console;
import java.util.Arrays;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/ConsolePasswordCallback.class */
final class ConsolePasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = -4044214967262414899L;
    private final transient String prompt;

    ConsolePasswordCallback(String str) {
        super(str, true);
        this.prompt = str;
    }

    private static String removeHTML(String str) {
        return str.replace("<br/>", "").replace("&nbsp;", "");
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        Console console = System.console();
        if (console == null) {
            throw new NoConsoleException("No hay consola para solicitar el PIN");
        }
        char[] password = console.readPassword(removeHTML(this.prompt) + ":\n", new Object[0]);
        if (password == null) {
            throw new CancelledOperationException("Se ha cancelado la introduccion de PIN en consola");
        }
        if (password.length < 8 || password.length > 16) {
            console.printf(Messages.getString("ConsolePasswordCallback.1") + "\n", new Object[0]);
            Arrays.fill(password, (char) 0);
            return getPassword();
        }
        console.flush();
        return password;
    }
}
