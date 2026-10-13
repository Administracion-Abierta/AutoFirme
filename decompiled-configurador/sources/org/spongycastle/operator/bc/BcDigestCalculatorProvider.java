package org.spongycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.Digest;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcDigestCalculatorProvider.class */
public class BcDigestCalculatorProvider implements DigestCalculatorProvider {
    private BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    @Override // org.spongycastle.operator.DigestCalculatorProvider
    public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
        Digest dig = this.digestProvider.get(algorithm);
        final DigestOutputStream stream = new DigestOutputStream(dig);
        return new DigestCalculator() { // from class: org.spongycastle.operator.bc.BcDigestCalculatorProvider.1
            @Override // org.spongycastle.operator.DigestCalculator
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithm;
            }

            @Override // org.spongycastle.operator.DigestCalculator
            public OutputStream getOutputStream() {
                return stream;
            }

            @Override // org.spongycastle.operator.DigestCalculator
            public byte[] getDigest() {
                return stream.getDigest();
            }
        };
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcDigestCalculatorProvider$DigestOutputStream.class */
    private class DigestOutputStream extends OutputStream {
        private Digest dig;

        DigestOutputStream(Digest dig) {
            this.dig = dig;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dig.update(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this.dig.update(bytes, 0, bytes.length);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.dig.update((byte) b);
        }

        byte[] getDigest() {
            byte[] d = new byte[this.dig.getDigestSize()];
            this.dig.doFinal(d, 0);
            return d;
        }
    }
}
