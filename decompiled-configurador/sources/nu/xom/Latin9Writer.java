package nu.xom;

import java.io.Writer;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.Primes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Latin9Writer.class */
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
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384 /* 165 */:
                return false;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256 /* 166 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 /* 167 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256 /* 168 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384 /* 169 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256 /* 170 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384 /* 171 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256 /* 172 */:
                return false;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256 /* 174 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384 /* 175 */:
                return false;
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
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
                return false;
            case 188:
                return true;
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 189 */:
                return true;
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 190 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256 /* 191 */:
                return false;
            case 192:
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 193 */:
                return false;
            case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 194 */:
                return false;
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 195 */:
                return false;
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 196 */:
                return false;
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256 /* 197 */:
                return false;
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
                return false;
            case 209:
                return false;
            case 210:
                return false;
            case Primes.SMALL_FACTOR_LIMIT /* 211 */:
                return false;
            case 212:
                return false;
            case 213:
                return false;
            case 214:
                return false;
            case 215:
                return false;
            case 216:
                return false;
            case 217:
                return false;
            case 218:
                return false;
            case 219:
                return false;
            case 220:
                return false;
            case 221:
                return false;
            case 222:
                return false;
            case 223:
                return false;
            case 224:
                return false;
            case 225:
                return false;
            case 226:
                return false;
            case 227:
                return false;
            case 228:
                return false;
            case 229:
                return false;
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
            case 252:
                return false;
            case 253:
                return false;
            case 254:
                return false;
            case 255:
                return false;
            default:
                switch (c) {
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
                        return true;
                    case 347:
                        return true;
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
