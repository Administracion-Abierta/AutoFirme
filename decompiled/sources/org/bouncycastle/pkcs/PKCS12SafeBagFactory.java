package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.InputDecryptorProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/PKCS12SafeBagFactory.class */
public class PKCS12SafeBagFactory {
    private ASN1Sequence safeBagSeq;

    public PKCS12SafeBagFactory(ContentInfo contentInfo) {
        if (contentInfo.getContentType().equals((ASN1Primitive) PKCSObjectIdentifiers.encryptedData)) {
            throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
        }
        this.safeBagSeq = ASN1Sequence.getInstance(ASN1OctetString.getInstance(contentInfo.getContent()).getOctets());
    }

    public PKCS12SafeBagFactory(ContentInfo contentInfo, InputDecryptorProvider inputDecryptorProvider) throws PKCSException {
        if (!contentInfo.getContentType().equals((ASN1Primitive) PKCSObjectIdentifiers.encryptedData)) {
            throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
        }
        try {
            this.safeBagSeq = ASN1Sequence.getInstance(new CMSEncryptedData(org.bouncycastle.asn1.cms.ContentInfo.getInstance(contentInfo)).getContent(inputDecryptorProvider));
        } catch (CMSException e) {
            throw new PKCSException("unable to extract data: " + e.getMessage(), e);
        }
    }

    public PKCS12SafeBag[] getSafeBags() {
        PKCS12SafeBag[] pKCS12SafeBagArr = new PKCS12SafeBag[this.safeBagSeq.size()];
        for (int i = 0; i != this.safeBagSeq.size(); i++) {
            pKCS12SafeBagArr[i] = new PKCS12SafeBag(SafeBag.getInstance(this.safeBagSeq.getObjectAt(i)));
        }
        return pKCS12SafeBagArr;
    }
}
