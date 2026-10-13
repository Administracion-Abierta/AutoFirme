package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.IDispatchCallback;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/AbstractComEventCallbackListener.class */
public abstract class AbstractComEventCallbackListener implements IComEventCallbackListener {
    IDispatchCallback dispatchCallback = null;

    @Override // com.sun.jna.platform.win32.COM.util.IComEventCallbackListener
    public void setDispatchCallbackListener(IDispatchCallback dispatchCallback) {
        this.dispatchCallback = dispatchCallback;
    }
}
