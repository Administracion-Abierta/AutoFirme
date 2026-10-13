package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsNullCipher.class */
public class TlsNullCipher implements TlsCipher {
    protected TlsContext context;
    protected TlsMac writeMac;
    protected TlsMac readMac;

    public TlsNullCipher(TlsContext context) {
        this.context = context;
        this.writeMac = null;
        this.readMac = null;
    }

    public TlsNullCipher(TlsContext context, Digest clientWriteDigest, Digest serverWriteDigest) throws IOException {
        if ((clientWriteDigest == null) != (serverWriteDigest == null)) {
            throw new TlsFatalAlert((short) 80);
        }
        this.context = context;
        TlsMac clientWriteMac = null;
        TlsMac serverWriteMac = null;
        if (clientWriteDigest != null) {
            int key_block_size = clientWriteDigest.getDigestSize() + serverWriteDigest.getDigestSize();
            byte[] key_block = TlsUtils.calculateKeyBlock(context, key_block_size);
            clientWriteMac = new TlsMac(context, clientWriteDigest, key_block, 0, clientWriteDigest.getDigestSize());
            int offset = 0 + clientWriteDigest.getDigestSize();
            serverWriteMac = new TlsMac(context, serverWriteDigest, key_block, offset, serverWriteDigest.getDigestSize());
            if (offset + serverWriteDigest.getDigestSize() != key_block_size) {
                throw new TlsFatalAlert((short) 80);
            }
        }
        if (context.isServer()) {
            this.writeMac = serverWriteMac;
            this.readMac = clientWriteMac;
        } else {
            this.writeMac = clientWriteMac;
            this.readMac = serverWriteMac;
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public int getPlaintextLimit(int ciphertextLimit) {
        int result = ciphertextLimit;
        if (this.writeMac != null) {
            result -= this.writeMac.getSize();
        }
        return result;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] encodePlaintext(long seqNo, short type, byte[] plaintext, int offset, int len) throws IOException {
        if (this.writeMac == null) {
            return Arrays.copyOfRange(plaintext, offset, offset + len);
        }
        byte[] mac = this.writeMac.calculateMac(seqNo, type, plaintext, offset, len);
        byte[] ciphertext = new byte[len + mac.length];
        System.arraycopy(plaintext, offset, ciphertext, 0, len);
        System.arraycopy(mac, 0, ciphertext, len, mac.length);
        return ciphertext;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] decodeCiphertext(long seqNo, short type, byte[] ciphertext, int offset, int len) throws IOException {
        if (this.readMac == null) {
            return Arrays.copyOfRange(ciphertext, offset, offset + len);
        }
        int macSize = this.readMac.getSize();
        if (len < macSize) {
            throw new TlsFatalAlert((short) 50);
        }
        int macInputLen = len - macSize;
        byte[] receivedMac = Arrays.copyOfRange(ciphertext, offset + macInputLen, offset + len);
        byte[] computedMac = this.readMac.calculateMac(seqNo, type, ciphertext, offset, macInputLen);
        if (!Arrays.constantTimeAreEqual(receivedMac, computedMac)) {
            throw new TlsFatalAlert((short) 20);
        }
        return Arrays.copyOfRange(ciphertext, offset, offset + macInputLen);
    }
}
