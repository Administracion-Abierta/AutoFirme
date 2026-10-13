package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/GDI32Util.class */
public class GDI32Util {
    private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, Winspool.PRINTER_ENUM_ICONMASK, 65280, 255);
    private static final int[] SCREENSHOT_BAND_MASKS = {SCREENSHOT_COLOR_MODEL.getRedMask(), SCREENSHOT_COLOR_MODEL.getGreenMask(), SCREENSHOT_COLOR_MODEL.getBlueMask()};

    public static BufferedImage getScreenshot(WinDef.HWND target) {
        WinNT.HANDLE result;
        WinNT.HANDLE result2;
        WinNT.HANDLE result3;
        WinDef.RECT rect = new WinDef.RECT();
        if (!User32.INSTANCE.GetWindowRect(target, rect)) {
            throw new Win32Exception(Native.getLastError());
        }
        Rectangle jRectangle = rect.toRectangle();
        int windowWidth = jRectangle.width;
        int windowHeight = jRectangle.height;
        if (windowWidth == 0 || windowHeight == 0) {
            throw new IllegalStateException("Window width and/or height were 0 even though GetWindowRect did not appear to fail.");
        }
        WinDef.HDC hdcTarget = User32.INSTANCE.GetDC(target);
        if (hdcTarget == null) {
            throw new Win32Exception(Native.getLastError());
        }
        Win32Exception we = null;
        BufferedImage image = null;
        try {
            WinDef.HDC hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
            if (hdcTargetMem == null) {
                throw new Win32Exception(Native.getLastError());
            }
            WinDef.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, windowWidth, windowHeight);
            if (hBitmap == null) {
                throw new Win32Exception(Native.getLastError());
            }
            WinNT.HANDLE hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);
            if (hOriginal == null) {
                throw new Win32Exception(Native.getLastError());
            }
            if (!GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0, windowWidth, windowHeight, hdcTarget, 0, 0, GDI32.SRCCOPY)) {
                throw new Win32Exception(Native.getLastError());
            }
            WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
            bmi.bmiHeader.biWidth = windowWidth;
            bmi.bmiHeader.biHeight = -windowHeight;
            bmi.bmiHeader.biPlanes = (short) 1;
            bmi.bmiHeader.biBitCount = (short) 32;
            bmi.bmiHeader.biCompression = 0;
            Memory buffer = new Memory(windowWidth * windowHeight * 4);
            int resultOfDrawing = GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, windowHeight, buffer, bmi, 0);
            if (resultOfDrawing == 0 || resultOfDrawing == 87) {
                throw new Win32Exception(Native.getLastError());
            }
            int bufferSize = windowWidth * windowHeight;
            WritableRaster raster = Raster.createPackedRaster(new DataBufferInt(buffer.getIntArray(0L, bufferSize), bufferSize), windowWidth, windowHeight, windowWidth, SCREENSHOT_BAND_MASKS, (Point) null);
            image = new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, (Hashtable) null);
            if (hOriginal != null && ((result3 = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal)) == null || WinGDI.HGDI_ERROR.equals(result3))) {
                Win32Exception ex = new Win32Exception(Native.getLastError());
                if (0 != 0) {
                    ex.addSuppressedReflected(null);
                }
                we = ex;
            }
            if (hBitmap != null && !GDI32.INSTANCE.DeleteObject(hBitmap)) {
                Win32Exception ex2 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex2.addSuppressedReflected(we);
                }
                we = ex2;
            }
            if (hdcTargetMem != null && !GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
                Win32Exception ex3 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex3.addSuppressedReflected(we);
                }
                we = ex3;
            }
            if (hdcTarget != null && 0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
                throw new IllegalStateException("Device context did not release properly.");
            }
            if (we != null) {
                throw we;
            }
            return image;
        } catch (Win32Exception e) {
            we = e;
            if (0 != 0 && ((result2 = GDI32.INSTANCE.SelectObject(null, null)) == null || WinGDI.HGDI_ERROR.equals(result2))) {
                Win32Exception ex4 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex4.addSuppressedReflected(we);
                }
                we = ex4;
            }
            if (0 != 0 && !GDI32.INSTANCE.DeleteObject(null)) {
                Win32Exception ex5 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex5.addSuppressedReflected(we);
                }
                we = ex5;
            }
            if (0 != 0 && !GDI32.INSTANCE.DeleteDC(null)) {
                Win32Exception ex6 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex6.addSuppressedReflected(we);
                }
                we = ex6;
            }
            if (hdcTarget != null && 0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
                throw new IllegalStateException("Device context did not release properly.");
            }
        } catch (Throwable th) {
            if (0 != 0 && ((result = GDI32.INSTANCE.SelectObject(null, null)) == null || WinGDI.HGDI_ERROR.equals(result))) {
                Win32Exception ex7 = new Win32Exception(Native.getLastError());
                if (0 != 0) {
                    ex7.addSuppressedReflected(null);
                }
                we = ex7;
            }
            if (0 != 0 && !GDI32.INSTANCE.DeleteObject(null)) {
                Win32Exception ex8 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex8.addSuppressedReflected(we);
                }
                we = ex8;
            }
            if (0 != 0 && !GDI32.INSTANCE.DeleteDC(null)) {
                Win32Exception ex9 = new Win32Exception(Native.getLastError());
                if (we != null) {
                    ex9.addSuppressedReflected(we);
                }
            }
            if (hdcTarget != null && 0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
                throw new IllegalStateException("Device context did not release properly.");
            }
            throw th;
        }
    }
}
