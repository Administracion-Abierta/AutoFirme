package org.spongycastle.cert.bc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.cert.X509ExtensionUtils;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/bc/BcX509ExtensionUtils.class */
public class BcX509ExtensionUtils extends X509ExtensionUtils {
    public BcX509ExtensionUtils() {
        super(new SHA1DigestCalculator());
    }

    public BcX509ExtensionUtils(DigestCalculator calculator) {
        super(calculator);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(AsymmetricKeyParameter publicKey) throws IOException {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey));
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(AsymmetricKeyParameter publicKey) throws IOException {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/bc/BcX509ExtensionUtils$SHA1DigestCalculator.class */
    private static class SHA1DigestCalculator implements DigestCalculator {
        private ByteArrayOutputStream bOut;

        private SHA1DigestCalculator() {
            this.bOut = new ByteArrayOutputStream();
        }

        @Override // org.spongycastle.operator.DigestCalculator
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }

        @Override // org.spongycastle.operator.DigestCalculator
        public OutputStream getOutputStream() {
            return this.bOut;
        }

        @Override // org.spongycastle.operator.DigestCalculator
        public byte[] getDigest() {
            byte[] bytes = this.bOut.toByteArray();
            this.bOut.reset();
            Digest sha1 = new SHA1Digest();
            sha1.update(bytes, 0, bytes.length);
            byte[] digest = new byte[sha1.getDigestSize()];
            sha1.doFinal(digest, 0);
            return digest;
        }
    }
}
