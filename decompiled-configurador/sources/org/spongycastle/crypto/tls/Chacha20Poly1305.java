package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.engines.ChaCha7539Engine;
import org.spongycastle.crypto.macs.Poly1305;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/Chacha20Poly1305.class */
public class Chacha20Poly1305 implements TlsCipher {
    private static final byte[] ZEROES = new byte[15];
    protected TlsContext context;
    protected ChaCha7539Engine encryptCipher;
    protected ChaCha7539Engine decryptCipher;
    protected byte[] encryptIV;
    protected byte[] decryptIV;

    public Chacha20Poly1305(TlsContext context) throws IOException {
        KeyParameter encryptKey;
        KeyParameter decryptKey;
        if (!TlsUtils.isTLSv12(context)) {
            throw new TlsFatalAlert((short) 80);
        }
        this.context = context;
        int key_block_size = (2 * 32) + (2 * 12);
        byte[] key_block = TlsUtils.calculateKeyBlock(context, key_block_size);
        KeyParameter client_write_key = new KeyParameter(key_block, 0, 32);
        int offset = 0 + 32;
        KeyParameter server_write_key = new KeyParameter(key_block, offset, 32);
        int offset2 = offset + 32;
        byte[] client_write_IV = Arrays.copyOfRange(key_block, offset2, offset2 + 12);
        int offset3 = offset2 + 12;
        byte[] server_write_IV = Arrays.copyOfRange(key_block, offset3, offset3 + 12);
        if (offset3 + 12 != key_block_size) {
            throw new TlsFatalAlert((short) 80);
        }
        this.encryptCipher = new ChaCha7539Engine();
        this.decryptCipher = new ChaCha7539Engine();
        if (context.isServer()) {
            encryptKey = server_write_key;
            decryptKey = client_write_key;
            this.encryptIV = server_write_IV;
            this.decryptIV = client_write_IV;
        } else {
            encryptKey = client_write_key;
            decryptKey = server_write_key;
            this.encryptIV = client_write_IV;
            this.decryptIV = server_write_IV;
        }
        this.encryptCipher.init(true, new ParametersWithIV(encryptKey, this.encryptIV));
        this.decryptCipher.init(false, new ParametersWithIV(decryptKey, this.decryptIV));
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public int getPlaintextLimit(int ciphertextLimit) {
        return ciphertextLimit - 16;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] encodePlaintext(long seqNo, short type, byte[] plaintext, int offset, int len) throws IOException {
        KeyParameter macKey = initRecord(this.encryptCipher, true, seqNo, this.encryptIV);
        byte[] output = new byte[len + 16];
        this.encryptCipher.processBytes(plaintext, offset, len, output, 0);
        byte[] additionalData = getAdditionalData(seqNo, type, len);
        byte[] mac = calculateRecordMAC(macKey, additionalData, output, 0, len);
        System.arraycopy(mac, 0, output, len, mac.length);
        return output;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] decodeCiphertext(long seqNo, short type, byte[] ciphertext, int offset, int len) throws IOException {
        if (getPlaintextLimit(len) < 0) {
            throw new TlsFatalAlert((short) 50);
        }
        KeyParameter macKey = initRecord(this.decryptCipher, false, seqNo, this.decryptIV);
        int plaintextLength = len - 16;
        byte[] additionalData = getAdditionalData(seqNo, type, plaintextLength);
        byte[] calculatedMAC = calculateRecordMAC(macKey, additionalData, ciphertext, offset, plaintextLength);
        byte[] receivedMAC = Arrays.copyOfRange(ciphertext, offset + plaintextLength, offset + len);
        if (!Arrays.constantTimeAreEqual(calculatedMAC, receivedMAC)) {
            throw new TlsFatalAlert((short) 20);
        }
        byte[] output = new byte[plaintextLength];
        this.decryptCipher.processBytes(ciphertext, offset, plaintextLength, output, 0);
        return output;
    }

    protected KeyParameter initRecord(StreamCipher cipher, boolean forEncryption, long seqNo, byte[] iv) {
        byte[] nonce = calculateNonce(seqNo, iv);
        cipher.init(forEncryption, new ParametersWithIV(null, nonce));
        return generateRecordMACKey(cipher);
    }

    protected byte[] calculateNonce(long seqNo, byte[] iv) {
        byte[] nonce = new byte[12];
        TlsUtils.writeUint64(seqNo, nonce, 4);
        for (int i = 0; i < 12; i++) {
            int i2 = i;
            nonce[i2] = (byte) (nonce[i2] ^ iv[i]);
        }
        return nonce;
    }

    protected KeyParameter generateRecordMACKey(StreamCipher cipher) {
        byte[] firstBlock = new byte[64];
        cipher.processBytes(firstBlock, 0, firstBlock.length, firstBlock, 0);
        KeyParameter macKey = new KeyParameter(firstBlock, 0, 32);
        Arrays.fill(firstBlock, (byte) 0);
        return macKey;
    }

    protected byte[] calculateRecordMAC(KeyParameter macKey, byte[] additionalData, byte[] buf, int off, int len) {
        Mac mac = new Poly1305();
        mac.init(macKey);
        updateRecordMACText(mac, additionalData, 0, additionalData.length);
        updateRecordMACText(mac, buf, off, len);
        updateRecordMACLength(mac, additionalData.length);
        updateRecordMACLength(mac, len);
        byte[] output = new byte[mac.getMacSize()];
        mac.doFinal(output, 0);
        return output;
    }

    protected void updateRecordMACLength(Mac mac, int len) {
        byte[] longLen = Pack.longToLittleEndian(((long) len) & BodyPartID.bodyIdMax);
        mac.update(longLen, 0, longLen.length);
    }

    protected void updateRecordMACText(Mac mac, byte[] buf, int off, int len) {
        mac.update(buf, off, len);
        int partial = len % 16;
        if (partial != 0) {
            mac.update(ZEROES, 0, 16 - partial);
        }
    }

    protected byte[] getAdditionalData(long seqNo, short type, int len) throws IOException {
        byte[] additional_data = new byte[13];
        TlsUtils.writeUint64(seqNo, additional_data, 0);
        TlsUtils.writeUint8(type, additional_data, 8);
        TlsUtils.writeVersion(this.context.getServerVersion(), additional_data, 9);
        TlsUtils.writeUint16(len, additional_data, 11);
        return additional_data;
    }
}
