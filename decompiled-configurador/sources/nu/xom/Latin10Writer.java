package nu.xom;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.Writer;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.Primes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Latin10Writer.class */
class Latin10Writer extends TextWriter {
    Latin10Writer(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 /* 167 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256 /* 168 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384 /* 169 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256 /* 170 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384 /* 171 */:
                return false;
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
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA256 /* 178 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384 /* 179 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA256 /* 180 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384 /* 181 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256 /* 182 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384 /* 183 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA256 /* 184 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384 /* 185 */:
                return true;
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 186 */:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
                return false;
            case 188:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 189 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 190 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256 /* 191 */:
                return true;
            case 192:
                return false;
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
                return false;
            case 199:
                return false;
            case DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE /* 200 */:
                return false;
            case 201:
                return false;
            case 202:
                return false;
            case 203:
                return false;
            case 204:
                return false;
            case 205:
                return false;
            case 206:
                return false;
            case 207:
                return false;
            case 208:
                return true;
            case 209:
                return true;
            case 210:
                return false;
            case Primes.SMALL_FACTOR_LIMIT /* 211 */:
                return false;
            case 212:
                return false;
            case 213:
                return true;
            case 214:
                return false;
            case 215:
                return true;
            case 216:
                return true;
            case 217:
                return false;
            case 218:
                return false;
            case 219:
                return false;
            case 220:
                return false;
            case 221:
                return true;
            case 222:
                return true;
            case 223:
                return false;
            case 224:
                return false;
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
                return false;
            case 231:
                return false;
            case 232:
                return false;
            case 233:
                return false;
            case 234:
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
                return true;
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
                return true;
            case 248:
                return true;
            case 249:
                return false;
            case 250:
                return false;
            case 251:
                return false;
            case 252:
                return false;
            case 253:
                return true;
            case 254:
                return true;
            case 255:
                return false;
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
                return true;
            case 271:
                return true;
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
                return true;
            case 283:
                return true;
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
                return true;
            case 314:
                return true;
            case 315:
                return true;
            case 316:
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
                return true;
            case 328:
                return true;
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
                return false;
            case 339:
                return false;
            case 340:
                return true;
            case 341:
                return true;
            case 342:
                return true;
            case 343:
                return true;
            case 344:
                return true;
            case 345:
                return true;
            case 346:
                return false;
            case 347:
                return false;
            case 348:
                return true;
            case 349:
                return true;
            case 350:
                return true;
            case 351:
                return true;
            case 352:
                return false;
            case 353:
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
                return false;
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
            default:
                switch (c) {
                    case 536:
                        return false;
                    case 537:
                        return false;
                    case 538:
                        return false;
                    case 539:
                        return false;
                    case 540:
                        return true;
                    case 8221:
                        return false;
                    case 8222:
                        return false;
                    default:
                        return c != 8364;
                }
        }
    }
}
