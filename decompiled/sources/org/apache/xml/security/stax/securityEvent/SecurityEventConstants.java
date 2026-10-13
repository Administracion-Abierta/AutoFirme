package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.ext.ComparableType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/SecurityEventConstants.class */
public class SecurityEventConstants {
    public static final Event SignatureValue = new Event("SignatureValue");
    public static final Event SignedElement = new Event("SignedElement");
    public static final Event KeyValueToken = new Event("KeyValueToken");
    public static final Event KeyNameToken = new Event("KeyNameToken");
    public static final Event X509Token = new Event("X509Token");
    public static final Event AlgorithmSuite = new Event("AlgorithmSuite");
    public static final Event DefaultToken = new Event("DefaultToken");
    public static final Event ContentEncrypted = new Event("ContentEncrypted");
    public static final Event EncryptedElement = new Event("EncryptedElement");
    public static final Event EncryptedKeyToken = new Event("EncryptedKeyToken");

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/SecurityEventConstants$Event.class */
    public static class Event extends ComparableType<Event> {
        public Event(String name) {
            super(name);
        }
    }
}
