package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/HSSSignature.class */
class HSSSignature implements Encodable {
    private final int lMinus1;
    private final LMSSignedPubKey[] signedPubKey;
    private final LMSSignature signature;

    public HSSSignature(int i, LMSSignedPubKey[] lMSSignedPubKeyArr, LMSSignature lMSSignature) {
        this.lMinus1 = i;
        this.signedPubKey = lMSSignedPubKeyArr;
        this.signature = lMSSignature;
    }

    public static HSSSignature getInstance(Object obj, int i) throws IOException {
        if (obj instanceof HSSSignature) {
            return (HSSSignature) obj;
        }
        if (obj instanceof DataInputStream) {
            int i2 = ((DataInputStream) obj).readInt();
            if (i2 != i - 1) {
                throw new IllegalStateException("nspk exceeded maxNspk");
            }
            LMSSignedPubKey[] lMSSignedPubKeyArr = new LMSSignedPubKey[i2];
            if (i2 != 0) {
                for (int i3 = 0; i3 < lMSSignedPubKeyArr.length; i3++) {
                    lMSSignedPubKeyArr[i3] = new LMSSignedPubKey(LMSSignature.getInstance(obj), LMSPublicKeyParameters.getInstance(obj));
                }
            }
            return new HSSSignature(i2, lMSSignedPubKeyArr, LMSSignature.getInstance(obj));
        }
        if (!(obj instanceof byte[])) {
            if (obj instanceof InputStream) {
                return getInstance(Streams.readAll((InputStream) obj), i);
            }
            throw new IllegalArgumentException("cannot parse " + obj);
        }
        DataInputStream dataInputStream = null;
        try {
            DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream((byte[]) obj));
            HSSSignature hSSSignature = getInstance(dataInputStream2, i);
            if (dataInputStream2 != null) {
            }
            return hSSSignature;
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

    public int getlMinus1() {
        return this.lMinus1;
    }

    public LMSSignedPubKey[] getSignedPubKey() {
        return this.signedPubKey;
    }

    public LMSSignature getSignature() {
        return this.signature;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HSSSignature hSSSignature = (HSSSignature) obj;
        return this.lMinus1 == hSSSignature.lMinus1 && Arrays.areEqual(this.signedPubKey, hSSSignature.signedPubKey) && Objects.areEqual(this.signature, hSSSignature.signature);
    }

    public int hashCode() {
        return (31 * ((31 * this.lMinus1) + Arrays.hashCode(this.signedPubKey))) + Objects.hashCode(this.signature);
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        Composer composerCompose = Composer.compose();
        composerCompose.u32str(this.lMinus1);
        if (this.signedPubKey != null) {
            for (LMSSignedPubKey lMSSignedPubKey : this.signedPubKey) {
                composerCompose.bytes(lMSSignedPubKey);
            }
        }
        composerCompose.bytes(this.signature);
        return composerCompose.build();
    }
}
