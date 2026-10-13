package org.bouncycastle.cert.crmf.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/bc/BcCRMFEncryptorBuilder.class */
public class BcCRMFEncryptorBuilder {
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private CRMFHelper helper;
    private SecureRandom random;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/bc/BcCRMFEncryptorBuilder$CRMFOutputEncryptor.class */
    private class CRMFOutputEncryptor implements OutputEncryptor {
        private KeyParameter encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Object cipher;

        CRMFOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i, SecureRandom secureRandom) throws CRMFException {
            SecureRandom secureRandom2 = CryptoServicesRegistrar.getSecureRandom(secureRandom);
            this.encKey = new KeyParameter(BcCRMFEncryptorBuilder.this.helper.createKeyGenerator(aSN1ObjectIdentifier, secureRandom2).generateKey());
            this.algorithmIdentifier = BcCRMFEncryptorBuilder.this.helper.generateEncryptionAlgID(aSN1ObjectIdentifier, this.encKey, secureRandom2);
            CRMFHelper unused = BcCRMFEncryptorBuilder.this.helper;
            this.cipher = CRMFHelper.createContentCipher(true, this.encKey, this.algorithmIdentifier);
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public OutputStream getOutputStream(OutputStream outputStream) {
            return CipherFactory.createOutputStream(outputStream, this.cipher);
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public GenericKey getKey() {
            return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
        }
    }

    public BcCRMFEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, -1);
    }

    public BcCRMFEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        this.helper = new CRMFHelper();
        this.encryptionOID = aSN1ObjectIdentifier;
        this.keySize = i;
    }

    public BcCRMFEncryptorBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public OutputEncryptor build() throws CRMFException {
        return new CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }
}
