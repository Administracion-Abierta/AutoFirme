package org.apache.fontbox.util;

import com.ctc.wstx.io.CharsetNames;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/util/Charsets.class */
public final class Charsets {
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_16 = Charset.forName(CharsetNames.CS_UTF16);
    public static final Charset UTF_16BE = Charset.forName(CharsetNames.CS_UTF16BE);
    public static final Charset US_ASCII = Charset.forName(CharsetNames.CS_US_ASCII);
    public static final Charset ISO_10646 = Charset.forName("ISO-10646-UCS-2");

    private Charsets() {
    }
}
