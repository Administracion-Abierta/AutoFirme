package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Secur32.class */
public interface Secur32 extends StdCallLibrary {
    public static final Secur32 INSTANCE = (Secur32) Native.load("Secur32", Secur32.class, W32APIOptions.DEFAULT_OPTIONS);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Secur32$EXTENDED_NAME_FORMAT.class */
    public static abstract class EXTENDED_NAME_FORMAT {
        public static final int NameUnknown = 0;
        public static final int NameFullyQualifiedDN = 1;
        public static final int NameSamCompatible = 2;
        public static final int NameDisplay = 3;
        public static final int NameUniqueId = 6;
        public static final int NameCanonical = 7;
        public static final int NameUserPrincipal = 8;
        public static final int NameCanonicalEx = 9;
        public static final int NameServicePrincipal = 10;
        public static final int NameDnsDomain = 12;
    }

    boolean GetUserNameEx(int i, char[] cArr, IntByReference intByReference);

    int AcquireCredentialsHandle(String str, String str2, int i, WinNT.LUID luid, Pointer pointer, Pointer pointer2, Pointer pointer3, Sspi.CredHandle credHandle, Sspi.TimeStamp timeStamp);

    int InitializeSecurityContext(Sspi.CredHandle credHandle, Sspi.CtxtHandle ctxtHandle, String str, int i, int i2, int i3, Sspi.SecBufferDesc secBufferDesc, int i4, Sspi.CtxtHandle ctxtHandle2, Sspi.SecBufferDesc secBufferDesc2, IntByReference intByReference, Sspi.TimeStamp timeStamp);

    int DeleteSecurityContext(Sspi.CtxtHandle ctxtHandle);

    int FreeCredentialsHandle(Sspi.CredHandle credHandle);

    int AcceptSecurityContext(Sspi.CredHandle credHandle, Sspi.CtxtHandle ctxtHandle, Sspi.SecBufferDesc secBufferDesc, int i, int i2, Sspi.CtxtHandle ctxtHandle2, Sspi.SecBufferDesc secBufferDesc2, IntByReference intByReference, Sspi.TimeStamp timeStamp);

    int CompleteAuthToken(Sspi.CtxtHandle ctxtHandle, Sspi.SecBufferDesc secBufferDesc);

    int EnumerateSecurityPackages(IntByReference intByReference, Sspi.PSecPkgInfo pSecPkgInfo);

    int FreeContextBuffer(Pointer pointer);

    int QuerySecurityContextToken(Sspi.CtxtHandle ctxtHandle, WinNT.HANDLEByReference hANDLEByReference);

    int ImpersonateSecurityContext(Sspi.CtxtHandle ctxtHandle);

    int RevertSecurityContext(Sspi.CtxtHandle ctxtHandle);

    int QueryContextAttributes(Sspi.CtxtHandle ctxtHandle, int i, Structure structure);

    int QueryCredentialsAttributes(Sspi.CredHandle credHandle, int i, Structure structure);

    int QuerySecurityPackageInfo(String str, Sspi.PSecPkgInfo pSecPkgInfo);

    int EncryptMessage(Sspi.CtxtHandle ctxtHandle, int i, Sspi.SecBufferDesc secBufferDesc, int i2);

    int VerifySignature(Sspi.CtxtHandle ctxtHandle, Sspi.SecBufferDesc secBufferDesc, int i, IntByReference intByReference);

    int MakeSignature(Sspi.CtxtHandle ctxtHandle, int i, Sspi.SecBufferDesc secBufferDesc, int i2);

    int DecryptMessage(Sspi.CtxtHandle ctxtHandle, Sspi.SecBufferDesc secBufferDesc, int i, IntByReference intByReference);
}
