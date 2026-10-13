package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Variant;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IEnumVariant.class */
public interface IEnumVariant extends IUnknown {
    IEnumVariant Clone();

    Variant.VARIANT[] Next(int i);

    void Reset();

    void Skip(int i);
}
