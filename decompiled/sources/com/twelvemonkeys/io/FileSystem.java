package com.twelvemonkeys.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FileSystem.class */
abstract class FileSystem {
    abstract long getFreeSpace(File file);

    abstract long getTotalSpace(File file);

    abstract String getName();

    FileSystem() {
    }

    static BufferedReader exec(String[] strArr) throws IOException {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(strArr).getInputStream()));
    }

    static FileSystem get() {
        String lowerCase = System.getProperty("os.name").toLowerCase();
        if (lowerCase.contains("windows")) {
            return new Win32FileSystem();
        }
        if (lowerCase.contains("linux") || lowerCase.contains("sun os") || lowerCase.contains("sunos") || lowerCase.contains("solaris") || lowerCase.contains("mpe/ix") || lowerCase.contains("hp-ux") || lowerCase.contains("aix") || lowerCase.contains("freebsd") || lowerCase.contains("irix") || lowerCase.contains("digital unix") || lowerCase.contains("unix") || lowerCase.contains("mac os x")) {
            return new UnixFileSystem();
        }
        return new UnknownFileSystem(lowerCase);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FileSystem$UnknownFileSystem.class */
    private static class UnknownFileSystem extends FileSystem {
        private final String osName;

        UnknownFileSystem(String str) {
            this.osName = str;
        }

        @Override // com.twelvemonkeys.io.FileSystem
        long getFreeSpace(File file) {
            return 0L;
        }

        @Override // com.twelvemonkeys.io.FileSystem
        long getTotalSpace(File file) {
            return 0L;
        }

        @Override // com.twelvemonkeys.io.FileSystem
        String getName() {
            return "Unknown (" + this.osName + ")";
        }
    }
}
