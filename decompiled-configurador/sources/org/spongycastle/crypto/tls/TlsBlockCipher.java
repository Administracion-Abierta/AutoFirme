package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.security.SecureRandom;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsBlockCipher.class */
public class TlsBlockCipher implements TlsCipher {
    protected TlsContext context;
    protected byte[] randomData = new byte[256];
    protected boolean useExplicitIV;
    protected boolean encryptThenMAC;
    protected BlockCipher encryptCipher;
    protected BlockCipher decryptCipher;
    protected TlsMac writeMac;
    protected TlsMac readMac;

    public TlsMac getWriteMac() {
        return this.writeMac;
    }

    public TlsMac getReadMac() {
        return this.readMac;
    }

    public TlsBlockCipher(TlsContext context, BlockCipher clientWriteCipher, BlockCipher serverWriteCipher, Digest clientWriteDigest, Digest serverWriteDigest, int cipherKeySize) throws IOException {
        byte[] client_write_IV;
        byte[] server_write_IV;
        CipherParameters encryptParams;
        CipherParameters decryptParams;
        this.context = context;
        context.getNonceRandomGenerator().nextBytes(this.randomData);
        this.useExplicitIV = TlsUtils.isTLSv11(context);
        this.encryptThenMAC = context.getSecurityParameters().encryptThenMAC;
        int key_block_size = (2 * cipherKeySize) + clientWriteDigest.getDigestSize() + serverWriteDigest.getDigestSize();
        key_block_size = this.useExplicitIV ? key_block_size : key_block_size + clientWriteCipher.getBlockSize() + serverWriteCipher.getBlockSize();
        byte[] key_block = TlsUtils.calculateKeyBlock(context, key_block_size);
        TlsMac clientWriteMac = new TlsMac(context, clientWriteDigest, key_block, 0, clientWriteDigest.getDigestSize());
        int offset = 0 + clientWriteDigest.getDigestSize();
        TlsMac serverWriteMac = new TlsMac(context, serverWriteDigest, key_block, offset, serverWriteDigest.getDigestSize());
        int offset2 = offset + serverWriteDigest.getDigestSize();
        KeyParameter client_write_key = new KeyParameter(key_block, offset2, cipherKeySize);
        int offset3 = offset2 + cipherKeySize;
        KeyParameter server_write_key = new KeyParameter(key_block, offset3, cipherKeySize);
        int offset4 = offset3 + cipherKeySize;
        if (this.useExplicitIV) {
            client_write_IV = new byte[clientWriteCipher.getBlockSize()];
            server_write_IV = new byte[serverWriteCipher.getBlockSize()];
        } else {
            client_write_IV = Arrays.copyOfRange(key_block, offset4, offset4 + clientWriteCipher.getBlockSize());
            int offset5 = offset4 + clientWriteCipher.getBlockSize();
            server_write_IV = Arrays.copyOfRange(key_block, offset5, offset5 + serverWriteCipher.getBlockSize());
            offset4 = offset5 + serverWriteCipher.getBlockSize();
        }
        if (offset4 != key_block_size) {
            throw new TlsFatalAlert((short) 80);
        }
        if (context.isServer()) {
            this.writeMac = serverWriteMac;
            this.readMac = clientWriteMac;
            this.encryptCipher = serverWriteCipher;
            this.decryptCipher = clientWriteCipher;
            encryptParams = new ParametersWithIV(server_write_key, server_write_IV);
            decryptParams = new ParametersWithIV(client_write_key, client_write_IV);
        } else {
            this.writeMac = clientWriteMac;
            this.readMac = serverWriteMac;
            this.encryptCipher = clientWriteCipher;
            this.decryptCipher = serverWriteCipher;
            encryptParams = new ParametersWithIV(client_write_key, client_write_IV);
            decryptParams = new ParametersWithIV(server_write_key, server_write_IV);
        }
        this.encryptCipher.init(true, encryptParams);
        this.decryptCipher.init(false, decryptParams);
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public int getPlaintextLimit(int ciphertextLimit) {
        int plaintextLimit;
        int blockSize = this.encryptCipher.getBlockSize();
        int macSize = this.writeMac.getSize();
        int plaintextLimit2 = ciphertextLimit;
        if (this.useExplicitIV) {
            plaintextLimit2 -= blockSize;
        }
        if (this.encryptThenMAC) {
            int plaintextLimit3 = plaintextLimit2 - macSize;
            plaintextLimit = plaintextLimit3 - (plaintextLimit3 % blockSize);
        } else {
            plaintextLimit = (plaintextLimit2 - (plaintextLimit2 % blockSize)) - macSize;
        }
        return plaintextLimit - 1;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] encodePlaintext(long seqNo, short type, byte[] plaintext, int offset, int len) {
        int blockSize = this.encryptCipher.getBlockSize();
        int macSize = this.writeMac.getSize();
        ProtocolVersion version = this.context.getServerVersion();
        int enc_input_length = len;
        if (!this.encryptThenMAC) {
            enc_input_length += macSize;
        }
        int padding_length = (blockSize - 1) - (enc_input_length % blockSize);
        if ((this.encryptThenMAC || !this.context.getSecurityParameters().truncatedHMac) && !version.isDTLS() && !version.isSSL()) {
            int maxExtraPadBlocks = (255 - padding_length) / blockSize;
            int actualExtraPadBlocks = chooseExtraPadBlocks(this.context.getSecureRandom(), maxExtraPadBlocks);
            padding_length += actualExtraPadBlocks * blockSize;
        }
        int totalSize = len + macSize + padding_length + 1;
        if (this.useExplicitIV) {
            totalSize += blockSize;
        }
        byte[] outBuf = new byte[totalSize];
        int outOff = 0;
        if (this.useExplicitIV) {
            byte[] explicitIV = new byte[blockSize];
            this.context.getNonceRandomGenerator().nextBytes(explicitIV);
            this.encryptCipher.init(true, new ParametersWithIV(null, explicitIV));
            System.arraycopy(explicitIV, 0, outBuf, 0, blockSize);
            outOff = 0 + blockSize;
        }
        int blocks_start = outOff;
        System.arraycopy(plaintext, offset, outBuf, outOff, len);
        int outOff2 = outOff + len;
        if (!this.encryptThenMAC) {
            byte[] mac = this.writeMac.calculateMac(seqNo, type, plaintext, offset, len);
            System.arraycopy(mac, 0, outBuf, outOff2, mac.length);
            outOff2 += mac.length;
        }
        for (int i = 0; i <= padding_length; i++) {
            int i2 = outOff2;
            outOff2++;
            outBuf[i2] = (byte) padding_length;
        }
        int i3 = blocks_start;
        while (true) {
            int i4 = i3;
            if (i4 >= outOff2) {
                break;
            }
            this.encryptCipher.processBlock(outBuf, i4, outBuf, i4);
            i3 = i4 + blockSize;
        }
        if (this.encryptThenMAC) {
            byte[] mac2 = this.writeMac.calculateMac(seqNo, type, outBuf, 0, outOff2);
            System.arraycopy(mac2, 0, outBuf, outOff2, mac2.length);
            int length = outOff2 + mac2.length;
        }
        return outBuf;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] decodeCiphertext(long seqNo, short type, byte[] ciphertext, int offset, int len) throws IOException {
        int minLen;
        int blockSize = this.decryptCipher.getBlockSize();
        int macSize = this.readMac.getSize();
        if (this.encryptThenMAC) {
            minLen = blockSize + macSize;
        } else {
            minLen = Math.max(blockSize, macSize + 1);
        }
        if (this.useExplicitIV) {
            minLen += blockSize;
        }
        if (len < minLen) {
            throw new TlsFatalAlert((short) 50);
        }
        int blocks_length = len;
        if (this.encryptThenMAC) {
            blocks_length -= macSize;
        }
        if (blocks_length % blockSize != 0) {
            throw new TlsFatalAlert((short) 21);
        }
        if (this.encryptThenMAC) {
            int end = offset + len;
            byte[] receivedMac = Arrays.copyOfRange(ciphertext, end - macSize, end);
            byte[] calculatedMac = this.readMac.calculateMac(seqNo, type, ciphertext, offset, len - macSize);
            if (!Arrays.constantTimeAreEqual(calculatedMac, receivedMac)) {
                throw new TlsFatalAlert((short) 20);
            }
        }
        if (this.useExplicitIV) {
            this.decryptCipher.init(false, new ParametersWithIV(null, ciphertext, offset, blockSize));
            offset += blockSize;
            blocks_length -= blockSize;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= blocks_length) {
                break;
            }
            this.decryptCipher.processBlock(ciphertext, offset + i2, ciphertext, offset + i2);
            i = i2 + blockSize;
        }
        int totalPad = checkPaddingConstantTime(ciphertext, offset, blocks_length, blockSize, this.encryptThenMAC ? 0 : macSize);
        boolean badMac = totalPad == 0;
        int dec_output_length = blocks_length - totalPad;
        if (!this.encryptThenMAC) {
            dec_output_length -= macSize;
            int macOff = offset + dec_output_length;
            byte[] receivedMac2 = Arrays.copyOfRange(ciphertext, macOff, macOff + macSize);
            byte[] calculatedMac2 = this.readMac.calculateMacConstantTime(seqNo, type, ciphertext, offset, dec_output_length, blocks_length - macSize, this.randomData);
            badMac |= !Arrays.constantTimeAreEqual(calculatedMac2, receivedMac2);
        }
        if (badMac) {
            throw new TlsFatalAlert((short) 20);
        }
        return Arrays.copyOfRange(ciphertext, offset, offset + dec_output_length);
    }

    protected int checkPaddingConstantTime(byte[] buf, int off, int len, int blockSize, int macSize) {
        int end = off + len;
        byte lastByte = buf[end - 1];
        int padlen = lastByte & 255;
        int totalPad = padlen + 1;
        int dummyIndex = 0;
        byte padDiff = 0;
        if ((TlsUtils.isSSL(this.context) && totalPad > blockSize) || macSize + totalPad > len) {
            totalPad = 0;
        } else {
            int padPos = end - totalPad;
            do {
                int i = padPos;
                padPos++;
                padDiff = (byte) (padDiff | (buf[i] ^ lastByte));
            } while (padPos < end);
            dummyIndex = totalPad;
            if (padDiff != 0) {
                totalPad = 0;
            }
        }
        byte[] dummyPad = this.randomData;
        while (dummyIndex < 256) {
            int i2 = dummyIndex;
            dummyIndex++;
            padDiff = (byte) (padDiff | (dummyPad[i2] ^ lastByte));
        }
        dummyPad[0] = (byte) (dummyPad[0] ^ padDiff);
        return totalPad;
    }

    protected int chooseExtraPadBlocks(SecureRandom r, int max) {
        int x = r.nextInt();
        int n = lowestBitSet(x);
        return Math.min(n, max);
    }

    protected int lowestBitSet(int x) {
        if (x == 0) {
            return 32;
        }
        int n = 0;
        while ((x & 1) == 0) {
            n++;
            x >>= 1;
        }
        return n;
    }
}
