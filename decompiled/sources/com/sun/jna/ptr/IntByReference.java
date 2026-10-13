package com.sun.jna.ptr;

import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/ptr/IntByReference.class */
public class IntByReference extends ByReference {
    public IntByReference() {
        this(0);
    }

    public IntByReference(int value) {
        super(4);
        setValue(value);
    }

    public void setValue(int value) {
        getPointer().setInt(0L, value);
    }

    public int getValue() {
        return getPointer().getInt(0L);
    }

    @Override // com.sun.jna.ptr.ByReference, com.sun.jna.PointerType
    public String toString() {
        return String.format("int@0x%1$x=0x%2$x (%2$d)", Long.valueOf(Pointer.nativeValue(getPointer())), Integer.valueOf(getValue()));
    }
}
