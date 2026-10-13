package org.spongycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/jcajce/JcaDigestCalculatorProviderBuilder.class */
public class JcaDigestCalculatorProviderBuilder {
    private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    public JcaDigestCalculatorProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaDigestCalculatorProviderBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public DigestCalculatorProvider build() throws OperatorCreationException {
        return new DigestCalculatorProvider() { // from class: org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder.1
            @Override // org.spongycastle.operator.DigestCalculatorProvider
            public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
                try {
                    MessageDigest dig = JcaDigestCalculatorProviderBuilder.this.helper.createDigest(algorithm);
                    final DigestOutputStream stream = JcaDigestCalculatorProviderBuilder.this.new DigestOutputStream(dig);
                    return new DigestCalculator() { // from class: org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder.1.1
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
                } catch (GeneralSecurityException e) {
                    throw new OperatorCreationException("exception on setup: " + e, e);
                }
            }
        };
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/jcajce/JcaDigestCalculatorProviderBuilder$DigestOutputStream.class */
    private class DigestOutputStream extends OutputStream {
        private MessageDigest dig;

        DigestOutputStream(MessageDigest dig) {
            this.dig = dig;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dig.update(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this.dig.update(bytes);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.dig.update((byte) b);
        }

        byte[] getDigest() {
            return this.dig.digest();
        }
    }
}
