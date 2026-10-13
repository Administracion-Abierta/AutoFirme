package nu.xom;

import com.sun.jna.platform.win32.WinError;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/ISOHebrewWriter.class */
class ISOHebrewWriter extends TextWriter {
    ISOHebrewWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case 162:
                return false;
            case 163:
                return false;
            case 164:
                return false;
            case 165:
                return false;
            case 166:
                return false;
            case 167:
                return false;
            case 168:
                return false;
            case 169:
                return false;
            case 170:
            default:
                switch (c) {
                    case 1488:
                        return false;
                    case 1489:
                        return false;
                    case 1490:
                        return false;
                    case 1491:
                        return false;
                    case 1492:
                        return false;
                    case 1493:
                        return false;
                    case 1494:
                        return false;
                    case 1495:
                        return false;
                    case 1496:
                        return false;
                    case 1497:
                        return false;
                    case 1498:
                        return false;
                    case 1499:
                        return false;
                    case WinError.ERROR_EVENTLOG_FILE_CORRUPT /* 1500 */:
                        return false;
                    case WinError.ERROR_EVENTLOG_CANT_START /* 1501 */:
                        return false;
                    case WinError.ERROR_LOG_FILE_FULL /* 1502 */:
                        return false;
                    case WinError.ERROR_EVENTLOG_FILE_CHANGED /* 1503 */:
                        return false;
                    case 1504:
                        return false;
                    case 1505:
                        return false;
                    case 1506:
                        return false;
                    case 1507:
                        return false;
                    case 1508:
                        return false;
                    case 1509:
                        return false;
                    case 1510:
                        return false;
                    case 1511:
                        return false;
                    case 1512:
                        return false;
                    case 1513:
                        return false;
                    case 1514:
                        return false;
                    default:
                        switch (c) {
                            case WinError.ERROR_NESTING_NOT_ALLOWED /* 215 */:
                                return false;
                            case 247:
                                return false;
                            case WinError.ERROR_DS_CANT_MOD_OBJ_CLASS /* 8215 */:
                                return false;
                            default:
                                return true;
                        }
                }
            case 171:
                return false;
            case 172:
                return false;
            case 173:
                return false;
            case 174:
                return false;
            case 175:
                return true;
            case 176:
                return false;
            case 177:
                return false;
            case 178:
                return false;
            case 179:
                return false;
            case 180:
                return false;
            case 181:
                return false;
            case 182:
                return false;
            case 183:
                return false;
            case 184:
                return false;
            case 185:
                return false;
            case 186:
                return true;
            case 187:
                return false;
            case 188:
                return false;
            case 189:
                return false;
            case 190:
                return false;
        }
    }
}
