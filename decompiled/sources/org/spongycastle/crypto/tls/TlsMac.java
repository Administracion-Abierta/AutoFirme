package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.digests.LongDigest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsMac.class */
public class TlsMac {
    protected TlsContext context;
    protected byte[] secret;
    protected Mac mac;
    protected int digestBlockSize;
    protected int digestOverhead;
    protected int macLength;

    public TlsMac(TlsContext context, Digest digest, byte[] key, int keyOff, int keyLen) {
        this.context = context;
        KeyParameter keyParameter = new KeyParameter(key, keyOff, keyLen);
        this.secret = Arrays.clone(keyParameter.getKey());
        if (digest instanceof LongDigest) {
            this.digestBlockSize = 128;
            this.digestOverhead = 16;
        } else {
            this.digestBlockSize = 64;
            this.digestOverhead = 8;
        }
        if (TlsUtils.isSSL(context)) {
            this.mac = new SSL3Mac(digest);
            if (digest.getDigestSize() == 20) {
                this.digestOverhead = 4;
            }
        } else {
            this.mac = new HMac(digest);
        }
        this.mac.init(keyParameter);
        this.macLength = this.mac.getMacSize();
        if (context.getSecurityParameters().truncatedHMac) {
            this.macLength = Math.min(this.macLength, 10);
        }
    }

    public byte[] getMACSecret() {
        return this.secret;
    }

    public int getSize() {
        return this.macLength;
    }

    public byte[] calculateMac(long seqNo, short type, byte[] message, int offset, int length) {
        ProtocolVersion serverVersion = this.context.getServerVersion();
        boolean isSSL = serverVersion.isSSL();
        byte[] macHeader = new byte[isSSL ? 11 : 13];
        TlsUtils.writeUint64(seqNo, macHeader, 0);
        TlsUtils.writeUint8(type, macHeader, 8);
        if (!isSSL) {
            TlsUtils.writeVersion(serverVersion, macHeader, 9);
        }
        TlsUtils.writeUint16(length, macHeader, macHeader.length - 2);
        this.mac.update(macHeader, 0, macHeader.length);
        this.mac.update(message, offset, length);
        byte[] result = new byte[this.mac.getMacSize()];
        this.mac.doFinal(result, 0);
        return truncate(result);
    }

    public byte[] calculateMacConstantTime(long seqNo, short type, byte[] message, int offset, int length, int fullLength, byte[] dummyData) {
        byte[] result = calculateMac(seqNo, type, message, offset, length);
        int headerLength = TlsUtils.isSSL(this.context) ? 11 : 13;
        int extra = getDigestBlockCount(headerLength + fullLength) - getDigestBlockCount(headerLength + length);
        while (true) {
            extra--;
            if (extra >= 0) {
                this.mac.update(dummyData, 0, this.digestBlockSize);
            } else {
                this.mac.update(dummyData[0]);
                this.mac.reset();
                return result;
            }
        }
    }

    protected int getDigestBlockCount(int inputLength) {
        return (inputLength + this.digestOverhead) / this.digestBlockSize;
    }

    protected byte[] truncate(byte[] bs) {
        if (bs.length <= this.macLength) {
            return bs;
        }
        return Arrays.copyOf(bs, this.macLength);
    }
}
