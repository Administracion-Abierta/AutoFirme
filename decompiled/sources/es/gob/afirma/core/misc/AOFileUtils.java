package es.gob.afirma.core.misc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/AOFileUtils.class */
public final class AOFileUtils {
    private static final String SHORTENER_ELLIPSE = "...";
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static boolean posixSupported = false;
    private static boolean posixSupportDefined = false;

    private AOFileUtils() {
    }

    public static File createTempFile(byte[] bArr) throws IOException {
        File fileCreateTempFile = File.createTempFile("afirma", null);
        FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
        Throwable th = null;
        try {
            try {
                fileOutputStream.write(bArr);
                fileOutputStream.flush();
                fileOutputStream.close();
                if (fileOutputStream != null) {
                    if (0 != 0) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                return fileCreateTempFile;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (fileOutputStream != null) {
                if (th != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th4;
        }
    }

    public static String pathLengthShortener(String str, int i) {
        if (str == null) {
            throw new IllegalArgumentException("El numbre de la ruta no puede ser nulo");
        }
        if (str.length() <= i) {
            return str;
        }
        char[] cArr = new char[i];
        char[] charArray = str.toCharArray();
        char[] charArray2 = SHORTENER_ELLIPSE.toCharArray();
        int length = charArray.length - 1;
        int i2 = i - 1;
        int i3 = 0;
        while (i3 < i && charArray[length - i3] != '/' && charArray[length - i3] != '\\') {
            cArr[i2 - i3] = charArray[length - i3];
            i3++;
        }
        int i4 = i - i3;
        if (i4 < SHORTENER_ELLIPSE.length()) {
            System.arraycopy(charArray2, 0, cArr, 0, charArray2.length);
        } else {
            int i5 = 0;
            while (i5 + charArray2.length < i4) {
                cArr[i5] = charArray[i5];
                i5++;
            }
            for (int i6 = 0; i5 + i6 < i4; i6++) {
                cArr[i5 + i6] = charArray2[i6];
            }
        }
        return new String(cArr);
    }

    public static boolean isXML(byte[] bArr) {
        try {
            XMLReader xMLReader = SecureXmlBuilder.getSecureSAXParser().getXMLReader();
            xMLReader.setErrorHandler(new ErrorHandler() { // from class: es.gob.afirma.core.misc.AOFileUtils.1
                @Override // org.xml.sax.ErrorHandler
                public void warning(SAXParseException sAXParseException) {
                    log(sAXParseException);
                }

                @Override // org.xml.sax.ErrorHandler
                public void fatalError(SAXParseException sAXParseException) {
                    log(sAXParseException);
                }

                @Override // org.xml.sax.ErrorHandler
                public void error(SAXParseException sAXParseException) {
                    log(sAXParseException);
                }

                private void log(Exception exc) {
                    AOFileUtils.LOGGER.fine("El documento no es un XML: " + exc);
                }
            });
            xMLReader.parse(new InputSource(new ByteArrayInputStream(bArr)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setAllPermissions(File file) {
        try {
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se han podido establecer los permisos del archivo " + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()), (Throwable) e);
        }
        AclFileAttributeView aclAttributes = getAclAttributes(file);
        if (aclAttributes != null) {
            try {
                List<AclEntry> acl = aclAttributes.getAcl();
                ArrayList arrayList = new ArrayList();
                Iterator<AclEntry> it = acl.iterator();
                while (it.hasNext()) {
                    arrayList.add(AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(it.next().principal()).setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT).setPermissions(AclEntryPermission.WRITE_NAMED_ATTRS, AclEntryPermission.WRITE_ATTRIBUTES, AclEntryPermission.DELETE, AclEntryPermission.WRITE_DATA, AclEntryPermission.READ_ACL, AclEntryPermission.APPEND_DATA, AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.READ_DATA, AclEntryPermission.EXECUTE, AclEntryPermission.SYNCHRONIZE, AclEntryPermission.READ_NAMED_ATTRS, AclEntryPermission.DELETE, AclEntryPermission.DELETE_CHILD).build());
                }
                aclAttributes.setAcl(arrayList);
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "No se ha podido establecer la ACL del archivo " + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()), (Throwable) e2);
            }
        }
        if (isPosixSupported()) {
            HashSet hashSet = new HashSet();
            hashSet.add(PosixFilePermission.OWNER_READ);
            hashSet.add(PosixFilePermission.GROUP_READ);
            hashSet.add(PosixFilePermission.OTHERS_READ);
            hashSet.add(PosixFilePermission.OWNER_WRITE);
            hashSet.add(PosixFilePermission.GROUP_WRITE);
            hashSet.add(PosixFilePermission.OTHERS_WRITE);
            hashSet.add(PosixFilePermission.OWNER_EXECUTE);
            hashSet.add(PosixFilePermission.GROUP_EXECUTE);
            hashSet.add(PosixFilePermission.OTHERS_EXECUTE);
            try {
                Files.setPosixFilePermissions(file.toPath(), hashSet);
            } catch (Exception e3) {
                LOGGER.log(Level.WARNING, "No se han podido dar permisos posix sobre el archivo " + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()), (Throwable) e3);
            }
        }
    }

    private static boolean isPosixSupported() {
        if (!posixSupportDefined) {
            posixSupported = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
            posixSupportDefined = true;
        }
        return posixSupported;
    }

    private static AclFileAttributeView getAclAttributes(File file) {
        return (AclFileAttributeView) Files.getFileAttributeView(file.toPath(), AclFileAttributeView.class, new LinkOption[0]);
    }
}
