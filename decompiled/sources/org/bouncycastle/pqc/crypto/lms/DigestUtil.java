package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/DigestUtil.class */
class DigestUtil {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/DigestUtil$WrapperDigest.class */
    static class WrapperDigest implements Digest {
        private final Digest digest;
        private final int length;

        WrapperDigest(Digest digest, int i) {
            this.digest = digest;
            this.length = i;
        }

        @Override // org.bouncycastle.crypto.Digest
        public String getAlgorithmName() {
            return this.digest.getAlgorithmName() + "/" + (this.length * 8);
        }

        @Override // org.bouncycastle.crypto.Digest
        public int getDigestSize() {
            return this.length;
        }

        @Override // org.bouncycastle.crypto.Digest
        public void update(byte b) {
            this.digest.update(b);
        }

        @Override // org.bouncycastle.crypto.Digest
        public void update(byte[] bArr, int i, int i2) {
            this.digest.update(bArr, i, i2);
        }

        @Override // org.bouncycastle.crypto.Digest
        public int doFinal(byte[] bArr, int i) {
            byte[] bArr2 = new byte[this.digest.getDigestSize()];
            this.digest.doFinal(bArr2, 0);
            System.arraycopy(bArr2, 0, bArr, i, this.length);
            return this.length;
        }

        @Override // org.bouncycastle.crypto.Digest
        public void reset() {
            this.digest.reset();
        }
    }

    DigestUtil() {
    }

    static Digest getDigest(LMOtsParameters lMOtsParameters) {
        return createDigest(lMOtsParameters.getDigestOID(), lMOtsParameters.getN());
    }

    static Digest getDigest(LMSigParameters lMSigParameters) {
        return createDigest(lMSigParameters.getDigestOID(), lMSigParameters.getM());
    }

    private static Digest createDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        Digest digestCreateDigest = createDigest(aSN1ObjectIdentifier);
        return (NISTObjectIdentifiers.id_shake256_len.equals((ASN1Primitive) aSN1ObjectIdentifier) || digestCreateDigest.getDigestSize() != i) ? new WrapperDigest(digestCreateDigest, i) : digestCreateDigest;
    }

    private static Digest createDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_shake256_len)) {
            return new SHAKEDigest(256);
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + aSN1ObjectIdentifier);
    }
}
