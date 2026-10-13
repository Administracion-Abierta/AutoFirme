package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/TypeMapper.class */
public interface TypeMapper {
    FromNativeConverter getFromNativeConverter(Class<?> cls);

    ToNativeConverter getToNativeConverter(Class<?> cls);
}
