package com.twelvemonkeys.util;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.twelvemonkeys.lang.Validate;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/CollectionUtil.class */
public final class CollectionUtil {
    /* JADX WARN: Multi-variable type inference failed */
    public static void main(String[] strArr) {
        int i = 1000;
        if (strArr.length > 0) {
            i = Integer.parseInt(strArr[0]);
        }
        String[] strArr2 = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        System.out.println("\nFilterIterators:\n");
        List listAsList = Arrays.asList("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen");
        FilterIterator filterIterator = new FilterIterator(listAsList.iterator(), new FilterIterator.Filter() { // from class: com.twelvemonkeys.util.CollectionUtil.1
            @Override // com.twelvemonkeys.util.FilterIterator.Filter
            public boolean accept(Object obj) {
                return ((String) obj).length() > 5;
            }
        });
        while (filterIterator.hasNext()) {
            System.out.println(((String) filterIterator.next()) + " has more than 5 letters!");
        }
        FilterIterator filterIterator2 = new FilterIterator(listAsList.iterator(), new FilterIterator.Filter() { // from class: com.twelvemonkeys.util.CollectionUtil.2
            @Override // com.twelvemonkeys.util.FilterIterator.Filter
            public boolean accept(Object obj) {
                return ((String) obj).length() <= 5;
            }
        });
        while (filterIterator2.hasNext()) {
            System.out.println(((String) filterIterator2.next()) + " has less than, or exactly 5 letters!");
        }
        System.currentTimeMillis();
        for (int i2 = 0; i2 < i; i2++) {
            FilterIterator filterIterator3 = new FilterIterator(listAsList.iterator(), new FilterIterator.Filter() { // from class: com.twelvemonkeys.util.CollectionUtil.3
                @Override // com.twelvemonkeys.util.FilterIterator.Filter
                public boolean accept(Object obj) {
                    return ((String) obj).length() <= 5;
                }
            });
            while (filterIterator3.hasNext()) {
                filterIterator3.next();
                System.out.print("");
            }
        }
    }

    private CollectionUtil() {
    }

    public static Object mergeArrays(Object obj, Object obj2) {
        return mergeArrays(obj, 0, Array.getLength(obj), obj2, 0, Array.getLength(obj2));
    }

    public static Object mergeArrays(Object obj, int i, int i2, Object obj2, int i3, int i4) {
        Object objNewInstance = Array.newInstance(obj.getClass().getComponentType(), i2 + i4);
        System.arraycopy(obj, i, objNewInstance, 0, i2);
        System.arraycopy(obj2, i3, objNewInstance, i2, i4);
        return objNewInstance;
    }

    public static Object subArray(Object obj, int i) {
        return subArray(obj, i, -1);
    }

    public static <T> T[] subArray(T[] tArr, int i) {
        return (T[]) subArray((Object[]) tArr, i, -1);
    }

    public static Object subArray(Object obj, int i, int i2) {
        int iMin;
        Object objNewInstance;
        Validate.notNull(obj, "array");
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i + " < 0");
        }
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType == null) {
            throw new IllegalArgumentException("Not an array: " + obj);
        }
        int length = Array.getLength(obj);
        if (i2 < 0) {
            iMin = Math.max(0, length - i);
        } else {
            iMin = Math.min(i2, Math.max(0, length - i));
        }
        int i3 = iMin;
        if (i3 < length) {
            objNewInstance = Array.newInstance(componentType, i3);
            System.arraycopy(obj, i, objNewInstance, 0, i3);
        } else {
            objNewInstance = obj;
        }
        return objNewInstance;
    }

    public static <T> T[] subArray(T[] tArr, int i, int i2) {
        return (T[]) ((Object[]) subArray((Object) tArr, i, i2));
    }

    public static <T> Iterator<T> iterator(final Enumeration<T> enumeration) {
        Validate.notNull(enumeration, XSDatatype.FACET_ENUMERATION);
        return new Iterator<T>() { // from class: com.twelvemonkeys.util.CollectionUtil.4
            @Override // java.util.Iterator
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override // java.util.Iterator
            public T next() {
                return (T) enumeration.nextElement();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <E> void addAll(Collection<E> collection, Iterator<? extends E> it) {
        while (it.hasNext()) {
            collection.add(it.next());
        }
    }

    public static <E> ListIterator<E> iterator(E[] eArr) {
        return iterator(eArr, 0, ((Object[]) Validate.notNull(eArr)).length);
    }

    public static <E> ListIterator<E> iterator(E[] eArr, int i, int i2) {
        return new ArrayIterator(eArr, i, i2);
    }

    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        return invert(map, null, null);
    }

    public static <K, V> Map<V, K> invert(Map<K, V> map, Map<V, K> map2, DuplicateHandler<K> duplicateHandler) {
        if (map == null) {
            throw new IllegalArgumentException("source == null");
        }
        Map<V, K> map3 = map2;
        if (map3 == null) {
            try {
                map3 = (Map) map.getClass().newInstance();
            } catch (IllegalAccessException e) {
            } catch (InstantiationException e2) {
            }
            if (map3 == null) {
                throw new IllegalArgumentException("result == null and source class " + map.getClass() + " cannot be instantiated.");
            }
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            V value = entry.getValue();
            K key = entry.getKey();
            if (map3.containsKey(value)) {
                if (duplicateHandler != null) {
                    key = duplicateHandler.resolve(map3.get(value), key);
                } else {
                    throw new IllegalArgumentException("Result would include duplicate keys, but no DuplicateHandler specified.");
                }
            }
            map3.put(value, key);
        }
        return map3;
    }

    public static <T> Comparator<T> reverseOrder(Comparator<T> comparator) {
        return new ReverseComparator(comparator);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/CollectionUtil$ReverseComparator.class */
    private static class ReverseComparator<T> implements Comparator<T> {
        private final Comparator<T> comparator;

        public ReverseComparator(Comparator<T> comparator) {
            this.comparator = (Comparator) Validate.notNull(comparator);
        }

        @Override // java.util.Comparator
        public int compare(T t, T t2) {
            int iCompare = this.comparator.compare(t, t2);
            return -(iCompare | (iCompare >>> 1));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <T extends Iterator<? super E>, E> T generify(Iterator<?> it, Class<E> cls) {
        return it;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <T extends Collection<? super E>, E> T generify(Collection<?> collection, Class<E> cls) {
        return collection;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <T extends Map<? super K, ? super V>, K, V> T generify(Map<?, ?> map, Class<K> cls, Class<V> cls2) {
        return map;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <T extends Collection<? super E>, E> T generify2(Collection<?> collection) {
        return collection;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/CollectionUtil$ArrayIterator.class */
    private static class ArrayIterator<E> implements ListIterator<E> {
        private int next;
        private final int start;
        private final int length;
        private final E[] array;

        public ArrayIterator(E[] eArr, int i, int i2) {
            this.array = (E[]) ((Object[]) Validate.notNull(eArr, "array"));
            this.start = ((Integer) Validate.isTrue(i >= 0, Integer.valueOf(i), "start < 0: %d")).intValue();
            this.length = ((Integer) Validate.isTrue(i2 <= eArr.length - i, Integer.valueOf(i2), "length > array.length - start: %d")).intValue();
            this.next = this.start;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.next < this.length + this.start;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                E[] eArr = this.array;
                int i = this.next;
                this.next = i + 1;
                return eArr[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                NoSuchElementException noSuchElementException = new NoSuchElementException(e.getMessage());
                noSuchElementException.initCause(e);
                throw noSuchElementException;
            }
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.ListIterator
        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.next > this.start;
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.next - this.start;
        }

        @Override // java.util.ListIterator
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            try {
                E[] eArr = this.array;
                int i = this.next - 1;
                this.next = i;
                return eArr[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                NoSuchElementException noSuchElementException = new NoSuchElementException(e.getMessage());
                noSuchElementException.initCause(e);
                throw noSuchElementException;
            }
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return nextIndex() - 1;
        }

        @Override // java.util.ListIterator
        public void set(E e) {
            this.array[this.next - 1] = e;
        }
    }
}
