package nu.xom;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.Writer;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.Primes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Latin7Writer.class */
class Latin7Writer extends TextWriter {
    Latin7Writer(Writer writer, String str) {
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
                return false;
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 /* 163 */:
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256 /* 164 */:
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384 /* 165 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256 /* 166 */:
                return false;
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
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256 /* 174 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384 /* 175 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_NULL_SHA256 /* 176 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_NULL_SHA384 /* 177 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA256 /* 178 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384 /* 179 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA256 /* 180 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384 /* 181 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256 /* 182 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384 /* 183 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA256 /* 184 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384 /* 185 */:
                return false;
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 186 */:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
                return false;
            case 188:
                return false;
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 189 */:
                return false;
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 190 */:
                return false;
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256 /* 191 */:
                return true;
            case 192:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 193 */:
                return true;
            case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 194 */:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 195 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 196 */:
                return false;
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256 /* 197 */:
                return false;
            case 198:
                return false;
            case 199:
                return true;
            case DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE /* 200 */:
                return true;
            case 201:
                return false;
            case 202:
                return true;
            case 203:
                return true;
            case 204:
                return true;
            case 205:
                return true;
            case 206:
                return true;
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
                return true;
            case 213:
                return false;
            case 214:
                return false;
            case 215:
                return false;
            case 216:
                return false;
            case 217:
                return true;
            case 218:
                return true;
            case 219:
                return true;
            case 220:
                return false;
            case 221:
                return true;
            case 222:
                return true;
            case 223:
                return false;
            case 224:
                return true;
            case 225:
                return true;
            case 226:
                return true;
            case 227:
                return true;
            case 228:
                return false;
            case 229:
                return false;
            case 230:
                return false;
            case 231:
                return true;
            case 232:
                return true;
            case 233:
                return false;
            case 234:
                return true;
            case 235:
                return true;
            case 236:
                return true;
            case 237:
                return true;
            case 238:
                return true;
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
                return true;
            case 245:
                return false;
            case 246:
                return false;
            case 247:
                return false;
            case 248:
                return false;
            case 249:
                return true;
            case 250:
                return true;
            case 251:
                return true;
            case 252:
                return false;
            case 253:
                return true;
            case 254:
                return true;
            case 255:
                return true;
            case 256:
                return false;
            case 257:
                return false;
            case 258:
                return true;
            case 259:
                return true;
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
                return true;
            case 273:
                return true;
            case 274:
                return false;
            case 275:
                return false;
            case 276:
                return true;
            case 277:
                return true;
            case 278:
                return false;
            case 279:
                return false;
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
                return false;
            case 291:
                return false;
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
                return false;
            case 299:
                return false;
            case 300:
                return true;
            case 301:
                return true;
            case 302:
                return false;
            case 303:
                return false;
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
                return false;
            case 311:
                return false;
            case 312:
                return true;
            case 313:
                return true;
            case 314:
                return true;
            case 315:
                return false;
            case 316:
                return false;
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
                return false;
            case 326:
                return false;
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
                return false;
            case 333:
                return false;
            case 334:
                return true;
            case 335:
                return true;
            case 336:
                return true;
            case 337:
                return true;
            case 338:
                return true;
            case 339:
                return true;
            case 340:
                return true;
            case 341:
                return true;
            case 342:
                return false;
            case 343:
                return false;
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
                return false;
            case 363:
                return false;
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
                return false;
            case 371:
                return false;
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
            default:
                switch (c) {
                    case 8217:
                        return false;
                    case 8218:
                        return true;
                    case 8219:
                        return true;
                    case 8220:
                        return false;
                    case 8221:
                        return false;
                    case 8222:
                        return false;
                    default:
                        return true;
                }
        }
    }
}
