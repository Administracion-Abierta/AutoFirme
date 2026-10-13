package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/ServerSRPParams.class */
public class ServerSRPParams {
    protected BigInteger N;
    protected BigInteger g;
    protected BigInteger B;
    protected byte[] s;

    public ServerSRPParams(BigInteger N, BigInteger g, byte[] s, BigInteger B) {
        this.N = N;
        this.g = g;
        this.s = Arrays.clone(s);
        this.B = B;
    }

    public BigInteger getB() {
        return this.B;
    }

    public BigInteger getG() {
        return this.g;
    }

    public BigInteger getN() {
        return this.N;
    }

    public byte[] getS() {
        return this.s;
    }

    public void encode(OutputStream output) throws IOException {
        TlsSRPUtils.writeSRPParameter(this.N, output);
        TlsSRPUtils.writeSRPParameter(this.g, output);
        TlsUtils.writeOpaque8(this.s, output);
        TlsSRPUtils.writeSRPParameter(this.B, output);
    }

    public static ServerSRPParams parse(InputStream input) throws IOException {
        BigInteger N = TlsSRPUtils.readSRPParameter(input);
        BigInteger g = TlsSRPUtils.readSRPParameter(input);
        byte[] s = TlsUtils.readOpaque8(input);
        BigInteger B = TlsSRPUtils.readSRPParameter(input);
        return new ServerSRPParams(N, g, s, B);
    }
}
