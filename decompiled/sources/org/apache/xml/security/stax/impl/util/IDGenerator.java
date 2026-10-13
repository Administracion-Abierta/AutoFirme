package org.apache.xml.security.stax.impl.util;

import java.util.UUID;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/util/IDGenerator.class */
public class IDGenerator {
    public static String generateID(String prefix) {
        String id = UUID.randomUUID().toString();
        if (prefix != null) {
            return prefix + id;
        }
        return "G" + id;
    }
}
