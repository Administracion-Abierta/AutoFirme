package com.sun.jna.platform.win32;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMJoin.class */
public interface LMJoin {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMJoin$NETSETUP_JOIN_STATUS.class */
    public static abstract class NETSETUP_JOIN_STATUS {
        public static final int NetSetupUnknownStatus = 0;
        public static final int NetSetupUnjoined = 1;
        public static final int NetSetupWorkgroupName = 2;
        public static final int NetSetupDomainName = 3;
    }
}
