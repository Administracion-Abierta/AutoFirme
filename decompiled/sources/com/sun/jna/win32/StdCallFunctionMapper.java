package com.sun.jna.win32;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import java.lang.reflect.Method;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/win32/StdCallFunctionMapper.class */
public class StdCallFunctionMapper implements FunctionMapper {
    protected int getArgumentNativeStackSize(Class<?> cls) {
        if (NativeMapped.class.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        if (cls.isArray()) {
            return Native.POINTER_SIZE;
        }
        try {
            return Native.getNativeSize(cls);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown native stack allocation size for " + cls);
        }
    }

    @Override // com.sun.jna.FunctionMapper
    public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        int pop = 0;
        Class<?>[] argTypes = method.getParameterTypes();
        for (Class<?> cls : argTypes) {
            pop += getArgumentNativeStackSize(cls);
        }
        String decorated = name + Registry.Key.DEFAULT_NAME + pop;
        try {
            Function func = library.getFunction(decorated, 63);
            name = func.getName();
        } catch (UnsatisfiedLinkError e) {
            try {
                Function func2 = library.getFunction("_" + decorated, 63);
                name = func2.getName();
            } catch (UnsatisfiedLinkError e2) {
            }
        }
        return name;
    }
}
