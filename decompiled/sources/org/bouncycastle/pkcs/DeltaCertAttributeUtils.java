package org.bouncycastle.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.operator.ContentVerifierProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/DeltaCertAttributeUtils.class */
public class DeltaCertAttributeUtils {
    public static boolean isDeltaRequestSignatureValid(PKCS10CertificationRequest pKCS10CertificationRequest, ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        DeltaCertificateRequestAttributeValue deltaCertificateRequestAttributeValue = new DeltaCertificateRequestAttributeValue(pKCS10CertificationRequest.getAttributes(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.2"))[0]);
        Attribute[] attributes = pKCS10CertificationRequest.getAttributes(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.3"));
        CertificationRequestInfo certificationRequestInfo = pKCS10CertificationRequest.toASN1Structure().getCertificationRequestInfo();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(certificationRequestInfo.getVersion());
        aSN1EncodableVector.add(certificationRequestInfo.getSubject());
        aSN1EncodableVector.add(certificationRequestInfo.getSubjectPublicKeyInfo());
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        Enumeration objects = certificationRequestInfo.getAttributes().getObjects();
        while (objects.hasMoreElements()) {
            Attribute attribute = Attribute.getInstance(objects.nextElement());
            if (!attribute.getAttrType().equals((ASN1Primitive) new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.3"))) {
                aSN1EncodableVector2.add(attribute);
            }
        }
        aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) new DERSet(aSN1EncodableVector2)));
        ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
        aSN1EncodableVector3.add(new DERSequence(aSN1EncodableVector));
        aSN1EncodableVector3.add(deltaCertificateRequestAttributeValue.getSignatureAlgorithm());
        aSN1EncodableVector3.add(attributes[0].getAttributeValues()[0]);
        return new PKCS10CertificationRequest(CertificationRequest.getInstance(new DERSequence(aSN1EncodableVector3))).isSignatureValid(contentVerifierProvider);
    }
}
