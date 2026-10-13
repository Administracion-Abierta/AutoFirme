package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsStreamCipher.class */
public class TlsStreamCipher implements TlsCipher {
    protected TlsContext context;
    protected StreamCipher encryptCipher;
    protected StreamCipher decryptCipher;
    protected TlsMac writeMac;
    protected TlsMac readMac;
    protected boolean usesNonce;

    public TlsStreamCipher(TlsContext context, StreamCipher clientWriteCipher, StreamCipher serverWriteCipher, Digest clientWriteDigest, Digest serverWriteDigest, int cipherKeySize, boolean usesNonce) throws IOException {
        CipherParameters encryptParams;
        CipherParameters decryptParams;
        boolean isServer = context.isServer();
        this.context = context;
        this.usesNonce = usesNonce;
        this.encryptCipher = clientWriteCipher;
        this.decryptCipher = serverWriteCipher;
        int key_block_size = (2 * cipherKeySize) + clientWriteDigest.getDigestSize() + serverWriteDigest.getDigestSize();
        byte[] key_block = TlsUtils.calculateKeyBlock(context, key_block_size);
        TlsMac clientWriteMac = new TlsMac(context, clientWriteDigest, key_block, 0, clientWriteDigest.getDigestSize());
        int offset = 0 + clientWriteDigest.getDigestSize();
        TlsMac serverWriteMac = new TlsMac(context, serverWriteDigest, key_block, offset, serverWriteDigest.getDigestSize());
        int offset2 = offset + serverWriteDigest.getDigestSize();
        KeyParameter clientWriteKey = new KeyParameter(key_block, offset2, cipherKeySize);
        int offset3 = offset2 + cipherKeySize;
        KeyParameter serverWriteKey = new KeyParameter(key_block, offset3, cipherKeySize);
        if (offset3 + cipherKeySize != key_block_size) {
            throw new TlsFatalAlert((short) 80);
        }
        if (isServer) {
            this.writeMac = serverWriteMac;
            this.readMac = clientWriteMac;
            this.encryptCipher = serverWriteCipher;
            this.decryptCipher = clientWriteCipher;
            encryptParams = serverWriteKey;
            decryptParams = clientWriteKey;
        } else {
            this.writeMac = clientWriteMac;
            this.readMac = serverWriteMac;
            this.encryptCipher = clientWriteCipher;
            this.decryptCipher = serverWriteCipher;
            encryptParams = clientWriteKey;
            decryptParams = serverWriteKey;
        }
        if (usesNonce) {
            byte[] dummyNonce = new byte[8];
            encryptParams = new ParametersWithIV(encryptParams, dummyNonce);
            decryptParams = new ParametersWithIV(decryptParams, dummyNonce);
        }
        this.encryptCipher.init(true, encryptParams);
        this.decryptCipher.init(false, decryptParams);
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public int getPlaintextLimit(int ciphertextLimit) {
        return ciphertextLimit - this.writeMac.getSize();
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] encodePlaintext(long seqNo, short type, byte[] plaintext, int offset, int len) {
        if (this.usesNonce) {
            updateIV(this.encryptCipher, true, seqNo);
        }
        byte[] outBuf = new byte[len + this.writeMac.getSize()];
        this.encryptCipher.processBytes(plaintext, offset, len, outBuf, 0);
        byte[] mac = this.writeMac.calculateMac(seqNo, type, plaintext, offset, len);
        this.encryptCipher.processBytes(mac, 0, mac.length, outBuf, len);
        return outBuf;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] decodeCiphertext(long seqNo, short type, byte[] ciphertext, int offset, int len) throws IOException {
        if (this.usesNonce) {
            updateIV(this.decryptCipher, false, seqNo);
        }
        int macSize = this.readMac.getSize();
        if (len < macSize) {
            throw new TlsFatalAlert((short) 50);
        }
        int plaintextLength = len - macSize;
        byte[] deciphered = new byte[len];
        this.decryptCipher.processBytes(ciphertext, offset, len, deciphered, 0);
        checkMAC(seqNo, type, deciphered, plaintextLength, len, deciphered, 0, plaintextLength);
        return Arrays.copyOfRange(deciphered, 0, plaintextLength);
    }

    protected void checkMAC(long seqNo, short type, byte[] recBuf, int recStart, int recEnd, byte[] calcBuf, int calcOff, int calcLen) throws IOException {
        byte[] receivedMac = Arrays.copyOfRange(recBuf, recStart, recEnd);
        byte[] computedMac = this.readMac.calculateMac(seqNo, type, calcBuf, calcOff, calcLen);
        if (!Arrays.constantTimeAreEqual(receivedMac, computedMac)) {
            throw new TlsFatalAlert((short) 20);
        }
    }

    protected void updateIV(StreamCipher cipher, boolean forEncryption, long seqNo) {
        byte[] nonce = new byte[8];
        TlsUtils.writeUint64(seqNo, nonce, 0);
        cipher.init(forEncryption, new ParametersWithIV(null, nonce));
    }
}
