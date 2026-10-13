package nu.xom;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.Writer;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.Primes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Latin8Writer.class */
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
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 /* 163 */:
                return false;
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256 /* 164 */:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384 /* 165 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256 /* 166 */:
                return true;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 /* 167 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256 /* 168 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384 /* 169 */:
                return false;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256 /* 170 */:
                return true;
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384 /* 171 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256 /* 172 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256 /* 174 */:
                return false;
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384 /* 175 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_NULL_SHA256 /* 176 */:
                return true;
            case CipherSuite.TLS_PSK_WITH_NULL_SHA384 /* 177 */:
                return true;
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
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA256 /* 184 */:
                return true;
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384 /* 185 */:
                return true;
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 186 */:
                return true;
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
                return true;
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
                return true;
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
                return true;
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
            case 252:
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
            case 262:
                return true;
            case 263:
                return true;
            case 264:
                return true;
            case 265:
                return true;
            case 266:
                return false;
            case 267:
                return false;
            case 268:
                return true;
            case 269:
                return true;
            case 270:
                return true;
            case 271:
                return true;
            case XmlConsts.XML_V_11 /* 272 */:
                return true;
            case 273:
                return true;
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
                return true;
            case 281:
                return true;
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
                return false;
            case 289:
                return false;
        }
    }
}
