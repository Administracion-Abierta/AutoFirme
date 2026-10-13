package com.aowagie.text.pdf.crypto;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/crypto/ARCFOUREncryption.class */
public class ARCFOUREncryption {
    private byte[] state = new byte[256];
    private int x;
    private int y;

    public void prepareARCFOURKey(byte[] key) {
        prepareARCFOURKey(key, 0, key.length);
    }

    public void prepareARCFOURKey(byte[] key, int off, int len) {
        int index1 = 0;
        int index2 = 0;
        for (int k = 0; k < 256; k++) {
            this.state[k] = (byte) k;
        }
        this.x = 0;
        this.y = 0;
        for (int k2 = 0; k2 < 256; k2++) {
            index2 = (key[index1 + off] + this.state[k2] + index2) & 255;
            byte tmp = this.state[k2];
            this.state[k2] = this.state[index2];
            this.state[index2] = tmp;
            index1 = (index1 + 1) % len;
        }
    }

    public void encryptARCFOUR(byte[] dataIn, int off, int len, byte[] dataOut, int offOut) {
        int length = len + off;
        for (int k = off; k < length; k++) {
            this.x = (this.x + 1) & 255;
            this.y = (this.state[this.x] + this.y) & 255;
            byte tmp = this.state[this.x];
            this.state[this.x] = this.state[this.y];
            this.state[this.y] = tmp;
            dataOut[(k - off) + offOut] = (byte) (dataIn[k] ^ this.state[(this.state[this.x] + this.state[this.y]) & 255]);
        }
    }

    public void encryptARCFOUR(byte[] data, int off, int len) {
        encryptARCFOUR(data, off, len, data, off);
    }

    public void encryptARCFOUR(byte[] dataIn, byte[] dataOut) {
        encryptARCFOUR(dataIn, 0, dataIn.length, dataOut, 0);
    }

    public void encryptARCFOUR(byte[] data) {
        encryptARCFOUR(data, 0, data.length, data, 0);
    }
}
