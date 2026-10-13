package org.bouncycastle.util.io.pem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/io/pem/PemReader.class */
public class PemReader extends BufferedReader {
    public static final String LAX_PEM_PARSING_SYSTEM_PROPERTY_NAME = "org.bouncycastle.pemreader.lax";
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";
    private static final Logger LOG = Logger.getLogger(PemReader.class.getName());

    public PemReader(Reader reader) {
        super(reader);
    }

    public PemObject readPemObject() throws IOException {
        String str;
        String strTrim;
        int iIndexOf;
        String line = readLine();
        while (true) {
            str = line;
            if (str == null || str.startsWith(BEGIN)) {
                break;
            }
            line = readLine();
        }
        if (str == null || (iIndexOf = (strTrim = str.substring(BEGIN.length()).trim()).indexOf(45)) <= 0 || !strTrim.endsWith("-----") || strTrim.length() - iIndexOf != 5) {
            return null;
        }
        return loadObject(strTrim.substring(0, iIndexOf));
    }

    private PemObject loadObject(String str) throws IOException {
        String str2;
        String str3 = END + str + "-----";
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList arrayList = new ArrayList();
        while (true) {
            String line = readLine();
            str2 = line;
            if (line == null) {
                break;
            }
            int iIndexOf = str2.indexOf(58);
            if (iIndexOf >= 0) {
                arrayList.add(new PemHeader(str2.substring(0, iIndexOf), str2.substring(iIndexOf + 1).trim()));
            } else {
                if (System.getProperty(LAX_PEM_PARSING_SYSTEM_PROPERTY_NAME, "false").equalsIgnoreCase("true")) {
                    String strTrim = str2.trim();
                    if (!strTrim.equals(str2) && LOG.isLoggable(Level.WARNING)) {
                        LOG.log(Level.WARNING, "PEM object contains whitespaces on -----END line", (Throwable) new Exception("trace"));
                    }
                    str2 = strTrim;
                }
                if (str2.indexOf(str3) == 0) {
                    break;
                }
                stringBuffer.append(str2.trim());
            }
        }
        if (str2 == null) {
            throw new IOException(str3 + " not found");
        }
        return new PemObject(str, arrayList, Base64.decode(stringBuffer.toString()));
    }
}
