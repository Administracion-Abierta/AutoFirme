package com.sun.jna.platform.unix;

import com.sun.jna.Library;
import com.sun.jna.Native;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/LibC.class */
public interface LibC extends LibCAPI, Library {
    public static final String NAME = "c";
    public static final LibC INSTANCE = (LibC) Native.load("c", LibC.class);
}
