package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APITypeMapper;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi.class */
public interface Sspi {
    public static final int MAX_TOKEN_SIZE;
    public static final int SECPKG_CRED_INBOUND = 1;
    public static final int SECPKG_CRED_OUTBOUND = 2;
    public static final int SECURITY_NATIVE_DREP = 16;
    public static final int SECURITY_NETWORK_DREP = 0;
    public static final int ISC_REQ_ALLOCATE_MEMORY = 256;
    public static final int ISC_REQ_CONFIDENTIALITY = 16;
    public static final int ISC_REQ_CONNECTION = 2048;
    public static final int ISC_REQ_DELEGATE = 1;
    public static final int ISC_REQ_EXTENDED_ERROR = 16384;
    public static final int ISC_REQ_INTEGRITY = 65536;
    public static final int ISC_REQ_MUTUAL_AUTH = 2;
    public static final int ISC_REQ_REPLAY_DETECT = 4;
    public static final int ISC_REQ_SEQUENCE_DETECT = 8;
    public static final int ISC_REQ_STREAM = 32768;
    public static final int SECBUFFER_VERSION = 0;
    public static final int SECBUFFER_EMPTY = 0;
    public static final int SECBUFFER_DATA = 1;
    public static final int SECBUFFER_TOKEN = 2;
    public static final int SECPKG_ATTR_SIZES = 0;
    public static final int SECPKG_ATTR_NAMES = 1;
    public static final int SECPKG_ATTR_LIFESPAN = 2;
    public static final int SECPKG_ATTR_DCE_INFO = 3;
    public static final int SECPKG_ATTR_STREAM_SIZES = 4;
    public static final int SECPKG_ATTR_KEY_INFO = 5;
    public static final int SECPKG_ATTR_AUTHORITY = 6;
    public static final int SECPKG_ATTR_PROTO_INFO = 7;
    public static final int SECPKG_ATTR_PASSWORD_EXPIRY = 8;
    public static final int SECPKG_ATTR_SESSION_KEY = 9;
    public static final int SECPKG_ATTR_PACKAGE_INFO = 10;
    public static final int SECPKG_ATTR_USER_FLAGS = 11;
    public static final int SECPKG_ATTR_NEGOTIATION_INFO = 12;
    public static final int SECPKG_ATTR_NATIVE_NAMES = 13;
    public static final int SECPKG_ATTR_FLAGS = 14;
    public static final int SECPKG_ATTR_USE_VALIDATED = 15;
    public static final int SECPKG_ATTR_CREDENTIAL_NAME = 16;
    public static final int SECPKG_ATTR_TARGET_INFORMATION = 17;
    public static final int SECPKG_ATTR_ACCESS_TOKEN = 18;
    public static final int SECPKG_ATTR_TARGET = 19;
    public static final int SECPKG_ATTR_AUTHENTICATION_ID = 20;
    public static final int SECPKG_ATTR_LOGOFF_TIME = 21;
    public static final int SECPKG_ATTR_NEGO_KEYS = 22;
    public static final int SECPKG_ATTR_PROMPTING_NEEDED = 24;
    public static final int SECPKG_ATTR_UNIQUE_BINDINGS = 25;
    public static final int SECPKG_ATTR_ENDPOINT_BINDINGS = 26;
    public static final int SECPKG_ATTR_CLIENT_SPECIFIED_TARGET = 27;
    public static final int SECPKG_ATTR_LAST_CLIENT_TOKEN_STATUS = 30;
    public static final int SECPKG_ATTR_NEGO_PKG_INFO = 31;
    public static final int SECPKG_ATTR_NEGO_STATUS = 32;
    public static final int SECPKG_ATTR_CONTEXT_DELETED = 33;
    public static final int SECPKG_ATTR_SUBJECT_SECURITY_ATTRIBUTES = 128;
    public static final int SECPKG_NEGOTIATION_COMPLETE = 0;
    public static final int SECPKG_NEGOTIATION_OPTIMISTIC = 1;
    public static final int SECPKG_NEGOTIATION_IN_PROGRESS = 2;
    public static final int SECPKG_NEGOTIATION_DIRECT = 3;
    public static final int SECPKG_NEGOTIATION_TRY_MULTICRED = 4;
    public static final int SECPKG_FLAG_INTEGRITY = 1;
    public static final int SECPKG_FLAG_PRIVACY = 2;
    public static final int SECPKG_FLAG_TOKEN_ONLY = 4;
    public static final int SECPKG_FLAG_DATAGRAM = 8;
    public static final int SECPKG_FLAG_CONNECTION = 16;
    public static final int SECPKG_FLAG_MULTI_REQUIRED = 32;
    public static final int SECPKG_FLAG_CLIENT_ONLY = 64;
    public static final int SECPKG_FLAG_EXTENDED_ERROR = 128;
    public static final int SECPKG_FLAG_IMPERSONATION = 256;
    public static final int SECPKG_FLAG_ACCEPT_WIN32_NAME = 512;
    public static final int SECPKG_FLAG_STREAM = 1024;
    public static final int SECPKG_FLAG_NEGOTIABLE = 2048;
    public static final int SECPKG_FLAG_GSS_COMPATIBLE = 4096;
    public static final int SECPKG_FLAG_LOGON = 8192;
    public static final int SECPKG_FLAG_ASCII_BUFFERS = 16384;
    public static final int SECPKG_FLAG_FRAGMENT = 32768;
    public static final int SECPKG_FLAG_MUTUAL_AUTH = 65536;
    public static final int SECPKG_FLAG_DELEGATION = 131072;
    public static final int SECPKG_FLAG_RESTRICTED_TOKENS = 524288;
    public static final int SECPKG_FLAG_NEGO_EXTENDER = 1048576;
    public static final int SECPKG_FLAG_NEGOTIABLE2 = 2097152;
    public static final int SECPKG_FLAG_APPCONTAINER_PASSTHROUGH = 4194304;
    public static final int SECPKG_FLAG_APPCONTAINER_CHECKS = 8388608;
    public static final int SECPKG_CRED_ATTR_NAMES = 1;
    public static final int SECQOP_WRAP_NO_ENCRYPT = -2147483647;
    public static final int SECQOP_WRAP_OOB_DATA = 1073741824;
    public static final int SEC_WINNT_AUTH_IDENTITY_ANSI = 1;
    public static final int SEC_WINNT_AUTH_IDENTITY_UNICODE = 2;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$CredHandle.class */
    public static class CredHandle extends SecHandle {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$CtxtHandle.class */
    public static class CtxtHandle extends SecHandle {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SECURITY_INTEGER.class */
    @Structure.FieldOrder({"dwLower", "dwUpper"})
    public static class SECURITY_INTEGER extends Structure {
        public int dwLower;
        public int dwUpper;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecBufferDesc.class */
    @Structure.FieldOrder({"ulVersion", "cBuffers", "pBuffers"})
    public static class SecBufferDesc extends Structure {
        public int ulVersion = 0;
        public int cBuffers = 1;
        public Pointer pBuffers;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$TimeStamp.class */
    public static class TimeStamp extends SECURITY_INTEGER {
    }

    static {
        MAX_TOKEN_SIZE = VersionHelpers.IsWindows8OrGreater() ? 48000 : 12000;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecHandle.class */
    @Structure.FieldOrder({"dwLower", "dwUpper"})
    public static class SecHandle extends Structure {
        public Pointer dwLower;
        public Pointer dwUpper;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecHandle$ByReference.class */
        public static class ByReference extends SecHandle implements Structure.ByReference {
        }

        public boolean isNull() {
            return this.dwLower == null && this.dwUpper == null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$PSecHandle.class */
    @Structure.FieldOrder({"secHandle"})
    public static class PSecHandle extends Structure {
        public SecHandle.ByReference secHandle;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$PSecHandle$ByReference.class */
        public static class ByReference extends PSecHandle implements Structure.ByReference {
        }

        public PSecHandle() {
        }

        public PSecHandle(SecHandle h) {
            super(h.getPointer());
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecBuffer.class */
    @Structure.FieldOrder({"cbBuffer", "BufferType", "pvBuffer"})
    public static class SecBuffer extends Structure {
        public int cbBuffer;
        public int BufferType;
        public Pointer pvBuffer;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecBuffer$ByReference.class */
        public static class ByReference extends SecBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(int type, int size) {
                super(type, size);
            }

            public ByReference(int type, byte[] token) {
                super(type, token);
            }
        }

        public SecBuffer() {
            this.BufferType = 0;
        }

        public SecBuffer(int type, int size) {
            this.BufferType = 0;
            this.cbBuffer = size;
            this.pvBuffer = new Memory(size);
            this.BufferType = type;
        }

        public SecBuffer(int type, byte[] token) {
            this.BufferType = 0;
            this.cbBuffer = token.length;
            this.pvBuffer = new Memory(token.length);
            this.pvBuffer.write(0L, token, 0, token.length);
            this.BufferType = type;
        }

        public byte[] getBytes() {
            if (this.pvBuffer == null) {
                return null;
            }
            return this.pvBuffer.getByteArray(0L, this.cbBuffer);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$PSecPkgInfo.class */
    @Structure.FieldOrder({"pPkgInfo"})
    public static class PSecPkgInfo extends Structure {
        public SecPkgInfo.ByReference pPkgInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$PSecPkgInfo$ByReference.class */
        public static class ByReference extends PSecPkgInfo implements Structure.ByReference {
            @Override // com.sun.jna.platform.win32.Sspi.PSecPkgInfo, com.sun.jna.Structure
            public /* bridge */ /* synthetic */ Structure[] toArray(int i) {
                return super.toArray(i);
            }
        }

        @Override // com.sun.jna.Structure
        public SecPkgInfo.ByReference[] toArray(int size) {
            return (SecPkgInfo.ByReference[]) this.pPkgInfo.toArray(size);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgInfo.class */
    @Structure.FieldOrder({"fCapabilities", "wVersion", "wRPCID", "cbMaxToken", "Name", "Comment"})
    public static class SecPkgInfo extends Structure {
        public int fCapabilities;
        public short wVersion;
        public short wRPCID;
        public int cbMaxToken;
        public String Name;
        public String Comment;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgInfo$ByReference.class */
        public static class ByReference extends SecPkgInfo implements Structure.ByReference {
        }

        public SecPkgInfo() {
            super(W32APITypeMapper.DEFAULT);
            this.wVersion = (short) 1;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_PackageInfo.class */
    @Structure.FieldOrder({"PackageInfo"})
    public static class SecPkgContext_PackageInfo extends Structure {
        public SecPkgInfo.ByReference PackageInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_PackageInfo$ByReference.class */
        public static class ByReference extends SecPkgContext_PackageInfo implements Structure.ByReference {
        }

        public SecPkgContext_PackageInfo() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgCredentials_Names.class */
    @Structure.FieldOrder({"sUserName"})
    public static class SecPkgCredentials_Names extends Structure {
        public Pointer sUserName;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgCredentials_Names$ByReference.class */
        public static class ByReference extends SecPkgCredentials_Names implements Structure.ByReference {
        }

        public SecPkgCredentials_Names() {
            super(W32APITypeMapper.DEFAULT);
        }

        public synchronized String getUserName() {
            if (this.sUserName == null) {
                return null;
            }
            return Boolean.getBoolean("w32.ascii") ? this.sUserName.getString(0L) : this.sUserName.getWideString(0L);
        }

        public synchronized int free() {
            if (this.sUserName != null) {
                int result = Secur32.INSTANCE.FreeContextBuffer(this.sUserName);
                this.sUserName = null;
                return result;
            }
            return 0;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_Sizes.class */
    @Structure.FieldOrder({"cbMaxToken", "cbMaxSignature", "cbBlockSize", "cbSecurityTrailer"})
    public static class SecPkgContext_Sizes extends Structure {
        public int cbMaxToken;
        public int cbMaxSignature;
        public int cbBlockSize;
        public int cbSecurityTrailer;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_Sizes$ByReference.class */
        public static class ByReference extends SecPkgContext_Sizes implements Structure.ByReference {
        }

        public SecPkgContext_Sizes() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override // com.sun.jna.Structure
        public String toString() {
            return "SecPkgContext_Sizes{cbMaxToken=" + this.cbMaxToken + ", cbMaxSignature=" + this.cbMaxSignature + ", cbBlockSize=" + this.cbBlockSize + ", cbSecurityTrailer=" + this.cbSecurityTrailer + '}';
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_SessionKey.class */
    @Structure.FieldOrder({"SessionKeyLength", "SessionKey"})
    public static class SecPkgContext_SessionKey extends Structure {
        public int SessionKeyLength;
        public Pointer SessionKey;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_SessionKey$ByReference.class */
        public static class ByReference extends SecPkgContext_SessionKey implements Structure.ByReference {
        }

        public SecPkgContext_SessionKey() {
            super(W32APITypeMapper.DEFAULT);
        }

        public byte[] getSessionKey() {
            if (this.SessionKey == null) {
                return null;
            }
            return this.SessionKey.getByteArray(0L, this.SessionKeyLength);
        }

        public synchronized void free() {
            if (this.SessionKey != null) {
                Secur32.INSTANCE.FreeContextBuffer(this.SessionKey);
                this.SessionKey = null;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_KeyInfo.class */
    @Structure.FieldOrder({"sSignatureAlgorithmName", "sEncryptAlgorithmName", EncryptionConstants._TAG_KEYSIZE, "SignatureAlgorithm", "EncryptAlgorithm"})
    public static class SecPkgContext_KeyInfo extends Structure {
        public Pointer sSignatureAlgorithmName;
        public Pointer sEncryptAlgorithmName;
        public int KeySize;
        public int SignatureAlgorithm;
        public int EncryptAlgorithm;

        public SecPkgContext_KeyInfo() {
            super(W32APITypeMapper.DEFAULT);
        }

        public synchronized String getSignatureAlgorithmName() {
            if (this.sSignatureAlgorithmName == null) {
                return null;
            }
            return Boolean.getBoolean("w32.ascii") ? this.sSignatureAlgorithmName.getString(0L) : this.sSignatureAlgorithmName.getWideString(0L);
        }

        public synchronized String getEncryptAlgorithmName() {
            if (this.sEncryptAlgorithmName == null) {
                return null;
            }
            return Boolean.getBoolean("w32.ascii") ? this.sEncryptAlgorithmName.getString(0L) : this.sEncryptAlgorithmName.getWideString(0L);
        }

        public synchronized void free() {
            if (this.sSignatureAlgorithmName != null) {
                Secur32.INSTANCE.FreeContextBuffer(this.sSignatureAlgorithmName);
                this.sSignatureAlgorithmName = null;
            }
            if (this.sEncryptAlgorithmName != null) {
                Secur32.INSTANCE.FreeContextBuffer(this.sEncryptAlgorithmName);
                this.sEncryptAlgorithmName = null;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_Lifespan.class */
    @Structure.FieldOrder({"tsStart", "tsExpiry"})
    public static class SecPkgContext_Lifespan extends Structure {
        public TimeStamp tsStart;
        public TimeStamp tsExpiry;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_Lifespan$ByReference.class */
        public static class ByReference extends SecPkgContext_Lifespan implements Structure.ByReference {
        }

        public SecPkgContext_Lifespan() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_NegotiationInfo.class */
    @Structure.FieldOrder({"PackageInfo", "NegotiationState"})
    public static class SecPkgContext_NegotiationInfo extends Structure {
        public PSecPkgInfo PackageInfo;
        public int NegotiationState;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_NegotiationInfo$ByReference.class */
        public static class ByReference extends SecPkgContext_NegotiationInfo implements Structure.ByReference {
        }

        public SecPkgContext_NegotiationInfo() {
            super(W32APITypeMapper.DEFAULT);
        }

        public synchronized void free() {
            if (this.PackageInfo != null) {
                Secur32.INSTANCE.FreeContextBuffer(this.PackageInfo.pPkgInfo.getPointer());
                this.PackageInfo = null;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_Flags.class */
    @Structure.FieldOrder({"Flags"})
    public static class SecPkgContext_Flags extends Structure {
        public int Flags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SecPkgContext_Flags$ByReference.class */
        public static class ByReference extends SecPkgContext_Flags implements Structure.ByReference {
        }

        public SecPkgContext_Flags() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Sspi$SEC_WINNT_AUTH_IDENTITY.class */
    @Structure.FieldOrder({"User", "UserLength", "Domain", "DomainLength", "Password", "PasswordLength", "Flags"})
    public static class SEC_WINNT_AUTH_IDENTITY extends Structure {
        public String User;
        public int UserLength;
        public String Domain;
        public int DomainLength;
        public String Password;
        public int PasswordLength;
        public int Flags;

        public SEC_WINNT_AUTH_IDENTITY() {
            super(W32APITypeMapper.UNICODE);
            this.Flags = 2;
        }

        @Override // com.sun.jna.Structure
        public void write() {
            this.UserLength = this.User == null ? 0 : this.User.length();
            this.DomainLength = this.Domain == null ? 0 : this.Domain.length();
            this.PasswordLength = this.Password == null ? 0 : this.Password.length();
            super.write();
        }
    }
}
