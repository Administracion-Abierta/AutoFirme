package com.ctc.wstx.shaded.msv_core.driver.textui;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/driver/textui/Debug.class */
public class Debug {
    public static boolean debug;

    static {
        try {
            debug = System.getProperty("com.ctc.wstx.shaded.msv_core.debug") != null;
        } catch (SecurityException e) {
            debug = false;
        }
    }
}
