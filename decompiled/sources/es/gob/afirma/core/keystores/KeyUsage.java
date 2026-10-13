package es.gob.afirma.core.keystores;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/keystores/KeyUsage.class */
public final class KeyUsage {
    private static final int KEYUSAGE_NBITS = 9;
    private final Boolean[] usage;
    private static final KeyUsage EMPTY_USAGES = new KeyUsage(new Boolean[]{null, null, null, null, null, null, null, null, null});
    public static final KeyUsage SIGN = new KeyUsage(new Boolean[]{null, Boolean.TRUE, null, null, null, null, null, null, null});
    public static final KeyUsage AUTH = new KeyUsage(new Boolean[]{Boolean.TRUE, null, null, null, null, null, null, null, null});
    public static final KeyUsage CYPH = new KeyUsage(new Boolean[]{null, null, null, Boolean.TRUE, null, null, null, null, null});

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof KeyUsage)) {
            return false;
        }
        Boolean[] usage = ((KeyUsage) obj).getUsage();
        if (usage == null) {
            usage = EMPTY_USAGES.getUsage();
        }
        Boolean[] usage2 = getUsage();
        if (usage2 == null) {
            usage2 = EMPTY_USAGES.getUsage();
        }
        if (usage != null && usage2 != null && usage.length != usage2.length) {
            return false;
        }
        if (usage2 != null && usage != null) {
            for (int i = 0; i < usage2.length; i++) {
                if (usage[i] != usage2[i]) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public boolean includes(KeyUsage keyUsage) {
        if (keyUsage == null) {
            return false;
        }
        Boolean[] usage = keyUsage.getUsage();
        Boolean[] usage2 = getUsage();
        if (usage == null || usage2 == null || usage.length != usage2.length) {
            return false;
        }
        for (int i = 0; i < usage2.length; i++) {
            if (usage[i] != null && usage[i] != usage2[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int iHashCode = 0;
        if (this.usage != null) {
            for (Boolean bool : this.usage) {
                iHashCode += bool.hashCode();
            }
        }
        return iHashCode;
    }

    public KeyUsage(Boolean[] boolArr) {
        if (boolArr == null || boolArr.length != 9) {
            throw new IllegalArgumentException("El uso debe proporcionarse como un Boolean[] no nulo de exactamente 9 posiciones");
        }
        this.usage = (Boolean[]) boolArr.clone();
    }

    public KeyUsage(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            throw new IllegalArgumentException("El certificado de origen no puede ser nulo");
        }
        boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage == null) {
            this.usage = null;
            return;
        }
        if (keyUsage.length != 9) {
            throw new IllegalArgumentException("El certificado de origen tiene un KeyUsage con un numero de posiciones no soportado: " + keyUsage.length);
        }
        this.usage = new Boolean[9];
        for (int i = 0; i < 9; i++) {
            this.usage[i] = Boolean.valueOf(keyUsage[i]);
        }
    }

    Boolean[] getUsage() {
        if (this.usage == null) {
            return null;
        }
        return (Boolean[]) this.usage.clone();
    }

    public String toString() {
        if (this.usage == null) {
            return "Desconocido";
        }
        if (includes(SIGN) && includes(AUTH) && includes(CYPH)) {
            return "Firma, autenticación y cifrado";
        }
        if (includes(AUTH) && includes(CYPH)) {
            return "Autenticación y cifrado";
        }
        if (includes(SIGN) && includes(AUTH) && includes(CYPH)) {
            return "Firma y cifrado";
        }
        if (includes(SIGN) && includes(AUTH)) {
            return "Firma y autenticación";
        }
        if (includes(AUTH)) {
            return "Autenticación";
        }
        if (includes(SIGN)) {
            return "Firma";
        }
        if (includes(CYPH)) {
            return "Cifrado";
        }
        return "Otros";
    }
}
