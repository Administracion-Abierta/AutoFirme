package com.sun.java.help.search;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Utilities.class */
class Utilities {
    static Class class$java$lang$String;

    Utilities() {
    }

    public static String URLDecoder(String str) {
        Class<?> clsClass$;
        String str2 = str;
        try {
            Class<?> cls = Class.forName("java.net.URLDecoder");
            Class<?>[] clsArr = new Class[1];
            if (class$java$lang$String == null) {
                clsClass$ = class$("java.lang.String");
                class$java$lang$String = clsClass$;
            } else {
                clsClass$ = class$java$lang$String;
            }
            clsArr[0] = clsClass$;
            Method method = cls.getMethod("decode", clsArr);
            if (method != null) {
                str2 = (String) method.invoke(null, str);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return str2;
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}
