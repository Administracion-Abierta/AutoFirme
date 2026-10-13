package com.sun.jna;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/FunctionMapper.class */
public interface FunctionMapper {
    String getFunctionName(NativeLibrary nativeLibrary, Method method);
}
