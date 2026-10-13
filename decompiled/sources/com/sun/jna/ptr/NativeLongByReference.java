package com.sun.jna.ptr;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/ptr/NativeLongByReference.class */
public class NativeLongByReference extends ByReference {
    public NativeLongByReference() {
        this(new NativeLong(0L));
    }

    public NativeLongByReference(NativeLong value) {
        super(NativeLong.SIZE);
        setValue(value);
    }

    public void setValue(NativeLong value) {
        getPointer().setNativeLong(0L, value);
    }

    public NativeLong getValue() {
        return getPointer().getNativeLong(0L);
    }

    @Override // com.sun.jna.ptr.ByReference, com.sun.jna.PointerType
    public String toString() {
        if (NativeLong.SIZE > 4) {
            return String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", Long.valueOf(Pointer.nativeValue(getPointer())), Long.valueOf(getValue().longValue()));
        }
        return String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", Long.valueOf(Pointer.nativeValue(getPointer())), Integer.valueOf(getValue().intValue()));
    }
}
