package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SetupApi.class */
public interface SetupApi extends StdCallLibrary {
    public static final SetupApi INSTANCE = (SetupApi) Native.load("setupapi", SetupApi.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final Guid.GUID GUID_DEVINTERFACE_DISK = new Guid.GUID("53F56307-B6BF-11D0-94F2-00A0C91EFB8B");
    public static final Guid.GUID GUID_DEVINTERFACE_COMPORT = new Guid.GUID("86E0D1E0-8089-11D0-9CE4-08003E301F73");
    public static final int DIGCF_DEFAULT = 1;
    public static final int DIGCF_PRESENT = 2;
    public static final int DIGCF_ALLCLASSES = 4;
    public static final int DIGCF_PROFILE = 8;
    public static final int DIGCF_DEVICEINTERFACE = 16;
    public static final int SPDRP_REMOVAL_POLICY = 31;
    public static final int CM_DEVCAP_REMOVABLE = 4;
    public static final int DICS_FLAG_GLOBAL = 1;
    public static final int DICS_FLAG_CONFIGSPECIFIC = 2;
    public static final int DICS_FLAG_CONFIGGENERAL = 4;
    public static final int DIREG_DEV = 1;
    public static final int DIREG_DRV = 2;
    public static final int DIREG_BOTH = 4;
    public static final int SPDRP_DEVICEDESC = 0;

    WinNT.HANDLE SetupDiGetClassDevs(Guid.GUID guid, Pointer pointer, Pointer pointer2, int i);

    boolean SetupDiDestroyDeviceInfoList(WinNT.HANDLE handle);

    boolean SetupDiEnumDeviceInterfaces(WinNT.HANDLE handle, Pointer pointer, Guid.GUID guid, int i, SP_DEVICE_INTERFACE_DATA sp_device_interface_data);

    boolean SetupDiGetDeviceInterfaceDetail(WinNT.HANDLE handle, SP_DEVICE_INTERFACE_DATA sp_device_interface_data, Pointer pointer, int i, IntByReference intByReference, SP_DEVINFO_DATA sp_devinfo_data);

    boolean SetupDiGetDeviceRegistryProperty(WinNT.HANDLE handle, SP_DEVINFO_DATA sp_devinfo_data, int i, IntByReference intByReference, Pointer pointer, int i2, IntByReference intByReference2);

    WinReg.HKEY SetupDiOpenDevRegKey(WinNT.HANDLE handle, SP_DEVINFO_DATA sp_devinfo_data, int i, int i2, int i3, int i4);

    boolean SetupDiEnumDeviceInfo(WinNT.HANDLE handle, int i, SP_DEVINFO_DATA sp_devinfo_data);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SetupApi$SP_DEVICE_INTERFACE_DATA.class */
    @Structure.FieldOrder({"cbSize", "InterfaceClassGuid", "Flags", "Reserved"})
    public static class SP_DEVICE_INTERFACE_DATA extends Structure {
        public int cbSize;
        public Guid.GUID InterfaceClassGuid;
        public int Flags;
        public Pointer Reserved;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SetupApi$SP_DEVICE_INTERFACE_DATA$ByReference.class */
        public static class ByReference extends SP_DEVINFO_DATA implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public SP_DEVICE_INTERFACE_DATA() {
            this.cbSize = size();
        }

        public SP_DEVICE_INTERFACE_DATA(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SetupApi$SP_DEVINFO_DATA.class */
    @Structure.FieldOrder({"cbSize", "InterfaceClassGuid", "DevInst", "Reserved"})
    public static class SP_DEVINFO_DATA extends Structure {
        public int cbSize;
        public Guid.GUID InterfaceClassGuid;
        public int DevInst;
        public Pointer Reserved;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SetupApi$SP_DEVINFO_DATA$ByReference.class */
        public static class ByReference extends SP_DEVINFO_DATA implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public SP_DEVINFO_DATA() {
            this.cbSize = size();
        }

        public SP_DEVINFO_DATA(Pointer memory) {
            super(memory);
            read();
        }
    }
}
