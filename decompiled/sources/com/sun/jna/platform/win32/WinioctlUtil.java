package com.sun.jna.platform.win32;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinioctlUtil.class */
public abstract class WinioctlUtil {
    public static final int FSCTL_GET_COMPRESSION = CTL_CODE(9, 15, 0, 0);
    public static final int FSCTL_SET_COMPRESSION = CTL_CODE(9, 16, 0, 3);
    public static final int FSCTL_SET_REPARSE_POINT = CTL_CODE(9, 41, 0, 0);
    public static final int FSCTL_GET_REPARSE_POINT = CTL_CODE(9, 42, 0, 0);
    public static final int FSCTL_DELETE_REPARSE_POINT = CTL_CODE(9, 43, 0, 0);

    public static int CTL_CODE(int DeviceType, int Function, int Method, int Access) {
        return (DeviceType << 16) | (Access << 14) | (Function << 2) | Method;
    }
}
