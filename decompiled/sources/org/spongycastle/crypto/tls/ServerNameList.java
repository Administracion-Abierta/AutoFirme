package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/ServerNameList.class */
public class ServerNameList {
    protected Vector serverNameList;

    public ServerNameList(Vector serverNameList) {
        if (serverNameList == null) {
            throw new IllegalArgumentException("'serverNameList' must not be null");
        }
        this.serverNameList = serverNameList;
    }

    public Vector getServerNameList() {
        return this.serverNameList;
    }

    public void encode(OutputStream output) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        short[] nameTypesSeen = new short[0];
        for (int i = 0; i < this.serverNameList.size(); i++) {
            ServerName entry = (ServerName) this.serverNameList.elementAt(i);
            nameTypesSeen = checkNameType(nameTypesSeen, entry.getNameType());
            if (nameTypesSeen == null) {
                throw new TlsFatalAlert((short) 80);
            }
            entry.encode(buf);
        }
        TlsUtils.checkUint16(buf.size());
        TlsUtils.writeUint16(buf.size(), output);
        Streams.writeBufTo(buf, output);
    }

    public static ServerNameList parse(InputStream input) throws IOException {
        int length = TlsUtils.readUint16(input);
        if (length < 1) {
            throw new TlsFatalAlert((short) 50);
        }
        byte[] data = TlsUtils.readFully(length, input);
        ByteArrayInputStream buf = new ByteArrayInputStream(data);
        short[] nameTypesSeen = new short[0];
        Vector server_name_list = new Vector();
        while (buf.available() > 0) {
            ServerName entry = ServerName.parse(buf);
            nameTypesSeen = checkNameType(nameTypesSeen, entry.getNameType());
            if (nameTypesSeen == null) {
                throw new TlsFatalAlert((short) 47);
            }
            server_name_list.addElement(entry);
        }
        return new ServerNameList(server_name_list);
    }

    private static short[] checkNameType(short[] nameTypesSeen, short nameType) {
        if (!NameType.isValid(nameType) || Arrays.contains(nameTypesSeen, nameType)) {
            return null;
        }
        return Arrays.append(nameTypesSeen, nameType);
    }
}
