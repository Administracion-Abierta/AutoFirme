package es.gob.afirma.core.misc;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/AOUtil.class */
public final class AOUtil {
    private static final int BUFFER_SIZE = 4096;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] SUPPORTED_URI_SCHEMES = {"http", "https", "file", "urn"};
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private AOUtil() {
    }

    public static URI createURI(String str) throws URISyntaxException {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una URI a partir de un nulo");
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty()) {
            throw new IllegalArgumentException("La URI no puede ser una cadena vacia");
        }
        String strReplace = strTrim.replace('\\', '/').replace(" ", "%20").replace("<", "%3C").replace(">", "%3E").replace(OperatorName.SHOW_TEXT_LINE_AND_SPACE, "%22").replace("{", "%7B").replace("}", "%7D").replace("|", "%7C").replace("^", "%5E").replace("[", "%5B").replace("]", "%5D").replace("`", "%60");
        URI uri = new URI(strReplace);
        String scheme = uri.getScheme();
        for (String str2 : SUPPORTED_URI_SCHEMES) {
            if (str2.equals(scheme)) {
                return uri;
            }
        }
        if (scheme == null) {
            return createURI("file://" + strReplace.replace("#", "%23"));
        }
        if (scheme.length() == 1 && Character.isLetter((char) scheme.getBytes()[0])) {
            return createURI("file://" + strReplace.replace("#", "%23"));
        }
        throw new URISyntaxException(strReplace, "Tipo de URI no soportado");
    }

    public static InputStream loadFile(URI uri) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException("Se ha pedido el contenido de una URI nula");
        }
        if (uri.getScheme().equals("file")) {
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            if (schemeSpecificPart.startsWith("//")) {
                schemeSpecificPart = schemeSpecificPart.substring(2);
            }
            return new FileInputStream(new File(schemeSpecificPart));
        }
        return new ByteArrayInputStream(getDataFromInputStream(new BufferedInputStream(uri.toURL().openStream())));
    }

    public static byte[] getDataFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return new byte[0];
        }
        byte[] bArr = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int i = inputStream.read(bArr);
            if (i != -1) {
                byteArrayOutputStream.write(bArr, 0, i);
            } else {
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    public static String getCN(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return null;
        }
        return getCN(x509Certificate.getSubjectX500Principal().toString());
    }

    public static String getCN(String str) {
        if (str == null) {
            return null;
        }
        String rDNvalueFromLdapName = getRDNvalueFromLdapName("cn", str);
        if (rDNvalueFromLdapName == null) {
            rDNvalueFromLdapName = getRDNvalueFromLdapName("ou", str);
        }
        if (rDNvalueFromLdapName != null) {
            return rDNvalueFromLdapName;
        }
        int iIndexOf = str.indexOf(61);
        if (iIndexOf != -1) {
            LOGGER.warning("No se ha podido obtener el Common Name ni la Organizational Unit, se devolvera el fragmento mas significativo");
            return getRDNvalueFromLdapName(str.substring(0, iIndexOf), str);
        }
        LOGGER.warning("Principal no valido, se devolvera la entrada");
        return str;
    }

    public static String[] getOUS(String str) {
        if (str == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        String rDNvalueFromLdapName = getRDNvalueFromLdapName("ou", str);
        String strReplace = str;
        while (rDNvalueFromLdapName != null) {
            arrayList.add(rDNvalueFromLdapName);
            strReplace = strReplace.replace("OU=" + rDNvalueFromLdapName, "");
            rDNvalueFromLdapName = getRDNvalueFromLdapName("ou", strReplace);
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static String getRDNvalueFromLdapName(String str, String str2) {
        int iIndexOf;
        int length = 0;
        while (true) {
            int iIndexOf2 = str2.toLowerCase(Locale.US).indexOf(str.toLowerCase(), length);
            if (iIndexOf2 != -1) {
                if (iIndexOf2 > 0 && str2.charAt(iIndexOf2 - 1) != ',' && str2.charAt(iIndexOf2 - 1) != ' ') {
                    length = iIndexOf2 + 1;
                } else {
                    length = iIndexOf2 + str.length();
                    while (length < str2.length() && str2.charAt(length) == ' ') {
                        length++;
                    }
                    if (length >= str2.length()) {
                        return null;
                    }
                    if (str2.charAt(length) == '=') {
                        do {
                            length++;
                            if (length >= str2.length()) {
                                break;
                            }
                        } while (str2.charAt(length) == ' ');
                        if (length >= str2.length() || str2.charAt(length) == ',') {
                            return "";
                        }
                        if (str2.charAt(length) == '\"') {
                            int i = length + 1;
                            if (i >= str2.length() || (iIndexOf = str2.indexOf(34, i)) == i) {
                                return "";
                            }
                            if (iIndexOf != -1) {
                                return str2.substring(i, iIndexOf);
                            }
                            return str2.substring(i);
                        }
                        int iIndexOf3 = str2.indexOf(44, length);
                        if (iIndexOf3 != -1) {
                            return str2.substring(length, iIndexOf3).trim();
                        }
                        return str2.substring(length).trim();
                    }
                }
            } else {
                return null;
            }
        }
    }

    public static boolean isPseudonymCert(X509Certificate x509Certificate) {
        return getRDNvalueFromLdapName("2.5.4.65", x509Certificate.getSubjectX500Principal().getName("RFC2253")) != null;
    }

    public static String hexify(byte[] bArr, boolean z) {
        if (bArr == null) {
            return "null";
        }
        StringBuffer stringBuffer = new StringBuffer(256);
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            if (z && i > 0) {
                stringBuffer.append('-');
            }
            stringBuffer.append(HEX_CHARS[(bArr[i2] >> 4) & 15]);
            stringBuffer.append(HEX_CHARS[bArr[i2] & 15]);
            i++;
            if (i == 16) {
                if (z && i2 < bArr.length - 1) {
                    stringBuffer.append('\n');
                }
                i = 0;
            }
        }
        return stringBuffer.toString();
    }

    public static String hexify(byte[] bArr, String str) {
        if (bArr == null) {
            return "null";
        }
        StringBuffer stringBuffer = new StringBuffer(256);
        for (int i = 0; i < bArr.length; i++) {
            if (str != null && i > 0) {
                stringBuffer.append(str);
            }
            stringBuffer.append(HEX_CHARS[(bArr[i] >> 4) & 15]);
            stringBuffer.append(HEX_CHARS[bArr[i] & 15]);
        }
        return stringBuffer.toString();
    }

    public static void loadNativeLibrary(String str) throws IOException {
        if (str == null) {
            LOGGER.warning("No se puede cargar una biblioteca nula");
            return;
        }
        int iLastIndexOf = str.lastIndexOf(46);
        File file = new File(str);
        File fileCreateTempFile = File.createTempFile(iLastIndexOf < 1 ? file.getName() : file.getName().substring(0, file.getName().indexOf(46)), (iLastIndexOf < 1 || iLastIndexOf == str.length() - 1) ? null : str.substring(iLastIndexOf));
        copyFile(file, fileCreateTempFile);
        fileCreateTempFile.deleteOnExit();
        LOGGER.info("Cargamos " + LoggerUtil.getCleanUserHomePath(fileCreateTempFile.getAbsolutePath()));
        System.load(fileCreateTempFile.getAbsolutePath());
    }

    /* JADX WARN: Not initialized variable reg: 11, insn: 0x0134: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:68:0x0134 */
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x0139: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:70:0x0139 */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x00dd: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:46:0x00dd */
    /* JADX WARN: Not initialized variable reg: 14, insn: 0x00e2: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r14 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:48:0x00e2 */
    public static void copyFile(File file, File file2) throws IOException {
        FileOutputStream fileOutputStream;
        Throwable th;
        FileChannel fileChannel;
        Throwable th2;
        if (file == null || file2 == null) {
            throw new IllegalArgumentException("Ni origen ni destino de la copia pueden ser nulos");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th3 = null;
        try {
            try {
                try {
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                    Throwable th4 = null;
                    try {
                        try {
                            FileChannel channel = fileInputStream.getChannel();
                            Throwable th5 = null;
                            FileChannel channel2 = fileOutputStream2.getChannel();
                            Throwable th6 = null;
                            try {
                                try {
                                    channel2.write(channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size()));
                                    if (channel2 != null) {
                                        if (0 != 0) {
                                            try {
                                                channel2.close();
                                            } catch (Throwable th7) {
                                                th6.addSuppressed(th7);
                                            }
                                        } else {
                                            channel2.close();
                                        }
                                    }
                                    if (channel != null) {
                                        if (0 != 0) {
                                            try {
                                                channel.close();
                                            } catch (Throwable th8) {
                                                th5.addSuppressed(th8);
                                            }
                                        } else {
                                            channel.close();
                                        }
                                    }
                                    if (fileOutputStream2 != null) {
                                        if (0 != 0) {
                                            try {
                                                fileOutputStream2.close();
                                            } catch (Throwable th9) {
                                                th4.addSuppressed(th9);
                                            }
                                        } else {
                                            fileOutputStream2.close();
                                        }
                                    }
                                    if (fileInputStream != null) {
                                        if (0 == 0) {
                                            fileInputStream.close();
                                            return;
                                        }
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th10) {
                                            th3.addSuppressed(th10);
                                        }
                                    }
                                } catch (Throwable th11) {
                                    th6 = th11;
                                    throw th11;
                                }
                            } catch (Throwable th12) {
                                if (channel2 != null) {
                                    if (th6 != null) {
                                        try {
                                            channel2.close();
                                        } catch (Throwable th13) {
                                            th6.addSuppressed(th13);
                                        }
                                    } else {
                                        channel2.close();
                                    }
                                }
                                throw th12;
                            }
                        } catch (Throwable th14) {
                            throw th14;
                        }
                    } catch (Throwable th15) {
                        if (fileChannel != null) {
                            if (th2 != null) {
                                try {
                                    fileChannel.close();
                                } catch (Throwable th16) {
                                    th2.addSuppressed(th16);
                                }
                            } else {
                                fileChannel.close();
                            }
                        }
                        throw th15;
                    }
                } catch (Throwable th17) {
                    throw th17;
                }
            } catch (Throwable th18) {
                if (fileOutputStream != null) {
                    if (th != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th19) {
                            th.addSuppressed(th19);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th18;
            }
        } catch (Throwable th20) {
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th21) {
                        th3.addSuppressed(th21);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th20;
        }
    }

    public static String[] split(String str, String str2) {
        int iIndexOf;
        ArrayList arrayList = new ArrayList();
        int length = 0;
        while (length != str.length() && (iIndexOf = str.indexOf(str2, length)) != -1) {
            if (length == iIndexOf) {
                arrayList.add("");
            } else {
                arrayList.add(str.substring(length, iIndexOf));
            }
            length = iIndexOf + str2.length();
        }
        if (length == str.length()) {
            arrayList.add("");
        } else {
            arrayList.add(str.substring(length));
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static String properties2Base64(Properties properties) throws IOException {
        if (properties == null) {
            return "";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        properties.store(new OutputStreamWriter(byteArrayOutputStream, DEFAULT_ENCODING), "");
        return Base64.encode(byteArrayOutputStream.toByteArray(), true);
    }

    public static Properties base642Properties(String str) throws IOException {
        Properties properties = new Properties();
        if (str == null || str.isEmpty()) {
            return properties;
        }
        properties.load(new InputStreamReader(new ByteArrayInputStream(Base64.decode(str.replace('-', '+').replace('_', '/'))), DEFAULT_ENCODING));
        return properties;
    }

    public static String propertiesAsString(Properties properties) throws IOException {
        if (properties == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : (String[]) properties.keySet().toArray(new String[0])) {
            sb.append(str).append("=").append(properties.getProperty(str)).append("\n");
        }
        return sb.toString();
    }

    public static boolean isJava9orNewer() {
        String property = System.getProperty("java.version");
        if (property == null || property.isEmpty()) {
            LOGGER.warning("No se ha podido determinar la version de Java");
            return false;
        }
        try {
            if (property.startsWith("1.")) {
                return Integer.parseInt(property.substring(2, 3)) > 8;
            }
            String strSubstring = property;
            if (strSubstring.indexOf(".") > -1) {
                strSubstring = strSubstring.substring(0, strSubstring.indexOf("."));
            }
            return isOnlyNumber(strSubstring) && Integer.parseInt(strSubstring) > 8;
        } catch (Exception e) {
            LOGGER.warning("No se ha podido determinar la version de Java (" + property + "):" + e);
            return false;
        }
    }

    public static boolean isOnlyNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("^[0-9]+$");
    }
}
