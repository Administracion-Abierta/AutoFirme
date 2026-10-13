package nu.xom;

import com.aowagie.text.pdf.Barcode128;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.iptc.IPTC;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Latin9Writer.class */
class Latin9Writer extends TextWriter {
    Latin9Writer(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 163 || c == 8364) {
            return false;
        }
        switch (c) {
            case 165:
                return false;
            case 166:
                return true;
            case 167:
                return false;
            case 168:
                return true;
            case 169:
                return false;
            case 170:
                return false;
            case 171:
                return false;
            case 172:
                return false;
            case 173:
                return false;
            case 174:
                return false;
            case 175:
                return false;
            case 176:
                return false;
            case 177:
                return false;
            case 178:
                return false;
            case 179:
                return false;
            case 180:
                return true;
            case 181:
                return false;
            case 182:
                return false;
            case 183:
                return false;
            case 184:
                return true;
            case 185:
                return false;
            case 186:
                return false;
            case 187:
                return false;
            case 188:
                return true;
            case 189:
                return true;
            case 190:
                return true;
            case 191:
                return false;
            case 192:
                return false;
            case 193:
                return false;
            case 194:
                return false;
            case 195:
                return false;
            case 196:
                return false;
            case 197:
                return false;
            case 198:
                return false;
            case 199:
                return false;
            case 200:
                return false;
            case 201:
                return false;
            case 202:
                return false;
            case 203:
                return false;
            case Barcode128.STARTB /* 204 */:
                return false;
            case 205:
                return false;
            case WinError.ERROR_FILENAME_EXCED_RANGE /* 206 */:
                return false;
            case WinError.ERROR_RING2_STACK_IN_USE /* 207 */:
                return false;
            case WinError.ERROR_META_EXPANSION_TOO_LONG /* 208 */:
                return false;
            case WinError.ERROR_INVALID_SIGNAL_NUMBER /* 209 */:
                return false;
            case WinError.ERROR_THREAD_1_INACTIVE /* 210 */:
                return false;
            case 211:
                return false;
            case WinError.ERROR_LOCKED /* 212 */:
                return false;
            case 213:
                return false;
            case WinError.ERROR_TOO_MANY_MODULES /* 214 */:
                return false;
            case WinError.ERROR_NESTING_NOT_ALLOWED /* 215 */:
                return false;
            case WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH /* 216 */:
                return false;
            case WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY /* 217 */:
                return false;
            case WinError.ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY /* 218 */:
                return false;
            case 219:
                return false;
            case WinError.ERROR_FILE_CHECKED_OUT /* 220 */:
                return false;
            case WinError.ERROR_CHECKOUT_REQUIRED /* 221 */:
                return false;
            case WinError.ERROR_BAD_FILE_TYPE /* 222 */:
                return false;
            case WinError.ERROR_FILE_TOO_LARGE /* 223 */:
                return false;
            case 224:
                return false;
            case WinError.ERROR_VIRUS_INFECTED /* 225 */:
                return false;
            case WinError.ERROR_VIRUS_DELETED /* 226 */:
                return false;
            case 227:
                return false;
            case 228:
                return false;
            case WinError.ERROR_PIPE_LOCAL /* 229 */:
                return false;
            case WinError.ERROR_BAD_PIPE /* 230 */:
                return false;
            case WinError.ERROR_PIPE_BUSY /* 231 */:
                return false;
            case WinError.ERROR_NO_DATA /* 232 */:
                return false;
            case WinError.ERROR_PIPE_NOT_CONNECTED /* 233 */:
                return false;
            case WinError.ERROR_MORE_DATA /* 234 */:
                return false;
            case 235:
                return false;
            case 236:
                return false;
            case 237:
                return false;
            case 238:
                return false;
            case 239:
                return false;
            case 240:
                return false;
            case 241:
                return false;
            case 242:
                return false;
            case 243:
                return false;
            case 244:
                return false;
            case 245:
                return false;
            case 246:
                return false;
            case 247:
                return false;
            case 248:
                return false;
            case 249:
                return false;
            case 250:
                return false;
            case 251:
                return false;
            case DHCPOptions.OPTION_PROXY_AUTODISCOVERY /* 252 */:
                return false;
            case 253:
                return false;
            case 254:
                return false;
            case 255:
                return false;
            default:
                switch (c) {
                    case TIFF.TAG_EXTRA_SAMPLES /* 338 */:
                        return false;
                    case TIFF.TAG_SAMPLE_FORMAT /* 339 */:
                        return false;
                    case 340:
                        return true;
                    case 341:
                        return true;
                    case TIFF.TAG_TRANSFER_RANGE /* 342 */:
                        return true;
                    case 343:
                        return true;
                    case 344:
                        return true;
                    case 345:
                        return true;
                    case IPTC.TAG_CODED_CHARACTER_SET /* 346 */:
                        return true;
                    case TIFF.TAG_JPEG_TABLES /* 347 */:
                        return true;
                    case 348:
                        return true;
                    case 349:
                        return true;
                    case WinError.ERROR_FAIL_NOACTION_REBOOT /* 350 */:
                        return true;
                    case WinError.ERROR_FAIL_SHUTDOWN /* 351 */:
                        return true;
                    case WinError.ERROR_FAIL_RESTART /* 352 */:
                        return false;
                    case WinError.ERROR_MAX_SESSIONS_REACHED /* 353 */:
                        return false;
                    case 354:
                        return true;
                    case 355:
                        return true;
                    case 356:
                        return true;
                    case 357:
                        return true;
                    case 358:
                        return true;
                    case 359:
                        return true;
                    case 360:
                        return true;
                    case 361:
                        return true;
                    case 362:
                        return true;
                    case 363:
                        return true;
                    case 364:
                        return true;
                    case 365:
                        return true;
                    case 366:
                        return true;
                    case 367:
                        return true;
                    case 368:
                        return true;
                    case 369:
                        return true;
                    case 370:
                        return true;
                    case 371:
                        return true;
                    case 372:
                        return true;
                    case 373:
                        return true;
                    case 374:
                        return true;
                    case 375:
                        return true;
                    case 376:
                        return false;
                    case 377:
                    case 378:
                    case 379:
                    case 380:
                    default:
                        return true;
                    case 381:
                        return false;
                    case 382:
                        return false;
                }
        }
    }
}
