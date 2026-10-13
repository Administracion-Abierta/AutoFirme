package com.twelvemonkeys.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/Win32Lnk.class */
final class Win32Lnk extends File {
    private static final byte[] LNK_MAGIC = {76, 0, 0, 0};
    private static final byte[] LNK_GUID = {1, 20, 2, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 70};
    private final File target;
    private static final int FLAG_ITEM_ID_LIST = 1;
    private static final int FLAG_FILE_LOC_INFO = 2;
    private static final int FLAG_DESC_STRING = 4;
    private static final int FLAG_REL_PATH_STRING = 8;
    private static final int FLAG_WORKING_DIRECTORY = 16;
    private static final int FLAG_COMMAND_LINE_ARGS = 32;
    private static final int FLAG_ICON_FILENAME = 64;
    private static final int FLAG_ADDITIONAL_INFO = 128;

    private Win32Lnk(String str) throws IOException {
        super(str);
        File file = parse(this);
        this.target = file == this ? new File(str) : file;
    }

    Win32Lnk(File file) throws IOException {
        this(file.getPath());
    }

    static File parse(File file) throws IOException {
        if (!file.getName().endsWith(".lnk")) {
            return file;
        }
        File file2 = file;
        LittleEndianDataInputStream littleEndianDataInputStream = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(file)));
        try {
            byte[] bArr = new byte[4];
            littleEndianDataInputStream.readFully(bArr);
            byte[] bArr2 = new byte[16];
            littleEndianDataInputStream.readFully(bArr2);
            if (Arrays.equals(LNK_MAGIC, bArr) && Arrays.equals(LNK_GUID, bArr2)) {
                int i = littleEndianDataInputStream.readInt();
                littleEndianDataInputStream.readInt();
                littleEndianDataInputStream.skipBytes(48);
                if ((i & 1) != 0) {
                    littleEndianDataInputStream.skipBytes(littleEndianDataInputStream.readShort());
                }
                if ((i & 2) != 0) {
                    int i2 = littleEndianDataInputStream.readInt();
                    littleEndianDataInputStream.readInt();
                    int i3 = littleEndianDataInputStream.readInt();
                    if ((i3 & 1) != 0) {
                    }
                    if ((i3 & 2) != 0) {
                    }
                    littleEndianDataInputStream.skipBytes(4);
                    int i4 = littleEndianDataInputStream.readInt();
                    littleEndianDataInputStream.skipBytes(i4 - 20);
                    byte[] bArr3 = new byte[(i2 - i4) - 1];
                    littleEndianDataInputStream.readFully(bArr3, 0, bArr3.length);
                    try {
                        file2 = parse(new File(new String(bArr3, 0, bArr3.length - 1)));
                    } catch (StackOverflowError e) {
                        throw new IOException("Cannot resolve cyclic link: " + e.getMessage());
                    }
                }
                if ((i & 4) != 0) {
                    int i5 = littleEndianDataInputStream.readShort();
                    littleEndianDataInputStream.readFully(new byte[i5], 0, i5);
                }
                if ((i & 8) != 0) {
                    int i6 = littleEndianDataInputStream.readShort();
                    byte[] bArr4 = new byte[i6];
                    littleEndianDataInputStream.readFully(bArr4, 0, i6);
                    String str = new String(bArr4, 0, i6);
                    if (file2 == file) {
                        try {
                            file2 = parse(new File(file.getParentFile(), str));
                        } catch (StackOverflowError e2) {
                            throw new IOException("Cannot resolve cyclic link: " + e2.getMessage());
                        }
                    }
                }
                if ((i & 16) != 0) {
                }
                if ((i & 32) != 0) {
                    file2 = file;
                }
                if ((i & 64) != 0) {
                }
                if ((i & 128) != 0) {
                }
                littleEndianDataInputStream.close();
                return file2;
            }
            littleEndianDataInputStream.close();
            return file;
        } catch (Throwable th) {
            littleEndianDataInputStream.close();
            throw th;
        }
    }

    public File getTarget() {
        return this.target;
    }

    @Override // java.io.File
    public boolean isDirectory() {
        return this.target.isDirectory();
    }

    @Override // java.io.File
    public boolean canRead() {
        return this.target.canRead();
    }

    @Override // java.io.File
    public boolean canWrite() {
        return this.target.canWrite();
    }

    @Override // java.io.File
    public boolean exists() {
        return this.target.exists();
    }

    @Override // java.io.File
    public File getCanonicalFile() throws IOException {
        return this.target.getCanonicalFile();
    }

    @Override // java.io.File
    public String getCanonicalPath() throws IOException {
        return this.target.getCanonicalPath();
    }

    @Override // java.io.File
    public boolean isFile() {
        return this.target.isFile();
    }

    @Override // java.io.File
    public boolean isHidden() {
        return this.target.isHidden();
    }

    @Override // java.io.File
    public long lastModified() {
        return this.target.lastModified();
    }

    @Override // java.io.File
    public long length() {
        return this.target.length();
    }

    @Override // java.io.File
    public String[] list() {
        return this.target.list();
    }

    @Override // java.io.File
    public String[] list(FilenameFilter filenameFilter) {
        return this.target.list(filenameFilter);
    }

    @Override // java.io.File
    public File[] listFiles() {
        return Win32File.wrap(this.target.listFiles());
    }

    @Override // java.io.File
    public File[] listFiles(FileFilter fileFilter) {
        return Win32File.wrap(this.target.listFiles(fileFilter));
    }

    @Override // java.io.File
    public File[] listFiles(FilenameFilter filenameFilter) {
        return Win32File.wrap(this.target.listFiles(filenameFilter));
    }

    @Override // java.io.File
    public boolean setLastModified(long j) {
        return this.target.setLastModified(j);
    }

    @Override // java.io.File
    public boolean setReadOnly() {
        return this.target.setReadOnly();
    }

    @Override // java.io.File
    public String toString() {
        if (this.target.equals(this)) {
            return super.toString();
        }
        return super.toString() + " -> " + this.target.toString();
    }
}
