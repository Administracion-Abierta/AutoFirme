package es.uji.crypto.xades.jxades.util;

import com.sun.jna.platform.win32.WinNT;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/UniversalIndexKey.class */
public class UniversalIndexKey implements Comparable<UniversalIndexKey> {
    private Vector<Comparable> indexKeys;

    public UniversalIndexKey(Comparable... comparableValues) {
        int size = comparableValues.length;
        if (size < 1) {
            throw new IllegalArgumentException("The minimum number of parameters for constructing of UniversalIndexKey is 1.");
        }
        this.indexKeys = new Vector<>(size);
        Collections.addAll(this.indexKeys, comparableValues);
    }

    @Override // java.lang.Comparable
    public int compareTo(UniversalIndexKey other) {
        if (other == null) {
            throw new NullPointerException();
        }
        int thisSize = this.indexKeys.size();
        int otherSize = other.indexKeys.size();
        int size = thisSize <= otherSize ? thisSize : otherSize;
        for (int i = 0; i < size; i++) {
            Comparable firstComparable = this.indexKeys.get(i);
            Comparable secondComparable = other.indexKeys.get(i);
            if (firstComparable == null) {
                return Integer.MIN_VALUE;
            }
            if (secondComparable == null) {
                return WinNT.MAXLONG;
            }
            try {
                int compareResult = firstComparable.compareTo(secondComparable);
                if (compareResult != 0) {
                    return compareResult;
                }
            } catch (ClassCastException e) {
                String s1 = String.valueOf(firstComparable);
                String s2 = String.valueOf(secondComparable);
                System.out.println("s1: " + s1 + ", s2: " + s2);
                return s1.compareTo(s2);
            }
        }
        if (thisSize < otherSize) {
            return -1;
        }
        if (thisSize > otherSize) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object otherObject) {
        if (otherObject == null || !(otherObject instanceof UniversalIndexKey)) {
            return false;
        }
        UniversalIndexKey other = (UniversalIndexKey) otherObject;
        int thisSize = this.indexKeys.size();
        int otherSize = other.indexKeys.size();
        if (thisSize != otherSize) {
            return false;
        }
        for (int i = 0; i < thisSize; i++) {
            Comparable firstComparable = this.indexKeys.get(i);
            Comparable secondComparable = other.indexKeys.get(i);
            if (firstComparable == null && secondComparable == null) {
                return true;
            }
            if (firstComparable == null && secondComparable != null) {
                return false;
            }
            if (firstComparable != null && secondComparable == null) {
                return false;
            }
            boolean equalsResult = firstComparable.equals(secondComparable);
            if (!equalsResult) {
                return equalsResult;
            }
        }
        return true;
    }

    public List<Comparable> getKeys() {
        return this.indexKeys;
    }

    public Comparable getKey(int keyIndex) {
        return this.indexKeys.get(keyIndex);
    }

    public static String getMinString(int length) {
        return getMinString(null, length);
    }

    public static String getMinString(String source) {
        return getMinString(source, 0);
    }

    public static String getMinString(String source, int length) {
        if (length < 1 && (source == null || source.length() <= 0)) {
            throw new IllegalArgumentException("The lenght can not be less or equal to 0.");
        }
        int maxLength = length;
        if (source != null && source.length() > length) {
            maxLength = source.length();
        }
        StringBuilder sb = new StringBuilder(maxLength);
        if (source != null) {
            sb.append(source.substring(0, source.length() - 1));
        }
        while (sb.length() < maxLength) {
            sb.append((char) 0);
        }
        return sb.toString();
    }

    public static String getMaxString(int length) {
        return getMaxString(null, length);
    }

    public static String getMaxString(String source) {
        return getMaxString(source, 0);
    }

    public static String getMaxString(String source, int length) {
        if (length < 1 && (source == null || source.length() <= 0)) {
            throw new IllegalArgumentException("The lenght can not be less or equal to 0.");
        }
        int maxLength = length;
        if (source != null && source.length() > length) {
            maxLength = source.length();
        }
        StringBuilder sb = new StringBuilder(maxLength);
        if (source != null) {
            sb.append(source.substring(0, source.length() - 1));
        }
        while (sb.length() < maxLength) {
            sb.append((char) 65535);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("getMaxString(\"123\", 3): " + getMaxString("123", 3));
        System.out.println("getMaxString(\"123\", 2): " + getMaxString("123", 2));
        System.out.println("getMaxString(\"123\", 5): " + getMaxString("123", 5));
        System.out.println("getMaxString(NULL, 5): " + getMaxString(null, 5));
        System.out.println("getMaxString(7): " + getMaxString(7));
        System.out.println("getMaxString(\"1234\"): " + getMaxString("1234"));
    }
}
