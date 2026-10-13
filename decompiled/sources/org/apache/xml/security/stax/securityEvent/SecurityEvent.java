package org.apache.xml.security.stax.securityEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/SecurityEvent.class */
public abstract class SecurityEvent {
    private final SecurityEventConstants.Event securityEventType;
    private String correlationID;

    protected SecurityEvent(SecurityEventConstants.Event securityEventType) {
        this.securityEventType = securityEventType;
    }

    public SecurityEventConstants.Event getSecurityEventType() {
        return this.securityEventType;
    }

    public String getCorrelationID() {
        return this.correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }
}
