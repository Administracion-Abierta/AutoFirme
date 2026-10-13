package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Crypt32.class */
public interface Crypt32 extends StdCallLibrary {
    public static final Crypt32 INSTANCE = (Crypt32) Native.load("Crypt32", Crypt32.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean CryptProtectData(WinCrypt.DATA_BLOB data_blob, String str, WinCrypt.DATA_BLOB data_blob2, Pointer pointer, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT cryptprotect_promptstruct, int i, WinCrypt.DATA_BLOB data_blob3);

    boolean CryptUnprotectData(WinCrypt.DATA_BLOB data_blob, PointerByReference pointerByReference, WinCrypt.DATA_BLOB data_blob2, Pointer pointer, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT cryptprotect_promptstruct, int i, WinCrypt.DATA_BLOB data_blob3);

    boolean CertAddEncodedCertificateToSystemStore(String str, Pointer pointer, int i);

    WinCrypt.HCERTSTORE CertOpenSystemStore(Pointer pointer, String str);

    boolean CryptSignMessage(WinCrypt.CRYPT_SIGN_MESSAGE_PARA crypt_sign_message_para, boolean z, int i, Pointer[] pointerArr, int[] iArr, Pointer pointer, IntByReference intByReference);

    boolean CryptVerifyMessageSignature(WinCrypt.CRYPT_VERIFY_MESSAGE_PARA crypt_verify_message_para, int i, Pointer pointer, int i2, Pointer pointer2, IntByReference intByReference, PointerByReference pointerByReference);

    boolean CertGetCertificateChain(WinCrypt.HCERTCHAINENGINE hcertchainengine, WinCrypt.CERT_CONTEXT cert_context, WinBase.FILETIME filetime, WinCrypt.HCERTSTORE hcertstore, WinCrypt.CERT_CHAIN_PARA cert_chain_para, int i, Pointer pointer, PointerByReference pointerByReference);

    boolean CertFreeCertificateContext(WinCrypt.CERT_CONTEXT cert_context);

    void CertFreeCertificateChain(WinCrypt.CERT_CHAIN_CONTEXT cert_chain_context);

    boolean CertCloseStore(WinCrypt.HCERTSTORE hcertstore, int i);

    int CertNameToStr(int i, WinCrypt.DATA_BLOB data_blob, int i2, Pointer pointer, int i3);

    boolean CertVerifyCertificateChainPolicy(WTypes.LPSTR lpstr, WinCrypt.CERT_CHAIN_CONTEXT cert_chain_context, WinCrypt.CERT_CHAIN_POLICY_PARA cert_chain_policy_para, WinCrypt.CERT_CHAIN_POLICY_STATUS cert_chain_policy_status);

    WinCrypt.CERT_CONTEXT.ByReference CertFindCertificateInStore(WinCrypt.HCERTSTORE hcertstore, int i, int i2, int i3, Pointer pointer, WinCrypt.CERT_CONTEXT cert_context);

    WinCrypt.HCERTSTORE PFXImportCertStore(WinCrypt.DATA_BLOB data_blob, WTypes.LPWSTR lpwstr, int i);

    WinCrypt.CERT_CONTEXT.ByReference CertEnumCertificatesInStore(WinCrypt.HCERTSTORE hcertstore, Pointer pointer);

    WinCrypt.CTL_CONTEXT.ByReference CertEnumCTLsInStore(WinCrypt.HCERTSTORE hcertstore, Pointer pointer);

    WinCrypt.CRL_CONTEXT.ByReference CertEnumCRLsInStore(WinCrypt.HCERTSTORE hcertstore, Pointer pointer);

    boolean CryptQueryObject(int i, Pointer pointer, int i2, int i3, int i4, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, PointerByReference pointerByReference, PointerByReference pointerByReference2, PointerByReference pointerByReference3);
}
