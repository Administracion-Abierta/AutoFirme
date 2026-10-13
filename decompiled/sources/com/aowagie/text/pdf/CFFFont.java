package com.aowagie.text.pdf;

import com.aowagie.text.ElementTags;
import com.aowagie.text.ExceptionConverter;
import com.ctc.wstx.cfg.XmlConsts;
import org.apache.fontbox.afm.AFMParser;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont.class */
class CFFFont {
    private static final String[] operatorNames = {XmlConsts.XML_DECL_KW_VERSION, AFMParser.NOTICE, AFMParser.FULL_NAME, AFMParser.FAMILY_NAME, AFMParser.WEIGHT, AFMParser.FONT_BBOX, "BlueValues", "OtherBlues", "FamilyBlues", "FamilyOtherBlues", AFMParser.STD_HW, AFMParser.STD_VW, "UNKNOWN_12", "UniqueID", "XUID", "charset", "Encoding", "CharStrings", StandardStructureTypes.PRIVATE, "Subrs", "defaultWidthX", "nominalWidthX", "UNKNOWN_22", "UNKNOWN_23", "UNKNOWN_24", "UNKNOWN_25", "UNKNOWN_26", "UNKNOWN_27", "UNKNOWN_28", "UNKNOWN_29", "UNKNOWN_30", "UNKNOWN_31", "Copyright", "isFixedPitch", AFMParser.ITALIC_ANGLE, AFMParser.UNDERLINE_POSITION, AFMParser.UNDERLINE_THICKNESS, "PaintType", "CharstringType", "FontMatrix", "StrokeWidth", "BlueScale", "BlueShift", "BlueFuzz", "StemSnapH", "StemSnapV", "ForceBold", "UNKNOWN_12_15", "UNKNOWN_12_16", "LanguageGroup", "ExpansionFactor", "initialRandomSeed", "SyntheticBase", "PostScript", "BaseFontName", "BaseFontBlend", "UNKNOWN_12_24", "UNKNOWN_12_25", "UNKNOWN_12_26", "UNKNOWN_12_27", "UNKNOWN_12_28", "UNKNOWN_12_29", "ROS", "CIDFontVersion", "CIDFontRevision", "CIDFontType", "CIDCount", "UIDBase", "FDArray", "FDSelect", AFMParser.FONT_NAME};
    static final String[] standardStrings = {BaseFont.notdef, "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quoteright", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", StandardStructureTypes.H, "I", "J", OperatorName.STROKING_COLOR_CMYK, "L", OperatorName.SET_LINE_MITERLIMIT, "N", PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE, "P", "Q", "R", "S", PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE, PDBorderStyleDictionary.STYLE_UNDERLINE, "V", "W", "X", Constants._TAG_Y, "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "quoteleft", PDPageLabelRange.STYLE_LETTERS_LOWER, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, "c", OperatorName.SET_LINE_DASHPATTERN, "e", OperatorName.FILL_NON_ZERO, OperatorName.NON_STROKING_GRAY, OperatorName.CLOSE_PATH, OperatorName.SET_FLATNESS, OperatorName.SET_LINE_JOINSTYLE, OperatorName.NON_STROKING_CMYK, OperatorName.LINE_TO, OperatorName.MOVE_TO, OperatorName.ENDPATH, "o", "p", OperatorName.SAVE, PDPageLabelRange.STYLE_ROMAN_LOWER, OperatorName.CLOSE_AND_STROKE, "t", "u", OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT, OperatorName.SET_LINE_WIDTH, "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "z", "braceleft", "bar", "braceright", "asciitilde", "exclamdown", "cent", "sterling", "fraction", "yen", "florin", ElementTags.SECTION, "currency", "quotesingle", "quotedblleft", "guillemotleft", "guilsinglleft", "guilsinglright", "fi", "fl", "endash", "dagger", "daggerdbl", "periodcentered", ElementTags.PARAGRAPH, "bullet", "quotesinglbase", "quotedblbase", "quotedblright", "guillemotright", "ellipsis", "perthousand", "questiondown", "grave", "acute", "circumflex", "tilde", "macron", "breve", "dotaccent", "dieresis", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "emdash", "AE", "ordfeminine", "Lslash", "Oslash", "OE", "ordmasculine", "ae", "dotlessi", "lslash", "oslash", "oe", "germandbls", "onesuperior", "logicalnot", "mu", "trademark", "Eth", "onehalf", "plusminus", "Thorn", "onequarter", "divide", "brokenbar", "degree", "thorn", "threequarters", "twosuperior", "registered", "minus", "eth", "multiply", "threesuperior", "copyright", "Aacute", "Acircumflex", "Adieresis", "Agrave", "Aring", "Atilde", "Ccedilla", "Eacute", "Ecircumflex", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Ntilde", "Oacute", "Ocircumflex", "Odieresis", "Ograve", "Otilde", "Scaron", "Uacute", "Ucircumflex", "Udieresis", "Ugrave", "Yacute", "Ydieresis", "Zcaron", "aacute", "acircumflex", "adieresis", "agrave", "aring", "atilde", "ccedilla", "eacute", "ecircumflex", "edieresis", "egrave", "iacute", "icircumflex", "idieresis", "igrave", "ntilde", "oacute", "ocircumflex", "odieresis", "ograve", "otilde", "scaron", "uacute", "ucircumflex", "udieresis", "ugrave", "yacute", "ydieresis", "zcaron", "exclamsmall", "Hungarumlautsmall", "dollaroldstyle", "dollarsuperior", "ampersandsmall", "Acutesmall", "parenleftsuperior", "parenrightsuperior", "twodotenleader", "onedotenleader", "zerooldstyle", "oneoldstyle", "twooldstyle", "threeoldstyle", "fouroldstyle", "fiveoldstyle", "sixoldstyle", "sevenoldstyle", "eightoldstyle", "nineoldstyle", "commasuperior", "threequartersemdash", "periodsuperior", "questionsmall", "asuperior", "bsuperior", "centsuperior", "dsuperior", "esuperior", "isuperior", "lsuperior", "msuperior", "nsuperior", "osuperior", "rsuperior", "ssuperior", "tsuperior", "ff", "ffi", "ffl", "parenleftinferior", "parenrightinferior", "Circumflexsmall", "hyphensuperior", "Gravesmall", "Asmall", "Bsmall", "Csmall", "Dsmall", "Esmall", "Fsmall", "Gsmall", "Hsmall", "Ismall", "Jsmall", "Ksmall", "Lsmall", "Msmall", "Nsmall", "Osmall", "Psmall", "Qsmall", "Rsmall", "Ssmall", "Tsmall", "Usmall", "Vsmall", "Wsmall", "Xsmall", "Ysmall", "Zsmall", "colonmonetary", "onefitted", "rupiah", "Tildesmall", "exclamdownsmall", "centoldstyle", "Lslashsmall", "Scaronsmall", "Zcaronsmall", "Dieresissmall", "Brevesmall", "Caronsmall", "Dotaccentsmall", "Macronsmall", "figuredash", "hypheninferior", "Ogoneksmall", "Ringsmall", "Cedillasmall", "questiondownsmall", "oneeighth", "threeeighths", "fiveeighths", "seveneighths", "onethird", "twothirds", "zerosuperior", "foursuperior", "fivesuperior", "sixsuperior", "sevensuperior", "eightsuperior", "ninesuperior", "zeroinferior", "oneinferior", "twoinferior", "threeinferior", "fourinferior", "fiveinferior", "sixinferior", "seveninferior", "eightinferior", "nineinferior", "centinferior", "dollarinferior", "periodinferior", "commainferior", "Agravesmall", "Aacutesmall", "Acircumflexsmall", "Atildesmall", "Adieresissmall", "Aringsmall", "AEsmall", "Ccedillasmall", "Egravesmall", "Eacutesmall", "Ecircumflexsmall", "Edieresissmall", "Igravesmall", "Iacutesmall", "Icircumflexsmall", "Idieresissmall", "Ethsmall", "Ntildesmall", "Ogravesmall", "Oacutesmall", "Ocircumflexsmall", "Otildesmall", "Odieresissmall", "OEsmall", "Oslashsmall", "Ugravesmall", "Uacutesmall", "Ucircumflexsmall", "Udieresissmall", "Yacutesmall", "Thornsmall", "Ydieresissmall", "001.000", "001.001", "001.002", "001.003", "Black", "Bold", "Book", "Light", "Medium", "Regular", "Roman", "Semibold"};
    protected String key;
    protected Object[] args = new Object[48];
    protected int arg_count = 0;
    protected RandomAccessFileOrArray buf;
    private final int offSize;
    private final int nameIndexOffset;
    private final int topdictIndexOffset;
    protected int stringIndexOffset;
    protected int gsubrIndexOffset;
    private final int[] nameOffsets;
    protected int[] topdictOffsets;
    protected int[] stringOffsets;
    protected int[] gsubrOffsets;
    protected Font[] fonts;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$IndexBaseItem.class */
    protected static final class IndexBaseItem extends Item {
    }

    private String getString(char sid) {
        if (sid < standardStrings.length) {
            return standardStrings[sid];
        }
        if (sid >= (standardStrings.length + this.stringOffsets.length) - 1) {
            return null;
        }
        int j = sid - standardStrings.length;
        int p = getPosition();
        seek(this.stringOffsets[j]);
        StringBuffer s = new StringBuffer();
        for (int k = this.stringOffsets[j]; k < this.stringOffsets[j + 1]; k++) {
            s.append(getCard8());
        }
        seek(p);
        return s.toString();
    }

    char getCard8() {
        try {
            byte i = this.buf.readByte();
            return (char) (i & 255);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    char getCard16() {
        try {
            return this.buf.readChar();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private int getOffset(int offSize) {
        int offset = 0;
        for (int i = 0; i < offSize; i++) {
            offset = (offset * 256) + getCard8();
        }
        return offset;
    }

    void seek(int offset) {
        try {
            this.buf.seek(offset);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private short getShort() {
        try {
            return this.buf.readShort();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private int getInt() {
        try {
            return this.buf.readInt();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    int getPosition() {
        try {
            return this.buf.getFilePointer();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    int[] getIndex(int nextIndexOffset) {
        seek(nextIndexOffset);
        int count = getCard16();
        int[] offsets = new int[count + 1];
        if (count == 0) {
            offsets[0] = -1;
            int i = nextIndexOffset + 2;
            return offsets;
        }
        int indexOffSize = getCard8();
        for (int j = 0; j <= count; j++) {
            offsets[j] = ((((nextIndexOffset + 2) + 1) + ((count + 1) * indexOffSize)) - 1) + getOffset(indexOffSize);
        }
        return offsets;
    }

    protected void getDictItem() {
        for (int i = 0; i < this.arg_count; i++) {
            this.args[i] = null;
        }
        this.arg_count = 0;
        this.key = null;
        boolean gotKey = false;
        while (!gotKey) {
            char b0 = getCard8();
            if (b0 == 29) {
                int item = getInt();
                this.args[this.arg_count] = Integer.valueOf(item);
                this.arg_count++;
            } else if (b0 == 28) {
                short item2 = getShort();
                this.args[this.arg_count] = Integer.valueOf(item2);
                this.arg_count++;
            } else if (b0 >= ' ' && b0 <= 246) {
                byte item3 = (byte) (b0 - 139);
                this.args[this.arg_count] = Integer.valueOf(item3);
                this.arg_count++;
            } else if (b0 >= 247 && b0 <= 250) {
                char b1 = getCard8();
                short item4 = (short) (((b0 - 247) * 256) + b1 + 108);
                this.args[this.arg_count] = Integer.valueOf(item4);
                this.arg_count++;
            } else if (b0 >= 251 && b0 <= 254) {
                char b2 = getCard8();
                short item5 = (short) ((((-(b0 - 251)) * 256) - b2) - 108);
                this.args[this.arg_count] = Integer.valueOf(item5);
                this.arg_count++;
            } else if (b0 == 30) {
                String item6 = "";
                boolean done = false;
                char buffer = 0;
                byte avail = 0;
                int nibble = 0;
                while (!done) {
                    if (avail == 0) {
                        buffer = getCard8();
                        avail = 2;
                    }
                    if (avail == 1) {
                        nibble = buffer / 16;
                        avail = (byte) (avail - 1);
                    }
                    if (avail == 2) {
                        nibble = buffer % 16;
                        avail = (byte) (avail - 1);
                    }
                    switch (nibble) {
                        case 10:
                            item6 = item6 + ".";
                            break;
                        case 11:
                            item6 = item6 + "E";
                            break;
                        case 12:
                            item6 = item6 + "E-";
                            break;
                        case 13:
                        default:
                            if (nibble >= 0 && nibble <= 9) {
                                item6 = item6 + String.valueOf(nibble);
                            } else {
                                item6 = item6 + "<NIBBLE ERROR: " + nibble + '>';
                                done = true;
                            }
                            break;
                        case 14:
                            item6 = item6 + "-";
                            break;
                        case 15:
                            done = true;
                            break;
                    }
                }
                this.args[this.arg_count] = item6;
                this.arg_count++;
            } else if (b0 <= 21) {
                gotKey = true;
                if (b0 != '\f') {
                    this.key = operatorNames[b0];
                } else {
                    this.key = operatorNames[' ' + getCard8()];
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$Item.class */
    protected static abstract class Item {
        int myOffset = -1;

        protected Item() {
        }

        public void increment(int[] currentOffset) {
            this.myOffset = currentOffset[0];
        }

        public void emit(byte[] buffer) {
        }

        public void xref() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$OffsetItem.class */
    protected static abstract class OffsetItem extends Item {
        int value;

        protected OffsetItem() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void set(int offset) {
            this.value = offset;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$RangeItem.class */
    protected static final class RangeItem extends Item {
        private final int offset;
        private final int length;
        private final RandomAccessFileOrArray buf;

        RangeItem(RandomAccessFileOrArray buf, int offset, int length) {
            this.offset = offset;
            this.length = length;
            this.buf = buf;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + this.length;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            try {
                this.buf.seek(this.offset);
                for (int i = this.myOffset; i < this.myOffset + this.length; i++) {
                    buffer[i] = this.buf.readByte();
                }
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$IndexOffsetItem.class */
    protected static final class IndexOffsetItem extends OffsetItem {
        private final int size;

        IndexOffsetItem(int size, int value) {
            this.size = size;
            this.value = value;
        }

        IndexOffsetItem(int size) {
            this.size = size;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + this.size;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            int i = 0;
            switch (this.size) {
                case 4:
                    buffer[this.myOffset + 0] = (byte) ((this.value >>> 24) & 255);
                    i = 0 + 1;
                case 3:
                    buffer[this.myOffset + i] = (byte) ((this.value >>> 16) & 255);
                    i++;
                case 2:
                    buffer[this.myOffset + i] = (byte) ((this.value >>> 8) & 255);
                    i++;
                case 1:
                    buffer[this.myOffset + i] = (byte) ((this.value >>> 0) & 255);
                    int i2 = i + 1;
                    break;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$IndexMarkerItem.class */
    protected static final class IndexMarkerItem extends Item {
        private final OffsetItem offItem;
        private final IndexBaseItem indexBase;

        IndexMarkerItem(OffsetItem offItem, IndexBaseItem indexBase) {
            this.offItem = offItem;
            this.indexBase = indexBase;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void xref() {
            this.offItem.set((this.myOffset - this.indexBase.myOffset) + 1);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$SubrMarkerItem.class */
    protected static final class SubrMarkerItem extends Item {
        private final OffsetItem offItem;
        private final IndexBaseItem indexBase;

        SubrMarkerItem(OffsetItem offItem, IndexBaseItem indexBase) {
            this.offItem = offItem;
            this.indexBase = indexBase;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void xref() {
            this.offItem.set(this.myOffset - this.indexBase.myOffset);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$DictOffsetItem.class */
    protected static final class DictOffsetItem extends OffsetItem {
        private final int size = 5;

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + this.size;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            if (this.size == 5) {
                buffer[this.myOffset] = 29;
                buffer[this.myOffset + 1] = (byte) ((this.value >>> 24) & 255);
                buffer[this.myOffset + 2] = (byte) ((this.value >>> 16) & 255);
                buffer[this.myOffset + 3] = (byte) ((this.value >>> 8) & 255);
                buffer[this.myOffset + 4] = (byte) ((this.value >>> 0) & 255);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$UInt24Item.class */
    protected static final class UInt24Item extends Item {
        private final int value;

        UInt24Item(int value) {
            this.value = value;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + 3;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            buffer[this.myOffset + 0] = (byte) ((this.value >>> 16) & 255);
            buffer[this.myOffset + 1] = (byte) ((this.value >>> 8) & 255);
            buffer[this.myOffset + 2] = (byte) ((this.value >>> 0) & 255);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$UInt32Item.class */
    protected static final class UInt32Item extends Item {
        private final int value;

        UInt32Item(int value) {
            this.value = value;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + 4;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            buffer[this.myOffset + 0] = (byte) ((this.value >>> 24) & 255);
            buffer[this.myOffset + 1] = (byte) ((this.value >>> 16) & 255);
            buffer[this.myOffset + 2] = (byte) ((this.value >>> 8) & 255);
            buffer[this.myOffset + 3] = (byte) ((this.value >>> 0) & 255);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$UInt16Item.class */
    protected static final class UInt16Item extends Item {
        private final char value;

        UInt16Item(char value) {
            this.value = value;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + 2;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            buffer[this.myOffset + 0] = (byte) ((this.value >>> '\b') & 255);
            buffer[this.myOffset + 1] = (byte) ((this.value >>> 0) & 255);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$UInt8Item.class */
    protected static final class UInt8Item extends Item {
        private final char value;

        UInt8Item(char value) {
            this.value = value;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + 1;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            buffer[this.myOffset + 0] = (byte) ((this.value >>> 0) & 255);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$StringItem.class */
    protected static final class StringItem extends Item {
        private final String s;

        StringItem(String s) {
            this.s = s;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            currentOffset[0] = currentOffset[0] + this.s.length();
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            for (int i = 0; i < this.s.length(); i++) {
                buffer[this.myOffset + i] = (byte) (this.s.charAt(i) & 255);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$DictNumberItem.class */
    protected static final class DictNumberItem extends Item {
        private final int value;
        private final int size = 5;

        DictNumberItem(int value) {
            this.value = value;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void increment(int[] currentOffset) {
            super.increment(currentOffset);
            int i = currentOffset[0];
            getClass();
            currentOffset[0] = i + 5;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void emit(byte[] buffer) {
            getClass();
            if (5 == 5) {
                buffer[this.myOffset] = 29;
                buffer[this.myOffset + 1] = (byte) ((this.value >>> 24) & 255);
                buffer[this.myOffset + 2] = (byte) ((this.value >>> 16) & 255);
                buffer[this.myOffset + 3] = (byte) ((this.value >>> 8) & 255);
                buffer[this.myOffset + 4] = (byte) ((this.value >>> 0) & 255);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$MarkerItem.class */
    protected static final class MarkerItem extends Item {
        private final OffsetItem p;

        MarkerItem(OffsetItem pointerToMarker) {
            this.p = pointerToMarker;
        }

        @Override // com.aowagie.text.pdf.CFFFont.Item
        public void xref() {
            this.p.set(this.myOffset);
        }
    }

    protected RangeItem getEntireIndexRange(int indexOffset) {
        seek(indexOffset);
        int count = getCard16();
        if (count == 0) {
            return new RangeItem(this.buf, indexOffset, 2);
        }
        int indexOffSize = getCard8();
        seek(indexOffset + 2 + 1 + (count * indexOffSize));
        int size = getOffset(indexOffSize) - 1;
        return new RangeItem(this.buf, indexOffset, 3 + ((count + 1) * indexOffSize) + size);
    }

    public String[] getNames() {
        String[] names = new String[this.fonts.length];
        for (int i = 0; i < this.fonts.length; i++) {
            names[i] = this.fonts[i].name;
        }
        return names;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CFFFont$Font.class */
    final class Font {
        String name;
        int[] fdprivateOffsets;
        int[] fdprivateLengths;
        int nglyphs;
        int nstrings;
        int CharsetLength;
        int[] charstringsOffsets;
        int[] FDSelect;
        int FDSelectLength;
        int FDSelectFormat;
        int FDArrayCount;
        int FDArrayOffsize;
        int[] FDArrayOffsets;
        int[] PrivateSubrsOffset;
        int[][] PrivateSubrsOffsetsArray;
        int[] SubrsOffsets;
        boolean isCID = false;
        int privateOffset = -1;
        int privateLength = -1;
        int privateSubrs = -1;
        int charstringsOffset = -1;
        private int encodingOffset = -1;
        int charsetOffset = -1;
        int fdarrayOffset = -1;
        int fdselectOffset = -1;
        int CharstringType = 2;

        Font() {
        }
    }

    public CFFFont(RandomAccessFileOrArray inputbuffer) {
        this.buf = inputbuffer;
        seek(0);
        getCard8();
        getCard8();
        int hdrSize = getCard8();
        this.offSize = getCard8();
        this.nameIndexOffset = hdrSize;
        this.nameOffsets = getIndex(this.nameIndexOffset);
        this.topdictIndexOffset = this.nameOffsets[this.nameOffsets.length - 1];
        this.topdictOffsets = getIndex(this.topdictIndexOffset);
        this.stringIndexOffset = this.topdictOffsets[this.topdictOffsets.length - 1];
        this.stringOffsets = getIndex(this.stringIndexOffset);
        this.gsubrIndexOffset = this.stringOffsets[this.stringOffsets.length - 1];
        this.gsubrOffsets = getIndex(this.gsubrIndexOffset);
        this.fonts = new Font[this.nameOffsets.length - 1];
        for (int j = 0; j < this.nameOffsets.length - 1; j++) {
            this.fonts[j] = new Font();
            seek(this.nameOffsets[j]);
            this.fonts[j].name = "";
            for (int k = this.nameOffsets[j]; k < this.nameOffsets[j + 1]; k++) {
                StringBuilder sb = new StringBuilder();
                Font font = this.fonts[j];
                font.name = sb.append(font.name).append(getCard8()).toString();
            }
        }
        for (int j2 = 0; j2 < this.topdictOffsets.length - 1; j2++) {
            seek(this.topdictOffsets[j2]);
            while (getPosition() < this.topdictOffsets[j2 + 1]) {
                getDictItem();
                if (this.key == "ROS") {
                    this.fonts[j2].isCID = true;
                } else if (this.key == StandardStructureTypes.PRIVATE) {
                    this.fonts[j2].privateLength = ((Integer) this.args[0]).intValue();
                    this.fonts[j2].privateOffset = ((Integer) this.args[1]).intValue();
                } else if (this.key == "charset") {
                    this.fonts[j2].charsetOffset = ((Integer) this.args[0]).intValue();
                } else if (this.key == "Encoding") {
                    this.fonts[j2].encodingOffset = ((Integer) this.args[0]).intValue();
                    ReadEncoding(this.fonts[j2].encodingOffset);
                } else if (this.key == "CharStrings") {
                    this.fonts[j2].charstringsOffset = ((Integer) this.args[0]).intValue();
                    int p = getPosition();
                    this.fonts[j2].charstringsOffsets = getIndex(this.fonts[j2].charstringsOffset);
                    seek(p);
                } else if (this.key == "FDArray") {
                    this.fonts[j2].fdarrayOffset = ((Integer) this.args[0]).intValue();
                } else if (this.key == "FDSelect") {
                    this.fonts[j2].fdselectOffset = ((Integer) this.args[0]).intValue();
                } else if (this.key == "CharstringType") {
                    this.fonts[j2].CharstringType = ((Integer) this.args[0]).intValue();
                }
            }
            if (this.fonts[j2].privateOffset >= 0) {
                seek(this.fonts[j2].privateOffset);
                while (getPosition() < this.fonts[j2].privateOffset + this.fonts[j2].privateLength) {
                    getDictItem();
                    if (this.key == "Subrs") {
                        this.fonts[j2].privateSubrs = ((Integer) this.args[0]).intValue() + this.fonts[j2].privateOffset;
                    }
                }
            }
            if (this.fonts[j2].fdarrayOffset >= 0) {
                int[] fdarrayOffsets = getIndex(this.fonts[j2].fdarrayOffset);
                this.fonts[j2].fdprivateOffsets = new int[fdarrayOffsets.length - 1];
                this.fonts[j2].fdprivateLengths = new int[fdarrayOffsets.length - 1];
                for (int k2 = 0; k2 < fdarrayOffsets.length - 1; k2++) {
                    seek(fdarrayOffsets[k2]);
                    while (getPosition() < fdarrayOffsets[k2 + 1]) {
                        getDictItem();
                    }
                    if (this.key == StandardStructureTypes.PRIVATE) {
                        this.fonts[j2].fdprivateLengths[k2] = ((Integer) this.args[0]).intValue();
                        this.fonts[j2].fdprivateOffsets[k2] = ((Integer) this.args[1]).intValue();
                    }
                }
            }
        }
    }

    private void ReadEncoding(int nextIndexOffset) {
        seek(nextIndexOffset);
        getCard8();
    }
}
