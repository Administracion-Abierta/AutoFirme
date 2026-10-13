package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.operator.OutputAEADEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthEnvelopedDataGenerator.class */
public class CMSAuthEnvelopedDataGenerator extends CMSAuthEnvelopedGenerator {
    private CMSAuthEnvelopedData doGenerate(CMSTypedData cMSTypedData, OutputAEADEncryptor outputAEADEncryptor) throws CMSException {
        ASN1EncodableVector recipentInfos = CMSUtils.getRecipentInfos(outputAEADEncryptor.getKey(), this.recipientInfoGenerators);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            OutputStream outputStream = outputAEADEncryptor.getOutputStream(byteArrayOutputStream);
            cMSTypedData.write(outputStream);
            ASN1Set aSN1SetProcessAuthAttrSet = CMSUtils.processAuthAttrSet(this.authAttrsGenerator, outputAEADEncryptor);
            outputStream.close();
            return new CMSAuthEnvelopedData(new ContentInfo(CMSObjectIdentifiers.authEnvelopedData, new AuthEnvelopedData(this.originatorInfo, new DERSet(recipentInfos), CMSUtils.getEncryptedContentInfo(cMSTypedData, outputAEADEncryptor, byteArrayOutputStream.toByteArray()), aSN1SetProcessAuthAttrSet, new DEROctetString(outputAEADEncryptor.getMAC()), CMSUtils.getAttrDLSet(this.unauthAttrsGenerator))));
        } catch (IOException e) {
            throw new CMSException("unable to process authenticated content: " + e.getMessage(), e);
        }
    }

    public CMSAuthEnvelopedData generate(CMSTypedData cMSTypedData, OutputAEADEncryptor outputAEADEncryptor) throws CMSException {
        return doGenerate(cMSTypedData, outputAEADEncryptor);
    }
}
