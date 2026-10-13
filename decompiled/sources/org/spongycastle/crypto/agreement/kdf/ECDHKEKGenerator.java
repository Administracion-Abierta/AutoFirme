package org.spongycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.DigestDerivationFunction;
import org.spongycastle.crypto.generators.KDF2BytesGenerator;
import org.spongycastle.crypto.params.KDFParameters;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/kdf/ECDHKEKGenerator.class */
public class ECDHKEKGenerator implements DigestDerivationFunction {
    private DigestDerivationFunction kdf;
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;

    public ECDHKEKGenerator(Digest digest) {
        this.kdf = new KDF2BytesGenerator(digest);
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters param) {
        DHKDFParameters params = (DHKDFParameters) param;
        this.algorithm = params.getAlgorithm();
        this.keySize = params.getKeySize();
        this.z = params.getZ();
    }

    @Override // org.spongycastle.crypto.DigestDerivationFunction
    public Digest getDigest() {
        return this.kdf.getDigest();
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new AlgorithmIdentifier(this.algorithm, DERNull.INSTANCE));
        v.add(new DERTaggedObject(true, 2, new DEROctetString(Pack.intToBigEndian(this.keySize))));
        try {
            this.kdf.init(new KDFParameters(this.z, new DERSequence(v).getEncoded("DER")));
            return this.kdf.generateBytes(out, outOff, len);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to initialise kdf: " + e.getMessage());
        }
    }
}
