package es.gob.afirma.signers.cms;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.ContainsNoDataException;
import es.gob.afirma.signers.pkcs7.P7ContentSignerParameters;
import es.gob.afirma.signers.pkcs7.SigUtils;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.ASN1UTCTime;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERPrintableString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerIdentifier;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.style.RFC4519Style;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.TBSCertificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cms/CoSigner.class */
final class CoSigner {
    private ASN1Set signedAttr2;

    CoSigner() {
    }

    byte[] coSigner(P7ContentSignerParameters p7ContentSignerParameters, byte[] bArr, boolean z, String str, PrivateKey privateKey, Certificate[] certificateArr, Map<String, byte[]> map, Map<String, byte[]> map2, byte[] bArr2) throws NoSuchAlgorithmException, IOException, CertificateException {
        ASN1Set aSN1SetGenerateSignerInfoFromHash;
        String oid;
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        Throwable th = null;
        try {
            try {
                ASN1Sequence aSN1Sequence = (ASN1Sequence) aSN1InputStream.readObject();
                if (aSN1InputStream != null) {
                    if (0 != 0) {
                        try {
                            aSN1InputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        aSN1InputStream.close();
                    }
                }
                Enumeration objects = aSN1Sequence.getObjects();
                objects.nextElement();
                SignedData signedData = SignedData.getInstance((ASN1Sequence) ((ASN1TaggedObject) objects.nextElement()).getObject());
                byte[] content = p7ContentSignerParameters.getContent();
                ContentInfo contentInfo = CmsUtil.getContentInfo(content, z, str);
                ASN1Set aSN1SetFillRestCerts = null;
                ASN1Set certificates = signedData.getCertificates();
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                Enumeration objects2 = certificates.getObjects();
                while (objects2.hasMoreElements()) {
                    aSN1EncodableVector.add((ASN1Encodable) objects2.nextElement());
                }
                if (certificateArr.length != 0) {
                    ArrayList arrayList = new ArrayList();
                    for (Certificate certificate : certificateArr) {
                        arrayList.add(org.spongycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(certificate.getEncoded())));
                    }
                    aSN1SetFillRestCerts = SigUtils.fillRestCerts(arrayList, aSN1EncodableVector);
                }
                String signatureAlgorithm = p7ContentSignerParameters.getSignatureAlgorithm();
                String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(signatureAlgorithm);
                AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
                TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificateArr[0]).getTBSCertificate()));
                SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue()));
                if (bArr2 == null) {
                    aSN1SetGenerateSignerInfoFromHash = generateSignerInfo(digestAlgorithmName, content != null ? content : p7ContentSignerParameters.getContent(), str, map);
                } else {
                    aSN1SetGenerateSignerInfoFromHash = generateSignerInfoFromHash((X509Certificate) certificateArr[0], bArr2, str, map);
                }
                ASN1Set aSN1SetGenerateUnsignerInfo = generateUnsignerInfo(map2);
                if (signatureAlgorithm.contains("withRSA")) {
                    oid = AOAlgorithmID.getOID("RSA");
                } else {
                    oid = AOAlgorithmID.getOID(signatureAlgorithm);
                }
                AlgorithmIdentifier algorithmIdentifierMakeAlgId2 = SigUtils.makeAlgId(oid);
                ASN1Set signerInfos = signedData.getSignerInfos();
                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                for (int i = 0; i < signerInfos.size(); i++) {
                    aSN1EncodableVector2.add(SignerInfo.getInstance(signerInfos.getObjectAt(i)));
                }
                try {
                    aSN1EncodableVector2.add(new SignerInfo(signerIdentifier, algorithmIdentifierMakeAlgId, aSN1SetGenerateSignerInfoFromHash, algorithmIdentifierMakeAlgId2, CmsUtil.firma(signatureAlgorithm, privateKey, this.signedAttr2), aSN1SetGenerateUnsignerInfo));
                    return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(signedData.getDigestAlgorithms(), contentInfo, aSN1SetFillRestCerts, null, new DERSet(aSN1EncodableVector2))).getEncoded("DER");
                } catch (Exception e) {
                    throw new IOException("Error al generar la firma: " + e, e);
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (aSN1InputStream != null) {
                if (th != null) {
                    try {
                        aSN1InputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    aSN1InputStream.close();
                }
            }
            throw th4;
        }
    }

    byte[] coSigner(String str, X509Certificate[] x509CertificateArr, byte[] bArr, String str2, PrivateKey privateKey, Map<String, byte[]> map, Map<String, byte[]> map2, byte[] bArr2) throws ContainsNoDataException, NoSuchAlgorithmException, IOException, CertificateException {
        String oid;
        ASN1Set aSN1SetGenerateSignerInfoFromHash;
        byte[] octets = bArr2 != null ? (byte[]) bArr2.clone() : null;
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        Throwable th = null;
        try {
            try {
                ASN1Sequence aSN1Sequence = (ASN1Sequence) aSN1InputStream.readObject();
                if (aSN1InputStream != null) {
                    if (0 != 0) {
                        try {
                            aSN1InputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        aSN1InputStream.close();
                    }
                }
                Enumeration objects = aSN1Sequence.getObjects();
                objects.nextElement();
                SignedData signedData = SignedData.getInstance((ASN1Sequence) ((ASN1TaggedObject) objects.nextElement()).getObject());
                ContentInfo encapContentInfo = signedData.getEncapContentInfo();
                DEROctetString dEROctetString = (DEROctetString) encapContentInfo.getContent();
                byte[] dataFromInputStream = null;
                if (dEROctetString != null) {
                    dataFromInputStream = AOUtil.getDataFromInputStream(dEROctetString.getOctetStream());
                }
                ASN1Set aSN1SetFillRestCerts = null;
                ASN1Set certificates = signedData.getCertificates();
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                Enumeration objects2 = certificates.getObjects();
                while (objects2.hasMoreElements()) {
                    aSN1EncodableVector.add((ASN1Encodable) objects2.nextElement());
                }
                if (x509CertificateArr.length != 0) {
                    ArrayList arrayList = new ArrayList();
                    for (X509Certificate x509Certificate : x509CertificateArr) {
                        arrayList.add(org.spongycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(x509Certificate.getEncoded())));
                    }
                    aSN1SetFillRestCerts = SigUtils.fillRestCerts(arrayList, aSN1EncodableVector);
                }
                String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
                AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
                TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(x509CertificateArr[0].getTBSCertificate()));
                SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue()));
                ASN1Set aSN1SetGenerateUnsignerInfo = generateUnsignerInfo(map2);
                if (str.contains("withRSA")) {
                    oid = AOAlgorithmID.getOID("RSA");
                } else {
                    oid = AOAlgorithmID.getOID(str);
                }
                AlgorithmIdentifier algorithmIdentifierMakeAlgId2 = SigUtils.makeAlgId(oid);
                ASN1Set signerInfos = signedData.getSignerInfos();
                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                for (int i = 0; i < signerInfos.size(); i++) {
                    SignerInfo signerInfo = SignerInfo.getInstance(signerInfos.getObjectAt(i));
                    if (signerInfo.getDigestAlgorithm().getAlgorithm().toString().equals(AOAlgorithmID.getOID(digestAlgorithmName))) {
                        ASN1Set authenticatedAttributes = signerInfo.getAuthenticatedAttributes();
                        for (int i2 = 0; i2 < authenticatedAttributes.size(); i2++) {
                            ASN1Sequence aSN1Sequence2 = (ASN1Sequence) authenticatedAttributes.getObjectAt(i2);
                            if (CMSAttributes.messageDigest.getId().toString().equals(((ASN1ObjectIdentifier) aSN1Sequence2.getObjectAt(0)).toString())) {
                                octets = ((DEROctetString) ((DERSet) aSN1Sequence2.getObjectAt(1)).getObjectAt(0)).getOctets();
                            }
                        }
                    }
                    aSN1EncodableVector2.add(signerInfo);
                }
                if (dataFromInputStream != null) {
                    aSN1SetGenerateSignerInfoFromHash = generateSignerInfo(digestAlgorithmName, dataFromInputStream, str2, map);
                } else if (octets != null) {
                    aSN1SetGenerateSignerInfoFromHash = generateSignerInfoFromHash(x509CertificateArr[0], octets, str2, map);
                } else {
                    throw new ContainsNoDataException("No se puede crear la cofirma ya que no se han encontrado ni los datos firmados ni una huella digital compatible con el algoritmo de firma");
                }
                try {
                    aSN1EncodableVector2.add(new SignerInfo(signerIdentifier, algorithmIdentifierMakeAlgId, aSN1SetGenerateSignerInfoFromHash, algorithmIdentifierMakeAlgId2, CmsUtil.firma(str, privateKey, this.signedAttr2), aSN1SetGenerateUnsignerInfo));
                    return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(signedData.getDigestAlgorithms(), encapContentInfo, aSN1SetFillRestCerts, null, new DERSet(aSN1EncodableVector2))).getEncoded("DER");
                } catch (Exception e) {
                    throw new IOException("Error al generar la firma: " + e, e);
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (aSN1InputStream != null) {
                if (th != null) {
                    try {
                        aSN1InputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    aSN1InputStream.close();
                }
            }
            throw th4;
        }
    }

    private ASN1Set generateSignerInfo(String str, byte[] bArr, String str2, Map<String, byte[]> map) throws NoSuchAlgorithmException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new Attribute(CMSAttributes.contentType, new DERSet(new ASN1ObjectIdentifier(str2))));
        aSN1EncodableVector.add(new Attribute(CMSAttributes.signingTime, new DERSet(new ASN1UTCTime(new Date()))));
        aSN1EncodableVector.add(new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString((byte[]) MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(str)).digest(bArr).clone()))));
        if (map.size() != 0) {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
        }
        this.signedAttr2 = SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
    }

    private ASN1Set generateSignerInfoFromHash(X509Certificate x509Certificate, byte[] bArr, String str, Map<String, byte[]> map) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new Attribute(CMSAttributes.contentType, new DERSet(new ASN1ObjectIdentifier(str))));
        aSN1EncodableVector.add(new Attribute(CMSAttributes.signingTime, new DERSet(new ASN1UTCTime(new Date()))));
        aSN1EncodableVector.add(new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(bArr))));
        aSN1EncodableVector.add(new Attribute(RFC4519Style.serialNumber, new DERSet(new DERPrintableString(x509Certificate.getSerialNumber().toString()))));
        if (map.size() != 0) {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
        }
        this.signedAttr2 = SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
    }

    private static ASN1Set generateUnsignerInfo(Map<String, byte[]> map) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (map.size() != 0) {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
            return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        }
        return null;
    }
}
