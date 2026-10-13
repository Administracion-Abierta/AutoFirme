package org.apache.pdfbox.util;

import com.ctc.wstx.io.CharsetNames;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/Charsets.class */
public final class Charsets {
    public static final Charset US_ASCII = Charset.forName(CharsetNames.CS_US_ASCII);
    public static final Charset UTF_16BE = Charset.forName(CharsetNames.CS_UTF16BE);
    public static final Charset UTF_16LE = Charset.forName(CharsetNames.CS_UTF16LE);
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset WINDOWS_1252 = Charset.forName("Windows-1252");
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private Charsets() {
    }
}
