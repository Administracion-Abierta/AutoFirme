package org.apache.xml.security.stax.impl.util;

import java.util.UUID;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/util/IDGenerator.class */
public class IDGenerator {
    public static String generateID(String prefix) {
        String id = UUID.randomUUID().toString();
        if (prefix != null) {
            return prefix + id;
        }
        return Constants._TAG_G + id;
    }
}
