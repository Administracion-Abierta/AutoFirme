package es.gob.afirma.core.signers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/CounterSignTarget.class */
public enum CounterSignTarget {
    SIGNERS("signers"),
    NODES("nodes"),
    TREE("tree"),
    LEAFS("leafs");

    private final String name;

    CounterSignTarget(String str) {
        this.name = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }

    public static CounterSignTarget getTarget(String str) {
        if (str == null) {
            throw new IllegalArgumentException("El objetivo de la contrafirma no puede ser nulo");
        }
        String strTrim = str.trim();
        if ("signers".equalsIgnoreCase(strTrim)) {
            return SIGNERS;
        }
        if ("nodes".equalsIgnoreCase(strTrim)) {
            return NODES;
        }
        if ("tree".equalsIgnoreCase(strTrim)) {
            return TREE;
        }
        if ("leafs".equalsIgnoreCase(strTrim)) {
            return LEAFS;
        }
        throw new IllegalArgumentException("Objetivo no soportado: " + strTrim);
    }
}
