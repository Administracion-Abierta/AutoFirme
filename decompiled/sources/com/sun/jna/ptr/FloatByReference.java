package com.sun.jna.ptr;

import com.aowagie.text.pdf.ColumnText;
import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/ptr/FloatByReference.class */
public class FloatByReference extends ByReference {
    public FloatByReference() {
        this(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public FloatByReference(float value) {
        super(4);
        setValue(value);
    }

    public void setValue(float value) {
        getPointer().setFloat(0L, value);
    }

    public float getValue() {
        return getPointer().getFloat(0L);
    }

    @Override // com.sun.jna.ptr.ByReference, com.sun.jna.PointerType
    public String toString() {
        return String.format("float@0x%x=%s", Long.valueOf(Pointer.nativeValue(getPointer())), Float.valueOf(getValue()));
    }
}
