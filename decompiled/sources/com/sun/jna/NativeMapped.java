package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/NativeMapped.class */
public interface NativeMapped {
    Object fromNative(Object obj, FromNativeContext fromNativeContext);

    Object toNative();

    Class<?> nativeType();
}
