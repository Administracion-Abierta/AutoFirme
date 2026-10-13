package com.sun.java.help.search;

import java.io.IOException;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/RAFFileFactoryOn12.class */
public final class RAFFileFactoryOn12 {
    private static int BUF_SIZE = 2048;
    private static final boolean debug = false;

    public static RAFFile get(URLConnection uRLConnection) throws IOException {
        RAFFile memoryRAFFile;
        debug(new StringBuffer().append("get on ").append(uRLConnection).toString());
        Permission permission = uRLConnection.getPermission();
        uRLConnection.getContentLength();
        try {
            memoryRAFFile = (RAFFile) AccessController.doPrivileged(new PrivilegedExceptionAction(uRLConnection, permission) { // from class: com.sun.java.help.search.RAFFileFactoryOn12.1
                RAFFile back = null;
                private final URLConnection val$connection;
                private final Permission val$permission;

                {
                    this.val$connection = uRLConnection;
                    this.val$permission = permission;
                }

                /* JADX WARN: Code duplicated, block: B:19:0x007d  */
                /* JADX WARN: Code duplicated, block: B:22:0x0085  */
                /* JADX WARN: Code restructure failed: missing block: B:16:0x0076, code lost:
                
                    throw r12;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:28:0x007d, code lost:
                
                    r7.close();
                 */
                /* JADX WARN: Code restructure failed: missing block: B:30:0x0082, code lost:
                
                    if (0 == 0) goto L23;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:31:0x0085, code lost:
                
                    r8.close();
                 */
                @Override // java.security.PrivilegedExceptionAction
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public java.lang.Object run() throws java.io.IOException {
                    /*
                        r6 = this;
                        r0 = 0
                        r7 = r0
                        r0 = 0
                        r8 = r0
                        java.lang.String r0 = "dict_cache"
                        r1 = 0
                        java.io.File r0 = java.io.File.createTempFile(r0, r1)     // Catch: java.lang.Throwable -> L6f
                        r9 = r0
                        r0 = r9
                        r0.deleteOnExit()     // Catch: java.lang.Throwable -> L6f
                        r0 = r9
                        if (r0 == 0) goto L5a
                        r0 = r6
                        java.net.URLConnection r0 = r0.val$connection     // Catch: java.lang.Throwable -> L6f
                        java.io.InputStream r0 = r0.getInputStream()     // Catch: java.lang.Throwable -> L6f
                        r7 = r0
                        java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L6f
                        r1 = r0
                        r2 = r9
                        r1.<init>(r2)     // Catch: java.lang.Throwable -> L6f
                        r8 = r0
                        r0 = 0
                        r10 = r0
                        int r0 = com.sun.java.help.search.RAFFileFactoryOn12.access$000()     // Catch: java.lang.Throwable -> L6f
                        byte[] r0 = new byte[r0]     // Catch: java.lang.Throwable -> L6f
                        r11 = r0
                    L2e:
                        r0 = r7
                        r1 = r11
                        int r0 = r0.read(r1)     // Catch: java.lang.Throwable -> L6f
                        r1 = r0
                        r10 = r1
                        r1 = -1
                        if (r0 == r1) goto L47
                        r0 = r8
                        r1 = r11
                        r2 = 0
                        r3 = r10
                        r0.write(r1, r2, r3)     // Catch: java.lang.Throwable -> L6f
                        goto L2e
                    L47:
                        r0 = r6
                        com.sun.java.help.search.TemporaryRAFFile r1 = new com.sun.java.help.search.TemporaryRAFFile     // Catch: java.lang.Throwable -> L6f
                        r2 = r1
                        r3 = r9
                        r4 = r6
                        java.security.Permission r4 = r4.val$permission     // Catch: java.lang.Throwable -> L6f
                        r2.<init>(r3, r4)     // Catch: java.lang.Throwable -> L6f
                        r0.back = r1     // Catch: java.lang.Throwable -> L6f
                        goto L69
                    L5a:
                        r0 = r6
                        com.sun.java.help.search.MemoryRAFFile r1 = new com.sun.java.help.search.MemoryRAFFile     // Catch: java.lang.Throwable -> L6f
                        r2 = r1
                        r3 = r6
                        java.net.URLConnection r3 = r3.val$connection     // Catch: java.lang.Throwable -> L6f
                        r2.<init>(r3)     // Catch: java.lang.Throwable -> L6f
                        r0.back = r1     // Catch: java.lang.Throwable -> L6f
                    L69:
                        r0 = jsr -> L77
                    L6c:
                        goto L8b
                    L6f:
                        r12 = move-exception
                        r0 = jsr -> L77
                    L74:
                        r1 = r12
                        throw r1
                    L77:
                        r13 = r0
                        r0 = r7
                        if (r0 == 0) goto L81
                        r0 = r7
                        r0.close()
                    L81:
                        r0 = r8
                        if (r0 == 0) goto L89
                        r0 = r8
                        r0.close()
                    L89:
                        ret r13
                    L8b:
                        r1 = r6
                        com.sun.java.help.search.RAFFile r1 = r1.back
                        return r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.sun.java.help.search.RAFFileFactoryOn12.AnonymousClass1.run():java.lang.Object");
                }
            });
        } catch (SecurityException e) {
            memoryRAFFile = new MemoryRAFFile(uRLConnection);
        } catch (PrivilegedActionException e2) {
            memoryRAFFile = new MemoryRAFFile(uRLConnection);
        }
        return memoryRAFFile;
    }

    private static void debug(String str) {
    }
}
