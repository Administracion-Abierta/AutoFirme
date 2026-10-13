package org.spongycastle.i18n.filter;

import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/i18n/filter/HTMLFilter.class */
public class HTMLFilter implements Filter {
    @Override // org.spongycastle.i18n.filter.Filter
    public String doFilter(String input) {
        StringBuffer buf = new StringBuffer(input);
        int i = 0;
        while (i < buf.length()) {
            char ch = buf.charAt(i);
            switch (ch) {
                case '\"':
                    buf.replace(i, i + 1, "&#34");
                    break;
                case '#':
                    buf.replace(i, i + 1, "&#35");
                    break;
                case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                case '*':
                case ',':
                case '.':
                case '/':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ':':
                case '=':
                default:
                    i -= 3;
                    break;
                case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                    buf.replace(i, i + 1, "&#37");
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    buf.replace(i, i + 1, "&#38");
                    break;
                case EACTags.INTERCHANGE_CONTROL /* 39 */:
                    buf.replace(i, i + 1, "&#39");
                    break;
                case '(':
                    buf.replace(i, i + 1, "&#40");
                    break;
                case ')':
                    buf.replace(i, i + 1, "&#41");
                    break;
                case '+':
                    buf.replace(i, i + 1, "&#43");
                    break;
                case '-':
                    buf.replace(i, i + 1, "&#45");
                    break;
                case ';':
                    buf.replace(i, i + 1, "&#59");
                    break;
                case '<':
                    buf.replace(i, i + 1, "&#60");
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    buf.replace(i, i + 1, "&#62");
                    break;
            }
            i += 4;
        }
        return buf.toString();
    }

    @Override // org.spongycastle.i18n.filter.Filter
    public String doFilterUrl(String input) {
        return doFilter(input);
    }
}
