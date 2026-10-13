package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/pkcs/PBMAC1Params.class */
public class PBMAC1Params extends ASN1Object implements PKCSObjectIdentifiers {
    private AlgorithmIdentifier func;
    private AlgorithmIdentifier scheme;

    public static PBMAC1Params getInstance(Object obj) {
        if (obj instanceof PBMAC1Params) {
            return (PBMAC1Params) obj;
        }
        if (obj != null) {
            return new PBMAC1Params(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PBMAC1Params(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        this.func = algorithmIdentifier;
        this.scheme = algorithmIdentifier2;
    }

    private PBMAC1Params(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(((ASN1Encodable) objects.nextElement()).toASN1Primitive());
        if (aSN1Sequence2.getObjectAt(0).equals(id_PBKDF2)) {
            this.func = new AlgorithmIdentifier(id_PBKDF2, PBKDF2Params.getInstance(aSN1Sequence2.getObjectAt(1)));
        } else {
            this.func = AlgorithmIdentifier.getInstance(aSN1Sequence2);
        }
        this.scheme = AlgorithmIdentifier.getInstance(objects.nextElement());
    }

    public AlgorithmIdentifier getKeyDerivationFunc() {
        return this.func;
    }

    public AlgorithmIdentifier getMessageAuthScheme() {
        return this.scheme;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.func, this.scheme);
    }
}
