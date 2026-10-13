package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/ToNativeConverter.class */
public interface ToNativeConverter {
    Object toNative(Object obj, ToNativeContext toNativeContext);

    Class<?> nativeType();
}
