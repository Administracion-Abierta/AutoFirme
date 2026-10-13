package nu.xom;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.Writer;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.Primes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Latin2Writer.class */
class Latin2Writer extends TextWriter {
    Latin2Writer(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_GCM_SHA384 /* 161 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_128_GCM_SHA256 /* 162 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 /* 163 */:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256 /* 164 */:
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384 /* 165 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256 /* 166 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 /* 167 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256 /* 168 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384 /* 169 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256 /* 170 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384 /* 171 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256 /* 172 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256 /* 174 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384 /* 175 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_NULL_SHA256 /* 176 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_NULL_SHA384 /* 177 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA256 /* 178 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384 /* 179 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA256 /* 180 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384 /* 181 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256 /* 182 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384 /* 183 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA256 /* 184 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384 /* 185 */:
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 186 */:
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
            case 188:
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 189 */:
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 190 */:
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256 /* 191 */:
            default:
                switch (c) {
                    case 711:
                        return false;
                    case 712:
                        return true;
                    case 713:
                        return true;
                    case 714:
                        return true;
                    case 715:
                        return true;
                    case 716:
                        return true;
                    case 717:
                        return true;
                    case 718:
                        return true;
                    case 719:
                        return true;
                    case 720:
                        return true;
                    case 721:
                        return true;
                    case 722:
                        return true;
                    case 723:
                        return true;
                    case 724:
                        return true;
                    case 725:
                        return true;
                    case 726:
                        return true;
                    case 727:
                        return true;
                    case 728:
                        return false;
                    case 729:
                        return false;
                    case 730:
                        return true;
                    case 731:
                        return false;
                    case 732:
                        return true;
                    case 733:
                        return false;
                    default:
                        return true;
                }
            case 192:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 193 */:
                return false;
            case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 194 */:
                return false;
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 195 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 196 */:
                return false;
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256 /* 197 */:
                return true;
            case 198:
                return true;
            case 199:
                return false;
            case DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE /* 200 */:
                return true;
            case 201:
                return false;
            case 202:
                return true;
            case 203:
                return false;
            case 204:
                return true;
            case 205:
                return false;
            case 206:
                return false;
            case 207:
                return true;
            case 208:
                return true;
            case 209:
                return true;
            case 210:
                return true;
            case Primes.SMALL_FACTOR_LIMIT /* 211 */:
                return false;
            case 212:
                return false;
            case 213:
                return true;
            case 214:
                return false;
            case 215:
                return false;
            case 216:
                return true;
            case 217:
                return true;
            case 218:
                return false;
            case 219:
                return true;
            case 220:
                return false;
            case 221:
                return false;
            case 222:
                return true;
            case 223:
                return false;
            case 224:
                return true;
            case 225:
                return false;
            case 226:
                return false;
            case 227:
                return true;
            case 228:
                return false;
            case 229:
                return true;
            case 230:
                return true;
            case 231:
                return false;
            case 232:
                return true;
            case 233:
                return false;
            case 234:
                return true;
            case 235:
                return false;
            case 236:
                return true;
            case 237:
                return false;
            case 238:
                return false;
            case 239:
                return true;
            case 240:
                return true;
            case 241:
                return true;
            case 242:
                return true;
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
                return true;
            case 250:
                return false;
            case 251:
                return true;
            case 252:
                return false;
            case 253:
                return false;
            case 254:
                return true;
            case 255:
                return true;
            case 256:
                return true;
            case 257:
                return true;
            case 258:
                return false;
            case 259:
                return false;
            case 260:
                return false;
            case 261:
                return false;
            case 262:
                return false;
            case 263:
                return false;
            case 264:
                return true;
            case 265:
                return true;
            case 266:
                return true;
            case 267:
                return true;
            case 268:
                return false;
            case 269:
                return false;
            case 270:
                return false;
            case 271:
                return false;
            case XmlConsts.XML_V_11 /* 272 */:
                return false;
            case 273:
                return false;
            case 274:
                return true;
            case 275:
                return true;
            case 276:
                return true;
            case 277:
                return true;
            case 278:
                return true;
            case 279:
                return true;
            case 280:
                return false;
            case 281:
                return false;
            case 282:
                return false;
            case 283:
                return false;
            case 284:
                return true;
            case 285:
                return true;
            case 286:
                return true;
            case 287:
                return true;
            case 288:
                return true;
            case 289:
                return true;
            case 290:
                return true;
            case 291:
                return true;
            case 292:
                return true;
            case 293:
                return true;
            case 294:
                return true;
            case 295:
                return true;
            case 296:
                return true;
            case 297:
                return true;
            case 298:
                return true;
            case 299:
                return true;
            case 300:
                return true;
            case 301:
                return true;
            case 302:
                return true;
            case 303:
                return true;
            case 304:
                return true;
            case 305:
                return true;
            case 306:
                return true;
            case 307:
                return true;
            case 308:
                return true;
            case 309:
                return true;
            case 310:
                return true;
            case 311:
                return true;
            case 312:
                return true;
            case 313:
                return false;
            case 314:
                return false;
            case 315:
                return true;
            case 316:
                return true;
            case 317:
                return false;
            case 318:
                return false;
            case 319:
                return true;
            case 320:
                return true;
            case 321:
                return false;
            case 322:
                return false;
            case 323:
                return false;
            case 324:
                return false;
            case 325:
                return true;
            case 326:
                return true;
            case 327:
                return false;
            case 328:
                return false;
            case 329:
                return true;
            case 330:
                return true;
            case 331:
                return true;
            case 332:
                return true;
            case 333:
                return true;
            case 334:
                return true;
            case 335:
                return true;
            case 336:
                return false;
            case 337:
                return false;
            case 338:
                return true;
            case 339:
                return true;
            case 340:
                return false;
            case 341:
                return false;
            case 342:
                return true;
            case 343:
                return true;
            case 344:
                return false;
            case 345:
                return false;
            case 346:
                return false;
            case 347:
                return false;
            case 348:
                return true;
            case 349:
                return true;
            case 350:
                return false;
            case 351:
                return false;
            case 352:
                return false;
            case 353:
                return false;
            case 354:
                return false;
            case 355:
                return false;
            case 356:
                return false;
            case 357:
                return false;
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
                return false;
            case 367:
                return false;
            case 368:
                return false;
            case 369:
                return false;
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
                return false;
            case 378:
                return false;
            case 379:
                return false;
            case 380:
                return false;
            case 381:
                return false;
            case 382:
                return false;
        }
    }
}
