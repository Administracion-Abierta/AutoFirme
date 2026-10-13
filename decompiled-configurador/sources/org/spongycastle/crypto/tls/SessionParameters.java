package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/SessionParameters.class */
public final class SessionParameters {
    private int cipherSuite;
    private short compressionAlgorithm;
    private byte[] masterSecret;
    private Certificate peerCertificate;
    private byte[] pskIdentity;
    private byte[] srpIdentity;
    private byte[] encodedServerExtensions;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/SessionParameters$Builder.class */
    public static final class Builder {
        private int cipherSuite = -1;
        private short compressionAlgorithm = -1;
        private byte[] masterSecret = null;
        private Certificate peerCertificate = null;
        private byte[] pskIdentity = null;
        private byte[] srpIdentity = null;
        private byte[] encodedServerExtensions = null;

        public SessionParameters build() {
            validate(this.cipherSuite >= 0, "cipherSuite");
            validate(this.compressionAlgorithm >= 0, "compressionAlgorithm");
            validate(this.masterSecret != null, "masterSecret");
            return new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions);
        }

        public Builder setCipherSuite(int cipherSuite) {
            this.cipherSuite = cipherSuite;
            return this;
        }

        public Builder setCompressionAlgorithm(short compressionAlgorithm) {
            this.compressionAlgorithm = compressionAlgorithm;
            return this;
        }

        public Builder setMasterSecret(byte[] masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        public Builder setPeerCertificate(Certificate peerCertificate) {
            this.peerCertificate = peerCertificate;
            return this;
        }

        public Builder setPskIdentity(byte[] pskIdentity) {
            this.pskIdentity = pskIdentity;
            return this;
        }

        public Builder setPSKIdentity(byte[] pskIdentity) {
            this.pskIdentity = pskIdentity;
            return this;
        }

        public Builder setSRPIdentity(byte[] srpIdentity) {
            this.srpIdentity = srpIdentity;
            return this;
        }

        public Builder setServerExtensions(Hashtable serverExtensions) throws IOException {
            if (serverExtensions == null) {
                this.encodedServerExtensions = null;
            } else {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                TlsProtocol.writeExtensions(buf, serverExtensions);
                this.encodedServerExtensions = buf.toByteArray();
            }
            return this;
        }

        private void validate(boolean condition, String parameter) {
            if (!condition) {
                throw new IllegalStateException("Required session parameter '" + parameter + "' not configured");
            }
        }
    }

    private SessionParameters(int cipherSuite, short compressionAlgorithm, byte[] masterSecret, Certificate peerCertificate, byte[] pskIdentity, byte[] srpIdentity, byte[] encodedServerExtensions) {
        this.pskIdentity = null;
        this.srpIdentity = null;
        this.cipherSuite = cipherSuite;
        this.compressionAlgorithm = compressionAlgorithm;
        this.masterSecret = Arrays.clone(masterSecret);
        this.peerCertificate = peerCertificate;
        this.pskIdentity = Arrays.clone(pskIdentity);
        this.srpIdentity = Arrays.clone(srpIdentity);
        this.encodedServerExtensions = encodedServerExtensions;
    }

    public void clear() {
        if (this.masterSecret != null) {
            Arrays.fill(this.masterSecret, (byte) 0);
        }
    }

    public SessionParameters copy() {
        return new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions);
    }

    public int getCipherSuite() {
        return this.cipherSuite;
    }

    public short getCompressionAlgorithm() {
        return this.compressionAlgorithm;
    }

    public byte[] getMasterSecret() {
        return this.masterSecret;
    }

    public Certificate getPeerCertificate() {
        return this.peerCertificate;
    }

    public byte[] getPskIdentity() {
        return this.pskIdentity;
    }

    public byte[] getPSKIdentity() {
        return this.pskIdentity;
    }

    public byte[] getSRPIdentity() {
        return this.srpIdentity;
    }

    public Hashtable readServerExtensions() throws IOException {
        if (this.encodedServerExtensions == null) {
            return null;
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(this.encodedServerExtensions);
        return TlsProtocol.readExtensions(buf);
    }
}
