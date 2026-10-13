package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.X509AttributeCertificateHolder;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Encodable;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSSignedData.class */
public class CMSSignedData implements Encodable {
    private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
    SignedData signedData;
    ContentInfo contentInfo;
    CMSTypedData signedContent;
    SignerInformationStore signerInfoStore;
    private Map hashes;

    private CMSSignedData(CMSSignedData c) {
        this.signedData = c.signedData;
        this.contentInfo = c.contentInfo;
        this.signedContent = c.signedContent;
        this.signerInfoStore = c.signerInfoStore;
    }

    public CMSSignedData(byte[] sigBlock) throws CMSException {
        this(CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(CMSProcessable signedContent, byte[] sigBlock) throws CMSException {
        this(signedContent, CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(Map hashes, byte[] sigBlock) throws CMSException {
        this(hashes, CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(CMSProcessable signedContent, InputStream sigData) throws CMSException {
        this(signedContent, CMSUtils.readContentInfo((InputStream) new ASN1InputStream(sigData)));
    }

    public CMSSignedData(InputStream sigData) throws CMSException {
        this(CMSUtils.readContentInfo(sigData));
    }

    public CMSSignedData(final CMSProcessable signedContent, ContentInfo sigData) throws CMSException {
        if (signedContent instanceof CMSTypedData) {
            this.signedContent = (CMSTypedData) signedContent;
        } else {
            this.signedContent = new CMSTypedData() { // from class: org.spongycastle.cms.CMSSignedData.1
                @Override // org.spongycastle.cms.CMSTypedData
                public ASN1ObjectIdentifier getContentType() {
                    return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
                }

                @Override // org.spongycastle.cms.CMSProcessable
                public void write(OutputStream out) throws IOException, CMSException {
                    signedContent.write(out);
                }

                @Override // org.spongycastle.cms.CMSProcessable
                public Object getContent() {
                    return signedContent.getContent();
                }
            };
        }
        this.contentInfo = sigData;
        this.signedData = getSignedData();
    }

    public CMSSignedData(Map hashes, ContentInfo sigData) throws CMSException {
        this.hashes = hashes;
        this.contentInfo = sigData;
        this.signedData = getSignedData();
    }

    public CMSSignedData(ContentInfo sigData) throws CMSException {
        this.contentInfo = sigData;
        this.signedData = getSignedData();
        ASN1Encodable content = this.signedData.getEncapContentInfo().getContent();
        if (content != null) {
            if (content instanceof ASN1OctetString) {
                this.signedContent = new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString) content).getOctets());
                return;
            } else {
                this.signedContent = new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), content);
                return;
            }
        }
        this.signedContent = null;
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
        return this.signedData.getVersion().getValue().intValue();
    }

    public SignerInformationStore getSignerInfos() {
        if (this.signerInfoStore == null) {
            ASN1Set s = this.signedData.getSignerInfos();
            List signerInfos = new ArrayList();
            for (int i = 0; i != s.size(); i++) {
                SignerInfo info = SignerInfo.getInstance(s.getObjectAt(i));
                ASN1ObjectIdentifier contentType = this.signedData.getEncapContentInfo().getContentType();
                if (this.hashes == null) {
                    signerInfos.add(new SignerInformation(info, contentType, this.signedContent, null));
                } else {
                    Object obj = this.hashes.keySet().iterator().next();
                    byte[] hash = obj instanceof String ? (byte[]) this.hashes.get(info.getDigestAlgorithm().getAlgorithm().getId()) : (byte[]) this.hashes.get(info.getDigestAlgorithm().getAlgorithm());
                    signerInfos.add(new SignerInformation(info, contentType, null, hash));
                }
            }
            this.signerInfoStore = new SignerInformationStore(signerInfos);
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

    public Store getOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat) {
        return HELPER.getOtherRevocationInfo(otherRevocationInfoFormat, this.signedData.getCRLs());
    }

    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        Set<AlgorithmIdentifier> digests = new HashSet<>(this.signedData.getDigestAlgorithms().size());
        Enumeration en = this.signedData.getDigestAlgorithms().getObjects();
        while (en.hasMoreElements()) {
            digests.add(AlgorithmIdentifier.getInstance(en.nextElement()));
        }
        return Collections.unmodifiableSet(digests);
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

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public boolean verifySignatures(SignerInformationVerifierProvider verifierProvider) throws CMSException {
        return verifySignatures(verifierProvider, false);
    }

    public boolean verifySignatures(SignerInformationVerifierProvider verifierProvider, boolean ignoreCounterSignatures) throws CMSException {
        for (SignerInformation signer : getSignerInfos().getSigners()) {
            try {
                SignerInformationVerifier verifier = verifierProvider.get(signer.getSID());
                if (!signer.verify(verifier)) {
                    return false;
                }
                if (!ignoreCounterSignatures) {
                    Iterator<SignerInformation> it = signer.getCounterSignatures().getSigners().iterator();
                    while (it.hasNext()) {
                        if (!verifyCounterSignature(it.next(), verifierProvider)) {
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

    private boolean verifyCounterSignature(SignerInformation counterSigner, SignerInformationVerifierProvider verifierProvider) throws OperatorCreationException, CMSException {
        SignerInformationVerifier counterVerifier = verifierProvider.get(counterSigner.getSID());
        if (!counterSigner.verify(counterVerifier)) {
            return false;
        }
        Iterator<SignerInformation> it = counterSigner.getCounterSignatures().getSigners().iterator();
        while (it.hasNext()) {
            if (!verifyCounterSignature(it.next(), verifierProvider)) {
                return false;
            }
        }
        return true;
    }

    public static CMSSignedData replaceSigners(CMSSignedData signedData, SignerInformationStore signerInformationStore) {
        CMSSignedData cms = new CMSSignedData(signedData);
        cms.signerInfoStore = signerInformationStore;
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        ASN1EncodableVector vec = new ASN1EncodableVector();
        for (SignerInformation signer : signerInformationStore.getSigners()) {
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
            vec.add(signer.toASN1Structure());
        }
        ASN1Set digests = new DERSet(digestAlgs);
        ASN1Set signers = new DERSet(vec);
        ASN1Sequence sD = (ASN1Sequence) signedData.signedData.toASN1Primitive();
        ASN1EncodableVector vec2 = new ASN1EncodableVector();
        vec2.add(sD.getObjectAt(0));
        vec2.add(digests);
        for (int i = 2; i != sD.size() - 1; i++) {
            vec2.add(sD.getObjectAt(i));
        }
        vec2.add(signers);
        cms.signedData = SignedData.getInstance(new BERSequence(vec2));
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), cms.signedData);
        return cms;
    }

    public static CMSSignedData replaceCertificatesAndCRLs(CMSSignedData signedData, Store certificates, Store attrCerts, Store revocations) throws CMSException {
        CMSSignedData cms = new CMSSignedData(signedData);
        ASN1Set certSet = null;
        ASN1Set crlSet = null;
        if (certificates != null || attrCerts != null) {
            List certs = new ArrayList();
            if (certificates != null) {
                certs.addAll(CMSUtils.getCertificatesFromStore(certificates));
            }
            if (attrCerts != null) {
                certs.addAll(CMSUtils.getAttributeCertificatesFromStore(attrCerts));
            }
            ASN1Set set = CMSUtils.createBerSetFromList(certs);
            if (set.size() != 0) {
                certSet = set;
            }
        }
        if (revocations != null) {
            ASN1Set set2 = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(revocations));
            if (set2.size() != 0) {
                crlSet = set2;
            }
        }
        cms.signedData = new SignedData(signedData.signedData.getDigestAlgorithms(), signedData.signedData.getEncapContentInfo(), certSet, crlSet, signedData.signedData.getSignerInfos());
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), cms.signedData);
        return cms;
    }
}
