package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSSignedData.class */
public class CMSSignedData implements Encodable {
    private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
    private static final DefaultDigestAlgorithmIdentifierFinder DIGEST_ALG_ID_FINDER = new DefaultDigestAlgorithmIdentifierFinder();
    SignedData signedData;
    ContentInfo contentInfo;
    CMSTypedData signedContent;
    SignerInformationStore signerInfoStore;
    private Map hashes;

    private CMSSignedData(CMSSignedData cMSSignedData) {
        this.signedData = cMSSignedData.signedData;
        this.contentInfo = cMSSignedData.contentInfo;
        this.signedContent = cMSSignedData.signedContent;
        this.signerInfoStore = cMSSignedData.signerInfoStore;
    }

    public CMSSignedData(byte[] bArr) throws CMSException {
        this(CMSUtils.readContentInfo(bArr));
    }

    public CMSSignedData(CMSProcessable cMSProcessable, byte[] bArr) throws CMSException {
        this(cMSProcessable, CMSUtils.readContentInfo(bArr));
    }

    public CMSSignedData(Map map, byte[] bArr) throws CMSException {
        this(map, CMSUtils.readContentInfo(bArr));
    }

    public CMSSignedData(CMSProcessable cMSProcessable, InputStream inputStream) throws CMSException {
        this(cMSProcessable, CMSUtils.readContentInfo((InputStream) new ASN1InputStream(inputStream)));
    }

    public CMSSignedData(InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }

    public CMSSignedData(final CMSProcessable cMSProcessable, ContentInfo contentInfo) throws CMSException {
        if (cMSProcessable instanceof CMSTypedData) {
            this.signedContent = (CMSTypedData) cMSProcessable;
        } else {
            this.signedContent = new CMSTypedData() { // from class: org.bouncycastle.cms.CMSSignedData.1
                @Override // org.bouncycastle.cms.CMSTypedData
                public ASN1ObjectIdentifier getContentType() {
                    return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
                }

                @Override // org.bouncycastle.cms.CMSProcessable
                public void write(OutputStream outputStream) throws CMSException, IOException {
                    cMSProcessable.write(outputStream);
                }

                @Override // org.bouncycastle.cms.CMSProcessable
                public Object getContent() {
                    return cMSProcessable.getContent();
                }
            };
        }
        this.contentInfo = contentInfo;
        this.signedData = getSignedData();
    }

    public CMSSignedData(Map map, ContentInfo contentInfo) throws CMSException {
        this.hashes = map;
        this.contentInfo = contentInfo;
        this.signedData = getSignedData();
    }

    public CMSSignedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        this.signedData = getSignedData();
        ASN1Encodable content = this.signedData.getEncapContentInfo().getContent();
        if (content == null) {
            this.signedContent = null;
        } else if (content instanceof ASN1OctetString) {
            this.signedContent = new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString) content).getOctets());
        } else {
            this.signedContent = new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), content);
        }
    }

    private SignedData getSignedData() throws CMSException {
        try {
            return SignedData.getInstance(this.contentInfo.getContent());
        } catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("Malformed content.", e2);
        }
    }

    public int getVersion() {
        return this.signedData.getVersion().intValueExact();
    }

    public SignerInformationStore getSignerInfos() {
        if (this.signerInfoStore == null) {
            ASN1Set signerInfos = this.signedData.getSignerInfos();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i != signerInfos.size(); i++) {
                SignerInfo signerInfo = SignerInfo.getInstance(signerInfos.getObjectAt(i));
                ASN1ObjectIdentifier contentType = this.signedData.getEncapContentInfo().getContentType();
                if (this.hashes == null) {
                    arrayList.add(new SignerInformation(signerInfo, contentType, this.signedContent, null));
                } else {
                    arrayList.add(new SignerInformation(signerInfo, contentType, null, this.hashes.keySet().iterator().next() instanceof String ? (byte[]) this.hashes.get(signerInfo.getDigestAlgorithm().getAlgorithm().getId()) : (byte[]) this.hashes.get(signerInfo.getDigestAlgorithm().getAlgorithm())));
                }
            }
            this.signerInfoStore = new SignerInformationStore(arrayList);
        }
        return this.signerInfoStore;
    }

    public boolean isDetachedSignature() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() > 0;
    }

    public boolean isCertificateManagementMessage() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() == 0;
    }

    public Store<X509CertificateHolder> getCertificates() {
        return HELPER.getCertificates(this.signedData.getCertificates());
    }

    public Store<X509CRLHolder> getCRLs() {
        return HELPER.getCRLs(this.signedData.getCRLs());
    }

    public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
        return HELPER.getAttributeCertificates(this.signedData.getCertificates());
    }

    public Store getOtherRevocationInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return HELPER.getOtherRevocationInfo(aSN1ObjectIdentifier, this.signedData.getCRLs());
    }

    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        HashSet hashSet = new HashSet();
        Enumeration objects = this.signedData.getDigestAlgorithms().getObjects();
        while (objects.hasMoreElements()) {
            hashSet.add(AlgorithmIdentifier.getInstance(objects.nextElement()));
        }
        return Collections.unmodifiableSet(hashSet);
    }

    public String getSignedContentTypeOID() {
        return this.signedData.getEncapContentInfo().getContentType().getId();
    }

    public CMSTypedData getSignedContent() {
        return this.signedContent;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public byte[] getEncoded(String str) throws IOException {
        return this.contentInfo.getEncoded(str);
    }

    public boolean verifySignatures(SignerInformationVerifierProvider signerInformationVerifierProvider) throws CMSException {
        return verifySignatures(signerInformationVerifierProvider, false);
    }

    public boolean verifySignatures(SignerInformationVerifierProvider signerInformationVerifierProvider, boolean z) throws CMSException {
        for (SignerInformation signerInformation : getSignerInfos().getSigners()) {
            try {
                if (!signerInformation.verify(signerInformationVerifierProvider.get(signerInformation.getSID()))) {
                    return false;
                }
                if (!z) {
                    Iterator<SignerInformation> it = signerInformation.getCounterSignatures().getSigners().iterator();
                    while (it.hasNext()) {
                        if (!verifyCounterSignature(it.next(), signerInformationVerifierProvider)) {
                            return false;
                        }
                    }
                }
            } catch (OperatorCreationException e) {
                throw new CMSException("failure in verifier provider: " + e.getMessage(), e);
            }
        }
        return true;
    }

    private boolean verifyCounterSignature(SignerInformation signerInformation, SignerInformationVerifierProvider signerInformationVerifierProvider) throws CMSException, OperatorCreationException {
        if (!signerInformation.verify(signerInformationVerifierProvider.get(signerInformation.getSID()))) {
            return false;
        }
        Iterator<SignerInformation> it = signerInformation.getCounterSignatures().getSigners().iterator();
        while (it.hasNext()) {
            if (!verifyCounterSignature(it.next(), signerInformationVerifierProvider)) {
                return false;
            }
        }
        return true;
    }

    public static CMSSignedData addDigestAlgorithm(CMSSignedData cMSSignedData, AlgorithmIdentifier algorithmIdentifier) {
        return addDigestAlgorithm(cMSSignedData, algorithmIdentifier, DIGEST_ALG_ID_FINDER);
    }

    public static CMSSignedData addDigestAlgorithm(CMSSignedData cMSSignedData, AlgorithmIdentifier algorithmIdentifier, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        Set<AlgorithmIdentifier> digestAlgorithmIDs = cMSSignedData.getDigestAlgorithmIDs();
        AlgorithmIdentifier algorithmIdentifierFixDigestAlgID = HELPER.fixDigestAlgID(algorithmIdentifier, digestAlgorithmIdentifierFinder);
        if (digestAlgorithmIDs.contains(algorithmIdentifierFixDigestAlgID)) {
            return cMSSignedData;
        }
        CMSSignedData cMSSignedData2 = new CMSSignedData(cMSSignedData);
        HashSet hashSet = new HashSet();
        Iterator<AlgorithmIdentifier> it = digestAlgorithmIDs.iterator();
        while (it.hasNext()) {
            hashSet.add(HELPER.fixDigestAlgID(it.next(), digestAlgorithmIdentifierFinder));
        }
        hashSet.add(algorithmIdentifierFixDigestAlgID);
        ASN1Set aSN1SetConvertToDlSet = CMSUtils.convertToDlSet(hashSet);
        ASN1Sequence aSN1Sequence = (ASN1Sequence) cMSSignedData.signedData.toASN1Primitive();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(aSN1Sequence.size());
        aSN1EncodableVector.add(aSN1Sequence.getObjectAt(0));
        aSN1EncodableVector.add(aSN1SetConvertToDlSet);
        for (int i = 2; i != aSN1Sequence.size(); i++) {
            aSN1EncodableVector.add(aSN1Sequence.getObjectAt(i));
        }
        cMSSignedData2.signedData = SignedData.getInstance(new BERSequence(aSN1EncodableVector));
        cMSSignedData2.contentInfo = new ContentInfo(cMSSignedData2.contentInfo.getContentType(), cMSSignedData2.signedData);
        return cMSSignedData2;
    }

    public static CMSSignedData replaceSigners(CMSSignedData cMSSignedData, SignerInformationStore signerInformationStore) {
        return replaceSigners(cMSSignedData, signerInformationStore, DIGEST_ALG_ID_FINDER);
    }

    public static CMSSignedData replaceSigners(CMSSignedData cMSSignedData, SignerInformationStore signerInformationStore, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        CMSSignedData cMSSignedData2 = new CMSSignedData(cMSSignedData);
        cMSSignedData2.signerInfoStore = signerInformationStore;
        HashSet hashSet = new HashSet();
        Collection<SignerInformation> signers = signerInformationStore.getSigners();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(signers.size());
        for (SignerInformation signerInformation : signers) {
            CMSUtils.addDigestAlgs(hashSet, signerInformation, digestAlgorithmIdentifierFinder);
            aSN1EncodableVector.add(signerInformation.toASN1Structure());
        }
        Set<AlgorithmIdentifier> digestAlgorithmIDs = cMSSignedData.getDigestAlgorithmIDs();
        AlgorithmIdentifier[] algorithmIdentifierArr = (AlgorithmIdentifier[]) digestAlgorithmIDs.toArray(new AlgorithmIdentifier[digestAlgorithmIDs.size()]);
        AlgorithmIdentifier[] algorithmIdentifierArr2 = (AlgorithmIdentifier[]) hashSet.toArray(new AlgorithmIdentifier[hashSet.size()]);
        compareAndReplaceAlgIds(algorithmIdentifierArr, algorithmIdentifierArr2);
        DLSet dLSet = new DLSet(algorithmIdentifierArr2);
        DLSet dLSet2 = new DLSet(aSN1EncodableVector);
        ASN1Sequence aSN1Sequence = (ASN1Sequence) cMSSignedData.signedData.toASN1Primitive();
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector(aSN1Sequence.size());
        aSN1EncodableVector2.add(aSN1Sequence.getObjectAt(0));
        aSN1EncodableVector2.add(dLSet);
        for (int i = 2; i != aSN1Sequence.size() - 1; i++) {
            aSN1EncodableVector2.add(aSN1Sequence.getObjectAt(i));
        }
        aSN1EncodableVector2.add(dLSet2);
        cMSSignedData2.signedData = SignedData.getInstance(new BERSequence(aSN1EncodableVector2));
        cMSSignedData2.contentInfo = new ContentInfo(cMSSignedData2.contentInfo.getContentType(), cMSSignedData2.signedData);
        return cMSSignedData2;
    }

    private static void compareAndReplaceAlgIds(AlgorithmIdentifier[] algorithmIdentifierArr, AlgorithmIdentifier[] algorithmIdentifierArr2) {
        for (int i = 0; i != algorithmIdentifierArr2.length; i++) {
            AlgorithmIdentifier algorithmIdentifier = algorithmIdentifierArr2[i];
            for (int i2 = 0; i2 != algorithmIdentifierArr.length; i2++) {
                AlgorithmIdentifier algorithmIdentifier2 = algorithmIdentifierArr[i2];
                if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) algorithmIdentifier2.getAlgorithm()) && (algorithmIdentifier.getParameters() == null || DERNull.INSTANCE.equals(algorithmIdentifier.getParameters()))) {
                    algorithmIdentifierArr2[i] = algorithmIdentifier2;
                    break;
                }
            }
        }
    }

    public static CMSSignedData replaceCertificatesAndCRLs(CMSSignedData cMSSignedData, Store store, Store store2, Store store3) throws CMSException {
        CMSSignedData cMSSignedData2 = new CMSSignedData(cMSSignedData);
        ASN1Set aSN1Set = null;
        ASN1Set aSN1Set2 = null;
        if (store != null || store2 != null) {
            ArrayList arrayList = new ArrayList();
            if (store != null) {
                arrayList.addAll(CMSUtils.getCertificatesFromStore(store));
            }
            if (store2 != null) {
                arrayList.addAll(CMSUtils.getAttributeCertificatesFromStore(store2));
            }
            ASN1Set aSN1SetCreateBerSetFromList = CMSUtils.createBerSetFromList(arrayList);
            if (aSN1SetCreateBerSetFromList.size() != 0) {
                aSN1Set = aSN1SetCreateBerSetFromList;
            }
        }
        if (store3 != null) {
            ASN1Set aSN1SetCreateBerSetFromList2 = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(store3));
            if (aSN1SetCreateBerSetFromList2.size() != 0) {
                aSN1Set2 = aSN1SetCreateBerSetFromList2;
            }
        }
        cMSSignedData2.signedData = new SignedData(cMSSignedData.signedData.getDigestAlgorithms(), cMSSignedData.signedData.getEncapContentInfo(), aSN1Set, aSN1Set2, cMSSignedData.signedData.getSignerInfos());
        cMSSignedData2.contentInfo = new ContentInfo(cMSSignedData2.contentInfo.getContentType(), cMSSignedData2.signedData);
        return cMSSignedData2;
    }
}
