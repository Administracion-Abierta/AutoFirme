package org.apache.xml.security.stax.impl;

import org.apache.xml.security.stax.ext.SecurePart;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/SignaturePartDef.class */
public class SignaturePartDef {
    private SecurePart securePart;
    private String sigRefId;
    private String digestValue;
    private String[] transforms;
    private String digestAlgo;
    private String inclusiveNamespacesPrefixes;
    private boolean excludeVisibleC14Nprefixes;
    private boolean externalResource;
    private boolean generateXPointer;

    public SecurePart getSecurePart() {
        return this.securePart;
    }

    public void setSecurePart(SecurePart securePart) {
        this.securePart = securePart;
    }

    public String getSigRefId() {
        return this.sigRefId;
    }

    public void setSigRefId(String sigRefId) {
        this.sigRefId = sigRefId;
    }

    public String getDigestValue() {
        return this.digestValue;
    }

    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    public String[] getTransforms() {
        return this.transforms;
    }

    public void setTransforms(String[] transforms) {
        this.transforms = transforms;
    }

    public String getDigestAlgo() {
        return this.digestAlgo;
    }

    public void setDigestAlgo(String digestAlgo) {
        this.digestAlgo = digestAlgo;
    }

    public String getInclusiveNamespacesPrefixes() {
        return this.inclusiveNamespacesPrefixes;
    }

    public void setInclusiveNamespacesPrefixes(String inclusiveNamespacesPrefixes) {
        this.inclusiveNamespacesPrefixes = inclusiveNamespacesPrefixes;
    }

    public boolean isExcludeVisibleC14Nprefixes() {
        return this.excludeVisibleC14Nprefixes;
    }

    public void setExcludeVisibleC14Nprefixes(boolean excludeVisibleC14Nprefixes) {
        this.excludeVisibleC14Nprefixes = excludeVisibleC14Nprefixes;
    }

    public boolean isExternalResource() {
        return this.externalResource;
    }

    public void setExternalResource(boolean externalResource) {
        this.externalResource = externalResource;
    }

    public boolean isGenerateXPointer() {
        return this.generateXPointer;
    }

    public void setGenerateXPointer(boolean generateXPointer) {
        this.generateXPointer = generateXPointer;
    }
}
