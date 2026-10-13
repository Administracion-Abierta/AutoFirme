package org.spongycastle.cms.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSAlgorithm;
import org.spongycastle.cms.CMSException;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.io.CipherOutputStream;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OutputEncryptor;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/bc/BcCMSContentEncryptorBuilder.class */
public class BcCMSContentEncryptorBuilder {
    private static Map keySizes = new HashMap();
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private SecureRandom random;

    static {
        keySizes.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
        keySizes.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
        keySizes.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
        keySizes.put(CMSAlgorithm.CAMELLIA128_CBC, Integers.valueOf(128));
        keySizes.put(CMSAlgorithm.CAMELLIA192_CBC, Integers.valueOf(192));
        keySizes.put(CMSAlgorithm.CAMELLIA256_CBC, Integers.valueOf(256));
    }

    private static int getKeySize(ASN1ObjectIdentifier oid) {
        Integer size = (Integer) keySizes.get(oid);
        if (size != null) {
            return size.intValue();
        }
        return -1;
    }

    public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID) {
        this(encryptionOID, getKeySize(encryptionOID));
    }

    public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID, int keySize) {
        this.helper = new EnvelopedDataHelper();
        this.encryptionOID = encryptionOID;
        this.keySize = keySize;
    }

    public BcCMSContentEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public OutputEncryptor build() throws CMSException {
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/bc/BcCMSContentEncryptorBuilder$CMSOutputEncryptor.class */
    private class CMSOutputEncryptor implements OutputEncryptor {
        private KeyParameter encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Object cipher;

        CMSOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, SecureRandom random) throws CMSException {
            random = random == null ? new SecureRandom() : random;
            CipherKeyGenerator keyGen = BcCMSContentEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID, random);
            this.encKey = new KeyParameter(keyGen.generateKey());
            this.algorithmIdentifier = BcCMSContentEncryptorBuilder.this.helper.generateAlgorithmIdentifier(encryptionOID, this.encKey, random);
            EnvelopedDataHelper unused = BcCMSContentEncryptorBuilder.this.helper;
            this.cipher = EnvelopedDataHelper.createContentCipher(true, this.encKey, this.algorithmIdentifier);
        }

        @Override // org.spongycastle.operator.OutputEncryptor
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override // org.spongycastle.operator.OutputEncryptor
        public OutputStream getOutputStream(OutputStream dOut) {
            if (this.cipher instanceof BufferedBlockCipher) {
                return new CipherOutputStream(dOut, (BufferedBlockCipher) this.cipher);
            }
            return new CipherOutputStream(dOut, (StreamCipher) this.cipher);
        }

        @Override // org.spongycastle.operator.OutputEncryptor
        public GenericKey getKey() {
            return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
        }
    }
}
