package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APITypeMapper;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT.class */
public interface DBT {
    public static final int DBT_NO_DISK_SPACE = 71;
    public static final int DBT_LOW_DISK_SPACE = 72;
    public static final int DBT_CONFIGMGPRIVATE = 32767;
    public static final int DBT_DEVICEARRIVAL = 32768;
    public static final int DBT_DEVICEQUERYREMOVE = 32769;
    public static final int DBT_DEVICEQUERYREMOVEFAILED = 32770;
    public static final int DBT_DEVICEREMOVEPENDING = 32771;
    public static final int DBT_DEVICEREMOVECOMPLETE = 32772;
    public static final int DBT_DEVNODES_CHANGED = 7;
    public static final int DBT_DEVICETYPESPECIFIC = 32773;
    public static final int DBT_CUSTOMEVENT = 32774;
    public static final Guid.GUID GUID_DEVINTERFACE_USB_DEVICE = new Guid.GUID("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
    public static final Guid.GUID GUID_DEVINTERFACE_HID = new Guid.GUID("{4D1E55B2-F16F-11CF-88CB-001111000030}");
    public static final Guid.GUID GUID_DEVINTERFACE_VOLUME = new Guid.GUID("{53F5630D-B6BF-11D0-94F2-00A0C91EFB8B}");
    public static final Guid.GUID GUID_DEVINTERFACE_KEYBOARD = new Guid.GUID("{884b96c3-56ef-11d1-bc8c-00a0c91405dd}");
    public static final Guid.GUID GUID_DEVINTERFACE_MOUSE = new Guid.GUID("{378DE44C-56EF-11D1-BC8C-00A0C91405DD}");
    public static final int DBT_DEVTYP_OEM = 0;
    public static final int DBT_DEVTYP_DEVNODE = 1;
    public static final int DBT_DEVTYP_VOLUME = 2;
    public static final int DBT_DEVTYP_PORT = 3;
    public static final int DBT_DEVTYP_NET = 4;
    public static final int DBT_DEVTYP_DEVICEINTERFACE = 5;
    public static final int DBT_DEVTYP_HANDLE = 6;
    public static final int DBTF_MEDIA = 1;
    public static final int DBTF_NET = 2;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_HDR.class */
    @Structure.FieldOrder({"dbch_size", "dbch_devicetype", "dbch_reserved"})
    public static class DEV_BROADCAST_HDR extends Structure {
        public int dbch_size;
        public int dbch_devicetype;
        public int dbch_reserved;

        public DEV_BROADCAST_HDR() {
        }

        public DEV_BROADCAST_HDR(long pointer) {
            this(new Pointer(pointer));
        }

        public DEV_BROADCAST_HDR(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_OEM.class */
    @Structure.FieldOrder({"dbco_size", "dbco_devicetype", "dbco_reserved", "dbco_identifier", "dbco_suppfunc"})
    public static class DEV_BROADCAST_OEM extends Structure {
        public int dbco_size;
        public int dbco_devicetype;
        public int dbco_reserved;
        public int dbco_identifier;
        public int dbco_suppfunc;

        public DEV_BROADCAST_OEM() {
        }

        public DEV_BROADCAST_OEM(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_DEVNODE.class */
    @Structure.FieldOrder({"dbcd_size", "dbcd_devicetype", "dbcd_reserved", "dbcd_devnode"})
    public static class DEV_BROADCAST_DEVNODE extends Structure {
        public int dbcd_size;
        public int dbcd_devicetype;
        public int dbcd_reserved;
        public int dbcd_devnode;

        public DEV_BROADCAST_DEVNODE() {
        }

        public DEV_BROADCAST_DEVNODE(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_VOLUME.class */
    @Structure.FieldOrder({"dbcv_size", "dbcv_devicetype", "dbcv_reserved", "dbcv_unitmask", "dbcv_flags"})
    public static class DEV_BROADCAST_VOLUME extends Structure {
        public int dbcv_size;
        public int dbcv_devicetype;
        public int dbcv_reserved;
        public int dbcv_unitmask;
        public short dbcv_flags;

        public DEV_BROADCAST_VOLUME() {
        }

        public DEV_BROADCAST_VOLUME(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_PORT.class */
    @Structure.FieldOrder({"dbcp_size", "dbcp_devicetype", "dbcp_reserved", "dbcp_name"})
    public static class DEV_BROADCAST_PORT extends Structure {
        public int dbcp_size;
        public int dbcp_devicetype;
        public int dbcp_reserved;
        public byte[] dbcp_name;

        public DEV_BROADCAST_PORT() {
            this.dbcp_name = new byte[1];
        }

        public DEV_BROADCAST_PORT(Pointer memory) {
            super(memory);
            this.dbcp_name = new byte[1];
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            int size = getPointer().getInt(0L);
            this.dbcp_name = new byte[size - fieldOffset("dbcp_name")];
            super.read();
        }

        public String getDbcpName() {
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.ASCII) {
                return Native.toString(this.dbcp_name);
            }
            return new String(this.dbcp_name, StandardCharsets.UTF_16LE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_NET.class */
    @Structure.FieldOrder({"dbcn_size", "dbcn_devicetype", "dbcn_reserved", "dbcn_resource", "dbcn_flags"})
    public static class DEV_BROADCAST_NET extends Structure {
        public int dbcn_size;
        public int dbcn_devicetype;
        public int dbcn_reserved;
        public int dbcn_resource;
        public int dbcn_flags;

        public DEV_BROADCAST_NET() {
        }

        public DEV_BROADCAST_NET(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_DEVICEINTERFACE.class */
    @Structure.FieldOrder({"dbcc_size", "dbcc_devicetype", "dbcc_reserved", "dbcc_classguid", "dbcc_name"})
    public static class DEV_BROADCAST_DEVICEINTERFACE extends Structure {
        public int dbcc_size;
        public int dbcc_devicetype;
        public int dbcc_reserved;
        public Guid.GUID dbcc_classguid;
        public char[] dbcc_name;

        public DEV_BROADCAST_DEVICEINTERFACE() {
            this.dbcc_name = new char[1];
        }

        public DEV_BROADCAST_DEVICEINTERFACE(long pointer) {
            this(new Pointer(pointer));
        }

        public DEV_BROADCAST_DEVICEINTERFACE(Pointer memory) {
            super(memory);
            this.dbcc_name = new char[1];
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.ASCII) {
                Logger.getLogger(DBT.class.getName()).warning("DEV_BROADCAST_DEVICEINTERFACE must not be used with w32.ascii = true!");
            }
            int size = getPointer().getInt(0L);
            int len = (size - fieldOffset("dbcc_name")) / Native.WCHAR_SIZE;
            this.dbcc_name = new char[len];
            super.read();
        }

        public String getDbcc_name() {
            return Native.toString(this.dbcc_name);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DBT$DEV_BROADCAST_HANDLE.class */
    @Structure.FieldOrder({"dbch_size", "dbch_devicetype", "dbch_reserved", "dbch_handle", "dbch_hdevnotify", "dbch_eventguid", "dbch_nameoffset", "dbch_data"})
    public static class DEV_BROADCAST_HANDLE extends Structure {
        public int dbch_size;
        public int dbch_devicetype;
        public int dbch_reserved;
        public WinNT.HANDLE dbch_handle;
        public WinUser.HDEVNOTIFY dbch_hdevnotify;
        public Guid.GUID dbch_eventguid;
        public WinDef.LONG dbch_nameoffset;
        public byte[] dbch_data;

        public DEV_BROADCAST_HANDLE() {
            this.dbch_data = new byte[1];
        }

        public DEV_BROADCAST_HANDLE(Pointer memory) {
            super(memory);
            this.dbch_data = new byte[1];
            read();
        }
    }
}
