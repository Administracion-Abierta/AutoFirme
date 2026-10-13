package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/PointerType.class */
public abstract class PointerType implements NativeMapped {
    private Pointer pointer;

    protected PointerType() {
        this.pointer = Pointer.NULL;
    }

    protected PointerType(Pointer p) {
        this.pointer = p;
    }

    @Override // com.sun.jna.NativeMapped
    public Class<?> nativeType() {
        return Pointer.class;
    }

    @Override // com.sun.jna.NativeMapped
    public Object toNative() {
        return getPointer();
    }

    public Pointer getPointer() {
        return this.pointer;
    }

    public void setPointer(Pointer p) {
        this.pointer = p;
    }

    @Override // com.sun.jna.NativeMapped
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        if (nativeValue == null) {
            return null;
        }
        PointerType pt = (PointerType) Klass.newInstance(getClass());
        pt.pointer = (Pointer) nativeValue;
        return pt;
    }

    public int hashCode() {
        if (this.pointer != null) {
            return this.pointer.hashCode();
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PointerType) {
            Pointer p = ((PointerType) o).getPointer();
            if (this.pointer == null) {
                return p == null;
            }
            return this.pointer.equals(p);
        }
        return false;
    }

    public String toString() {
        return this.pointer == null ? "NULL" : this.pointer.toString() + " (" + super.toString() + ")";
    }
}
