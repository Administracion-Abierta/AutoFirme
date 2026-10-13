package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IPersist.class */
public interface IPersist extends IUnknown {
    Guid.CLSID GetClassID();
}
