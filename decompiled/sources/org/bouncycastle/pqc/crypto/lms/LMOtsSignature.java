package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMOtsSignature.class */
class LMOtsSignature implements Encodable {
    private final LMOtsParameters type;
    private final byte[] C;
    private final byte[] y;

    public LMOtsSignature(LMOtsParameters lMOtsParameters, byte[] bArr, byte[] bArr2) {
        this.type = lMOtsParameters;
        this.C = bArr;
        this.y = bArr2;
    }

    public static LMOtsSignature getInstance(Object obj) throws IOException {
        if (obj instanceof LMOtsSignature) {
            return (LMOtsSignature) obj;
        }
        if (obj instanceof DataInputStream) {
            LMOtsParameters parametersForType = LMOtsParameters.getParametersForType(((DataInputStream) obj).readInt());
            byte[] bArr = new byte[parametersForType.getN()];
            ((DataInputStream) obj).readFully(bArr);
            byte[] bArr2 = new byte[parametersForType.getP() * parametersForType.getN()];
            ((DataInputStream) obj).readFully(bArr2);
            return new LMOtsSignature(parametersForType, bArr, bArr2);
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
            LMOtsSignature lMOtsSignature = getInstance(dataInputStream2);
            if (dataInputStream2 != null) {
            }
            return lMOtsSignature;
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

    public LMOtsParameters getType() {
        return this.type;
    }

    public byte[] getC() {
        return this.C;
    }

    public byte[] getY() {
        return this.y;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LMOtsSignature lMOtsSignature = (LMOtsSignature) obj;
        if (this.type != null) {
            if (!this.type.equals(lMOtsSignature.type)) {
                return false;
            }
        } else if (lMOtsSignature.type != null) {
            return false;
        }
        if (Arrays.equals(this.C, lMOtsSignature.C)) {
            return Arrays.equals(this.y, lMOtsSignature.y);
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((31 * (this.type != null ? this.type.hashCode() : 0)) + Arrays.hashCode(this.C))) + Arrays.hashCode(this.y);
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return Composer.compose().u32str(this.type.getType()).bytes(this.C).bytes(this.y).build();
    }
}
