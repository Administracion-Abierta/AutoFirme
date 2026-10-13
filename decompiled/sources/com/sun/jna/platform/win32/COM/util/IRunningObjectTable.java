package com.sun.jna.platform.win32.COM.util;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/IRunningObjectTable.class */
public interface IRunningObjectTable {
    Iterable<IDispatch> enumRunning();

    <T> List<T> getActiveObjectsByInterface(Class<T> cls);
}
