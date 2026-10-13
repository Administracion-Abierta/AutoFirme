package org.apache.xml.security.stax.ext;

import java.util.List;
import java.util.Map;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/SecurityContext.class */
public interface SecurityContext extends SecurityEventListener {
    <T> void put(String str, T t);

    <T> T get(String str);

    <T> T remove(String str);

    <T extends List> void putList(Object obj, T t);

    <T> void putAsList(Object obj, T t);

    <T> List<T> getAsList(Object obj);

    <T, U> void putAsMap(Object obj, T t, U u);

    <T, U> Map<T, U> getAsMap(Object obj);

    void addSecurityEventListener(SecurityEventListener securityEventListener);
}
