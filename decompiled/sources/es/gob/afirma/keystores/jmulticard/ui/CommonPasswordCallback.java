package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.HeadlessException;
import java.security.KeyStore;
import java.util.logging.Logger;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/CommonPasswordCallback.class */
public final class CommonPasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = 5514503307266079255L;
    private static boolean headless = false;
    private final transient String title;
    private final transient boolean isDnie;
    private final transient boolean allowUseCache;
    private final transient boolean defaultUseCacheValue;
    private transient boolean useCacheChecked;

    static {
        setHeadLess(Boolean.getBoolean("java.awt.headless"));
    }

    static void setHeadLess(boolean z) {
        headless = z;
    }

    public CommonPasswordCallback(String str, String str2, boolean z) {
        this(str, str2, z, false, false);
    }

    public CommonPasswordCallback(String str, String str2, boolean z, boolean z2, boolean z3) {
        super(str, true);
        if (str == null) {
            throw new IllegalArgumentException("El texto de solicitud no puede ser nulo");
        }
        if (str2 == null) {
            this.title = str;
        } else {
            this.title = str2;
        }
        this.isDnie = z;
        this.allowUseCache = z2;
        this.defaultUseCacheValue = z3;
        this.useCacheChecked = this.defaultUseCacheValue;
    }

    public CommonPasswordCallback(KeyStore.PasswordProtection passwordProtection) {
        super("Por favor, introduzca el PIN de su tarjeta", false);
        if (passwordProtection == null) {
            throw new IllegalArgumentException("El PasswordProtection no puede ser nulo");
        }
        this.title = getPrompt();
        this.isDnie = false;
        this.allowUseCache = false;
        this.defaultUseCacheValue = false;
        this.useCacheChecked = this.defaultUseCacheValue;
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        UIPasswordCallbackAccessibility uIPasswordCallbackAccessibility;
        if (!headless) {
            try {
                if (this.isDnie) {
                    uIPasswordCallbackAccessibility = new UIPasswordCallbackAccessibility(getPrompt(), PasswordCallbackManager.getDialogOwner(), getPrompt(), 80, this.title, "/images/dnie.png", this.allowUseCache, this.defaultUseCacheValue);
                } else {
                    uIPasswordCallbackAccessibility = new UIPasswordCallbackAccessibility(getPrompt(), PasswordCallbackManager.getDialogOwner(), getPrompt(), 80, this.title, "/images/chipcard.png", this.allowUseCache, this.defaultUseCacheValue);
                }
                char[] password = uIPasswordCallbackAccessibility.getPassword();
                this.useCacheChecked = uIPasswordCallbackAccessibility.isUseCacheChecked();
                uIPasswordCallbackAccessibility.clearPassword();
                return password;
            } catch (HeadlessException e) {
                Logger.getLogger("es.gob.afirma").info("No hay entorno grafico, se revierte a consola: " + e);
            }
        }
        ConsolePasswordCallback consolePasswordCallback = new ConsolePasswordCallback(getPrompt());
        char[] password2 = consolePasswordCallback.getPassword();
        consolePasswordCallback.clearPassword();
        return password2;
    }

    public boolean isUseCacheChecked() {
        return this.useCacheChecked;
    }
}
