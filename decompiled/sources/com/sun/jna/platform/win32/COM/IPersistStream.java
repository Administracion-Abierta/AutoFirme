package com.sun.jna.platform.win32.COM;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IPersistStream.class */
public interface IPersistStream extends IPersist {
    boolean IsDirty();

    void Load(IStream iStream);

    void Save(IStream iStream);

    void GetSizeMax();
}
