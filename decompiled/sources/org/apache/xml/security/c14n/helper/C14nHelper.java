package org.apache.xml.security.c14n.helper;

import org.w3c.dom.Attr;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/helper/C14nHelper.class */
public final class C14nHelper {
    private C14nHelper() {
    }

    public static boolean namespaceIsRelative(Attr namespace) {
        return !namespaceIsAbsolute(namespace);
    }

    public static boolean namespaceIsRelative(String namespaceValue) {
        return !namespaceIsAbsolute(namespaceValue);
    }

    public static boolean namespaceIsAbsolute(Attr namespace) {
        return namespaceIsAbsolute(namespace.getValue());
    }

    public static boolean namespaceIsAbsolute(String namespaceValue) {
        return namespaceValue.length() == 0 || namespaceValue.indexOf(58) > 0;
    }
}
