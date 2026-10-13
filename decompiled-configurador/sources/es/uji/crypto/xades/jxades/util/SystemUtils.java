package es.uji.crypto.xades.jxades.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/util/SystemUtils.class */
public class SystemUtils {
    private static final String KEY_PREFIX = "com.cosmos";
    public static final String KEY_APPLICATION_NAME = "com.cosmos.apps.name";
    public static final String KEY_CLIENT_CONFIG_FOLDER = "com.cosmos.apps.client.config.folder";
    public static final String KEY_KEYSTORE_FOLDER = "com.cosmos.key.store.folder";
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final char[] EMPTY_CHAR_ARRAY = new char[0];
    private static DateFormat dateFormat;
    private static DecimalFormat decimalFormat;

    private static DateFormat getDateFormatter() {
        if (dateFormat == null) {
            dateFormat = new ISO8601DateFormat();
        }
        return dateFormat;
    }

    public static String formatDate(Object date) {
        return getDateFormatter().format(date);
    }

    public static String formatDate(Date date) {
        return getDateFormatter().format(date);
    }

    public static Date parseDate(String dateString) throws ParseException {
        DateFormat dateFormat2 = getDateFormatter();
        try {
            return dateFormat2.parse(dateString);
        } catch (ParseException e) {
            DateFormat dateFormat3 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
            return dateFormat3.parse(dateString);
        }
    }

    public static DecimalFormat getDecimalFormatter() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#,##0.##");
        }
        return decimalFormat;
    }

    public static String getCauseMessages(Throwable ex) {
        if (ex == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getErrorMessage(ex)).append("; \n");
        Throwable cause = ex.getCause();
        while (true) {
            Throwable cause2 = cause;
            if (cause2 != null) {
                String message = getErrorMessage(cause2);
                if (message != null) {
                    sb.append(message).append("; \n");
                }
                cause = cause2.getCause();
            } else {
                return sb.toString();
            }
        }
    }

    public static String getErrorMessage(Throwable ex) {
        if (ex != null) {
            String message = ex.getMessage();
            if (message == null) {
                message = ex.getClass().getName();
            }
            return message;
        }
        return null;
    }

    public static byte[] toByteArray(InputStream inStream) throws IOException {
        if (inStream == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(inStream, os);
        return os.toByteArray();
    }

    public static void copy(InputStream inStream, OutputStream outStream) throws IOException {
        if (inStream == null) {
            throw new IllegalArgumentException("InputStream can not be NULL in copy method.");
        }
        if (outStream == null) {
            throw new IllegalArgumentException("OutputStream can not be NULL in copy method.");
        }
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                int read = inStream.read(buffer);
                if (read >= 0) {
                    outStream.write(buffer, 0, read);
                } else {
                    outStream.flush();
                    inStream.close();
                    outStream.close();
                    return;
                }
            } catch (Throwable th) {
                inStream.close();
                outStream.close();
                throw th;
            }
        }
    }

    public static String trimFileName(String filePathName) {
        char ch;
        if (filePathName == null) {
            return filePathName;
        }
        StringBuilder sb = new StringBuilder(filePathName);
        int size = sb.length();
        while (size > 0 && ((ch = sb.charAt(size - 1)) == '.' || ch == File.separatorChar)) {
            size--;
            sb.setLength(size);
        }
        return sb.toString();
    }

    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static String getIOTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String toHexString(byte[] data) {
        return new String(toHexChars(data));
    }

    public static char[] toHexChars(byte[] data) {
        if (data == null || data.length <= 0) {
            return EMPTY_CHAR_ARRAY;
        }
        int size = data.length;
        char[] result = new char[size << 1];
        int j = 0;
        for (byte b : data) {
            int i = j;
            int j2 = j + 1;
            result[i] = HEX_DIGITS[(b & 240) >>> 4];
            j = j2 + 1;
            result[j2] = HEX_DIGITS[b & 15];
        }
        return result;
    }
}
