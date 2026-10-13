package es.gob.afirma.core.misc.http;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.Locale;
import java.util.logging.Logger;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/URLName.class */
final class URLName {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    protected String fullURL;
    private String protocol;
    private String username;
    private String password;
    private String host;
    private InetAddress hostAddress;
    private String file;
    private String ref;
    private static boolean doEncode;
    static BitSet dontNeedEncoding;
    static final int CASE_DIFF = 32;
    private boolean hostAddressKnown = false;
    private int port = -1;
    private int hashCode = 0;

    static {
        doEncode = true;
        try {
            doEncode = !Boolean.getBoolean("mail.URLName.dontencode");
        } catch (Exception e) {
            LOGGER.warning("No se ha podido leer la variable 'mail.URLName.dontencode': " + e);
        }
        dontNeedEncoding = new BitSet(256);
        for (int i = 97; i <= 122; i++) {
            dontNeedEncoding.set(i);
        }
        for (int i2 = 65; i2 <= 90; i2++) {
            dontNeedEncoding.set(i2);
        }
        for (int i3 = 48; i3 <= 57; i3++) {
            dontNeedEncoding.set(i3);
        }
        dontNeedEncoding.set(32);
        dontNeedEncoding.set(45);
        dontNeedEncoding.set(95);
        dontNeedEncoding.set(46);
        dontNeedEncoding.set(42);
    }

    URLName(String str) {
        parseString(str);
    }

    public String toString() {
        if (this.fullURL == null) {
            StringBuffer stringBuffer = new StringBuffer();
            if (this.protocol != null) {
                stringBuffer.append(this.protocol);
                stringBuffer.append(":");
            }
            if (this.username != null || this.host != null) {
                stringBuffer.append("//");
                if (this.username != null) {
                    stringBuffer.append(this.username);
                    if (this.password != null) {
                        stringBuffer.append(":");
                        stringBuffer.append(this.password);
                    }
                    stringBuffer.append(Registry.Key.DEFAULT_NAME);
                }
                if (this.host != null) {
                    stringBuffer.append(this.host);
                }
                if (this.port != -1) {
                    stringBuffer.append(":");
                    stringBuffer.append(Integer.toString(this.port));
                }
                if (this.file != null) {
                    stringBuffer.append("/");
                }
            }
            if (this.file != null) {
                stringBuffer.append(this.file);
            }
            if (this.ref != null) {
                stringBuffer.append("#");
                stringBuffer.append(this.ref);
            }
            this.fullURL = stringBuffer.toString();
        }
        return this.fullURL;
    }

    private void parseString(String str) {
        int iIndexOf;
        String strSubstring;
        int iIndexOf2;
        this.password = null;
        this.username = null;
        this.host = null;
        this.ref = null;
        this.file = null;
        this.protocol = null;
        this.port = -1;
        int length = str.length();
        int iIndexOf3 = str.indexOf(58);
        if (iIndexOf3 != -1) {
            this.protocol = str.substring(0, iIndexOf3);
        }
        if (str.regionMatches(iIndexOf3 + 1, "//", 0, 2)) {
            int iIndexOf4 = str.indexOf(47, iIndexOf3 + 3);
            if (iIndexOf4 != -1) {
                strSubstring = str.substring(iIndexOf3 + 3, iIndexOf4);
                if (iIndexOf4 + 1 < length) {
                    this.file = str.substring(iIndexOf4 + 1);
                } else {
                    this.file = "";
                }
            } else {
                strSubstring = str.substring(iIndexOf3 + 3);
            }
            int iIndexOf5 = strSubstring.indexOf(64);
            if (iIndexOf5 != -1) {
                String strSubstring2 = strSubstring.substring(0, iIndexOf5);
                strSubstring = strSubstring.substring(iIndexOf5 + 1);
                int iIndexOf6 = strSubstring2.indexOf(58);
                if (iIndexOf6 != -1) {
                    this.username = strSubstring2.substring(0, iIndexOf6);
                    this.password = strSubstring2.substring(iIndexOf6 + 1);
                } else {
                    this.username = strSubstring2;
                }
            }
            if (strSubstring.length() > 0 && strSubstring.charAt(0) == '[') {
                iIndexOf2 = strSubstring.indexOf(58, strSubstring.indexOf(93));
            } else {
                iIndexOf2 = strSubstring.indexOf(58);
            }
            if (iIndexOf2 != -1) {
                String strSubstring3 = strSubstring.substring(iIndexOf2 + 1);
                if (strSubstring3.length() > 0) {
                    try {
                        this.port = Integer.parseInt(strSubstring3);
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El numero de puerto establecido '" + strSubstring3 + "' no es valido: " + e);
                        this.port = -1;
                    }
                }
                this.host = strSubstring.substring(0, iIndexOf2);
            } else {
                this.host = strSubstring;
            }
        } else if (iIndexOf3 + 1 < length) {
            this.file = str.substring(iIndexOf3 + 1);
        }
        if (this.file != null && (iIndexOf = this.file.indexOf(35)) != -1) {
            this.ref = this.file.substring(iIndexOf + 1);
            this.file = this.file.substring(0, iIndexOf);
        }
    }

    int getPort() {
        return this.port;
    }

    String getProtocol() {
        return this.protocol;
    }

    String getFile() {
        return this.file;
    }

    String getHost() {
        return this.host;
    }

    String getUsername() {
        return doEncode ? decode(this.username) : this.username;
    }

    String getPassword() {
        return doEncode ? decode(this.password) : this.password;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof URLName)) {
            return false;
        }
        URLName uRLName = (URLName) obj;
        if (uRLName.protocol == null || !uRLName.protocol.equals(this.protocol)) {
            return false;
        }
        InetAddress hostAddress = getHostAddress();
        InetAddress hostAddress2 = uRLName.getHostAddress();
        if (hostAddress != null && hostAddress2 != null) {
            if (!hostAddress.equals(hostAddress2)) {
                return false;
            }
        } else if (this.host != null && uRLName.host != null) {
            if (!this.host.equalsIgnoreCase(uRLName.host)) {
                return false;
            }
        } else if (this.host != uRLName.host) {
            return false;
        }
        if (this.username != uRLName.username && (this.username == null || !this.username.equals(uRLName.username))) {
            return false;
        }
        if (!(this.file == null ? "" : this.file).equals(uRLName.file == null ? "" : uRLName.file) || this.port != uRLName.port) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }
        if (this.protocol != null) {
            this.hashCode += this.protocol.hashCode();
        }
        InetAddress hostAddress = getHostAddress();
        if (hostAddress != null) {
            this.hashCode += hostAddress.hashCode();
        } else if (this.host != null) {
            this.hashCode += this.host.toLowerCase(Locale.ENGLISH).hashCode();
        }
        if (this.username != null) {
            this.hashCode += this.username.hashCode();
        }
        if (this.file != null) {
            this.hashCode += this.file.hashCode();
        }
        this.hashCode += this.port;
        return this.hashCode;
    }

    private synchronized InetAddress getHostAddress() {
        if (this.hostAddressKnown) {
            return this.hostAddress;
        }
        if (this.host == null) {
            return null;
        }
        try {
            this.hostAddress = InetAddress.getByName(this.host);
        } catch (UnknownHostException e) {
            LOGGER.warning("El host establecido no se puede resolver: " + e);
            this.hostAddress = null;
        }
        this.hostAddressKnown = true;
        return this.hostAddress;
    }

    static String decode(String str) {
        if (str == null) {
            return null;
        }
        if (indexOfAny(str, "+%") == -1) {
            return str;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        while (i < str.length()) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case '%':
                    try {
                        stringBuffer.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(e);
                    }
                    break;
                case '+':
                    stringBuffer.append(' ');
                    break;
                default:
                    stringBuffer.append(cCharAt);
                    break;
            }
            i++;
        }
        String string = stringBuffer.toString();
        try {
            string = new String(string.getBytes("8859_1"));
        } catch (UnsupportedEncodingException e2) {
            LOGGER.warning("El sistema no soporta la codificacion ISO 8859-1: " + e2);
        }
        return string;
    }

    private static int indexOfAny(String str, String str2) {
        return indexOfAny(str, str2, 0);
    }

    private static int indexOfAny(String str, String str2, int i) {
        try {
            int length = str.length();
            for (int i2 = i; i2 < length; i2++) {
                if (str2.indexOf(str.charAt(i2)) >= 0) {
                    return i2;
                }
            }
            return -1;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        }
    }
}
