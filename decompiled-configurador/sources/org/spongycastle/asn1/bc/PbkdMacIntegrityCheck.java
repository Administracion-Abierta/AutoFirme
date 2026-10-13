package org.spongycastle.asn1.bc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.pkcs.KeyDerivationFunc;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bc/PbkdMacIntegrityCheck.class */
public class PbkdMacIntegrityCheck extends ASN1Object {
    private final AlgorithmIdentifier macAlgorithm;
    private final KeyDerivationFunc pbkdAlgorithm;
    private final ASN1OctetString mac;

    public PbkdMacIntegrityCheck(AlgorithmIdentifier macAlgorithm, KeyDerivationFunc pbkdAlgorithm, byte[] mac) {
        this.macAlgorithm = macAlgorithm;
        this.pbkdAlgorithm = pbkdAlgorithm;
        this.mac = new DEROctetString(Arrays.clone(mac));
    }

    private PbkdMacIntegrityCheck(ASN1Sequence seq) {
        this.macAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.pbkdAlgorithm = KeyDerivationFunc.getInstance(seq.getObjectAt(1));
        this.mac = ASN1OctetString.getInstance(seq.getObjectAt(2));
    }

    public static PbkdMacIntegrityCheck getInstance(Object o) {
        if (o instanceof PbkdMacIntegrityCheck) {
            return (PbkdMacIntegrityCheck) o;
        }
        if (o != null) {
            return new PbkdMacIntegrityCheck(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public KeyDerivationFunc getPbkdAlgorithm() {
        return this.pbkdAlgorithm;
    }

    public byte[] getMac() {
        return Arrays.clone(this.mac.getOctets());
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.macAlgorithm);
        v.add(this.pbkdAlgorithm);
        v.add(this.mac);
        return new DERSequence(v);
    }
}
