package es.gob.afirma.keystores;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/KeyStoreConfiguration.class */
public final class KeyStoreConfiguration {
    private final AOKeyStore type;
    private final String name;
    private final String lib;

    public KeyStoreConfiguration(AOKeyStore aOKeyStore, String str, String str2) {
        this.type = aOKeyStore;
        if (aOKeyStore == null) {
            throw new IllegalArgumentException("Es necesario indicar el tipo de almacen");
        }
        this.name = str != null ? str : aOKeyStore.getName();
        this.lib = str2;
    }

    public AOKeyStore getType() {
        return this.type;
    }

    public String getLib() {
        return this.lib;
    }

    public String toString() {
        return this.name;
    }
}
