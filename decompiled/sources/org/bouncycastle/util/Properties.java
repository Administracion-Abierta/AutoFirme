package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Properties.class */
public class Properties {
    public static final String EMULATE_ORACLE = "org.bouncycastle.emulate.oracle";
    private static final ThreadLocal threadProperties = new ThreadLocal();

    private Properties() {
    }

    public static boolean isOverrideSet(String str) {
        try {
            return isSetTrue(getPropertyValue(str));
        } catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean isOverrideSet(String str, boolean z) {
        try {
            String propertyValue = getPropertyValue(str);
            return propertyValue == null ? z : isSetTrue(propertyValue);
        } catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean isOverrideSetTo(String str, boolean z) {
        try {
            String propertyValue = getPropertyValue(str);
            return z ? isSetTrue(propertyValue) : isSetFalse(propertyValue);
        } catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean setThreadOverride(String str, boolean z) {
        boolean zIsOverrideSet = isOverrideSet(str);
        Map map = (Map) threadProperties.get();
        if (map == null) {
            map = new HashMap();
            threadProperties.set(map);
        }
        map.put(str, z ? "true" : "false");
        return zIsOverrideSet;
    }

    public static boolean removeThreadOverride(String str) {
        String str2;
        Map map = (Map) threadProperties.get();
        if (map == null || (str2 = (String) map.remove(str)) == null) {
            return false;
        }
        if (map.isEmpty()) {
            threadProperties.remove();
        }
        return "true".equals(Strings.toLowerCase(str2));
    }

    public static int asInteger(String str, int i) {
        String propertyValue = getPropertyValue(str);
        return propertyValue != null ? Integer.parseInt(propertyValue) : i;
    }

    public static BigInteger asBigInteger(String str) {
        String propertyValue = getPropertyValue(str);
        if (propertyValue != null) {
            return new BigInteger(propertyValue);
        }
        return null;
    }

    public static Set<String> asKeySet(String str) {
        HashSet hashSet = new HashSet();
        String propertyValue = getPropertyValue(str);
        if (propertyValue != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(propertyValue, ",");
            while (stringTokenizer.hasMoreElements()) {
                hashSet.add(Strings.toLowerCase(stringTokenizer.nextToken()).trim());
            }
        }
        return Collections.unmodifiableSet(hashSet);
    }

    public static String getPropertyValue(final String str) {
        String str2;
        String str3 = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.bouncycastle.util.Properties.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return Security.getProperty(str);
            }
        });
        if (str3 != null) {
            return str3;
        }
        Map map = (Map) threadProperties.get();
        return (map == null || (str2 = (String) map.get(str)) == null) ? (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.bouncycastle.util.Properties.2
            @Override // java.security.PrivilegedAction
            public Object run() {
                return System.getProperty(str);
            }
        }) : str2;
    }

    public static String getPropertyValue(String str, String str2) {
        String propertyValue = getPropertyValue(str);
        return propertyValue == null ? str2 : propertyValue;
    }

    private static boolean isSetFalse(String str) {
        if (str == null || str.length() != 5) {
            return false;
        }
        return (str.charAt(0) == 'f' || str.charAt(0) == 'F') && (str.charAt(1) == 'a' || str.charAt(1) == 'A') && ((str.charAt(2) == 'l' || str.charAt(2) == 'L') && ((str.charAt(3) == 's' || str.charAt(3) == 'S') && (str.charAt(4) == 'e' || str.charAt(4) == 'E')));
    }

    private static boolean isSetTrue(String str) {
        if (str == null || str.length() != 4) {
            return false;
        }
        return (str.charAt(0) == 't' || str.charAt(0) == 'T') && (str.charAt(1) == 'r' || str.charAt(1) == 'R') && ((str.charAt(2) == 'u' || str.charAt(2) == 'U') && (str.charAt(3) == 'e' || str.charAt(3) == 'E'));
    }
}
