package org.spongycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.EncryptedContentInfo;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSEnvelopedDataGenerator.class */
public class CMSEnvelopedDataGenerator extends CMSEnvelopedGenerator {
    private CMSEnvelopedData doGenerate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        if (!this.oldRecipientInfoGenerators.isEmpty()) {
            throw new IllegalStateException("can only use addRecipientGenerator() with this method");
        }
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            OutputStream cOut = contentEncryptor.getOutputStream(bOut);
            content.write(cOut);
            cOut.close();
            byte[] encryptedContent = bOut.toByteArray();
            AlgorithmIdentifier encAlgId = contentEncryptor.getAlgorithmIdentifier();
            ASN1OctetString encContent = new BEROctetString(encryptedContent);
            GenericKey encKey = contentEncryptor.getKey();
            for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
                recipientInfos.add(recipient.generate(encKey));
            }
            EncryptedContentInfo eci = new EncryptedContentInfo(content.getContentType(), encAlgId, encContent);
            ASN1Set unprotectedAttrSet = null;
            if (this.unprotectedAttributeGenerator != null) {
                AttributeTable attrTable = this.unprotectedAttributeGenerator.getAttributes(new HashMap());
                unprotectedAttrSet = new BERSet(attrTable.toASN1EncodableVector());
            }
            ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.envelopedData, new EnvelopedData(this.originatorInfo, new DERSet(recipientInfos), eci, unprotectedAttrSet));
            return new CMSEnvelopedData(contentInfo);
        } catch (IOException e) {
            throw new CMSException("");
        }
    }

    public CMSEnvelopedData generate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        return doGenerate(content, contentEncryptor);
    }
}
