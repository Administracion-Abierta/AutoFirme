package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthenticatedDataGenerator.class */
public class CMSAuthenticatedDataGenerator extends CMSAuthenticatedGenerator {
    public CMSAuthenticatedData generate(CMSTypedData cMSTypedData, MacCalculator macCalculator) throws CMSException {
        return generate(cMSTypedData, macCalculator, null);
    }

    public CMSAuthenticatedData generate(CMSTypedData cMSTypedData, MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        AuthenticatedData authenticatedData;
        ASN1EncodableVector recipentInfos = CMSUtils.getRecipentInfos(macCalculator.getKey(), this.recipientInfoGenerators);
        if (digestCalculator != null) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TeeOutputStream teeOutputStream = new TeeOutputStream(digestCalculator.getOutputStream(), byteArrayOutputStream);
                cMSTypedData.write(teeOutputStream);
                teeOutputStream.close();
                BEROctetString bEROctetString = new BEROctetString(byteArrayOutputStream.toByteArray());
                Map mapUnmodifiableMap = Collections.unmodifiableMap(getBaseParameters(cMSTypedData.getContentType(), digestCalculator.getAlgorithmIdentifier(), macCalculator.getAlgorithmIdentifier(), digestCalculator.getDigest()));
                if (this.authGen == null) {
                    this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
                }
                DERSet dERSet = new DERSet(this.authGen.getAttributes(mapUnmodifiableMap).toASN1EncodableVector());
                try {
                    OutputStream outputStream = macCalculator.getOutputStream();
                    outputStream.write(dERSet.getEncoded("DER"));
                    outputStream.close();
                    DEROctetString dEROctetString = new DEROctetString(macCalculator.getMac());
                    authenticatedData = new AuthenticatedData(this.originatorInfo, new DERSet(recipentInfos), macCalculator.getAlgorithmIdentifier(), digestCalculator.getAlgorithmIdentifier(), new ContentInfo(cMSTypedData.getContentType(), bEROctetString), dERSet, dEROctetString, CMSUtils.getAttrBERSet(this.unauthGen));
                } catch (IOException e) {
                    throw new CMSException("unable to perform MAC calculation: " + e.getMessage(), e);
                }
            } catch (IOException e2) {
                throw new CMSException("unable to perform digest calculation: " + e2.getMessage(), e2);
            }
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                TeeOutputStream teeOutputStream2 = new TeeOutputStream(byteArrayOutputStream2, macCalculator.getOutputStream());
                cMSTypedData.write(teeOutputStream2);
                teeOutputStream2.close();
                BEROctetString bEROctetString2 = new BEROctetString(byteArrayOutputStream2.toByteArray());
                DEROctetString dEROctetString2 = new DEROctetString(macCalculator.getMac());
                authenticatedData = new AuthenticatedData(this.originatorInfo, new DERSet(recipentInfos), macCalculator.getAlgorithmIdentifier(), null, new ContentInfo(cMSTypedData.getContentType(), bEROctetString2), null, dEROctetString2, CMSUtils.getAttrBERSet(this.unauthGen));
            } catch (IOException e3) {
                throw new CMSException("unable to perform MAC calculation: " + e3.getMessage(), e3);
            }
        }
        return new CMSAuthenticatedData(new ContentInfo(CMSObjectIdentifiers.authenticatedData, authenticatedData), new DigestCalculatorProvider() { // from class: org.bouncycastle.cms.CMSAuthenticatedDataGenerator.1
            @Override // org.bouncycastle.operator.DigestCalculatorProvider
            public DigestCalculator get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                return digestCalculator;
            }
        });
    }
}
