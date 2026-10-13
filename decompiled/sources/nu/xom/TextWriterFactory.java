package nu.xom;

import com.ctc.wstx.io.CharsetNames;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/TextWriterFactory.class */
class TextWriterFactory {
    TextWriterFactory() {
    }

    public static TextWriter getTextWriter(Writer writer, String str) {
        String upperCase = str.toUpperCase(Locale.ENGLISH);
        if (upperCase.startsWith("UTF") || upperCase.startsWith("UNICODE")) {
            return new UnicodeWriter(writer, str);
        }
        if (upperCase.startsWith("ISO-10646-UCS") || upperCase.startsWith("UCS") || upperCase.equals("GB18030")) {
            return new UCSWriter(writer, str);
        }
        if (upperCase.equals("ISO-8859-1")) {
            return new Latin1Writer(writer, str);
        }
        if (upperCase.equals("ISO-8859-2")) {
            return new Latin2Writer(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-3")) {
            return new Latin3Writer(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-4")) {
            return new Latin4Writer(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-5")) {
            return new ISOCyrillicWriter(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-6")) {
            return new ISOArabicWriter(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-7")) {
            return new ISOGreekWriter(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-8")) {
            return new ISOHebrewWriter(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-9") || upperCase.equals("EBCDIC-CP-TR") || upperCase.equals("CP1037")) {
            return new Latin5Writer(writer, upperCase);
        }
        if (str.equals("ISO-8859-10")) {
            return new Latin6Writer(writer, str);
        }
        if (upperCase.equals("ISO-8859-11") || upperCase.equals("TIS-620") || upperCase.equals("TIS620")) {
            return new ISOThaiWriter(writer, upperCase);
        }
        if (upperCase.equals("ISO-8859-13")) {
            return new Latin7Writer(writer, upperCase);
        }
        if (str.equals("ISO-8859-14")) {
            return new Latin8Writer(writer, str);
        }
        if (upperCase.equals("ISO-8859-15")) {
            return new Latin9Writer(writer, upperCase);
        }
        if (str.equals("ISO-8859-16")) {
            return new Latin10Writer(writer, str);
        }
        if (upperCase.endsWith("ASCII")) {
            return new ASCIIWriter(writer, upperCase);
        }
        if (upperCase.equals(CharsetNames.CS_EBCDIC_SUBSET) || upperCase.equals("CP037") || upperCase.equals("EBCDIC-CP-US") || upperCase.equals("EBCDIC-CP-CA") || upperCase.equals("EBCDIC-CP-WA") || upperCase.equals("EBCDIC-CP-NL") || upperCase.equals("CSIBM037")) {
            return new Latin1Writer(writer, upperCase);
        }
        try {
            return new GenericWriter(writer, str);
        } catch (UnsupportedEncodingException e) {
            return new ASCIIWriter(writer, str);
        }
    }
}
