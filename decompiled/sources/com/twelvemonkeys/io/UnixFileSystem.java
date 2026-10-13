package com.twelvemonkeys.io;

import com.twelvemonkeys.util.StringTokenIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/UnixFileSystem.class */
final class UnixFileSystem extends FileSystem {
    UnixFileSystem() {
    }

    @Override // com.twelvemonkeys.io.FileSystem
    long getFreeSpace(File file) {
        try {
            return getNumber(file, 3);
        } catch (IOException e) {
            return 0L;
        }
    }

    @Override // com.twelvemonkeys.io.FileSystem
    long getTotalSpace(File file) {
        try {
            return getNumber(file, 5);
        } catch (IOException e) {
            return 0L;
        }
    }

    private long getNumber(File file, int i) throws IOException {
        BufferedReader bufferedReaderExec = exec(new String[]{"df", "-k", file.getAbsolutePath()});
        String str = null;
        while (true) {
            try {
                String line = bufferedReaderExec.readLine();
                if (line == null) {
                    break;
                }
                str = line;
            } catch (Throwable th) {
                FileUtil.close(bufferedReaderExec);
                throw th;
            }
        }
        FileUtil.close(bufferedReaderExec);
        if (str != null) {
            String strNextToken = null;
            StringTokenIterator stringTokenIterator = new StringTokenIterator(str, " ", -1);
            for (int i2 = 0; i2 < i && stringTokenIterator.hasNext(); i2++) {
                strNextToken = stringTokenIterator.nextToken();
            }
            if (strNextToken != null) {
                try {
                    return Long.parseLong(strNextToken) * 1024;
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
            return 0L;
        }
        return 0L;
    }

    @Override // com.twelvemonkeys.io.FileSystem
    String getName() {
        return "Unix";
    }
}
