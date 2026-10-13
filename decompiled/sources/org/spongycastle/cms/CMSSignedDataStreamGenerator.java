package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequenceGenerator;
import org.spongycastle.asn1.BERTaggedObject;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.SignerInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSSignedDataStreamGenerator.class */
public class CMSSignedDataStreamGenerator extends CMSSignedGenerator {
    private int _bufferSize;

    public void setBufferSize(int bufferSize) {
        this._bufferSize = bufferSize;
    }

    public OutputStream open(OutputStream out) throws IOException {
        return open(out, false);
    }

    public OutputStream open(OutputStream out, boolean encapsulate) throws IOException {
        return open(CMSObjectIdentifiers.data, out, encapsulate);
    }

    public OutputStream open(OutputStream out, boolean encapsulate, OutputStream dataOutputStream) throws IOException {
        return open(CMSObjectIdentifiers.data, out, encapsulate, dataOutputStream);
    }

    public OutputStream open(ASN1ObjectIdentifier eContentType, OutputStream out, boolean encapsulate) throws IOException {
        return open(eContentType, out, encapsulate, null);
    }

    public OutputStream open(ASN1ObjectIdentifier eContentType, OutputStream out, boolean encapsulate, OutputStream dataOutputStream) throws IOException {
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject(CMSObjectIdentifiers.signedData);
        BERSequenceGenerator sigGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        sigGen.addObject(calculateVersion(eContentType));
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        for (SignerInformation signer : this._signers) {
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
        }
        for (SignerInfoGenerator signerGen : this.signerGens) {
            digestAlgs.add(signerGen.getDigestAlgorithm());
        }
        sigGen.getRawOutputStream().write(new DERSet(digestAlgs).getEncoded());
        BERSequenceGenerator eiGen = new BERSequenceGenerator(sigGen.getRawOutputStream());
        eiGen.addObject(eContentType);
        OutputStream encapStream = encapsulate ? CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, true, this._bufferSize) : null;
        OutputStream contentStream = CMSUtils.getSafeTeeOutputStream(dataOutputStream, encapStream);
        OutputStream sigStream = CMSUtils.attachSignersToOutputStream(this.signerGens, contentStream);
        return new CmsSignedDataOutputStream(sigStream, eContentType, sGen, sigGen, eiGen);
    }

    private ASN1Integer calculateVersion(ASN1ObjectIdentifier contentOid) {
        boolean otherCert = false;
        boolean otherCrl = false;
        boolean attrCertV1Found = false;
        boolean attrCertV2Found = false;
        if (this.certs != null) {
            for (Object obj : this.certs) {
                if (obj instanceof ASN1TaggedObject) {
                    ASN1TaggedObject tagged = (ASN1TaggedObject) obj;
                    if (tagged.getTagNo() == 1) {
                        attrCertV1Found = true;
                    } else if (tagged.getTagNo() == 2) {
                        attrCertV2Found = true;
                    } else if (tagged.getTagNo() == 3) {
                        otherCert = true;
                    }
                }
            }
        }
        if (otherCert) {
            return new ASN1Integer(5L);
        }
        if (this.crls != null) {
            Iterator it = this.crls.iterator();
            while (it.hasNext()) {
                if (it.next() instanceof ASN1TaggedObject) {
                    otherCrl = true;
                }
            }
        }
        if (otherCrl) {
            return new ASN1Integer(5L);
        }
        if (attrCertV2Found) {
            return new ASN1Integer(4L);
        }
        if (attrCertV1Found) {
            return new ASN1Integer(3L);
        }
        if (checkForVersion3(this._signers, this.signerGens)) {
            return new ASN1Integer(3L);
        }
        if (!CMSObjectIdentifiers.data.equals(contentOid)) {
            return new ASN1Integer(3L);
        }
        return new ASN1Integer(1L);
    }

    private boolean checkForVersion3(List signerInfos, List signerInfoGens) {
        Iterator it = signerInfos.iterator();
        while (it.hasNext()) {
            SignerInfo s = SignerInfo.getInstance(((SignerInformation) it.next()).toASN1Structure());
            if (s.getVersion().getValue().intValue() == 3) {
                return true;
            }
        }
        Iterator it2 = signerInfoGens.iterator();
        while (it2.hasNext()) {
            SignerInfoGenerator s2 = (SignerInfoGenerator) it2.next();
            if (s2.getGeneratedVersion() == 3) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSSignedDataStreamGenerator$CmsSignedDataOutputStream.class */
    private class CmsSignedDataOutputStream extends OutputStream {
        private OutputStream _out;
        private ASN1ObjectIdentifier _contentOID;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _sigGen;
        private BERSequenceGenerator _eiGen;

        public CmsSignedDataOutputStream(OutputStream out, ASN1ObjectIdentifier contentOID, BERSequenceGenerator sGen, BERSequenceGenerator sigGen, BERSequenceGenerator eiGen) {
            this._out = out;
            this._contentOID = contentOID;
            this._sGen = sGen;
            this._sigGen = sigGen;
            this._eiGen = eiGen;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this._out.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this._out.write(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this._out.write(bytes);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this._out.close();
            this._eiGen.close();
            CMSSignedDataStreamGenerator.this.digests.clear();
            if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
                ASN1Set certSet = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs);
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 0, certSet).getEncoded());
            }
            if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
                ASN1Set crlSet = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls);
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 1, crlSet).getEncoded());
            }
            ASN1EncodableVector signerInfos = new ASN1EncodableVector();
            for (SignerInfoGenerator sigGen : CMSSignedDataStreamGenerator.this.signerGens) {
                try {
                    signerInfos.add(sigGen.generate(this._contentOID));
                    byte[] calculatedDigest = sigGen.getCalculatedDigest();
                    CMSSignedDataStreamGenerator.this.digests.put(sigGen.getDigestAlgorithm().getAlgorithm().getId(), calculatedDigest);
                } catch (CMSException e) {
                    throw new CMSStreamException("exception generating signers: " + e.getMessage(), e);
                }
            }
            for (SignerInformation signer : CMSSignedDataStreamGenerator.this._signers) {
                signerInfos.add(signer.toASN1Structure());
            }
            this._sigGen.getRawOutputStream().write(new DERSet(signerInfos).getEncoded());
            this._sigGen.close();
            this._sGen.close();
        }
    }
}
