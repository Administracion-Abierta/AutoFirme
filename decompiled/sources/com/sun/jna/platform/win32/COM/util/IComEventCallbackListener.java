package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.IDispatchCallback;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/IComEventCallbackListener.class */
public interface IComEventCallbackListener {
    void setDispatchCallbackListener(IDispatchCallback iDispatchCallback);

    void errorReceivingCallbackEvent(String str, Exception exc);
}
