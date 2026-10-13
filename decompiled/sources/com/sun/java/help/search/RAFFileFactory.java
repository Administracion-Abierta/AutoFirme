package com.sun.java.help.search;

import com.sun.jna.platform.win32.WinError;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/RAFFileFactory.class */
class RAFFileFactory {
    static RAFFileFactory theFactory;
    private int memoryCacheLimit = WinError.WSABASEERR;
    private static final boolean debug = false;
    static Class class$java$net$URLConnection;

    private RAFFileFactory() {
    }

    public static synchronized RAFFileFactory create() {
        if (theFactory == null) {
            theFactory = new RAFFileFactory();
        }
        return theFactory;
    }

    private boolean isFileURL(URL url) {
        return url.getProtocol().equalsIgnoreCase("file");
    }

    public int getMemoryCacheLimit() {
        return this.memoryCacheLimit;
    }

    public void setMemoryCacheLimit(int i) {
        this.memoryCacheLimit = i;
    }

    public synchronized RAFFile get(URL url, boolean z) throws IOException {
        RAFFile rAFFile = null;
        if (isFileURL(url)) {
            try {
                String strURLDecoder = Utilities.URLDecoder(url.getFile());
                rAFFile = new RAFFile(strURLDecoder, z);
                debug(new StringBuffer().append("Opened Dict file with file protocol:").append(strURLDecoder).toString());
            } catch (SecurityException e) {
            }
        }
        if (rAFFile == null) {
            rAFFile = createLocalRAFFile(url);
        }
        if (rAFFile == null) {
            throw new FileNotFoundException(url.toString());
        }
        return rAFFile;
    }

    private static RAFFile createLocalRAFFile(URL url) throws IOException {
        Class clsClass$;
        RAFFile memoryRAFFile = null;
        URLConnection uRLConnectionOpenConnection = url.openConnection();
        try {
            Class<?>[] clsArr = new Class[0];
            if (class$java$net$URLConnection == null) {
                clsClass$ = class$("java.net.URLConnection");
                class$java$net$URLConnection = clsClass$;
            } else {
                clsClass$ = class$java$net$URLConnection;
            }
            clsClass$.getMethod("getPermission", clsArr);
            memoryRAFFile = RAFFileFactoryOn12.get(uRLConnectionOpenConnection);
        } catch (NoSuchMethodError e) {
        } catch (NoSuchMethodException e2) {
        }
        if (memoryRAFFile == null) {
            memoryRAFFile = new MemoryRAFFile(uRLConnectionOpenConnection);
            debug("Opening a Dict file in Memory");
        }
        return memoryRAFFile;
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    private static void debug(String str) {
    }
}
