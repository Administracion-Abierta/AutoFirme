package com.twelvemonkeys.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/Win32FileSystem.class */
final class Win32FileSystem extends FileSystem {
    Win32FileSystem() {
    }

    @Override // com.twelvemonkeys.io.FileSystem
    public long getFreeSpace(File file) {
        try {
            BufferedReader bufferedReaderExec = exec(new String[]{"CMD.EXE", "/C", "DIR", "/-C", file.getAbsolutePath()});
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
                int iLastIndexOf = str.lastIndexOf(" bytes free");
                int iLastIndexOf2 = str.lastIndexOf(32, iLastIndexOf - 1);
                if (iLastIndexOf2 >= 0 && iLastIndexOf >= 0) {
                    try {
                        return Long.parseLong(str.substring(iLastIndexOf2 + 1, iLastIndexOf));
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return 0L;
        } catch (IOException e2) {
            return 0L;
        }
    }

    @Override // com.twelvemonkeys.io.FileSystem
    long getTotalSpace(File file) {
        return getFreeSpace(file);
    }

    @Override // com.twelvemonkeys.io.FileSystem
    String getName() {
        return "Win32";
    }
}
