package com.twelvemonkeys.lang;

import com.twelvemonkeys.util.StringTokenIterator;
import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/StringUtil.class */
public final class StringUtil {
    public static final String DELIMITER_STRING = ", \t\n\r\f";

    private StringUtil() {
    }

    public static String decode(byte[] bArr, int i, int i2, String str) {
        try {
            return new String(bArr, i, i2, str);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(str);
        }
    }

    public static String valueOf(Object obj) {
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    public static String toUpperCase(String str) {
        if (str != null) {
            return str.toUpperCase();
        }
        return null;
    }

    public static String toLowerCase(String str) {
        if (str != null) {
            return str.toLowerCase();
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isEmpty(String[] strArr) {
        if (strArr == null) {
            return true;
        }
        for (String str : strArr) {
            if (!isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(String str, String str2) {
        return (str == null || str2 == null || str.indexOf(str2) < 0) ? false : true;
    }

    public static boolean containsIgnoreCase(String str, String str2) {
        return indexOfIgnoreCase(str, str2, 0) >= 0;
    }

    public static boolean contains(String str, int i) {
        return str != null && str.indexOf(i) >= 0;
    }

    public static boolean containsIgnoreCase(String str, int i) {
        return str != null && (str.indexOf(Character.toLowerCase((char) i)) >= 0 || str.indexOf(Character.toUpperCase((char) i)) >= 0);
    }

    public static int indexOfIgnoreCase(String str, String str2) {
        return indexOfIgnoreCase(str, str2, 0);
    }

    public static int indexOfIgnoreCase(String str, String str2, int i) {
        int i2;
        if (str == null || str2 == null) {
            return -1;
        }
        if (str2.length() == 0) {
            return i;
        }
        if (str2.length() > str.length()) {
            return -1;
        }
        char lowerCase = Character.toLowerCase(str2.charAt(0));
        char upperCase = Character.toUpperCase(str2.charAt(0));
        int iIndexOf = 0;
        int iIndexOf2 = 0;
        int i3 = i;
        while (i3 <= str.length() - str2.length()) {
            iIndexOf = (iIndexOf < 0 || iIndexOf > i3) ? iIndexOf : str.indexOf(lowerCase, i3);
            iIndexOf2 = (iIndexOf2 < 0 || iIndexOf2 > i3) ? iIndexOf2 : str.indexOf(upperCase, i3);
            if (iIndexOf < 0) {
                if (iIndexOf2 < 0) {
                    return -1;
                }
                i2 = iIndexOf2;
            } else if (iIndexOf2 < 0) {
                i2 = iIndexOf;
            } else {
                i2 = iIndexOf < iIndexOf2 ? iIndexOf : iIndexOf2;
            }
            if (str2.length() == 1) {
                return i2;
            }
            if (i2 > str.length() - str2.length()) {
                return -1;
            }
            if ((str.charAt((i2 + str2.length()) - 1) != Character.toLowerCase(str2.charAt(str2.length() - 1)) && str.charAt((i2 + str2.length()) - 1) != Character.toUpperCase(str2.charAt(str2.length() - 1))) || (str2.length() > 2 && !str.regionMatches(true, i2 + 1, str2, 1, str2.length() - 2))) {
                i3 = i2 + 1;
            } else {
                return i2;
            }
        }
        return -1;
    }

    public static int lastIndexOfIgnoreCase(String str, String str2) {
        return lastIndexOfIgnoreCase(str, str2, str != null ? str.length() - 1 : -1);
    }

    public static int lastIndexOfIgnoreCase(String str, String str2, int i) {
        int i2;
        if (str == null || str2 == null) {
            return -1;
        }
        if (str2.length() == 0) {
            return i;
        }
        if (str2.length() > str.length()) {
            return -1;
        }
        char lowerCase = Character.toLowerCase(str2.charAt(0));
        char upperCase = Character.toUpperCase(str2.charAt(0));
        int iLastIndexOf = i;
        int iLastIndexOf2 = i;
        int i3 = i;
        while (i3 >= 0) {
            iLastIndexOf = (iLastIndexOf < 0 || iLastIndexOf < i3) ? iLastIndexOf : str.lastIndexOf(lowerCase, i3);
            iLastIndexOf2 = (iLastIndexOf2 < 0 || iLastIndexOf2 < i3) ? iLastIndexOf2 : str.lastIndexOf(upperCase, i3);
            if (iLastIndexOf < 0) {
                if (iLastIndexOf2 < 0) {
                    return -1;
                }
                i2 = iLastIndexOf2;
            } else if (iLastIndexOf2 < 0) {
                i2 = iLastIndexOf;
            } else {
                i2 = iLastIndexOf > iLastIndexOf2 ? iLastIndexOf : iLastIndexOf2;
            }
            if (str2.length() == 1) {
                return i2;
            }
            if (i2 > str.length() - str2.length() || ((str.charAt((i2 + str2.length()) - 1) != Character.toLowerCase(str2.charAt(str2.length() - 1)) && str.charAt((i2 + str2.length()) - 1) != Character.toUpperCase(str2.charAt(str2.length() - 1))) || (str2.length() > 2 && !str.regionMatches(true, i2 + 1, str2, 1, str2.length() - 2)))) {
                i3 = i2 - 1;
            } else {
                return i2;
            }
        }
        return -1;
    }

    public static int indexOfIgnoreCase(String str, int i) {
        return indexOfIgnoreCase(str, i, 0);
    }

    public static int indexOfIgnoreCase(String str, int i, int i2) {
        if (str == null) {
            return -1;
        }
        char lowerCase = Character.toLowerCase((char) i);
        char upperCase = Character.toUpperCase((char) i);
        int iIndexOf = str.indexOf(lowerCase, i2);
        int iIndexOf2 = str.indexOf(upperCase, i2);
        if (iIndexOf < 0) {
            return iIndexOf2;
        }
        if (iIndexOf2 < 0) {
            return iIndexOf;
        }
        return iIndexOf < iIndexOf2 ? iIndexOf : iIndexOf2;
    }

    public static int lastIndexOfIgnoreCase(String str, int i) {
        return lastIndexOfIgnoreCase(str, i, str != null ? str.length() : -1);
    }

    public static int lastIndexOfIgnoreCase(String str, int i, int i2) {
        if (str == null) {
            return -1;
        }
        char lowerCase = Character.toLowerCase((char) i);
        char upperCase = Character.toUpperCase((char) i);
        int iLastIndexOf = str.lastIndexOf(lowerCase, i2);
        int iLastIndexOf2 = str.lastIndexOf(upperCase, i2);
        if (iLastIndexOf < 0) {
            return iLastIndexOf2;
        }
        if (iLastIndexOf2 < 0) {
            return iLastIndexOf;
        }
        return iLastIndexOf > iLastIndexOf2 ? iLastIndexOf : iLastIndexOf2;
    }

    public static String ltrim(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                if (i == 0) {
                    return str;
                }
                return str.substring(i);
            }
        }
        return "";
    }

    public static String rtrim(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        for (int length = str.length(); length > 0; length--) {
            if (!Character.isWhitespace(str.charAt(length - 1))) {
                if (length == str.length()) {
                    return str;
                }
                return str.substring(0, length);
            }
        }
        return "";
    }

    public static String replace(String str, String str2, String str3) {
        if (str2.length() == 0) {
            return str;
        }
        int length = 0;
        StringBuilder sb = new StringBuilder();
        while (true) {
            int iIndexOf = str.indexOf(str2, length);
            if (iIndexOf != -1) {
                sb.append(str.substring(length, iIndexOf));
                sb.append(str3);
                length = iIndexOf + str2.length();
            } else {
                sb.append(str.substring(length));
                return sb.toString();
            }
        }
    }

    public static String replaceIgnoreCase(String str, String str2, String str3) {
        if (str2.length() == 0) {
            return str;
        }
        int length = 0;
        StringBuilder sb = new StringBuilder();
        while (true) {
            int iIndexOfIgnoreCase = indexOfIgnoreCase(str, str2, length);
            if (iIndexOfIgnoreCase != -1) {
                sb.append(str.substring(length, iIndexOfIgnoreCase));
                sb.append(str3);
                length = iIndexOfIgnoreCase + str2.length();
            } else {
                sb.append(str.substring(length));
                return sb.toString();
            }
        }
    }

    public static String cut(String str, int i, String str2) {
        if (str == null) {
            return null;
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str.length() > i) {
            return str.substring(0, str.lastIndexOf(32, i - str2.length())) + str2;
        }
        return str;
    }

    public static String capitalize(String str, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("Negative index not allowed: " + i);
        }
        if (str == null || str.length() <= i) {
            return str;
        }
        if (Character.isUpperCase(str.charAt(i))) {
            return str;
        }
        char[] charArray = str.toCharArray();
        charArray[i] = Character.toUpperCase(charArray[i]);
        return new String(charArray);
    }

    public static String capitalize(String str) {
        return capitalize(str, 0);
    }

    @Deprecated
    static String formatNumber(long j, int i) throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();
        if (j >= Math.pow(10.0d, i)) {
            throw new IllegalArgumentException("The number to format cannot contain more digits than the length argument specifies!");
        }
        for (int i2 = i; i2 > 1 && j < Math.pow(10.0d, i2 - 1); i2--) {
            sb.append('0');
        }
        sb.append(j);
        return sb.toString();
    }

    public static String pad(String str, int i, String str2, boolean z) {
        if (str2 == null || str2.length() == 0) {
            throw new IllegalArgumentException("Pad string: \"" + str2 + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        if (str.length() >= i) {
            return str;
        }
        int length = i - str.length();
        StringBuilder sb = new StringBuilder(str2);
        while (sb.length() < length) {
            sb.append((CharSequence) sb);
        }
        if (sb.length() > length) {
            sb.delete(length, sb.length());
        }
        return z ? sb.append(str).toString() : sb.insert(0, str).toString();
    }

    public static Date toDate(String str) {
        return toDate(str, DateFormat.getInstance());
    }

    public static Date toDate(String str, String str2) {
        return toDate(str, new SimpleDateFormat(str2));
    }

    public static Date toDate(String str, DateFormat dateFormat) {
        Date date;
        try {
            synchronized (dateFormat) {
                date = dateFormat.parse(str);
            }
            return date;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage() + " at pos " + e.getErrorOffset());
        }
    }

    public static Timestamp toTimestamp(String str) {
        return Timestamp.valueOf(str);
    }

    public static String[] toStringArray(String str, String str2) {
        if (isEmpty(str)) {
            return new String[0];
        }
        StringTokenIterator stringTokenIterator = new StringTokenIterator(str, str2);
        ArrayList arrayList = new ArrayList();
        while (stringTokenIterator.hasMoreElements()) {
            arrayList.add(stringTokenIterator.nextToken());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public static String[] toStringArray(String str) {
        return toStringArray(str, DELIMITER_STRING);
    }

    public static int[] toIntArray(String str, String str2, int i) {
        if (isEmpty(str)) {
            return new int[0];
        }
        String[] stringArray = toStringArray(str, str2);
        int[] iArr = new int[stringArray.length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr[i2] = Integer.parseInt(stringArray[i2], i);
        }
        return iArr;
    }

    public static int[] toIntArray(String str) {
        return toIntArray(str, DELIMITER_STRING, 10);
    }

    public static int[] toIntArray(String str, String str2) {
        return toIntArray(str, str2, 10);
    }

    public static long[] toLongArray(String str, String str2) {
        if (isEmpty(str)) {
            return new long[0];
        }
        String[] stringArray = toStringArray(str, str2);
        long[] jArr = new long[stringArray.length];
        for (int i = 0; i < jArr.length; i++) {
            jArr[i] = Long.parseLong(stringArray[i]);
        }
        return jArr;
    }

    public static long[] toLongArray(String str) {
        return toLongArray(str, DELIMITER_STRING);
    }

    public static double[] toDoubleArray(String str, String str2) {
        if (isEmpty(str)) {
            return new double[0];
        }
        String[] stringArray = toStringArray(str, str2);
        double[] dArr = new double[stringArray.length];
        for (int i = 0; i < dArr.length; i++) {
            dArr[i] = Double.valueOf(stringArray[i]).doubleValue();
        }
        return dArr;
    }

    public static double[] toDoubleArray(String str) {
        return toDoubleArray(str, DELIMITER_STRING);
    }

    public static Color toColor(String str) {
        if (str == null) {
            return null;
        }
        if (str.charAt(0) == '#') {
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = -1;
            if (str.length() >= 7) {
                int i5 = 1;
                if (str.length() >= 9) {
                    i4 = Integer.parseInt(str.substring(1, 1 + 2), 16);
                    i5 = 1 + 2;
                }
                i = Integer.parseInt(str.substring(i5, i5 + 2), 16);
                i2 = Integer.parseInt(str.substring(i5 + 2, i5 + 4), 16);
                i3 = Integer.parseInt(str.substring(i5 + 4, i5 + 6), 16);
            } else if (str.length() >= 4) {
                int i6 = 1;
                if (str.length() >= 5) {
                    i6 = 1 + 1;
                    i4 = Integer.parseInt(str.substring(1, i6), 16) * 16;
                }
                int i7 = i6;
                int i8 = i6 + 1;
                i = Integer.parseInt(str.substring(i7, i8), 16) * 16;
                int i9 = i8 + 1;
                i2 = Integer.parseInt(str.substring(i8, i9), 16) * 16;
                i3 = Integer.parseInt(str.substring(i9, i9 + 1), 16) * 16;
            }
            if (i4 != -1) {
                return new Color(i, i2, i3, i4);
            }
            return new Color(i, i2, i3);
        }
        Field field = null;
        try {
            try {
                field = Color.class.getField(str);
            } catch (Exception e) {
            }
            if (field == null) {
                field = Color.class.getField(str.toLowerCase());
            }
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                return (Color) field.get(null);
            }
            return null;
        } catch (IllegalAccessException e2) {
            return null;
        } catch (IllegalArgumentException e3) {
            return null;
        } catch (NoSuchFieldException e4) {
            throw new IllegalArgumentException("No such color: " + str);
        } catch (SecurityException e5) {
            return null;
        }
    }

    public static String toColorString(Color color) {
        if (color == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(Integer.toHexString(color.getRGB()));
        for (int length = sb.length(); length < 8; length++) {
            sb.insert(0, '0');
        }
        if (sb.charAt(0) == 'f' && sb.charAt(1) == 'f') {
            sb.delete(0, 2);
        }
        return sb.insert(0, '#').toString();
    }

    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        char cCharAt = str.charAt(0);
        if (cCharAt != '-' && !Character.isDigit(cCharAt)) {
            return false;
        }
        for (int i = 1; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static String ensureIncludesAt(String str, String str2, int i) {
        StringBuilder sb = new StringBuilder(str);
        try {
            if (!str.substring(i, i + str2.length()).equalsIgnoreCase(str2)) {
                sb.insert(i, str2);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    static String ensureExcludesAt(String str, String str2, int i) {
        StringBuilder sb = new StringBuilder(str);
        try {
            if (!str.substring(i + 1, i + str2.length() + 1).equalsIgnoreCase(str2)) {
                sb.delete(i, i + str2.length());
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static String substring(String str, String str2, String str3, int i) {
        int iIndexOf;
        int iIndexOf2 = str.indexOf(str2, i < 0 ? 0 : i) + str2.length();
        if (iIndexOf2 < 0 || (iIndexOf = str.indexOf(str3, iIndexOf2)) < 0) {
            return null;
        }
        return str.substring(iIndexOf2, iIndexOf);
    }

    @Deprecated
    static String removeSubstring(String str, char c, char c2, int i) {
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (char c3 : str.toCharArray()) {
            if (!z) {
                if (c3 == c) {
                    z = true;
                } else {
                    sb.append(c3);
                }
            } else if (c3 == c2) {
                z = false;
            }
        }
        return sb.toString();
    }

    static String removeSubstrings(String str, char c, char c2) {
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (char c3 : str.toCharArray()) {
            if (!z) {
                if (c3 == c) {
                    z = true;
                } else {
                    sb.append(c3);
                }
            } else if (c3 == c2) {
                z = false;
            }
        }
        return sb.toString();
    }

    public static String getFirstElement(String str, String str2) {
        if (str2 == null) {
            throw new IllegalArgumentException("delimiter == null");
        }
        if (isEmpty(str)) {
            return str;
        }
        int iIndexOf = str.indexOf(str2);
        if (iIndexOf >= 0) {
            return str.substring(0, iIndexOf);
        }
        return str;
    }

    public static String getLastElement(String str, String str2) {
        if (str2 == null) {
            throw new IllegalArgumentException("delimiter == null");
        }
        if (isEmpty(str)) {
            return str;
        }
        int iLastIndexOf = str.lastIndexOf(str2);
        if (iLastIndexOf >= 0) {
            return str.substring(iLastIndexOf + 1);
        }
        return str;
    }

    public static String toCSVString(Object[] objArr) {
        return toCSVString(objArr, ", ");
    }

    public static String toCSVString(Object[] objArr, String str) {
        if (objArr == null) {
            return "";
        }
        if (str == null) {
            throw new IllegalArgumentException("delimiter == null");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objArr.length; i++) {
            if (i > 0) {
                sb.append(str);
            }
            sb.append(objArr[i]);
        }
        return sb.toString();
    }

    public static String deepToString(Object obj) {
        return deepToString(obj, false, 1);
    }

    public static String deepToString(Object obj, boolean z, int i) {
        Class<?> cls;
        if (obj == null) {
            return null;
        }
        if (!z && !isIdentityToString(obj)) {
            return obj.toString();
        }
        StringBuilder sb = new StringBuilder();
        if (obj.getClass().isArray()) {
            Class<?> componentType = obj.getClass();
            while (true) {
                cls = componentType;
                if (!cls.isArray()) {
                    break;
                }
                sb.append('[');
                sb.append(Array.getLength(obj));
                sb.append(']');
                componentType = cls.getComponentType();
            }
            sb.insert(0, cls);
            sb.append(" {hashCode=");
            sb.append(Integer.toHexString(obj.hashCode()));
            sb.append("}");
        } else {
            if (isIdentityToString(obj)) {
                sb.append(" {");
            } else {
                sb.append(" {toString=");
                sb.append(obj.toString());
                sb.append(", ");
            }
            sb.append("hashCode=");
            sb.append(Integer.toHexString(obj.hashCode()));
            for (Method method : obj.getClass().getMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String strSubstring = null;
                    if (!name.equals("getClass") && name.length() > 3 && name.startsWith("get") && Character.isUpperCase(name.charAt(3))) {
                        strSubstring = name.substring(3);
                    } else if (name.length() > 2 && name.startsWith("is") && Character.isUpperCase(name.charAt(2))) {
                        strSubstring = name.substring(2);
                    }
                    if (strSubstring != null) {
                        if (strSubstring.length() > 1 && Character.isLowerCase(strSubstring.charAt(1))) {
                            strSubstring = Character.toLowerCase(strSubstring.charAt(0)) + strSubstring.substring(1);
                        }
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        boolean z2 = parameterTypes != null && parameterTypes.length > 0;
                        if (!Void.TYPE.equals(method.getReturnType()) && !z2) {
                            try {
                                Object objInvoke = method.invoke(obj, new Object[0]);
                                sb.append(", ");
                                sb.append(strSubstring);
                                sb.append('=');
                                if (i != 0 && objInvoke != null && isIdentityToString(objInvoke)) {
                                    sb.append(deepToString(objInvoke, z, i > 0 ? i - 1 : -1));
                                } else {
                                    sb.append(objInvoke);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            sb.append('}');
            sb.insert(0, obj.getClass().getName());
        }
        return sb.toString();
    }

    private static boolean isIdentityToString(Object obj) {
        try {
            if (obj.getClass().getMethod("toString", new Class[0]).getDeclaringClass() == Object.class) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String identityToString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
    }

    public boolean matches(String str, String str2) throws PatternSyntaxException {
        return Pattern.matches(str2, str);
    }

    public String replaceFirst(String str, String str2, String str3) {
        return Pattern.compile(str2).matcher(str).replaceFirst(str3);
    }

    public String replaceAll(String str, String str2, String str3) {
        return Pattern.compile(str2).matcher(str).replaceAll(str3);
    }

    public String[] split(String str, String str2, int i) {
        return Pattern.compile(str2).split(str, i);
    }

    public String[] split(String str, String str2) {
        return split(str, str2, 0);
    }

    public static String camelToLisp(String str) {
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (str.length() == 0) {
            return str;
        }
        StringBuilder sb = null;
        int i = 0;
        boolean z = false;
        boolean z2 = false;
        for (int i2 = 1; i2 < str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (Character.isUpperCase(cCharAt)) {
                if (sb == null) {
                    sb = new StringBuilder(str.length() + 3);
                }
                if (z2) {
                    z2 = false;
                    sb.append(str.substring(i, i2));
                    if (cCharAt != '-') {
                        sb.append('-');
                    }
                    i = i2;
                } else {
                    char cCharAt2 = str.charAt(i2 - 1);
                    if (i2 == i || Character.isUpperCase(cCharAt2)) {
                        z = true;
                    } else {
                        sb.append(str.substring(i, i2).toLowerCase());
                        if (cCharAt2 != '-') {
                            sb.append('-');
                        }
                        sb.append(Character.toLowerCase(cCharAt));
                        i = i2 + 1;
                    }
                }
            } else if (Character.isDigit(cCharAt)) {
                if (sb == null) {
                    sb = new StringBuilder(str.length() + 3);
                }
                if (z) {
                    z = false;
                    sb.append(str.substring(i, i2).toLowerCase());
                    if (cCharAt != '-') {
                        sb.append('-');
                    }
                    i = i2;
                } else {
                    char cCharAt3 = str.charAt(i2 - 1);
                    if (i2 == i || Character.isDigit(cCharAt3)) {
                        z2 = true;
                    } else {
                        sb.append(str.substring(i, i2).toLowerCase());
                        if (cCharAt3 != '-') {
                            sb.append('-');
                        }
                        sb.append(Character.toLowerCase(cCharAt));
                        i = i2 + 1;
                    }
                }
            } else if (z2) {
                z2 = false;
                sb.append(str.substring(i, i2));
                if (cCharAt != '-') {
                    sb.append('-');
                }
                i = i2;
            } else if (z) {
                z = false;
                sb.append(str.substring(i, i2 - 1).toLowerCase());
                if (cCharAt != '-') {
                    sb.append('-');
                }
                i = i2 - 1;
            }
        }
        if (sb == null) {
            return Character.isUpperCase(str.charAt(0)) ? str.toLowerCase() : str;
        }
        sb.append(str.substring(i).toLowerCase());
        return sb.toString();
    }

    public static String lispToCamel(String str) {
        return lispToCamel(str, false);
    }

    public static String lispToCamel(String str, boolean z) {
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (str.length() == 0) {
            return str;
        }
        StringBuilder sb = null;
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (str.charAt(i2) == '-') {
                if (sb == null) {
                    sb = new StringBuilder(str.length() - 1);
                }
                if (i != 0 || z) {
                    sb.append(Character.toUpperCase(str.charAt(i)));
                    i++;
                }
                sb.append(str.substring(i, i2).toLowerCase());
                i = i2 + 1;
            }
        }
        if (sb != null) {
            sb.append(Character.toUpperCase(str.charAt(i)));
            sb.append(str.substring(i + 1).toLowerCase());
            return sb.toString();
        }
        if (z && !Character.isUpperCase(str.charAt(0))) {
            return capitalize(str, 0);
        }
        if (!z && Character.isUpperCase(str.charAt(0))) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
        return str;
    }

    public static String reverse(String str) {
        char[] cArr = new char[str.length()];
        str.getChars(0, cArr.length, cArr, 0);
        for (int i = 0; i < cArr.length / 2; i++) {
            char c = cArr[i];
            cArr[i] = cArr[(cArr.length - 1) - i];
            cArr[(cArr.length - 1) - i] = c;
        }
        return new String(cArr);
    }
}
