package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/TupleHash.class */
public class TupleHash implements Xof, Digest {
    private static final byte[] N_TUPLE_HASH = Strings.toByteArray("TupleHash");
    private final CSHAKEDigest cshake;
    private final int bitLength;
    private final int outputLength;
    private boolean firstOutput;

    public TupleHash(int i, byte[] bArr) {
        this(i, bArr, i * 2);
    }

    public TupleHash(int i, byte[] bArr, int i2) {
        this.cshake = new CSHAKEDigest(i, N_TUPLE_HASH, bArr);
        this.bitLength = i;
        this.outputLength = (i2 + 7) / 8;
        reset();
    }

    public TupleHash(TupleHash tupleHash) {
        this.cshake = new CSHAKEDigest(tupleHash.cshake);
        this.bitLength = this.cshake.fixedOutputLength;
        this.outputLength = (this.bitLength * 2) / 8;
        this.firstOutput = tupleHash.firstOutput;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "TupleHash" + this.cshake.getAlgorithmName().substring(6);
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.cshake.getByteLength();
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.outputLength;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) throws IllegalStateException {
        byte[] bArrEncode = XofUtils.encode(b);
        this.cshake.update(bArrEncode, 0, bArrEncode.length);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) throws IllegalStateException, DataLengthException {
        byte[] bArrEncode = XofUtils.encode(bArr, i, i2);
        this.cshake.update(bArrEncode, 0, bArrEncode.length);
    }

    private void wrapUp(int i) {
        byte[] bArrRightEncode = XofUtils.rightEncode(((long) i) * 8);
        this.cshake.update(bArrRightEncode, 0, bArrRightEncode.length);
        this.firstOutput = false;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        if (this.firstOutput) {
            wrapUp(getDigestSize());
        }
        int iDoFinal = this.cshake.doFinal(bArr, i, getDigestSize());
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doFinal(byte[] bArr, int i, int i2) {
        if (this.firstOutput) {
            wrapUp(getDigestSize());
        }
        int iDoFinal = this.cshake.doFinal(bArr, i, i2);
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doOutput(byte[] bArr, int i, int i2) {
        if (this.firstOutput) {
            wrapUp(0);
        }
        return this.cshake.doOutput(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.cshake.reset();
        this.firstOutput = true;
    }
}
