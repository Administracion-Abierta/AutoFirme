package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/FromNativeConverter.class */
public interface FromNativeConverter {
    Object fromNative(Object obj, FromNativeContext fromNativeContext);

    Class<?> nativeType();
}
