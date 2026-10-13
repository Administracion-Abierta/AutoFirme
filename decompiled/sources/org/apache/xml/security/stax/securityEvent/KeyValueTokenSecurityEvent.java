package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityToken.SecurityToken;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/KeyValueTokenSecurityEvent.class */
public class KeyValueTokenSecurityEvent extends TokenSecurityEvent<SecurityToken> {

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/KeyValueTokenSecurityEvent$KeyValueTokenType.class */
    public enum KeyValueTokenType {
        RSA,
        DSA,
        EC
    }

    public KeyValueTokenSecurityEvent() {
        super(SecurityEventConstants.KeyValueToken);
    }

    public KeyValueTokenType getKeyValueTokenType() {
        try {
            String algo = getSecurityToken().getPublicKey().getAlgorithm();
            return KeyValueTokenType.valueOf(algo);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (XMLSecurityException e2) {
            return null;
        }
    }
}
