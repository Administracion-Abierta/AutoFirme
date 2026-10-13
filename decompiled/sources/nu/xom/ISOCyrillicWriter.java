package nu.xom;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.psd.PSD;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/ISOCyrillicWriter.class */
class ISOCyrillicWriter extends TextWriter {
    ISOCyrillicWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case 1025:
                return false;
            case 1026:
                return false;
            case 1027:
                return false;
            case PSD.RES_IPTC_NAA /* 1028 */:
                return false;
            case 1029:
                return false;
            case 1030:
                return false;
            case 1031:
                return false;
            case 1032:
                return false;
            case 1033:
                return false;
            case 1034:
                return false;
            case 1035:
                return false;
            case 1036:
                return false;
            case 1037:
                return true;
            case 1038:
                return false;
            case PSD.RES_ICC_PROFILE /* 1039 */:
                return false;
            case 1040:
                return false;
            case 1041:
                return false;
            case 1042:
                return false;
            case 1043:
                return false;
            case 1044:
                return false;
            case 1045:
                return false;
            case 1046:
                return false;
            case 1047:
                return false;
            case 1048:
                return false;
            case 1049:
                return false;
            case 1050:
                return false;
            case WinError.ERROR_DEPENDENT_SERVICES_RUNNING /* 1051 */:
                return false;
            case WinError.ERROR_INVALID_SERVICE_CONTROL /* 1052 */:
                return false;
            case WinError.ERROR_SERVICE_REQUEST_TIMEOUT /* 1053 */:
                return false;
            case WinError.ERROR_SERVICE_NO_THREAD /* 1054 */:
                return false;
            case WinError.ERROR_SERVICE_DATABASE_LOCKED /* 1055 */:
                return false;
            case WinError.ERROR_SERVICE_ALREADY_RUNNING /* 1056 */:
                return false;
            case WinError.ERROR_INVALID_SERVICE_ACCOUNT /* 1057 */:
                return false;
            case WinError.ERROR_SERVICE_DISABLED /* 1058 */:
                return false;
            case WinError.ERROR_CIRCULAR_DEPENDENCY /* 1059 */:
                return false;
            case WinError.ERROR_SERVICE_DOES_NOT_EXIST /* 1060 */:
                return false;
            case WinError.ERROR_SERVICE_CANNOT_ACCEPT_CTRL /* 1061 */:
                return false;
            case WinError.ERROR_SERVICE_NOT_ACTIVE /* 1062 */:
                return false;
            case WinError.ERROR_FAILED_SERVICE_CONTROLLER_CONNECT /* 1063 */:
                return false;
            case WinError.ERROR_EXCEPTION_IN_SERVICE /* 1064 */:
                return false;
            case WinError.ERROR_DATABASE_DOES_NOT_EXIST /* 1065 */:
                return false;
            case WinError.ERROR_SERVICE_SPECIFIC_ERROR /* 1066 */:
                return false;
            case WinError.ERROR_PROCESS_ABORTED /* 1067 */:
                return false;
            case WinError.ERROR_SERVICE_DEPENDENCY_FAIL /* 1068 */:
                return false;
            case WinError.ERROR_SERVICE_LOGON_FAILED /* 1069 */:
                return false;
            case WinError.ERROR_SERVICE_START_HANG /* 1070 */:
                return false;
            case WinError.ERROR_INVALID_SERVICE_LOCK /* 1071 */:
                return false;
            case WinError.ERROR_SERVICE_MARKED_FOR_DELETE /* 1072 */:
                return false;
            case WinError.ERROR_SERVICE_EXISTS /* 1073 */:
                return false;
            case WinError.ERROR_ALREADY_RUNNING_LKG /* 1074 */:
                return false;
            case WinError.ERROR_SERVICE_DEPENDENCY_DELETED /* 1075 */:
                return false;
            case WinError.ERROR_BOOT_ALREADY_ACCEPTED /* 1076 */:
                return false;
            case WinError.ERROR_SERVICE_NEVER_STARTED /* 1077 */:
                return false;
            case WinError.ERROR_DUPLICATE_SERVICE_NAME /* 1078 */:
                return false;
            case WinError.ERROR_DIFFERENT_SERVICE_ACCOUNT /* 1079 */:
                return false;
            case WinError.ERROR_CANNOT_DETECT_DRIVER_FAILURE /* 1080 */:
                return false;
            case WinError.ERROR_CANNOT_DETECT_PROCESS_ABORT /* 1081 */:
                return false;
            case WinError.ERROR_NO_RECOVERY_PROGRAM /* 1082 */:
                return false;
            case WinError.ERROR_SERVICE_NOT_IN_EXE /* 1083 */:
                return false;
            case WinError.ERROR_NOT_SAFEBOOT_SERVICE /* 1084 */:
                return false;
            case 1085:
                return false;
            case 1086:
                return false;
            case 1087:
                return false;
            case 1088:
                return false;
            case 1089:
                return false;
            case 1090:
                return false;
            case 1091:
                return false;
            case 1092:
                return false;
            case 1093:
                return false;
            case 1094:
                return false;
            case 1095:
                return false;
            case 1096:
                return false;
            case 1097:
                return false;
            case 1098:
                return false;
            case 1099:
                return false;
            case WinError.ERROR_END_OF_MEDIA /* 1100 */:
                return false;
            case WinError.ERROR_FILEMARK_DETECTED /* 1101 */:
                return false;
            case WinError.ERROR_BEGINNING_OF_MEDIA /* 1102 */:
                return false;
            case WinError.ERROR_SETMARK_DETECTED /* 1103 */:
                return false;
            case WinError.ERROR_NO_DATA_DETECTED /* 1104 */:
                return true;
            case WinError.ERROR_PARTITION_FAILURE /* 1105 */:
                return false;
            case WinError.ERROR_INVALID_BLOCK_LENGTH /* 1106 */:
                return false;
            case WinError.ERROR_DEVICE_NOT_PARTITIONED /* 1107 */:
                return false;
            case WinError.ERROR_UNABLE_TO_LOCK_MEDIA /* 1108 */:
                return false;
            case WinError.ERROR_UNABLE_TO_UNLOAD_MEDIA /* 1109 */:
                return false;
            case WinError.ERROR_MEDIA_CHANGED /* 1110 */:
                return false;
            case WinError.ERROR_BUS_RESET /* 1111 */:
                return false;
            case WinError.ERROR_NO_MEDIA_IN_DRIVE /* 1112 */:
                return false;
            case WinError.ERROR_NO_UNICODE_TRANSLATION /* 1113 */:
                return false;
            case WinError.ERROR_DLL_INIT_FAILED /* 1114 */:
                return false;
            case WinError.ERROR_SHUTDOWN_IN_PROGRESS /* 1115 */:
                return false;
            case WinError.ERROR_NO_SHUTDOWN_IN_PROGRESS /* 1116 */:
                return false;
            case WinError.ERROR_IO_DEVICE /* 1117 */:
                return true;
            case WinError.ERROR_SERIAL_NO_DEVICE /* 1118 */:
                return false;
            case WinError.ERROR_IRQ_BUSY /* 1119 */:
                return false;
            default:
                switch (c) {
                    case 167:
                        return false;
                    case 173:
                        return false;
                    case WinError.ERROR_DS_NAME_ERROR_NOT_FOUND /* 8470 */:
                        return false;
                    default:
                        return true;
                }
        }
    }
}
