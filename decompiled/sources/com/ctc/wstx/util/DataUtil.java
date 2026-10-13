package com.ctc.wstx.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.codehaus.stax2.ri.SingletonIterator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/DataUtil.class */
public final class DataUtil {
    static final char[] EMPTY_CHAR_ARRAY = new char[0];
    static final Long MAX_LONG = Long.MAX_VALUE;
    static final String NO_TYPE = "Illegal to pass null; can not determine component type";

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/DataUtil$EI.class */
    private static final class EI implements Iterator<Object> {
        public static final Iterator<?> sInstance = new EI();

        private EI() {
        }

        public static <T> Iterator<T> getInstance() {
            return (Iterator<T>) sInstance;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new IllegalStateException();
        }
    }

    private DataUtil() {
    }

    public static char[] getEmptyCharArray() {
        return EMPTY_CHAR_ARRAY;
    }

    public static Integer Integer(int i) {
        return Integer.valueOf(i);
    }

    public static <T> Iterator<T> singletonIterator(T item) {
        return SingletonIterator.create(item);
    }

    public static <T> Iterator<T> emptyIterator() {
        return EI.getInstance();
    }

    public static <T> boolean anyValuesInCommon(Collection<T> c1, Collection<T> c2) {
        if (c1.size() > c2.size()) {
            c1 = c2;
            c2 = c1;
        }
        Iterator<T> it = c1.iterator();
        while (it.hasNext()) {
            if (c2.contains(it.next())) {
                return true;
            }
        }
        return false;
    }

    public static Object growArrayBy50Pct(Object arr) {
        if (arr == null) {
            throw new IllegalArgumentException(NO_TYPE);
        }
        int len = Array.getLength(arr);
        Object arr2 = Array.newInstance(arr.getClass().getComponentType(), len + (len >> 1));
        System.arraycopy(arr, 0, arr2, 0, len);
        return arr2;
    }

    public static Object growArrayToAtLeast(Object arr, int minLen) {
        if (arr == null) {
            throw new IllegalArgumentException(NO_TYPE);
        }
        int oldLen = Array.getLength(arr);
        int newLen = oldLen + ((oldLen + 1) >> 1);
        if (newLen < minLen) {
            newLen = minLen;
        }
        Object arr2 = Array.newInstance(arr.getClass().getComponentType(), newLen);
        System.arraycopy(arr, 0, arr2, 0, oldLen);
        return arr2;
    }

    public static Object growArrayToAtMost(Object arr, int maxLen) {
        if (arr == null) {
            throw new IllegalArgumentException(NO_TYPE);
        }
        int oldLen = Array.getLength(arr);
        int newLen = oldLen + ((oldLen + 1) >> 1);
        if (newLen > maxLen) {
            newLen = maxLen;
        }
        Object arr2 = Array.newInstance(arr.getClass().getComponentType(), newLen);
        System.arraycopy(arr, 0, arr2, 0, oldLen);
        return arr2;
    }

    public static String[] growArrayBy(String[] arr, int more) {
        if (arr == null) {
            return new String[more];
        }
        return (String[]) Arrays.copyOf(arr, arr.length + more);
    }

    public static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }
}
