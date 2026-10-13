package org.spongycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/NullDigest.class */
public class NullDigest implements Digest {
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "NULL";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.bOut.size();
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        this.bOut.write(in);
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        this.bOut.write(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        byte[] res = this.bOut.toByteArray();
        System.arraycopy(res, 0, out, outOff, res.length);
        reset();
        return res.length;
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.bOut.reset();
    }
}
