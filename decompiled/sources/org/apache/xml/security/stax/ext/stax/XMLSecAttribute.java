package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.Attribute;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/stax/XMLSecAttribute.class */
public interface XMLSecAttribute extends XMLSecEvent, Attribute, Comparable<XMLSecAttribute> {
    XMLSecNamespace getAttributeNamespace();
}
