package es.gob.afirma.signers.cms;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.P7ContentSignerParameters;
import es.gob.afirma.signers.pkcs7.SigUtils;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
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
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.TBSCertificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cms/GenSignedData.class */
final class GenSignedData {
    private ASN1Set signedAttr2;

    GenSignedData() {
    }

    byte[] generateSignedData(P7ContentSignerParameters p7ContentSignerParameters, boolean z, boolean z2, String str, PrivateKey privateKey, Certificate[] certificateArr, Map<String, byte[]> map, Map<String, byte[]> map2, byte[] bArr) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        String oid;
        if (p7ContentSignerParameters == null) {
            throw new IllegalArgumentException("Los parametros no pueden ser nulos");
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        String signatureAlgorithm = p7ContentSignerParameters.getSignatureAlgorithm();
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(signatureAlgorithm);
        AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
        aSN1EncodableVector.add(algorithmIdentifierMakeAlgId);
        byte[] content = p7ContentSignerParameters.getContent();
        ContentInfo contentInfo = CmsUtil.getContentInfo(content, z, str);
        ASN1Set aSN1SetCreateBerSetFromList = null;
        if (certificateArr.length != 0) {
            ArrayList arrayList = new ArrayList();
            for (Certificate certificate : certificateArr) {
                arrayList.add(org.spongycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(certificate.getEncoded())));
            }
            aSN1SetCreateBerSetFromList = SigUtils.createBerSetFromList(arrayList);
        }
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificateArr[0]).getTBSCertificate()));
        SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue()));
        ASN1Set aSN1SetGenerateSignedInfo = generateSignedInfo(digestAlgorithmName, content, str, z2, map, bArr);
        ASN1Set aSN1SetGenerateUnsignedInfo = generateUnsignedInfo(map2);
        try {
            if (signatureAlgorithm.contains("withRSA")) {
                oid = AOAlgorithmID.getOID("RSA");
            } else {
                oid = AOAlgorithmID.getOID(signatureAlgorithm);
            }
            aSN1EncodableVector2.add(new SignerInfo(signerIdentifier, algorithmIdentifierMakeAlgId, aSN1SetGenerateSignedInfo, SigUtils.makeAlgId(oid), firma(signatureAlgorithm, privateKey), aSN1SetGenerateUnsignedInfo));
            return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(new DERSet(aSN1EncodableVector), contentInfo, aSN1SetCreateBerSetFromList, null, new DERSet(aSN1EncodableVector2))).getEncoded("DER");
        } catch (Exception e) {
            throw new IOException("Error de codificacion: " + e, e);
        }
    }

    private ASN1Set generateSignedInfo(String str, byte[] bArr, String str2, boolean z, Map<String, byte[]> map, byte[] bArr2) throws NoSuchAlgorithmException {
        byte[] bArrDigest;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new Attribute(CMSAttributes.contentType, new DERSet(new ASN1ObjectIdentifier(str2))));
        if (z) {
            aSN1EncodableVector.add(new Attribute(CMSAttributes.signingTime, new DERSet(new ASN1UTCTime(new Date()))));
        }
        if (bArr2 == null || bArr2.length < 1) {
            bArrDigest = MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(str)).digest(bArr);
        } else {
            bArrDigest = bArr2;
        }
        aSN1EncodableVector.add(new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString((byte[]) bArrDigest.clone()))));
        if (map.size() != 0) {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
        }
        this.signedAttr2 = SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
    }

    private static ASN1Set generateUnsignedInfo(Map<String, byte[]> map) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (map.size() != 0) {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
            return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        }
        return null;
    }

    private ASN1OctetString firma(String str, PrivateKey privateKey) throws AOException {
        try {
            Signature signature = Signature.getInstance(str);
            try {
                signature.initSign(privateKey);
                try {
                    signature.update(this.signedAttr2.getEncoded("DER"));
                    try {
                        return new DEROctetString(signature.sign());
                    } catch (Exception e) {
                        throw new AOException("Error durante el proceso de firma", e);
                    }
                } catch (Exception e2) {
                    throw new AOException("Error al configurar la informacion de firma o al obtener los atributos a firmar", e2);
                }
            } catch (Exception e3) {
                throw new AOException("Error al inicializar la firma con la clave privada", e3);
            }
        } catch (Exception e4) {
            throw new AOException("Error obteniendo la clase de firma para el algoritmo " + str, e4);
        }
    }
}
