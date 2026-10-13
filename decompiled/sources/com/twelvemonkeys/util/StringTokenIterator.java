package com.twelvemonkeys.util;

import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/StringTokenIterator.class */
public class StringTokenIterator extends AbstractTokenIterator {
    private final String string;
    private final char[] delimiters;
    private int position;
    private final int maxPosition;
    private String next;
    private String nextDelimiter;
    private final boolean includeDelimiters;
    private final boolean includeEmpty;
    private final boolean reverse;
    public static final int FORWARD = 1;
    public static final int REVERSE = -1;
    private final char maxDelimiter;

    public StringTokenIterator(String str) {
        this(str, " \t\n\r\f".toCharArray(), 1, false, false);
    }

    public StringTokenIterator(String str, String str2) {
        this(str, toCharArray(str2), 1, false, false);
    }

    public StringTokenIterator(String str, String str2, int i) {
        this(str, toCharArray(str2), i, false, false);
    }

    public StringTokenIterator(String str, String str2, boolean z) {
        this(str, toCharArray(str2), 1, z, false);
    }

    public StringTokenIterator(String str, String str2, int i, boolean z, boolean z2) {
        this(str, toCharArray(str2), i, z, z2);
    }

    private StringTokenIterator(String str, char[] cArr, int i, boolean z, boolean z2) {
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        }
        this.string = str;
        this.maxPosition = str.length();
        this.delimiters = cArr;
        this.includeDelimiters = z;
        this.reverse = i == -1;
        this.includeEmpty = z2;
        this.maxDelimiter = initMaxDelimiter(cArr);
        reset();
    }

    private static char[] toCharArray(String str) {
        if (str == null) {
            throw new IllegalArgumentException("delimiters == null");
        }
        return str.toCharArray();
    }

    private static char initMaxDelimiter(char[] cArr) {
        if (cArr == null) {
            return (char) 0;
        }
        char c = 0;
        for (char c2 : cArr) {
            if (c < c2) {
                c = c2;
            }
        }
        return c;
    }

    @Override // com.twelvemonkeys.util.TokenIterator
    public void reset() {
        this.position = 0;
        this.next = null;
        this.nextDelimiter = null;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return (this.next == null && fetchNext() == null) ? false : true;
    }

    private String fetchNext() {
        if (this.nextDelimiter != null) {
            this.next = this.nextDelimiter;
            this.nextDelimiter = null;
            return this.next;
        }
        if (this.position >= this.maxPosition) {
            return null;
        }
        return this.reverse ? fetchReverse() : fetchForward();
    }

    private String fetchReverse() {
        int iScanForPrev = scanForPrev();
        this.next = this.string.substring(iScanForPrev + 1, this.maxPosition - this.position);
        if (this.includeDelimiters && iScanForPrev >= 0 && iScanForPrev < this.maxPosition) {
            this.nextDelimiter = this.string.substring(iScanForPrev, iScanForPrev + 1);
        }
        this.position = this.maxPosition - iScanForPrev;
        if (this.next.length() == 0 && !this.includeEmpty) {
            return fetchNext();
        }
        return this.next;
    }

    private String fetchForward() {
        int iScanForNext = scanForNext();
        this.next = this.string.substring(this.position, iScanForNext);
        if (this.includeDelimiters && iScanForNext >= 0 && iScanForNext < this.maxPosition) {
            this.nextDelimiter = this.string.substring(iScanForNext, iScanForNext + 1);
        }
        this.position = iScanForNext + 1;
        if (this.next.length() == 0 && !this.includeEmpty) {
            return fetchNext();
        }
        return this.next;
    }

    private int scanForNext() {
        int i = this.position;
        while (i < this.maxPosition) {
            char cCharAt = this.string.charAt(i);
            if (cCharAt <= this.maxDelimiter) {
                for (char c : this.delimiters) {
                    if (cCharAt == c) {
                        return i;
                    }
                }
            }
            i++;
        }
        return i;
    }

    private int scanForPrev() {
        int i = (this.maxPosition - 1) - this.position;
        while (i >= 0) {
            char cCharAt = this.string.charAt(i);
            if (cCharAt <= this.maxDelimiter) {
                for (char c : this.delimiters) {
                    if (cCharAt == c) {
                        return i;
                    }
                }
            }
            i--;
        }
        return i;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String str = this.next;
        this.next = fetchNext();
        return str;
    }
}
