package com.twelvemonkeys.lang;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/ReflectUtil.class */
public final class ReflectUtil {
    private ReflectUtil() {
    }

    public static Class unwrapType(Class cls) {
        if (cls == Boolean.class) {
            return Boolean.TYPE;
        }
        if (cls == Byte.class) {
            return Byte.TYPE;
        }
        if (cls == Character.class) {
            return Character.TYPE;
        }
        if (cls == Double.class) {
            return Double.TYPE;
        }
        if (cls == Float.class) {
            return Float.TYPE;
        }
        if (cls == Integer.class) {
            return Integer.TYPE;
        }
        if (cls == Long.class) {
            return Long.TYPE;
        }
        if (cls == Short.class) {
            return Short.TYPE;
        }
        throw new IllegalArgumentException("Not a primitive wrapper: " + cls);
    }

    public static Class wrapType(Class cls) {
        if (cls == Boolean.TYPE) {
            return Boolean.class;
        }
        if (cls == Byte.TYPE) {
            return Byte.class;
        }
        if (cls == Character.TYPE) {
            return Character.class;
        }
        if (cls == Double.TYPE) {
            return Double.class;
        }
        if (cls == Float.TYPE) {
            return Float.class;
        }
        if (cls == Integer.TYPE) {
            return Integer.class;
        }
        if (cls == Long.TYPE) {
            return Long.class;
        }
        if (cls == Short.TYPE) {
            return Short.class;
        }
        throw new IllegalArgumentException("Not a primitive type: " + cls);
    }

    public static boolean isPrimitiveWrapper(Class cls) {
        return cls == Boolean.class || cls == Byte.class || cls == Character.class || cls == Double.class || cls == Float.class || cls == Integer.class || cls == Long.class || cls == Short.class;
    }
}
