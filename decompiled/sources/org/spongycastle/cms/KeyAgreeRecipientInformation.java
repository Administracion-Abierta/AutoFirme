package org.spongycastle.cms;

import java.io.IOException;
import java.util.List;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.spongycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.spongycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.spongycastle.asn1.cms.OriginatorPublicKey;
import org.spongycastle.asn1.cms.RecipientEncryptedKey;
import org.spongycastle.asn1.cms.RecipientKeyIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/KeyAgreeRecipientInformation.class */
public class KeyAgreeRecipientInformation extends RecipientInformation {
    private KeyAgreeRecipientInfo info;
    private ASN1OctetString encryptedKey;

    static void readRecipientInfo(List infos, KeyAgreeRecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        RecipientId keyAgreeRecipientId;
        ASN1Sequence s = info.getRecipientEncryptedKeys();
        for (int i = 0; i < s.size(); i++) {
            RecipientEncryptedKey id = RecipientEncryptedKey.getInstance(s.getObjectAt(i));
            KeyAgreeRecipientIdentifier karid = id.getIdentifier();
            IssuerAndSerialNumber iAndSN = karid.getIssuerAndSerialNumber();
            if (iAndSN != null) {
                keyAgreeRecipientId = new KeyAgreeRecipientId(iAndSN.getName(), iAndSN.getSerialNumber().getValue());
            } else {
                RecipientKeyIdentifier rKeyID = karid.getRKeyID();
                keyAgreeRecipientId = new KeyAgreeRecipientId(rKeyID.getSubjectKeyIdentifier().getOctets());
            }
            RecipientId rid = keyAgreeRecipientId;
            infos.add(new KeyAgreeRecipientInformation(info, rid, id.getEncryptedKey(), messageAlgorithm, secureReadable, additionalData));
        }
    }

    KeyAgreeRecipientInformation(KeyAgreeRecipientInfo info, RecipientId rid, ASN1OctetString encryptedKey, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        super(info.getKeyEncryptionAlgorithm(), messageAlgorithm, secureReadable, additionalData);
        this.info = info;
        this.rid = rid;
        this.encryptedKey = encryptedKey;
    }

    private SubjectPublicKeyInfo getSenderPublicKeyInfo(AlgorithmIdentifier recKeyAlgId, OriginatorIdentifierOrKey originator) throws IOException, CMSException {
        OriginatorId origID;
        OriginatorPublicKey opk = originator.getOriginatorKey();
        if (opk != null) {
            return getPublicKeyInfoFromOriginatorPublicKey(recKeyAlgId, opk);
        }
        IssuerAndSerialNumber iAndSN = originator.getIssuerAndSerialNumber();
        if (iAndSN != null) {
            origID = new OriginatorId(iAndSN.getName(), iAndSN.getSerialNumber().getValue());
        } else {
            SubjectKeyIdentifier ski = originator.getSubjectKeyIdentifier();
            origID = new OriginatorId(ski.getKeyIdentifier());
        }
        return getPublicKeyInfoFromOriginatorId(origID);
    }

    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorPublicKey(AlgorithmIdentifier recKeyAlgId, OriginatorPublicKey originatorPublicKey) {
        SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(recKeyAlgId, originatorPublicKey.getPublicKey().getBytes());
        return pubInfo;
    }

    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorId(OriginatorId origID) throws CMSException {
        throw new CMSException("No support for 'originator' as IssuerAndSerialNumber or SubjectKeyIdentifier");
    }

    @Override // org.spongycastle.cms.RecipientInformation
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws IOException, CMSException {
        KeyAgreeRecipient agreeRecipient = (KeyAgreeRecipient) recipient;
        AlgorithmIdentifier recKeyAlgId = agreeRecipient.getPrivateKeyAlgorithmIdentifier();
        return ((KeyAgreeRecipient) recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, getSenderPublicKeyInfo(recKeyAlgId, this.info.getOriginator()), this.info.getUserKeyingMaterial(), this.encryptedKey.getOctets());
    }
}
