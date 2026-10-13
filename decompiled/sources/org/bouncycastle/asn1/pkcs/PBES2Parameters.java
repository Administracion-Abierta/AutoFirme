package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/pkcs/PBES2Parameters.class */
public class PBES2Parameters extends ASN1Object implements PKCSObjectIdentifiers {
    private KeyDerivationFunc func;
    private EncryptionScheme scheme;

    public static PBES2Parameters getInstance(Object obj) {
        if (obj instanceof PBES2Parameters) {
            return (PBES2Parameters) obj;
        }
        if (obj != null) {
            return new PBES2Parameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PBES2Parameters(KeyDerivationFunc keyDerivationFunc, EncryptionScheme encryptionScheme) {
        this.func = keyDerivationFunc;
        this.scheme = encryptionScheme;
    }

    private PBES2Parameters(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(((ASN1Encodable) objects.nextElement()).toASN1Primitive());
        if (aSN1Sequence2.getObjectAt(0).equals(id_PBKDF2)) {
            this.func = new KeyDerivationFunc(id_PBKDF2, PBKDF2Params.getInstance(aSN1Sequence2.getObjectAt(1)));
        } else {
            this.func = KeyDerivationFunc.getInstance(aSN1Sequence2);
        }
        this.scheme = EncryptionScheme.getInstance(objects.nextElement());
    }

    public KeyDerivationFunc getKeyDerivationFunc() {
        return this.func;
    }

    public EncryptionScheme getEncryptionScheme() {
        return this.scheme;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.func, this.scheme);
    }
}
