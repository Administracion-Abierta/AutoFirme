package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Component;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/PasswordCallbackManager.class */
public final class PasswordCallbackManager {
    private static Component dialogOwner = null;

    public static void setDialogOwner(Object obj) {
        if (obj instanceof Component) {
            dialogOwner = (Component) obj;
        }
    }

    public static Component getDialogOwner() {
        return dialogOwner;
    }

    private PasswordCallbackManager() {
    }
}
