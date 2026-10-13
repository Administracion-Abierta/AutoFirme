package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Win32Exception.class */
public class Win32Exception extends LastErrorException {
    private static final long serialVersionUID = 1;
    private WinNT.HRESULT _hr;
    private static Method addSuppressedMethod;

    public WinNT.HRESULT getHR() {
        return this._hr;
    }

    public Win32Exception(int code) {
        this(code, W32Errors.HRESULT_FROM_WIN32(code));
    }

    public Win32Exception(WinNT.HRESULT hr) {
        this(W32Errors.HRESULT_CODE(hr.intValue()), hr);
    }

    protected Win32Exception(int code, WinNT.HRESULT hr) {
        this(code, hr, Kernel32Util.formatMessage(hr));
    }

    protected Win32Exception(int code, WinNT.HRESULT hr, String msg) {
        super(code, msg);
        this._hr = hr;
    }

    static {
        addSuppressedMethod = null;
        try {
            addSuppressedMethod = Throwable.class.getMethod("addSuppressed", Throwable.class);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException ex) {
            Logger.getLogger(Win32Exception.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", (Throwable) ex);
        }
    }

    void addSuppressedReflected(Throwable exception) {
        if (addSuppressedMethod == null) {
            return;
        }
        try {
            addSuppressedMethod.invoke(this, exception);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex);
        } catch (IllegalArgumentException ex2) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex2);
        } catch (InvocationTargetException ex3) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex3);
        }
    }
}
