package com.sun.jna.platform;

import com.sun.jna.platform.mac.MacFileUtils;
import com.sun.jna.platform.win32.W32FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/FileUtils.class */
public abstract class FileUtils {
    public abstract void moveToTrash(File... fileArr) throws IOException;

    public boolean hasTrash() {
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/FileUtils$Holder.class */
    private static class Holder {
        public static final FileUtils INSTANCE;

        private Holder() {
        }

        static {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                INSTANCE = new W32FileUtils();
            } else if (os.startsWith("Mac")) {
                INSTANCE = new MacFileUtils();
            } else {
                INSTANCE = new DefaultFileUtils();
            }
        }
    }

    public static FileUtils getInstance() {
        return Holder.INSTANCE;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/FileUtils$DefaultFileUtils.class */
    private static class DefaultFileUtils extends FileUtils {
        private DefaultFileUtils() {
        }

        private File getTrashDirectory() {
            File home = new File(System.getProperty("user.home"));
            File trash = new File(home, ".Trash");
            if (!trash.exists()) {
                trash = new File(home, "Trash");
                if (!trash.exists()) {
                    File desktop = new File(home, "Desktop");
                    if (desktop.exists()) {
                        trash = new File(desktop, ".Trash");
                        if (!trash.exists()) {
                            trash = new File(desktop, "Trash");
                            if (!trash.exists()) {
                                trash = new File(System.getProperty("fileutils.trash", "Trash"));
                            }
                        }
                    }
                }
            }
            return trash;
        }

        @Override // com.sun.jna.platform.FileUtils
        public boolean hasTrash() {
            return getTrashDirectory().exists();
        }

        @Override // com.sun.jna.platform.FileUtils
        public void moveToTrash(File... files) throws IOException {
            File trash = getTrashDirectory();
            if (!trash.exists()) {
                throw new IOException("No trash location found (define fileutils.trash to be the path to the trash)");
            }
            List<File> failed = new ArrayList<>();
            for (File src : files) {
                File target = new File(trash, src.getName());
                if (!src.renameTo(target)) {
                    failed.add(src);
                }
            }
            if (failed.size() > 0) {
                throw new IOException("The following files could not be trashed: " + failed);
            }
        }
    }
}
