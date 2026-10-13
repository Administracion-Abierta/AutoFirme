package org.spongycastle.util.io.pem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/pem/PemReader.class */
public class PemReader extends BufferedReader {
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";

    public PemReader(Reader reader) {
        super(reader);
    }

    public PemObject readPemObject() throws IOException {
        String line;
        String line2 = readLine();
        while (true) {
            line = line2;
            if (line == null || line.startsWith(BEGIN)) {
                break;
            }
            line2 = readLine();
        }
        if (line != null) {
            String line3 = line.substring(BEGIN.length());
            int index = line3.indexOf(45);
            String type = line3.substring(0, index);
            if (index > 0) {
                return loadObject(type);
            }
            return null;
        }
        return null;
    }

    private PemObject loadObject(String type) throws IOException {
        String line;
        String endMarker = END + type;
        StringBuffer buf = new StringBuffer();
        List headers = new ArrayList();
        while (true) {
            line = readLine();
            if (line == null) {
                break;
            }
            if (line.indexOf(":") >= 0) {
                int index = line.indexOf(58);
                String hdr = line.substring(0, index);
                String value = line.substring(index + 1).trim();
                headers.add(new PemHeader(hdr, value));
            } else {
                if (line.indexOf(endMarker) != -1) {
                    break;
                }
                buf.append(line.trim());
            }
        }
        if (line == null) {
            throw new IOException(endMarker + " not found");
        }
        return new PemObject(type, headers, Base64.decode(buf.toString()));
    }
}
