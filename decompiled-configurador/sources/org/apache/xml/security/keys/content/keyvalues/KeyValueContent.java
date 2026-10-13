package org.apache.xml.security.keys.content.keyvalues;

import java.security.PublicKey;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/content/keyvalues/KeyValueContent.class */
public interface KeyValueContent {
    PublicKey getPublicKey() throws XMLSecurityException;
}
