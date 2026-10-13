package org.mozilla.javascript;

import java.util.Comparator;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Sorting.class */
public final class Sorting {
    private static final int SMALLSORT = 16;
    private static final Sorting sorting = new Sorting();

    private Sorting() {
    }

    public static Sorting get() {
        return sorting;
    }

    public void insertionSort(Object[] a, Comparator<Object> cmp) {
        insertionSort(a, 0, a.length - 1, cmp);
    }

    private static void insertionSort(Object[] a, int start, int end, Comparator<Object> cmp) {
        for (int i = start; i <= end; i++) {
            Object x = a[i];
            int j = i - 1;
            while (j >= start && cmp.compare(a[j], x) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = x;
        }
    }

    public void hybridSort(Object[] a, Comparator<Object> cmp) {
        hybridSort(a, 0, a.length - 1, cmp, log2(a.length) * 2);
    }

    private void hybridSort(Object[] a, int start, int end, Comparator<Object> cmp, int maxdepth) {
        if (start < end) {
            if (maxdepth == 0 || end - start <= 16) {
                insertionSort(a, start, end, cmp);
                return;
            }
            int p = partition(a, start, end, cmp);
            hybridSort(a, start, p, cmp, maxdepth - 1);
            hybridSort(a, p + 1, end, cmp, maxdepth - 1);
        }
    }

    private int partition(Object[] a, int start, int end, Comparator<Object> cmp) {
        int p = median(a, start, end, cmp);
        Object pivot = a[p];
        a[p] = a[start];
        a[start] = pivot;
        int i = start;
        int j = end + 1;
        while (true) {
            i++;
            if (cmp.compare(a[i], pivot) >= 0 || i == end) {
                do {
                    j--;
                    if (cmp.compare(a[j], pivot) < 0) {
                        break;
                    }
                } while (j != start);
                if (i < j) {
                    swap(a, i, j);
                } else {
                    swap(a, start, j);
                    return j;
                }
            }
        }
    }

    private static void swap(Object[] a, int l, int h) {
        Object tmp = a[l];
        a[l] = a[h];
        a[h] = tmp;
    }

    private static int log2(int n) {
        return (int) (Math.log10(n) / Math.log10(2.0d));
    }

    public int median(Object[] a, int start, int end, Comparator<Object> cmp) {
        int m = start + ((end - start) / 2);
        int smallest = start;
        if (cmp.compare(a[smallest], a[m]) > 0) {
            smallest = m;
        }
        if (cmp.compare(a[smallest], a[end]) > 0) {
            smallest = end;
        }
        if (smallest == start) {
            return cmp.compare(a[m], a[end]) < 0 ? m : end;
        }
        if (smallest == m) {
            return cmp.compare(a[start], a[end]) < 0 ? start : end;
        }
        return cmp.compare(a[start], a[m]) < 0 ? start : m;
    }
}
