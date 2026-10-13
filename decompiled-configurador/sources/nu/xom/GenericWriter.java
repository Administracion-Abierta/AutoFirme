package nu.xom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/GenericWriter.class */
class GenericWriter extends TextWriter {
    private final ByteArrayOutputStream bout;
    private final OutputStreamWriter wout;
    private final boolean isJapanese;

    GenericWriter(Writer writer, String str) throws UnsupportedEncodingException {
        super(writer, str);
        this.bout = new ByteArrayOutputStream(32);
        this.wout = new OutputStreamWriter(this.bout, str);
        String upperCase = str.toUpperCase(Locale.ENGLISH);
        if (upperCase.indexOf("EUC-JP") > -1 || upperCase.startsWith("EUC_JP") || upperCase.equals("SHIFT_JIS") || upperCase.equals("SJIS") || upperCase.equals("ISO-2022-JP")) {
            this.isJapanese = true;
        } else {
            this.isJapanese = false;
        }
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 127) {
            return false;
        }
        if (this.isJapanese && (c == 165 || c == 8254)) {
            return true;
        }
        boolean z = false;
        try {
            this.wout.write(c);
            this.wout.flush();
            byte[] byteArray = this.bout.toByteArray();
            if (byteArray.length == 0 || byteArray[0] == 63) {
                z = true;
            } else if (this.isJapanese && byteArray[0] == 33) {
                z = true;
            }
            return z;
        } catch (IOException e) {
            return true;
        } catch (Error e2) {
            return true;
        } finally {
            this.bout.reset();
        }
    }
}
