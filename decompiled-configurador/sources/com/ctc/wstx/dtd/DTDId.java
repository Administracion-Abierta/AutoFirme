package com.ctc.wstx.dtd;

import java.net.URI;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDId.class */
public final class DTDId {
    protected final String mPublicId;
    protected final URI mSystemId;
    protected final int mConfigFlags;
    protected final boolean mXml11;
    protected int mHashCode = 0;

    private DTDId(String publicId, URI systemId, int configFlags, boolean xml11) {
        this.mPublicId = publicId;
        this.mSystemId = systemId;
        this.mConfigFlags = configFlags;
        this.mXml11 = xml11;
    }

    public static DTDId constructFromPublicId(String publicId, int configFlags, boolean xml11) {
        if (publicId == null || publicId.length() == 0) {
            throw new IllegalArgumentException("Empty/null public id.");
        }
        return new DTDId(publicId, null, configFlags, xml11);
    }

    public static DTDId constructFromSystemId(URI systemId, int configFlags, boolean xml11) {
        if (systemId == null) {
            throw new IllegalArgumentException("Null system id.");
        }
        return new DTDId(null, systemId, configFlags, xml11);
    }

    public static DTDId construct(String publicId, URI systemId, int configFlags, boolean xml11) {
        if (publicId != null && publicId.length() > 0) {
            return new DTDId(publicId, null, configFlags, xml11);
        }
        if (systemId == null) {
            throw new IllegalArgumentException("Illegal arguments; both public and system id null/empty.");
        }
        return new DTDId(null, systemId, configFlags, xml11);
    }

    public int hashCode() {
        int hash = this.mHashCode;
        if (hash == 0) {
            int hash2 = this.mConfigFlags;
            if (this.mPublicId != null) {
                hash = hash2 ^ this.mPublicId.hashCode();
            } else {
                hash = hash2 ^ this.mSystemId.hashCode();
            }
            if (this.mXml11) {
                hash ^= 1;
            }
            this.mHashCode = hash;
        }
        return hash;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(60);
        sb.append("Public-id: ");
        sb.append(this.mPublicId);
        sb.append(", system-id: ");
        sb.append(this.mSystemId);
        sb.append(" [config flags: 0x");
        sb.append(Integer.toHexString(this.mConfigFlags));
        sb.append("], xml11: ");
        sb.append(this.mXml11);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        DTDId other = (DTDId) o;
        if (other.mConfigFlags != this.mConfigFlags || other.mXml11 != this.mXml11) {
            return false;
        }
        if (this.mPublicId != null) {
            String op = other.mPublicId;
            return op != null && op.equals(this.mPublicId);
        }
        return this.mSystemId.equals(other.mSystemId);
    }
}
