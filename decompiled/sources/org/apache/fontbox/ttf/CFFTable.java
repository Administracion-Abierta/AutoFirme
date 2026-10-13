package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/CFFTable.class */
public class CFFTable extends TTFTable {
    public static final String TAG = "CFF ";
    private CFFFont cffFont;

    CFFTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        byte[] bytes = data.read((int) getLength());
        CFFParser parser = new CFFParser();
        this.cffFont = parser.parse(bytes, new CFFBytesource(this.font)).get(0);
        this.initialized = true;
    }

    public CFFFont getFont() {
        return this.cffFont;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/CFFTable$CFFBytesource.class */
    private static class CFFBytesource implements CFFParser.ByteSource {
        private final TrueTypeFont ttf;

        CFFBytesource(TrueTypeFont ttf) {
            this.ttf = ttf;
        }

        @Override // org.apache.fontbox.cff.CFFParser.ByteSource
        public byte[] getBytes() throws IOException {
            return this.ttf.getTableBytes(this.ttf.getTableMap().get(CFFTable.TAG));
        }
    }
}
