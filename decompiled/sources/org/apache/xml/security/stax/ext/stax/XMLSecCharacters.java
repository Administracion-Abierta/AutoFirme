package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.Characters;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/stax/XMLSecCharacters.class */
public interface XMLSecCharacters extends XMLSecEvent, Characters {
    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asCharacters, reason: merged with bridge method [inline-methods] */
    XMLSecCharacters mo1374asCharacters();

    char[] getText();
}
