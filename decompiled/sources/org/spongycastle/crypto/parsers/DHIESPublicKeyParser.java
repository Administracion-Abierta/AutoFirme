package org.spongycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.spongycastle.crypto.KeyParser;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/parsers/DHIESPublicKeyParser.class */
public class DHIESPublicKeyParser implements KeyParser {
    private DHParameters dhParams;

    public DHIESPublicKeyParser(DHParameters dhParams) {
        this.dhParams = dhParams;
    }

    @Override // org.spongycastle.crypto.KeyParser
    public AsymmetricKeyParameter readKey(InputStream stream) throws IOException {
        byte[] V = new byte[(this.dhParams.getP().bitLength() + 7) / 8];
        Streams.readFully(stream, V, 0, V.length);
        return new DHPublicKeyParameters(new BigInteger(1, V), this.dhParams);
    }
}
