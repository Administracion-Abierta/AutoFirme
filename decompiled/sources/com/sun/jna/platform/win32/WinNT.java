package com.sun.jna.platform.win32;

import com.aowagie.text.ElementTags;
import com.sun.jna.FromNativeContext;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT.class */
public interface WinNT extends WinError, WinDef, WinBase, BaseTSD {
    public static final int MINCHAR = 128;
    public static final int MAXCHAR = 127;
    public static final int MINSHORT = 32768;
    public static final int MAXSHORT = 32767;
    public static final int MINLONG = Integer.MIN_VALUE;
    public static final int MAXLONG = Integer.MAX_VALUE;
    public static final int MAXBYTE = 255;
    public static final int MAXWORD = 65535;
    public static final int MAXDWORD = -1;
    public static final int DELETE = 65536;
    public static final int READ_CONTROL = 131072;
    public static final int WRITE_DAC = 262144;
    public static final int WRITE_OWNER = 524288;
    public static final int SYNCHRONIZE = 1048576;
    public static final int STANDARD_RIGHTS_REQUIRED = 983040;
    public static final int STANDARD_RIGHTS_READ = 131072;
    public static final int STANDARD_RIGHTS_WRITE = 131072;
    public static final int STANDARD_RIGHTS_EXECUTE = 131072;
    public static final int STANDARD_RIGHTS_ALL = 2031616;
    public static final int SPECIFIC_RIGHTS_ALL = 65535;
    public static final int MUTANT_QUERY_STATE = 1;
    public static final int MUTANT_ALL_ACCESS = 2031617;
    public static final int TOKEN_ASSIGN_PRIMARY = 1;
    public static final int TOKEN_DUPLICATE = 2;
    public static final int TOKEN_IMPERSONATE = 4;
    public static final int TOKEN_QUERY = 8;
    public static final int TOKEN_QUERY_SOURCE = 16;
    public static final int TOKEN_ADJUST_PRIVILEGES = 32;
    public static final int TOKEN_ADJUST_GROUPS = 64;
    public static final int TOKEN_ADJUST_DEFAULT = 128;
    public static final int TOKEN_ADJUST_SESSIONID = 256;
    public static final int TOKEN_ALL_ACCESS_P = 983295;
    public static final int TOKEN_ALL_ACCESS = 983551;
    public static final int TOKEN_READ = 131080;
    public static final int TOKEN_WRITE = 131296;
    public static final int TOKEN_EXECUTE = 131072;
    public static final int THREAD_TERMINATE = 1;
    public static final int THREAD_SUSPEND_RESUME = 2;
    public static final int THREAD_GET_CONTEXT = 8;
    public static final int THREAD_SET_CONTEXT = 16;
    public static final int THREAD_QUERY_INFORMATION = 64;
    public static final int THREAD_SET_INFORMATION = 32;
    public static final int THREAD_SET_THREAD_TOKEN = 128;
    public static final int THREAD_IMPERSONATE = 256;
    public static final int THREAD_DIRECT_IMPERSONATION = 512;
    public static final int THREAD_SET_LIMITED_INFORMATION = 1024;
    public static final int THREAD_QUERY_LIMITED_INFORMATION = 2048;
    public static final int THREAD_ALL_ACCESS = 2032639;
    public static final int LTP_PC_SMT = 1;
    public static final int FILE_READ_DATA = 1;
    public static final int FILE_LIST_DIRECTORY = 1;
    public static final int FILE_WRITE_DATA = 2;
    public static final int FILE_ADD_FILE = 2;
    public static final int FILE_APPEND_DATA = 4;
    public static final int FILE_ADD_SUBDIRECTORY = 4;
    public static final int FILE_CREATE_PIPE_INSTANCE = 4;
    public static final int FILE_READ_EA = 8;
    public static final int FILE_WRITE_EA = 16;
    public static final int FILE_EXECUTE = 32;
    public static final int FILE_TRAVERSE = 32;
    public static final int FILE_DELETE_CHILD = 64;
    public static final int FILE_READ_ATTRIBUTES = 128;
    public static final int FILE_WRITE_ATTRIBUTES = 256;
    public static final int FILE_ALL_ACCESS = 2032127;
    public static final int FILE_GENERIC_READ = 1179785;
    public static final int FILE_GENERIC_WRITE = 1179926;
    public static final int FILE_GENERIC_EXECUTE = 1179808;
    public static final int CREATE_NEW = 1;
    public static final int CREATE_ALWAYS = 2;
    public static final int OPEN_EXISTING = 3;
    public static final int OPEN_ALWAYS = 4;
    public static final int TRUNCATE_EXISTING = 5;
    public static final int FILE_FLAG_WRITE_THROUGH = Integer.MIN_VALUE;
    public static final int FILE_FLAG_OVERLAPPED = 1073741824;
    public static final int FILE_FLAG_NO_BUFFERING = 536870912;
    public static final int FILE_FLAG_RANDOM_ACCESS = 268435456;
    public static final int FILE_FLAG_SEQUENTIAL_SCAN = 134217728;
    public static final int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
    public static final int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
    public static final int FILE_FLAG_POSIX_SEMANTICS = 16777216;
    public static final int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
    public static final int FILE_FLAG_OPEN_NO_RECALL = 1048576;
    public static final int GENERIC_READ = Integer.MIN_VALUE;
    public static final int GENERIC_WRITE = 1073741824;
    public static final int GENERIC_EXECUTE = 536870912;
    public static final int GENERIC_ALL = 268435456;
    public static final int ACCESS_SYSTEM_SECURITY = 16777216;
    public static final int PAGE_GUARD = 256;
    public static final int PAGE_NOACCESS = 1;
    public static final int PAGE_READONLY = 2;
    public static final int PAGE_READWRITE = 4;
    public static final int PAGE_WRITECOPY = 8;
    public static final int PAGE_EXECUTE = 16;
    public static final int PAGE_EXECUTE_READ = 32;
    public static final int PAGE_EXECUTE_READWRITE = 64;
    public static final int SECTION_QUERY = 1;
    public static final int SECTION_MAP_WRITE = 2;
    public static final int SECTION_MAP_READ = 4;
    public static final int SECTION_MAP_EXECUTE = 8;
    public static final int SECTION_EXTEND_SIZE = 16;
    public static final int SECTION_ALL_ACCESS = 983071;
    public static final int SECTION_MAP_EXECUTE_EXPLICIT = 32;
    public static final int FILE_SHARE_READ = 1;
    public static final int FILE_SHARE_WRITE = 2;
    public static final int FILE_SHARE_DELETE = 4;
    public static final int FILE_TYPE_CHAR = 2;
    public static final int FILE_TYPE_DISK = 1;
    public static final int FILE_TYPE_PIPE = 3;
    public static final int FILE_TYPE_REMOTE = 32768;
    public static final int FILE_TYPE_UNKNOWN = 0;
    public static final int FILE_ATTRIBUTE_READONLY = 1;
    public static final int FILE_ATTRIBUTE_HIDDEN = 2;
    public static final int FILE_ATTRIBUTE_SYSTEM = 4;
    public static final int FILE_ATTRIBUTE_DIRECTORY = 16;
    public static final int FILE_ATTRIBUTE_ARCHIVE = 32;
    public static final int FILE_ATTRIBUTE_DEVICE = 64;
    public static final int FILE_ATTRIBUTE_NORMAL = 128;
    public static final int FILE_ATTRIBUTE_TEMPORARY = 256;
    public static final int FILE_ATTRIBUTE_SPARSE_FILE = 512;
    public static final int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
    public static final int FILE_ATTRIBUTE_COMPRESSED = 2048;
    public static final int FILE_ATTRIBUTE_OFFLINE = 4096;
    public static final int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 8192;
    public static final int FILE_ATTRIBUTE_ENCRYPTED = 16384;
    public static final int FILE_ATTRIBUTE_VIRTUAL = 65536;
    public static final int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
    public static final int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
    public static final int FILE_NOTIFY_CHANGE_NAME = 3;
    public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
    public static final int FILE_NOTIFY_CHANGE_SIZE = 8;
    public static final int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
    public static final int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
    public static final int FILE_NOTIFY_CHANGE_CREATION = 64;
    public static final int FILE_NOTIFY_CHANGE_SECURITY = 256;
    public static final int FILE_ACTION_ADDED = 1;
    public static final int FILE_ACTION_REMOVED = 2;
    public static final int FILE_ACTION_MODIFIED = 3;
    public static final int FILE_ACTION_RENAMED_OLD_NAME = 4;
    public static final int FILE_ACTION_RENAMED_NEW_NAME = 5;
    public static final int FILE_CASE_SENSITIVE_SEARCH = 1;
    public static final int FILE_CASE_PRESERVED_NAMES = 2;
    public static final int FILE_UNICODE_ON_DISK = 4;
    public static final int FILE_PERSISTENT_ACLS = 8;
    public static final int FILE_FILE_COMPRESSION = 16;
    public static final int FILE_VOLUME_QUOTAS = 32;
    public static final int FILE_SUPPORTS_SPARSE_FILES = 64;
    public static final int FILE_SUPPORTS_REPARSE_POINTS = 128;
    public static final int FILE_SUPPORTS_REMOTE_STORAGE = 256;
    public static final int FILE_VOLUME_IS_COMPRESSED = 32768;
    public static final int FILE_SUPPORTS_OBJECT_IDS = 65536;
    public static final int FILE_SUPPORTS_ENCRYPTION = 131072;
    public static final int FILE_NAMED_STREAMS = 262144;
    public static final int FILE_READ_ONLY_VOLUME = 524288;
    public static final int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
    public static final int FILE_SUPPORTS_TRANSACTIONS = 2097152;
    public static final int FILE_SUPPORTS_HARD_LINKS = 4194304;
    public static final int FILE_SUPPORTS_EXTENDED_ATTRIBUTES = 8388608;
    public static final int FILE_SUPPORTS_OPEN_BY_FILE_ID = 16777216;
    public static final int FILE_SUPPORTS_USN_JOURNAL = 33554432;
    public static final int IO_REPARSE_TAG_MOUNT_POINT = -1610612733;
    public static final int IO_REPARSE_TAG_HSM = -1073741820;
    public static final int IO_REPARSE_TAG_HSM2 = -2147483642;
    public static final int IO_REPARSE_TAG_SIS = -2147483641;
    public static final int IO_REPARSE_TAG_WIM = -2147483640;
    public static final int IO_REPARSE_TAG_CSV = -2147483639;
    public static final int IO_REPARSE_TAG_DFS = -2147483638;
    public static final int IO_REPARSE_TAG_SYMLINK = -1610612724;
    public static final int IO_REPARSE_TAG_DFSR = -2147483630;
    public static final int DDD_RAW_TARGET_PATH = 1;
    public static final int DDD_REMOVE_DEFINITION = 2;
    public static final int DDD_EXACT_MATCH_ON_REMOVE = 4;
    public static final int DDD_NO_BROADCAST_SYSTEM = 8;
    public static final int COMPRESSION_FORMAT_NONE = 0;
    public static final int COMPRESSION_FORMAT_DEFAULT = 1;
    public static final int COMPRESSION_FORMAT_LZNT1 = 2;
    public static final int COMPRESSION_FORMAT_XPRESS = 3;
    public static final int COMPRESSION_FORMAT_XPRESS_HUFF = 4;
    public static final int COMPRESSION_ENGINE_STANDARD = 0;
    public static final int COMPRESSION_ENGINE_MAXIMUM = 256;
    public static final int COMPRESSION_ENGINE_HIBER = 512;
    public static final int KEY_QUERY_VALUE = 1;
    public static final int KEY_SET_VALUE = 2;
    public static final int KEY_CREATE_SUB_KEY = 4;
    public static final int KEY_ENUMERATE_SUB_KEYS = 8;
    public static final int KEY_NOTIFY = 16;
    public static final int KEY_CREATE_LINK = 32;
    public static final int KEY_WOW64_32KEY = 512;
    public static final int KEY_WOW64_64KEY = 256;
    public static final int KEY_WOW64_RES = 768;
    public static final int KEY_READ = 131097;
    public static final int KEY_WRITE = 131078;
    public static final int KEY_EXECUTE = 131097;
    public static final int KEY_ALL_ACCESS = 983103;
    public static final int REG_OPTION_RESERVED = 0;
    public static final int REG_OPTION_NON_VOLATILE = 0;
    public static final int REG_OPTION_VOLATILE = 1;
    public static final int REG_OPTION_CREATE_LINK = 2;
    public static final int REG_OPTION_BACKUP_RESTORE = 4;
    public static final int REG_OPTION_OPEN_LINK = 8;
    public static final int REG_LEGAL_OPTION = 15;
    public static final int REG_CREATED_NEW_KEY = 1;
    public static final int REG_OPENED_EXISTING_KEY = 2;
    public static final int REG_STANDARD_FORMAT = 1;
    public static final int REG_LATEST_FORMAT = 2;
    public static final int REG_NO_COMPRESSION = 4;
    public static final int REG_WHOLE_HIVE_VOLATILE = 1;
    public static final int REG_REFRESH_HIVE = 2;
    public static final int REG_NO_LAZY_FLUSH = 4;
    public static final int REG_FORCE_RESTORE = 8;
    public static final int REG_APP_HIVE = 16;
    public static final int REG_PROCESS_PRIVATE = 32;
    public static final int REG_START_JOURNAL = 64;
    public static final int REG_HIVE_EXACT_FILE_GROWTH = 128;
    public static final int REG_HIVE_NO_RM = 256;
    public static final int REG_HIVE_SINGLE_LOG = 512;
    public static final int REG_FORCE_UNLOAD = 1;
    public static final int REG_NOTIFY_CHANGE_NAME = 1;
    public static final int REG_NOTIFY_CHANGE_ATTRIBUTES = 2;
    public static final int REG_NOTIFY_CHANGE_LAST_SET = 4;
    public static final int REG_NOTIFY_CHANGE_SECURITY = 8;
    public static final int REG_NOTIFY_THREAD_AGNOSTIC = 268435456;
    public static final int REG_LEGAL_CHANGE_FILTER = 268435471;
    public static final int REG_NONE = 0;
    public static final int REG_SZ = 1;
    public static final int REG_EXPAND_SZ = 2;
    public static final int REG_BINARY = 3;
    public static final int REG_DWORD = 4;
    public static final int REG_DWORD_LITTLE_ENDIAN = 4;
    public static final int REG_DWORD_BIG_ENDIAN = 5;
    public static final int REG_LINK = 6;
    public static final int REG_MULTI_SZ = 7;
    public static final int REG_RESOURCE_LIST = 8;
    public static final int REG_FULL_RESOURCE_DESCRIPTOR = 9;
    public static final int REG_RESOURCE_REQUIREMENTS_LIST = 10;
    public static final int REG_QWORD = 11;
    public static final int REG_QWORD_LITTLE_ENDIAN = 11;
    public static final int SID_REVISION = 1;
    public static final int SID_MAX_SUB_AUTHORITIES = 15;
    public static final int SID_RECOMMENDED_SUB_AUTHORITIES = 1;
    public static final int SECURITY_MAX_SID_SIZE = 68;
    public static final int VER_EQUAL = 1;
    public static final int VER_GREATER = 2;
    public static final int VER_GREATER_EQUAL = 3;
    public static final int VER_LESS = 4;
    public static final int VER_LESS_EQUAL = 5;
    public static final int VER_AND = 6;
    public static final int VER_OR = 7;
    public static final int VER_CONDITION_MASK = 7;
    public static final int VER_NUM_BITS_PER_CONDITION_MASK = 3;
    public static final int VER_MINORVERSION = 1;
    public static final int VER_MAJORVERSION = 2;
    public static final int VER_BUILDNUMBER = 4;
    public static final int VER_PLATFORMID = 8;
    public static final int VER_SERVICEPACKMINOR = 16;
    public static final int VER_SERVICEPACKMAJOR = 32;
    public static final int VER_SUITENAME = 64;
    public static final int VER_PRODUCT_TYPE = 128;
    public static final int VER_NT_WORKSTATION = 1;
    public static final int VER_NT_DOMAIN_CONTROLLER = 2;
    public static final int VER_NT_SERVER = 3;
    public static final int VER_PLATFORM_WIN32s = 0;
    public static final int VER_PLATFORM_WIN32_WINDOWS = 1;
    public static final int VER_PLATFORM_WIN32_NT = 2;
    public static final short WIN32_WINNT_NT4 = 1024;
    public static final short WIN32_WINNT_WIN2K = 1280;
    public static final short WIN32_WINNT_WINXP = 1281;
    public static final short WIN32_WINNT_WS03 = 1282;
    public static final short WIN32_WINNT_WIN6 = 1536;
    public static final short WIN32_WINNT_VISTA = 1536;
    public static final short WIN32_WINNT_WS08 = 1536;
    public static final short WIN32_WINNT_LONGHORN = 1536;
    public static final short WIN32_WINNT_WIN7 = 1537;
    public static final short WIN32_WINNT_WIN8 = 1538;
    public static final short WIN32_WINNT_WINBLUE = 1539;
    public static final short WIN32_WINNT_WINTHRESHOLD = 2560;
    public static final short WIN32_WINNT_WIN10 = 2560;
    public static final int EVENTLOG_SEQUENTIAL_READ = 1;
    public static final int EVENTLOG_SEEK_READ = 2;
    public static final int EVENTLOG_FORWARDS_READ = 4;
    public static final int EVENTLOG_BACKWARDS_READ = 8;
    public static final int EVENTLOG_SUCCESS = 0;
    public static final int EVENTLOG_ERROR_TYPE = 1;
    public static final int EVENTLOG_WARNING_TYPE = 2;
    public static final int EVENTLOG_INFORMATION_TYPE = 4;
    public static final int EVENTLOG_AUDIT_SUCCESS = 8;
    public static final int EVENTLOG_AUDIT_FAILURE = 16;
    public static final int SERVICE_KERNEL_DRIVER = 1;
    public static final int SERVICE_FILE_SYSTEM_DRIVER = 2;
    public static final int SERVICE_ADAPTER = 4;
    public static final int SERVICE_RECOGNIZER_DRIVER = 8;
    public static final int SERVICE_DRIVER = 11;
    public static final int SERVICE_WIN32_OWN_PROCESS = 16;
    public static final int SERVICE_WIN32_SHARE_PROCESS = 32;
    public static final int SERVICE_WIN32 = 48;
    public static final int SERVICE_INTERACTIVE_PROCESS = 256;
    public static final int SERVICE_TYPE_ALL = 319;
    public static final int SERVICE_BOOT_START = 0;
    public static final int SERVICE_SYSTEM_START = 1;
    public static final int SERVICE_AUTO_START = 2;
    public static final int SERVICE_DEMAND_START = 3;
    public static final int SERVICE_DISABLED = 4;
    public static final int SERVICE_ERROR_IGNORE = 0;
    public static final int SERVICE_ERROR_NORMAL = 1;
    public static final int SERVICE_ERROR_SEVERE = 2;
    public static final int SERVICE_ERROR_CRITICAL = 3;
    public static final int STATUS_PENDING = 259;
    public static final String SE_CREATE_TOKEN_NAME = "SeCreateTokenPrivilege";
    public static final String SE_ASSIGNPRIMARYTOKEN_NAME = "SeAssignPrimaryTokenPrivilege";
    public static final String SE_LOCK_MEMORY_NAME = "SeLockMemoryPrivilege";
    public static final String SE_INCREASE_QUOTA_NAME = "SeIncreaseQuotaPrivilege";
    public static final String SE_UNSOLICITED_INPUT_NAME = "SeUnsolicitedInputPrivilege";
    public static final String SE_MACHINE_ACCOUNT_NAME = "SeMachineAccountPrivilege";
    public static final String SE_TCB_NAME = "SeTcbPrivilege";
    public static final String SE_SECURITY_NAME = "SeSecurityPrivilege";
    public static final String SE_TAKE_OWNERSHIP_NAME = "SeTakeOwnershipPrivilege";
    public static final String SE_LOAD_DRIVER_NAME = "SeLoadDriverPrivilege";
    public static final String SE_SYSTEM_PROFILE_NAME = "SeSystemProfilePrivilege";
    public static final String SE_SYSTEMTIME_NAME = "SeSystemtimePrivilege";
    public static final String SE_PROF_SINGLE_PROCESS_NAME = "SeProfileSingleProcessPrivilege";
    public static final String SE_INC_BASE_PRIORITY_NAME = "SeIncreaseBasePriorityPrivilege";
    public static final String SE_CREATE_PAGEFILE_NAME = "SeCreatePagefilePrivilege";
    public static final String SE_CREATE_PERMANENT_NAME = "SeCreatePermanentPrivilege";
    public static final String SE_BACKUP_NAME = "SeBackupPrivilege";
    public static final String SE_RESTORE_NAME = "SeRestorePrivilege";
    public static final String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";
    public static final String SE_DEBUG_NAME = "SeDebugPrivilege";
    public static final String SE_AUDIT_NAME = "SeAuditPrivilege";
    public static final String SE_SYSTEM_ENVIRONMENT_NAME = "SeSystemEnvironmentPrivilege";
    public static final String SE_CHANGE_NOTIFY_NAME = "SeChangeNotifyPrivilege";
    public static final String SE_REMOTE_SHUTDOWN_NAME = "SeRemoteShutdownPrivilege";
    public static final String SE_UNDOCK_NAME = "SeUndockPrivilege";
    public static final String SE_SYNC_AGENT_NAME = "SeSyncAgentPrivilege";
    public static final String SE_ENABLE_DELEGATION_NAME = "SeEnableDelegationPrivilege";
    public static final String SE_MANAGE_VOLUME_NAME = "SeManageVolumePrivilege";
    public static final String SE_IMPERSONATE_NAME = "SeImpersonatePrivilege";
    public static final String SE_CREATE_GLOBAL_NAME = "SeCreateGlobalPrivilege";
    public static final int SE_PRIVILEGE_ENABLED_BY_DEFAULT = 1;
    public static final int SE_PRIVILEGE_ENABLED = 2;
    public static final int SE_PRIVILEGE_REMOVED = 4;
    public static final int SE_PRIVILEGE_USED_FOR_ACCESS = Integer.MIN_VALUE;
    public static final int PROCESS_CREATE_PROCESS = 128;
    public static final int PROCESS_CREATE_THREAD = 2;
    public static final int PROCESS_DUP_HANDLE = 64;
    public static final int PROCESS_ALL_ACCESS = 2039803;
    public static final int PROCESS_QUERY_INFORMATION = 1024;
    public static final int PROCESS_QUERY_LIMITED_INFORMATION = 4096;
    public static final int PROCESS_SET_INFORMATION = 512;
    public static final int PROCESS_SET_QUOTA = 256;
    public static final int PROCESS_SUSPEND_RESUME = 2048;
    public static final int PROCESS_TERMINATE = 1;
    public static final int PROCESS_NAME_NATIVE = 1;
    public static final int PROCESS_VM_OPERATION = 8;
    public static final int PROCESS_VM_READ = 16;
    public static final int PROCESS_VM_WRITE = 32;
    public static final int PROCESS_SYNCHRONIZE = 1048576;
    public static final int OWNER_SECURITY_INFORMATION = 1;
    public static final int GROUP_SECURITY_INFORMATION = 2;
    public static final int DACL_SECURITY_INFORMATION = 4;
    public static final int SACL_SECURITY_INFORMATION = 8;
    public static final int LABEL_SECURITY_INFORMATION = 16;
    public static final int PROTECTED_DACL_SECURITY_INFORMATION = Integer.MIN_VALUE;
    public static final int PROTECTED_SACL_SECURITY_INFORMATION = 1073741824;
    public static final int UNPROTECTED_DACL_SECURITY_INFORMATION = 536870912;
    public static final int UNPROTECTED_SACL_SECURITY_INFORMATION = 268435456;
    public static final int SE_OWNER_DEFAULTED = 1;
    public static final int SE_GROUP_DEFAULTED = 2;
    public static final int SE_DACL_PRESENT = 4;
    public static final int SE_DACL_DEFAULTED = 8;
    public static final int SE_SACL_PRESENT = 16;
    public static final int SE_SACL_DEFAULTED = 32;
    public static final int SE_DACL_AUTO_INHERIT_REQ = 256;
    public static final int SE_SACL_AUTO_INHERIT_REQ = 512;
    public static final int SE_DACL_AUTO_INHERITED = 1024;
    public static final int SE_SACL_AUTO_INHERITED = 2048;
    public static final int SE_DACL_PROTECTED = 4096;
    public static final int SE_SACL_PROTECTED = 8192;
    public static final int SE_RM_CONTROL_VALID = 16384;
    public static final int SE_SELF_RELATIVE = 32768;
    public static final int SECURITY_DESCRIPTOR_REVISION = 1;
    public static final int ACL_REVISION = 2;
    public static final int ACL_REVISION_DS = 4;
    public static final int ACL_REVISION1 = 1;
    public static final int ACL_REVISION2 = 2;
    public static final int ACL_REVISION3 = 3;
    public static final int ACL_REVISION4 = 4;
    public static final int MIN_ACL_REVISION = 2;
    public static final int MAX_ACL_REVISION = 4;
    public static final byte ACCESS_ALLOWED_ACE_TYPE = 0;
    public static final byte ACCESS_DENIED_ACE_TYPE = 1;
    public static final byte SYSTEM_AUDIT_ACE_TYPE = 2;
    public static final byte SYSTEM_ALARM_ACE_TYPE = 3;
    public static final byte ACCESS_ALLOWED_COMPOUND_ACE_TYPE = 4;
    public static final byte ACCESS_ALLOWED_OBJECT_ACE_TYPE = 5;
    public static final byte ACCESS_DENIED_OBJECT_ACE_TYPE = 6;
    public static final byte SYSTEM_AUDIT_OBJECT_ACE_TYPE = 7;
    public static final byte SYSTEM_ALARM_OBJECT_ACE_TYPE = 8;
    public static final byte ACCESS_ALLOWED_CALLBACK_ACE_TYPE = 9;
    public static final byte ACCESS_DENIED_CALLBACK_ACE_TYPE = 10;
    public static final byte ACCESS_ALLOWED_CALLBACK_OBJECT_ACE_TYPE = 11;
    public static final byte ACCESS_DENIED_CALLBACK_OBJECT_ACE_TYPE = 12;
    public static final byte SYSTEM_AUDIT_CALLBACK_ACE_TYPE = 13;
    public static final byte SYSTEM_ALARM_CALLBACK_ACE_TYPE = 14;
    public static final byte SYSTEM_AUDIT_CALLBACK_OBJECT_ACE_TYPE = 15;
    public static final byte SYSTEM_ALARM_CALLBACK_OBJECT_ACE_TYPE = 16;
    public static final byte SYSTEM_MANDATORY_LABEL_ACE_TYPE = 17;
    public static final byte OBJECT_INHERIT_ACE = 1;
    public static final byte CONTAINER_INHERIT_ACE = 2;
    public static final byte NO_PROPAGATE_INHERIT_ACE = 4;
    public static final byte INHERIT_ONLY_ACE = 8;
    public static final byte INHERITED_ACE = 16;
    public static final byte VALID_INHERIT_FLAGS = 31;
    public static final byte CACHE_FULLY_ASSOCIATIVE = -1;
    public static final int NUM_DISCHARGE_POLICIES = 4;
    public static final int MEM_COMMIT = 4096;
    public static final int MEM_FREE = 65536;
    public static final int MEM_RESERVE = 8192;
    public static final int MEM_IMAGE = 16777216;
    public static final int MEM_MAPPED = 262144;
    public static final int MEM_PRIVATE = 131072;
    public static final int MEM_RESET = 524288;
    public static final int MEM_RESET_UNDO = 16777216;
    public static final int MEM_LARGE_PAGES = 536870912;
    public static final int MEM_PHYSICAL = 4194304;
    public static final int MEM_TOP_DOWN = 1048576;
    public static final int MEM_COALESCE_PLACEHOLDERS = 1;
    public static final int MEM_PRESERVE_PLACEHOLDER = 2;
    public static final int MEM_DECOMMIT = 16384;
    public static final int MEM_RELEASE = 32768;
    public static final byte SECURITY_DYNAMIC_TRACKING = 1;
    public static final byte SECURITY_STATIC_TRACKING = 0;
    public static final byte BOOLEAN_TRUE = 1;
    public static final byte BOOLEAN_FALSE = 0;
    public static final int LANG_NEUTRAL = 0;
    public static final int LANG_INVARIANT = 127;
    public static final int LANG_AFRIKAANS = 54;
    public static final int LANG_ALBANIAN = 28;
    public static final int LANG_ARABIC = 1;
    public static final int LANG_ARMENIAN = 43;
    public static final int LANG_ASSAMESE = 77;
    public static final int LANG_AZERI = 44;
    public static final int LANG_BASQUE = 45;
    public static final int LANG_BELARUSIAN = 35;
    public static final int LANG_BENGALI = 69;
    public static final int LANG_BULGARIAN = 2;
    public static final int LANG_CATALAN = 3;
    public static final int LANG_CHINESE = 4;
    public static final int LANG_CROATIAN = 26;
    public static final int LANG_CZECH = 5;
    public static final int LANG_DANISH = 6;
    public static final int LANG_DIVEHI = 101;
    public static final int LANG_DUTCH = 19;
    public static final int LANG_ENGLISH = 9;
    public static final int LANG_ESTONIAN = 37;
    public static final int LANG_FAEROESE = 56;
    public static final int LANG_FARSI = 41;
    public static final int LANG_FINNISH = 11;
    public static final int LANG_FRENCH = 12;
    public static final int LANG_GALICIAN = 86;
    public static final int LANG_GEORGIAN = 55;
    public static final int LANG_GERMAN = 7;
    public static final int LANG_GREEK = 8;
    public static final int LANG_GUJARATI = 71;
    public static final int LANG_HEBREW = 13;
    public static final int LANG_HINDI = 57;
    public static final int LANG_HUNGARIAN = 14;
    public static final int LANG_ICELANDIC = 15;
    public static final int LANG_INDONESIAN = 33;
    public static final int LANG_ITALIAN = 16;
    public static final int LANG_JAPANESE = 17;
    public static final int LANG_KANNADA = 75;
    public static final int LANG_KASHMIRI = 96;
    public static final int LANG_KAZAK = 63;
    public static final int LANG_KONKANI = 87;
    public static final int LANG_KOREAN = 18;
    public static final int LANG_KYRGYZ = 64;
    public static final int LANG_LATVIAN = 38;
    public static final int LANG_LITHUANIAN = 39;
    public static final int LANG_MACEDONIAN = 47;
    public static final int LANG_MALAY = 62;
    public static final int LANG_MALAYALAM = 76;
    public static final int LANG_MANIPURI = 88;
    public static final int LANG_MARATHI = 78;
    public static final int LANG_MONGOLIAN = 80;
    public static final int LANG_NEPALI = 97;
    public static final int LANG_NORWEGIAN = 20;
    public static final int LANG_ORIYA = 72;
    public static final int LANG_POLISH = 21;
    public static final int LANG_PORTUGUESE = 22;
    public static final int LANG_PUNJABI = 70;
    public static final int LANG_ROMANIAN = 24;
    public static final int LANG_RUSSIAN = 25;
    public static final int LANG_SANSKRIT = 79;
    public static final int LANG_SERBIAN = 26;
    public static final int LANG_SINDHI = 89;
    public static final int LANG_SLOVAK = 27;
    public static final int LANG_SLOVENIAN = 36;
    public static final int LANG_SPANISH = 10;
    public static final int LANG_SWAHILI = 65;
    public static final int LANG_SWEDISH = 29;
    public static final int LANG_SYRIAC = 90;
    public static final int LANG_TAMIL = 73;
    public static final int LANG_TATAR = 68;
    public static final int LANG_TELUGU = 74;
    public static final int LANG_THAI = 30;
    public static final int LANG_TURKISH = 31;
    public static final int LANG_UKRAINIAN = 34;
    public static final int LANG_URDU = 32;
    public static final int LANG_UZBEK = 67;
    public static final int LANG_VIETNAMESE = 42;
    public static final int SUBLANG_NEUTRAL = 0;
    public static final int SUBLANG_DEFAULT = 1;
    public static final int SUBLANG_SYS_DEFAULT = 2;
    public static final int SUBLANG_ARABIC_SAUDI_ARABIA = 1;
    public static final int SUBLANG_ARABIC_IRAQ = 2;
    public static final int SUBLANG_ARABIC_EGYPT = 3;
    public static final int SUBLANG_ARABIC_LIBYA = 4;
    public static final int SUBLANG_ARABIC_ALGERIA = 5;
    public static final int SUBLANG_ARABIC_MOROCCO = 6;
    public static final int SUBLANG_ARABIC_TUNISIA = 7;
    public static final int SUBLANG_ARABIC_OMAN = 8;
    public static final int SUBLANG_ARABIC_YEMEN = 9;
    public static final int SUBLANG_ARABIC_SYRIA = 10;
    public static final int SUBLANG_ARABIC_JORDAN = 11;
    public static final int SUBLANG_ARABIC_LEBANON = 12;
    public static final int SUBLANG_ARABIC_KUWAIT = 13;
    public static final int SUBLANG_ARABIC_UAE = 14;
    public static final int SUBLANG_ARABIC_BAHRAIN = 15;
    public static final int SUBLANG_ARABIC_QATAR = 16;
    public static final int SUBLANG_AZERI_LATIN = 1;
    public static final int SUBLANG_AZERI_CYRILLIC = 2;
    public static final int SUBLANG_CHINESE_TRADITIONAL = 1;
    public static final int SUBLANG_CHINESE_SIMPLIFIED = 2;
    public static final int SUBLANG_CHINESE_HONGKONG = 3;
    public static final int SUBLANG_CHINESE_SINGAPORE = 4;
    public static final int SUBLANG_CHINESE_MACAU = 5;
    public static final int SUBLANG_DUTCH = 1;
    public static final int SUBLANG_DUTCH_BELGIAN = 2;
    public static final int SUBLANG_ENGLISH_US = 1;
    public static final int SUBLANG_ENGLISH_UK = 2;
    public static final int SUBLANG_ENGLISH_AUS = 3;
    public static final int SUBLANG_ENGLISH_CAN = 4;
    public static final int SUBLANG_ENGLISH_NZ = 5;
    public static final int SUBLANG_ENGLISH_EIRE = 6;
    public static final int SUBLANG_ENGLISH_SOUTH_AFRICA = 7;
    public static final int SUBLANG_ENGLISH_JAMAICA = 8;
    public static final int SUBLANG_ENGLISH_CARIBBEAN = 9;
    public static final int SUBLANG_ENGLISH_BELIZE = 10;
    public static final int SUBLANG_ENGLISH_TRINIDAD = 11;
    public static final int SUBLANG_ENGLISH_ZIMBABWE = 12;
    public static final int SUBLANG_ENGLISH_PHILIPPINES = 13;
    public static final int SUBLANG_FRENCH = 1;
    public static final int SUBLANG_FRENCH_BELGIAN = 2;
    public static final int SUBLANG_FRENCH_CANADIAN = 3;
    public static final int SUBLANG_FRENCH_SWISS = 4;
    public static final int SUBLANG_FRENCH_LUXEMBOURG = 5;
    public static final int SUBLANG_FRENCH_MONACO = 6;
    public static final int SUBLANG_GERMAN = 1;
    public static final int SUBLANG_GERMAN_SWISS = 2;
    public static final int SUBLANG_GERMAN_AUSTRIAN = 3;
    public static final int SUBLANG_GERMAN_LUXEMBOURG = 4;
    public static final int SUBLANG_GERMAN_LIECHTENSTEIN = 5;
    public static final int SUBLANG_ITALIAN = 1;
    public static final int SUBLANG_ITALIAN_SWISS = 2;
    public static final int SUBLANG_KASHMIRI_SASIA = 2;
    public static final int SUBLANG_KASHMIRI_INDIA = 2;
    public static final int SUBLANG_KOREAN = 1;
    public static final int SUBLANG_LITHUANIAN = 1;
    public static final int SUBLANG_MALAY_MALAYSIA = 1;
    public static final int SUBLANG_MALAY_BRUNEI_DARUSSALAM = 2;
    public static final int SUBLANG_NEPALI_INDIA = 2;
    public static final int SUBLANG_NORWEGIAN_BOKMAL = 1;
    public static final int SUBLANG_NORWEGIAN_NYNORSK = 2;
    public static final int SUBLANG_PORTUGUESE = 2;
    public static final int SUBLANG_PORTUGUESE_BRAZILIAN = 1;
    public static final int SUBLANG_SERBIAN_LATIN = 2;
    public static final int SUBLANG_SERBIAN_CYRILLIC = 3;
    public static final int SUBLANG_SPANISH = 1;
    public static final int SUBLANG_SPANISH_MEXICAN = 2;
    public static final int SUBLANG_SPANISH_MODERN = 3;
    public static final int SUBLANG_SPANISH_GUATEMALA = 4;
    public static final int SUBLANG_SPANISH_COSTA_RICA = 5;
    public static final int SUBLANG_SPANISH_PANAMA = 6;
    public static final int SUBLANG_SPANISH_DOMINICAN_REPUBLIC = 7;
    public static final int SUBLANG_SPANISH_VENEZUELA = 8;
    public static final int SUBLANG_SPANISH_COLOMBIA = 9;
    public static final int SUBLANG_SPANISH_PERU = 10;
    public static final int SUBLANG_SPANISH_ARGENTINA = 11;
    public static final int SUBLANG_SPANISH_ECUADOR = 12;
    public static final int SUBLANG_SPANISH_CHILE = 13;
    public static final int SUBLANG_SPANISH_URUGUAY = 14;
    public static final int SUBLANG_SPANISH_PARAGUAY = 15;
    public static final int SUBLANG_SPANISH_BOLIVIA = 16;
    public static final int SUBLANG_SPANISH_EL_SALVADOR = 17;
    public static final int SUBLANG_SPANISH_HONDURAS = 18;
    public static final int SUBLANG_SPANISH_NICARAGUA = 19;
    public static final int SUBLANG_SPANISH_PUERTO_RICO = 20;
    public static final int SUBLANG_SWEDISH = 1;
    public static final int SUBLANG_SWEDISH_FINLAND = 2;
    public static final int SUBLANG_URDU_PAKISTAN = 1;
    public static final int SUBLANG_URDU_INDIA = 2;
    public static final int SUBLANG_UZBEK_LATIN = 1;
    public static final int SUBLANG_UZBEK_CYRILLIC = 2;
    public static final int SORT_DEFAULT = 0;
    public static final int SORT_JAPANESE_XJIS = 0;
    public static final int SORT_JAPANESE_UNICODE = 1;
    public static final int SORT_CHINESE_BIG5 = 0;
    public static final int SORT_CHINESE_PRCP = 0;
    public static final int SORT_CHINESE_UNICODE = 1;
    public static final int SORT_CHINESE_PRC = 2;
    public static final int SORT_CHINESE_BOPOMOFO = 3;
    public static final int SORT_KOREAN_KSC = 0;
    public static final int SORT_KOREAN_UNICODE = 1;
    public static final int SORT_GERMAN_PHONE_BOOK = 1;
    public static final int SORT_HUNGARIAN_DEFAULT = 0;
    public static final int SORT_HUNGARIAN_TECHNICAL = 1;
    public static final int SORT_GEORGIAN_TRADITIONAL = 0;
    public static final int SORT_GEORGIAN_MODERN = 1;
    public static final int NLS_VALID_LOCALE_MASK = 1048575;
    public static final int LANG_SYSTEM_DEFAULT = LocaleMacros.MAKELANGID(0, 2);
    public static final int LANG_USER_DEFAULT = LocaleMacros.MAKELANGID(0, 1);
    public static final WinDef.LCID LOCALE_SYSTEM_DEFAULT = LocaleMacros.MAKELCID(LANG_SYSTEM_DEFAULT, 0);
    public static final WinDef.LCID LOCALE_USER_DEFAULT = LocaleMacros.MAKELCID(LANG_USER_DEFAULT, 0);
    public static final WinDef.LCID LOCALE_NEUTRAL = LocaleMacros.MAKELCID(LocaleMacros.MAKELANGID(0, 0), 0);
    public static final WinDef.LCID LOCALE_INVARIANT = LocaleMacros.MAKELCID(LocaleMacros.MAKELANGID(127, 0), 0);
    public static final int EVENT_MODIFY_STATE = 2;
    public static final int EVENT_ALL_ACCESS = 2031619;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$BATTERY_REPORTING_SCALE.class */
    @Structure.FieldOrder({"Granularity", "Capacity"})
    public static class BATTERY_REPORTING_SCALE extends Structure {
        public int Granularity;
        public int Capacity;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$CACHE_DESCRIPTOR.class */
    @Structure.FieldOrder({"level", "associativity", "lineSize", ElementTags.SIZE, "type"})
    public static class CACHE_DESCRIPTOR extends Structure {
        public WinDef.BYTE level;
        public WinDef.BYTE associativity;
        public WinDef.WORD lineSize;
        public WinDef.DWORD size;
        public int type;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$GENERIC_MAPPING.class */
    @Structure.FieldOrder({"genericRead", "genericWrite", "genericExecute", "genericAll"})
    public static class GENERIC_MAPPING extends Structure {
        public WinDef.DWORD genericRead;
        public WinDef.DWORD genericWrite;
        public WinDef.DWORD genericExecute;
        public WinDef.DWORD genericAll;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$GENERIC_MAPPING$ByReference.class */
        public static class ByReference extends GENERIC_MAPPING implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LOGICAL_PROCESSOR_RELATIONSHIP.class */
    public interface LOGICAL_PROCESSOR_RELATIONSHIP {
        public static final int RelationProcessorCore = 0;
        public static final int RelationNumaNode = 1;
        public static final int RelationCache = 2;
        public static final int RelationProcessorPackage = 3;
        public static final int RelationGroup = 4;
        public static final int RelationProcessorDie = 5;
        public static final int RelationNumaNodeEx = 6;
        public static final int RelationProcessorModule = 7;
        public static final int RelationAll = 65535;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LUID.class */
    @Structure.FieldOrder({"LowPart", "HighPart"})
    public static class LUID extends Structure {
        public int LowPart;
        public int HighPart;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$MEMORY_BASIC_INFORMATION.class */
    @Structure.FieldOrder({"baseAddress", "allocationBase", "allocationProtect", "regionSize", "state", "protect", "type"})
    public static class MEMORY_BASIC_INFORMATION extends Structure {
        public Pointer baseAddress;
        public Pointer allocationBase;
        public WinDef.DWORD allocationProtect;
        public BaseTSD.SIZE_T regionSize;
        public WinDef.DWORD state;
        public WinDef.DWORD protect;
        public WinDef.DWORD type;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$OVERLAPPED_COMPLETION_ROUTINE.class */
    public interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallLibrary.StdCallCallback {
        void callback(int i, int i2, WinBase.OVERLAPPED overlapped);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$POWER_ACTION.class */
    public interface POWER_ACTION {
        public static final int PowerActionNone = 0;
        public static final int PowerActionReserved = 1;
        public static final int PowerActionSleep = 2;
        public static final int PowerActionHibernate = 3;
        public static final int PowerActionShutdown = 4;
        public static final int PowerActionShutdownReset = 5;
        public static final int PowerActionShutdownOff = 6;
        public static final int PowerActionWarmEject = 7;
        public static final int PowerActionDisplayOff = 8;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$POWER_ACTION_POLICY.class */
    @Structure.FieldOrder({PDAction.TYPE, "Flags", "EventCode"})
    public static class POWER_ACTION_POLICY extends Structure {
        public int Action;
        public int Flags;
        public int EventCode;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PROCESSOR_CACHE_TYPE.class */
    public static abstract class PROCESSOR_CACHE_TYPE {
        public static int CacheUnified = 0;
        public static int CacheInstruction = 1;
        public static int CacheData = 2;
        public static int CacheTrace = 3;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SECURITY_IMPERSONATION_LEVEL.class */
    public static abstract class SECURITY_IMPERSONATION_LEVEL {
        public static final int SecurityAnonymous = 0;
        public static final int SecurityIdentification = 1;
        public static final int SecurityImpersonation = 2;
        public static final int SecurityDelegation = 3;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SID_NAME_USE.class */
    public static abstract class SID_NAME_USE {
        public static final int SidTypeUser = 1;
        public static final int SidTypeGroup = 2;
        public static final int SidTypeDomain = 3;
        public static final int SidTypeAlias = 4;
        public static final int SidTypeWellKnownGroup = 5;
        public static final int SidTypeDeletedAccount = 6;
        public static final int SidTypeInvalid = 7;
        public static final int SidTypeUnknown = 8;
        public static final int SidTypeComputer = 9;
        public static final int SidTypeLabel = 10;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_POWER_LEVEL.class */
    @Structure.FieldOrder({"Enable", "Spare", "BatteryLevel", "PowerPolicy", "MinSystemState"})
    public static class SYSTEM_POWER_LEVEL extends Structure {
        public byte Enable;
        public byte[] Spare = new byte[3];
        public int BatteryLevel;
        public POWER_ACTION_POLICY PowerPolicy;
        public int MinSystemState;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_POWER_STATE.class */
    public interface SYSTEM_POWER_STATE {
        public static final int PowerSystemUnspecified = 0;
        public static final int PowerSystemWorking = 1;
        public static final int PowerSystemSleeping1 = 2;
        public static final int PowerSystemSleeping2 = 3;
        public static final int PowerSystemSleeping3 = 4;
        public static final int PowerSystemHibernate = 5;
        public static final int PowerSystemShutdown = 6;
        public static final int PowerSystemMaximum = 7;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_INFORMATION_CLASS.class */
    public static abstract class TOKEN_INFORMATION_CLASS {
        public static final int TokenUser = 1;
        public static final int TokenGroups = 2;
        public static final int TokenPrivileges = 3;
        public static final int TokenOwner = 4;
        public static final int TokenPrimaryGroup = 5;
        public static final int TokenDefaultDacl = 6;
        public static final int TokenSource = 7;
        public static final int TokenType = 8;
        public static final int TokenImpersonationLevel = 9;
        public static final int TokenStatistics = 10;
        public static final int TokenRestrictedSids = 11;
        public static final int TokenSessionId = 12;
        public static final int TokenGroupsAndPrivileges = 13;
        public static final int TokenSessionReference = 14;
        public static final int TokenSandBoxInert = 15;
        public static final int TokenAuditPolicy = 16;
        public static final int TokenOrigin = 17;
        public static final int TokenElevationType = 18;
        public static final int TokenLinkedToken = 19;
        public static final int TokenElevation = 20;
        public static final int TokenHasRestrictions = 21;
        public static final int TokenAccessInformation = 22;
        public static final int TokenVirtualizationAllowed = 23;
        public static final int TokenVirtualizationEnabled = 24;
        public static final int TokenIntegrityLevel = 25;
        public static final int TokenUIAccess = 26;
        public static final int TokenMandatoryPolicy = 27;
        public static final int TokenLogonSid = 28;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_TYPE.class */
    public static abstract class TOKEN_TYPE {
        public static final int TokenPrimary = 1;
        public static final int TokenImpersonation = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$WELL_KNOWN_SID_TYPE.class */
    public static abstract class WELL_KNOWN_SID_TYPE {
        public static final int WinNullSid = 0;
        public static final int WinWorldSid = 1;
        public static final int WinLocalSid = 2;
        public static final int WinCreatorOwnerSid = 3;
        public static final int WinCreatorGroupSid = 4;
        public static final int WinCreatorOwnerServerSid = 5;
        public static final int WinCreatorGroupServerSid = 6;
        public static final int WinNtAuthoritySid = 7;
        public static final int WinDialupSid = 8;
        public static final int WinNetworkSid = 9;
        public static final int WinBatchSid = 10;
        public static final int WinInteractiveSid = 11;
        public static final int WinServiceSid = 12;
        public static final int WinAnonymousSid = 13;
        public static final int WinProxySid = 14;
        public static final int WinEnterpriseControllersSid = 15;
        public static final int WinSelfSid = 16;
        public static final int WinAuthenticatedUserSid = 17;
        public static final int WinRestrictedCodeSid = 18;
        public static final int WinTerminalServerSid = 19;
        public static final int WinRemoteLogonIdSid = 20;
        public static final int WinLogonIdsSid = 21;
        public static final int WinLocalSystemSid = 22;
        public static final int WinLocalServiceSid = 23;
        public static final int WinNetworkServiceSid = 24;
        public static final int WinBuiltinDomainSid = 25;
        public static final int WinBuiltinAdministratorsSid = 26;
        public static final int WinBuiltinUsersSid = 27;
        public static final int WinBuiltinGuestsSid = 28;
        public static final int WinBuiltinPowerUsersSid = 29;
        public static final int WinBuiltinAccountOperatorsSid = 30;
        public static final int WinBuiltinSystemOperatorsSid = 31;
        public static final int WinBuiltinPrintOperatorsSid = 32;
        public static final int WinBuiltinBackupOperatorsSid = 33;
        public static final int WinBuiltinReplicatorSid = 34;
        public static final int WinBuiltinPreWindows2000CompatibleAccessSid = 35;
        public static final int WinBuiltinRemoteDesktopUsersSid = 36;
        public static final int WinBuiltinNetworkConfigurationOperatorsSid = 37;
        public static final int WinAccountAdministratorSid = 38;
        public static final int WinAccountGuestSid = 39;
        public static final int WinAccountKrbtgtSid = 40;
        public static final int WinAccountDomainAdminsSid = 41;
        public static final int WinAccountDomainUsersSid = 42;
        public static final int WinAccountDomainGuestsSid = 43;
        public static final int WinAccountComputersSid = 44;
        public static final int WinAccountControllersSid = 45;
        public static final int WinAccountCertAdminsSid = 46;
        public static final int WinAccountSchemaAdminsSid = 47;
        public static final int WinAccountEnterpriseAdminsSid = 48;
        public static final int WinAccountPolicyAdminsSid = 49;
        public static final int WinAccountRasAndIasServersSid = 50;
        public static final int WinNTLMAuthenticationSid = 51;
        public static final int WinDigestAuthenticationSid = 52;
        public static final int WinSChannelAuthenticationSid = 53;
        public static final int WinThisOrganizationSid = 54;
        public static final int WinOtherOrganizationSid = 55;
        public static final int WinBuiltinIncomingForestTrustBuildersSid = 56;
        public static final int WinBuiltinPerfMonitoringUsersSid = 57;
        public static final int WinBuiltinPerfLoggingUsersSid = 58;
        public static final int WinBuiltinAuthorizationAccessSid = 59;
        public static final int WinBuiltinTerminalServerLicenseServersSid = 60;
        public static final int WinBuiltinDCOMUsersSid = 61;
        public static final int WinBuiltinIUsersSid = 62;
        public static final int WinIUserSid = 63;
        public static final int WinBuiltinCryptoOperatorsSid = 64;
        public static final int WinUntrustedLabelSid = 65;
        public static final int WinLowLabelSid = 66;
        public static final int WinMediumLabelSid = 67;
        public static final int WinHighLabelSid = 68;
        public static final int WinSystemLabelSid = 69;
        public static final int WinWriteRestrictedCodeSid = 70;
        public static final int WinCreatorOwnerRightsSid = 71;
        public static final int WinCacheablePrincipalsGroupSid = 72;
        public static final int WinNonCacheablePrincipalsGroupSid = 73;
        public static final int WinEnterpriseReadonlyControllersSid = 74;
        public static final int WinAccountReadonlyControllersSid = 75;
        public static final int WinBuiltinEventLogReadersGroup = 76;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LUID_AND_ATTRIBUTES.class */
    @Structure.FieldOrder({"Luid", "Attributes"})
    public static class LUID_AND_ATTRIBUTES extends Structure {
        public LUID Luid;
        public WinDef.DWORD Attributes;

        public LUID_AND_ATTRIBUTES() {
        }

        public LUID_AND_ATTRIBUTES(LUID luid, WinDef.DWORD attributes) {
            this.Luid = luid;
            this.Attributes = attributes;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SID_AND_ATTRIBUTES.class */
    @Structure.FieldOrder({"Sid", "Attributes"})
    public static class SID_AND_ATTRIBUTES extends Structure {
        public PSID.ByReference Sid;
        public int Attributes;

        public SID_AND_ATTRIBUTES() {
        }

        public SID_AND_ATTRIBUTES(Pointer memory) {
            super(memory);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_OWNER.class */
    @Structure.FieldOrder({"Owner"})
    public static class TOKEN_OWNER extends Structure {
        public PSID.ByReference Owner;

        public TOKEN_OWNER() {
        }

        public TOKEN_OWNER(int size) {
            super(new Memory(size));
        }

        public TOKEN_OWNER(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PSID.class */
    @Structure.FieldOrder({"sid"})
    public static class PSID extends Structure {
        public Pointer sid;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PSID$ByReference.class */
        public static class ByReference extends PSID implements Structure.ByReference {
        }

        public PSID() {
        }

        public PSID(byte[] data) {
            super(new Memory(data.length));
            getPointer().write(0L, data, 0, data.length);
            read();
        }

        public PSID(int size) {
            super(new Memory(size));
        }

        public PSID(Pointer memory) {
            super(memory);
            read();
        }

        public byte[] getBytes() {
            int len = Advapi32.INSTANCE.GetLengthSid(this);
            return getPointer().getByteArray(0L, len);
        }

        public String getSidString() {
            return Advapi32Util.convertSidToStringSid(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PSIDByReference.class */
    public static class PSIDByReference extends ByReference {
        public PSIDByReference() {
            this(null);
        }

        public PSIDByReference(PSID h) {
            super(Native.POINTER_SIZE);
            setValue(h);
        }

        public void setValue(PSID h) {
            getPointer().setPointer(0L, h != null ? h.getPointer() : null);
        }

        public PSID getValue() {
            Pointer p = getPointer().getPointer(0L);
            if (p == null) {
                return null;
            }
            return new PSID(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_USER.class */
    @Structure.FieldOrder({"User"})
    public static class TOKEN_USER extends Structure {
        public SID_AND_ATTRIBUTES User;

        public TOKEN_USER() {
        }

        public TOKEN_USER(Pointer memory) {
            super(memory);
            read();
        }

        public TOKEN_USER(int size) {
            super(new Memory(size));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_PRIMARY_GROUP.class */
    @Structure.FieldOrder({"PrimaryGroup"})
    public static class TOKEN_PRIMARY_GROUP extends Structure {
        public PSID.ByReference PrimaryGroup;

        public TOKEN_PRIMARY_GROUP() {
        }

        public TOKEN_PRIMARY_GROUP(Pointer memory) {
            super(memory);
            read();
        }

        public TOKEN_PRIMARY_GROUP(int size) {
            super(new Memory(size));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_GROUPS.class */
    @Structure.FieldOrder({"GroupCount", "Group0"})
    public static class TOKEN_GROUPS extends Structure {
        public int GroupCount;
        public SID_AND_ATTRIBUTES Group0;

        public TOKEN_GROUPS() {
        }

        public TOKEN_GROUPS(Pointer memory) {
            super(memory);
            read();
        }

        public TOKEN_GROUPS(int size) {
            super(new Memory(size));
        }

        public SID_AND_ATTRIBUTES[] getGroups() {
            return (SID_AND_ATTRIBUTES[]) this.Group0.toArray(this.GroupCount);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PRIVILEGE_SET.class */
    @Structure.FieldOrder({"PrivilegeCount", "Control", "Privileges"})
    public static class PRIVILEGE_SET extends Structure {
        public WinDef.DWORD PrivilegeCount;
        public WinDef.DWORD Control;
        public LUID_AND_ATTRIBUTES[] Privileges;

        public PRIVILEGE_SET() {
            this(0);
        }

        public PRIVILEGE_SET(int nbOfPrivileges) {
            this.PrivilegeCount = new WinDef.DWORD(nbOfPrivileges);
            if (nbOfPrivileges > 0) {
                this.Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
            }
        }

        public PRIVILEGE_SET(Pointer p) {
            super(p);
            int count = p.getInt(0L);
            this.PrivilegeCount = new WinDef.DWORD(count);
            if (count > 0) {
                this.Privileges = new LUID_AND_ATTRIBUTES[count];
            }
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$TOKEN_PRIVILEGES.class */
    @Structure.FieldOrder({"PrivilegeCount", "Privileges"})
    public static class TOKEN_PRIVILEGES extends Structure {
        public WinDef.DWORD PrivilegeCount;
        public LUID_AND_ATTRIBUTES[] Privileges;

        public TOKEN_PRIVILEGES() {
            this(0);
        }

        public TOKEN_PRIVILEGES(int nbOfPrivileges) {
            this.PrivilegeCount = new WinDef.DWORD(nbOfPrivileges);
            this.Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
        }

        public TOKEN_PRIVILEGES(Pointer p) {
            super(p);
            int count = p.getInt(0L);
            this.PrivilegeCount = new WinDef.DWORD(count);
            this.Privileges = new LUID_AND_ATTRIBUTES[count];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$FILE_NOTIFY_INFORMATION.class */
    @Structure.FieldOrder({"NextEntryOffset", PDAction.TYPE, "FileNameLength", "FileName"})
    public static class FILE_NOTIFY_INFORMATION extends Structure {
        public int NextEntryOffset;
        public int Action;
        public int FileNameLength;
        public char[] FileName;

        private FILE_NOTIFY_INFORMATION() {
            this.FileName = new char[1];
        }

        public FILE_NOTIFY_INFORMATION(int size) {
            this.FileName = new char[1];
            if (size < size()) {
                throw new IllegalArgumentException("Size must greater than " + size() + ", requested " + size);
            }
            allocateMemory(size);
        }

        public String getFilename() {
            return new String(this.FileName, 0, this.FileNameLength / 2);
        }

        @Override // com.sun.jna.Structure
        public void read() {
            this.FileName = new char[0];
            super.read();
            this.FileName = getPointer().getCharArray(12L, this.FileNameLength / 2);
        }

        public FILE_NOTIFY_INFORMATION next() {
            if (this.NextEntryOffset == 0) {
                return null;
            }
            FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
            next.useMemory(getPointer(), this.NextEntryOffset);
            next.read();
            return next;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LARGE_INTEGER.class */
    @Structure.FieldOrder({"u"})
    public static class LARGE_INTEGER extends Structure implements Comparable<LARGE_INTEGER> {
        public UNION u;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LARGE_INTEGER$ByReference.class */
        public static class ByReference extends LARGE_INTEGER implements Structure.ByReference {
            @Override // com.sun.jna.platform.win32.WinNT.LARGE_INTEGER, java.lang.Comparable
            public /* bridge */ /* synthetic */ int compareTo(LARGE_INTEGER large_integer) {
                return super.compareTo(large_integer);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LARGE_INTEGER$LowHigh.class */
        @Structure.FieldOrder({"LowPart", "HighPart"})
        public static class LowHigh extends Structure {
            public WinDef.DWORD LowPart;
            public WinDef.DWORD HighPart;

            public LowHigh() {
            }

            public LowHigh(long value) {
                this(new WinDef.DWORD(value & 4294967295L), new WinDef.DWORD((value >> 32) & 4294967295L));
            }

            public LowHigh(WinDef.DWORD low, WinDef.DWORD high) {
                this.LowPart = low;
                this.HighPart = high;
            }

            public long longValue() {
                long loValue = this.LowPart.longValue();
                long hiValue = this.HighPart.longValue();
                return ((hiValue << 32) & (-4294967296L)) | (loValue & 4294967295L);
            }

            @Override // com.sun.jna.Structure
            public String toString() {
                if (this.LowPart == null || this.HighPart == null) {
                    return "null";
                }
                return Long.toString(longValue());
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LARGE_INTEGER$UNION.class */
        public static class UNION extends Union {
            public LowHigh lh;
            public long value;

            public UNION() {
            }

            public UNION(long value) {
                this.value = value;
                this.lh = new LowHigh(value);
            }

            @Override // com.sun.jna.Structure
            public void read() {
                readField("lh");
                readField(ES6Iterator.VALUE_PROPERTY);
            }

            public long longValue() {
                return this.value;
            }

            @Override // com.sun.jna.Structure
            public String toString() {
                return Long.toString(longValue());
            }
        }

        public LARGE_INTEGER() {
        }

        public LARGE_INTEGER(long value) {
            this.u = new UNION(value);
        }

        public WinDef.DWORD getLow() {
            return this.u.lh.LowPart;
        }

        public WinDef.DWORD getHigh() {
            return this.u.lh.HighPart;
        }

        public long getValue() {
            return this.u.value;
        }

        @Override // java.lang.Comparable
        public int compareTo(LARGE_INTEGER other) {
            return compare(this, other);
        }

        @Override // com.sun.jna.Structure
        public String toString() {
            return this.u == null ? "null" : Long.toString(getValue());
        }

        public static int compare(LARGE_INTEGER v1, LARGE_INTEGER v2) {
            if (v1 == v2) {
                return 0;
            }
            if (v1 == null) {
                return 1;
            }
            if (v2 == null) {
                return -1;
            }
            return IntegerType.compare(v1.getValue(), v2.getValue());
        }

        public static int compare(LARGE_INTEGER v1, long v2) {
            if (v1 == null) {
                return 1;
            }
            return IntegerType.compare(v1.getValue(), v2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$HANDLE.class */
    public static class HANDLE extends PointerType {
        private boolean immutable;

        public HANDLE() {
        }

        public HANDLE(Pointer p) {
            setPointer(p);
            this.immutable = true;
        }

        @Override // com.sun.jna.PointerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Object o = super.fromNative(nativeValue, context);
            if (WinBase.INVALID_HANDLE_VALUE.equals(o)) {
                return WinBase.INVALID_HANDLE_VALUE;
            }
            return o;
        }

        @Override // com.sun.jna.PointerType
        public void setPointer(Pointer p) {
            if (this.immutable) {
                throw new UnsupportedOperationException("immutable reference");
            }
            super.setPointer(p);
        }

        @Override // com.sun.jna.PointerType
        public String toString() {
            return String.valueOf(getPointer());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$HANDLEByReference.class */
    public static class HANDLEByReference extends ByReference {
        public HANDLEByReference() {
            this(null);
        }

        public HANDLEByReference(HANDLE h) {
            super(Native.POINTER_SIZE);
            setValue(h);
        }

        public void setValue(HANDLE h) {
            getPointer().setPointer(0L, h != null ? h.getPointer() : null);
        }

        public HANDLE getValue() {
            Pointer p = getPointer().getPointer(0L);
            if (p == null) {
                return null;
            }
            if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
                return WinBase.INVALID_HANDLE_VALUE;
            }
            HANDLE h = new HANDLE();
            h.setPointer(p);
            return h;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$HRESULT.class */
    public static class HRESULT extends NativeLong {
        public HRESULT() {
        }

        public HRESULT(int value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$OSVERSIONINFO.class */
    @Structure.FieldOrder({"dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion"})
    public static class OSVERSIONINFO extends Structure {
        public WinDef.DWORD dwOSVersionInfoSize;
        public WinDef.DWORD dwMajorVersion;
        public WinDef.DWORD dwMinorVersion;
        public WinDef.DWORD dwBuildNumber;
        public WinDef.DWORD dwPlatformId;
        public char[] szCSDVersion;

        public OSVERSIONINFO() {
            this.szCSDVersion = new char[128];
            this.dwOSVersionInfoSize = new WinDef.DWORD(size());
        }

        public OSVERSIONINFO(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$OSVERSIONINFOEX.class */
    @Structure.FieldOrder({"dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion", "wServicePackMajor", "wServicePackMinor", "wSuiteMask", "wProductType", "wReserved"})
    public static class OSVERSIONINFOEX extends Structure {
        public WinDef.DWORD dwOSVersionInfoSize;
        public WinDef.DWORD dwMajorVersion;
        public WinDef.DWORD dwMinorVersion;
        public WinDef.DWORD dwBuildNumber;
        public WinDef.DWORD dwPlatformId;
        public char[] szCSDVersion;
        public WinDef.WORD wServicePackMajor;
        public WinDef.WORD wServicePackMinor;
        public WinDef.WORD wSuiteMask;
        public byte wProductType;
        public byte wReserved;

        public OSVERSIONINFOEX() {
            this.szCSDVersion = new char[128];
            this.dwOSVersionInfoSize = new WinDef.DWORD(size());
        }

        public OSVERSIONINFOEX(Pointer memory) {
            super(memory);
            read();
        }

        public int getMajor() {
            return this.dwMajorVersion.intValue();
        }

        public int getMinor() {
            return this.dwMinorVersion.intValue();
        }

        public int getBuildNumber() {
            return this.dwBuildNumber.intValue();
        }

        public int getPlatformId() {
            return this.dwPlatformId.intValue();
        }

        public String getServicePack() {
            return Native.toString(this.szCSDVersion);
        }

        public int getSuiteMask() {
            return this.wSuiteMask.intValue();
        }

        public byte getProductType() {
            return this.wProductType;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$EVENTLOGRECORD.class */
    @Structure.FieldOrder({"Length", "Reserved", "RecordNumber", "TimeGenerated", "TimeWritten", "EventID", "EventType", "NumStrings", "EventCategory", "ReservedFlags", "ClosingRecordNumber", "StringOffset", "UserSidLength", "UserSidOffset", "DataLength", "DataOffset"})
    public static class EVENTLOGRECORD extends Structure {
        public WinDef.DWORD Length;
        public WinDef.DWORD Reserved;
        public WinDef.DWORD RecordNumber;
        public WinDef.DWORD TimeGenerated;
        public WinDef.DWORD TimeWritten;
        public WinDef.DWORD EventID;
        public WinDef.WORD EventType;
        public WinDef.WORD NumStrings;
        public WinDef.WORD EventCategory;
        public WinDef.WORD ReservedFlags;
        public WinDef.DWORD ClosingRecordNumber;
        public WinDef.DWORD StringOffset;
        public WinDef.DWORD UserSidLength;
        public WinDef.DWORD UserSidOffset;
        public WinDef.DWORD DataLength;
        public WinDef.DWORD DataOffset;

        public EVENTLOGRECORD() {
        }

        public EVENTLOGRECORD(Pointer p) {
            super(p);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SECURITY_DESCRIPTOR.class */
    @Structure.FieldOrder({"data"})
    public static class SECURITY_DESCRIPTOR extends Structure {
        public byte[] data;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SECURITY_DESCRIPTOR$ByReference.class */
        public static class ByReference extends SECURITY_DESCRIPTOR implements Structure.ByReference {
        }

        public SECURITY_DESCRIPTOR() {
        }

        public SECURITY_DESCRIPTOR(byte[] data) {
            this.data = data;
            useMemory(new Memory(data.length));
        }

        public SECURITY_DESCRIPTOR(int size) {
            useMemory(new Memory(size));
            this.data = new byte[size];
        }

        public SECURITY_DESCRIPTOR(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$ACL.class */
    @Structure.FieldOrder({"AclRevision", "Sbz1", "AclSize", "AceCount", "Sbz2"})
    public static class ACL extends Structure {
        public static int MAX_ACL_SIZE = 65536;
        public byte AclRevision;
        public byte Sbz1;
        public short AclSize;
        public short AceCount;
        public short Sbz2;

        public ACL() {
        }

        public ACL(int size) {
            useMemory(new Memory(size));
        }

        public ACL(Pointer pointer) {
            super(pointer);
            read();
        }

        public ACE_HEADER[] getACEs() {
            ACE_HEADER[] ACEs = new ACE_HEADER[this.AceCount];
            Pointer pointer = getPointer();
            int offset = size();
            for (int i = 0; i < this.AceCount; i++) {
                Pointer share = pointer.share(offset);
                byte aceType = share.getByte(0L);
                switch (aceType) {
                    case 0:
                        ACEs[i] = new ACCESS_ALLOWED_ACE(share);
                        break;
                    case 1:
                        ACEs[i] = new ACCESS_DENIED_ACE(share);
                        break;
                    default:
                        ACEs[i] = new ACE_HEADER(share);
                        break;
                }
                offset += ACEs[i].AceSize;
            }
            return ACEs;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PACLByReference.class */
    public static class PACLByReference extends ByReference {
        public PACLByReference() {
            this(null);
        }

        public PACLByReference(ACL h) {
            super(Native.POINTER_SIZE);
            setValue(h);
        }

        public void setValue(ACL h) {
            getPointer().setPointer(0L, h != null ? h.getPointer() : null);
        }

        public ACL getValue() {
            Pointer p = getPointer().getPointer(0L);
            if (p == null) {
                return null;
            }
            return new ACL(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SECURITY_DESCRIPTOR_RELATIVE.class */
    @Structure.FieldOrder({"Revision", "Sbz1", "Control", "Owner", PDAnnotationMarkup.RT_GROUP, "Sacl", "Dacl"})
    public static class SECURITY_DESCRIPTOR_RELATIVE extends Structure {
        public byte Revision;
        public byte Sbz1;
        public short Control;
        public int Owner;
        public int Group;
        public int Sacl;
        public int Dacl;
        private PSID OWNER;
        private PSID GROUP;
        private ACL SACL;
        private ACL DACL;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SECURITY_DESCRIPTOR_RELATIVE$ByReference.class */
        public static class ByReference extends SECURITY_DESCRIPTOR_RELATIVE implements Structure.ByReference {
        }

        public SECURITY_DESCRIPTOR_RELATIVE() {
        }

        public SECURITY_DESCRIPTOR_RELATIVE(byte[] data) {
            super(new Memory(data.length));
            getPointer().write(0L, data, 0, data.length);
            setMembers();
        }

        public SECURITY_DESCRIPTOR_RELATIVE(int length) {
            super(new Memory(length));
        }

        public SECURITY_DESCRIPTOR_RELATIVE(Pointer p) {
            super(p);
            setMembers();
        }

        public PSID getOwner() {
            return this.OWNER;
        }

        public PSID getGroup() {
            return this.GROUP;
        }

        public ACL getDiscretionaryACL() {
            return this.DACL;
        }

        public ACL getSystemACL() {
            return this.SACL;
        }

        private final void setMembers() {
            read();
            if (this.Dacl != 0) {
                this.DACL = new ACL(getPointer().share(this.Dacl));
            }
            if (this.Sacl != 0) {
                this.SACL = new ACL(getPointer().share(this.Sacl));
            }
            if (this.Group != 0) {
                this.GROUP = new PSID(getPointer().share(this.Group));
            }
            if (this.Owner != 0) {
                this.OWNER = new PSID(getPointer().share(this.Owner));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$ACE_HEADER.class */
    @Structure.FieldOrder({"AceType", "AceFlags", "AceSize"})
    public static class ACE_HEADER extends Structure {
        public byte AceType;
        public byte AceFlags;
        public short AceSize;

        public ACE_HEADER() {
        }

        public ACE_HEADER(Pointer p) {
            super(p);
            read();
        }

        public ACE_HEADER(byte AceType, byte AceFlags, short AceSize) {
            this.AceType = AceType;
            this.AceFlags = AceFlags;
            this.AceSize = AceSize;
            write();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$ACCESS_ACEStructure.class */
    @Structure.FieldOrder({"Mask", "SidStart"})
    public static abstract class ACCESS_ACEStructure extends ACE_HEADER {
        public int Mask;
        public byte[] SidStart;
        PSID psid;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !WinNT.class.desiredAssertionStatus();
        }

        public ACCESS_ACEStructure() {
            this.SidStart = new byte[4];
        }

        public ACCESS_ACEStructure(int Mask, byte AceType, byte AceFlags, PSID psid) {
            this.SidStart = new byte[4];
            calculateSize(true);
            this.AceType = AceType;
            this.AceFlags = AceFlags;
            this.AceSize = (short) (super.fieldOffset("SidStart") + psid.getBytes().length);
            this.psid = psid;
            this.Mask = Mask;
            this.SidStart = psid.getPointer().getByteArray(0L, this.SidStart.length);
            allocateMemory(this.AceSize);
            write();
        }

        public ACCESS_ACEStructure(Pointer p) {
            super(p);
            this.SidStart = new byte[4];
            read();
        }

        public String getSidString() {
            return Advapi32Util.convertSidToStringSid(this.psid);
        }

        public PSID getSID() {
            return this.psid;
        }

        @Override // com.sun.jna.Structure
        public void write() {
            super.write();
            int offsetOfSID = super.fieldOffset("SidStart");
            int sizeOfSID = this.AceSize - super.fieldOffset("SidStart");
            if (this.psid != null) {
                byte[] psidWrite = this.psid.getBytes();
                if (!$assertionsDisabled && psidWrite.length > sizeOfSID) {
                    throw new AssertionError();
                }
                getPointer().write(offsetOfSID, psidWrite, 0, sizeOfSID);
            }
        }

        @Override // com.sun.jna.Structure
        public void read() {
            if (this.SidStart == null) {
                this.SidStart = new byte[4];
            }
            super.read();
            int offsetOfSID = super.fieldOffset("SidStart");
            int sizeOfSID = this.AceSize - super.fieldOffset("SidStart");
            if (sizeOfSID > 0) {
                this.psid = new PSID(getPointer().getByteArray(offsetOfSID, sizeOfSID));
            } else {
                this.psid = new PSID();
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$ACCESS_ALLOWED_ACE.class */
    public static class ACCESS_ALLOWED_ACE extends ACCESS_ACEStructure {
        public ACCESS_ALLOWED_ACE() {
        }

        public ACCESS_ALLOWED_ACE(Pointer p) {
            super(p);
        }

        public ACCESS_ALLOWED_ACE(int Mask, byte AceFlags, PSID psid) {
            super(Mask, (byte) 0, AceFlags, psid);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$ACCESS_DENIED_ACE.class */
    public static class ACCESS_DENIED_ACE extends ACCESS_ACEStructure {
        public ACCESS_DENIED_ACE() {
        }

        public ACCESS_DENIED_ACE(Pointer p) {
            super(p);
        }

        public ACCESS_DENIED_ACE(int Mask, byte AceFlags, PSID psid) {
            super(Mask, (byte) 1, AceFlags, psid);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_LOGICAL_PROCESSOR_INFORMATION.class */
    @Structure.FieldOrder({"processorMask", "relationship", "payload"})
    public static class SYSTEM_LOGICAL_PROCESSOR_INFORMATION extends Structure {
        public BaseTSD.ULONG_PTR processorMask;
        public int relationship;
        public AnonymousUnionPayload payload;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_LOGICAL_PROCESSOR_INFORMATION$AnonymousStructNumaNode.class */
        @Structure.FieldOrder({"nodeNumber"})
        public static class AnonymousStructNumaNode extends Structure {
            public WinDef.DWORD nodeNumber;
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_LOGICAL_PROCESSOR_INFORMATION$AnonymousStructProcessorCore.class */
        @Structure.FieldOrder({"flags"})
        public static class AnonymousStructProcessorCore extends Structure {
            public WinDef.BYTE flags;
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_LOGICAL_PROCESSOR_INFORMATION$AnonymousUnionPayload.class */
        public static class AnonymousUnionPayload extends Union {
            public AnonymousStructProcessorCore processorCore;
            public AnonymousStructNumaNode numaNode;
            public CACHE_DESCRIPTOR cache;
            public WinDef.ULONGLONG[] reserved = new WinDef.ULONGLONG[2];
        }

        public SYSTEM_LOGICAL_PROCESSOR_INFORMATION() {
        }

        public SYSTEM_LOGICAL_PROCESSOR_INFORMATION(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.class */
    @Structure.FieldOrder({"relationship", ElementTags.SIZE})
    public static abstract class SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX extends Structure {
        public int relationship;
        public int size;

        public SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX() {
        }

        protected SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX(Pointer memory) {
            super(memory);
        }

        public static SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX fromPointer(Pointer memory) {
            SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX result;
            int relationship = memory.getInt(0L);
            switch (relationship) {
                case 0:
                case 3:
                case 5:
                case 7:
                    result = new PROCESSOR_RELATIONSHIP(memory);
                    break;
                case 1:
                case 6:
                    result = new NUMA_NODE_RELATIONSHIP(memory);
                    break;
                case 2:
                    result = new CACHE_RELATIONSHIP(memory);
                    break;
                case 4:
                    result = new GROUP_RELATIONSHIP(memory);
                    break;
                default:
                    result = new UNKNOWN_RELATIONSHIP(memory);
                    break;
            }
            result.read();
            return result;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PROCESSOR_RELATIONSHIP.class */
    @Structure.FieldOrder({"flags", "efficiencyClass", "reserved", "groupCount", "groupMask"})
    public static class PROCESSOR_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
        public byte flags;
        public byte efficiencyClass;
        public byte[] reserved;
        public short groupCount;
        public GROUP_AFFINITY[] groupMask;

        public PROCESSOR_RELATIONSHIP() {
            this.reserved = new byte[20];
            this.groupMask = new GROUP_AFFINITY[1];
        }

        public PROCESSOR_RELATIONSHIP(Pointer memory) {
            super(memory);
            this.reserved = new byte[20];
            this.groupMask = new GROUP_AFFINITY[1];
        }

        @Override // com.sun.jna.Structure
        public void read() {
            readField("groupCount");
            if (this.groupCount != this.groupMask.length) {
                this.groupMask = new GROUP_AFFINITY[this.groupCount];
            }
            super.read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$NUMA_NODE_RELATIONSHIP.class */
    @Structure.FieldOrder({"nodeNumber", "reserved", "groupCount", "groupMasks"})
    public static class NUMA_NODE_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
        public int nodeNumber;
        public byte[] reserved;
        public short groupCount;
        public GROUP_AFFINITY groupMask;
        public GROUP_AFFINITY[] groupMasks;

        public NUMA_NODE_RELATIONSHIP() {
            this.reserved = new byte[18];
            this.groupMasks = new GROUP_AFFINITY[1];
        }

        public NUMA_NODE_RELATIONSHIP(Pointer memory) {
            super(memory);
            this.reserved = new byte[18];
            this.groupMasks = new GROUP_AFFINITY[1];
        }

        @Override // com.sun.jna.Structure
        public void read() {
            readField("groupCount");
            int actualGroupCount = Math.max(1, (int) this.groupCount);
            if (actualGroupCount != this.groupMasks.length) {
                this.groupMasks = new GROUP_AFFINITY[actualGroupCount];
            }
            super.read();
            this.groupMask = this.groupMasks[0];
        }

        @Override // com.sun.jna.Structure
        protected List<Field> getFieldList() {
            List<Field> fields = new ArrayList<>(super.getFieldList());
            Iterator<Field> fieldIterator = fields.iterator();
            while (fieldIterator.hasNext()) {
                Field field = fieldIterator.next();
                if ("groupMask".equals(field.getName())) {
                    fieldIterator.remove();
                }
            }
            return fields;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$CACHE_RELATIONSHIP.class */
    @Structure.FieldOrder({"level", "associativity", "lineSize", "cacheSize", "type", "reserved", "groupMask"})
    public static class CACHE_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
        public byte level;
        public byte associativity;
        public short lineSize;
        public int cacheSize;
        public int type;
        public byte[] reserved;
        public GROUP_AFFINITY groupMask;

        public CACHE_RELATIONSHIP() {
            this.reserved = new byte[20];
        }

        public CACHE_RELATIONSHIP(Pointer memory) {
            super(memory);
            this.reserved = new byte[20];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$GROUP_RELATIONSHIP.class */
    @Structure.FieldOrder({"maximumGroupCount", "activeGroupCount", "reserved", "groupInfo"})
    public static class GROUP_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
        public short maximumGroupCount;
        public short activeGroupCount;
        public byte[] reserved;
        public PROCESSOR_GROUP_INFO[] groupInfo;

        public GROUP_RELATIONSHIP() {
            this.reserved = new byte[20];
            this.groupInfo = new PROCESSOR_GROUP_INFO[1];
        }

        public GROUP_RELATIONSHIP(Pointer memory) {
            super(memory);
            this.reserved = new byte[20];
            this.groupInfo = new PROCESSOR_GROUP_INFO[1];
        }

        @Override // com.sun.jna.Structure
        public void read() {
            readField("activeGroupCount");
            if (this.activeGroupCount != this.groupInfo.length) {
                this.groupInfo = new PROCESSOR_GROUP_INFO[this.activeGroupCount];
            }
            super.read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$UNKNOWN_RELATIONSHIP.class */
    @Structure.FieldOrder({})
    public static class UNKNOWN_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
        public UNKNOWN_RELATIONSHIP() {
        }

        public UNKNOWN_RELATIONSHIP(Pointer memory) {
            super(memory);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$GROUP_AFFINITY.class */
    @Structure.FieldOrder({"mask", "group", "reserved"})
    public static class GROUP_AFFINITY extends Structure {
        public BaseTSD.ULONG_PTR mask;
        public short group;
        public short[] reserved;

        public GROUP_AFFINITY(Pointer memory) {
            super(memory);
            this.reserved = new short[3];
        }

        public GROUP_AFFINITY() {
            this.reserved = new short[3];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PROCESSOR_GROUP_INFO.class */
    @Structure.FieldOrder({"maximumProcessorCount", "activeProcessorCount", "reserved", "activeProcessorMask"})
    public static class PROCESSOR_GROUP_INFO extends Structure {
        public byte maximumProcessorCount;
        public byte activeProcessorCount;
        public byte[] reserved;
        public BaseTSD.ULONG_PTR activeProcessorMask;

        public PROCESSOR_GROUP_INFO(Pointer memory) {
            super(memory);
            this.reserved = new byte[38];
        }

        public PROCESSOR_GROUP_INFO() {
            this.reserved = new byte[38];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_BATTERY_STATE.class */
    @Structure.FieldOrder({"AcOnLine", "BatteryPresent", "Charging", "Discharging", "Spare1", PDAnnotationFileAttachment.ATTACHMENT_NAME_TAG, "MaxCapacity", "RemainingCapacity", "Rate", "EstimatedTime", "DefaultAlert1", "DefaultAlert2"})
    public static class SYSTEM_BATTERY_STATE extends Structure {
        public byte AcOnLine;
        public byte BatteryPresent;
        public byte Charging;
        public byte Discharging;
        public byte[] Spare1;
        public byte Tag;
        public int MaxCapacity;
        public int RemainingCapacity;
        public int Rate;
        public int EstimatedTime;
        public int DefaultAlert1;
        public int DefaultAlert2;

        public SYSTEM_BATTERY_STATE(Pointer p) {
            super(p);
            this.Spare1 = new byte[3];
            read();
        }

        public SYSTEM_BATTERY_STATE() {
            this.Spare1 = new byte[3];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$PROCESSOR_POWER_INFORMATION.class */
    @Structure.FieldOrder({"Number", "MaxMhz", "CurrentMhz", "MhzLimit", "MaxIdleState", "CurrentIdleState"})
    public static class PROCESSOR_POWER_INFORMATION extends Structure {
        public int Number;
        public int MaxMhz;
        public int CurrentMhz;
        public int MhzLimit;
        public int MaxIdleState;
        public int CurrentIdleState;

        public PROCESSOR_POWER_INFORMATION(Pointer p) {
            super(p);
            read();
        }

        public PROCESSOR_POWER_INFORMATION() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_POWER_INFORMATION.class */
    @Structure.FieldOrder({"MaxIdlenessAllowed", "Idleness", "TimeRemaining", "CoolingMode"})
    public static class SYSTEM_POWER_INFORMATION extends Structure {
        public int MaxIdlenessAllowed;
        public int Idleness;
        public int TimeRemaining;
        public byte CoolingMode;

        public SYSTEM_POWER_INFORMATION(Pointer p) {
            super(p);
            read();
        }

        public SYSTEM_POWER_INFORMATION() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_POWER_POLICY.class */
    @Structure.FieldOrder({"Revision", "PowerButton", "SleepButton", "LidClose", "LidOpenWake", "Reserved", "Idle", "IdleTimeout", "IdleSensitivity", "DynamicThrottle", "Spare2", "MinSleep", "MaxSleep", "ReducedLatencySleep", "WinLogonFlags", "Spare3", "DozeS4Timeout", "BroadcastCapacityResolution", "DischargePolicy", "VideoTimeout", "VideoDimDisplay", "VideoReserved", "SpindownTimeout", "OptimizeForPower", "FanThrottleTolerance", "ForcedThrottle", "MinThrottle", "OverThrottled"})
    public static class SYSTEM_POWER_POLICY extends Structure {
        public int Revision;
        public POWER_ACTION_POLICY PowerButton;
        public POWER_ACTION_POLICY SleepButton;
        public POWER_ACTION_POLICY LidClose;
        public int LidOpenWake;
        public int Reserved;
        public POWER_ACTION_POLICY Idle;
        public int IdleTimeout;
        public byte IdleSensitivity;
        public byte DynamicThrottle;
        public byte[] Spare2;
        public int MinSleep;
        public int MaxSleep;
        public int ReducedLatencySleep;
        public int WinLogonFlags;
        public int Spare3;
        public int DozeS4Timeout;
        public int BroadcastCapacityResolution;
        public SYSTEM_POWER_LEVEL[] DischargePolicy;
        public int VideoTimeout;
        public byte VideoDimDisplay;
        public int[] VideoReserved;
        public int SpindownTimeout;
        public byte OptimizeForPower;
        public byte FanThrottleTolerance;
        public byte ForcedThrottle;
        public byte MinThrottle;
        public POWER_ACTION_POLICY OverThrottled;

        public SYSTEM_POWER_POLICY(Pointer p) {
            super(p);
            this.Spare2 = new byte[2];
            this.DischargePolicy = new SYSTEM_POWER_LEVEL[4];
            this.VideoReserved = new int[3];
            read();
        }

        public SYSTEM_POWER_POLICY() {
            this.Spare2 = new byte[2];
            this.DischargePolicy = new SYSTEM_POWER_LEVEL[4];
            this.VideoReserved = new int[3];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SYSTEM_POWER_CAPABILITIES.class */
    @Structure.FieldOrder({"PowerButtonPresent", "SleepButtonPresent", "LidPresent", "SystemS1", "SystemS2", "SystemS3", "SystemS4", "SystemS5", "HiberFilePresent", "FullWake", "VideoDimPresent", "ApmPresent", "UpsPresent", "ThermalControl", "ProcessorThrottle", "ProcessorMinThrottle", "ProcessorMaxThrottle", "FastSystemS4", "Hiberboot", "WakeAlarmPresent", "AoAc", "DiskSpinDown", "HiberFileType", "AoAcConnectivitySupported", "spare3", "SystemBatteriesPresent", "BatteriesAreShortTerm", "BatteryScale", "AcOnLineWake", "SoftLidWake", "RtcWake", "MinDeviceWakeState", "DefaultLowLatencyWake"})
    public static class SYSTEM_POWER_CAPABILITIES extends Structure {
        public byte PowerButtonPresent;
        public byte SleepButtonPresent;
        public byte LidPresent;
        public byte SystemS1;
        public byte SystemS2;
        public byte SystemS3;
        public byte SystemS4;
        public byte SystemS5;
        public byte HiberFilePresent;
        public byte FullWake;
        public byte VideoDimPresent;
        public byte ApmPresent;
        public byte UpsPresent;
        public byte ThermalControl;
        public byte ProcessorThrottle;
        public byte ProcessorMinThrottle;
        public byte ProcessorMaxThrottle;
        public byte FastSystemS4;
        public byte Hiberboot;
        public byte WakeAlarmPresent;
        public byte AoAc;
        public byte DiskSpinDown;
        public byte HiberFileType;
        public byte AoAcConnectivitySupported;
        public byte[] spare3;
        public byte SystemBatteriesPresent;
        public byte BatteriesAreShortTerm;
        public BATTERY_REPORTING_SCALE[] BatteryScale;
        public int AcOnLineWake;
        public int SoftLidWake;
        public int RtcWake;
        public int MinDeviceWakeState;
        public int DefaultLowLatencyWake;

        public SYSTEM_POWER_CAPABILITIES(Pointer p) {
            super(p);
            this.spare3 = new byte[6];
            this.BatteryScale = new BATTERY_REPORTING_SCALE[3];
            read();
        }

        public SYSTEM_POWER_CAPABILITIES() {
            this.spare3 = new byte[6];
            this.BatteryScale = new BATTERY_REPORTING_SCALE[3];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$SECURITY_QUALITY_OF_SERVICE.class */
    @Structure.FieldOrder({"Length", "ImpersonationLevel", "ContextTrackingMode", "EffectiveOnly"})
    public static class SECURITY_QUALITY_OF_SERVICE extends Structure {
        public int Length;
        public int ImpersonationLevel;
        public byte ContextTrackingMode;
        public byte EffectiveOnly;

        @Override // com.sun.jna.Structure
        public void write() {
            this.Length = size();
            super.write();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$LocaleMacros.class */
    public static final class LocaleMacros {
        private static final int _MAKELCID(int lgid, int srtid) {
            return (srtid << 16) | lgid;
        }

        public static final WinDef.LCID MAKELCID(int lgid, int srtid) {
            return new WinDef.LCID(_MAKELCID(lgid, srtid));
        }

        public static final WinDef.LCID MAKESORTLCID(int lgid, int srtid, int ver) {
            return new WinDef.LCID(_MAKELCID(lgid, srtid) | (ver << 20));
        }

        public static final int LANGIDFROMLCID(WinDef.LCID lcid) {
            return lcid.intValue() & 65535;
        }

        public static final int SORTIDFROMLCID(WinDef.LCID lcid) {
            return (lcid.intValue() >>> 16) & 15;
        }

        public static final int SORTVERSIONFROMLCID(WinDef.LCID lcid) {
            return (lcid.intValue() >>> 20) & 15;
        }

        public static final int MAKELANGID(int p, int s) {
            return (s << 10) | (p & 65535);
        }

        public static final int PRIMARYLANGID(int lgid) {
            return lgid & WinUser.CF_GDIOBJLAST;
        }

        public static final int SUBLANGID(int lgid) {
            return (lgid & 65535) >>> 10;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinNT$IO_COUNTERS.class */
    @Structure.FieldOrder({"ReadOperationCount", "WriteOperationCount", "OtherOperationCount", "ReadTransferCount", "WriteTransferCount", "OtherTransferCount"})
    public static class IO_COUNTERS extends Structure {
        public long ReadOperationCount;
        public long WriteOperationCount;
        public long OtherOperationCount;
        public long ReadTransferCount;
        public long WriteTransferCount;
        public long OtherTransferCount;

        public IO_COUNTERS() {
        }

        public IO_COUNTERS(Pointer memory) {
            super(memory);
        }
    }
}
