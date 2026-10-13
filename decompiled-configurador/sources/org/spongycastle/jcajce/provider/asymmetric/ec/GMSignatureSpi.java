package org.spongycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SM3Digest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.signers.SM2Signer;
import org.spongycastle.jcajce.provider.asymmetric.util.DSABase;
import org.spongycastle.jcajce.provider.asymmetric.util.DSAEncoder;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/GMSignatureSpi.class */
public class GMSignatureSpi extends DSABase {
    GMSignatureSpi(Digest digest, DSA signer, DSAEncoder encoder) {
        super(digest, signer, encoder);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param = ECUtils.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = ECUtil.generatePrivateKeyParameter(privateKey);
        this.digest.reset();
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(param, this.appRandom));
        } else {
            this.signer.init(true, param);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/GMSignatureSpi$sm3WithSM2.class */
    public static class sm3WithSM2 extends GMSignatureSpi {
        public sm3WithSM2() {
            super(new SM3Digest(), new SM2Signer(), new StdDSAEncoder());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/GMSignatureSpi$StdDSAEncoder.class */
    private static class StdDSAEncoder implements DSAEncoder {
        private StdDSAEncoder() {
        }

        @Override // org.spongycastle.jcajce.provider.asymmetric.util.DSAEncoder
        public byte[] encode(BigInteger r, BigInteger s) throws IOException {
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new ASN1Integer(r));
            v.add(new ASN1Integer(s));
            return new DERSequence(v).getEncoded(ASN1Encoding.DER);
        }

        @Override // org.spongycastle.jcajce.provider.asymmetric.util.DSAEncoder
        public BigInteger[] decode(byte[] encoding) throws IOException {
            ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(encoding);
            if (s.size() != 2) {
                throw new IOException("malformed signature");
            }
            if (!Arrays.areEqual(encoding, s.getEncoded(ASN1Encoding.DER))) {
                throw new IOException("malformed signature");
            }
            BigInteger[] sig = {ASN1Integer.getInstance(s.getObjectAt(0)).getValue(), ASN1Integer.getInstance(s.getObjectAt(1)).getValue()};
            return sig;
        }
    }
}
