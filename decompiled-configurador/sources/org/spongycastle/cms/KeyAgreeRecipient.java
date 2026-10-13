package org.spongycastle.cms;

import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/KeyAgreeRecipient.class */
public interface KeyAgreeRecipient extends Recipient {
    RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, SubjectPublicKeyInfo subjectPublicKeyInfo, ASN1OctetString aSN1OctetString, byte[] bArr) throws CMSException;

    AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier();
}
