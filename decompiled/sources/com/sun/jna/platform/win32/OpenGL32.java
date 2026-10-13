package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OpenGL32.class */
public interface OpenGL32 extends StdCallLibrary {
    public static final OpenGL32 INSTANCE = (OpenGL32) Native.load("opengl32", OpenGL32.class);

    String glGetString(int i);

    WinDef.HGLRC wglCreateContext(WinDef.HDC hdc);

    WinDef.HGLRC wglGetCurrentContext();

    boolean wglMakeCurrent(WinDef.HDC hdc, WinDef.HGLRC hglrc);

    boolean wglDeleteContext(WinDef.HGLRC hglrc);

    Pointer wglGetProcAddress(String str);
}
