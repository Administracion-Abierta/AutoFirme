package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/parsers/XIESPublicKeyParser.class */
public class XIESPublicKeyParser implements KeyParser {
    private final boolean isX25519;

    public XIESPublicKeyParser(boolean z) {
        this.isX25519 = z;
    }

    @Override // org.bouncycastle.crypto.KeyParser
    public AsymmetricKeyParameter readKey(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[this.isX25519 ? 32 : 56];
        Streams.readFully(inputStream, bArr, 0, bArr.length);
        return this.isX25519 ? new X25519PublicKeyParameters(bArr, 0) : new X448PublicKeyParameters(bArr, 0);
    }
}
