package com.twelvemonkeys.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/Validate.class */
public final class Validate {
    private static final String UNSPECIFIED_PARAM_NAME = "method parameter";

    private Validate() {
    }

    public static <T> T notNull(T t) {
        return (T) notNull(t, null);
    }

    public static <T> T notNull(T t, String str) {
        if (t == null) {
            Object[] objArr = new Object[1];
            objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
            throw new IllegalArgumentException(String.format("%s may not be null", objArr));
        }
        return t;
    }

    public static <T extends CharSequence> T notEmpty(T t) {
        return (T) notEmpty(t, (String) null);
    }

    public static <T extends CharSequence> T notEmpty(T t, String str) {
        if (t == null || t.length() == 0 || isOnlyWhiteSpace(t)) {
            Object[] objArr = new Object[1];
            objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
            throw new IllegalArgumentException(String.format("%s may not be blank", objArr));
        }
        return t;
    }

    private static <T extends CharSequence> boolean isOnlyWhiteSpace(T t) {
        for (int i = 0; i < t.length(); i++) {
            if (!Character.isWhitespace(t.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static <T> T[] notEmpty(T[] tArr) {
        return (T[]) notEmpty(tArr, (String) null);
    }

    public static <T> T[] notEmpty(T[] tArr, String str) {
        if (tArr == null || tArr.length == 0) {
            Object[] objArr = new Object[1];
            objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
            throw new IllegalArgumentException(String.format("%s may not be empty", objArr));
        }
        return tArr;
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection) {
        return notEmpty(collection, (String) null);
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection, String str) {
        if (collection == null || collection.isEmpty()) {
            Object[] objArr = new Object[1];
            objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
            throw new IllegalArgumentException(String.format("%s may not be empty", objArr));
        }
        return collection;
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map) {
        return notEmpty(map, (String) null);
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String str) {
        if (map == null || map.isEmpty()) {
            Object[] objArr = new Object[1];
            objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
            throw new IllegalArgumentException(String.format("%s may not be empty", objArr));
        }
        return map;
    }

    public static <T> T[] noNullElements(T[] tArr) {
        return (T[]) noNullElements(tArr, (String) null);
    }

    public static <T> T[] noNullElements(T[] tArr, String str) {
        noNullElements(tArr == null ? null : Arrays.asList(tArr), str);
        return tArr;
    }

    public static <T> Collection<T> noNullElements(Collection<T> collection) {
        return noNullElements(collection, (String) null);
    }

    public static <T> Collection<T> noNullElements(Collection<T> collection, String str) {
        notNull(collection, str);
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                Object[] objArr = new Object[1];
                objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
                throw new IllegalArgumentException(String.format("%s may not contain null elements", objArr));
            }
        }
        return collection;
    }

    public static <K, V> Map<K, V> noNullValues(Map<K, V> map) {
        return noNullValues(map, null);
    }

    public static <K, V> Map<K, V> noNullValues(Map<K, V> map, String str) {
        notNull(map, str);
        Iterator<V> it = map.values().iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                Object[] objArr = new Object[1];
                objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
                throw new IllegalArgumentException(String.format("%s may not contain null values", objArr));
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> noNullKeys(Map<K, V> map) {
        return noNullKeys(map, null);
    }

    public static <K, V> Map<K, V> noNullKeys(Map<K, V> map, String str) {
        notNull(map, str);
        Iterator<K> it = map.keySet().iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                Object[] objArr = new Object[1];
                objArr[0] = str == null ? UNSPECIFIED_PARAM_NAME : str;
                throw new IllegalArgumentException(String.format("%s may not contain null keys", objArr));
            }
        }
        return map;
    }

    public static boolean isTrue(boolean z, String str) {
        return ((Boolean) isTrue(z, Boolean.valueOf(z), str)).booleanValue();
    }

    public static <T> T isTrue(boolean z, T t, String str) {
        if (!z) {
            throw new IllegalArgumentException(String.format(str == null ? "expression may not be %s" : str, t));
        }
        return t;
    }
}
