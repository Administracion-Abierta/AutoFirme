package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.BufferingOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/BufferingContentSigner.class */
public class BufferingContentSigner implements ExtendedContentSigner {
    private final ContentSigner contentSigner;
    private final OutputStream output;

    public BufferingContentSigner(ContentSigner contentSigner) {
        this.contentSigner = contentSigner;
        this.output = new BufferingOutputStream(contentSigner.getOutputStream());
    }

    public BufferingContentSigner(ContentSigner contentSigner, int i) {
        this.contentSigner = contentSigner;
        this.output = new BufferingOutputStream(contentSigner.getOutputStream(), i);
    }

    @Override // org.bouncycastle.operator.ContentSigner
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.contentSigner.getAlgorithmIdentifier();
    }

    @Override // org.bouncycastle.operator.ContentSigner
    public OutputStream getOutputStream() {
        return this.output;
    }

    @Override // org.bouncycastle.operator.ContentSigner
    public byte[] getSignature() {
        return this.contentSigner.getSignature();
    }

    @Override // org.bouncycastle.operator.ExtendedContentSigner
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        if (this.contentSigner instanceof ExtendedContentSigner) {
            return ((ExtendedContentSigner) this.contentSigner).getDigestAlgorithmIdentifier();
        }
        return null;
    }
}
