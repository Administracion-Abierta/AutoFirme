package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/URLAndHash.class */
public class URLAndHash {
    protected String url;
    protected byte[] sha1Hash;

    public URLAndHash(String url, byte[] sha1Hash) {
        if (url == null || url.length() < 1 || url.length() >= 65536) {
            throw new IllegalArgumentException("'url' must have length from 1 to (2^16 - 1)");
        }
        if (sha1Hash != null && sha1Hash.length != 20) {
            throw new IllegalArgumentException("'sha1Hash' must have length == 20, if present");
        }
        this.url = url;
        this.sha1Hash = sha1Hash;
    }

    public String getURL() {
        return this.url;
    }

    public byte[] getSHA1Hash() {
        return this.sha1Hash;
    }

    public void encode(OutputStream output) throws IOException {
        byte[] urlEncoding = Strings.toByteArray(this.url);
        TlsUtils.writeOpaque16(urlEncoding, output);
        if (this.sha1Hash == null) {
            TlsUtils.writeUint8(0, output);
        } else {
            TlsUtils.writeUint8(1, output);
            output.write(this.sha1Hash);
        }
    }

    public static URLAndHash parse(TlsContext context, InputStream input) throws IOException {
        byte[] urlEncoding = TlsUtils.readOpaque16(input);
        if (urlEncoding.length < 1) {
            throw new TlsFatalAlert((short) 47);
        }
        String url = Strings.fromByteArray(urlEncoding);
        byte[] sha1Hash = null;
        short padding = TlsUtils.readUint8(input);
        switch (padding) {
            case 0:
                if (TlsUtils.isTLSv12(context)) {
                    throw new TlsFatalAlert((short) 47);
                }
                break;
            case 1:
                sha1Hash = TlsUtils.readFully(20, input);
                break;
            default:
                throw new TlsFatalAlert((short) 47);
        }
        return new URLAndHash(url, sha1Hash);
    }
}
