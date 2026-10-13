package org.spongycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.EncryptedContentInfo;
import org.spongycastle.asn1.cms.EncryptedData;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSEncryptedDataGenerator.class */
public class CMSEncryptedDataGenerator extends CMSEncryptedGenerator {
    private CMSEncryptedData doGenerate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            OutputStream cOut = contentEncryptor.getOutputStream(bOut);
            content.write(cOut);
            cOut.close();
            byte[] encryptedContent = bOut.toByteArray();
            AlgorithmIdentifier encAlgId = contentEncryptor.getAlgorithmIdentifier();
            ASN1OctetString encContent = new BEROctetString(encryptedContent);
            EncryptedContentInfo eci = new EncryptedContentInfo(content.getContentType(), encAlgId, encContent);
            ASN1Set unprotectedAttrSet = null;
            if (this.unprotectedAttributeGenerator != null) {
                AttributeTable attrTable = this.unprotectedAttributeGenerator.getAttributes(new HashMap());
                unprotectedAttrSet = new BERSet(attrTable.toASN1EncodableVector());
            }
            ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.encryptedData, new EncryptedData(eci, unprotectedAttrSet));
            return new CMSEncryptedData(contentInfo);
        } catch (IOException e) {
            throw new CMSException("");
        }
    }

    public CMSEncryptedData generate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        return doGenerate(content, contentEncryptor);
    }
}
