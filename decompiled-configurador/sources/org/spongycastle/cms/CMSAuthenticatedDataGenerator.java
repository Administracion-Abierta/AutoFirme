package org.spongycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AuthenticatedData;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSAuthenticatedDataGenerator.class */
public class CMSAuthenticatedDataGenerator extends CMSAuthenticatedGenerator {
    public CMSAuthenticatedData generate(CMSTypedData typedData, MacCalculator macCalculator) throws CMSException {
        return generate(typedData, macCalculator, null);
    }

    public CMSAuthenticatedData generate(CMSTypedData typedData, MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        AuthenticatedData authData;
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
            recipientInfos.add(recipient.generate(macCalculator.getKey()));
        }
        if (digestCalculator != null) {
            try {
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                OutputStream out = new TeeOutputStream(digestCalculator.getOutputStream(), bOut);
                typedData.write(out);
                out.close();
                ASN1OctetString encContent = new BEROctetString(bOut.toByteArray());
                Map parameters = getBaseParameters(typedData.getContentType(), digestCalculator.getAlgorithmIdentifier(), macCalculator.getAlgorithmIdentifier(), digestCalculator.getDigest());
                if (this.authGen == null) {
                    this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
                }
                ASN1Set authed = new DERSet(this.authGen.getAttributes(Collections.unmodifiableMap(parameters)).toASN1EncodableVector());
                try {
                    OutputStream mOut = macCalculator.getOutputStream();
                    mOut.write(authed.getEncoded(ASN1Encoding.DER));
                    mOut.close();
                    ASN1OctetString macResult = new DEROctetString(macCalculator.getMac());
                    ASN1Set unauthed = this.unauthGen != null ? new BERSet(this.unauthGen.getAttributes(Collections.unmodifiableMap(parameters)).toASN1EncodableVector()) : null;
                    ContentInfo eci = new ContentInfo(CMSObjectIdentifiers.data, encContent);
                    authData = new AuthenticatedData(this.originatorInfo, new DERSet(recipientInfos), macCalculator.getAlgorithmIdentifier(), digestCalculator.getAlgorithmIdentifier(), eci, authed, macResult, unauthed);
                } catch (IOException e) {
                    throw new CMSException("exception decoding algorithm parameters.", e);
                }
            } catch (IOException e2) {
                throw new CMSException("unable to perform digest calculation: " + e2.getMessage(), e2);
            }
        } else {
            try {
                ByteArrayOutputStream bOut2 = new ByteArrayOutputStream();
                OutputStream mOut2 = new TeeOutputStream(bOut2, macCalculator.getOutputStream());
                typedData.write(mOut2);
                mOut2.close();
                ASN1OctetString encContent2 = new BEROctetString(bOut2.toByteArray());
                ASN1OctetString macResult2 = new DEROctetString(macCalculator.getMac());
                ASN1Set unauthed2 = this.unauthGen != null ? new BERSet(this.unauthGen.getAttributes(new HashMap()).toASN1EncodableVector()) : null;
                ContentInfo eci2 = new ContentInfo(CMSObjectIdentifiers.data, encContent2);
                authData = new AuthenticatedData(this.originatorInfo, new DERSet(recipientInfos), macCalculator.getAlgorithmIdentifier(), null, eci2, null, macResult2, unauthed2);
            } catch (IOException e3) {
                throw new CMSException("exception decoding algorithm parameters.", e3);
            }
        }
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.authenticatedData, authData);
        return new CMSAuthenticatedData(contentInfo, new DigestCalculatorProvider() { // from class: org.spongycastle.cms.CMSAuthenticatedDataGenerator.1
            @Override // org.spongycastle.operator.DigestCalculatorProvider
            public DigestCalculator get(AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException {
                return digestCalculator;
            }
        });
    }
}
