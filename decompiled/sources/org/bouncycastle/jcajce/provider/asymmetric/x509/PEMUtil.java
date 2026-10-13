package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/PEMUtil.class */
class PEMUtil {
    private final Boundaries[] _supportedBoundaries;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/PEMUtil$Boundaries.class */
    private static class Boundaries {
        private final String _header;
        private final String _footer;

        private Boundaries(String str) {
            this._header = "-----BEGIN " + str + "-----";
            this._footer = "-----END " + str + "-----";
        }

        public boolean isTheExpectedHeader(String str) {
            return str.startsWith(this._header);
        }

        public boolean isTheExpectedFooter(String str) {
            return str.startsWith(this._footer);
        }
    }

    PEMUtil(String str) {
        this._supportedBoundaries = new Boundaries[]{new Boundaries(str), new Boundaries("X509 " + str), new Boundaries("PKCS7")};
    }

    private String readLine(InputStream inputStream) throws IOException {
        int i;
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            i = inputStream.read();
            if (i != 13 && i != 10 && i >= 0) {
                stringBuffer.append((char) i);
            } else if (i < 0 || stringBuffer.length() != 0) {
                break;
            }
        }
        if (i < 0) {
            if (stringBuffer.length() == 0) {
                return null;
            }
            return stringBuffer.toString();
        }
        if (i == 13) {
            inputStream.mark(1);
            int i2 = inputStream.read();
            if (i2 == 10) {
                inputStream.mark(1);
            }
            if (i2 > 0) {
                inputStream.reset();
            }
        }
        return stringBuffer.toString();
    }

    private Boundaries getBoundaries(String str) {
        for (int i = 0; i != this._supportedBoundaries.length; i++) {
            Boundaries boundaries = this._supportedBoundaries[i];
            if (boundaries.isTheExpectedHeader(str) || boundaries.isTheExpectedFooter(str)) {
                return boundaries;
            }
        }
        return null;
    }

    ASN1Sequence readPEMObject(InputStream inputStream, boolean z) throws IOException {
        String line;
        String line2;
        StringBuffer stringBuffer = new StringBuffer();
        Boundaries boundaries = null;
        while (boundaries == null && (line2 = readLine(inputStream)) != null) {
            boundaries = getBoundaries(line2);
            if (boundaries != null && !boundaries.isTheExpectedHeader(line2)) {
                throw new IOException("malformed PEM data: found footer where header was expected");
            }
        }
        if (boundaries == null) {
            if (z) {
                throw new IOException("malformed PEM data: no header found");
            }
            return null;
        }
        Boundaries boundaries2 = null;
        while (boundaries2 == null && (line = readLine(inputStream)) != null) {
            boundaries2 = getBoundaries(line);
            if (boundaries2 == null) {
                stringBuffer.append(line);
            } else if (!boundaries.isTheExpectedFooter(line)) {
                throw new IOException("malformed PEM data: header/footer mismatch");
            }
        }
        if (boundaries2 == null) {
            throw new IOException("malformed PEM data: no footer found");
        }
        if (stringBuffer.length() == 0) {
            return null;
        }
        try {
            return ASN1Sequence.getInstance(Base64.decode(stringBuffer.toString()));
        } catch (Exception e) {
            throw new IOException("malformed PEM data encountered");
        }
    }
}
