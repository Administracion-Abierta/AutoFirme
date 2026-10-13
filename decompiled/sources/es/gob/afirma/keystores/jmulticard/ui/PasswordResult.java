package es.gob.afirma.keystores.jmulticard.ui;

import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/PasswordResult.class */
final class PasswordResult {
    private transient char[] password;
    private transient boolean useCache;

    PasswordResult(char[] cArr) {
        this.password = cArr != null ? (char[]) cArr.clone() : null;
        this.useCache = false;
    }

    PasswordResult(char[] cArr, boolean z) {
        this.password = cArr != null ? (char[]) cArr.clone() : null;
        this.useCache = z;
    }

    char[] getPassword() {
        if (this.password != null) {
            return (char[]) this.password.clone();
        }
        return null;
    }

    boolean isUseCache() {
        return this.useCache;
    }

    void clear() {
        if (this.password != null) {
            Arrays.fill(this.password, (char) 0);
            this.password = null;
        }
        this.useCache = false;
    }
}
