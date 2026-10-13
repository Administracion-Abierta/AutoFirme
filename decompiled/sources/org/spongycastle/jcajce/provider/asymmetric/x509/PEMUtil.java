package org.spongycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/x509/PEMUtil.class */
class PEMUtil {
    private final String _header1;
    private final String _header2;
    private final String _footer1;
    private final String _footer2;
    private final String _header3 = "-----BEGIN PKCS7-----";
    private final String _footer3 = "-----END PKCS7-----";

    PEMUtil(String type) {
        this._header1 = "-----BEGIN " + type + "-----";
        this._header2 = "-----BEGIN X509 " + type + "-----";
        this._footer1 = "-----END " + type + "-----";
        this._footer2 = "-----END X509 " + type + "-----";
    }

    private String readLine(InputStream in) throws IOException {
        int c;
        StringBuffer l = new StringBuffer();
        while (true) {
            c = in.read();
            if (c != 13 && c != 10 && c >= 0) {
                l.append((char) c);
            } else if (c < 0 || l.length() != 0) {
                break;
            }
        }
        if (c < 0) {
            return null;
        }
        if (c == 13) {
            in.mark(1);
            int c2 = in.read();
            if (c2 == 10) {
                in.mark(1);
            }
            if (c2 > 0) {
                in.reset();
            }
        }
        return l.toString();
    }

    ASN1Sequence readPEMObject(InputStream in) throws IOException {
        String line;
        StringBuffer pemBuf = new StringBuffer();
        do {
            line = readLine(in);
            if (line == null || line.startsWith(this._header1) || line.startsWith(this._header2)) {
                break;
            }
        } while (!line.startsWith(this._header3));
        while (true) {
            String line2 = readLine(in);
            if (line2 == null || line2.startsWith(this._footer1) || line2.startsWith(this._footer2) || line2.startsWith(this._footer3)) {
                break;
            }
            pemBuf.append(line2);
        }
        if (pemBuf.length() != 0) {
            try {
                return ASN1Sequence.getInstance(Base64.decode(pemBuf.toString()));
            } catch (Exception e) {
                throw new IOException("malformed PEM data encountered");
            }
        }
        return null;
    }
}
