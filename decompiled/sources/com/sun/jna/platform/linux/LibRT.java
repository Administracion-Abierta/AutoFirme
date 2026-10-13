package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/LibRT.class */
public interface LibRT extends Library {
    public static final LibRT INSTANCE = (LibRT) Native.load("rt", LibRT.class);

    int shm_open(String str, int i, int i2);

    int shm_unlink(String str);
}
