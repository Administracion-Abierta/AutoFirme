package com.sun.jna.platform.unix;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/Reboot.class */
public interface Reboot {
    public static final int RB_AUTOBOOT = 19088743;
    public static final int RB_HALT_SYSTEM = -839974621;
    public static final int RB_ENABLE_CAD = -1985229329;
    public static final int RB_DISABLE_CAD = 0;
    public static final int RB_POWER_OFF = 1126301404;
    public static final int RB_SW_SUSPEND = -805241630;
    public static final int RB_KEXEC = 1163412803;

    int reboot(int i);
}
