package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSUtils.class */
class CMSUtils {
    private static final Set<String> des = new HashSet();
    private static final Set mqvAlgs = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();

    CMSUtils() {
    }

    static boolean isMQV(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return mqvAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isEC(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ecAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isGOST(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return gostAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isRFC2631(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals((ASN1Primitive) PKCSObjectIdentifiers.id_alg_ESDH) || aSN1ObjectIdentifier.equals((ASN1Primitive) PKCSObjectIdentifiers.id_alg_SSDH);
    }

    static boolean isDES(String str) {
        return des.contains(Strings.toUpperCase(str));
    }

    static boolean isEquivalent(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        if (algorithmIdentifier == null || algorithmIdentifier2 == null || !algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        ASN1Encodable parameters2 = algorithmIdentifier2.getParameters();
        if (parameters != null) {
            return parameters.equals(parameters2) || (parameters.equals(DERNull.INSTANCE) && parameters2 == null);
        }
        return parameters2 == null || parameters2.equals(DERNull.INSTANCE);
    }

    static ContentInfo readContentInfo(byte[] bArr) throws CMSException {
        return readContentInfo(new ASN1InputStream(bArr));
    }

    static ContentInfo readContentInfo(InputStream inputStream) throws CMSException {
        return readContentInfo(new ASN1InputStream(inputStream));
    }

    static ASN1Set convertToDlSet(Set<AlgorithmIdentifier> set) {
        return new DLSet((AlgorithmIdentifier[]) set.toArray(new AlgorithmIdentifier[set.size()]));
    }

    static void addDigestAlgs(Set<AlgorithmIdentifier> set, SignerInformation signerInformation, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        set.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(signerInformation.getDigestAlgorithmID(), digestAlgorithmIdentifierFinder));
        Iterator<SignerInformation> it = signerInformation.getCounterSignatures().iterator();
        while (it.hasNext()) {
            set.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(it.next().getDigestAlgorithmID(), digestAlgorithmIdentifierFinder));
        }
    }

    static List getCertificatesFromStore(Store store) throws CMSException {
        ArrayList arrayList = new ArrayList();
        try {
            Iterator it = store.getMatches(null).iterator();
            while (it.hasNext()) {
                arrayList.add(((X509CertificateHolder) it.next()).toASN1Structure());
            }
            return arrayList;
        } catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static List getAttributeCertificatesFromStore(Store store) throws CMSException {
        ArrayList arrayList = new ArrayList();
        try {
            Iterator it = store.getMatches(null).iterator();
            while (it.hasNext()) {
                arrayList.add(new DERTaggedObject(false, 2, (ASN1Encodable) ((X509AttributeCertificateHolder) it.next()).toASN1Structure()));
            }
            return arrayList;
        } catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static List getCRLsFromStore(Store store) throws CMSException {
        ArrayList arrayList = new ArrayList();
        try {
            for (Object obj : store.getMatches(null)) {
                if (obj instanceof X509CRLHolder) {
                    arrayList.add(((X509CRLHolder) obj).toASN1Structure());
                } else if (obj instanceof OtherRevocationInfoFormat) {
                    OtherRevocationInfoFormat otherRevocationInfoFormat = OtherRevocationInfoFormat.getInstance(obj);
                    validateInfoFormat(otherRevocationInfoFormat);
                    arrayList.add(new DERTaggedObject(false, 1, (ASN1Encodable) otherRevocationInfoFormat));
                } else if (obj instanceof ASN1TaggedObject) {
                    arrayList.add(obj);
                }
            }
            return arrayList;
        } catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static void validateInfoFormat(OtherRevocationInfoFormat otherRevocationInfoFormat) {
        if (CMSObjectIdentifiers.id_ri_ocsp_response.equals((ASN1Primitive) otherRevocationInfoFormat.getInfoFormat()) && 0 != OCSPResponse.getInstance(otherRevocationInfoFormat.getInfo()).getResponseStatus().getIntValue()) {
            throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData");
        }
    }

    static Collection getOthersFromStore(ASN1ObjectIdentifier aSN1ObjectIdentifier, Store store) {
        ArrayList arrayList = new ArrayList();
        Iterator it = store.getMatches(null).iterator();
        while (it.hasNext()) {
            OtherRevocationInfoFormat otherRevocationInfoFormat = new OtherRevocationInfoFormat(aSN1ObjectIdentifier, (ASN1Encodable) it.next());
            validateInfoFormat(otherRevocationInfoFormat);
            arrayList.add(new DERTaggedObject(false, 1, (ASN1Encodable) otherRevocationInfoFormat));
        }
        return arrayList;
    }

    static ASN1Set createBerSetFromList(List list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add((ASN1Encodable) it.next());
        }
        return new BERSet(aSN1EncodableVector);
    }

    static ASN1Set createDlSetFromList(List list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add((ASN1Encodable) it.next());
        }
        return new DLSet(aSN1EncodableVector);
    }

    static ASN1Set createDerSetFromList(List list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add((ASN1Encodable) it.next());
        }
        return new DERSet(aSN1EncodableVector);
    }

    static OutputStream createBEROctetOutputStream(OutputStream outputStream, int i, boolean z, int i2) throws IOException {
        BEROctetStringGenerator bEROctetStringGenerator = new BEROctetStringGenerator(outputStream, i, z);
        return i2 != 0 ? bEROctetStringGenerator.getOctetOutputStream(new byte[i2]) : bEROctetStringGenerator.getOctetOutputStream();
    }

    private static ContentInfo readContentInfo(ASN1InputStream aSN1InputStream) throws CMSException {
        try {
            ContentInfo contentInfo = ContentInfo.getInstance(aSN1InputStream.readObject());
            if (contentInfo == null) {
                throw new CMSException("No content found.");
            }
            return contentInfo;
        } catch (IOException e) {
            throw new CMSException("IOException reading content.", e);
        } catch (ClassCastException e2) {
            throw new CMSException("Malformed content.", e2);
        } catch (IllegalArgumentException e3) {
            throw new CMSException("Malformed content.", e3);
        }
    }

    public static byte[] streamToByteArray(InputStream inputStream) throws IOException {
        return Streams.readAll(inputStream);
    }

    public static byte[] streamToByteArray(InputStream inputStream, int i) throws IOException {
        return Streams.readAllLimited(inputStream, i);
    }

    static InputStream attachDigestsToInputStream(Collection collection, InputStream inputStream) {
        InputStream teeInputStream = inputStream;
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            teeInputStream = new TeeInputStream(teeInputStream, ((DigestCalculator) it.next()).getOutputStream());
        }
        return teeInputStream;
    }

    static OutputStream attachSignersToOutputStream(Collection collection, OutputStream outputStream) {
        OutputStream safeTeeOutputStream = outputStream;
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            safeTeeOutputStream = getSafeTeeOutputStream(safeTeeOutputStream, ((SignerInfoGenerator) it.next()).getCalculatingOutputStream());
        }
        return safeTeeOutputStream;
    }

    static OutputStream getSafeOutputStream(OutputStream outputStream) {
        return outputStream == null ? new NullOutputStream() : outputStream;
    }

    static OutputStream getSafeTeeOutputStream(OutputStream outputStream, OutputStream outputStream2) {
        if (outputStream == null) {
            return getSafeOutputStream(outputStream2);
        }
        return outputStream2 == null ? getSafeOutputStream(outputStream) : new TeeOutputStream(outputStream, outputStream2);
    }

    static EncryptedContentInfo getEncryptedContentInfo(CMSTypedData cMSTypedData, OutputEncryptor outputEncryptor, byte[] bArr) {
        return getEncryptedContentInfo(cMSTypedData.getContentType(), outputEncryptor.getAlgorithmIdentifier(), bArr);
    }

    static EncryptedContentInfo getEncryptedContentInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        return new EncryptedContentInfo(aSN1ObjectIdentifier, algorithmIdentifier, new BEROctetString(bArr));
    }

    static ASN1EncodableVector getRecipentInfos(GenericKey genericKey, List list) throws CMSException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add(((RecipientInfoGenerator) it.next()).generate(genericKey));
        }
        return aSN1EncodableVector;
    }

    static void addRecipientInfosToGenerator(ASN1EncodableVector aSN1EncodableVector, BERSequenceGenerator bERSequenceGenerator, boolean z) throws IOException {
        if (z) {
            bERSequenceGenerator.getRawOutputStream().write(new BERSet(aSN1EncodableVector).getEncoded());
        } else {
            bERSequenceGenerator.getRawOutputStream().write(new DERSet(aSN1EncodableVector).getEncoded());
        }
    }

    static void addOriginatorInfoToGenerator(BERSequenceGenerator bERSequenceGenerator, OriginatorInfo originatorInfo) throws IOException {
        if (originatorInfo != null) {
            bERSequenceGenerator.addObject((ASN1Primitive) new DERTaggedObject(false, 0, (ASN1Encodable) originatorInfo));
        }
    }

    static void addAttriSetToGenerator(BERSequenceGenerator bERSequenceGenerator, CMSAttributeTableGenerator cMSAttributeTableGenerator, int i, Map map) throws IOException {
        if (cMSAttributeTableGenerator != null) {
            bERSequenceGenerator.addObject((ASN1Primitive) new DERTaggedObject(false, i, (ASN1Encodable) new BERSet(cMSAttributeTableGenerator.getAttributes(map).toASN1EncodableVector())));
        }
    }

    static ASN1Set processAuthAttrSet(CMSAttributeTableGenerator cMSAttributeTableGenerator, OutputAEADEncryptor outputAEADEncryptor) throws IOException {
        DERSet dERSet = null;
        if (cMSAttributeTableGenerator != null) {
            dERSet = new DERSet(cMSAttributeTableGenerator.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector());
            outputAEADEncryptor.getAADStream().write(dERSet.getEncoded("DER"));
        }
        return dERSet;
    }

    static AttributeTable getAttributesTable(ASN1SetParser aSN1SetParser) throws IOException {
        if (aSN1SetParser == null) {
            return null;
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        while (true) {
            ASN1Encodable object = aSN1SetParser.readObject();
            if (object == null) {
                return new AttributeTable(new DERSet(aSN1EncodableVector));
            }
            aSN1EncodableVector.add(((ASN1SequenceParser) object).toASN1Primitive());
        }
    }

    static ASN1Set getAttrDLSet(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        if (cMSAttributeTableGenerator != null) {
            return new DLSet(cMSAttributeTableGenerator.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector());
        }
        return null;
    }

    static ASN1Set getAttrBERSet(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        if (cMSAttributeTableGenerator != null) {
            return new BERSet(cMSAttributeTableGenerator.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector());
        }
        return null;
    }

    static byte[] encodeObj(ASN1Encodable aSN1Encodable) throws IOException {
        if (aSN1Encodable != null) {
            return aSN1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }

    static {
        des.add("DES");
        des.add("DESEDE");
        des.add(OIWObjectIdentifiers.desCBC.getId());
        des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
        mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
    }
}
