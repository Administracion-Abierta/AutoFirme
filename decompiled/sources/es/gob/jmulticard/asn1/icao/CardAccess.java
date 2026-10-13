package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.DigestAlgorithm;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/CardAccess.class */
public final class CardAccess {
    private final PaceAlgorithm paceAlgorithm;
    private final PaceAlgorithmParam paceAlgorithmParam;
    private final DigestAlgorithm paceDigestAlgorithm;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/CardAccess$PaceAlgorithm.class */
    public enum PaceAlgorithm {
        PACE_ECDH_GM_AES_CBC_CMAC_128(new byte[]{10, 4, 0, 127, 0, 7, 2, 2, 4, 2, 2}, 128),
        PACE_ECDH_GM_AES_CBC_CMAC_192(new byte[]{10, 4, 0, 127, 0, 7, 2, 2, 4, 2, 3}, 192),
        PACE_DH_GM_AES_CBC_CMAC_128(new byte[]{10, 4, 0, 127, 0, 7, 2, 2, 4, 1, 2}, 128);

        private final byte[] oidBytes;
        private final int keyLength;

        PaceAlgorithm(byte[] oid, int keyLen) {
            this.oidBytes = (byte[]) oid.clone();
            this.keyLength = keyLen;
        }

        public byte[] getBytes() {
            return (byte[]) this.oidBytes.clone();
        }

        public int getKeyLength() {
            return this.keyLength;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/CardAccess$PaceAlgorithmParam.class */
    public enum PaceAlgorithmParam {
        BRAINPOOL_256_R1(new byte[]{-124, 1, 13}, "brainpoolp256r1");

        private final byte[] paramBytes;
        private final String curveName;

        PaceAlgorithmParam(byte[] paramId, String curve) {
            this.paramBytes = (byte[]) paramId.clone();
            this.curveName = curve;
        }

        public byte[] getBytes() {
            return (byte[]) this.paramBytes.clone();
        }

        public String getCurveName() {
            return this.curveName;
        }
    }

    public CardAccess(PaceAlgorithm algorithm, PaceAlgorithmParam algorithmParam, DigestAlgorithm digestAlgorithm) {
        this.paceAlgorithm = algorithm;
        this.paceAlgorithmParam = algorithmParam;
        this.paceDigestAlgorithm = digestAlgorithm;
    }

    public PaceAlgorithm getPaceAlgorithm() {
        return this.paceAlgorithm;
    }

    public PaceAlgorithmParam getPaceAlgorithmParam() {
        return this.paceAlgorithmParam;
    }

    public DigestAlgorithm getPaceDigestAlgorithm() {
        return this.paceDigestAlgorithm;
    }
}
