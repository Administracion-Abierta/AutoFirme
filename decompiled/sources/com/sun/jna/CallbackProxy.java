package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/CallbackProxy.class */
public interface CallbackProxy extends Callback {
    Object callback(Object[] objArr);

    Class<?>[] getParameterTypes();

    Class<?> getReturnType();
}
