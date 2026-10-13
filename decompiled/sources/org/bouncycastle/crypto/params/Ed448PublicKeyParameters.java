package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/Ed448PublicKeyParameters.class */
public final class Ed448PublicKeyParameters extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 57;
    private final Ed448.PublicPoint publicPoint;

    public Ed448PublicKeyParameters(byte[] bArr) {
        this(validate(bArr), 0);
    }

    public Ed448PublicKeyParameters(byte[] bArr, int i) {
        super(false);
        this.publicPoint = parse(bArr, i);
    }

    public Ed448PublicKeyParameters(InputStream inputStream) throws IOException {
        super(false);
        byte[] bArr = new byte[57];
        if (57 != Streams.readFully(inputStream, bArr)) {
            throw new EOFException("EOF encountered in middle of Ed448 public key");
        }
        this.publicPoint = parse(bArr, 0);
    }

    public Ed448PublicKeyParameters(Ed448.PublicPoint publicPoint) {
        super(false);
        if (publicPoint == null) {
            throw new NullPointerException("'publicPoint' cannot be null");
        }
        this.publicPoint = publicPoint;
    }

    public void encode(byte[] bArr, int i) {
        Ed448.encodePublicPoint(this.publicPoint, bArr, i);
    }

    public byte[] getEncoded() {
        byte[] bArr = new byte[57];
        encode(bArr, 0);
        return bArr;
    }

    public boolean verify(int i, byte[] bArr, byte[] bArr2, int i2, int i3, byte[] bArr3, int i4) {
        switch (i) {
            case 0:
                if (null == bArr) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (bArr.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                return Ed448.verify(bArr3, i4, this.publicPoint, bArr, bArr2, i2, i3);
            case 1:
                if (null == bArr) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (bArr.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                if (64 != i3) {
                    throw new IllegalArgumentException("msgLen");
                }
                return Ed448.verifyPrehash(bArr3, i4, this.publicPoint, bArr, bArr2, i2);
            default:
                throw new IllegalArgumentException("algorithm");
        }
    }

    private static Ed448.PublicPoint parse(byte[] bArr, int i) {
        Ed448.PublicPoint publicPointValidatePublicKeyPartialExport = Ed448.validatePublicKeyPartialExport(bArr, i);
        if (publicPointValidatePublicKeyPartialExport == null) {
            throw new IllegalArgumentException("invalid public key");
        }
        return publicPointValidatePublicKeyPartialExport;
    }

    private static byte[] validate(byte[] bArr) {
        if (bArr.length != 57) {
            throw new IllegalArgumentException("'buf' must have length 57");
        }
        return bArr;
    }
}
