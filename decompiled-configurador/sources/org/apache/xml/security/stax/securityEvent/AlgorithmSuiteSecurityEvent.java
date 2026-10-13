package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.ext.XMLSecurityConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityEvent/AlgorithmSuiteSecurityEvent.class */
public class AlgorithmSuiteSecurityEvent extends SecurityEvent {
    private int keyLength;
    private boolean derivedKey;
    private String algorithmURI;
    private XMLSecurityConstants.AlgorithmUsage algorithmUsage;

    public AlgorithmSuiteSecurityEvent() {
        super(SecurityEventConstants.AlgorithmSuite);
        this.derivedKey = false;
    }

    public int getKeyLength() {
        return this.keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public boolean isDerivedKey() {
        return this.derivedKey;
    }

    public void setDerivedKey(boolean derivedKey) {
        this.derivedKey = derivedKey;
    }

    public String getAlgorithmURI() {
        return this.algorithmURI;
    }

    public void setAlgorithmURI(String algorithmURI) {
        this.algorithmURI = algorithmURI;
    }

    public XMLSecurityConstants.AlgorithmUsage getAlgorithmUsage() {
        return this.algorithmUsage;
    }

    public void setAlgorithmUsage(XMLSecurityConstants.AlgorithmUsage algorithmUsage) {
        this.algorithmUsage = algorithmUsage;
    }
}
