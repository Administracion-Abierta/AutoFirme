package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.FileUtils;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/MacFileUtils.class */
public class MacFileUtils extends FileUtils {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/MacFileUtils$FileManager.class */
    public interface FileManager extends Library {
        public static final FileManager INSTANCE = (FileManager) Native.load("CoreServices", FileManager.class);
        public static final int kFSFileOperationDefaultOptions = 0;
        public static final int kFSFileOperationsOverwrite = 1;
        public static final int kFSFileOperationsSkipSourcePermissionErrors = 2;
        public static final int kFSFileOperationsDoNotMoveAcrossVolumes = 4;
        public static final int kFSFileOperationsSkipPreflight = 8;
        public static final int kFSPathDefaultOptions = 0;
        public static final int kFSPathMakeRefDoNotFollowLeafSymlink = 1;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/MacFileUtils$FileManager$FSRef.class */
        @Structure.FieldOrder({"hidden"})
        public static class FSRef extends Structure {
            public byte[] hidden = new byte[80];
        }

        int FSRefMakePath(FSRef fSRef, byte[] bArr, int i);

        int FSPathMakeRef(String str, int i, ByteByReference byteByReference);

        int FSPathMakeRefWithOptions(String str, int i, FSRef fSRef, ByteByReference byteByReference);

        int FSPathMoveObjectToTrashSync(String str, PointerByReference pointerByReference, int i);

        int FSMoveObjectToTrashSync(FSRef fSRef, FSRef fSRef2, int i);
    }

    @Override // com.sun.jna.platform.FileUtils
    public boolean hasTrash() {
        return true;
    }

    @Override // com.sun.jna.platform.FileUtils
    public void moveToTrash(File... files) throws IOException {
        List<String> failed = new ArrayList<>();
        for (File src : files) {
            FileManager.FSRef fsref = new FileManager.FSRef();
            int status = FileManager.INSTANCE.FSPathMakeRefWithOptions(src.getAbsolutePath(), 1, fsref, null);
            if (status != 0) {
                failed.add(src + " (FSRef: " + status + ")");
            } else {
                int status2 = FileManager.INSTANCE.FSMoveObjectToTrashSync(fsref, null, 0);
                if (status2 != 0) {
                    failed.add(src + " (" + status2 + ")");
                }
            }
        }
        if (failed.size() > 0) {
            throw new IOException("The following files could not be trashed: " + failed);
        }
    }
}
