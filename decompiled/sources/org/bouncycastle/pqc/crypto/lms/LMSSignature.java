package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSSignature.class */
class LMSSignature implements Encodable {
    private final int q;
    private final LMOtsSignature otsSignature;
    private final LMSigParameters parameter;
    private final byte[][] y;

    public LMSSignature(int i, LMOtsSignature lMOtsSignature, LMSigParameters lMSigParameters, byte[][] bArr) {
        this.q = i;
        this.otsSignature = lMOtsSignature;
        this.parameter = lMSigParameters;
        this.y = bArr;
    }

    public static LMSSignature getInstance(Object obj) throws IOException {
        if (obj instanceof LMSSignature) {
            return (LMSSignature) obj;
        }
        if (obj instanceof DataInputStream) {
            int i = ((DataInputStream) obj).readInt();
            LMOtsSignature lMOtsSignature = LMOtsSignature.getInstance(obj);
            LMSigParameters parametersForType = LMSigParameters.getParametersForType(((DataInputStream) obj).readInt());
            byte[][] bArr = new byte[parametersForType.getH()][];
            for (int i2 = 0; i2 < bArr.length; i2++) {
                bArr[i2] = new byte[parametersForType.getM()];
                ((DataInputStream) obj).readFully(bArr[i2]);
            }
            return new LMSSignature(i, lMOtsSignature, parametersForType, bArr);
        }
        if (!(obj instanceof byte[])) {
            if (obj instanceof InputStream) {
                return getInstance(Streams.readAll((InputStream) obj));
            }
            throw new IllegalArgumentException("cannot parse " + obj);
        }
        DataInputStream dataInputStream = null;
        try {
            DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream((byte[]) obj));
            LMSSignature lMSSignature = getInstance(dataInputStream2);
            if (dataInputStream2 != null) {
            }
            return lMSSignature;
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LMSSignature lMSSignature = (LMSSignature) obj;
        if (this.q != lMSSignature.q) {
            return false;
        }
        if (this.otsSignature != null) {
            if (!this.otsSignature.equals(lMSSignature.otsSignature)) {
                return false;
            }
        } else if (lMSSignature.otsSignature != null) {
            return false;
        }
        if (this.parameter != null) {
            if (!this.parameter.equals(lMSSignature.parameter)) {
                return false;
            }
        } else if (lMSSignature.parameter != null) {
            return false;
        }
        return Arrays.deepEquals(this.y, lMSSignature.y);
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * this.q) + (this.otsSignature != null ? this.otsSignature.hashCode() : 0))) + (this.parameter != null ? this.parameter.hashCode() : 0))) + Arrays.deepHashCode(this.y);
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return Composer.compose().u32str(this.q).bytes(this.otsSignature.getEncoded()).u32str(this.parameter.getType()).bytes(this.y).build();
    }

    public int getQ() {
        return this.q;
    }

    public LMOtsSignature getOtsSignature() {
        return this.otsSignature;
    }

    public LMSigParameters getParameter() {
        return this.parameter;
    }

    public byte[][] getY() {
        return this.y;
    }
}
