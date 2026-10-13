package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/SecurityUtilities.class */
public class SecurityUtilities {
    public static String getSystemProperty(final String name) {
        return (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.mozilla.javascript.SecurityUtilities.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public String run() {
                return System.getProperty(name);
            }
        });
    }

    public static ProtectionDomain getProtectionDomain(final Class<?> clazz) {
        return (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() { // from class: org.mozilla.javascript.SecurityUtilities.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public ProtectionDomain run() {
                return clazz.getProtectionDomain();
            }
        });
    }

    public static ProtectionDomain getScriptProtectionDomain() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager instanceof RhinoSecurityManager) {
            return (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() { // from class: org.mozilla.javascript.SecurityUtilities.3
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public ProtectionDomain run() {
                    Class<?> c = ((RhinoSecurityManager) securityManager).getCurrentScriptClass();
                    if (c == null) {
                        return null;
                    }
                    return c.getProtectionDomain();
                }
            });
        }
        return null;
    }
}
