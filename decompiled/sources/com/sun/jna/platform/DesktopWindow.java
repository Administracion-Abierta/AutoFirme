package com.sun.jna.platform;

import com.sun.jna.platform.win32.WinDef;
import java.awt.Rectangle;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/DesktopWindow.class */
public class DesktopWindow {
    private WinDef.HWND hwnd;
    private String title;
    private String filePath;
    private Rectangle locAndSize;

    public DesktopWindow(WinDef.HWND hwnd, String title, String filePath, Rectangle locAndSize) {
        this.hwnd = hwnd;
        this.title = title;
        this.filePath = filePath;
        this.locAndSize = locAndSize;
    }

    public WinDef.HWND getHWND() {
        return this.hwnd;
    }

    public String getTitle() {
        return this.title;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Rectangle getLocAndSize() {
        return this.locAndSize;
    }
}
