package org.apache.pdfbox.util;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/IterativeMergeSort.class */
public final class IterativeMergeSort {
    private IterativeMergeSort() {
    }

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        if (list.size() < 2) {
            return;
        }
        Object[] array = list.toArray();
        iterativeMergeSort(array, comparator);
        ListIterator<T> listIterator = list.listIterator();
        for (Object obj : array) {
            listIterator.next();
            listIterator.set(obj);
        }
    }

    private static <T> void iterativeMergeSort(T[] arr, Comparator<? super T> cmp) {
        Object[] objArr = (Object[]) arr.clone();
        int i = 1;
        while (true) {
            int blockSize = i;
            if (blockSize < arr.length) {
                int i2 = 0;
                while (true) {
                    int start = i2;
                    if (start < arr.length) {
                        merge(arr, objArr, start, start + blockSize, start + (blockSize << 1), cmp);
                        i2 = start + (blockSize << 1);
                    }
                }
                i = blockSize << 1;
            } else {
                return;
            }
        }
    }

    private static <T> void merge(T[] arr, T[] aux, int from, int mid, int to, Comparator<? super T> cmp) {
        if (mid >= arr.length) {
            return;
        }
        if (to > arr.length) {
            to = arr.length;
        }
        int i = from;
        int j = mid;
        for (int k = from; k < to; k++) {
            if (i == mid) {
                int i2 = j;
                j++;
                aux[k] = arr[i2];
            } else if (j == to) {
                int i3 = i;
                i++;
                aux[k] = arr[i3];
            } else if (cmp.compare(arr[j], arr[i]) < 0) {
                int i4 = j;
                j++;
                aux[k] = arr[i4];
            } else {
                int i5 = i;
                i++;
                aux[k] = arr[i5];
            }
        }
        System.arraycopy(aux, from, arr, from, to - from);
    }
}
