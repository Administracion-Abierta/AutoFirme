package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32.class */
public interface Advapi32 extends StdCallLibrary {
    public static final Advapi32 INSTANCE = (Advapi32) Native.load("Advapi32", Advapi32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int MAX_KEY_LENGTH = 255;
    public static final int MAX_VALUE_NAME = 16383;
    public static final int RRF_RT_ANY = 65535;
    public static final int RRF_RT_DWORD = 24;
    public static final int RRF_RT_QWORD = 72;
    public static final int RRF_RT_REG_BINARY = 8;
    public static final int RRF_RT_REG_DWORD = 16;
    public static final int RRF_RT_REG_EXPAND_SZ = 4;
    public static final int RRF_RT_REG_MULTI_SZ = 32;
    public static final int RRF_RT_REG_NONE = 1;
    public static final int RRF_RT_REG_QWORD = 64;
    public static final int RRF_RT_REG_SZ = 2;
    public static final int REG_PROCESS_APPKEY = 1;
    public static final int LOGON_WITH_PROFILE = 1;
    public static final int LOGON_NETCREDENTIALS_ONLY = 2;

    boolean GetUserNameW(char[] cArr, IntByReference intByReference);

    boolean LookupAccountName(String str, String str2, WinNT.PSID psid, IntByReference intByReference, char[] cArr, IntByReference intByReference2, PointerByReference pointerByReference);

    boolean LookupAccountSid(String str, WinNT.PSID psid, char[] cArr, IntByReference intByReference, char[] cArr2, IntByReference intByReference2, PointerByReference pointerByReference);

    boolean ConvertSidToStringSid(WinNT.PSID psid, PointerByReference pointerByReference);

    boolean ConvertStringSidToSid(String str, WinNT.PSIDByReference pSIDByReference);

    int GetLengthSid(WinNT.PSID psid);

    boolean IsValidSid(WinNT.PSID psid);

    boolean EqualSid(WinNT.PSID psid, WinNT.PSID psid2);

    boolean IsWellKnownSid(WinNT.PSID psid, int i);

    boolean CreateWellKnownSid(int i, WinNT.PSID psid, WinNT.PSID psid2, IntByReference intByReference);

    boolean InitializeSecurityDescriptor(WinNT.SECURITY_DESCRIPTOR security_descriptor, int i);

    boolean GetSecurityDescriptorControl(WinNT.SECURITY_DESCRIPTOR security_descriptor, ShortByReference shortByReference, IntByReference intByReference);

    boolean SetSecurityDescriptorControl(WinNT.SECURITY_DESCRIPTOR security_descriptor, short s, short s2);

    boolean GetSecurityDescriptorOwner(WinNT.SECURITY_DESCRIPTOR security_descriptor, WinNT.PSIDByReference pSIDByReference, WinDef.BOOLByReference bOOLByReference);

    boolean SetSecurityDescriptorOwner(WinNT.SECURITY_DESCRIPTOR security_descriptor, WinNT.PSID psid, boolean z);

    boolean GetSecurityDescriptorGroup(WinNT.SECURITY_DESCRIPTOR security_descriptor, WinNT.PSIDByReference pSIDByReference, WinDef.BOOLByReference bOOLByReference);

    boolean SetSecurityDescriptorGroup(WinNT.SECURITY_DESCRIPTOR security_descriptor, WinNT.PSID psid, boolean z);

    boolean GetSecurityDescriptorDacl(WinNT.SECURITY_DESCRIPTOR security_descriptor, WinDef.BOOLByReference bOOLByReference, WinNT.PACLByReference pACLByReference, WinDef.BOOLByReference bOOLByReference2);

    boolean SetSecurityDescriptorDacl(WinNT.SECURITY_DESCRIPTOR security_descriptor, boolean z, WinNT.ACL acl, boolean z2);

    boolean InitializeAcl(WinNT.ACL acl, int i, int i2);

    boolean AddAce(WinNT.ACL acl, int i, int i2, Pointer pointer, int i3);

    boolean AddAccessAllowedAce(WinNT.ACL acl, int i, int i2, WinNT.PSID psid);

    boolean AddAccessAllowedAceEx(WinNT.ACL acl, int i, int i2, int i3, WinNT.PSID psid);

    boolean GetAce(WinNT.ACL acl, int i, PointerByReference pointerByReference);

    boolean LogonUser(String str, String str2, String str3, int i, int i2, WinNT.HANDLEByReference hANDLEByReference);

    boolean OpenThreadToken(WinNT.HANDLE handle, int i, boolean z, WinNT.HANDLEByReference hANDLEByReference);

    boolean SetThreadToken(WinNT.HANDLEByReference hANDLEByReference, WinNT.HANDLE handle);

    boolean OpenProcessToken(WinNT.HANDLE handle, int i, WinNT.HANDLEByReference hANDLEByReference);

    boolean DuplicateToken(WinNT.HANDLE handle, int i, WinNT.HANDLEByReference hANDLEByReference);

    boolean DuplicateTokenEx(WinNT.HANDLE handle, int i, WinBase.SECURITY_ATTRIBUTES security_attributes, int i2, int i3, WinNT.HANDLEByReference hANDLEByReference);

    boolean GetTokenInformation(WinNT.HANDLE handle, int i, Structure structure, int i2, IntByReference intByReference);

    boolean ImpersonateLoggedOnUser(WinNT.HANDLE handle);

    boolean ImpersonateSelf(int i);

    boolean RevertToSelf();

    int RegOpenKeyEx(WinReg.HKEY hkey, String str, int i, int i2, WinReg.HKEYByReference hKEYByReference);

    int RegLoadAppKey(String str, WinReg.HKEYByReference hKEYByReference, int i, int i2, int i3);

    int RegConnectRegistry(String str, WinReg.HKEY hkey, WinReg.HKEYByReference hKEYByReference);

    int RegQueryValueEx(WinReg.HKEY hkey, String str, int i, IntByReference intByReference, char[] cArr, IntByReference intByReference2);

    int RegQueryValueEx(WinReg.HKEY hkey, String str, int i, IntByReference intByReference, byte[] bArr, IntByReference intByReference2);

    int RegQueryValueEx(WinReg.HKEY hkey, String str, int i, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    int RegQueryValueEx(WinReg.HKEY hkey, String str, int i, IntByReference intByReference, LongByReference longByReference, IntByReference intByReference2);

    int RegQueryValueEx(WinReg.HKEY hkey, String str, int i, IntByReference intByReference, Pointer pointer, IntByReference intByReference2);

    int RegCloseKey(WinReg.HKEY hkey);

    int RegDeleteValue(WinReg.HKEY hkey, String str);

    int RegSetValueEx(WinReg.HKEY hkey, String str, int i, int i2, Pointer pointer, int i3);

    int RegSetValueEx(WinReg.HKEY hkey, String str, int i, int i2, char[] cArr, int i3);

    int RegSetValueEx(WinReg.HKEY hkey, String str, int i, int i2, byte[] bArr, int i3);

    int RegCreateKeyEx(WinReg.HKEY hkey, String str, int i, String str2, int i2, int i3, WinBase.SECURITY_ATTRIBUTES security_attributes, WinReg.HKEYByReference hKEYByReference, IntByReference intByReference);

    int RegDeleteKey(WinReg.HKEY hkey, String str);

    int RegEnumKeyEx(WinReg.HKEY hkey, int i, char[] cArr, IntByReference intByReference, IntByReference intByReference2, char[] cArr2, IntByReference intByReference3, WinBase.FILETIME filetime);

    int RegEnumValue(WinReg.HKEY hkey, int i, char[] cArr, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, Pointer pointer, IntByReference intByReference4);

    int RegEnumValue(WinReg.HKEY hkey, int i, char[] cArr, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, byte[] bArr, IntByReference intByReference4);

    int RegQueryInfoKey(WinReg.HKEY hkey, char[] cArr, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, IntByReference intByReference4, IntByReference intByReference5, IntByReference intByReference6, IntByReference intByReference7, IntByReference intByReference8, IntByReference intByReference9, WinBase.FILETIME filetime);

    int RegGetValue(WinReg.HKEY hkey, String str, String str2, int i, IntByReference intByReference, Pointer pointer, IntByReference intByReference2);

    int RegGetValue(WinReg.HKEY hkey, String str, String str2, int i, IntByReference intByReference, byte[] bArr, IntByReference intByReference2);

    int RegNotifyChangeKeyValue(WinReg.HKEY hkey, boolean z, int i, WinNT.HANDLE handle, boolean z2);

    WinNT.HANDLE RegisterEventSource(String str, String str2);

    boolean DeregisterEventSource(WinNT.HANDLE handle);

    WinNT.HANDLE OpenEventLog(String str, String str2);

    boolean CloseEventLog(WinNT.HANDLE handle);

    boolean GetNumberOfEventLogRecords(WinNT.HANDLE handle, IntByReference intByReference);

    boolean ReportEvent(WinNT.HANDLE handle, int i, int i2, int i3, WinNT.PSID psid, int i4, int i5, String[] strArr, Pointer pointer);

    boolean ClearEventLog(WinNT.HANDLE handle, String str);

    boolean BackupEventLog(WinNT.HANDLE handle, String str);

    WinNT.HANDLE OpenBackupEventLog(String str, String str2);

    boolean ReadEventLog(WinNT.HANDLE handle, int i, int i2, Pointer pointer, int i3, IntByReference intByReference, IntByReference intByReference2);

    boolean GetOldestEventLogRecord(WinNT.HANDLE handle, IntByReference intByReference);

    boolean ChangeServiceConfig2(Winsvc.SC_HANDLE sc_handle, int i, Winsvc.ChangeServiceConfig2Info changeServiceConfig2Info);

    boolean QueryServiceConfig2(Winsvc.SC_HANDLE sc_handle, int i, Pointer pointer, int i2, IntByReference intByReference);

    boolean QueryServiceStatusEx(Winsvc.SC_HANDLE sc_handle, int i, Winsvc.SERVICE_STATUS_PROCESS service_status_process, int i2, IntByReference intByReference);

    boolean QueryServiceStatus(Winsvc.SC_HANDLE sc_handle, Winsvc.SERVICE_STATUS service_status);

    boolean ControlService(Winsvc.SC_HANDLE sc_handle, int i, Winsvc.SERVICE_STATUS service_status);

    boolean StartService(Winsvc.SC_HANDLE sc_handle, int i, String[] strArr);

    boolean CloseServiceHandle(Winsvc.SC_HANDLE sc_handle);

    Winsvc.SC_HANDLE OpenService(Winsvc.SC_HANDLE sc_handle, String str, int i);

    Winsvc.SC_HANDLE OpenSCManager(String str, String str2, int i);

    boolean EnumDependentServices(Winsvc.SC_HANDLE sc_handle, int i, Pointer pointer, int i2, IntByReference intByReference, IntByReference intByReference2);

    boolean EnumServicesStatusEx(Winsvc.SC_HANDLE sc_handle, int i, int i2, int i3, Pointer pointer, int i4, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, String str);

    boolean CreateProcessAsUser(WinNT.HANDLE handle, String str, String str2, WinBase.SECURITY_ATTRIBUTES security_attributes, WinBase.SECURITY_ATTRIBUTES security_attributes2, boolean z, int i, String str3, String str4, WinBase.STARTUPINFO startupinfo, WinBase.PROCESS_INFORMATION process_information);

    boolean AdjustTokenPrivileges(WinNT.HANDLE handle, boolean z, WinNT.TOKEN_PRIVILEGES token_privileges, int i, WinNT.TOKEN_PRIVILEGES token_privileges2, IntByReference intByReference);

    boolean LookupPrivilegeName(String str, WinNT.LUID luid, char[] cArr, IntByReference intByReference);

    boolean LookupPrivilegeValue(String str, String str2, WinNT.LUID luid);

    boolean GetFileSecurity(String str, int i, Pointer pointer, int i2, IntByReference intByReference);

    boolean SetFileSecurity(String str, int i, Pointer pointer);

    int GetSecurityInfo(WinNT.HANDLE handle, int i, int i2, PointerByReference pointerByReference, PointerByReference pointerByReference2, PointerByReference pointerByReference3, PointerByReference pointerByReference4, PointerByReference pointerByReference5);

    int SetSecurityInfo(WinNT.HANDLE handle, int i, int i2, Pointer pointer, Pointer pointer2, Pointer pointer3, Pointer pointer4);

    int GetNamedSecurityInfo(String str, int i, int i2, PointerByReference pointerByReference, PointerByReference pointerByReference2, PointerByReference pointerByReference3, PointerByReference pointerByReference4, PointerByReference pointerByReference5);

    int SetNamedSecurityInfo(String str, int i, int i2, Pointer pointer, Pointer pointer2, Pointer pointer3, Pointer pointer4);

    int GetSecurityDescriptorLength(Pointer pointer);

    boolean IsValidSecurityDescriptor(Pointer pointer);

    boolean MakeSelfRelativeSD(WinNT.SECURITY_DESCRIPTOR security_descriptor, WinNT.SECURITY_DESCRIPTOR_RELATIVE security_descriptor_relative, IntByReference intByReference);

    boolean MakeAbsoluteSD(WinNT.SECURITY_DESCRIPTOR_RELATIVE security_descriptor_relative, WinNT.SECURITY_DESCRIPTOR security_descriptor, IntByReference intByReference, WinNT.ACL acl, IntByReference intByReference2, WinNT.ACL acl2, IntByReference intByReference3, WinNT.PSID psid, IntByReference intByReference4, WinNT.PSID psid2, IntByReference intByReference5);

    boolean IsValidAcl(Pointer pointer);

    void MapGenericMask(WinDef.DWORDByReference dWORDByReference, WinNT.GENERIC_MAPPING generic_mapping);

    boolean AccessCheck(Pointer pointer, WinNT.HANDLE handle, WinDef.DWORD dword, WinNT.GENERIC_MAPPING generic_mapping, WinNT.PRIVILEGE_SET privilege_set, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.BOOLByReference bOOLByReference);

    boolean EncryptFile(String str);

    boolean DecryptFile(String str, WinDef.DWORD dword);

    boolean FileEncryptionStatus(String str, WinDef.DWORDByReference dWORDByReference);

    boolean EncryptionDisable(String str, boolean z);

    int OpenEncryptedFileRaw(String str, WinDef.ULONG ulong, PointerByReference pointerByReference);

    int ReadEncryptedFileRaw(WinBase.FE_EXPORT_FUNC fe_export_func, Pointer pointer, Pointer pointer2);

    int WriteEncryptedFileRaw(WinBase.FE_IMPORT_FUNC fe_import_func, Pointer pointer, Pointer pointer2);

    void CloseEncryptedFileRaw(Pointer pointer);

    boolean CreateProcessWithLogonW(String str, String str2, String str3, int i, String str4, String str5, int i2, Pointer pointer, String str6, WinBase.STARTUPINFO startupinfo, WinBase.PROCESS_INFORMATION process_information);

    boolean StartServiceCtrlDispatcher(Winsvc.SERVICE_TABLE_ENTRY[] service_table_entryArr);

    Winsvc.SERVICE_STATUS_HANDLE RegisterServiceCtrlHandler(String str, Library.Handler handler);

    Winsvc.SERVICE_STATUS_HANDLE RegisterServiceCtrlHandlerEx(String str, Winsvc.HandlerEx handlerEx, Pointer pointer);

    boolean SetServiceStatus(Winsvc.SERVICE_STATUS_HANDLE service_status_handle, Winsvc.SERVICE_STATUS service_status);

    Winsvc.SC_HANDLE CreateService(Winsvc.SC_HANDLE sc_handle, String str, String str2, int i, int i2, int i3, int i4, String str3, String str4, IntByReference intByReference, String str5, String str6, String str7);

    boolean DeleteService(Winsvc.SC_HANDLE sc_handle);
}
