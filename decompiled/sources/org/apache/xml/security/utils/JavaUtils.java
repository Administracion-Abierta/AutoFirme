package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.security.SecurityPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/JavaUtils.class */
public final class JavaUtils {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) JavaUtils.class);
    private static final SecurityPermission REGISTER_PERMISSION = new SecurityPermission("org.apache.xml.security.register");

    private JavaUtils() {
    }

    public static byte[] getBytesFromFile(String fileName) throws Exception {
        InputStream inputStream = Files.newInputStream(Paths.get(fileName, new String[0]), new OpenOption[0]);
        try {
            UnsyncByteArrayOutputStream baos = new UnsyncByteArrayOutputStream();
            Throwable th = null;
            try {
                try {
                    byte[] buf = new byte[1024];
                    while (true) {
                        int len = inputStream.read(buf);
                        if (len <= 0) {
                            break;
                        }
                        baos.write(buf, 0, len);
                        $closeResource(th, baos);
                        throw th;
                    }
                    byte[] refBytes = baos.toByteArray();
                    $closeResource(null, baos);
                    if (inputStream != null) {
                        $closeResource(null, inputStream);
                    }
                    return refBytes;
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, baos);
                throw th3;
            }
        } catch (Throwable th4) {
            if (inputStream != null) {
                $closeResource(null, inputStream);
            }
            throw th4;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    public static void writeBytesToFilename(String filename, byte[] bytes) throws Exception {
        Throwable th;
        if (filename != null && bytes != null) {
            try {
                OutputStream outputStream = Files.newOutputStream(Paths.get(filename, new String[0]), new OpenOption[0]);
                Throwable th2 = null;
                try {
                    try {
                        outputStream.write(bytes);
                        if (outputStream != null) {
                        }
                        return;
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } finally {
                    if (outputStream != null) {
                        $closeResource(th, outputStream);
                    }
                }
            } catch (IOException ex) {
                LOG.debug(ex.getMessage(), (Throwable) ex);
                return;
            }
        }
        LOG.debug("writeBytesToFilename got null byte[] pointed");
    }

    public static byte[] getBytesFromStream(InputStream inputStream) throws Exception {
        UnsyncByteArrayOutputStream baos = new UnsyncByteArrayOutputStream();
        try {
            byte[] buf = new byte[4096];
            while (true) {
                int len = inputStream.read(buf);
                if (len > 0) {
                    baos.write(buf, 0, len);
                } else {
                    byte[] byteArray = baos.toByteArray();
                    $closeResource(null, baos);
                    return byteArray;
                }
            }
        } catch (Throwable th) {
            $closeResource(null, baos);
            throw th;
        }
    }

    public static byte[] convertDsaASN1toXMLDSIG(byte[] asn1Bytes, int size) throws IOException {
        if (asn1Bytes[0] != 48 || asn1Bytes[1] != asn1Bytes.length - 2 || asn1Bytes[2] != 2) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        byte rLength = asn1Bytes[3];
        int i = rLength;
        while (i > 0 && asn1Bytes[(4 + rLength) - i] == 0) {
            i--;
        }
        byte sLength = asn1Bytes[5 + rLength];
        int j = sLength;
        while (j > 0 && asn1Bytes[((6 + rLength) + sLength) - j] == 0) {
            j--;
        }
        if (i > size || asn1Bytes[4 + rLength] != 2 || j > size) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        byte[] xmldsigBytes = new byte[size * 2];
        System.arraycopy(asn1Bytes, (4 + rLength) - i, xmldsigBytes, size - i, i);
        System.arraycopy(asn1Bytes, ((6 + rLength) + sLength) - j, xmldsigBytes, (size * 2) - j, j);
        return xmldsigBytes;
    }

    public static byte[] convertDsaXMLDSIGtoASN1(byte[] xmldsigBytes, int size) throws IOException {
        int totalSize = size * 2;
        if (xmldsigBytes.length != totalSize) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }
        int i = size;
        while (i > 0 && xmldsigBytes[size - i] == 0) {
            i--;
        }
        int j = i;
        if (xmldsigBytes[size - i] < 0) {
            j++;
        }
        int k = size;
        while (k > 0 && xmldsigBytes[totalSize - k] == 0) {
            k--;
        }
        int l = k;
        if (xmldsigBytes[totalSize - k] < 0) {
            l++;
        }
        byte[] asn1Bytes = new byte[6 + j + l];
        asn1Bytes[0] = 48;
        asn1Bytes[1] = (byte) (4 + j + l);
        asn1Bytes[2] = 2;
        asn1Bytes[3] = (byte) j;
        System.arraycopy(xmldsigBytes, size - i, asn1Bytes, (4 + j) - i, i);
        asn1Bytes[4 + j] = 2;
        asn1Bytes[5 + j] = (byte) l;
        System.arraycopy(xmldsigBytes, totalSize - k, asn1Bytes, ((6 + j) + l) - k, k);
        return asn1Bytes;
    }

    public static void checkRegisterPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(REGISTER_PERMISSION);
        }
    }

    public static <T> T newInstanceWithEmptyConstructor(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        try {
            return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            throw ((InstantiationException) new InstantiationException(clazz.getName()).initCause(e));
        }
    }
}
