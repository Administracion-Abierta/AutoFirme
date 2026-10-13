package org.apache.commons.codec.language;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import java.util.Locale;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/Caverphone1.class */
public class Caverphone1 extends AbstractCaverphone {
    private static final String SIX_1 = "111111";

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String source) {
        if (source == null || source.isEmpty()) {
            return SIX_1;
        }
        String txt = source.toLowerCase(Locale.ENGLISH);
        return (txt.replaceAll("[^a-z]", "").replaceAll("^cough", "cou2f").replaceAll("^rough", "rou2f").replaceAll("^tough", "tou2f").replaceAll("^enough", "enou2f").replaceAll("^gn", "2n").replaceAll("mb$", "m2").replace("cq", "2q").replace("ci", "si").replace("ce", "se").replace("cy", "sy").replace("tch", "2ch").replace("c", "k").replace("q", "k").replace("x", "k").replace("v", "f").replace("dg", "2g").replace("tio", "sio").replace("tia", "sia").replace("d", "t").replace("ph", "fh").replace("b", "p").replace("sh", "s2").replace("z", "s").replaceAll("^[aeiou]", "A").replaceAll("[aeiou]", TsaParams.TS_SIGN_DOC).replace("3gh3", "3kh3").replace("gh", "22").replace("g", "k").replaceAll("s+", "S").replaceAll("t+", "T").replaceAll("p+", Constants._TAG_P).replaceAll("k+", "K").replaceAll("f+", "F").replaceAll("m+", "M").replaceAll("n+", "N").replace("w3", "W3").replace("wy", "Wy").replace("wh3", "Wh3").replace("why", "Why").replace("w", TsaParams.TS_DOC).replaceAll("^h", "A").replace("h", TsaParams.TS_DOC).replace("r3", "R3").replace("ry", "Ry").replace("r", TsaParams.TS_DOC).replace("l3", "L3").replace("ly", "Ly").replace("l", TsaParams.TS_DOC).replace("j", "y").replace("y3", "Y3").replace("y", TsaParams.TS_DOC).replace(TsaParams.TS_DOC, "").replace(TsaParams.TS_SIGN_DOC, "") + SIX_1).substring(0, SIX_1.length());
    }
}
