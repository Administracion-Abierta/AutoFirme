package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/ServerName.class */
public class ServerName {
    protected short nameType;
    protected Object name;

    public ServerName(short nameType, Object name) {
        if (!isCorrectType(nameType, name)) {
            throw new IllegalArgumentException("'name' is not an instance of the correct type");
        }
        this.nameType = nameType;
        this.name = name;
    }

    public short getNameType() {
        return this.nameType;
    }

    public Object getName() {
        return this.name;
    }

    public String getHostName() {
        if (!isCorrectType((short) 0, this.name)) {
            throw new IllegalStateException("'name' is not a HostName string");
        }
        return (String) this.name;
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(this.nameType, output);
        switch (this.nameType) {
            case 0:
                byte[] asciiEncoding = ((String) this.name).getBytes("ASCII");
                if (asciiEncoding.length < 1) {
                    throw new TlsFatalAlert((short) 80);
                }
                TlsUtils.writeOpaque16(asciiEncoding, output);
                return;
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    public static ServerName parse(InputStream input) throws IOException {
        short name_type = TlsUtils.readUint8(input);
        switch (name_type) {
            case 0:
                byte[] asciiEncoding = TlsUtils.readOpaque16(input);
                if (asciiEncoding.length < 1) {
                    throw new TlsFatalAlert((short) 50);
                }
                Object name = new String(asciiEncoding, "ASCII");
                return new ServerName(name_type, name);
            default:
                throw new TlsFatalAlert((short) 50);
        }
    }

    protected static boolean isCorrectType(short nameType, Object name) {
        switch (nameType) {
            case 0:
                return name instanceof String;
            default:
                throw new IllegalArgumentException("'nameType' is an unsupported NameType");
        }
    }
}
