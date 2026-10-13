package org.apache.fontbox.ttf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TrueTypeCollection.class */
public class TrueTypeCollection implements Closeable {
    private final TTFDataStream stream;
    private final int numFonts;
    private final long[] fontOffsets;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TrueTypeCollection$TrueTypeFontProcessor.class */
    public interface TrueTypeFontProcessor {
        void process(TrueTypeFont trueTypeFont) throws IOException;
    }

    public TrueTypeCollection(File file) throws IOException {
        this(new RAFDataStream(file, PDPageLabelRange.STYLE_ROMAN_LOWER));
    }

    public TrueTypeCollection(InputStream stream) throws IOException {
        this(new MemoryTTFDataStream(stream));
    }

    TrueTypeCollection(TTFDataStream stream) throws IOException {
        this.stream = stream;
        String tag = stream.readTag();
        if (!tag.equals("ttcf")) {
            throw new IOException("Missing TTC header");
        }
        float version = stream.read32Fixed();
        this.numFonts = (int) stream.readUnsignedInt();
        if (this.numFonts <= 0 || this.numFonts > 1024) {
            throw new IOException("Invalid number of fonts " + this.numFonts);
        }
        this.fontOffsets = new long[this.numFonts];
        for (int i = 0; i < this.numFonts; i++) {
            this.fontOffsets[i] = stream.readUnsignedInt();
        }
        if (version >= 2.0f) {
            stream.readUnsignedShort();
            stream.readUnsignedShort();
            stream.readUnsignedShort();
        }
    }

    public void processAllFonts(TrueTypeFontProcessor trueTypeFontProcessor) throws IOException {
        for (int i = 0; i < this.numFonts; i++) {
            TrueTypeFont font = getFontAtIndex(i);
            trueTypeFontProcessor.process(font);
        }
    }

    private TrueTypeFont getFontAtIndex(int idx) throws IOException {
        TTFParser parser;
        this.stream.seek(this.fontOffsets[idx]);
        if (this.stream.readTag().equals("OTTO")) {
            parser = new OTFParser(false, true);
        } else {
            parser = new TTFParser(false, true);
        }
        this.stream.seek(this.fontOffsets[idx]);
        return parser.parse(new TTCDataStream(this.stream));
    }

    public TrueTypeFont getFontByName(String name) throws IOException {
        for (int i = 0; i < this.numFonts; i++) {
            TrueTypeFont font = getFontAtIndex(i);
            if (font.getName().equals(name)) {
                return font;
            }
        }
        return null;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.stream.close();
    }
}
