package org.apache.xml.security.stax.impl.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import org.apache.xml.security.encryption.XMLCipherUtil;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/util/IVSplittingOutputStream.class */
public class IVSplittingOutputStream extends FilterOutputStream {
    private ReplaceableOuputStream replaceableOuputStream;
    private final byte[] iv;
    private final int ivLength;
    private int pos;
    private final Cipher cipher;
    private final Key secretKey;

    public IVSplittingOutputStream(OutputStream out, Cipher cipher, Key secretKey, int ivLength) {
        super(out);
        this.ivLength = ivLength;
        this.iv = new byte[ivLength];
        this.cipher = cipher;
        this.secretKey = secretKey;
    }

    public byte[] getIv() {
        return this.iv;
    }

    public boolean isIVComplete() {
        return this.pos == this.iv.length;
    }

    private void initializeCipher() throws IOException {
        AlgorithmParameterSpec iv = XMLCipherUtil.constructBlockCipherParameters(this.cipher.getAlgorithm().toUpperCase().contains("GCM"), getIv());
        try {
            this.cipher.init(2, this.secretKey, iv);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        } catch (InvalidKeyException e2) {
            throw new IOException(e2);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        if (this.pos >= this.ivLength) {
            initializeCipher();
            this.out.write(b);
            this.replaceableOuputStream.setNewOutputStream(this.out);
        } else {
            byte[] bArr = this.iv;
            int i = this.pos;
            this.pos = i + 1;
            bArr[i] = (byte) b;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        int missingBytes = this.ivLength - this.pos;
        if (missingBytes > len) {
            System.arraycopy(b, off, this.iv, this.pos, len);
            this.pos += len;
            return;
        }
        System.arraycopy(b, off, this.iv, this.pos, missingBytes);
        this.pos += missingBytes;
        initializeCipher();
        this.out.write(b, off + missingBytes, len - missingBytes);
        this.replaceableOuputStream.setNewOutputStream(this.out);
    }

    public void setParentOutputStream(ReplaceableOuputStream replaceableOuputStream) {
        this.replaceableOuputStream = replaceableOuputStream;
    }
}
