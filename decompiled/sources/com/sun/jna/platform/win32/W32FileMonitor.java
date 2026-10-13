package com.sun.jna.platform.win32;

import com.sun.jna.platform.FileMonitor;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/W32FileMonitor.class */
public class W32FileMonitor extends FileMonitor {
    private static final Logger LOG = Logger.getLogger(W32FileMonitor.class.getName());
    private static final int BUFFER_SIZE = 4096;
    private Thread watcher;
    private WinNT.HANDLE port;
    private final Map<File, FileInfo> fileMap = new HashMap();
    private final Map<WinNT.HANDLE, FileInfo> handleMap = new HashMap();
    private boolean disposing = false;
    private static int watcherThreadID;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/W32FileMonitor$FileInfo.class */
    private class FileInfo {
        public final File file;
        public final WinNT.HANDLE handle;
        public final int notifyMask;
        public final boolean recursive;
        public final WinNT.FILE_NOTIFY_INFORMATION info = new WinNT.FILE_NOTIFY_INFORMATION(4096);
        public final IntByReference infoLength = new IntByReference();
        public final WinBase.OVERLAPPED overlapped = new WinBase.OVERLAPPED();

        public FileInfo(File f, WinNT.HANDLE h, int mask, boolean recurse) {
            this.file = f;
            this.handle = h;
            this.notifyMask = mask;
            this.recursive = recurse;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChanges(FileInfo finfo) throws IOException {
        Kernel32 klib = Kernel32.INSTANCE;
        WinNT.FILE_NOTIFY_INFORMATION fni = finfo.info;
        fni.read();
        do {
            FileMonitor.FileEvent event = null;
            File file = new File(finfo.file, fni.getFilename());
            switch (fni.Action) {
                case 0:
                    break;
                case 1:
                    event = new FileMonitor.FileEvent(file, 1);
                    break;
                case 2:
                    event = new FileMonitor.FileEvent(file, 2);
                    break;
                case 3:
                    event = new FileMonitor.FileEvent(file, 4);
                    break;
                case 4:
                    event = new FileMonitor.FileEvent(file, 16);
                    break;
                case 5:
                    event = new FileMonitor.FileEvent(file, 32);
                    break;
                default:
                    LOG.log(Level.WARNING, "Unrecognized file action ''{0}''", Integer.valueOf(fni.Action));
                    break;
            }
            if (event != null) {
                notify(event);
            }
            fni = fni.next();
        } while (fni != null);
        if (!finfo.file.exists()) {
            unwatch(finfo.file);
        } else if (!klib.ReadDirectoryChangesW(finfo.handle, finfo.info, finfo.info.size(), finfo.recursive, finfo.notifyMask, finfo.infoLength, finfo.overlapped, null) && !this.disposing) {
            int err = klib.GetLastError();
            throw new IOException("ReadDirectoryChangesW failed on " + finfo.file + ": '" + Kernel32Util.formatMessageFromLastErrorCode(err) + "' (" + err + ")");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public FileInfo waitForChange() {
        FileInfo fileInfo;
        IntByReference rcount = new IntByReference();
        BaseTSD.ULONG_PTRByReference rkey = new BaseTSD.ULONG_PTRByReference();
        PointerByReference roverlap = new PointerByReference();
        if (!Kernel32.INSTANCE.GetQueuedCompletionStatus(this.port, rcount, rkey, roverlap, -1)) {
            return null;
        }
        synchronized (this) {
            fileInfo = this.handleMap.get(new WinNT.HANDLE(rkey.getValue().toPointer()));
        }
        return fileInfo;
    }

    private int convertMask(int mask) {
        int result = 0;
        if ((mask & 1) != 0) {
            result = 0 | 64;
        }
        if ((mask & 2) != 0) {
            result |= 3;
        }
        if ((mask & 4) != 0) {
            result |= 16;
        }
        if ((mask & 48) != 0) {
            result |= 3;
        }
        if ((mask & 64) != 0) {
            result |= 8;
        }
        if ((mask & 8) != 0) {
            result |= 32;
        }
        if ((mask & 128) != 0) {
            result |= 4;
        }
        if ((mask & 256) != 0) {
            result |= 256;
        }
        return result;
    }

    @Override // com.sun.jna.platform.FileMonitor
    protected synchronized void watch(File file, int eventMask, boolean recursive) throws IOException {
        File dir = file;
        if (!dir.isDirectory()) {
            recursive = false;
            dir = file.getParentFile();
        }
        while (dir != null && !dir.exists()) {
            recursive = true;
            dir = dir.getParentFile();
        }
        if (dir == null) {
            throw new FileNotFoundException("No ancestor found for " + file);
        }
        Kernel32 klib = Kernel32.INSTANCE;
        WinNT.HANDLE handle = klib.CreateFile(file.getAbsolutePath(), 1, 7, null, 3, 1107296256, null);
        if (WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
            throw new IOException("Unable to open " + file + " (" + klib.GetLastError() + ")");
        }
        int notifyMask = convertMask(eventMask);
        FileInfo finfo = new FileInfo(file, handle, notifyMask, recursive);
        this.fileMap.put(file, finfo);
        this.handleMap.put(handle, finfo);
        this.port = klib.CreateIoCompletionPort(handle, this.port, handle.getPointer(), 0);
        if (WinBase.INVALID_HANDLE_VALUE.equals(this.port)) {
            throw new IOException("Unable to create/use I/O Completion port for " + file + " (" + klib.GetLastError() + ")");
        }
        if (!klib.ReadDirectoryChangesW(handle, finfo.info, finfo.info.size(), recursive, notifyMask, finfo.infoLength, finfo.overlapped, null)) {
            int err = klib.GetLastError();
            throw new IOException("ReadDirectoryChangesW failed on " + finfo.file + ", handle " + handle + ": '" + Kernel32Util.formatMessageFromLastErrorCode(err) + "' (" + err + ")");
        }
        if (this.watcher == null) {
            StringBuilder sbAppend = new StringBuilder().append("W32 File Monitor-");
            int i = watcherThreadID;
            watcherThreadID = i + 1;
            this.watcher = new Thread(sbAppend.append(i).toString()) { // from class: com.sun.jna.platform.win32.W32FileMonitor.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    while (true) {
                        FileInfo finfo2 = W32FileMonitor.this.waitForChange();
                        if (finfo2 != null) {
                            try {
                                W32FileMonitor.this.handleChanges(finfo2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            synchronized (W32FileMonitor.this) {
                                if (W32FileMonitor.this.fileMap.isEmpty()) {
                                    W32FileMonitor.this.watcher = null;
                                    return;
                                }
                            }
                        }
                    }
                }
            };
            this.watcher.setDaemon(true);
            this.watcher.start();
        }
    }

    @Override // com.sun.jna.platform.FileMonitor
    protected synchronized void unwatch(File file) {
        FileInfo finfo = this.fileMap.remove(file);
        if (finfo != null) {
            this.handleMap.remove(finfo.handle);
            Kernel32 klib = Kernel32.INSTANCE;
            klib.CloseHandle(finfo.handle);
        }
    }

    @Override // com.sun.jna.platform.FileMonitor
    public synchronized void dispose() {
        this.disposing = true;
        int i = 0;
        Object[] keys = this.fileMap.keySet().toArray();
        while (!this.fileMap.isEmpty()) {
            int i2 = i;
            i++;
            unwatch((File) keys[i2]);
        }
        Kernel32 klib = Kernel32.INSTANCE;
        klib.PostQueuedCompletionStatus(this.port, 0, null, null);
        klib.CloseHandle(this.port);
        this.port = null;
        this.watcher = null;
    }
}
