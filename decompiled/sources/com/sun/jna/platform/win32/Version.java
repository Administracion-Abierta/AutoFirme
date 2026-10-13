package com.sun.jna.platform.win32;

import com.ctc.wstx.cfg.XmlConsts;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Version.class */
public interface Version extends StdCallLibrary {
    public static final Version INSTANCE = (Version) Native.load(XmlConsts.XML_DECL_KW_VERSION, Version.class, W32APIOptions.DEFAULT_OPTIONS);

    int GetFileVersionInfoSize(String str, IntByReference intByReference);

    boolean GetFileVersionInfo(String str, int i, int i2, Pointer pointer);

    boolean VerQueryValue(Pointer pointer, String str, PointerByReference pointerByReference, IntByReference intByReference);
}
