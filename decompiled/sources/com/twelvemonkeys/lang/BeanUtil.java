package com.twelvemonkeys.lang;

import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.Converter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/BeanUtil.class */
public final class BeanUtil {
    private BeanUtil() {
    }

    public static Object getPropertyValue(Object obj, String str) {
        String strSubstring;
        if (obj == null || str == null || str.length() < 1) {
            return null;
        }
        Class<?> cls = obj.getClass();
        Object objInvoke = obj;
        int i = 0;
        int iIndexOf = 0;
        while (i < str.length() && i >= 0) {
            iIndexOf = str.indexOf(46, iIndexOf + 1);
            if (iIndexOf > 0) {
                strSubstring = str.substring(i, iIndexOf);
                i = iIndexOf + 1;
            } else {
                strSubstring = str.substring(i);
                i = -1;
            }
            Object[] objArr = null;
            Class<?>[] clsArr = new Class[0];
            int iIndexOf2 = strSubstring.indexOf(91);
            if (iIndexOf2 > 0) {
                if (!strSubstring.endsWith("]")) {
                    return null;
                }
                String strSubstring2 = strSubstring.substring(iIndexOf2 + 1, strSubstring.length() - 1);
                strSubstring = strSubstring.substring(0, iIndexOf2);
                objArr = new Object[1];
                clsArr = new Class[1];
                if (StringUtil.isNumber(strSubstring2)) {
                    try {
                        objArr[0] = Integer.valueOf(strSubstring2);
                        clsArr[0] = Integer.TYPE;
                    } catch (NumberFormatException e) {
                    }
                } else {
                    objArr[0] = strSubstring2.toLowerCase();
                    clsArr[0] = String.class;
                }
            }
            String str2 = "get" + StringUtil.capitalize(strSubstring);
            try {
                Method method = cls.getMethod(str2, clsArr);
                if (method == null) {
                    return null;
                }
                try {
                    objInvoke = method.invoke(objInvoke, objArr);
                    if (objInvoke != null) {
                        cls = objInvoke.getClass();
                    } else {
                        return null;
                    }
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                    return null;
                } catch (NullPointerException e3) {
                    System.err.println(cls.getName() + "." + method.getName() + "(" + ((clsArr.length <= 0 || clsArr[0] == null) ? "" : clsArr[0].getName()) + ")");
                    e3.printStackTrace();
                    return null;
                } catch (InvocationTargetException e4) {
                    System.err.println("property=" + str + " & result=" + objInvoke + " & param=" + Arrays.toString(objArr));
                    e4.getTargetException().printStackTrace();
                    e4.printStackTrace();
                    return null;
                }
            } catch (NoSuchMethodException e5) {
                System.err.print("No method named \"" + str2 + "()\"");
                if (clsArr.length > 0 && clsArr[0] != null) {
                    System.err.print(" with the parameter " + clsArr[0].getName());
                }
                System.err.println(" in class " + cls.getName() + "!");
                return null;
            }
        }
        return objInvoke;
    }

    public static void setPropertyValue(Object obj, String str, Object obj2) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> cls = obj2 != null ? obj2.getClass() : Object.class;
        Object propertyValue = obj;
        String strSubstring = str;
        int iIndexOf = strSubstring.indexOf(46);
        if (iIndexOf >= 0) {
            propertyValue = getPropertyValue(propertyValue, strSubstring.substring(0, iIndexOf));
            strSubstring = strSubstring.substring(iIndexOf + 1);
        }
        Object[] objArr = {obj2};
        getMethodMayModifyParams(propertyValue, "set" + StringUtil.capitalize(strSubstring), new Class[]{cls}, objArr).invoke(propertyValue, objArr);
    }

    /* JADX WARN: Bottom block not found for handler: Throwable -> 0x005b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.reflect.Method getMethodMayModifyParams(java.lang.Object r5, java.lang.String r6, java.lang.Class[] r7, java.lang.Object[] r8) throws java.lang.NoSuchMethodException {
        /*
            Method dump skipped, instruction units count: 232
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.twelvemonkeys.lang.BeanUtil.getMethodMayModifyParams(java.lang.Object, java.lang.String, java.lang.Class[], java.lang.Object[]):java.lang.reflect.Method");
    }

    private static Object convertValueToType(Object obj, Class<?> cls) throws ConversionException {
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE && (obj instanceof Boolean)) {
                return obj;
            }
            if (cls == Byte.TYPE && (obj instanceof Byte)) {
                return obj;
            }
            if (cls == Character.TYPE && (obj instanceof Character)) {
                return obj;
            }
            if (cls == Double.TYPE && (obj instanceof Double)) {
                return obj;
            }
            if (cls == Float.TYPE && (obj instanceof Float)) {
                return obj;
            }
            if (cls == Integer.TYPE && (obj instanceof Integer)) {
                return obj;
            }
            if (cls == Long.TYPE && (obj instanceof Long)) {
                return obj;
            }
            if (cls == Short.TYPE && (obj instanceof Short)) {
                return obj;
            }
        }
        if (obj instanceof String) {
            return Converter.getInstance().toObject((String) obj, cls);
        }
        if (cls == String.class) {
            return Converter.getInstance().toString(obj);
        }
        throw new ConversionException("Cannot convert " + obj.getClass().getName() + " to " + cls.getName());
    }

    public static <T> T createInstance(Class<T> cls, Object obj) throws InvocationTargetException {
        return (T) createInstance((Class) cls, obj);
    }

    public static <T> T createInstance(Class<T> cls, Object... objArr) throws InvocationTargetException {
        Class<?>[] clsArr = null;
        if (objArr != null) {
            try {
                if (objArr.length > 0) {
                    clsArr = new Class[objArr.length];
                    for (int i = 0; i < objArr.length; i++) {
                        clsArr[i] = objArr[i].getClass();
                    }
                }
            } catch (ExceptionInInitializerError e) {
                return null;
            } catch (IllegalAccessException e2) {
                return null;
            } catch (IllegalArgumentException e3) {
                return null;
            } catch (InstantiationException e4) {
                return null;
            } catch (NoSuchMethodException e5) {
                return null;
            }
        }
        return cls.getConstructor(clsArr).newInstance(objArr);
    }

    public static Object invokeStaticMethod(Class<?> cls, String str, Object obj) throws InvocationTargetException {
        return invokeStaticMethod(cls, str, obj);
    }

    public static Object invokeStaticMethod(Class<?> cls, String str, Object... objArr) throws InvocationTargetException {
        Object objInvoke = null;
        try {
            Class<?>[] clsArr = new Class[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                clsArr[i] = objArr[i].getClass();
            }
            Method method = cls.getMethod(str, clsArr);
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                objInvoke = method.invoke(null, objArr);
            }
            return objInvoke;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e2) {
            return null;
        } catch (NoSuchMethodException e3) {
            return null;
        }
    }

    public static void configure(Object obj, Map<String, ?> map) throws InvocationTargetException {
        configure(obj, map, false);
    }

    public static void configure(Object obj, Map<String, ?> map, boolean z) throws InvocationTargetException {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                String strValueOf = StringUtil.valueOf(entry.getKey());
                try {
                    setPropertyValue(obj, strValueOf, entry.getValue());
                } catch (NoSuchMethodException e) {
                    if (z && strValueOf.indexOf(45) > 0) {
                        setPropertyValue(obj, StringUtil.lispToCamel(strValueOf, false), entry.getValue());
                    }
                }
            } catch (IllegalAccessException e2) {
            } catch (NoSuchMethodException e3) {
            }
        }
    }
}
