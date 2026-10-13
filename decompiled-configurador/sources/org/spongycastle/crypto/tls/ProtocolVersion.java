package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/ProtocolVersion.class */
public final class ProtocolVersion {
    public static final ProtocolVersion SSLv3 = new ProtocolVersion(768, "SSL 3.0");
    public static final ProtocolVersion TLSv10 = new ProtocolVersion(769, "TLS 1.0");
    public static final ProtocolVersion TLSv11 = new ProtocolVersion(770, "TLS 1.1");
    public static final ProtocolVersion TLSv12 = new ProtocolVersion(771, "TLS 1.2");
    public static final ProtocolVersion DTLSv10 = new ProtocolVersion(65279, "DTLS 1.0");
    public static final ProtocolVersion DTLSv12 = new ProtocolVersion(65277, "DTLS 1.2");
    private int version;
    private String name;

    private ProtocolVersion(int v, String name) {
        this.version = v & 65535;
        this.name = name;
    }

    public int getFullVersion() {
        return this.version;
    }

    public int getMajorVersion() {
        return this.version >> 8;
    }

    public int getMinorVersion() {
        return this.version & 255;
    }

    public boolean isDTLS() {
        return getMajorVersion() == 254;
    }

    public boolean isSSL() {
        return this == SSLv3;
    }

    public boolean isTLS() {
        return getMajorVersion() == 3;
    }

    public ProtocolVersion getEquivalentTLSVersion() {
        if (!isDTLS()) {
            return this;
        }
        if (this == DTLSv10) {
            return TLSv11;
        }
        return TLSv12;
    }

    public boolean isEqualOrEarlierVersionOf(ProtocolVersion version) {
        if (getMajorVersion() != version.getMajorVersion()) {
            return false;
        }
        int diffMinorVersion = version.getMinorVersion() - getMinorVersion();
        if (isDTLS()) {
            return diffMinorVersion <= 0;
        }
        return diffMinorVersion >= 0;
    }

    public boolean isLaterVersionOf(ProtocolVersion version) {
        if (getMajorVersion() != version.getMajorVersion()) {
            return false;
        }
        int diffMinorVersion = version.getMinorVersion() - getMinorVersion();
        if (isDTLS()) {
            return diffMinorVersion > 0;
        }
        return diffMinorVersion < 0;
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof ProtocolVersion) && equals((ProtocolVersion) other));
    }

    public boolean equals(ProtocolVersion other) {
        return other != null && this.version == other.version;
    }

    public int hashCode() {
        return this.version;
    }

    public static ProtocolVersion get(int major, int minor) throws IOException {
        switch (major) {
            case 3:
                switch (minor) {
                    case 0:
                        return SSLv3;
                    case 1:
                        return TLSv10;
                    case 2:
                        return TLSv11;
                    case 3:
                        return TLSv12;
                    default:
                        return getUnknownVersion(major, minor, "TLS");
                }
            case 254:
                switch (minor) {
                    case 253:
                        return DTLSv12;
                    case 254:
                        throw new TlsFatalAlert((short) 47);
                    case 255:
                        return DTLSv10;
                    default:
                        return getUnknownVersion(major, minor, "DTLS");
                }
            default:
                throw new TlsFatalAlert((short) 47);
        }
    }

    public String toString() {
        return this.name;
    }

    private static ProtocolVersion getUnknownVersion(int major, int minor, String prefix) throws IOException {
        TlsUtils.checkUint8(major);
        TlsUtils.checkUint8(minor);
        int v = (major << 8) | minor;
        String hex = Strings.toUpperCase(Integer.toHexString(65536 | v).substring(1));
        return new ProtocolVersion(v, prefix + " 0x" + hex);
    }
}
