package org.spongycastle.util;

import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/Properties.class */
public class Properties {
    private static final ThreadLocal threadProperties = new ThreadLocal();

    private Properties() {
    }

    public static boolean isOverrideSet(String propertyName) {
        try {
            String p = fetchProperty(propertyName);
            if (p != null) {
                return "true".equals(Strings.toLowerCase(p));
            }
            return false;
        } catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean setThreadOverride(String propertyName, boolean enable) {
        boolean isSet = isOverrideSet(propertyName);
        Map localProps = (Map) threadProperties.get();
        if (localProps == null) {
            localProps = new HashMap();
        }
        localProps.put(propertyName, enable ? "true" : "false");
        threadProperties.set(localProps);
        return isSet;
    }

    public static boolean removeThreadOverride(String propertyName) {
        boolean isSet = isOverrideSet(propertyName);
        Map localProps = (Map) threadProperties.get();
        if (localProps == null) {
            return false;
        }
        localProps.remove(propertyName);
        if (localProps.isEmpty()) {
            threadProperties.remove();
        } else {
            threadProperties.set(localProps);
        }
        return isSet;
    }

    public static BigInteger asBigInteger(String propertyName) {
        String p = fetchProperty(propertyName);
        if (p != null) {
            return new BigInteger(p);
        }
        return null;
    }

    public static Set<String> asKeySet(String propertyName) {
        Set<String> set = new HashSet<>();
        String p = fetchProperty(propertyName);
        if (p != null) {
            StringTokenizer sTok = new StringTokenizer(p, ",");
            while (sTok.hasMoreElements()) {
                set.add(Strings.toLowerCase(sTok.nextToken()).trim());
            }
        }
        return Collections.unmodifiableSet(set);
    }

    private static String fetchProperty(final String propertyName) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.spongycastle.util.Properties.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                Map localProps = (Map) Properties.threadProperties.get();
                if (localProps != null) {
                    return localProps.get(propertyName);
                }
                return System.getProperty(propertyName);
            }
        });
    }
}
