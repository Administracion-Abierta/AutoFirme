package nu.xom;

import com.aowagie.text.pdf.Barcode128;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Latin8Writer.class */
class Latin8Writer extends TextWriter {
    Latin8Writer(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case 163:
                return false;
            case 164:
                return true;
            case 165:
                return true;
            case 166:
                return true;
            case 167:
                return false;
            case 168:
                return true;
            case 169:
                return false;
            case 170:
                return true;
            case 171:
                return true;
            case 172:
                return true;
            case 173:
                return false;
            case 174:
                return false;
            case 175:
                return true;
            case 176:
                return true;
            case 177:
                return true;
            case 178:
                return true;
            case 179:
                return true;
            case 180:
                return true;
            case 181:
                return true;
            case 182:
                return false;
            case 183:
                return true;
            case 184:
                return true;
            case 185:
                return true;
            case 186:
                return true;
            case 187:
                return true;
            case 188:
                return true;
            case 189:
                return true;
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
                return true;
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
                return true;
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
                return false;
            case 246:
                return false;
            case 247:
            default:
                switch (c) {
                    case 372:
                        return false;
                    case 373:
                        return false;
                    case 374:
                        return false;
                    case 375:
                        return false;
                    case 376:
                        return false;
                    default:
                        switch (c) {
                            case 7682:
                                return false;
                            case 7683:
                                return false;
                            case 7684:
                                return true;
                            case 7685:
                                return true;
                            case 7686:
                                return true;
                            case 7687:
                                return true;
                            case 7688:
                                return true;
                            case 7689:
                                return true;
                            case 7690:
                                return false;
                            case 7691:
                                return false;
                            case 7692:
                                return true;
                            case 7693:
                                return true;
                            case 7694:
                                return true;
                            case 7695:
                                return true;
                            case 7696:
                                return true;
                            case 7697:
                                return true;
                            case 7698:
                                return true;
                            case 7699:
                                return true;
                            case 7700:
                                return true;
                            case 7701:
                                return true;
                            case 7702:
                                return true;
                            case 7703:
                                return true;
                            case 7704:
                                return true;
                            case 7705:
                                return true;
                            case 7706:
                                return true;
                            case 7707:
                                return true;
                            case 7708:
                                return true;
                            case 7709:
                                return true;
                            case 7710:
                                return false;
                            case 7711:
                                return false;
                            case 7712:
                                return true;
                            case 7713:
                                return true;
                            case 7714:
                                return true;
                            case 7715:
                                return true;
                            case 7716:
                                return true;
                            case 7717:
                                return true;
                            case 7718:
                                return true;
                            case 7719:
                                return true;
                            case 7720:
                                return true;
                            case 7721:
                                return true;
                            case 7722:
                                return true;
                            case 7723:
                                return true;
                            case 7724:
                                return true;
                            case 7725:
                                return true;
                            case 7726:
                                return true;
                            case 7727:
                                return true;
                            case 7728:
                                return true;
                            case 7729:
                                return true;
                            case 7730:
                                return true;
                            case 7731:
                                return true;
                            case 7732:
                                return true;
                            case 7733:
                                return true;
                            case 7734:
                                return true;
                            case 7735:
                                return true;
                            case 7736:
                                return true;
                            case 7737:
                                return true;
                            case 7738:
                                return true;
                            case 7739:
                                return true;
                            case 7740:
                                return true;
                            case 7741:
                                return true;
                            case 7742:
                                return true;
                            case 7743:
                                return true;
                            case 7744:
                                return false;
                            case 7745:
                                return false;
                            case 7746:
                                return true;
                            case 7747:
                                return true;
                            case 7748:
                                return true;
                            case 7749:
                                return true;
                            case 7750:
                                return true;
                            case 7751:
                                return true;
                            case 7752:
                                return true;
                            case 7753:
                                return true;
                            case 7754:
                                return true;
                            case 7755:
                                return true;
                            case 7756:
                                return true;
                            case 7757:
                                return true;
                            case 7758:
                                return true;
                            case 7759:
                                return true;
                            case 7760:
                                return true;
                            case 7761:
                                return true;
                            case 7762:
                                return true;
                            case 7763:
                                return true;
                            case 7764:
                                return true;
                            case 7765:
                                return true;
                            case 7766:
                                return false;
                            case 7767:
                                return false;
                            case 7768:
                                return true;
                            case 7769:
                                return true;
                            case 7770:
                                return true;
                            case 7771:
                                return true;
                            case 7772:
                                return true;
                            case 7773:
                                return true;
                            case 7774:
                                return true;
                            case 7775:
                                return true;
                            case 7776:
                                return false;
                            case 7777:
                                return false;
                            case 7778:
                                return true;
                            case 7779:
                                return true;
                            case 7780:
                                return true;
                            case 7781:
                                return true;
                            case 7782:
                                return true;
                            case 7783:
                                return true;
                            case 7784:
                                return true;
                            case 7785:
                                return true;
                            case 7786:
                                return false;
                            case 7787:
                                return false;
                            case 7788:
                                return true;
                            case 7789:
                                return true;
                            case 7790:
                                return true;
                            case 7791:
                                return true;
                            case 7792:
                                return true;
                            case 7793:
                                return true;
                            case 7794:
                                return true;
                            case 7795:
                                return true;
                            case 7796:
                                return true;
                            case 7797:
                                return true;
                            case 7798:
                                return true;
                            case 7799:
                                return true;
                            case 7800:
                                return true;
                            case 7801:
                                return true;
                            case 7802:
                                return true;
                            case 7803:
                                return true;
                            case 7804:
                                return true;
                            case 7805:
                                return true;
                            case 7806:
                                return true;
                            case 7807:
                                return true;
                            case 7808:
                                return false;
                            case 7809:
                                return false;
                            case 7810:
                                return false;
                            case 7811:
                                return false;
                            case 7812:
                                return false;
                            case 7813:
                                return false;
                            default:
                                switch (c) {
                                    case 7922:
                                        return false;
                                    case 7923:
                                        return false;
                                    default:
                                        return true;
                                }
                        }
                }
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
                return true;
            case 255:
                return false;
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
                return true;
            case 265:
                return true;
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
                return true;
            case TIFF.TAG_PAGE_NAME /* 285 */:
                return true;
            case TIFF.TAG_X_POSITION /* 286 */:
                return true;
            case TIFF.TAG_Y_POSITION /* 287 */:
                return true;
            case 288:
                return false;
            case TIFF.TAG_FREE_BYTE_COUNTS /* 289 */:
                return false;
        }
    }
}
