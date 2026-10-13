package org.spongycastle.cms;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.spongycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.spongycastle.asn1.cms.OriginatorPublicKey;
import org.spongycastle.asn1.cms.RecipientInfo;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/KeyAgreeRecipientInfoGenerator.class */
public abstract class KeyAgreeRecipientInfoGenerator implements RecipientInfoGenerator {
    private ASN1ObjectIdentifier keyAgreementOID;
    private ASN1ObjectIdentifier keyEncryptionOID;
    private SubjectPublicKeyInfo originatorKeyInfo;

    protected abstract ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, GenericKey genericKey) throws CMSException;

    protected abstract byte[] getUserKeyingMaterial(AlgorithmIdentifier algorithmIdentifier) throws CMSException;

    protected KeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier keyAgreementOID, SubjectPublicKeyInfo originatorKeyInfo, ASN1ObjectIdentifier keyEncryptionOID) {
        this.originatorKeyInfo = originatorKeyInfo;
        this.keyAgreementOID = keyAgreementOID;
        this.keyEncryptionOID = keyEncryptionOID;
    }

    @Override // org.spongycastle.cms.RecipientInfoGenerator
    public RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        AlgorithmIdentifier keyEncAlg;
        OriginatorIdentifierOrKey originator = new OriginatorIdentifierOrKey(createOriginatorPublicKey(this.originatorKeyInfo));
        if (CMSUtils.isDES(this.keyEncryptionOID.getId()) || this.keyEncryptionOID.equals(PKCSObjectIdentifiers.id_alg_CMSRC2wrap)) {
            keyEncAlg = new AlgorithmIdentifier(this.keyEncryptionOID, DERNull.INSTANCE);
        } else if (CMSUtils.isGOST(this.keyAgreementOID)) {
            keyEncAlg = new AlgorithmIdentifier(this.keyEncryptionOID, new Gost2814789KeyWrapParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet));
        } else {
            keyEncAlg = new AlgorithmIdentifier(this.keyEncryptionOID);
        }
        AlgorithmIdentifier keyAgreeAlg = new AlgorithmIdentifier(this.keyAgreementOID, keyEncAlg);
        ASN1Sequence recipients = generateRecipientEncryptedKeys(keyAgreeAlg, keyEncAlg, contentEncryptionKey);
        byte[] userKeyingMaterial = getUserKeyingMaterial(keyAgreeAlg);
        if (userKeyingMaterial != null) {
            return new RecipientInfo(new KeyAgreeRecipientInfo(originator, new DEROctetString(userKeyingMaterial), keyAgreeAlg, recipients));
        }
        return new RecipientInfo(new KeyAgreeRecipientInfo(originator, null, keyAgreeAlg, recipients));
    }

    protected OriginatorPublicKey createOriginatorPublicKey(SubjectPublicKeyInfo originatorKeyInfo) {
        return new OriginatorPublicKey(new AlgorithmIdentifier(originatorKeyInfo.getAlgorithm().getAlgorithm(), DERNull.INSTANCE), originatorKeyInfo.getPublicKeyData().getBytes());
    }
}
