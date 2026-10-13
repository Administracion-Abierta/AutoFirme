package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IMoniker.class */
public interface IMoniker extends IPersistStream {
    void BindToObject();

    void BindToStorage();

    void Reduce();

    void ComposeWith();

    void Enum();

    void IsEqual();

    void Hash();

    void IsRunning();

    void GetTimeOfLastChange();

    void Inverse();

    void CommonPrefixWith();

    String GetDisplayName(Pointer pointer, Pointer pointer2);

    void ParseDisplayName();

    void IsSystemMoniker();

    void RelativePathTo();
}
