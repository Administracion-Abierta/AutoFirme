package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BERSequenceGenerator;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSEnvelopedDataStreamGenerator.class */
public class CMSEnvelopedDataStreamGenerator extends CMSEnvelopedGenerator {
    private ASN1Set _unprotectedAttributes = null;
    private int _bufferSize;
    private boolean _berEncodeRecipientSet;

    public void setBufferSize(int bufferSize) {
        this._bufferSize = bufferSize;
    }

    public void setBEREncodeRecipients(boolean berEncodeRecipientSet) {
        this._berEncodeRecipientSet = berEncodeRecipientSet;
    }

    private ASN1Integer getVersion() {
        if (this.originatorInfo != null || this._unprotectedAttributes != null) {
            return new ASN1Integer(2L);
        }
        return new ASN1Integer(0L);
    }

    private OutputStream doOpen(ASN1ObjectIdentifier dataType, OutputStream out, OutputEncryptor encryptor) throws IOException, CMSException {
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        GenericKey encKey = encryptor.getKey();
        for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
            recipientInfos.add(recipient.generate(encKey));
        }
        return open(dataType, out, recipientInfos, encryptor);
    }

    protected OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, ASN1EncodableVector recipientInfos, OutputEncryptor encryptor) throws IOException {
        BERSequenceGenerator cGen = new BERSequenceGenerator(out);
        cGen.addObject(CMSObjectIdentifiers.envelopedData);
        BERSequenceGenerator envGen = new BERSequenceGenerator(cGen.getRawOutputStream(), 0, true);
        envGen.addObject(getVersion());
        if (this.originatorInfo != null) {
            envGen.addObject(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        if (this._berEncodeRecipientSet) {
            envGen.getRawOutputStream().write(new BERSet(recipientInfos).getEncoded());
        } else {
            envGen.getRawOutputStream().write(new DERSet(recipientInfos).getEncoded());
        }
        BERSequenceGenerator eiGen = new BERSequenceGenerator(envGen.getRawOutputStream());
        eiGen.addObject(dataType);
        AlgorithmIdentifier encAlgId = encryptor.getAlgorithmIdentifier();
        eiGen.getRawOutputStream().write(encAlgId.getEncoded());
        OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, false, this._bufferSize);
        OutputStream cOut = encryptor.getOutputStream(octetStream);
        return new CmsEnvelopedDataOutputStream(cOut, cGen, envGen, eiGen);
    }

    protected OutputStream open(OutputStream out, ASN1EncodableVector recipientInfos, OutputEncryptor encryptor) throws CMSException {
        ASN1Set recipients;
        try {
            BERSequenceGenerator cGen = new BERSequenceGenerator(out);
            cGen.addObject(CMSObjectIdentifiers.envelopedData);
            BERSequenceGenerator envGen = new BERSequenceGenerator(cGen.getRawOutputStream(), 0, true);
            if (this._berEncodeRecipientSet) {
                recipients = new BERSet(recipientInfos);
            } else {
                recipients = new DERSet(recipientInfos);
            }
            envGen.addObject(new ASN1Integer(EnvelopedData.calculateVersion(this.originatorInfo, recipients, this._unprotectedAttributes)));
            if (this.originatorInfo != null) {
                envGen.addObject(new DERTaggedObject(false, 0, this.originatorInfo));
            }
            envGen.getRawOutputStream().write(recipients.getEncoded());
            BERSequenceGenerator eiGen = new BERSequenceGenerator(envGen.getRawOutputStream());
            eiGen.addObject(CMSObjectIdentifiers.data);
            AlgorithmIdentifier encAlgId = encryptor.getAlgorithmIdentifier();
            eiGen.getRawOutputStream().write(encAlgId.getEncoded());
            OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, false, this._bufferSize);
            return new CmsEnvelopedDataOutputStream(encryptor.getOutputStream(octetStream), cGen, envGen, eiGen);
        } catch (IOException e) {
            throw new CMSException("exception decoding algorithm parameters.", e);
        }
    }

    public OutputStream open(OutputStream out, OutputEncryptor encryptor) throws IOException, CMSException {
        return doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), out, encryptor);
    }

    public OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, OutputEncryptor encryptor) throws IOException, CMSException {
        return doOpen(dataType, out, encryptor);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSEnvelopedDataStreamGenerator$CmsEnvelopedDataOutputStream.class */
    private class CmsEnvelopedDataOutputStream extends OutputStream {
        private OutputStream _out;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _envGen;
        private BERSequenceGenerator _eiGen;

        public CmsEnvelopedDataOutputStream(OutputStream out, BERSequenceGenerator cGen, BERSequenceGenerator envGen, BERSequenceGenerator eiGen) {
            this._out = out;
            this._cGen = cGen;
            this._envGen = envGen;
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
            if (CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator != null) {
                AttributeTable attrTable = CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator.getAttributes(new HashMap());
                ASN1Set unprotectedAttrs = new BERSet(attrTable.toASN1EncodableVector());
                this._envGen.addObject(new DERTaggedObject(false, 1, unprotectedAttrs));
            }
            this._envGen.close();
            this._cGen.close();
        }
    }
}
