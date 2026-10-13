package nu.xom;

import com.aowagie.text.pdf.Barcode128;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.iptc.IPTC;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Latin3Writer.class */
class Latin3Writer extends TextWriter {
    Latin3Writer(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case 161:
                return true;
            case 162:
                return true;
            case 163:
                return true;
            case 164:
                return false;
            case 165:
                return true;
            case 166:
                return true;
            case 167:
                return false;
            case 168:
                return false;
            case 169:
                return true;
            case 170:
                return true;
            case 171:
                return true;
            case 172:
                return true;
            case 173:
                return false;
            case 174:
                return true;
            case 175:
                return true;
            case 176:
                return false;
            case 177:
                return true;
            case 178:
                return false;
            case 179:
                return false;
            case 180:
                return false;
            case 181:
                return false;
            case 182:
            case 213:
            case WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH /* 216 */:
            case WinError.ERROR_TOO_MANY_POSTS /* 298 */:
            case WinError.ERROR_PARTIAL_COPY /* 299 */:
            case 300:
            case 301:
            case WinError.ERROR_DISK_TOO_FRAGMENTED /* 302 */:
            case WinError.ERROR_DELETE_PENDING /* 303 */:
            default:
                switch (c) {
                    case WinError.ERROR_FIRMWARE_UPDATED /* 728 */:
                        return false;
                    case WinError.ERROR_DRIVERS_LEAKING_LOCKED_PAGES /* 729 */:
                        return false;
                    default:
                        return true;
                }
            case 183:
                return false;
            case 184:
                return false;
            case 185:
                return true;
            case 186:
                return true;
            case 187:
                return true;
            case 188:
                return true;
            case 189:
                return false;
            case 190:
                return true;
            case 191:
                return true;
            case 192:
                return false;
            case 193:
                return false;
            case 194:
                return false;
            case 195:
                return true;
            case 196:
                return false;
            case 197:
                return true;
            case 198:
                return true;
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
                return true;
            case WinError.ERROR_INVALID_SIGNAL_NUMBER /* 209 */:
                return false;
            case WinError.ERROR_THREAD_1_INACTIVE /* 210 */:
                return false;
            case 211:
                return false;
            case WinError.ERROR_LOCKED /* 212 */:
                return false;
            case WinError.ERROR_TOO_MANY_MODULES /* 214 */:
                return false;
            case WinError.ERROR_NESTING_NOT_ALLOWED /* 215 */:
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
                return true;
            case WinError.ERROR_BAD_FILE_TYPE /* 222 */:
                return true;
            case WinError.ERROR_FILE_TOO_LARGE /* 223 */:
                return false;
            case 224:
                return false;
            case WinError.ERROR_VIRUS_INFECTED /* 225 */:
                return false;
            case WinError.ERROR_VIRUS_DELETED /* 226 */:
                return false;
            case 227:
                return true;
            case 228:
                return false;
            case WinError.ERROR_PIPE_LOCAL /* 229 */:
                return true;
            case WinError.ERROR_BAD_PIPE /* 230 */:
                return true;
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
                return true;
            case 241:
                return false;
            case 242:
                return false;
            case 243:
                return false;
            case 244:
                return false;
            case 245:
                return true;
            case 246:
                return false;
            case 247:
                return false;
            case 248:
                return true;
            case 249:
                return false;
            case 250:
                return false;
            case 251:
                return false;
            case DHCPOptions.OPTION_PROXY_AUTODISCOVERY /* 252 */:
                return false;
            case 253:
                return true;
            case 254:
                return true;
            case 255:
                return true;
            case 256:
                return true;
            case 257:
                return true;
            case 258:
                return true;
            case 259:
                return true;
            case 260:
                return true;
            case 261:
                return true;
            case TIFF.TAG_PHOTOMETRIC_INTERPRETATION /* 262 */:
                return true;
            case 263:
                return true;
            case 264:
                return false;
            case 265:
                return false;
            case 266:
                return false;
            case WinError.ERROR_DIRECTORY /* 267 */:
                return false;
            case 268:
                return true;
            case TIFF.TAG_DOCUMENT_NAME /* 269 */:
                return true;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                return true;
            case TIFF.TAG_MAKE /* 271 */:
                return true;
            case 272:
                return true;
            case TIFF.TAG_STRIP_OFFSETS /* 273 */:
                return true;
            case 274:
                return true;
            case WinError.ERROR_EAS_DIDNT_FIT /* 275 */:
                return true;
            case WinError.ERROR_EA_FILE_CORRUPT /* 276 */:
                return true;
            case 277:
                return true;
            case 278:
                return true;
            case TIFF.TAG_STRIP_BYTE_COUNTS /* 279 */:
                return true;
            case 280:
                return true;
            case 281:
                return true;
            case 282:
                return true;
            case TIFF.TAG_Y_RESOLUTION /* 283 */:
                return true;
            case TIFF.TAG_PLANAR_CONFIGURATION /* 284 */:
                return false;
            case TIFF.TAG_PAGE_NAME /* 285 */:
                return false;
            case TIFF.TAG_X_POSITION /* 286 */:
                return false;
            case TIFF.TAG_Y_POSITION /* 287 */:
                return false;
            case 288:
                return false;
            case TIFF.TAG_FREE_BYTE_COUNTS /* 289 */:
                return false;
            case 290:
                return true;
            case 291:
                return true;
            case TIFF.TAG_GROUP3OPTIONS /* 292 */:
                return false;
            case TIFF.TAG_GROUP4OPTIONS /* 293 */:
                return false;
            case 294:
                return false;
            case 295:
                return false;
            case TIFF.TAG_RESOLUTION_UNIT /* 296 */:
                return true;
            case TIFF.TAG_PAGE_NUMBER /* 297 */:
                return true;
            case 304:
                return false;
            case TIFF.TAG_SOFTWARE /* 305 */:
                return false;
            case 306:
                return true;
            case 307:
                return true;
            case 308:
                return false;
            case 309:
                return false;
            case 310:
                return true;
            case 311:
                return true;
            case 312:
                return true;
            case 313:
                return true;
            case 314:
                return true;
            case TIFF.TAG_ARTIST /* 315 */:
                return true;
            case TIFF.TAG_HOST_COMPUTER /* 316 */:
                return true;
            case 317:
                return true;
            case 318:
                return true;
            case 319:
                return true;
            case 320:
                return true;
            case 321:
                return true;
            case TIFF.TAG_TILE_WIDTH /* 322 */:
                return true;
            case TIFF.TAG_TILE_HEIGTH /* 323 */:
                return true;
            case TIFF.TAG_TILE_OFFSETS /* 324 */:
                return true;
            case TIFF.TAG_TILE_BYTE_COUNTS /* 325 */:
                return true;
            case 326:
                return true;
            case 327:
                return true;
            case 328:
                return true;
            case 329:
                return true;
            case TIFF.TAG_SUB_IFD /* 330 */:
                return true;
            case 331:
                return true;
            case TIFF.TAG_INK_SET /* 332 */:
                return true;
            case TIFF.TAG_INK_NAMES /* 333 */:
                return true;
            case TIFF.TAG_NUMBER_OF_INKS /* 334 */:
                return true;
            case 335:
                return true;
            case 336:
                return true;
            case 337:
                return true;
            case TIFF.TAG_EXTRA_SAMPLES /* 338 */:
                return true;
            case TIFF.TAG_SAMPLE_FORMAT /* 339 */:
                return true;
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
                return false;
            case 349:
                return false;
            case WinError.ERROR_FAIL_NOACTION_REBOOT /* 350 */:
                return false;
            case WinError.ERROR_FAIL_SHUTDOWN /* 351 */:
                return false;
            case WinError.ERROR_FAIL_RESTART /* 352 */:
                return true;
            case WinError.ERROR_MAX_SESSIONS_REACHED /* 353 */:
                return true;
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
                return false;
            case 365:
                return false;
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
                return true;
            case 377:
                return true;
            case 378:
                return true;
            case 379:
                return false;
            case 380:
                return false;
        }
    }
}
