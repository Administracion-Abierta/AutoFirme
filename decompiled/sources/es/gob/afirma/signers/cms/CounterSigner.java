package es.gob.afirma.signers.cms;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
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
import java.util.HashMap;
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
import org.spongycastle.asn1.BERSet;
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

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cms/CounterSigner.class */
final class CounterSigner {
    private ASN1Set signedAttr2;
    private int actualIndex = 0;
    private Map<String, byte[]> atrib2 = new HashMap();
    private Map<String, byte[]> uatrib2 = new HashMap();

    CounterSigner() {
    }

    byte[] counterSigner(P7ContentSignerParameters p7ContentSignerParameters, byte[] bArr, CounterSignTarget counterSignTarget, int[] iArr, PrivateKey privateKey, Certificate[] certificateArr, Map<String, byte[]> map, Map<String, byte[]> map2) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ASN1EncodableVector aSN1EncodableVectorCounterLeaf;
        this.atrib2 = map;
        this.uatrib2 = map2;
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        Throwable th = null;
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
            ASN1Set signerInfos = signedData.getSignerInfos();
            BERSet bERSet = null;
            ASN1Set certificates = signedData.getCertificates();
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            Enumeration objects2 = certificates.getObjects();
            while (objects2.hasMoreElements()) {
                aSN1EncodableVector.add((ASN1Encodable) objects2.nextElement());
            }
            if (certificateArr.length != 0) {
                aSN1EncodableVector.add(org.spongycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(certificateArr[0].getEncoded())));
                bERSet = new BERSet(aSN1EncodableVector);
            }
            new ASN1EncodableVector();
            if (CounterSignTarget.TREE.equals(counterSignTarget)) {
                aSN1EncodableVectorCounterLeaf = counterTree(signerInfos, p7ContentSignerParameters, privateKey, certificateArr);
            } else if (CounterSignTarget.LEAFS.equals(counterSignTarget)) {
                aSN1EncodableVectorCounterLeaf = counterLeaf(signerInfos, p7ContentSignerParameters, privateKey, certificateArr);
            } else {
                if (CounterSignTarget.NODES.equals(counterSignTarget) || counterSignTarget.equals(CounterSignTarget.SIGNERS)) {
                    SignedData signedData2 = signedData;
                    for (int length = iArr.length - 1; length >= 0; length--) {
                        ASN1InputStream aSN1InputStream2 = new ASN1InputStream(new SignedData(signedData.getDigestAlgorithms(), signedData.getEncapContentInfo(), bERSet, null, new DERSet(counterNode(signedData2, p7ContentSignerParameters, privateKey, certificateArr, iArr[length]))).getEncoded("DER"));
                        Throwable th3 = null;
                        try {
                            try {
                                ASN1Sequence aSN1Sequence2 = (ASN1Sequence) aSN1InputStream2.readObject();
                                if (aSN1InputStream2 != null) {
                                    if (0 != 0) {
                                        try {
                                            aSN1InputStream2.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        aSN1InputStream2.close();
                                    }
                                }
                                signedData2 = SignedData.getInstance(aSN1Sequence2);
                            } catch (Throwable th5) {
                                th3 = th5;
                                throw th5;
                            }
                        } catch (Throwable th6) {
                            if (aSN1InputStream2 != null) {
                                if (th3 != null) {
                                    try {
                                        aSN1InputStream2.close();
                                    } catch (Throwable th7) {
                                        th3.addSuppressed(th7);
                                    }
                                } else {
                                    aSN1InputStream2.close();
                                }
                            }
                            throw th6;
                        }
                    }
                    return new ContentInfo(PKCSObjectIdentifiers.signedData, signedData2).getEncoded("DER");
                }
                throw new IllegalArgumentException("Tipo de objetivo para la contrafirma no soportado: " + counterSignTarget);
            }
            return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(signedData.getDigestAlgorithms(), signedData.getEncapContentInfo(), bERSet, null, new DERSet(aSN1EncodableVectorCounterLeaf))).getEncoded("DER");
        } catch (Throwable th8) {
            if (aSN1InputStream != null) {
                if (0 != 0) {
                    try {
                        aSN1InputStream.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    aSN1InputStream.close();
                }
            }
            throw th8;
        }
    }

    private ASN1EncodableVector counterTree(ASN1Set aSN1Set, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < aSN1Set.size(); i++) {
            aSN1EncodableVector.add(getCounterUnsignedAtributes(SignerInfo.getInstance((ASN1Sequence) aSN1Set.getObjectAt(i)), p7ContentSignerParameters, privateKey, certificateArr));
        }
        return aSN1EncodableVector;
    }

    private ASN1EncodableVector counterLeaf(ASN1Set aSN1Set, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < aSN1Set.size(); i++) {
            aSN1EncodableVector.add(getCounterLeafUnsignedAtributes(SignerInfo.getInstance((ASN1Sequence) aSN1Set.getObjectAt(i)), p7ContentSignerParameters, privateKey, certificateArr));
        }
        return aSN1EncodableVector;
    }

    private ASN1EncodableVector counterNode(SignedData signedData, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr, int i) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ASN1Set signerInfos = signedData.getSignerInfos();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        this.actualIndex = 0;
        for (int i2 = 0; i2 < signerInfos.size(); i2++) {
            SignerInfo signerInfo = SignerInfo.getInstance((ASN1Sequence) signerInfos.getObjectAt(i2));
            SignerInfo counterNodeUnsignedAtributes = null;
            if (this.actualIndex == i) {
                counterNodeUnsignedAtributes = getCounterNodeUnsignedAtributes(signerInfo, p7ContentSignerParameters, privateKey, certificateArr);
            } else if (this.actualIndex != i) {
                counterNodeUnsignedAtributes = getCounterNodeUnsignedAtributes(signerInfo, p7ContentSignerParameters, privateKey, certificateArr, i);
            }
            this.actualIndex++;
            aSN1EncodableVector.add(counterNodeUnsignedAtributes);
        }
        return aSN1EncodableVector;
    }

    private SignerInfo getCounterUnsignedAtributes(SignerInfo signerInfo, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ArrayList arrayList = new ArrayList();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        if (signerInfo.getUnauthenticatedAttributes() != null) {
            Enumeration objects = signerInfo.getUnauthenticatedAttributes().getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                if (!attribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
                    Enumeration objects2 = attribute.getAttrValues().getObjects();
                    while (objects2.hasMoreElements()) {
                        Object objNextElement = objects2.nextElement();
                        if (objNextElement instanceof ASN1Sequence) {
                            aSN1EncodableVector.add(getCounterUnsignedAtributes(SignerInfo.getInstance((ASN1Sequence) objNextElement), p7ContentSignerParameters, privateKey, certificateArr));
                        } else {
                            arrayList.add(objNextElement);
                        }
                    }
                } else {
                    aSN1EncodableVector.add(attribute);
                }
            }
            aSN1EncodableVector.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
            ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
            if (aSN1EncodableVector.size() > 1) {
                for (int i = 0; i < aSN1EncodableVector.size(); i++) {
                    if (aSN1EncodableVector.get(i) instanceof Attribute) {
                        aSN1EncodableVector3.add(aSN1EncodableVector.get(i));
                    } else {
                        aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(i))));
                    }
                }
                return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector3)));
            }
            if (aSN1EncodableVector.size() == 1) {
                if (aSN1EncodableVector.get(0) instanceof Attribute) {
                    aSN1EncodableVector3.add(aSN1EncodableVector.get(0));
                    aSN1EncodableVector2.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
                    aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2)));
                } else {
                    aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(0))));
                }
                return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector3)));
            }
            return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), generateUnsignerInfoFromCounter(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector))));
        }
        aSN1EncodableVector2.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
        return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), generateUnsignerInfoFromCounter(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2))));
    }

    private SignerInfo getCounterLeafUnsignedAtributes(SignerInfo signerInfo, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ArrayList arrayList = new ArrayList();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        if (signerInfo.getUnauthenticatedAttributes() != null) {
            Enumeration objects = signerInfo.getUnauthenticatedAttributes().getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                if (!attribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
                    Enumeration objects2 = attribute.getAttrValues().getObjects();
                    while (objects2.hasMoreElements()) {
                        Object objNextElement = objects2.nextElement();
                        if (objNextElement instanceof ASN1Sequence) {
                            aSN1EncodableVector.add(getCounterLeafUnsignedAtributes(SignerInfo.getInstance(objNextElement), p7ContentSignerParameters, privateKey, certificateArr));
                        } else {
                            arrayList.add(objNextElement);
                        }
                    }
                } else {
                    aSN1EncodableVector.add(attribute);
                }
            }
            ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
            if (aSN1EncodableVector.size() > 1) {
                for (int i = 0; i < aSN1EncodableVector.size(); i++) {
                    if (aSN1EncodableVector.get(i) instanceof Attribute) {
                        aSN1EncodableVector3.add(aSN1EncodableVector.get(i));
                    } else {
                        aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(i))));
                    }
                }
                return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector3)));
            }
            if (aSN1EncodableVector.size() == 1) {
                if (aSN1EncodableVector.get(0) instanceof Attribute) {
                    aSN1EncodableVector3.add(aSN1EncodableVector.get(0));
                    aSN1EncodableVector2.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
                    aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2)));
                } else {
                    aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(0))));
                }
                return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector3)));
            }
            return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), generateUnsignerInfoFromCounter(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector))));
        }
        aSN1EncodableVector2.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
        return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), new DERSet(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2))));
    }

    private SignerInfo getCounterNodeUnsignedAtributes(SignerInfo signerInfo, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        ArrayList arrayList = new ArrayList();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        if (signerInfo.getUnauthenticatedAttributes() != null) {
            Enumeration objects = signerInfo.getUnauthenticatedAttributes().getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                if (!attribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
                    Enumeration objects2 = attribute.getAttrValues().getObjects();
                    while (objects2.hasMoreElements()) {
                        Object objNextElement = objects2.nextElement();
                        if (objNextElement instanceof ASN1Sequence) {
                            aSN1EncodableVector.add(SignerInfo.getInstance(objNextElement));
                        } else {
                            arrayList.add(objNextElement);
                        }
                    }
                } else {
                    aSN1EncodableVector.add(attribute);
                }
            }
            aSN1EncodableVector.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
            ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
            if (aSN1EncodableVector.size() > 1) {
                for (int i = 0; i < aSN1EncodableVector.size(); i++) {
                    if (aSN1EncodableVector.get(i) instanceof Attribute) {
                        aSN1EncodableVector3.add(aSN1EncodableVector.get(i));
                    } else {
                        aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(i))));
                    }
                }
                return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector3)));
            }
            if (aSN1EncodableVector.size() == 1) {
                if (aSN1EncodableVector.get(0) instanceof Attribute) {
                    aSN1EncodableVector3.add(aSN1EncodableVector.get(0));
                    aSN1EncodableVector2.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
                    aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2)));
                } else {
                    aSN1EncodableVector3.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(0))));
                }
                return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector3)));
            }
            return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), generateUnsignerInfoFromCounter(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector))));
        }
        aSN1EncodableVector2.add(unsignedAtributte(p7ContentSignerParameters, signerInfo, privateKey, certificateArr));
        return new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), new DERSet(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2))));
    }

    private SignerInfo getCounterNodeUnsignedAtributes(SignerInfo signerInfo, P7ContentSignerParameters p7ContentSignerParameters, PrivateKey privateKey, Certificate[] certificateArr, int i) throws NoSuchAlgorithmException, IOException, CertificateException, AOException {
        ArrayList arrayList = new ArrayList();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        SignerInfo signerInfo2 = null;
        if (signerInfo.getUnauthenticatedAttributes() != null) {
            Enumeration objects = signerInfo.getUnauthenticatedAttributes().getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                if (!attribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
                    Enumeration objects2 = attribute.getAttrValues().getObjects();
                    while (objects2.hasMoreElements()) {
                        Object objNextElement = objects2.nextElement();
                        if (objNextElement instanceof ASN1Sequence) {
                            SignerInfo signerInfo3 = SignerInfo.getInstance((ASN1Sequence) objNextElement);
                            this.actualIndex++;
                            if (this.actualIndex != i) {
                                if (this.actualIndex < i) {
                                    aSN1EncodableVector.add(getCounterNodeUnsignedAtributes(signerInfo3, p7ContentSignerParameters, privateKey, certificateArr, i));
                                } else {
                                    aSN1EncodableVector.add(signerInfo3);
                                }
                            } else {
                                aSN1EncodableVector.add(getCounterNodeUnsignedAtributes(signerInfo3, p7ContentSignerParameters, privateKey, certificateArr));
                            }
                        } else {
                            arrayList.add(objNextElement);
                        }
                    }
                } else {
                    aSN1EncodableVector.add(attribute);
                }
            }
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            if (aSN1EncodableVector.size() > 1) {
                for (int i2 = 0; i2 < aSN1EncodableVector.size(); i2++) {
                    if (aSN1EncodableVector.get(i2) instanceof Attribute) {
                        aSN1EncodableVector2.add(aSN1EncodableVector.get(i2));
                    } else {
                        aSN1EncodableVector2.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(i2))));
                    }
                }
                signerInfo2 = new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector2)));
            } else if (aSN1EncodableVector.size() == 1) {
                if (aSN1EncodableVector.get(0) instanceof Attribute) {
                    aSN1EncodableVector2.add(aSN1EncodableVector.get(0));
                } else {
                    aSN1EncodableVector2.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector.get(0))));
                }
                signerInfo2 = new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector2)));
            }
        } else {
            signerInfo2 = new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), (ASN1Set) null);
        }
        return signerInfo2;
    }

    private ASN1Set generateSignerInfo(X509Certificate x509Certificate, String str, byte[] bArr) throws NoSuchAlgorithmException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new Attribute(CMSAttributes.signingTime, new DERSet(new ASN1UTCTime(new Date()))));
        aSN1EncodableVector.add(new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(str)).digest(bArr)))));
        aSN1EncodableVector.add(new Attribute(RFC4519Style.serialNumber, new DERSet(new DERPrintableString(x509Certificate.getSerialNumber().toString()))));
        if (this.atrib2.size() != 0) {
            for (Map.Entry<String, byte[]> entry : this.atrib2.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
        }
        this.signedAttr2 = SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
    }

    private ASN1Set generateUnsignerInfo() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.uatrib2.size() != 0) {
            for (Map.Entry<String, byte[]> entry : this.uatrib2.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
            return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
        }
        return null;
    }

    private ASN1Set generateUnsignerInfoFromCounter(Attribute attribute) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.uatrib2.size() != 0) {
            for (Map.Entry<String, byte[]> entry : this.uatrib2.entrySet()) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(entry.getKey().toString()), new DERSet(new DERPrintableString(new String(entry.getValue())))));
            }
        }
        aSN1EncodableVector.add(attribute);
        return SigUtils.getAttributeSet(new AttributeTable(aSN1EncodableVector));
    }

    private SignerInfo unsignedAtributte(P7ContentSignerParameters p7ContentSignerParameters, SignerInfo signerInfo, PrivateKey privateKey, Certificate[] certificateArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        String oid;
        String signatureAlgorithm = p7ContentSignerParameters.getSignatureAlgorithm();
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(signatureAlgorithm);
        ASN1Set aSN1SetGenerateSignerInfo = generateSignerInfo((X509Certificate) certificateArr[0], digestAlgorithmName, signerInfo.getEncryptedDigest().getOctets());
        ASN1Set aSN1SetGenerateUnsignerInfo = generateUnsignerInfo();
        TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificateArr[0]).getTBSCertificate()));
        SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue()));
        AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
        if (signatureAlgorithm.contains("withRSA")) {
            oid = AOAlgorithmID.getOID("RSA");
        } else {
            oid = AOAlgorithmID.getOID(signatureAlgorithm);
        }
        try {
            return new SignerInfo(signerIdentifier, algorithmIdentifierMakeAlgId, aSN1SetGenerateSignerInfo, SigUtils.makeAlgId(oid), CmsUtil.firma(signatureAlgorithm, privateKey, this.signedAttr2), aSN1SetGenerateUnsignerInfo);
        } catch (Exception e) {
            throw new IOException("Error realizando la firma: " + e, e);
        }
    }
}
