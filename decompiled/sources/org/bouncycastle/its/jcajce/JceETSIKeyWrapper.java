package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.its.ETSIKeyWrapper;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JceETSIKeyWrapper.class */
public class JceETSIKeyWrapper implements ETSIKeyWrapper {
    private final ECPublicKey recipientKey;
    private final byte[] recipientHash;
    private final JcaJceHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JceETSIKeyWrapper$Builder.class */
    public static class Builder {
        private final ECPublicKey recipientKey;
        private final byte[] recipientHash;
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder(ECPublicKey eCPublicKey, byte[] bArr) {
            this.recipientKey = eCPublicKey;
            this.recipientHash = bArr;
        }

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String str) {
            this.helper = new NamedJcaJceHelper(str);
            return this;
        }

        public JceETSIKeyWrapper build() {
            return new JceETSIKeyWrapper(this.recipientKey, this.recipientHash, this.helper);
        }
    }

    private JceETSIKeyWrapper(ECPublicKey eCPublicKey, byte[] bArr, JcaJceHelper jcaJceHelper) {
        this.recipientKey = eCPublicKey;
        this.recipientHash = bArr;
        this.helper = jcaJceHelper;
    }

    @Override // org.bouncycastle.its.ETSIKeyWrapper
    public EncryptedDataEncryptionKey wrap(byte[] bArr) {
        try {
            Cipher cipherCreateCipher = this.helper.createCipher("ETSIKEMwithSHA256");
            cipherCreateCipher.init(3, this.recipientKey, new IESKEMParameterSpec(this.recipientHash, true));
            byte[] bArrWrap = cipherCreateCipher.wrap(new SecretKeySpec(bArr, "AES"));
            int fieldSize = (this.recipientKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
            int i = bArrWrap[0] == 4 ? (2 * fieldSize) + 1 : fieldSize + 1;
            ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(SubjectPublicKeyInfo.getInstance(this.recipientKey.getEncoded()).getAlgorithm().getParameters());
            EciesP256EncryptedKey eciesP256EncryptedKeyCreateEciesP256EncryptedKey = EciesP256EncryptedKey.builder().setV(EccP256CurvePoint.createEncodedPoint(Arrays.copyOfRange(bArrWrap, 0, i))).setC(Arrays.copyOfRange(bArrWrap, i, i + bArr.length)).setT(Arrays.copyOfRange(bArrWrap, i + bArr.length, bArrWrap.length)).createEciesP256EncryptedKey();
            if (aSN1ObjectIdentifier.equals((ASN1Primitive) SECObjectIdentifiers.secp256r1)) {
                return EncryptedDataEncryptionKey.eciesNistP256(eciesP256EncryptedKeyCreateEciesP256EncryptedKey);
            }
            if (aSN1ObjectIdentifier.equals((ASN1Primitive) TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
                return EncryptedDataEncryptionKey.eciesBrainpoolP256r1(eciesP256EncryptedKeyCreateEciesP256EncryptedKey);
            }
            throw new IllegalStateException("recipient key curve is not P-256 or Brainpool P256r1");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
