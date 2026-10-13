package es.gob.jmulticard.card.cwa14890;

import java.security.PublicKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/cwa14890/Cwa14890PublicConstants.class */
public interface Cwa14890PublicConstants {
    byte[] getRefCCvCaPublicKey();

    byte[] getCCvCa();

    byte[] getChrCCvCa();

    byte[] getRefIccPrivateKey();

    byte[] getCCvIfd();

    byte[] getChrCCvIfd();

    PublicKey getCaComponentPublicKey();

    int getIfdKeyLength();
}
