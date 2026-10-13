package com.sun.jna.ptr;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/ptr/ByReference.class */
public abstract class ByReference extends PointerType {
    protected ByReference(int dataSize) {
        setPointer(new Memory(dataSize));
    }

    @Override // com.sun.jna.PointerType
    public String toString() {
        try {
            Method getValue = getClass().getMethod("getValue", new Class[0]);
            Object value = getValue.invoke(this, new Object[0]);
            if (value == null) {
                return String.format("null@0x%x", Long.valueOf(Pointer.nativeValue(getPointer())));
            }
            return String.format("%s@0x%x=%s", value.getClass().getSimpleName(), Long.valueOf(Pointer.nativeValue(getPointer())), value);
        } catch (Exception ex) {
            return String.format("ByReference Contract violated - %s#getValue raised exception: %s", getClass().getName(), ex.getMessage());
        }
    }
}
