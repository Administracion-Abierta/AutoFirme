package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/HSSPublicKeyParameters.class */
public class HSSPublicKeyParameters extends LMSKeyParameters implements LMSContextBasedVerifier {
    private final int l;
    private final LMSPublicKeyParameters lmsPublicKey;

    public HSSPublicKeyParameters(int i, LMSPublicKeyParameters lMSPublicKeyParameters) {
        super(false);
        if (lMSPublicKeyParameters == null) {
            throw new NullPointerException("lmsPublicKey");
        }
        this.l = i;
        this.lmsPublicKey = lMSPublicKeyParameters;
    }

    public static HSSPublicKeyParameters getInstance(Object obj) throws IOException {
        if (obj instanceof HSSPublicKeyParameters) {
            return (HSSPublicKeyParameters) obj;
        }
        if (obj instanceof DataInputStream) {
            return new HSSPublicKeyParameters(((DataInputStream) obj).readInt(), LMSPublicKeyParameters.getInstance(obj));
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
            HSSPublicKeyParameters hSSPublicKeyParameters = getInstance(dataInputStream2);
            if (dataInputStream2 != null) {
            }
            return hSSPublicKeyParameters;
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

    public int getL() {
        return this.l;
    }

    public LMSPublicKeyParameters getLMSPublicKey() {
        return this.lmsPublicKey;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HSSPublicKeyParameters hSSPublicKeyParameters = (HSSPublicKeyParameters) obj;
        if (this.l != hSSPublicKeyParameters.l) {
            return false;
        }
        return this.lmsPublicKey.equals(hSSPublicKeyParameters.lmsPublicKey);
    }

    public int hashCode() {
        return (31 * this.l) + this.lmsPublicKey.hashCode();
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSKeyParameters, org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return Composer.compose().u32str(this.l).bytes(this.lmsPublicKey.getEncoded()).build();
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedVerifier
    public LMSContext generateLMSContext(byte[] bArr) {
        try {
            HSSSignature hSSSignature = HSSSignature.getInstance(bArr, getL());
            LMSSignedPubKey[] signedPubKey = hSSSignature.getSignedPubKey();
            return (signedPubKey.length != 0 ? signedPubKey[signedPubKey.length - 1].getPublicKey() : getLMSPublicKey()).generateOtsContext(hSSSignature.getSignature()).withSignedPublicKeys(signedPubKey);
        } catch (IOException e) {
            throw new IllegalStateException("cannot parse signature: " + e.getMessage());
        }
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedVerifier
    public boolean verify(LMSContext lMSContext) {
        boolean zVerifySignature = true;
        LMSSignedPubKey[] signedPubKeys = lMSContext.getSignedPubKeys();
        if (signedPubKeys.length != getL() - 1) {
            return false;
        }
        LMSPublicKeyParameters lMSPublicKey = getLMSPublicKey();
        for (int i = 0; i < signedPubKeys.length; i++) {
            zVerifySignature &= LMS.verifySignature(lMSPublicKey, signedPubKeys[i].getSignature(), signedPubKeys[i].getPublicKey().toByteArray());
            lMSPublicKey = signedPubKeys[i].getPublicKey();
        }
        return zVerifySignature & lMSPublicKey.verify(lMSContext);
    }
}
