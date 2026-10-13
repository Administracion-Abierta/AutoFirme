package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/FromNativeContext.class */
public class FromNativeContext {
    private Class<?> type;

    FromNativeContext(Class<?> javaType) {
        this.type = javaType;
    }

    public Class<?> getTargetType() {
        return this.type;
    }
}
