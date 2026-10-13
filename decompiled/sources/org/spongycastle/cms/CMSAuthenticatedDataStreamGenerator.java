package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BERSequenceGenerator;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.AuthenticatedData;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSAuthenticatedDataStreamGenerator.class */
public class CMSAuthenticatedDataStreamGenerator extends CMSAuthenticatedGenerator {
    private int bufferSize;
    private boolean berEncodeRecipientSet;
    private MacCalculator macCalculator;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setBEREncodeRecipients(boolean useBerEncodingForRecipients) {
        this.berEncodeRecipientSet = useBerEncodingForRecipients;
    }

    public OutputStream open(OutputStream out, MacCalculator macCalculator) throws CMSException {
        return open(CMSObjectIdentifiers.data, out, macCalculator);
    }

    public OutputStream open(OutputStream out, MacCalculator macCalculator, DigestCalculator digestCalculator) throws CMSException {
        return open(CMSObjectIdentifiers.data, out, macCalculator, digestCalculator);
    }

    public OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, MacCalculator macCalculator) throws CMSException {
        return open(dataType, out, macCalculator, null);
    }

    public OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, MacCalculator macCalculator, DigestCalculator digestCalculator) throws CMSException {
        OutputStream mOut;
        this.macCalculator = macCalculator;
        try {
            ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
            for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
                recipientInfos.add(recipient.generate(macCalculator.getKey()));
            }
            BERSequenceGenerator cGen = new BERSequenceGenerator(out);
            cGen.addObject(CMSObjectIdentifiers.authenticatedData);
            BERSequenceGenerator authGen = new BERSequenceGenerator(cGen.getRawOutputStream(), 0, true);
            authGen.addObject(new ASN1Integer(AuthenticatedData.calculateVersion(this.originatorInfo)));
            if (this.originatorInfo != null) {
                authGen.addObject(new DERTaggedObject(false, 0, this.originatorInfo));
            }
            if (this.berEncodeRecipientSet) {
                authGen.getRawOutputStream().write(new BERSet(recipientInfos).getEncoded());
            } else {
                authGen.getRawOutputStream().write(new DERSet(recipientInfos).getEncoded());
            }
            AlgorithmIdentifier macAlgId = macCalculator.getAlgorithmIdentifier();
            authGen.getRawOutputStream().write(macAlgId.getEncoded());
            if (digestCalculator != null) {
                authGen.addObject(new DERTaggedObject(false, 1, digestCalculator.getAlgorithmIdentifier()));
            }
            BERSequenceGenerator eiGen = new BERSequenceGenerator(authGen.getRawOutputStream());
            eiGen.addObject(dataType);
            OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, false, this.bufferSize);
            if (digestCalculator != null) {
                mOut = new TeeOutputStream(octetStream, digestCalculator.getOutputStream());
            } else {
                mOut = new TeeOutputStream(octetStream, macCalculator.getOutputStream());
            }
            return new CmsAuthenticatedDataOutputStream(macCalculator, digestCalculator, dataType, mOut, cGen, authGen, eiGen);
        } catch (IOException e) {
            throw new CMSException("exception decoding algorithm parameters.", e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSAuthenticatedDataStreamGenerator$CmsAuthenticatedDataOutputStream.class */
    private class CmsAuthenticatedDataOutputStream extends OutputStream {
        private OutputStream dataStream;
        private BERSequenceGenerator cGen;
        private BERSequenceGenerator envGen;
        private BERSequenceGenerator eiGen;
        private MacCalculator macCalculator;
        private DigestCalculator digestCalculator;
        private ASN1ObjectIdentifier contentType;

        public CmsAuthenticatedDataOutputStream(MacCalculator macCalculator, DigestCalculator digestCalculator, ASN1ObjectIdentifier contentType, OutputStream dataStream, BERSequenceGenerator cGen, BERSequenceGenerator envGen, BERSequenceGenerator eiGen) {
            this.macCalculator = macCalculator;
            this.digestCalculator = digestCalculator;
            this.contentType = contentType;
            this.dataStream = dataStream;
            this.cGen = cGen;
            this.envGen = envGen;
            this.eiGen = eiGen;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.dataStream.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dataStream.write(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this.dataStream.write(bytes);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            Map parameters;
            this.dataStream.close();
            this.eiGen.close();
            if (this.digestCalculator != null) {
                parameters = Collections.unmodifiableMap(CMSAuthenticatedDataStreamGenerator.this.getBaseParameters(this.contentType, this.digestCalculator.getAlgorithmIdentifier(), this.macCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
                if (CMSAuthenticatedDataStreamGenerator.this.authGen == null) {
                    CMSAuthenticatedDataStreamGenerator.this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
                }
                ASN1Set authed = new DERSet(CMSAuthenticatedDataStreamGenerator.this.authGen.getAttributes(parameters).toASN1EncodableVector());
                OutputStream mOut = this.macCalculator.getOutputStream();
                mOut.write(authed.getEncoded("DER"));
                mOut.close();
                this.envGen.addObject(new DERTaggedObject(false, 2, authed));
            } else {
                parameters = Collections.unmodifiableMap(new HashMap());
            }
            this.envGen.addObject(new DEROctetString(this.macCalculator.getMac()));
            if (CMSAuthenticatedDataStreamGenerator.this.unauthGen != null) {
                this.envGen.addObject(new DERTaggedObject(false, 3, new BERSet(CMSAuthenticatedDataStreamGenerator.this.unauthGen.getAttributes(parameters).toASN1EncodableVector())));
            }
            this.envGen.close();
            this.cGen.close();
        }
    }
}
