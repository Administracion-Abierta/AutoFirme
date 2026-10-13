package org.spongycastle.crypto.encodings;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/encodings/PKCS1Encoding.class */
public class PKCS1Encoding implements AsymmetricBlockCipher {
    public static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.spongycastle.pkcs1.strict";
    public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "org.spongycastle.pkcs1.not_strict";
    private static final int HEADER_LENGTH = 10;
    private SecureRandom random;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private boolean useStrictLength;
    private int pLen;
    private byte[] fallback;
    private byte[] blockBuffer;

    public PKCS1Encoding(AsymmetricBlockCipher cipher) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = cipher;
        this.useStrictLength = useStrict();
    }

    public PKCS1Encoding(AsymmetricBlockCipher cipher, int pLen) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = cipher;
        this.useStrictLength = useStrict();
        this.pLen = pLen;
    }

    public PKCS1Encoding(AsymmetricBlockCipher cipher, byte[] fallback) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = cipher;
        this.useStrictLength = useStrict();
        this.fallback = fallback;
        this.pLen = fallback.length;
    }

    private boolean useStrict() {
        String strict = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.spongycastle.crypto.encodings.PKCS1Encoding.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return System.getProperty(PKCS1Encoding.STRICT_LENGTH_ENABLED_PROPERTY);
            }
        });
        String notStrict = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.spongycastle.crypto.encodings.PKCS1Encoding.2
            @Override // java.security.PrivilegedAction
            public Object run() {
                return System.getProperty(PKCS1Encoding.NOT_STRICT_LENGTH_ENABLED_PROPERTY);
            }
        });
        if (notStrict != null) {
            return !notStrict.equals("true");
        }
        return strict == null || strict.equals("true");
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        AsymmetricKeyParameter kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.random = rParam.getRandom();
            kParam = (AsymmetricKeyParameter) rParam.getParameters();
        } else {
            kParam = (AsymmetricKeyParameter) param;
            if (!kParam.isPrivate() && forEncryption) {
                this.random = new SecureRandom();
            }
        }
        this.engine.init(forEncryption, param);
        this.forPrivateKey = kParam.isPrivate();
        this.forEncryption = forEncryption;
        this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
        if (this.pLen > 0 && this.fallback == null && this.random == null) {
            throw new IllegalArgumentException("encoder requires random");
        }
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize - 10;
        }
        return baseBlockSize;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return baseBlockSize - 10;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return encodeBlock(in, inOff, inLen);
        }
        return decodeBlock(in, inOff, inLen);
    }

    private byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (inLen > getInputBlockSize()) {
            throw new IllegalArgumentException("input data too large");
        }
        byte[] block = new byte[this.engine.getInputBlockSize()];
        if (this.forPrivateKey) {
            block[0] = 1;
            for (int i = 1; i != (block.length - inLen) - 1; i++) {
                block[i] = -1;
            }
        } else {
            this.random.nextBytes(block);
            block[0] = 2;
            for (int i2 = 1; i2 != (block.length - inLen) - 1; i2++) {
                while (block[i2] == 0) {
                    block[i2] = (byte) this.random.nextInt();
                }
            }
        }
        block[(block.length - inLen) - 1] = 0;
        System.arraycopy(in, inOff, block, block.length - inLen, inLen);
        return this.engine.processBlock(block, 0, block.length);
    }

    private static int checkPkcs1Encoding(byte[] encoded, int pLen) {
        int correct = 0 | (encoded[0] ^ 2);
        int plen = encoded.length - (pLen + 1);
        for (int i = 1; i < plen; i++) {
            byte b = encoded[i];
            int tmp = b | (b >> 1);
            int tmp2 = tmp | (tmp >> 2);
            correct |= ((tmp2 | (tmp2 >> 4)) & 1) - 1;
        }
        int correct2 = correct | encoded[encoded.length - (pLen + 1)];
        int correct3 = correct2 | (correct2 >> 1);
        int correct4 = correct3 | (correct3 >> 2);
        return (((correct4 | (correct4 >> 4)) & 1) - 1) ^ (-1);
    }

    private byte[] decodeBlockOrRandom(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] random;
        if (!this.forPrivateKey) {
            throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing");
        }
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        if (this.fallback == null) {
            random = new byte[this.pLen];
            this.random.nextBytes(random);
        } else {
            random = this.fallback;
        }
        byte[] data = this.useStrictLength & (block.length != this.engine.getOutputBlockSize()) ? this.blockBuffer : block;
        int correct = checkPkcs1Encoding(data, this.pLen);
        byte[] result = new byte[this.pLen];
        for (int i = 0; i < this.pLen; i++) {
            result[i] = (byte) ((data[i + (data.length - this.pLen)] & (correct ^ (-1))) | (random[i] & correct));
        }
        Arrays.fill(data, (byte) 0);
        return result;
    }

    private byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] data;
        boolean badType;
        if (this.pLen != -1) {
            return decodeBlockOrRandom(in, inOff, inLen);
        }
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        boolean incorrectLength = this.useStrictLength & (block.length != this.engine.getOutputBlockSize());
        if (block.length < getOutputBlockSize()) {
            data = this.blockBuffer;
        } else {
            data = block;
        }
        byte type = data[0];
        if (this.forPrivateKey) {
            badType = type != 2;
        } else {
            badType = type != 1;
        }
        int start = findStart(type, data) + 1;
        if (badType | (start < 10)) {
            Arrays.fill(data, (byte) 0);
            throw new InvalidCipherTextException("block incorrect");
        }
        if (incorrectLength) {
            Arrays.fill(data, (byte) 0);
            throw new InvalidCipherTextException("block incorrect size");
        }
        byte[] result = new byte[data.length - start];
        System.arraycopy(data, start, result, 0, result.length);
        return result;
    }

    private int findStart(byte type, byte[] block) throws InvalidCipherTextException {
        int start = -1;
        boolean padErr = false;
        for (int i = 1; i != block.length; i++) {
            byte pad = block[i];
            if ((pad == 0) & (start < 0)) {
                start = i;
            }
            padErr |= (type == 1) & (start < 0) & (pad != -1);
        }
        if (padErr) {
            return -1;
        }
        return start;
    }
}
