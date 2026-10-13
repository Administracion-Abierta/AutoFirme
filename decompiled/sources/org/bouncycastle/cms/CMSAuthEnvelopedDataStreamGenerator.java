package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.operator.OutputAEADEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthEnvelopedDataStreamGenerator.class */
public class CMSAuthEnvelopedDataStreamGenerator extends CMSAuthEnvelopedGenerator {
    private int _bufferSize;
    private boolean _berEncodeRecipientSet;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthEnvelopedDataStreamGenerator$CMSAuthEnvelopedDataOutputStream.class */
    private class CMSAuthEnvelopedDataOutputStream extends OutputStream {
        private final OutputAEADEncryptor _encryptor;
        private final OutputStream _cOut;
        private final OutputStream _octetStream;
        private final BERSequenceGenerator _cGen;
        private final BERSequenceGenerator _envGen;
        private final BERSequenceGenerator _eiGen;

        public CMSAuthEnvelopedDataOutputStream(OutputAEADEncryptor outputAEADEncryptor, OutputStream outputStream, BERSequenceGenerator bERSequenceGenerator, BERSequenceGenerator bERSequenceGenerator2, BERSequenceGenerator bERSequenceGenerator3) {
            this._encryptor = outputAEADEncryptor;
            this._octetStream = outputStream;
            this._cOut = outputAEADEncryptor.getOutputStream(outputStream);
            this._cGen = bERSequenceGenerator;
            this._envGen = bERSequenceGenerator2;
            this._eiGen = bERSequenceGenerator3;
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            this._cOut.write(i);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            this._cOut.write(bArr, i, i2);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            this._cOut.write(bArr);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            ASN1Set aSN1SetProcessAuthAttrSet = CMSUtils.processAuthAttrSet(CMSAuthEnvelopedDataStreamGenerator.this.authAttrsGenerator, this._encryptor);
            this._cOut.close();
            this._octetStream.close();
            this._eiGen.close();
            if (aSN1SetProcessAuthAttrSet != null) {
                this._envGen.addObject((ASN1Primitive) new DERTaggedObject(false, 1, (ASN1Encodable) aSN1SetProcessAuthAttrSet));
            }
            this._envGen.addObject((ASN1Primitive) new DEROctetString(this._encryptor.getMAC()));
            CMSUtils.addAttriSetToGenerator(this._envGen, CMSAuthEnvelopedDataStreamGenerator.this.unauthAttrsGenerator, 2, Collections.EMPTY_MAP);
            this._envGen.close();
            this._cGen.close();
        }
    }

    public void setBufferSize(int i) {
        this._bufferSize = i;
    }

    public void setBEREncodeRecipients(boolean z) {
        this._berEncodeRecipientSet = z;
    }

    private OutputStream doOpen(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, OutputAEADEncryptor outputAEADEncryptor) throws CMSException, IOException {
        return open(aSN1ObjectIdentifier, outputStream, CMSUtils.getRecipentInfos(outputAEADEncryptor.getKey(), this.recipientInfoGenerators), outputAEADEncryptor);
    }

    protected OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, ASN1EncodableVector aSN1EncodableVector, OutputAEADEncryptor outputAEADEncryptor) throws IOException {
        BERSequenceGenerator bERSequenceGenerator = new BERSequenceGenerator(outputStream);
        bERSequenceGenerator.addObject((ASN1Primitive) CMSObjectIdentifiers.authEnvelopedData);
        BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator.getRawOutputStream(), 0, true);
        bERSequenceGenerator2.addObject((ASN1Primitive) new ASN1Integer(0L));
        CMSUtils.addOriginatorInfoToGenerator(bERSequenceGenerator2, this.originatorInfo);
        CMSUtils.addRecipientInfosToGenerator(aSN1EncodableVector, bERSequenceGenerator2, this._berEncodeRecipientSet);
        BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
        bERSequenceGenerator3.addObject((ASN1Primitive) aSN1ObjectIdentifier);
        bERSequenceGenerator3.getRawOutputStream().write(outputAEADEncryptor.getAlgorithmIdentifier().getEncoded());
        return new CMSAuthEnvelopedDataOutputStream(outputAEADEncryptor, CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize), bERSequenceGenerator, bERSequenceGenerator2, bERSequenceGenerator3);
    }

    protected OutputStream open(OutputStream outputStream, ASN1EncodableVector aSN1EncodableVector, OutputAEADEncryptor outputAEADEncryptor) throws CMSException {
        try {
            return open(CMSObjectIdentifiers.data, outputStream, aSN1EncodableVector, outputAEADEncryptor);
        } catch (IOException e) {
            throw new CMSException("exception decoding algorithm parameters.", e);
        }
    }

    public OutputStream open(OutputStream outputStream, OutputAEADEncryptor outputAEADEncryptor) throws CMSException, IOException {
        return doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), outputStream, outputAEADEncryptor);
    }
}
