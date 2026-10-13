package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation.class */
public interface CoreFoundation extends Library {
    public static final int kCFNotFound = -1;
    public static final int kCFStringEncodingASCII = 1536;
    public static final int kCFStringEncodingUTF8 = 134217984;
    public static final CoreFoundation INSTANCE = (CoreFoundation) Native.load("CoreFoundation", CoreFoundation.class);
    public static final CFTypeID ARRAY_TYPE_ID = INSTANCE.CFArrayGetTypeID();
    public static final CFTypeID BOOLEAN_TYPE_ID = INSTANCE.CFBooleanGetTypeID();
    public static final CFTypeID DATA_TYPE_ID = INSTANCE.CFDataGetTypeID();
    public static final CFTypeID DATE_TYPE_ID = INSTANCE.CFDateGetTypeID();
    public static final CFTypeID DICTIONARY_TYPE_ID = INSTANCE.CFDictionaryGetTypeID();
    public static final CFTypeID NUMBER_TYPE_ID = INSTANCE.CFNumberGetTypeID();
    public static final CFTypeID STRING_TYPE_ID = INSTANCE.CFStringGetTypeID();

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFAllocatorRef.class */
    public static class CFAllocatorRef extends CFTypeRef {
    }

    CFStringRef CFStringCreateWithCharacters(CFAllocatorRef cFAllocatorRef, char[] cArr, CFIndex cFIndex);

    CFNumberRef CFNumberCreate(CFAllocatorRef cFAllocatorRef, CFIndex cFIndex, ByReference byReference);

    CFArrayRef CFArrayCreate(CFAllocatorRef cFAllocatorRef, Pointer pointer, CFIndex cFIndex, Pointer pointer2);

    CFDataRef CFDataCreate(CFAllocatorRef cFAllocatorRef, Pointer pointer, CFIndex cFIndex);

    CFMutableDictionaryRef CFDictionaryCreateMutable(CFAllocatorRef cFAllocatorRef, CFIndex cFIndex, Pointer pointer, Pointer pointer2);

    CFStringRef CFCopyDescription(CFTypeRef cFTypeRef);

    void CFRelease(CFTypeRef cFTypeRef);

    CFTypeRef CFRetain(CFTypeRef cFTypeRef);

    CFIndex CFGetRetainCount(CFTypeRef cFTypeRef);

    CFIndex CFDictionaryGetCount(CFDictionaryRef cFDictionaryRef);

    Pointer CFDictionaryGetValue(CFDictionaryRef cFDictionaryRef, PointerType pointerType);

    byte CFDictionaryGetValueIfPresent(CFDictionaryRef cFDictionaryRef, PointerType pointerType, PointerByReference pointerByReference);

    void CFDictionarySetValue(CFMutableDictionaryRef cFMutableDictionaryRef, PointerType pointerType, PointerType pointerType2);

    byte CFStringGetCString(CFStringRef cFStringRef, Pointer pointer, CFIndex cFIndex, int i);

    byte CFBooleanGetValue(CFBooleanRef cFBooleanRef);

    CFIndex CFArrayGetCount(CFArrayRef cFArrayRef);

    Pointer CFArrayGetValueAtIndex(CFArrayRef cFArrayRef, CFIndex cFIndex);

    CFIndex CFNumberGetType(CFNumberRef cFNumberRef);

    byte CFNumberGetValue(CFNumberRef cFNumberRef, CFIndex cFIndex, ByReference byReference);

    CFIndex CFStringGetLength(CFStringRef cFStringRef);

    CFIndex CFStringGetMaximumSizeForEncoding(CFIndex cFIndex, int i);

    CFAllocatorRef CFAllocatorGetDefault();

    CFIndex CFDataGetLength(CFDataRef cFDataRef);

    Pointer CFDataGetBytePtr(CFDataRef cFDataRef);

    CFTypeID CFGetTypeID(CFTypeRef cFTypeRef);

    CFTypeID CFArrayGetTypeID();

    CFTypeID CFBooleanGetTypeID();

    CFTypeID CFDateGetTypeID();

    CFTypeID CFDataGetTypeID();

    CFTypeID CFDictionaryGetTypeID();

    CFTypeID CFNumberGetTypeID();

    CFTypeID CFStringGetTypeID();

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFTypeRef.class */
    public static class CFTypeRef extends PointerType {
        public CFTypeRef() {
        }

        public CFTypeRef(Pointer p) {
            super(p);
        }

        public CFTypeID getTypeID() {
            if (getPointer() == null) {
                return new CFTypeID(0L);
            }
            return CoreFoundation.INSTANCE.CFGetTypeID(this);
        }

        public boolean isTypeID(CFTypeID typeID) {
            return getTypeID().equals(typeID);
        }

        public void retain() {
            CoreFoundation.INSTANCE.CFRetain(this);
        }

        public void release() {
            CoreFoundation.INSTANCE.CFRelease(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFNumberRef.class */
    public static class CFNumberRef extends CFTypeRef {
        public CFNumberRef() {
        }

        public CFNumberRef(Pointer p) {
            super(p);
            if (!isTypeID(CoreFoundation.NUMBER_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFNumber. Type ID: " + getTypeID());
            }
        }

        public long longValue() {
            LongByReference lbr = new LongByReference();
            CoreFoundation.INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberLongLongType.typeIndex(), lbr);
            return lbr.getValue();
        }

        public int intValue() {
            IntByReference ibr = new IntByReference();
            CoreFoundation.INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberIntType.typeIndex(), ibr);
            return ibr.getValue();
        }

        public short shortValue() {
            ShortByReference sbr = new ShortByReference();
            CoreFoundation.INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberShortType.typeIndex(), sbr);
            return sbr.getValue();
        }

        public byte byteValue() {
            ByteByReference bbr = new ByteByReference();
            CoreFoundation.INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberCharType.typeIndex(), bbr);
            return bbr.getValue();
        }

        public double doubleValue() {
            DoubleByReference dbr = new DoubleByReference();
            CoreFoundation.INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberDoubleType.typeIndex(), dbr);
            return dbr.getValue();
        }

        public float floatValue() {
            FloatByReference fbr = new FloatByReference();
            CoreFoundation.INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberFloatType.typeIndex(), fbr);
            return fbr.getValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFNumberType.class */
    public enum CFNumberType {
        unusedZero,
        kCFNumberSInt8Type,
        kCFNumberSInt16Type,
        kCFNumberSInt32Type,
        kCFNumberSInt64Type,
        kCFNumberFloat32Type,
        kCFNumberFloat64Type,
        kCFNumberCharType,
        kCFNumberShortType,
        kCFNumberIntType,
        kCFNumberLongType,
        kCFNumberLongLongType,
        kCFNumberFloatType,
        kCFNumberDoubleType,
        kCFNumberCFIndexType,
        kCFNumberNSIntegerType,
        kCFNumberCGFloatType,
        kCFNumberMaxType;

        public CFIndex typeIndex() {
            return new CFIndex(ordinal());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFBooleanRef.class */
    public static class CFBooleanRef extends CFTypeRef {
        public CFBooleanRef() {
        }

        public CFBooleanRef(Pointer p) {
            super(p);
            if (!isTypeID(CoreFoundation.BOOLEAN_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFBoolean. Type ID: " + getTypeID());
            }
        }

        public boolean booleanValue() {
            return 0 != CoreFoundation.INSTANCE.CFBooleanGetValue(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFArrayRef.class */
    public static class CFArrayRef extends CFTypeRef {
        public CFArrayRef() {
        }

        public CFArrayRef(Pointer p) {
            super(p);
            if (!isTypeID(CoreFoundation.ARRAY_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFArray. Type ID: " + getTypeID());
            }
        }

        public int getCount() {
            return CoreFoundation.INSTANCE.CFArrayGetCount(this).intValue();
        }

        public Pointer getValueAtIndex(int idx) {
            return CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(this, new CFIndex(idx));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFDataRef.class */
    public static class CFDataRef extends CFTypeRef {
        public CFDataRef() {
        }

        public CFDataRef(Pointer p) {
            super(p);
            if (!isTypeID(CoreFoundation.DATA_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFData. Type ID: " + getTypeID());
            }
        }

        public int getLength() {
            return CoreFoundation.INSTANCE.CFDataGetLength(this).intValue();
        }

        public Pointer getBytePtr() {
            return CoreFoundation.INSTANCE.CFDataGetBytePtr(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFDictionaryRef.class */
    public static class CFDictionaryRef extends CFTypeRef {
        public CFDictionaryRef() {
        }

        public CFDictionaryRef(Pointer p) {
            super(p);
            if (!isTypeID(CoreFoundation.DICTIONARY_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + getTypeID());
            }
        }

        public Pointer getValue(PointerType key) {
            return CoreFoundation.INSTANCE.CFDictionaryGetValue(this, key);
        }

        public long getCount() {
            return CoreFoundation.INSTANCE.CFDictionaryGetCount(this).longValue();
        }

        public boolean getValueIfPresent(PointerType key, PointerByReference value) {
            return CoreFoundation.INSTANCE.CFDictionaryGetValueIfPresent(this, key, value) > 0;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFMutableDictionaryRef.class */
    public static class CFMutableDictionaryRef extends CFDictionaryRef {
        public CFMutableDictionaryRef() {
        }

        public CFMutableDictionaryRef(Pointer p) {
            super(p);
        }

        public void setValue(PointerType key, PointerType value) {
            CoreFoundation.INSTANCE.CFDictionarySetValue(this, key, value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFStringRef.class */
    public static class CFStringRef extends CFTypeRef {
        public CFStringRef() {
        }

        public CFStringRef(Pointer p) {
            super(p);
            if (!isTypeID(CoreFoundation.STRING_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFString. Type ID: " + getTypeID());
            }
        }

        public static CFStringRef createCFString(String s) {
            char[] chars = s.toCharArray();
            return CoreFoundation.INSTANCE.CFStringCreateWithCharacters(null, chars, new CFIndex(chars.length));
        }

        public String stringValue() {
            CFIndex length = CoreFoundation.INSTANCE.CFStringGetLength(this);
            if (length.longValue() == 0) {
                return "";
            }
            CFIndex maxSize = CoreFoundation.INSTANCE.CFStringGetMaximumSizeForEncoding(length, CoreFoundation.kCFStringEncodingUTF8);
            if (maxSize.intValue() == -1) {
                throw new StringIndexOutOfBoundsException("CFString maximum number of bytes exceeds LONG_MAX.");
            }
            maxSize.setValue(maxSize.longValue() + 1);
            Memory buf = new Memory(maxSize.longValue());
            if (0 != CoreFoundation.INSTANCE.CFStringGetCString(this, buf, maxSize, CoreFoundation.kCFStringEncodingUTF8)) {
                return buf.getString(0L, "UTF8");
            }
            throw new IllegalArgumentException("CFString conversion fails or the provided buffer is too small.");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFIndex.class */
    public static class CFIndex extends NativeLong {
        private static final long serialVersionUID = 1;

        public CFIndex() {
        }

        public CFIndex(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/CoreFoundation$CFTypeID.class */
    public static class CFTypeID extends NativeLong {
        private static final long serialVersionUID = 1;

        public CFTypeID() {
        }

        public CFTypeID(long value) {
            super(value);
        }

        @Override // com.sun.jna.IntegerType
        public String toString() {
            if (equals(CoreFoundation.ARRAY_TYPE_ID)) {
                return "CFArray";
            }
            if (equals(CoreFoundation.BOOLEAN_TYPE_ID)) {
                return "CFBoolean";
            }
            if (equals(CoreFoundation.DATA_TYPE_ID)) {
                return "CFData";
            }
            if (equals(CoreFoundation.DATE_TYPE_ID)) {
                return "CFDate";
            }
            if (equals(CoreFoundation.DICTIONARY_TYPE_ID)) {
                return "CFDictionary";
            }
            if (equals(CoreFoundation.NUMBER_TYPE_ID)) {
                return "CFNumber";
            }
            if (equals(CoreFoundation.STRING_TYPE_ID)) {
                return "CFString";
            }
            return super.toString();
        }
    }
}
