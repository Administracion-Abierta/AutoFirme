package org.apache.xml.security.encryption;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/ReferenceList.class */
public interface ReferenceList {
    public static final int DATA_REFERENCE = 1;
    public static final int KEY_REFERENCE = 2;

    void add(Reference reference);

    void remove(Reference reference);

    int size();

    boolean isEmpty();

    Iterator<Reference> getReferences();

    Reference newDataReference(String str);

    Reference newKeyReference(String str);
}
