package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSPublicKeyParameters.class */
public class LMSPublicKeyParameters extends LMSKeyParameters implements LMSContextBasedVerifier {
    private final LMSigParameters parameterSet;
    private final LMOtsParameters lmOtsType;
    private final byte[] I;
    private final byte[] T1;

    public LMSPublicKeyParameters(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters, byte[] bArr, byte[] bArr2) {
        super(false);
        this.parameterSet = lMSigParameters;
        this.lmOtsType = lMOtsParameters;
        this.I = Arrays.clone(bArr2);
        this.T1 = Arrays.clone(bArr);
    }

    public static LMSPublicKeyParameters getInstance(Object obj) throws IOException {
        if (obj instanceof LMSPublicKeyParameters) {
            return (LMSPublicKeyParameters) obj;
        }
        if (obj instanceof DataInputStream) {
            LMSigParameters parametersForType = LMSigParameters.getParametersForType(((DataInputStream) obj).readInt());
            LMOtsParameters parametersForType2 = LMOtsParameters.getParametersForType(((DataInputStream) obj).readInt());
            byte[] bArr = new byte[16];
            ((DataInputStream) obj).readFully(bArr);
            byte[] bArr2 = new byte[parametersForType.getM()];
            ((DataInputStream) obj).readFully(bArr2);
            return new LMSPublicKeyParameters(parametersForType, parametersForType2, bArr2, bArr);
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
            LMSPublicKeyParameters lMSPublicKeyParameters = getInstance(dataInputStream2);
            if (dataInputStream2 != null) {
            }
            return lMSPublicKeyParameters;
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSKeyParameters, org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return toByteArray();
    }

    public LMSigParameters getSigParameters() {
        return this.parameterSet;
    }

    public LMOtsParameters getOtsParameters() {
        return this.lmOtsType;
    }

    public LMSParameters getLMSParameters() {
        return new LMSParameters(getSigParameters(), getOtsParameters());
    }

    public byte[] getT1() {
        return Arrays.clone(this.T1);
    }

    boolean matchesT1(byte[] bArr) {
        return Arrays.constantTimeAreEqual(this.T1, bArr);
    }

    public byte[] getI() {
        return Arrays.clone(this.I);
    }

    byte[] refI() {
        return this.I;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LMSPublicKeyParameters lMSPublicKeyParameters = (LMSPublicKeyParameters) obj;
        if (this.parameterSet.equals(lMSPublicKeyParameters.parameterSet) && this.lmOtsType.equals(lMSPublicKeyParameters.lmOtsType) && Arrays.areEqual(this.I, lMSPublicKeyParameters.I)) {
            return Arrays.areEqual(this.T1, lMSPublicKeyParameters.T1);
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * this.parameterSet.hashCode()) + this.lmOtsType.hashCode())) + Arrays.hashCode(this.I))) + Arrays.hashCode(this.T1);
    }

    byte[] toByteArray() {
        return Composer.compose().u32str(this.parameterSet.getType()).u32str(this.lmOtsType.getType()).bytes(this.I).bytes(this.T1).build();
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedVerifier
    public LMSContext generateLMSContext(byte[] bArr) {
        try {
            return generateOtsContext(LMSSignature.getInstance(bArr));
        } catch (IOException e) {
            throw new IllegalStateException("cannot parse signature: " + e.getMessage());
        }
    }

    LMSContext generateOtsContext(LMSSignature lMSSignature) {
        int type = getOtsParameters().getType();
        if (lMSSignature.getOtsSignature().getType().getType() != type) {
            throw new IllegalArgumentException("ots type from lsm signature does not match ots signature type from embedded ots signature");
        }
        return new LMOtsPublicKey(LMOtsParameters.getParametersForType(type), this.I, lMSSignature.getQ(), null).createOtsContext(lMSSignature);
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedVerifier
    public boolean verify(LMSContext lMSContext) {
        return LMS.verifySignature(this, lMSContext);
    }
}
