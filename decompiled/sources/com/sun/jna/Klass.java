package com.sun.jna;

import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Klass.class */
abstract class Klass {
    private Klass() {
    }

    public static <T> T newInstance(Class<T> klass) {
        try {
            return klass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (IllegalAccessException e) {
            String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
            throw new IllegalArgumentException(msg, e);
        } catch (IllegalArgumentException e2) {
            String msg2 = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e2;
            throw new IllegalArgumentException(msg2, e2);
        } catch (InstantiationException e3) {
            String msg3 = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e3;
            throw new IllegalArgumentException(msg3, e3);
        } catch (NoSuchMethodException e4) {
            String msg4 = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e4;
            throw new IllegalArgumentException(msg4, e4);
        } catch (SecurityException e5) {
            String msg5 = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e5;
            throw new IllegalArgumentException(msg5, e5);
        } catch (InvocationTargetException e6) {
            if (e6.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e6.getCause());
            }
            String msg6 = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e6;
            throw new IllegalArgumentException(msg6, e6);
        }
    }
}
