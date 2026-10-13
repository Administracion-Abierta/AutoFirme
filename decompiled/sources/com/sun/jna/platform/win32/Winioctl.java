package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winioctl.class */
public interface Winioctl {
    public static final int FILE_DEVICE_BEEP = 1;
    public static final int FILE_DEVICE_CD_ROM = 2;
    public static final int FILE_DEVICE_CD_ROM_FILE_SYSTEM = 3;
    public static final int FILE_DEVICE_CONTROLLER = 4;
    public static final int FILE_DEVICE_DATALINK = 5;
    public static final int FILE_DEVICE_DFS = 6;
    public static final int FILE_DEVICE_DISK = 7;
    public static final int FILE_DEVICE_DISK_FILE_SYSTEM = 8;
    public static final int FILE_DEVICE_FILE_SYSTEM = 9;
    public static final int FILE_DEVICE_INPORT_PORT = 10;
    public static final int FILE_DEVICE_KEYBOARD = 11;
    public static final int FILE_DEVICE_MAILSLOT = 12;
    public static final int FILE_DEVICE_MIDI_IN = 13;
    public static final int FILE_DEVICE_MIDI_OUT = 14;
    public static final int FILE_DEVICE_MOUSE = 15;
    public static final int FILE_DEVICE_MULTI_UNC_PROVIDER = 16;
    public static final int FILE_DEVICE_NAMED_PIPE = 17;
    public static final int FILE_DEVICE_NETWORK = 18;
    public static final int FILE_DEVICE_NETWORK_BROWSER = 19;
    public static final int FILE_DEVICE_NETWORK_FILE_SYSTEM = 20;
    public static final int FILE_DEVICE_NULL = 21;
    public static final int FILE_DEVICE_PARALLEL_PORT = 22;
    public static final int FILE_DEVICE_PHYSICAL_NETCARD = 23;
    public static final int FILE_DEVICE_PRINTER = 24;
    public static final int FILE_DEVICE_SCANNER = 25;
    public static final int FILE_DEVICE_SERIAL_MOUSE_PORT = 26;
    public static final int FILE_DEVICE_SERIAL_PORT = 27;
    public static final int FILE_DEVICE_SCREEN = 28;
    public static final int FILE_DEVICE_SOUND = 29;
    public static final int FILE_DEVICE_STREAMS = 30;
    public static final int FILE_DEVICE_TAPE = 31;
    public static final int FILE_DEVICE_TAPE_FILE_SYSTEM = 32;
    public static final int FILE_DEVICE_TRANSPORT = 33;
    public static final int FILE_DEVICE_UNKNOWN = 34;
    public static final int FILE_DEVICE_VIDEO = 35;
    public static final int FILE_DEVICE_VIRTUAL_DISK = 36;
    public static final int FILE_DEVICE_WAVE_IN = 37;
    public static final int FILE_DEVICE_WAVE_OUT = 38;
    public static final int FILE_DEVICE_8042_PORT = 39;
    public static final int FILE_DEVICE_NETWORK_REDIRECTOR = 40;
    public static final int FILE_DEVICE_BATTERY = 41;
    public static final int FILE_DEVICE_BUS_EXTENDER = 42;
    public static final int FILE_DEVICE_MODEM = 43;
    public static final int FILE_DEVICE_VDM = 44;
    public static final int FILE_DEVICE_MASS_STORAGE = 45;
    public static final int FILE_DEVICE_SMB = 46;
    public static final int FILE_DEVICE_KS = 47;
    public static final int FILE_DEVICE_CHANGER = 48;
    public static final int FILE_DEVICE_SMARTCARD = 49;
    public static final int FILE_DEVICE_ACPI = 50;
    public static final int FILE_DEVICE_DVD = 51;
    public static final int FILE_DEVICE_FULLSCREEN_VIDEO = 52;
    public static final int FILE_DEVICE_DFS_FILE_SYSTEM = 53;
    public static final int FILE_DEVICE_DFS_VOLUME = 54;
    public static final int FILE_DEVICE_SERENUM = 55;
    public static final int FILE_DEVICE_TERMSRV = 56;
    public static final int FILE_DEVICE_KSEC = 57;
    public static final int FILE_DEVICE_FIPS = 58;
    public static final int FILE_DEVICE_INFINIBAND = 59;
    public static final int FILE_DEVICE_VMBUS = 62;
    public static final int FILE_DEVICE_CRYPT_PROVIDER = 63;
    public static final int FILE_DEVICE_WPD = 64;
    public static final int FILE_DEVICE_BLUETOOTH = 65;
    public static final int FILE_DEVICE_MT_COMPOSITE = 66;
    public static final int FILE_DEVICE_MT_TRANSPORT = 67;
    public static final int FILE_DEVICE_BIOMETRIC = 68;
    public static final int FILE_DEVICE_PMI = 69;
    public static final int FILE_DEVICE_EHSTOR = 70;
    public static final int FILE_DEVICE_DEVAPI = 71;
    public static final int FILE_DEVICE_GPIO = 72;
    public static final int FILE_DEVICE_USBEX = 73;
    public static final int FILE_DEVICE_CONSOLE = 80;
    public static final int FILE_DEVICE_NFP = 81;
    public static final int FILE_DEVICE_SYSENV = 82;
    public static final int FILE_DEVICE_VIRTUAL_BLOCK = 83;
    public static final int FILE_DEVICE_POINT_OF_SERVICE = 84;
    public static final int FSCTL_GET_COMPRESSION = 15;
    public static final int FSCTL_SET_COMPRESSION = 16;
    public static final int FSCTL_SET_REPARSE_POINT = 41;
    public static final int FSCTL_GET_REPARSE_POINT = 42;
    public static final int FSCTL_DELETE_REPARSE_POINT = 43;
    public static final int METHOD_BUFFERED = 0;
    public static final int METHOD_IN_DIRECT = 1;
    public static final int METHOD_OUT_DIRECT = 2;
    public static final int METHOD_NEITHER = 3;
    public static final int FILE_ANY_ACCESS = 0;
    public static final int FILE_SPECIAL_ACCESS = 0;
    public static final int FILE_READ_ACCESS = 1;
    public static final int FILE_WRITE_ACCESS = 2;
    public static final int IOCTL_STORAGE_GET_DEVICE_NUMBER = 2953344;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winioctl$STORAGE_DEVICE_NUMBER.class */
    @Structure.FieldOrder({"DeviceType", "DeviceNumber", "PartitionNumber"})
    public static class STORAGE_DEVICE_NUMBER extends Structure {
        public int DeviceType;
        public int DeviceNumber;
        public int PartitionNumber;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winioctl$STORAGE_DEVICE_NUMBER$ByReference.class */
        public static class ByReference extends STORAGE_DEVICE_NUMBER implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public STORAGE_DEVICE_NUMBER() {
        }

        public STORAGE_DEVICE_NUMBER(Pointer memory) {
            super(memory);
            read();
        }
    }
}
