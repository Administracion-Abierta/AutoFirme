package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.Font;
import com.aowagie.text.Phrase;
import com.aowagie.text.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/FontSelector.class */
class FontSelector {
    private final ArrayList fonts = new ArrayList();

    FontSelector() {
    }

    public void addFont(Font font) {
        if (font.getBaseFont() != null) {
            this.fonts.add(font);
            return;
        }
        BaseFont bf = font.getCalculatedBaseFont(true);
        Font f2 = new Font(bf, font.getSize(), font.getCalculatedStyle(), font.getColor());
        this.fonts.add(f2);
    }

    public Phrase process(String text) {
        int fsize = this.fonts.size();
        if (fsize == 0) {
            throw new IndexOutOfBoundsException("No font is defined.");
        }
        char[] cc = text.toCharArray();
        int len = cc.length;
        StringBuffer sb = new StringBuffer();
        int lastidx = -1;
        Phrase ret = new Phrase();
        int k = 0;
        while (k < len) {
            char c = cc[k];
            if (c == '\n' || c == '\r') {
                sb.append(c);
            } else if (Utilities.isSurrogatePair(cc, k)) {
                int u = Utilities.convertToUtf32(cc, k);
                for (int f = 0; f < fsize; f++) {
                    Font font = (Font) this.fonts.get(f);
                    if (font.getBaseFont().charExists(u)) {
                        if (lastidx != f) {
                            if (sb.length() > 0 && lastidx != -1) {
                                Chunk ck = new Chunk(sb.toString(), (Font) this.fonts.get(lastidx));
                                ret.add(ck);
                                sb.setLength(0);
                            }
                            lastidx = f;
                        }
                        sb.append(c);
                        k++;
                        sb.append(cc[k]);
                        break;
                    }
                }
            } else {
                for (int f2 = 0; f2 < fsize; f2++) {
                    Font font2 = (Font) this.fonts.get(f2);
                    if (font2.getBaseFont().charExists(c)) {
                        if (lastidx != f2) {
                            if (sb.length() > 0 && lastidx != -1) {
                                Chunk ck2 = new Chunk(sb.toString(), (Font) this.fonts.get(lastidx));
                                ret.add(ck2);
                                sb.setLength(0);
                            }
                            lastidx = f2;
                        }
                        sb.append(c);
                        break;
                    }
                }
            }
            k++;
        }
        if (sb.length() > 0) {
            Chunk ck3 = new Chunk(sb.toString(), (Font) this.fonts.get(lastidx == -1 ? 0 : lastidx));
            ret.add(ck3);
        }
        return ret;
    }
}
