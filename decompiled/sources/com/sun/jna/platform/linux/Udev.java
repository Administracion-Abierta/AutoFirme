package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Udev.class */
public interface Udev extends Library {
    public static final Udev INSTANCE = (Udev) Native.load("udev", Udev.class);

    UdevContext udev_new();

    UdevContext udev_ref(UdevContext udevContext);

    UdevContext udev_unref(UdevContext udevContext);

    UdevDevice udev_device_new_from_syspath(UdevContext udevContext, String str);

    UdevEnumerate udev_enumerate_new(UdevContext udevContext);

    UdevEnumerate udev_enumerate_ref(UdevEnumerate udevEnumerate);

    UdevEnumerate udev_enumerate_unref(UdevEnumerate udevEnumerate);

    int udev_enumerate_add_match_subsystem(UdevEnumerate udevEnumerate, String str);

    int udev_enumerate_scan_devices(UdevEnumerate udevEnumerate);

    UdevListEntry udev_enumerate_get_list_entry(UdevEnumerate udevEnumerate);

    UdevListEntry udev_list_entry_get_next(UdevListEntry udevListEntry);

    String udev_list_entry_get_name(UdevListEntry udevListEntry);

    UdevDevice udev_device_ref(UdevDevice udevDevice);

    UdevDevice udev_device_unref(UdevDevice udevDevice);

    UdevDevice udev_device_get_parent(UdevDevice udevDevice);

    UdevDevice udev_device_get_parent_with_subsystem_devtype(UdevDevice udevDevice, String str, String str2);

    String udev_device_get_syspath(UdevDevice udevDevice);

    String udev_device_get_sysname(UdevDevice udevDevice);

    String udev_device_get_devnode(UdevDevice udevDevice);

    String udev_device_get_devtype(UdevDevice udevDevice);

    String udev_device_get_subsystem(UdevDevice udevDevice);

    String udev_device_get_sysattr_value(UdevDevice udevDevice, String str);

    String udev_device_get_property_value(UdevDevice udevDevice, String str);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Udev$UdevContext.class */
    public static class UdevContext extends PointerType {
        public UdevContext ref() {
            return Udev.INSTANCE.udev_ref(this);
        }

        public void unref() {
            Udev.INSTANCE.udev_unref(this);
        }

        public UdevEnumerate enumerateNew() {
            return Udev.INSTANCE.udev_enumerate_new(this);
        }

        public UdevDevice deviceNewFromSyspath(String syspath) {
            return Udev.INSTANCE.udev_device_new_from_syspath(this, syspath);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Udev$UdevEnumerate.class */
    public static class UdevEnumerate extends PointerType {
        public UdevEnumerate ref() {
            return Udev.INSTANCE.udev_enumerate_ref(this);
        }

        public void unref() {
            Udev.INSTANCE.udev_enumerate_unref(this);
        }

        public int addMatchSubsystem(String subsystem) {
            return Udev.INSTANCE.udev_enumerate_add_match_subsystem(this, subsystem);
        }

        public int scanDevices() {
            return Udev.INSTANCE.udev_enumerate_scan_devices(this);
        }

        public UdevListEntry getListEntry() {
            return Udev.INSTANCE.udev_enumerate_get_list_entry(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Udev$UdevListEntry.class */
    public static class UdevListEntry extends PointerType {
        public UdevListEntry getNext() {
            return Udev.INSTANCE.udev_list_entry_get_next(this);
        }

        public String getName() {
            return Udev.INSTANCE.udev_list_entry_get_name(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Udev$UdevDevice.class */
    public static class UdevDevice extends PointerType {
        public UdevDevice ref() {
            return Udev.INSTANCE.udev_device_ref(this);
        }

        public void unref() {
            Udev.INSTANCE.udev_device_unref(this);
        }

        public UdevDevice getParent() {
            return Udev.INSTANCE.udev_device_get_parent(this);
        }

        public UdevDevice getParentWithSubsystemDevtype(String subsystem, String devtype) {
            return Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(this, subsystem, devtype);
        }

        public String getSyspath() {
            return Udev.INSTANCE.udev_device_get_syspath(this);
        }

        public String getSysname() {
            return Udev.INSTANCE.udev_device_get_syspath(this);
        }

        public String getDevnode() {
            return Udev.INSTANCE.udev_device_get_devnode(this);
        }

        public String getDevtype() {
            return Udev.INSTANCE.udev_device_get_devtype(this);
        }

        public String getSubsystem() {
            return Udev.INSTANCE.udev_device_get_subsystem(this);
        }

        public String getSysattrValue(String sysattr) {
            return Udev.INSTANCE.udev_device_get_sysattr_value(this, sysattr);
        }

        public String getPropertyValue(String key) {
            return Udev.INSTANCE.udev_device_get_property_value(this, key);
        }
    }
}
