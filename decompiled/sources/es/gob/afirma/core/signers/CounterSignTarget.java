package es.gob.afirma.core.signers;

import es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/CounterSignTarget.class */
public enum CounterSignTarget {
    SIGNERS("signers"),
    NODES("nodes"),
    TREE("tree"),
    LEAFS(AOXAdESTriPhaseSigner.COUNTERSIGN_TARGET_LEAFS);

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
        if (AOXAdESTriPhaseSigner.COUNTERSIGN_TARGET_LEAFS.equalsIgnoreCase(strTrim)) {
            return LEAFS;
        }
        throw new IllegalArgumentException("Objetivo no soportado: " + strTrim);
    }
}
