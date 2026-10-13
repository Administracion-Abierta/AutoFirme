package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/HeartbeatExtension.class */
public class HeartbeatExtension {
    protected short mode;

    public HeartbeatExtension(short mode) {
        if (!HeartbeatMode.isValid(mode)) {
            throw new IllegalArgumentException("'mode' is not a valid HeartbeatMode value");
        }
        this.mode = mode;
    }

    public short getMode() {
        return this.mode;
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(this.mode, output);
    }

    public static HeartbeatExtension parse(InputStream input) throws IOException {
        short mode = TlsUtils.readUint8(input);
        if (!HeartbeatMode.isValid(mode)) {
            throw new TlsFatalAlert((short) 47);
        }
        return new HeartbeatExtension(mode);
    }
}
