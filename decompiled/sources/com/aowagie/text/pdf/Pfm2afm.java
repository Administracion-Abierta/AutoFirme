package com.aowagie.text.pdf;

import com.aowagie.text.ElementTags;
import com.aowagie.text.html.Markup;
import com.sun.jna.platform.win32.WinError;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.apache.fontbox.afm.AFMParser;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/Pfm2afm.class */
final class Pfm2afm {
    private final RandomAccessFileOrArray in;
    private final PrintWriter out;
    private short vers;
    private int h_len;
    private String copyright;
    private short type;
    private short points;
    private short verres;
    private short horres;
    private short ascent;
    private short intleading;
    private short extleading;
    private byte italic;
    private byte uline;
    private byte overs;
    private short weight;
    private byte charset;
    private short pixwidth;
    private short pixheight;
    private byte kind;
    private short avgwidth;
    private short maxwidth;
    private int firstchar;
    private int lastchar;
    private byte defchar;
    private byte brkchar;
    private short widthby;
    private int device;
    private int face;
    private int bits;
    private int bitoff;
    private short extlen;
    private int psext;
    private int chartab;
    private int res1;
    private int kernpairs;
    private int res2;
    private int fontname;
    private short capheight;
    private short xheight;
    private short ascender;
    private short descender;
    private boolean isMono;
    private final int[] Win2PSStd = {0, 0, 0, 0, 197, 198, 199, 0, 202, 0, 205, WinError.ERROR_FILENAME_EXCED_RANGE, WinError.ERROR_RING2_STACK_IN_USE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 169, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 193, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 0, 0, 184, 166, 185, 188, 178, 179, 195, 189, 0, 172, WinError.ERROR_MORE_DATA, 0, 0, 0, 0, 96, 0, 170, 186, 183, 177, WinError.ERROR_META_EXPANSION_TOO_LONG, 196, 0, 0, 173, 250, 0, 0, 0, 0, 161, 162, 163, 168, 165, 0, 167, 200, 0, 227, 171, 0, 0, 0, 197, 0, 0, 0, 0, 194, 0, 182, 180, 203, 0, 235, 187, 0, 0, 0, 191, 0, 0, 0, 0, 0, 0, WinError.ERROR_VIRUS_INFECTED, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, WinError.ERROR_PIPE_NOT_CONNECTED, 0, 0, 0, 0, 0, 0, 251, 0, 0, 0, 0, 0, 0, 241, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 249, 0, 0, 0, 0, 0, 0, 0};
    private final int[] WinClass = {0, 0, 0, 0, 2, 2, 2, 0, 2, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private final String[] WinChars = {"W00", "W01", "W02", "W03", "macron", "breve", "dotaccent", "W07", "ring", "W09", "W0a", "W0b", "W0c", "W0d", "W0e", "W0f", "hungarumlaut", "ogonek", "caron", "W13", "W14", "W15", "W16", "W17", "W18", "W19", "W1a", "W1b", "W1c", "W1d", "W1e", "W1f", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quotesingle", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", StandardStructureTypes.H, "I", "J", OperatorName.STROKING_COLOR_CMYK, "L", OperatorName.SET_LINE_MITERLIMIT, "N", PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE, "P", "Q", "R", "S", PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE, PDBorderStyleDictionary.STYLE_UNDERLINE, "V", "W", "X", Constants._TAG_Y, "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "grave", PDPageLabelRange.STYLE_LETTERS_LOWER, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, "c", OperatorName.SET_LINE_DASHPATTERN, "e", OperatorName.FILL_NON_ZERO, OperatorName.NON_STROKING_GRAY, OperatorName.CLOSE_PATH, OperatorName.SET_FLATNESS, OperatorName.SET_LINE_JOINSTYLE, OperatorName.NON_STROKING_CMYK, OperatorName.LINE_TO, OperatorName.MOVE_TO, OperatorName.ENDPATH, "o", "p", OperatorName.SAVE, PDPageLabelRange.STYLE_ROMAN_LOWER, OperatorName.CLOSE_AND_STROKE, "t", "u", OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT, OperatorName.SET_LINE_WIDTH, "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "z", "braceleft", "bar", "braceright", "asciitilde", "W7f", "euro", "W81", "quotesinglbase", "florin", "quotedblbase", "ellipsis", "dagger", "daggerdbl", "circumflex", "perthousand", "Scaron", "guilsinglleft", "OE", "W8d", "Zcaron", "W8f", "W90", "quoteleft", "quoteright", "quotedblleft", "quotedblright", "bullet", "endash", "emdash", "tilde", "trademark", "scaron", "guilsinglright", "oe", "W9d", "zcaron", "Ydieresis", "reqspace", "exclamdown", "cent", "sterling", "currency", "yen", "brokenbar", ElementTags.SECTION, "dieresis", "copyright", "ordfeminine", "guillemotleft", "logicalnot", "syllable", "registered", "macron", "degree", "plusminus", "twosuperior", "threesuperior", "acute", "mu", ElementTags.PARAGRAPH, "periodcentered", "cedilla", "onesuperior", "ordmasculine", "guillemotright", "onequarter", "onehalf", "threequarters", "questiondown", "Agrave", "Aacute", "Acircumflex", "Atilde", "Adieresis", "Aring", "AE", "Ccedilla", "Egrave", "Eacute", "Ecircumflex", "Edieresis", "Igrave", "Iacute", "Icircumflex", "Idieresis", "Eth", "Ntilde", "Ograve", "Oacute", "Ocircumflex", "Otilde", "Odieresis", "multiply", "Oslash", "Ugrave", "Uacute", "Ucircumflex", "Udieresis", "Yacute", "Thorn", "germandbls", "agrave", "aacute", "acircumflex", "atilde", "adieresis", "aring", "ae", "ccedilla", "egrave", "eacute", "ecircumflex", "edieresis", "igrave", "iacute", "icircumflex", "idieresis", "eth", "ntilde", "ograve", "oacute", "ocircumflex", "otilde", "odieresis", "divide", "oslash", "ugrave", "uacute", "ucircumflex", "udieresis", "yacute", "thorn", "ydieresis"};

    private Pfm2afm(RandomAccessFileOrArray in, OutputStream out) throws IOException {
        this.in = in;
        this.out = new PrintWriter(new OutputStreamWriter(out, "ISO-8859-1"));
    }

    public static void convert(RandomAccessFileOrArray in, OutputStream out) throws IOException {
        Pfm2afm p = new Pfm2afm(in, out);
        p.openpfm();
        p.putheader();
        p.putchartab();
        p.putkerntab();
        p.puttrailer();
        p.out.flush();
    }

    public static void main(String[] args) {
        try {
            RandomAccessFileOrArray in = new RandomAccessFileOrArray(args[0]);
            OutputStream out = new FileOutputStream(args[1]);
            convert(in, out);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readString(int n) throws IOException {
        byte[] b = new byte[n];
        this.in.readFully(b);
        int k = 0;
        while (k < b.length && b[k] != 0) {
            k++;
        }
        return new String(b, 0, k, "ISO-8859-1");
    }

    private String readString() throws IOException {
        StringBuffer buf = new StringBuffer();
        while (true) {
            int c = this.in.read();
            if (c > 0) {
                buf.append((char) c);
            } else {
                return buf.toString();
            }
        }
    }

    private void outval(int n) {
        this.out.print(' ');
        this.out.print(n);
    }

    private void outchar(int code, int width, String name) {
        this.out.print("C ");
        outval(code);
        this.out.print(" ; WX ");
        outval(width);
        if (name != null) {
            this.out.print(" ; N ");
            this.out.print(name);
        }
        this.out.print(" ;\n");
    }

    private void openpfm() throws IOException {
        this.in.seek(0);
        this.vers = this.in.readShortLE();
        this.h_len = this.in.readIntLE();
        this.copyright = readString(60);
        this.type = this.in.readShortLE();
        this.points = this.in.readShortLE();
        this.verres = this.in.readShortLE();
        this.horres = this.in.readShortLE();
        this.ascent = this.in.readShortLE();
        this.intleading = this.in.readShortLE();
        this.extleading = this.in.readShortLE();
        this.italic = (byte) this.in.read();
        this.uline = (byte) this.in.read();
        this.overs = (byte) this.in.read();
        this.weight = this.in.readShortLE();
        this.charset = (byte) this.in.read();
        this.pixwidth = this.in.readShortLE();
        this.pixheight = this.in.readShortLE();
        this.kind = (byte) this.in.read();
        this.avgwidth = this.in.readShortLE();
        this.maxwidth = this.in.readShortLE();
        this.firstchar = this.in.read();
        this.lastchar = this.in.read();
        this.defchar = (byte) this.in.read();
        this.brkchar = (byte) this.in.read();
        this.widthby = this.in.readShortLE();
        this.device = this.in.readIntLE();
        this.face = this.in.readIntLE();
        this.bits = this.in.readIntLE();
        this.bitoff = this.in.readIntLE();
        this.extlen = this.in.readShortLE();
        this.psext = this.in.readIntLE();
        this.chartab = this.in.readIntLE();
        this.res1 = this.in.readIntLE();
        this.kernpairs = this.in.readIntLE();
        this.res2 = this.in.readIntLE();
        this.fontname = this.in.readIntLE();
        if (this.h_len != this.in.length() || this.extlen != 30 || this.fontname < 75 || this.fontname > 512) {
            throw new IOException("Not a valid PFM file.");
        }
        this.in.seek(this.psext + 14);
        this.capheight = this.in.readShortLE();
        this.xheight = this.in.readShortLE();
        this.ascender = this.in.readShortLE();
        this.descender = this.in.readShortLE();
    }

    private void putheader() throws IOException {
        this.out.print("StartFontMetrics 2.0\n");
        if (this.copyright.length() > 0) {
            this.out.print("Comment " + this.copyright + '\n');
        }
        this.out.print("FontName ");
        this.in.seek(this.fontname);
        String fname = readString();
        this.out.print(fname);
        this.out.print("\nEncodingScheme ");
        if (this.charset != 0) {
            this.out.print("FontSpecific\n");
        } else {
            this.out.print("AdobeStandardEncoding\n");
        }
        this.out.print("FullName " + fname.replace('-', ' '));
        if (this.face != 0) {
            this.in.seek(this.face);
            this.out.print("\nFamilyName " + readString());
        }
        this.out.print("\nWeight ");
        if (this.weight > 475 || fname.toLowerCase().indexOf(Markup.CSS_VALUE_BOLD) >= 0) {
            this.out.print("Bold");
        } else if ((this.weight < 325 && this.weight != 0) || fname.toLowerCase().indexOf("light") >= 0) {
            this.out.print("Light");
        } else if (fname.toLowerCase().indexOf("black") >= 0) {
            this.out.print("Black");
        } else {
            this.out.print("Medium");
        }
        this.out.print("\nItalicAngle ");
        if (this.italic != 0 || fname.toLowerCase().indexOf(Markup.CSS_VALUE_ITALIC) >= 0) {
            this.out.print("-12.00");
        } else {
            this.out.print("0");
        }
        this.out.print("\nIsFixedPitch ");
        if ((this.kind & 1) == 0 || this.avgwidth == this.maxwidth) {
            this.out.print("true");
            this.isMono = true;
        } else {
            this.out.print("false");
            this.isMono = false;
        }
        this.out.print("\nFontBBox");
        if (this.isMono) {
            outval(-20);
        } else {
            outval(-100);
        }
        outval(-(this.descender + 5));
        outval(this.maxwidth + 10);
        outval(this.ascent + 5);
        this.out.print("\nCapHeight");
        outval(this.capheight);
        this.out.print("\nXHeight");
        outval(this.xheight);
        this.out.print("\nDescender");
        outval(this.descender);
        this.out.print("\nAscender");
        outval(this.ascender);
        this.out.print('\n');
    }

    private void putchartab() throws IOException {
        int count = (this.lastchar - this.firstchar) + 1;
        int[] ctabs = new int[count];
        this.in.seek(this.chartab);
        for (int k = 0; k < count; k++) {
            ctabs[k] = this.in.readUnsignedShortLE();
        }
        int[] back = new int[256];
        if (this.charset == 0) {
            for (int i = this.firstchar; i <= this.lastchar; i++) {
                if (this.Win2PSStd[i] != 0) {
                    back[this.Win2PSStd[i]] = i;
                }
            }
        }
        this.out.print(AFMParser.START_CHAR_METRICS);
        outval(count);
        this.out.print('\n');
        if (this.charset != 0) {
            for (int i2 = this.firstchar; i2 <= this.lastchar; i2++) {
                if (ctabs[i2 - this.firstchar] != 0) {
                    outchar(i2, ctabs[i2 - this.firstchar], null);
                }
            }
        } else {
            for (int i3 = 0; i3 < 256; i3++) {
                int j = back[i3];
                if (j != 0) {
                    outchar(i3, ctabs[j - this.firstchar], this.WinChars[j]);
                    ctabs[j - this.firstchar] = 0;
                }
            }
            for (int i4 = this.firstchar; i4 <= this.lastchar; i4++) {
                if (ctabs[i4 - this.firstchar] != 0) {
                    outchar(-1, ctabs[i4 - this.firstchar], this.WinChars[i4]);
                }
            }
        }
        this.out.print("EndCharMetrics\n");
    }

    private void putkerntab() throws IOException {
        if (this.kernpairs == 0) {
            return;
        }
        this.in.seek(this.kernpairs);
        int count = this.in.readUnsignedShortLE();
        int nzero = 0;
        int[] kerns = new int[count * 3];
        int k = 0;
        while (k < kerns.length) {
            int i = k;
            int k2 = k + 1;
            kerns[i] = this.in.read();
            int k3 = k2 + 1;
            kerns[k2] = this.in.read();
            k = k3 + 1;
            short shortLE = this.in.readShortLE();
            kerns[k3] = shortLE;
            if (shortLE != 0) {
                nzero++;
            }
        }
        if (nzero == 0) {
            return;
        }
        this.out.print("StartKernData\nStartKernPairs");
        outval(nzero);
        this.out.print('\n');
        for (int k4 = 0; k4 < kerns.length; k4 += 3) {
            if (kerns[k4 + 2] != 0) {
                this.out.print("KPX ");
                this.out.print(this.WinChars[kerns[k4]]);
                this.out.print(' ');
                this.out.print(this.WinChars[kerns[k4 + 1]]);
                outval(kerns[k4 + 2]);
                this.out.print('\n');
            }
        }
        this.out.print("EndKernPairs\nEndKernData\n");
    }

    private void puttrailer() {
        this.out.print("EndFontMetrics\n");
    }
}
