package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.keystores.AOKeyStore;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/RegisteredKeystore.class */
public class RegisteredKeystore {
    private String name;
    private String lib;
    private String providerName;
    private final boolean isSystemSmartCard;

    RegisteredKeystore() {
        this.isSystemSmartCard = false;
    }

    RegisteredKeystore(AOKeyStore aOKeyStore) {
        this(aOKeyStore, false);
    }

    RegisteredKeystore(AOKeyStore aOKeyStore, boolean z) {
        this.name = aOKeyStore.getName();
        this.providerName = aOKeyStore.getProviderName();
        this.lib = null;
        this.isSystemSmartCard = z;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getLib() {
        return this.lib;
    }

    public void setLib(String str) {
        this.lib = str;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public void setProviderName(String str) {
        this.providerName = str;
    }

    public boolean isSystemSmartCard() {
        return this.isSystemSmartCard;
    }

    public String toString() {
        return getName();
    }
}
