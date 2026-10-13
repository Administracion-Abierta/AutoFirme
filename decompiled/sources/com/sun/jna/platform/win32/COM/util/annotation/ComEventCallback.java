package com.sun.jna.platform.win32.COM.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/annotation/ComEventCallback.class */
@Target({ElementType.METHOD})
@Deprecated
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ComEventCallback {
    int dispid() default -1;

    String name() default "";
}
