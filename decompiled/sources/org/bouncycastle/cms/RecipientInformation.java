package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/RecipientInformation.class */
public abstract class RecipientInformation {
    protected RecipientId rid;
    protected AlgorithmIdentifier keyEncAlg;
    protected AlgorithmIdentifier messageAlgorithm;
    protected CMSSecureReadable secureReadable;
    private byte[] resultMac;
    private RecipientOperator operator;

    RecipientInformation(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, CMSSecureReadable cMSSecureReadable) {
        this.keyEncAlg = algorithmIdentifier;
        this.messageAlgorithm = algorithmIdentifier2;
        this.secureReadable = cMSSecureReadable;
    }

    public RecipientId getRID() {
        return this.rid;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncAlg;
    }

    public String getKeyEncryptionAlgOID() {
        return this.keyEncAlg.getAlgorithm().getId();
    }

    public byte[] getKeyEncryptionAlgParams() {
        try {
            return CMSUtils.encodeObj(this.keyEncAlg.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public byte[] getContentDigest() {
        if (this.secureReadable instanceof CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable) {
            return ((CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable) this.secureReadable).getDigest();
        }
        return null;
    }

    public byte[] getMac() {
        if (this.resultMac == null) {
            if (this.operator.isMacBased() && this.secureReadable.hasAdditionalData()) {
                try {
                    Streams.drain(this.operator.getInputStream(new ByteArrayInputStream(this.secureReadable.getAuthAttrSet().getEncoded("DER"))));
                } catch (IOException e) {
                    throw new IllegalStateException("unable to drain input: " + e.getMessage());
                }
            }
            this.resultMac = this.operator.getMac();
        }
        return this.resultMac;
    }

    public byte[] getContent(Recipient recipient) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(getContentStream(recipient).getContentStream());
        } catch (IOException e) {
            throw new CMSException("unable to parse internal stream: " + e.getMessage(), e);
        }
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.secureReadable.getContentType();
    }

    public CMSTypedStream getContentStream(Recipient recipient) throws CMSException, IOException {
        this.operator = getRecipientOperator(recipient);
        if (this.operator.isAEADBased()) {
            ((CMSSecureReadableWithAAD) this.secureReadable).setAADStream(this.operator.getAADStream());
        } else if (this.secureReadable.hasAdditionalData()) {
            return new CMSTypedStream(this.secureReadable.getContentType(), this.secureReadable.getInputStream());
        }
        return new CMSTypedStream(this.secureReadable.getContentType(), this.operator.getInputStream(this.secureReadable.getInputStream()));
    }

    protected abstract RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException, IOException;
}
