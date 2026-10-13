package org.apache.pdfbox.pdmodel.font;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/ToUnicodeWriter.class */
final class ToUnicodeWriter {
    private final Map<Integer, String> cidToUnicode = new TreeMap();
    private int wMode = 0;
    static final int MAX_ENTRIES_PER_OPERATOR = 100;

    ToUnicodeWriter() {
    }

    public void setWMode(int wMode) {
        this.wMode = wMode;
    }

    public void add(int cid, String text) {
        if (cid < 0 || cid > 65535) {
            throw new IllegalArgumentException("CID is not valid");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text is null or empty");
        }
        this.cidToUnicode.put(Integer.valueOf(cid), text);
    }

    public void writeTo(OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charsets.US_ASCII));
        writeLine(writer, "/CIDInit /ProcSet findresource begin");
        writeLine(writer, "12 dict begin\n");
        writeLine(writer, "begincmap");
        writeLine(writer, "/CIDSystemInfo");
        writeLine(writer, "<< /Registry (Adobe)");
        writeLine(writer, "/Ordering (UCS)");
        writeLine(writer, "/Supplement 0");
        writeLine(writer, ">> def\n");
        writeLine(writer, "/CMapName /Adobe-Identity-UCS def");
        writeLine(writer, "/CMapType 2 def\n");
        if (this.wMode != 0) {
            writeLine(writer, "/WMode /" + this.wMode + " def");
        }
        writeLine(writer, "1 begincodespacerange");
        writeLine(writer, "<0000> <FFFF>");
        writeLine(writer, "endcodespacerange\n");
        List<Integer> srcFrom = new ArrayList<>();
        List<Integer> srcTo = new ArrayList<>();
        List<String> dstString = new ArrayList<>();
        int srcPrev = -1;
        String dstPrev = null;
        int srcCode1 = -1;
        for (Map.Entry<Integer, String> entry : this.cidToUnicode.entrySet()) {
            int cid = entry.getKey().intValue();
            String text = entry.getValue();
            if (cid == srcPrev + 1 && dstPrev.codePointCount(0, dstPrev.length()) == 1 && text.codePointAt(0) == dstPrev.codePointAt(0) + 1 && dstPrev.codePointAt(0) + 1 <= 255 - (cid - srcCode1)) {
                srcTo.set(srcTo.size() - 1, Integer.valueOf(cid));
            } else {
                srcCode1 = cid;
                srcFrom.add(Integer.valueOf(cid));
                srcTo.add(Integer.valueOf(cid));
                dstString.add(text);
            }
            srcPrev = cid;
            dstPrev = text;
        }
        int batchCount = (int) Math.ceil(((double) srcFrom.size()) / 100.0d);
        int batch = 0;
        while (batch < batchCount) {
            int count = batch == batchCount - 1 ? srcFrom.size() - (100 * batch) : 100;
            writer.write(count + " beginbfrange\n");
            for (int j = 0; j < count; j++) {
                int index = (batch * 100) + j;
                writer.write(60);
                writer.write(Hex.getChars(srcFrom.get(index).shortValue()));
                writer.write("> ");
                writer.write(60);
                writer.write(Hex.getChars(srcTo.get(index).shortValue()));
                writer.write("> ");
                writer.write(60);
                writer.write(Hex.getCharsUTF16BE(dstString.get(index)));
                writer.write(">\n");
            }
            writeLine(writer, "endbfrange\n");
            batch++;
        }
        writeLine(writer, "endcmap");
        writeLine(writer, "CMapName currentdict /CMap defineresource pop");
        writeLine(writer, "end");
        writeLine(writer, "end");
        writer.flush();
    }

    private void writeLine(BufferedWriter writer, String text) throws IOException {
        writer.write(text);
        writer.write(10);
    }
}
