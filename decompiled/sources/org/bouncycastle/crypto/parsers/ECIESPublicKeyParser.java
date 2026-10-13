package org.bouncycastle.crypto.parsers;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/parsers/ECIESPublicKeyParser.class */
public class ECIESPublicKeyParser implements KeyParser {
    private ECDomainParameters ecParams;

    public ECIESPublicKeyParser(ECDomainParameters eCDomainParameters) {
        this.ecParams = eCDomainParameters;
    }

    @Override // org.bouncycastle.crypto.KeyParser
    public AsymmetricKeyParameter readKey(InputStream inputStream) throws IOException {
        boolean z;
        int i = inputStream.read();
        if (i < 0) {
            throw new EOFException();
        }
        switch (i) {
            case 0:
                throw new IOException("Sender's public key invalid.");
            case 1:
            case 5:
            default:
                throw new IOException("Sender's public key has invalid point encoding 0x" + Integer.toString(i, 16));
            case 2:
            case 3:
                z = true;
                break;
            case 4:
            case 6:
            case 7:
                z = false;
                break;
        }
        ECCurve curve = this.ecParams.getCurve();
        int affinePointEncodingLength = curve.getAffinePointEncodingLength(z);
        byte[] bArr = new byte[affinePointEncodingLength];
        bArr[0] = (byte) i;
        int i2 = affinePointEncodingLength - 1;
        if (Streams.readFully(inputStream, bArr, 1, i2) != i2) {
            throw new EOFException();
        }
        return new ECPublicKeyParameters(curve.decodePoint(bArr), this.ecParams);
    }
}
