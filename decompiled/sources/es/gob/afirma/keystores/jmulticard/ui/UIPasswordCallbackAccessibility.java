package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Component;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/UIPasswordCallbackAccessibility.class */
public final class UIPasswordCallbackAccessibility extends PasswordCallback {
    private static final long serialVersionUID = 1719174318602363633L;
    private final transient String message;
    private final transient int mnemonic;
    private final transient Component parent;
    private final transient String title;
    private final transient String iconPath;
    private final transient boolean allowUseCache;
    private final transient boolean defaultUseCache;
    private transient boolean useCache;

    public UIPasswordCallbackAccessibility(String str, Component component, String str2, int i, String str3, String str4, boolean z, boolean z2) {
        super(str, false);
        this.parent = component;
        if (str != null) {
            this.message = str;
        } else {
            this.message = str2;
        }
        this.mnemonic = i;
        this.title = str3;
        this.iconPath = str4;
        this.allowUseCache = z;
        this.defaultUseCache = z2;
        this.useCache = false;
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        PasswordResult passwordResultShowInputPasswordDialog = InputPasswordSmartcardDialog.showInputPasswordDialog(this.parent, true, this.message, this.mnemonic, this.title, this.iconPath, this.allowUseCache, this.defaultUseCache);
        this.useCache = passwordResultShowInputPasswordDialog.isUseCache();
        char[] password = passwordResultShowInputPasswordDialog.getPassword();
        passwordResultShowInputPasswordDialog.clear();
        return password;
    }

    public boolean isUseCacheChecked() {
        return this.useCache;
    }
}
