package org.spongycastle.asn1.bc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.spongycastle.asn1.x509.Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bc/EncryptedPrivateKeyData.class */
public class EncryptedPrivateKeyData extends ASN1Object {
    private final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
    private final Certificate[] certificateChain;

    public EncryptedPrivateKeyData(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, Certificate[] certificateChain) {
        this.encryptedPrivateKeyInfo = encryptedPrivateKeyInfo;
        this.certificateChain = new Certificate[certificateChain.length];
        System.arraycopy(certificateChain, 0, this.certificateChain, 0, certificateChain.length);
    }

    private EncryptedPrivateKeyData(ASN1Sequence seq) {
        this.encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(seq.getObjectAt(0));
        ASN1Sequence certSeq = ASN1Sequence.getInstance(seq.getObjectAt(1));
        this.certificateChain = new Certificate[certSeq.size()];
        for (int i = 0; i != this.certificateChain.length; i++) {
            this.certificateChain[i] = Certificate.getInstance(certSeq.getObjectAt(i));
        }
    }

    public static EncryptedPrivateKeyData getInstance(Object o) {
        if (o instanceof EncryptedPrivateKeyData) {
            return (EncryptedPrivateKeyData) o;
        }
        if (o != null) {
            return new EncryptedPrivateKeyData(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public Certificate[] getCertificateChain() {
        Certificate[] tmp = new Certificate[this.certificateChain.length];
        System.arraycopy(this.certificateChain, 0, tmp, 0, this.certificateChain.length);
        return tmp;
    }

    public EncryptedPrivateKeyInfo getEncryptedPrivateKeyInfo() {
        return this.encryptedPrivateKeyInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.encryptedPrivateKeyInfo);
        v.add(new DERSequence(this.certificateChain));
        return new DERSequence(v);
    }
}
