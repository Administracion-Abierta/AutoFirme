package es.gob.afirma.signers.multi.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSimpleSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.signers.cades.CAdESParameters;
import es.gob.afirma.signers.cades.CAdESUtils;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.SigUtils;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
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

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/CAdESCounterSigner.class */
final class CAdESCounterSigner {
    private final AOSimpleSigner ss;

    CAdESCounterSigner(AOSimpleSigner aOSimpleSigner) {
        this.ss = aOSimpleSigner != null ? aOSimpleSigner : new AOPkcs1Signer();
    }

    byte[] counterSign(String str, byte[] bArr, CounterSignTarget counterSignTarget, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
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
                Object objNextElement = objects.nextElement();
                if (!(objNextElement instanceof ASN1ObjectIdentifier) && ((ASN1ObjectIdentifier) objNextElement).equals(PKCSObjectIdentifiers.signedData)) {
                    throw new AOFormatFileException("No se ha encontrado un SignedData en los datos a contrafirmar");
                }
                SignedData signedData = SignedData.getInstance(((ASN1TaggedObject) objects.nextElement()).getObject());
                return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(signedData.getDigestAlgorithms(), signedData.getEncapContentInfo(), CAdESMultiUtil.addCertificates(signedData, certificateArr), signedData.getCRLs(), new DERSet(counterSignSignerInfos(signedData.getSignerInfos(), str, privateKey, certificateArr, cAdESParameters, counterSignTarget)))).getEncoded("DER");
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

    private ASN1EncodableVector counterSignSignerInfos(ASN1Set aSN1Set, String str, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters, CounterSignTarget counterSignTarget) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < aSN1Set.size(); i++) {
            aSN1EncodableVector.add(counterSignSignerInfo(SignerInfo.getInstance(aSN1Set.getObjectAt(i)), str, privateKey, certificateArr, cAdESParameters, counterSignTarget));
        }
        return aSN1EncodableVector;
    }

    private SignerInfo counterSignSignerInfo(SignerInfo signerInfo, String str, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters, CounterSignTarget counterSignTarget) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ArrayList arrayList = new ArrayList();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        boolean z = true;
        if (signerInfo.getUnauthenticatedAttributes() != null) {
            Properties extraParams = cAdESParameters.getExtraParams();
            Enumeration objects = signerInfo.getUnauthenticatedAttributes().getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                String property = extraParams != null ? extraParams.getProperty("allowSignLTSignature") : null;
                if (property == null || !Boolean.parseBoolean(property)) {
                    try {
                        CAdESMultiUtil.checkUnsupported(attribute.getAttrType());
                    } catch (SigningLTSException e) {
                        if (property != null) {
                            throw new AOException(e.getMessage());
                        }
                        throw e;
                    }
                }
                if (CAdESMultiUtil.isCounterSignature(attribute.getAttrType())) {
                    z = false;
                    Iterator<SignerInfo> it = getSignerInfoFromUnauthenticatedAttributes(attribute).iterator();
                    while (it.hasNext()) {
                        aSN1EncodableVector.add(counterSignSignerInfo(it.next(), str, privateKey, certificateArr, cAdESParameters, counterSignTarget));
                    }
                } else {
                    arrayList.add(attribute);
                }
            }
        }
        if (CounterSignTarget.TREE.equals(counterSignTarget) || (CounterSignTarget.LEAFS.equals(counterSignTarget) && z)) {
            aSN1EncodableVector.add(signSignerInfo(signerInfo, str, privateKey, certificateArr, cAdESParameters));
        }
        arrayList.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector)));
        return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), new DERSet((ASN1Encodable[]) arrayList.toArray(new ASN1Encodable[arrayList.size()])));
    }

    private SignerInfo signSignerInfo(SignerInfo signerInfo, String str, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        cAdESParameters.setContentData(signerInfo.getEncryptedDigest().getOctets());
        cAdESParameters.setDataDigest(null);
        if ("advanced".equals(cAdESParameters.getProfileSet())) {
            cAdESParameters.setContentTypeOid(PKCSObjectIdentifiers.data.toString());
            cAdESParameters.setContentDescription(null);
        } else if (AOSignConstants.SIGN_PROFILE_BASELINE.equals(cAdESParameters.getProfileSet())) {
            cAdESParameters.setContentTypeOid(null);
            cAdESParameters.setContentDescription(null);
        }
        ASN1Set attributeSet = SigUtils.getAttributeSet(new AttributeTable(CAdESUtils.generateSignedAttributes(certificateArr[0], cAdESParameters, true)));
        DEROctetString dEROctetString = new DEROctetString(pkcs1Sign(attributeSet.getEncoded("DER"), str, privateKey, certificateArr, cAdESParameters.getExtraParams()));
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
        AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
        AlgorithmIdentifier algorithmIdentifierMakeAlgId2 = SigUtils.makeAlgId(AOAlgorithmID.getOID(AOSignConstants.composeSignatureAlgorithmName(digestAlgorithmName, certificateArr[0].getPublicKey().getAlgorithm())));
        TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificateArr[0]).getTBSCertificate()));
        return new SignerInfo(new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue())), algorithmIdentifierMakeAlgId, attributeSet, algorithmIdentifierMakeAlgId2, dEROctetString, (ASN1Set) null);
    }

    private static List<SignerInfo> getSignerInfoFromUnauthenticatedAttributes(Attribute attribute) {
        ArrayList arrayList = new ArrayList();
        Enumeration objects = attribute.getAttrValues().getObjects();
        while (objects.hasMoreElements()) {
            try {
                arrayList.add(SignerInfo.getInstance(objects.nextElement()));
            } catch (Exception e) {
            }
        }
        return arrayList;
    }

    private byte[] pkcs1Sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        try {
            return this.ss.sign(bArr, str, privateKey, certificateArr, properties);
        } catch (IOException e) {
            throw new AOException("Error en la firma PKCS#1 de la contrafirma CAdES: " + e, e);
        }
    }
}
