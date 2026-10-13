package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/RSAPSSParameterSpec.class */
public class RSAPSSParameterSpec implements SignatureMethodParameterSpec {
    private int trailerField;
    private int saltLength;
    private String digestName;

    public int getTrailerField() {
        return this.trailerField;
    }

    public void setTrailerField(int trailerField) {
        this.trailerField = trailerField;
    }

    public int getSaltLength() {
        return this.saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public String getDigestName() {
        return this.digestName;
    }

    public void setDigestName(String digestName) {
        this.digestName = digestName;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.digestName == null ? 0 : this.digestName.hashCode());
        return (31 * ((31 * result) + this.saltLength)) + this.trailerField;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RSAPSSParameterSpec other = (RSAPSSParameterSpec) obj;
        if (this.digestName == null) {
            if (other.digestName != null) {
                return false;
            }
        } else if (!this.digestName.equals(other.digestName)) {
            return false;
        }
        return this.saltLength == other.saltLength && this.trailerField == other.trailerField;
    }
}
