package es.gob.afirma.signers.multi.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.cades.CAdESParameters;
import es.gob.afirma.signers.cades.CAdESUtils;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.ContainsNoDataException;
import es.gob.afirma.signers.pkcs7.SigUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
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
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSProcessableByteArray;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/CAdESCoSigner.class */
final class CAdESCoSigner {
    CAdESCoSigner() {
    }

    static byte[] coSigner(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        return coSigner(CAdESMultiUtil.readData(bArr), str, privateKey, certificateArr, cAdESParameters);
    }

    static byte[] coSigner(SignedData signedData, String str, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ContentInfo encapContentInfo;
        DEROctetString dEROctetString;
        if (cAdESParameters.getContentData() != null) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(PKCSObjectIdentifiers.data.getId());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                new CMSProcessableByteArray(cAdESParameters.getContentData()).write(byteArrayOutputStream);
                encapContentInfo = new ContentInfo(aSN1ObjectIdentifier, new BEROctetString(byteArrayOutputStream.toByteArray()));
            } catch (CMSException e) {
                throw new IOException("Error en la escritura del procesable CMS: " + e, e);
            }
        } else {
            encapContentInfo = signedData.getEncapContentInfo();
            if (cAdESParameters.getDataDigest() == null && (dEROctetString = (DEROctetString) encapContentInfo.getContent()) != null) {
                InputStream octetStream = dEROctetString.getOctetStream();
                Throwable th = null;
                try {
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(octetStream);
                    if (octetStream != null) {
                        if (0 != 0) {
                            try {
                                octetStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            octetStream.close();
                        }
                    }
                    cAdESParameters.setDataDigest(MessageDigest.getInstance(cAdESParameters.getDigestAlgorithm()).digest(dataFromInputStream));
                } catch (Throwable th3) {
                    if (octetStream != null) {
                        if (0 != 0) {
                            try {
                                octetStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            octetStream.close();
                        }
                    }
                    throw th3;
                }
            }
        }
        ASN1Set aSN1SetAddCertificates = CAdESMultiUtil.addCertificates(signedData, certificateArr);
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
        AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
        TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificateArr[0]).getTBSCertificate()));
        SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue()));
        AlgorithmIdentifier algorithmIdentifierMakeAlgId2 = SigUtils.makeAlgId(AOAlgorithmID.getOID(AOSignConstants.composeSignatureAlgorithmName(digestAlgorithmName, certificateArr[0].getPublicKey().getAlgorithm())));
        ASN1Set signerInfos = signedData.getSignerInfos();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < signerInfos.size(); i++) {
            SignerInfo signerInfo = SignerInfo.getInstance(signerInfos.getObjectAt(i));
            aSN1EncodableVector.add(signerInfo);
            if (cAdESParameters.getDataDigest() == null && signerInfo.getDigestAlgorithm().getAlgorithm().toString().equals(AOAlgorithmID.getOID(digestAlgorithmName))) {
                ASN1Set authenticatedAttributes = signerInfo.getAuthenticatedAttributes();
                for (int i2 = 0; i2 < authenticatedAttributes.size(); i2++) {
                    ASN1Sequence aSN1Sequence = (ASN1Sequence) authenticatedAttributes.getObjectAt(i2);
                    if (CMSAttributes.messageDigest.getId().equals(((ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(0)).toString())) {
                        cAdESParameters.setDataDigest(((DEROctetString) ((DERSet) aSN1Sequence.getObjectAt(1)).getObjectAt(0)).getOctets());
                    }
                }
            }
        }
        if (cAdESParameters.getDataDigest() == null && cAdESParameters.getContentData() == null) {
            throw new ContainsNoDataException("No se puede crear la cofirma ya que no se han encontrado ni los datos firmados ni una huella digital compatible con el algoritmo de firma");
        }
        ASN1Set attributeSet = SigUtils.getAttributeSet(new AttributeTable(CAdESUtils.generateSignedAttributes(certificateArr[0], cAdESParameters, false)));
        aSN1EncodableVector.add(new SignerInfo(signerIdentifier, algorithmIdentifierMakeAlgId, attributeSet, algorithmIdentifierMakeAlgId2, generateSignValue(attributeSet, str, privateKey, certificateArr, cAdESParameters.getExtraParams()), (ASN1Set) null));
        return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(signedData.getDigestAlgorithms(), encapContentInfo, aSN1SetAddCertificates, signedData.getCRLs(), new DERSet(aSN1EncodableVector))).getEncoded("DER");
    }

    private static ASN1OctetString generateSignValue(ASN1Set aSN1Set, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        try {
            return new DEROctetString(new AOPkcs1Signer().sign(aSN1Set.getEncoded("DER"), str, privateKey, certificateArr, properties));
        } catch (IOException e) {
            throw new AOException("Error al obtener los datos a firmar", e);
        }
    }
}
