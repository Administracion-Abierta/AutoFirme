package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/X448PublicKeyParameters.class */
public final class X448PublicKeyParameters extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 56;
    private final byte[] data;

    public X448PublicKeyParameters(byte[] bArr) {
        this(validate(bArr), 0);
    }

    public X448PublicKeyParameters(byte[] bArr, int i) {
        super(false);
        this.data = new byte[56];
        System.arraycopy(bArr, i, this.data, 0, 56);
    }

    public X448PublicKeyParameters(InputStream inputStream) throws IOException {
        super(false);
        this.data = new byte[56];
        if (56 != Streams.readFully(inputStream, this.data)) {
            throw new EOFException("EOF encountered in middle of X448 public key");
        }
    }

    public void encode(byte[] bArr, int i) {
        System.arraycopy(this.data, 0, bArr, i, 56);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    private static byte[] validate(byte[] bArr) {
        if (bArr.length != 56) {
            throw new IllegalArgumentException("'buf' must have length 56");
        }
        return bArr;
    }
}
