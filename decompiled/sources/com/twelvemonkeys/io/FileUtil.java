package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.Visitor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.text.NumberFormat;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FileUtil.class */
public final class FileUtil {
    public static final int BUF_SIZE = 1024;
    private static String TEMP_DIR = null;
    private static final FileSystem FS = FileSystem.get();
    private static ThreadLocal<NumberFormat> sNumberFormat = new ThreadLocal<NumberFormat>() { // from class: com.twelvemonkeys.io.FileUtil.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public NumberFormat initialValue() {
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            numberInstance.setMaximumFractionDigits(0);
            return numberInstance;
        }
    };

    public static void main(String[] strArr) throws IOException {
        File file;
        if (strArr[0].startsWith("file:")) {
            file = toFile(new URL(strArr[0]));
            System.out.println(file);
        } else {
            file = new File(strArr[0]);
            System.out.println(file.toURL());
        }
        System.out.println("Free space: " + getFreeSpace(file) + "/" + getTotalSpace(file) + " bytes");
    }

    private FileUtil() {
    }

    public static boolean copy(String str, String str2) throws IOException {
        return copy(new File(str), new File(str2), false);
    }

    public static boolean copy(String str, String str2, boolean z) throws IOException {
        return copy(new File(str), new File(str2), z);
    }

    public static boolean copy(File file, File file2) throws IOException {
        return copy(file, file2, false);
    }

    public static boolean copy(File file, File file2, boolean z) throws IOException {
        if (file.isDirectory()) {
            return copyDir(file, file2, z);
        }
        if (file2.isDirectory()) {
            file2 = new File(file2, file.getName());
        }
        if (!z && file2.exists()) {
            return false;
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
            copy(fileInputStream, fileOutputStream2);
            close(fileInputStream);
            return true;
        } finally {
            close(fileInputStream);
            close(fileOutputStream);
        }
    }

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    private static boolean copyDir(File file, File file2, boolean z) throws IOException {
        if (file2.exists() && !file2.isDirectory()) {
            throw new IOException("A directory may only be copied to another directory, not to a file");
        }
        file2.mkdirs();
        boolean z2 = true;
        for (File file3 : file.listFiles()) {
            if (!copy(file3, new File(file2, file3.getName()), z)) {
                z2 = false;
            }
        }
        return z2;
    }

    public static boolean copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        Validate.notNull(inputStream, "from");
        Validate.notNull(outputStream, "to");
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 2048);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 2048);
        byte[] bArr = new byte[1024];
        while (true) {
            int i = bufferedInputStream.read(bArr);
            if (i != -1) {
                bufferedOutputStream.write(bArr, 0, i);
            } else {
                bufferedOutputStream.flush();
                return true;
            }
        }
    }

    public static String getExtension(String str) {
        return getExtension0(getFilename(str));
    }

    public static String getExtension(File file) {
        return getExtension0(file.getName());
    }

    private static String getExtension0(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf >= 0) {
            return str.substring(iLastIndexOf + 1);
        }
        return null;
    }

    public static String getBasename(String str) {
        return getBasename0(getFilename(str));
    }

    public static String getBasename(File file) {
        return getBasename0(file.getName());
    }

    public static String getBasename0(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf >= 0) {
            return str.substring(0, iLastIndexOf);
        }
        return str;
    }

    public static String getDirectoryname(String str) {
        return getDirectoryname(str, File.separatorChar);
    }

    public static String getDirectoryname(String str, char c) {
        int iLastIndexOf = str.lastIndexOf(c);
        if (iLastIndexOf < 0) {
            return "";
        }
        return str.substring(0, iLastIndexOf);
    }

    public static String getFilename(String str) {
        return getFilename(str, File.separatorChar);
    }

    public static String getFilename(String str, char c) {
        int iLastIndexOf = str.lastIndexOf(c);
        if (iLastIndexOf < 0) {
            return str;
        }
        return str.substring(iLastIndexOf + 1);
    }

    public static boolean isEmpty(File file) {
        if (file.isDirectory()) {
            return file.list().length == 0;
        }
        return file.length() == 0;
    }

    public static File getTempDirFile() {
        return new File(getTempDir());
    }

    public static String getTempDir() {
        synchronized (FileUtil.class) {
            if (TEMP_DIR == null) {
                String property = System.getProperty("java.io.tmpdir");
                if (StringUtil.isEmpty(property)) {
                    if (new File("/temp").exists()) {
                        property = "/temp";
                    } else {
                        property = "/tmp";
                    }
                }
                TEMP_DIR = property;
            }
        }
        return TEMP_DIR;
    }

    public static byte[] read(String str) throws IOException {
        return read(new File(str));
    }

    public static byte[] read(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        byte[] bArr = new byte[(int) file.length()];
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 2048);
            int i = 0;
            while (true) {
                int i2 = bufferedInputStream.read(bArr, i, bufferedInputStream.available());
                if (i2 == -1 || i >= bArr.length) {
                    break;
                }
                i += i2;
            }
            close(bufferedInputStream);
            return bArr;
        } catch (Throwable th) {
            close(bufferedInputStream);
            throw th;
        }
    }

    public static byte[] read(InputStream inputStream) throws IOException {
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream(1024);
        copy(inputStream, fastByteArrayOutputStream);
        return fastByteArrayOutputStream.toByteArray();
    }

    public static boolean write(OutputStream outputStream, byte[] bArr) throws IOException {
        outputStream.write(bArr);
        return true;
    }

    public static boolean write(File file, byte[] bArr) throws IOException {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(new FileOutputStream(file));
            return write(bufferedOutputStream2, bArr);
        } finally {
            close(bufferedOutputStream);
        }
    }

    public static boolean write(String str, byte[] bArr) throws IOException {
        return write(new File(str), bArr);
    }

    public static boolean delete(File file, boolean z) throws IOException {
        if (z && file.isDirectory()) {
            return deleteDir(file);
        }
        return file.exists() && file.delete();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.twelvemonkeys.io.FileUtil$1DeleteFilesVisitor, com.twelvemonkeys.util.Visitor] */
    private static boolean deleteDir(File file) throws IOException {
        ?? r0 = new Visitor<File>() { // from class: com.twelvemonkeys.io.FileUtil.1DeleteFilesVisitor
            private int failedCount = 0;
            private IOException exception = null;

            @Override // com.twelvemonkeys.util.Visitor
            public void visit(File file2) {
                try {
                    if (!FileUtil.delete(file2, true)) {
                        this.failedCount++;
                    }
                } catch (IOException e) {
                    this.failedCount++;
                    if (this.exception == null) {
                        this.exception = e;
                    }
                }
            }

            boolean succeeded() throws IOException {
                if (this.exception != null) {
                    throw this.exception;
                }
                return this.failedCount == 0;
            }
        };
        visitFiles(file, null, r0);
        return r0.succeeded() && file.delete();
    }

    public static boolean delete(String str, boolean z) throws IOException {
        return delete(new File(str), z);
    }

    public static boolean delete(File file) throws IOException {
        return delete(file, false);
    }

    public static boolean delete(String str) throws IOException {
        return delete(new File(str), false);
    }

    public static boolean rename(File file, File file2, boolean z) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (file.isFile() && file2.isDirectory()) {
            file2 = new File(file2, file.getName());
        }
        return (z || !file2.exists()) && file.renameTo(file2);
    }

    public static boolean rename(File file, File file2) throws IOException {
        return rename(file, file2, false);
    }

    public static boolean rename(File file, String str, boolean z) throws IOException {
        return rename(file, new File(str), z);
    }

    public static boolean rename(File file, String str) throws IOException {
        return rename(file, new File(str), false);
    }

    public static boolean rename(String str, String str2, boolean z) throws IOException {
        return rename(new File(str), new File(str2), z);
    }

    public static boolean rename(String str, String str2) throws IOException {
        return rename(new File(str), new File(str2), false);
    }

    public static File[] list(String str) throws FileNotFoundException {
        return list(str, null);
    }

    public static File[] list(String str, String str2) throws FileNotFoundException {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        File fileResolve = resolve(str);
        if (!fileResolve.isDirectory() || !fileResolve.canRead()) {
            throw new FileNotFoundException(OperatorName.SHOW_TEXT_LINE_AND_SPACE + str + "\" is not a directory or is not readable.");
        }
        if (StringUtil.isEmpty(str2)) {
            return fileResolve.listFiles();
        }
        return fileResolve.listFiles(new FilenameMaskFilter(str2));
    }

    public static File toFile(URL url) {
        if (url == null) {
            throw new NullPointerException("URL == null");
        }
        if (!"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("URL scheme is not \"file\"");
        }
        if (url.getAuthority() != null) {
            throw new IllegalArgumentException("URL has an authority component");
        }
        if (url.getRef() != null) {
            throw new IllegalArgumentException("URI has a fragment component");
        }
        if (url.getQuery() != null) {
            throw new IllegalArgumentException("URL has a query component");
        }
        String path = url.getPath();
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("URI is not hierarchical");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("URI path component is empty");
        }
        if (File.separatorChar != '/') {
            path = path.replace('/', File.separatorChar);
        }
        return resolve(path);
    }

    public static File resolve(String str) {
        return Win32File.wrap(new File(str));
    }

    public static File resolve(File file) {
        return Win32File.wrap(file);
    }

    public static File resolve(File file, String str) {
        return Win32File.wrap(new File(file, str));
    }

    public static File[] resolve(File[] fileArr) {
        return Win32File.wrap(fileArr);
    }

    public static long getFreeSpace(File file) {
        File file2 = file != null ? file : new File(".");
        Long space16 = getSpace16("getFreeSpace", file2);
        if (space16 != null) {
            return space16.longValue();
        }
        return FS.getFreeSpace(file2);
    }

    public static long getUsableSpace(File file) {
        File file2 = file != null ? file : new File(".");
        Long space16 = getSpace16("getUsableSpace", file2);
        if (space16 != null) {
            return space16.longValue();
        }
        return getTotalSpace(file2);
    }

    public static long getTotalSpace(File file) {
        File file2 = file != null ? file : new File(".");
        Long space16 = getSpace16("getTotalSpace", file2);
        if (space16 != null) {
            return space16.longValue();
        }
        return FS.getTotalSpace(file2);
    }

    private static Long getSpace16(String str, File file) {
        try {
            return (Long) File.class.getMethod(str, new Class[0]).invoke(file, new Object[0]);
        } catch (IllegalAccessException e) {
            return null;
        } catch (NoSuchMethodException e2) {
            return null;
        } catch (InvocationTargetException e3) {
            Throwable targetException = e3.getTargetException();
            if (targetException instanceof SecurityException) {
                throw ((SecurityException) targetException);
            }
            throw new UndeclaredThrowableException(targetException);
        }
    }

    public static String toHumanReadableSize(long j) {
        if (j < 1024) {
            return j + " Bytes";
        }
        if (j < 1048576) {
            return getSizeFormat().format(j / 1024.0d) + " KB";
        }
        if (j < 1073741824) {
            return getSizeFormat().format(j / 1048576.0d) + " MB";
        }
        if (j < 1099511627776L) {
            return getSizeFormat().format(j / 1.073741824E9d) + " GB";
        }
        if (j < 1125899906842624L) {
            return getSizeFormat().format(j / 1.099511627776E12d) + " TB";
        }
        return getSizeFormat().format(j / 1.125899906842624E15d) + " PB";
    }

    private static NumberFormat getSizeFormat() {
        return sNumberFormat.get();
    }

    public static void visitFiles(File file, final FileFilter fileFilter, final Visitor<File> visitor) {
        Validate.notNull(file, "directory");
        Validate.notNull(visitor, "visitor");
        file.listFiles(new FileFilter() { // from class: com.twelvemonkeys.io.FileUtil.2
            @Override // java.io.FileFilter
            public boolean accept(File file2) {
                if (fileFilter == null || fileFilter.accept(file2)) {
                    visitor.visit(file2);
                    return false;
                }
                return false;
            }
        });
    }
}
