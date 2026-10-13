package com.sun.jna.ptr;

import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/ptr/ShortByReference.class */
public class ShortByReference extends ByReference {
    public ShortByReference() {
        this((short) 0);
    }

    public ShortByReference(short value) {
        super(2);
        setValue(value);
    }

    public void setValue(short value) {
        getPointer().setShort(0L, value);
    }

    public short getValue() {
        return getPointer().getShort(0L);
    }

    @Override // com.sun.jna.ptr.ByReference, com.sun.jna.PointerType
    public String toString() {
        return String.format("short@0x%1$x=0x%2$x (%2$d)", Long.valueOf(Pointer.nativeValue(getPointer())), Short.valueOf(getValue()));
    }
}
