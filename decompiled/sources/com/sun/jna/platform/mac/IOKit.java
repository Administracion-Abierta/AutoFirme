package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOKit.class */
public interface IOKit extends Library {
    public static final IOKit INSTANCE = (IOKit) Native.load("IOKit", IOKit.class);
    public static final int kIORegistryIterateRecursively = 1;
    public static final int kIORegistryIterateParents = 2;
    public static final int kIOReturnNoDevice = -536870208;
    public static final double kIOPSTimeRemainingUnlimited = -2.0d;
    public static final double kIOPSTimeRemainingUnknown = -1.0d;

    int IOMasterPort(int i, IntByReference intByReference);

    CoreFoundation.CFMutableDictionaryRef IOServiceMatching(String str);

    CoreFoundation.CFMutableDictionaryRef IOServiceNameMatching(String str);

    CoreFoundation.CFMutableDictionaryRef IOBSDNameMatching(int i, int i2, String str);

    IOService IOServiceGetMatchingService(int i, CoreFoundation.CFDictionaryRef cFDictionaryRef);

    int IOServiceGetMatchingServices(int i, CoreFoundation.CFDictionaryRef cFDictionaryRef, PointerByReference pointerByReference);

    IORegistryEntry IOIteratorNext(IOIterator iOIterator);

    CoreFoundation.CFTypeRef IORegistryEntryCreateCFProperty(IORegistryEntry iORegistryEntry, CoreFoundation.CFStringRef cFStringRef, CoreFoundation.CFAllocatorRef cFAllocatorRef, int i);

    int IORegistryEntryCreateCFProperties(IORegistryEntry iORegistryEntry, PointerByReference pointerByReference, CoreFoundation.CFAllocatorRef cFAllocatorRef, int i);

    CoreFoundation.CFTypeRef IORegistryEntrySearchCFProperty(IORegistryEntry iORegistryEntry, String str, CoreFoundation.CFStringRef cFStringRef, CoreFoundation.CFAllocatorRef cFAllocatorRef, int i);

    int IORegistryEntryGetRegistryEntryID(IORegistryEntry iORegistryEntry, LongByReference longByReference);

    int IORegistryEntryGetName(IORegistryEntry iORegistryEntry, Pointer pointer);

    int IORegistryEntryGetChildIterator(IORegistryEntry iORegistryEntry, String str, PointerByReference pointerByReference);

    int IORegistryEntryGetChildEntry(IORegistryEntry iORegistryEntry, String str, PointerByReference pointerByReference);

    int IORegistryEntryGetParentEntry(IORegistryEntry iORegistryEntry, String str, PointerByReference pointerByReference);

    IORegistryEntry IORegistryGetRootEntry(int i);

    boolean IOObjectConformsTo(IOObject iOObject, String str);

    int IOObjectRelease(IOObject iOObject);

    int IOServiceOpen(IOService iOService, int i, int i2, PointerByReference pointerByReference);

    int IOServiceGetBusyState(IOService iOService, IntByReference intByReference);

    int IOServiceClose(IOConnect iOConnect);

    CoreFoundation.CFTypeRef IOPSCopyPowerSourcesInfo();

    CoreFoundation.CFArrayRef IOPSCopyPowerSourcesList(CoreFoundation.CFTypeRef cFTypeRef);

    CoreFoundation.CFDictionaryRef IOPSGetPowerSourceDescription(CoreFoundation.CFTypeRef cFTypeRef, CoreFoundation.CFTypeRef cFTypeRef2);

    double IOPSGetTimeRemainingEstimate();

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOKit$IOObject.class */
    public static class IOObject extends PointerType {
        public IOObject() {
        }

        public IOObject(Pointer p) {
            super(p);
        }

        public boolean conformsTo(String className) {
            return IOKit.INSTANCE.IOObjectConformsTo(this, className);
        }

        public int release() {
            return IOKit.INSTANCE.IOObjectRelease(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOKit$IOIterator.class */
    public static class IOIterator extends IOObject {
        public IOIterator() {
        }

        public IOIterator(Pointer p) {
            super(p);
        }

        public IORegistryEntry next() {
            return IOKit.INSTANCE.IOIteratorNext(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOKit$IORegistryEntry.class */
    public static class IORegistryEntry extends IOObject {
        public IORegistryEntry() {
        }

        public IORegistryEntry(Pointer p) {
            super(p);
        }

        public long getRegistryEntryID() {
            LongByReference id = new LongByReference();
            int kr = IOKit.INSTANCE.IORegistryEntryGetRegistryEntryID(this, id);
            if (kr != 0) {
                throw new IOReturnException(kr);
            }
            return id.getValue();
        }

        public String getName() {
            Memory name = new Memory(128L);
            int kr = IOKit.INSTANCE.IORegistryEntryGetName(this, name);
            if (kr != 0) {
                throw new IOReturnException(kr);
            }
            return name.getString(0L);
        }

        public IOIterator getChildIterator(String plane) {
            PointerByReference iter = new PointerByReference();
            int kr = IOKit.INSTANCE.IORegistryEntryGetChildIterator(this, plane, iter);
            if (kr != 0) {
                throw new IOReturnException(kr);
            }
            return new IOIterator(iter.getValue());
        }

        public IORegistryEntry getChildEntry(String plane) {
            PointerByReference child = new PointerByReference();
            int kr = IOKit.INSTANCE.IORegistryEntryGetChildEntry(this, plane, child);
            if (kr == -536870208) {
                return null;
            }
            if (kr != 0) {
                throw new IOReturnException(kr);
            }
            return new IORegistryEntry(child.getValue());
        }

        public IORegistryEntry getParentEntry(String plane) {
            PointerByReference parent = new PointerByReference();
            int kr = IOKit.INSTANCE.IORegistryEntryGetParentEntry(this, plane, parent);
            if (kr == -536870208) {
                return null;
            }
            if (kr != 0) {
                throw new IOReturnException(kr);
            }
            return new IORegistryEntry(parent.getValue());
        }

        public CoreFoundation.CFTypeRef createCFProperty(CoreFoundation.CFStringRef key) {
            return IOKit.INSTANCE.IORegistryEntryCreateCFProperty(this, key, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
        }

        public CoreFoundation.CFMutableDictionaryRef createCFProperties() {
            PointerByReference properties = new PointerByReference();
            int kr = IOKit.INSTANCE.IORegistryEntryCreateCFProperties(this, properties, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
            if (kr != 0) {
                throw new IOReturnException(kr);
            }
            return new CoreFoundation.CFMutableDictionaryRef(properties.getValue());
        }

        CoreFoundation.CFTypeRef searchCFProperty(String plane, CoreFoundation.CFStringRef key, int options) {
            return IOKit.INSTANCE.IORegistryEntrySearchCFProperty(this, plane, key, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), options);
        }

        public String getStringProperty(String key) {
            String value = null;
            CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
            CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
            keyAsCFString.release();
            if (valueAsCFType != null) {
                CoreFoundation.CFStringRef valueAsCFString = new CoreFoundation.CFStringRef(valueAsCFType.getPointer());
                value = valueAsCFString.stringValue();
                valueAsCFType.release();
            }
            return value;
        }

        public Long getLongProperty(String key) {
            Long value = null;
            CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
            CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
            keyAsCFString.release();
            if (valueAsCFType != null) {
                CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
                value = Long.valueOf(valueAsCFNumber.longValue());
                valueAsCFType.release();
            }
            return value;
        }

        public Integer getIntegerProperty(String key) {
            Integer value = null;
            CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
            CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
            keyAsCFString.release();
            if (valueAsCFType != null) {
                CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
                value = Integer.valueOf(valueAsCFNumber.intValue());
                valueAsCFType.release();
            }
            return value;
        }

        public Double getDoubleProperty(String key) {
            Double value = null;
            CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
            CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
            keyAsCFString.release();
            if (valueAsCFType != null) {
                CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
                value = Double.valueOf(valueAsCFNumber.doubleValue());
                valueAsCFType.release();
            }
            return value;
        }

        public Boolean getBooleanProperty(String key) {
            Boolean value = null;
            CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
            CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
            keyAsCFString.release();
            if (valueAsCFType != null) {
                CoreFoundation.CFBooleanRef valueAsCFBoolean = new CoreFoundation.CFBooleanRef(valueAsCFType.getPointer());
                value = Boolean.valueOf(valueAsCFBoolean.booleanValue());
                valueAsCFType.release();
            }
            return value;
        }

        public byte[] getByteArrayProperty(String key) {
            byte[] value = null;
            CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
            CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
            keyAsCFString.release();
            if (valueAsCFType != null) {
                CoreFoundation.CFDataRef valueAsCFData = new CoreFoundation.CFDataRef(valueAsCFType.getPointer());
                int length = valueAsCFData.getLength();
                Pointer p = valueAsCFData.getBytePtr();
                value = p.getByteArray(0L, length);
                valueAsCFType.release();
            }
            return value;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOKit$IOService.class */
    public static class IOService extends IORegistryEntry {
        public IOService() {
        }

        public IOService(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOKit$IOConnect.class */
    public static class IOConnect extends IOService {
        public IOConnect() {
        }

        public IOConnect(Pointer p) {
            super(p);
        }
    }
}
