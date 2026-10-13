package org.spongycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Generator;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetStringParser;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1SetParser;
import org.spongycastle.asn1.ASN1StreamParser;
import org.spongycastle.asn1.BERSequenceGenerator;
import org.spongycastle.asn1.BERSetParser;
import org.spongycastle.asn1.BERTaggedObject;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfoParser;
import org.spongycastle.asn1.cms.SignedDataParser;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Store;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSSignedDataParser.class */
public class CMSSignedDataParser extends CMSContentInfoParser {
    private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
    private SignedDataParser _signedData;
    private ASN1ObjectIdentifier _signedContentType;
    private CMSTypedStream _signedContent;
    private Map digests;
    private Set<AlgorithmIdentifier> digestAlgorithms;
    private SignerInformationStore _signerInfoStore;
    private ASN1Set _certSet;
    private ASN1Set _crlSet;
    private boolean _isCertCrlParsed;

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, byte[] sigBlock) throws CMSException {
        this(digestCalculatorProvider, new ByteArrayInputStream(sigBlock));
    }

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, CMSTypedStream signedContent, byte[] sigBlock) throws CMSException {
        this(digestCalculatorProvider, signedContent, new ByteArrayInputStream(sigBlock));
    }

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, InputStream sigData) throws CMSException {
        this(digestCalculatorProvider, (CMSTypedStream) null, sigData);
    }

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, CMSTypedStream signedContent, InputStream sigData) throws CMSException {
        super(sigData);
        try {
            this._signedContent = signedContent;
            this._signedData = SignedDataParser.getInstance(this._contentInfo.getContent(16));
            this.digests = new HashMap();
            ASN1SetParser digAlgs = this._signedData.getDigestAlgorithms();
            Set<AlgorithmIdentifier> algSet = new HashSet<>();
            while (true) {
                ASN1Encodable o = digAlgs.readObject();
                if (o == null) {
                    break;
                }
                AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance(o);
                algSet.add(algId);
                try {
                    DigestCalculator calculator = digestCalculatorProvider.get(algId);
                    if (calculator != null) {
                        this.digests.put(algId.getAlgorithm(), calculator);
                    }
                } catch (OperatorCreationException e) {
                }
            }
            this.digestAlgorithms = Collections.unmodifiableSet(algSet);
            ContentInfoParser cont = this._signedData.getEncapContentInfo();
            ASN1Encodable contentParser = cont.getContent(4);
            if (contentParser instanceof ASN1OctetStringParser) {
                ASN1OctetStringParser octs = (ASN1OctetStringParser) contentParser;
                CMSTypedStream ctStr = new CMSTypedStream(cont.getContentType(), octs.getOctetStream());
                if (this._signedContent == null) {
                    this._signedContent = ctStr;
                } else {
                    ctStr.drain();
                }
            } else if (contentParser != null) {
                PKCS7TypedStream pkcs7Stream = new PKCS7TypedStream(cont.getContentType(), contentParser);
                if (this._signedContent == null) {
                    this._signedContent = pkcs7Stream;
                } else {
                    pkcs7Stream.drain();
                }
            }
            if (signedContent == null) {
                this._signedContentType = cont.getContentType();
            } else {
                this._signedContentType = this._signedContent.getContentType();
            }
        } catch (IOException e2) {
            throw new CMSException("io exception: " + e2.getMessage(), e2);
        }
    }

    public int getVersion() {
        return this._signedData.getVersion().getValue().intValue();
    }

    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        return this.digestAlgorithms;
    }

    public SignerInformationStore getSignerInfos() throws CMSException {
        if (this._signerInfoStore == null) {
            populateCertCrlSets();
            List signerInfos = new ArrayList();
            Map hashes = new HashMap();
            for (Object digestKey : this.digests.keySet()) {
                hashes.put(digestKey, ((DigestCalculator) this.digests.get(digestKey)).getDigest());
            }
            try {
                ASN1SetParser s = this._signedData.getSignerInfos();
                while (true) {
                    ASN1Encodable o = s.readObject();
                    if (o == null) {
                        break;
                    }
                    SignerInfo info = SignerInfo.getInstance(o.toASN1Primitive());
                    byte[] hash = (byte[]) hashes.get(info.getDigestAlgorithm().getAlgorithm());
                    signerInfos.add(new SignerInformation(info, this._signedContentType, null, hash));
                }
                this._signerInfoStore = new SignerInformationStore(signerInfos);
            } catch (IOException e) {
                throw new CMSException("io exception: " + e.getMessage(), e);
            }
        }
        return this._signerInfoStore;
    }

    public Store getCertificates() throws CMSException {
        populateCertCrlSets();
        return HELPER.getCertificates(this._certSet);
    }

    public Store getCRLs() throws CMSException {
        populateCertCrlSets();
        return HELPER.getCRLs(this._crlSet);
    }

    public Store getAttributeCertificates() throws CMSException {
        populateCertCrlSets();
        return HELPER.getAttributeCertificates(this._certSet);
    }

    public Store getOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat) throws CMSException {
        populateCertCrlSets();
        return HELPER.getOtherRevocationInfo(otherRevocationInfoFormat, this._crlSet);
    }

    private void populateCertCrlSets() throws CMSException {
        if (this._isCertCrlParsed) {
            return;
        }
        this._isCertCrlParsed = true;
        try {
            this._certSet = getASN1Set(this._signedData.getCertificates());
            this._crlSet = getASN1Set(this._signedData.getCrls());
        } catch (IOException e) {
            throw new CMSException("problem parsing cert/crl sets", e);
        }
    }

    public String getSignedContentTypeOID() {
        return this._signedContentType.getId();
    }

    public CMSTypedStream getSignedContent() {
        if (this._signedContent == null) {
            return null;
        }
        InputStream digStream = CMSUtils.attachDigestsToInputStream(this.digests.values(), this._signedContent.getContentStream());
        return new CMSTypedStream(this._signedContent.getContentType(), digStream);
    }

    public static OutputStream replaceSigners(InputStream original, SignerInformationStore signerInformationStore, OutputStream out) throws IOException, CMSException {
        ASN1StreamParser in = new ASN1StreamParser(original);
        ContentInfoParser contentInfo = new ContentInfoParser((ASN1SequenceParser) in.readObject());
        SignedDataParser signedData = SignedDataParser.getInstance(contentInfo.getContent(16));
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject(CMSObjectIdentifiers.signedData);
        BERSequenceGenerator sigGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        sigGen.addObject(signedData.getVersion());
        signedData.getDigestAlgorithms().toASN1Primitive();
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        for (SignerInformation signer : signerInformationStore.getSigners()) {
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
        }
        sigGen.getRawOutputStream().write(new DERSet(digestAlgs).getEncoded());
        ContentInfoParser encapContentInfo = signedData.getEncapContentInfo();
        BERSequenceGenerator eiGen = new BERSequenceGenerator(sigGen.getRawOutputStream());
        eiGen.addObject(encapContentInfo.getContentType());
        pipeEncapsulatedOctetString(encapContentInfo, eiGen.getRawOutputStream());
        eiGen.close();
        writeSetToGeneratorTagged(sigGen, signedData.getCertificates(), 0);
        writeSetToGeneratorTagged(sigGen, signedData.getCrls(), 1);
        ASN1EncodableVector signerInfos = new ASN1EncodableVector();
        for (SignerInformation signer2 : signerInformationStore.getSigners()) {
            signerInfos.add(signer2.toASN1Structure());
        }
        sigGen.getRawOutputStream().write(new DERSet(signerInfos).getEncoded());
        sigGen.close();
        sGen.close();
        return out;
    }

    public static OutputStream replaceCertificatesAndCRLs(InputStream original, Store certs, Store crls, Store attrCerts, OutputStream out) throws IOException, CMSException {
        ASN1StreamParser in = new ASN1StreamParser(original);
        ContentInfoParser contentInfo = new ContentInfoParser((ASN1SequenceParser) in.readObject());
        SignedDataParser signedData = SignedDataParser.getInstance(contentInfo.getContent(16));
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject(CMSObjectIdentifiers.signedData);
        BERSequenceGenerator sigGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        sigGen.addObject(signedData.getVersion());
        sigGen.getRawOutputStream().write(signedData.getDigestAlgorithms().toASN1Primitive().getEncoded());
        ContentInfoParser encapContentInfo = signedData.getEncapContentInfo();
        BERSequenceGenerator eiGen = new BERSequenceGenerator(sigGen.getRawOutputStream());
        eiGen.addObject(encapContentInfo.getContentType());
        pipeEncapsulatedOctetString(encapContentInfo, eiGen.getRawOutputStream());
        eiGen.close();
        getASN1Set(signedData.getCertificates());
        getASN1Set(signedData.getCrls());
        if (certs != null || attrCerts != null) {
            List certificates = new ArrayList();
            if (certs != null) {
                certificates.addAll(CMSUtils.getCertificatesFromStore(certs));
            }
            if (attrCerts != null) {
                certificates.addAll(CMSUtils.getAttributeCertificatesFromStore(attrCerts));
            }
            ASN1Set asn1Certs = CMSUtils.createBerSetFromList(certificates);
            if (asn1Certs.size() > 0) {
                sigGen.getRawOutputStream().write(new DERTaggedObject(false, 0, asn1Certs).getEncoded());
            }
        }
        if (crls != null) {
            ASN1Set asn1Crls = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(crls));
            if (asn1Crls.size() > 0) {
                sigGen.getRawOutputStream().write(new DERTaggedObject(false, 1, asn1Crls).getEncoded());
            }
        }
        sigGen.getRawOutputStream().write(signedData.getSignerInfos().toASN1Primitive().getEncoded());
        sigGen.close();
        sGen.close();
        return out;
    }

    private static void writeSetToGeneratorTagged(ASN1Generator asn1Gen, ASN1SetParser asn1SetParser, int tagNo) throws IOException {
        ASN1Set asn1Set = getASN1Set(asn1SetParser);
        if (asn1Set != null) {
            if (asn1SetParser instanceof BERSetParser) {
                asn1Gen.getRawOutputStream().write(new BERTaggedObject(false, tagNo, asn1Set).getEncoded());
            } else {
                asn1Gen.getRawOutputStream().write(new DERTaggedObject(false, tagNo, asn1Set).getEncoded());
            }
        }
    }

    private static ASN1Set getASN1Set(ASN1SetParser asn1SetParser) {
        if (asn1SetParser == null) {
            return null;
        }
        return ASN1Set.getInstance(asn1SetParser.toASN1Primitive());
    }

    private static void pipeEncapsulatedOctetString(ContentInfoParser encapContentInfo, OutputStream rawOutputStream) throws IOException {
        ASN1OctetStringParser octs = (ASN1OctetStringParser) encapContentInfo.getContent(4);
        if (octs != null) {
            pipeOctetString(octs, rawOutputStream);
        }
    }

    private static void pipeOctetString(ASN1OctetStringParser octs, OutputStream output) throws IOException {
        OutputStream outOctets = CMSUtils.createBEROctetOutputStream(output, 0, true, 0);
        Streams.pipeAll(octs.getOctetStream(), outOctets);
        outOctets.close();
    }
}
