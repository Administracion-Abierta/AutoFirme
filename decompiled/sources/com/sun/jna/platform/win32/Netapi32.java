package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32.class */
public interface Netapi32 extends StdCallLibrary {
    public static final Netapi32 INSTANCE = (Netapi32) Native.load("Netapi32", Netapi32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int MAX_PREFERRED_LENGTH = -1;

    int NetSessionEnum(WString wString, WString wString2, WString wString3, int i, PointerByReference pointerByReference, int i2, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    int NetGetJoinInformation(String str, PointerByReference pointerByReference, IntByReference intByReference);

    int NetApiBufferFree(Pointer pointer);

    int NetLocalGroupEnum(String str, int i, PointerByReference pointerByReference, int i2, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    int NetGetDCName(String str, String str2, PointerByReference pointerByReference);

    int NetGroupEnum(String str, int i, PointerByReference pointerByReference, int i2, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    int NetUserEnum(String str, int i, int i2, PointerByReference pointerByReference, int i3, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    int NetUserGetGroups(String str, String str2, int i, PointerByReference pointerByReference, int i2, IntByReference intByReference, IntByReference intByReference2);

    int NetUserGetLocalGroups(String str, String str2, int i, int i2, PointerByReference pointerByReference, int i3, IntByReference intByReference, IntByReference intByReference2);

    int NetUserAdd(String str, int i, Structure structure, IntByReference intByReference);

    int NetUserDel(String str, String str2);

    int NetUserChangePassword(String str, String str2, String str3, String str4);

    int DsGetDcName(String str, String str2, Guid.GUID guid, String str3, int i, DsGetDC.PDOMAIN_CONTROLLER_INFO pdomain_controller_info);

    int DsGetForestTrustInformation(String str, String str2, int i, NTSecApi.PLSA_FOREST_TRUST_INFORMATION plsa_forest_trust_information);

    int DsEnumerateDomainTrusts(String str, int i, PointerByReference pointerByReference, IntByReference intByReference);

    int NetUserGetInfo(String str, String str2, int i, PointerByReference pointerByReference);

    int NetShareAdd(String str, int i, Pointer pointer, IntByReference intByReference);

    int NetShareDel(String str, String str2, int i);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32$SESSION_INFO_10.class */
    @Structure.FieldOrder({"sesi10_cname", "sesi10_username", "sesi10_time", "sesi10_idle_time"})
    public static class SESSION_INFO_10 extends Structure {
        public String sesi10_cname;
        public String sesi10_username;
        public int sesi10_time;
        public int sesi10_idle_time;

        public SESSION_INFO_10() {
            super(W32APITypeMapper.UNICODE);
        }

        public SESSION_INFO_10(Pointer p) {
            super(p, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }
}
