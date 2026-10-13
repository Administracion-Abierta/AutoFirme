package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winsock2.class */
public interface Winsock2 extends Library {
    public static final Winsock2 INSTANCE = (Winsock2) Native.load("ws2_32", Winsock2.class, W32APIOptions.ASCII_OPTIONS);

    int gethostname(byte[] bArr, int i);
}
