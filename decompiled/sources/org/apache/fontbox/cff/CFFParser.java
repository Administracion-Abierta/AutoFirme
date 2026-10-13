package org.apache.fontbox.cff;

import com.aowagie.text.pdf.BaseFont;
import com.ctc.wstx.cfg.XmlConsts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.util.Charsets;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser.class */
public class CFFParser {
    private static final Log LOG = LogFactory.getLog(CFFParser.class);
    private static final String TAG_OTTO = "OTTO";
    private static final String TAG_TTCF = "ttcf";
    private static final String TAG_TTFONLY = "\u0000\u0001\u0000\u0000";
    private String[] stringIndex = null;
    private ByteSource source;
    private String debugFontName;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$ByteSource.class */
    public interface ByteSource {
        byte[] getBytes() throws IOException;
    }

    public List<CFFFont> parse(byte[] bytes, ByteSource source) throws IOException {
        this.source = source;
        return parse(bytes);
    }

    public List<CFFFont> parse(byte[] bytes) throws IOException {
        CFFDataInput input = new CFFDataInput(bytes);
        String firstTag = readTagName(input);
        if (TAG_OTTO.equals(firstTag)) {
            input = createTaggedCFFDataInput(input, bytes);
        } else {
            if (TAG_TTCF.equals(firstTag)) {
                throw new IOException("True Type Collection fonts are not supported.");
            }
            if (TAG_TTFONLY.equals(firstTag)) {
                throw new IOException("OpenType fonts containing a true type font are not supported.");
            }
            input.setPosition(0);
        }
        readHeader(input);
        String[] nameIndex = readStringIndexData(input);
        if (nameIndex == null) {
            throw new IOException("Name index missing in CFF font");
        }
        byte[][] topDictIndex = readIndexData(input);
        this.stringIndex = readStringIndexData(input);
        byte[][] globalSubrIndex = readIndexData(input);
        List<CFFFont> fonts = new ArrayList<>(nameIndex.length);
        for (int i = 0; i < nameIndex.length; i++) {
            CFFFont font = parseFont(input, nameIndex[i], topDictIndex[i]);
            font.setGlobalSubrIndex(globalSubrIndex);
            font.setData(this.source);
            fonts.add(font);
        }
        return fonts;
    }

    private CFFDataInput createTaggedCFFDataInput(CFFDataInput input, byte[] bytes) throws IOException {
        short numTables = input.readShort();
        input.readShort();
        input.readShort();
        input.readShort();
        for (int q = 0; q < numTables; q++) {
            String tagName = readTagName(input);
            readLong(input);
            long offset = readLong(input);
            long length = readLong(input);
            if (CFFTable.TAG.equals(tagName)) {
                byte[] bytes2 = Arrays.copyOfRange(bytes, (int) offset, (int) (offset + length));
                return new CFFDataInput(bytes2);
            }
        }
        throw new IOException("CFF tag not found in this OpenType font.");
    }

    private static String readTagName(CFFDataInput input) throws IOException {
        byte[] b = input.readBytes(4);
        return new String(b, Charsets.ISO_8859_1);
    }

    private static long readLong(CFFDataInput input) throws IOException {
        return (input.readCard16() << 16) | input.readCard16();
    }

    private static Header readHeader(CFFDataInput input) throws IOException {
        Header cffHeader = new Header();
        cffHeader.major = input.readCard8();
        cffHeader.minor = input.readCard8();
        cffHeader.hdrSize = input.readCard8();
        cffHeader.offSize = input.readOffSize();
        return cffHeader;
    }

    private static int[] readIndexDataOffsets(CFFDataInput input) throws IOException {
        int count = input.readCard16();
        if (count == 0) {
            return null;
        }
        int offSize = input.readOffSize();
        int[] offsets = new int[count + 1];
        for (int i = 0; i <= count; i++) {
            int offset = input.readOffset(offSize);
            if (offset > input.length()) {
                throw new IOException("illegal offset value " + offset + " in CFF font");
            }
            offsets[i] = offset;
        }
        return offsets;
    }

    private static byte[][] readIndexData(CFFDataInput input) throws IOException {
        int[] offsets = readIndexDataOffsets(input);
        if (offsets == null) {
            return (byte[][]) null;
        }
        int count = offsets.length - 1;
        byte[][] indexDataValues = new byte[count][];
        for (int i = 0; i < count; i++) {
            int length = offsets[i + 1] - offsets[i];
            indexDataValues[i] = input.readBytes(length);
        }
        return indexDataValues;
    }

    private static String[] readStringIndexData(CFFDataInput input) throws IOException {
        int[] offsets = readIndexDataOffsets(input);
        if (offsets == null) {
            return null;
        }
        int count = offsets.length - 1;
        String[] indexDataValues = new String[count];
        for (int i = 0; i < count; i++) {
            int length = offsets[i + 1] - offsets[i];
            if (length < 0) {
                throw new IOException("Negative index data length + " + length + " at " + i + ": offsets[" + (i + 1) + "]=" + offsets[i + 1] + ", offsets[" + i + "]=" + offsets[i]);
            }
            indexDataValues[i] = new String(input.readBytes(length), Charsets.ISO_8859_1);
        }
        return indexDataValues;
    }

    private static DictData readDictData(CFFDataInput input) throws IOException {
        DictData dict = new DictData();
        while (input.hasRemaining()) {
            dict.add(readEntry(input));
        }
        return dict;
    }

    private static DictData readDictData(CFFDataInput input, int dictSize) throws IOException {
        DictData dict = new DictData();
        int endPosition = input.getPosition() + dictSize;
        while (input.getPosition() < endPosition) {
            dict.add(readEntry(input));
        }
        return dict;
    }

    private static DictData.Entry readEntry(CFFDataInput input) throws IOException {
        DictData.Entry entry = new DictData.Entry();
        while (true) {
            int b0 = input.readUnsignedByte();
            if (b0 < 0 || b0 > 21) {
                if (b0 != 28 && b0 != 29) {
                    if (b0 != 30) {
                        if (b0 < 32 || b0 > 254) {
                            throw new IOException("invalid DICT data b0 byte: " + b0);
                        }
                        entry.operands.add(readIntegerNumber(input, b0));
                    } else {
                        entry.operands.add(readRealNumber(input));
                    }
                } else {
                    entry.operands.add(readIntegerNumber(input, b0));
                }
            } else {
                entry.operator = readOperator(input, b0);
                return entry;
            }
        }
    }

    private static CFFOperator readOperator(CFFDataInput input, int b0) throws IOException {
        CFFOperator.Key key = readOperatorKey(input, b0);
        return CFFOperator.getOperator(key);
    }

    private static CFFOperator.Key readOperatorKey(CFFDataInput input, int b0) throws IOException {
        if (b0 == 12) {
            int b1 = input.readUnsignedByte();
            return new CFFOperator.Key(b0, b1);
        }
        return new CFFOperator.Key(b0);
    }

    private static Integer readIntegerNumber(CFFDataInput input, int b0) throws IOException {
        if (b0 == 28) {
            return Integer.valueOf(input.readShort());
        }
        if (b0 == 29) {
            return Integer.valueOf(input.readInt());
        }
        if (b0 >= 32 && b0 <= 246) {
            return Integer.valueOf(b0 - 139);
        }
        if (b0 >= 247 && b0 <= 250) {
            int b1 = input.readUnsignedByte();
            return Integer.valueOf(((b0 - 247) * 256) + b1 + 108);
        }
        if (b0 >= 251 && b0 <= 254) {
            int b2 = input.readUnsignedByte();
            return Integer.valueOf((((-(b0 - 251)) * 256) - b2) - 108);
        }
        throw new IllegalArgumentException();
    }

    private static Double readRealNumber(CFFDataInput input) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean done = false;
        boolean exponentMissing = false;
        boolean hasExponent = false;
        int[] nibbles = new int[2];
        while (!done) {
            int b = input.readUnsignedByte();
            nibbles[0] = b / 16;
            nibbles[1] = b % 16;
            for (int nibble : nibbles) {
                switch (nibble) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        sb.append(nibble);
                        exponentMissing = false;
                        break;
                    case 10:
                        sb.append(".");
                        break;
                    case 11:
                        if (hasExponent) {
                            LOG.warn("duplicate 'E' ignored after " + ((Object) sb));
                        } else {
                            sb.append("E");
                            exponentMissing = true;
                            hasExponent = true;
                        }
                        break;
                    case 12:
                        if (hasExponent) {
                            LOG.warn("duplicate 'E-' ignored after " + ((Object) sb));
                        } else {
                            sb.append("E-");
                            exponentMissing = true;
                            hasExponent = true;
                        }
                        break;
                    case 13:
                        break;
                    case 14:
                        sb.append("-");
                        break;
                    case 15:
                        done = true;
                        break;
                    default:
                        throw new IllegalArgumentException("illegal nibble " + nibble);
                }
            }
        }
        if (exponentMissing) {
            sb.append("0");
        }
        if (sb.length() == 0) {
            return Double.valueOf(0.0d);
        }
        try {
            return Double.valueOf(sb.toString());
        } catch (NumberFormatException ex) {
            throw new IOException(ex);
        }
    }

    private CFFFont parseFont(CFFDataInput input, String name, byte[] topDictIndex) throws IOException {
        CFFFont font;
        CFFCharset charset;
        CFFDataInput topDictInput = new CFFDataInput(topDictIndex);
        DictData topDict = readDictData(topDictInput);
        DictData.Entry syntheticBaseEntry = topDict.getEntry("SyntheticBase");
        if (syntheticBaseEntry != null) {
            throw new IOException("Synthetic Fonts are not supported");
        }
        boolean isCIDFont = topDict.getEntry("ROS") != null;
        if (isCIDFont) {
            CFFCIDFont cffCIDFont = new CFFCIDFont();
            DictData.Entry rosEntry = topDict.getEntry("ROS");
            if (rosEntry == null || rosEntry.size() < 3) {
                throw new IOException("ROS entry must have 3 elements");
            }
            cffCIDFont.setRegistry(readString(rosEntry.getNumber(0).intValue()));
            cffCIDFont.setOrdering(readString(rosEntry.getNumber(1).intValue()));
            cffCIDFont.setSupplement(rosEntry.getNumber(2).intValue());
            font = cffCIDFont;
        } else {
            font = new CFFType1Font();
        }
        this.debugFontName = name;
        font.setName(name);
        font.addValueToTopDict(XmlConsts.XML_DECL_KW_VERSION, getString(topDict, XmlConsts.XML_DECL_KW_VERSION));
        font.addValueToTopDict(AFMParser.NOTICE, getString(topDict, AFMParser.NOTICE));
        font.addValueToTopDict("Copyright", getString(topDict, "Copyright"));
        font.addValueToTopDict(AFMParser.FULL_NAME, getString(topDict, AFMParser.FULL_NAME));
        font.addValueToTopDict(AFMParser.FAMILY_NAME, getString(topDict, AFMParser.FAMILY_NAME));
        font.addValueToTopDict(AFMParser.WEIGHT, getString(topDict, AFMParser.WEIGHT));
        font.addValueToTopDict("isFixedPitch", topDict.getBoolean("isFixedPitch", false));
        font.addValueToTopDict(AFMParser.ITALIC_ANGLE, topDict.getNumber(AFMParser.ITALIC_ANGLE, 0));
        font.addValueToTopDict(AFMParser.UNDERLINE_POSITION, topDict.getNumber(AFMParser.UNDERLINE_POSITION, -100));
        font.addValueToTopDict(AFMParser.UNDERLINE_THICKNESS, topDict.getNumber(AFMParser.UNDERLINE_THICKNESS, 50));
        font.addValueToTopDict("PaintType", topDict.getNumber("PaintType", 0));
        font.addValueToTopDict("CharstringType", topDict.getNumber("CharstringType", 2));
        font.addValueToTopDict("FontMatrix", topDict.getArray("FontMatrix", Arrays.asList(Double.valueOf(0.001d), Double.valueOf(0.0d), Double.valueOf(0.0d), Double.valueOf(0.001d), Double.valueOf(0.0d), Double.valueOf(0.0d))));
        font.addValueToTopDict("UniqueID", topDict.getNumber("UniqueID", null));
        font.addValueToTopDict(AFMParser.FONT_BBOX, topDict.getArray(AFMParser.FONT_BBOX, Arrays.asList(0, 0, 0, 0)));
        font.addValueToTopDict("StrokeWidth", topDict.getNumber("StrokeWidth", 0));
        font.addValueToTopDict("XUID", topDict.getArray("XUID", null));
        DictData.Entry charStringsEntry = topDict.getEntry("CharStrings");
        if (charStringsEntry == null || !charStringsEntry.hasOperands()) {
            throw new IOException("CharStrings is missing or empty");
        }
        int charStringsOffset = charStringsEntry.getNumber(0).intValue();
        input.setPosition(charStringsOffset);
        byte[][] charStringsIndex = readIndexData(input);
        DictData.Entry charsetEntry = topDict.getEntry("charset");
        if (charsetEntry != null && charsetEntry.hasOperands()) {
            int charsetId = charsetEntry.getNumber(0).intValue();
            if (!isCIDFont && charsetId == 0) {
                charset = CFFISOAdobeCharset.getInstance();
            } else if (!isCIDFont && charsetId == 1) {
                charset = CFFExpertCharset.getInstance();
            } else if (!isCIDFont && charsetId == 2) {
                charset = CFFExpertSubsetCharset.getInstance();
            } else {
                input.setPosition(charsetId);
                charset = readCharset(input, charStringsIndex.length, isCIDFont);
            }
        } else if (isCIDFont) {
            charset = new EmptyCharset(charStringsIndex.length);
        } else {
            charset = CFFISOAdobeCharset.getInstance();
        }
        font.setCharset(charset);
        font.charStrings = charStringsIndex;
        if (isCIDFont) {
            parseCIDFontDicts(input, topDict, (CFFCIDFont) font, charStringsIndex.length);
            List<Number> privMatrix = null;
            List<Map<String, Object>> fontDicts = ((CFFCIDFont) font).getFontDicts();
            if (!fontDicts.isEmpty() && fontDicts.get(0).containsKey("FontMatrix")) {
                privMatrix = (List) fontDicts.get(0).get("FontMatrix");
            }
            List<Number> matrix = topDict.getArray("FontMatrix", null);
            if (matrix == null) {
                if (privMatrix != null) {
                    font.addValueToTopDict("FontMatrix", privMatrix);
                } else {
                    font.addValueToTopDict("FontMatrix", topDict.getArray("FontMatrix", Arrays.asList(Double.valueOf(0.001d), Double.valueOf(0.0d), Double.valueOf(0.0d), Double.valueOf(0.001d), Double.valueOf(0.0d), Double.valueOf(0.0d))));
                }
            } else if (privMatrix != null) {
                concatenateMatrix(matrix, privMatrix);
            }
        } else {
            parseType1Dicts(input, topDict, (CFFType1Font) font, charset);
        }
        return font;
    }

    private void concatenateMatrix(List<Number> matrixDest, List<Number> matrixConcat) {
        double a1 = matrixDest.get(0).doubleValue();
        double b1 = matrixDest.get(1).doubleValue();
        double c1 = matrixDest.get(2).doubleValue();
        double d1 = matrixDest.get(3).doubleValue();
        double x1 = matrixDest.get(4).doubleValue();
        double y1 = matrixDest.get(5).doubleValue();
        double a2 = matrixConcat.get(0).doubleValue();
        double b2 = matrixConcat.get(1).doubleValue();
        double c2 = matrixConcat.get(2).doubleValue();
        double d2 = matrixConcat.get(3).doubleValue();
        double x2 = matrixConcat.get(4).doubleValue();
        double y2 = matrixConcat.get(5).doubleValue();
        matrixDest.set(0, Double.valueOf((a1 * a2) + (b1 * c2)));
        matrixDest.set(1, Double.valueOf((a1 * b2) + (b1 * d1)));
        matrixDest.set(2, Double.valueOf((c1 * a2) + (d1 * c2)));
        matrixDest.set(3, Double.valueOf((c1 * b2) + (d1 * d2)));
        matrixDest.set(4, Double.valueOf((x1 * a2) + (y1 * c2) + x2));
        matrixDest.set(5, Double.valueOf((x1 * b2) + (y1 * d2) + y2));
    }

    private void parseCIDFontDicts(CFFDataInput input, DictData topDict, CFFCIDFont font, int nrOfcharStrings) throws IOException {
        DictData.Entry fdArrayEntry = topDict.getEntry("FDArray");
        if (fdArrayEntry == null || !fdArrayEntry.hasOperands()) {
            throw new IOException("FDArray is missing for a CIDKeyed Font.");
        }
        int fontDictOffset = fdArrayEntry.getNumber(0).intValue();
        input.setPosition(fontDictOffset);
        byte[][] fdIndex = readIndexData(input);
        if (fdIndex == null) {
            throw new IOException("Font dict index is missing for a CIDKeyed Font");
        }
        List<Map<String, Object>> privateDictionaries = new LinkedList<>();
        List<Map<String, Object>> fontDictionaries = new LinkedList<>();
        for (byte[] bytes : fdIndex) {
            CFFDataInput fontDictInput = new CFFDataInput(bytes);
            DictData fontDict = readDictData(fontDictInput);
            DictData.Entry privateEntry = fontDict.getEntry(StandardStructureTypes.PRIVATE);
            if (privateEntry == null || privateEntry.size() < 2) {
                throw new IOException("Font DICT invalid without \"Private\" entry");
            }
            Map<String, Object> fontDictMap = new LinkedHashMap<>(4);
            fontDictMap.put(AFMParser.FONT_NAME, getString(fontDict, AFMParser.FONT_NAME));
            fontDictMap.put("FontType", fontDict.getNumber("FontType", 0));
            fontDictMap.put(AFMParser.FONT_BBOX, fontDict.getArray(AFMParser.FONT_BBOX, null));
            fontDictMap.put("FontMatrix", fontDict.getArray("FontMatrix", null));
            fontDictionaries.add(fontDictMap);
            int privateOffset = privateEntry.getNumber(1).intValue();
            input.setPosition(privateOffset);
            int privateSize = privateEntry.getNumber(0).intValue();
            DictData privateDict = readDictData(input, privateSize);
            Map<String, Object> privDict = readPrivateDict(privateDict);
            privateDictionaries.add(privDict);
            Number localSubrOffset = privateDict.getNumber("Subrs", 0);
            if ((localSubrOffset instanceof Integer) && ((Integer) localSubrOffset).intValue() > 0) {
                input.setPosition(privateOffset + ((Integer) localSubrOffset).intValue());
                privDict.put("Subrs", readIndexData(input));
            }
        }
        DictData.Entry fdSelectEntry = topDict.getEntry("FDSelect");
        if (fdSelectEntry == null || !fdSelectEntry.hasOperands()) {
            throw new IOException("FDSelect is missing or empty");
        }
        int fdSelectPos = fdSelectEntry.getNumber(0).intValue();
        input.setPosition(fdSelectPos);
        FDSelect fdSelect = readFDSelect(input, nrOfcharStrings, font);
        font.setFontDict(fontDictionaries);
        font.setPrivDict(privateDictionaries);
        font.setFdSelect(fdSelect);
    }

    private Map<String, Object> readPrivateDict(DictData privateDict) {
        Map<String, Object> privDict = new LinkedHashMap<>(17);
        privDict.put("BlueValues", privateDict.getDelta("BlueValues", null));
        privDict.put("OtherBlues", privateDict.getDelta("OtherBlues", null));
        privDict.put("FamilyBlues", privateDict.getDelta("FamilyBlues", null));
        privDict.put("FamilyOtherBlues", privateDict.getDelta("FamilyOtherBlues", null));
        privDict.put("BlueScale", privateDict.getNumber("BlueScale", Double.valueOf(0.039625d)));
        privDict.put("BlueShift", privateDict.getNumber("BlueShift", 7));
        privDict.put("BlueFuzz", privateDict.getNumber("BlueFuzz", 1));
        privDict.put(AFMParser.STD_HW, privateDict.getNumber(AFMParser.STD_HW, null));
        privDict.put(AFMParser.STD_VW, privateDict.getNumber(AFMParser.STD_VW, null));
        privDict.put("StemSnapH", privateDict.getDelta("StemSnapH", null));
        privDict.put("StemSnapV", privateDict.getDelta("StemSnapV", null));
        privDict.put("ForceBold", privateDict.getBoolean("ForceBold", false));
        privDict.put("LanguageGroup", privateDict.getNumber("LanguageGroup", 0));
        privDict.put("ExpansionFactor", privateDict.getNumber("ExpansionFactor", Double.valueOf(0.06d)));
        privDict.put("initialRandomSeed", privateDict.getNumber("initialRandomSeed", 0));
        privDict.put("defaultWidthX", privateDict.getNumber("defaultWidthX", 0));
        privDict.put("nominalWidthX", privateDict.getNumber("nominalWidthX", 0));
        return privDict;
    }

    private void parseType1Dicts(CFFDataInput input, DictData topDict, CFFType1Font font, CFFCharset charset) throws IOException {
        CFFEncoding encoding;
        DictData.Entry encodingEntry = topDict.getEntry("Encoding");
        int encodingId = (encodingEntry == null || !encodingEntry.hasOperands()) ? 0 : encodingEntry.getNumber(0).intValue();
        switch (encodingId) {
            case 0:
                encoding = CFFStandardEncoding.getInstance();
                break;
            case 1:
                encoding = CFFExpertEncoding.getInstance();
                break;
            default:
                input.setPosition(encodingId);
                encoding = readEncoding(input, charset);
                break;
        }
        font.setEncoding(encoding);
        DictData.Entry privateEntry = topDict.getEntry(StandardStructureTypes.PRIVATE);
        if (privateEntry == null || privateEntry.size() < 2) {
            throw new IOException("Private dictionary entry missing for font " + font.fontName);
        }
        int privateOffset = privateEntry.getNumber(1).intValue();
        input.setPosition(privateOffset);
        int privateSize = privateEntry.getNumber(0).intValue();
        DictData privateDict = readDictData(input, privateSize);
        Map<String, Object> privDict = readPrivateDict(privateDict);
        for (Map.Entry<String, Object> entry : privDict.entrySet()) {
            font.addToPrivateDict(entry.getKey(), entry.getValue());
        }
        Number localSubrOffset = privateDict.getNumber("Subrs", 0);
        if ((localSubrOffset instanceof Integer) && ((Integer) localSubrOffset).intValue() > 0) {
            input.setPosition(privateOffset + ((Integer) localSubrOffset).intValue());
            font.addToPrivateDict("Subrs", readIndexData(input));
        }
    }

    private String readString(int index) {
        if (index >= 0 && index <= 390) {
            return CFFStandardString.getName(index);
        }
        if (index - 391 < this.stringIndex.length) {
            return this.stringIndex[index - 391];
        }
        return "SID" + index;
    }

    private String getString(DictData dict, String name) {
        DictData.Entry entry = dict.getEntry(name);
        if (entry == null || !entry.hasOperands()) {
            return null;
        }
        return readString(entry.getNumber(0).intValue());
    }

    private CFFEncoding readEncoding(CFFDataInput dataInput, CFFCharset charset) throws IOException {
        int format = dataInput.readCard8();
        int baseFormat = format & 127;
        switch (baseFormat) {
            case 0:
                return readFormat0Encoding(dataInput, charset, format);
            case 1:
                return readFormat1Encoding(dataInput, charset, format);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Format0Encoding readFormat0Encoding(CFFDataInput dataInput, CFFCharset charset, int format) throws IOException {
        Format0Encoding encoding = new Format0Encoding();
        encoding.format = format;
        encoding.nCodes = dataInput.readCard8();
        encoding.add(0, 0, BaseFont.notdef);
        for (int gid = 1; gid <= encoding.nCodes; gid++) {
            int code = dataInput.readCard8();
            int sid = charset.getSIDForGID(gid);
            encoding.add(code, sid, readString(sid));
        }
        if ((format & 128) != 0) {
            readSupplement(dataInput, encoding);
        }
        return encoding;
    }

    private Format1Encoding readFormat1Encoding(CFFDataInput dataInput, CFFCharset charset, int format) throws IOException {
        Format1Encoding encoding = new Format1Encoding();
        encoding.format = format;
        encoding.nRanges = dataInput.readCard8();
        encoding.add(0, 0, BaseFont.notdef);
        int gid = 1;
        for (int i = 0; i < encoding.nRanges; i++) {
            int rangeFirst = dataInput.readCard8();
            int rangeLeft = dataInput.readCard8();
            for (int j = 0; j <= rangeLeft; j++) {
                int sid = charset.getSIDForGID(gid);
                int code = rangeFirst + j;
                encoding.add(code, sid, readString(sid));
                gid++;
            }
        }
        if ((format & 128) != 0) {
            readSupplement(dataInput, encoding);
        }
        return encoding;
    }

    private void readSupplement(CFFDataInput dataInput, CFFBuiltInEncoding encoding) throws IOException {
        encoding.nSups = dataInput.readCard8();
        encoding.supplement = new CFFBuiltInEncoding.Supplement[encoding.nSups];
        for (int i = 0; i < encoding.supplement.length; i++) {
            CFFBuiltInEncoding.Supplement supplement = new CFFBuiltInEncoding.Supplement();
            supplement.code = dataInput.readCard8();
            supplement.sid = dataInput.readSID();
            supplement.name = readString(supplement.sid);
            encoding.supplement[i] = supplement;
            encoding.add(supplement.code, supplement.sid, readString(supplement.sid));
        }
    }

    private static FDSelect readFDSelect(CFFDataInput dataInput, int nGlyphs, CFFCIDFont ros) throws IOException {
        int format = dataInput.readCard8();
        switch (format) {
            case 0:
                return readFormat0FDSelect(dataInput, format, nGlyphs, ros);
            case 3:
                return readFormat3FDSelect(dataInput, format, nGlyphs, ros);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Format0FDSelect readFormat0FDSelect(CFFDataInput dataInput, int format, int nGlyphs, CFFCIDFont ros) throws IOException {
        Format0FDSelect fdselect = new Format0FDSelect(ros);
        fdselect.format = format;
        fdselect.fds = new int[nGlyphs];
        for (int i = 0; i < fdselect.fds.length; i++) {
            fdselect.fds[i] = dataInput.readCard8();
        }
        return fdselect;
    }

    private static Format3FDSelect readFormat3FDSelect(CFFDataInput dataInput, int format, int nGlyphs, CFFCIDFont ros) throws IOException {
        Format3FDSelect fdselect = new Format3FDSelect(ros);
        fdselect.format = format;
        fdselect.nbRanges = dataInput.readCard16();
        fdselect.range3 = new Range3[fdselect.nbRanges];
        for (int i = 0; i < fdselect.nbRanges; i++) {
            Range3 r3 = new Range3();
            r3.first = dataInput.readCard16();
            r3.fd = dataInput.readCard8();
            fdselect.range3[i] = r3;
        }
        fdselect.sentinel = dataInput.readCard16();
        return fdselect;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format3FDSelect.class */
    private static final class Format3FDSelect extends FDSelect {
        private int format;
        private int nbRanges;
        private Range3[] range3;
        private int sentinel;

        private Format3FDSelect(CFFCIDFont owner) {
            super(owner);
        }

        @Override // org.apache.fontbox.cff.FDSelect
        public int getFDIndex(int gid) {
            for (int i = 0; i < this.nbRanges; i++) {
                if (this.range3[i].first <= gid) {
                    if (i + 1 >= this.nbRanges) {
                        if (this.sentinel <= gid) {
                            return -1;
                        }
                        return this.range3[i].fd;
                    }
                    if (this.range3[i + 1].first > gid) {
                        return this.range3[i].fd;
                    }
                }
            }
            return 0;
        }

        public String toString() {
            return getClass().getName() + "[format=" + this.format + " nbRanges=" + this.nbRanges + ", range3=" + Arrays.toString(this.range3) + " sentinel=" + this.sentinel + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Range3.class */
    private static final class Range3 {
        private int first;
        private int fd;

        private Range3() {
        }

        public String toString() {
            return getClass().getName() + "[first=" + this.first + ", fd=" + this.fd + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format0FDSelect.class */
    private static class Format0FDSelect extends FDSelect {
        private int format;
        private int[] fds;

        private Format0FDSelect(CFFCIDFont owner) {
            super(owner);
        }

        @Override // org.apache.fontbox.cff.FDSelect
        public int getFDIndex(int gid) {
            if (gid < this.fds.length) {
                return this.fds[gid];
            }
            return 0;
        }

        public String toString() {
            return getClass().getName() + "[fds=" + Arrays.toString(this.fds) + "]";
        }
    }

    private CFFCharset readCharset(CFFDataInput dataInput, int nGlyphs, boolean isCIDFont) throws IOException {
        int format = dataInput.readCard8();
        switch (format) {
            case 0:
                return readFormat0Charset(dataInput, format, nGlyphs, isCIDFont);
            case 1:
                return readFormat1Charset(dataInput, format, nGlyphs, isCIDFont);
            case 2:
                return readFormat2Charset(dataInput, format, nGlyphs, isCIDFont);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Format0Charset readFormat0Charset(CFFDataInput dataInput, int format, int nGlyphs, boolean isCIDFont) throws IOException {
        Format0Charset charset = new Format0Charset(isCIDFont);
        charset.format = format;
        if (isCIDFont) {
            charset.addCID(0, 0);
        } else {
            charset.addSID(0, 0, BaseFont.notdef);
        }
        for (int gid = 1; gid < nGlyphs; gid++) {
            int sid = dataInput.readSID();
            if (isCIDFont) {
                charset.addCID(gid, sid);
            } else {
                charset.addSID(gid, sid, readString(sid));
            }
        }
        return charset;
    }

    private Format1Charset readFormat1Charset(CFFDataInput dataInput, int format, int nGlyphs, boolean isCIDFont) throws IOException {
        Format1Charset charset = new Format1Charset(isCIDFont);
        charset.format = format;
        if (isCIDFont) {
            charset.addCID(0, 0);
            charset.rangesCID2GID = new ArrayList();
        } else {
            charset.addSID(0, 0, BaseFont.notdef);
        }
        int gid = 1;
        while (gid < nGlyphs) {
            int rangeFirst = dataInput.readSID();
            int rangeLeft = dataInput.readCard8();
            if (!isCIDFont) {
                for (int j = 0; j < 1 + rangeLeft; j++) {
                    int sid = rangeFirst + j;
                    charset.addSID(gid + j, sid, readString(sid));
                }
            } else {
                charset.rangesCID2GID.add(new RangeMapping(gid, rangeFirst, rangeLeft));
            }
            gid = gid + rangeLeft + 1;
        }
        return charset;
    }

    private Format2Charset readFormat2Charset(CFFDataInput dataInput, int format, int nGlyphs, boolean isCIDFont) throws IOException {
        Format2Charset charset = new Format2Charset(isCIDFont);
        charset.format = format;
        if (isCIDFont) {
            charset.addCID(0, 0);
            charset.rangesCID2GID = new ArrayList();
        } else {
            charset.addSID(0, 0, BaseFont.notdef);
        }
        int gid = 1;
        while (gid < nGlyphs) {
            int first = dataInput.readSID();
            int nLeft = dataInput.readCard16();
            if (!isCIDFont) {
                for (int j = 0; j < 1 + nLeft; j++) {
                    int sid = first + j;
                    charset.addSID(gid + j, sid, readString(sid));
                }
            } else {
                charset.rangesCID2GID.add(new RangeMapping(gid, first, nLeft));
            }
            gid = gid + nLeft + 1;
        }
        return charset;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Header.class */
    private static class Header {
        private int major;
        private int minor;
        private int hdrSize;
        private int offSize;

        private Header() {
        }

        public String toString() {
            return getClass().getName() + "[major=" + this.major + ", minor=" + this.minor + ", hdrSize=" + this.hdrSize + ", offSize=" + this.offSize + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$DictData.class */
    private static class DictData {
        private final Map<String, Entry> entries;

        private DictData() {
            this.entries = new HashMap();
        }

        public void add(Entry entry) {
            if (entry.operator == null) {
                return;
            }
            this.entries.put(entry.operator.getName(), entry);
        }

        public Entry getEntry(String name) {
            return this.entries.get(name);
        }

        public Boolean getBoolean(String name, boolean defaultValue) {
            Entry entry = getEntry(name);
            return Boolean.valueOf((entry == null || entry.getArray().isEmpty()) ? defaultValue : entry.getBoolean(0).booleanValue());
        }

        public List<Number> getArray(String name, List<Number> defaultValue) {
            Entry entry = getEntry(name);
            return (entry == null || entry.getArray().isEmpty()) ? defaultValue : entry.getArray();
        }

        public Number getNumber(String name, Number defaultValue) {
            Entry entry = getEntry(name);
            return (entry == null || entry.getArray().isEmpty()) ? defaultValue : entry.getNumber(0);
        }

        public List<Number> getDelta(String name, List<Number> defaultValue) {
            Entry entry = getEntry(name);
            return (entry == null || entry.getArray().isEmpty()) ? defaultValue : entry.getDelta();
        }

        public String toString() {
            return getClass().getName() + "[entries=" + this.entries + "]";
        }

        /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$DictData$Entry.class */
        private static class Entry {
            private List<Number> operands;
            private CFFOperator operator;

            private Entry() {
                this.operands = new ArrayList();
                this.operator = null;
            }

            public Number getNumber(int index) {
                return this.operands.get(index);
            }

            public int size() {
                return this.operands.size();
            }

            public Boolean getBoolean(int index) {
                Number operand = this.operands.get(index);
                if (operand instanceof Integer) {
                    switch (operand.intValue()) {
                        case 0:
                            return Boolean.FALSE;
                        case 1:
                            return Boolean.TRUE;
                    }
                }
                throw new IllegalArgumentException();
            }

            public boolean hasOperands() {
                return !this.operands.isEmpty();
            }

            public List<Number> getArray() {
                return this.operands;
            }

            public List<Number> getDelta() {
                List<Number> result = new ArrayList<>(this.operands);
                for (int i = 1; i < result.size(); i++) {
                    Number previous = result.get(i - 1);
                    Number current = result.get(i);
                    Integer sum = Integer.valueOf(previous.intValue() + current.intValue());
                    result.set(i, sum);
                }
                return result;
            }

            public String toString() {
                return getClass().getName() + "[operands=" + this.operands + ", operator=" + this.operator + "]";
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$CFFBuiltInEncoding.class */
    static abstract class CFFBuiltInEncoding extends CFFEncoding {
        private int nSups;
        private Supplement[] supplement;

        CFFBuiltInEncoding() {
        }

        /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$CFFBuiltInEncoding$Supplement.class */
        static class Supplement {
            private int code;
            private int sid;
            private String name;

            Supplement() {
            }

            public int getCode() {
                return this.code;
            }

            public int getSID() {
                return this.sid;
            }

            public String getName() {
                return this.name;
            }

            public String toString() {
                return getClass().getName() + "[code=" + this.code + ", sid=" + this.sid + "]";
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format0Encoding.class */
    private static class Format0Encoding extends CFFBuiltInEncoding {
        private int format;
        private int nCodes;

        private Format0Encoding() {
        }

        public String toString() {
            return getClass().getName() + "[format=" + this.format + ", nCodes=" + this.nCodes + ", supplement=" + Arrays.toString(((CFFBuiltInEncoding) this).supplement) + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format1Encoding.class */
    private static class Format1Encoding extends CFFBuiltInEncoding {
        private int format;
        private int nRanges;

        private Format1Encoding() {
        }

        public String toString() {
            return getClass().getName() + "[format=" + this.format + ", nRanges=" + this.nRanges + ", supplement=" + Arrays.toString(((CFFBuiltInEncoding) this).supplement) + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$EmbeddedCharset.class */
    static abstract class EmbeddedCharset extends CFFCharset {
        protected EmbeddedCharset(boolean isCIDFont) {
            super(isCIDFont);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$EmptyCharset.class */
    private static class EmptyCharset extends EmbeddedCharset {
        protected EmptyCharset(int numCharStrings) {
            super(true);
            addCID(0, 0);
            for (int i = 1; i <= numCharStrings; i++) {
                addCID(i, i);
            }
        }

        public String toString() {
            return getClass().getName();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format0Charset.class */
    private static class Format0Charset extends EmbeddedCharset {
        private int format;

        protected Format0Charset(boolean isCIDFont) {
            super(isCIDFont);
        }

        public String toString() {
            return getClass().getName() + "[format=" + this.format + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format1Charset.class */
    private static class Format1Charset extends EmbeddedCharset {
        private int format;
        private List<RangeMapping> rangesCID2GID;

        protected Format1Charset(boolean isCIDFont) {
            super(isCIDFont);
        }

        @Override // org.apache.fontbox.cff.CFFCharset
        public int getCIDForGID(int gid) {
            if (isCIDFont()) {
                for (RangeMapping mapping : this.rangesCID2GID) {
                    if (mapping.isInRange(gid)) {
                        return mapping.mapValue(gid);
                    }
                }
            }
            return super.getCIDForGID(gid);
        }

        @Override // org.apache.fontbox.cff.CFFCharset
        public int getGIDForCID(int cid) {
            if (isCIDFont()) {
                for (RangeMapping mapping : this.rangesCID2GID) {
                    if (mapping.isInReverseRange(cid)) {
                        return mapping.mapReverseValue(cid);
                    }
                }
            }
            return super.getGIDForCID(cid);
        }

        public String toString() {
            return getClass().getName() + "[format=" + this.format + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$Format2Charset.class */
    private static class Format2Charset extends EmbeddedCharset {
        private int format;
        private List<RangeMapping> rangesCID2GID;

        protected Format2Charset(boolean isCIDFont) {
            super(isCIDFont);
        }

        @Override // org.apache.fontbox.cff.CFFCharset
        public int getCIDForGID(int gid) {
            for (RangeMapping mapping : this.rangesCID2GID) {
                if (mapping.isInRange(gid)) {
                    return mapping.mapValue(gid);
                }
            }
            return super.getCIDForGID(gid);
        }

        @Override // org.apache.fontbox.cff.CFFCharset
        public int getGIDForCID(int cid) {
            for (RangeMapping mapping : this.rangesCID2GID) {
                if (mapping.isInReverseRange(cid)) {
                    return mapping.mapReverseValue(cid);
                }
            }
            return super.getGIDForCID(cid);
        }

        public String toString() {
            return getClass().getName() + "[format=" + this.format + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFParser$RangeMapping.class */
    private static final class RangeMapping {
        private final int startValue;
        private final int endValue;
        private final int startMappedValue;
        private final int endMappedValue;

        private RangeMapping(int startGID, int first, int nLeft) {
            this.startValue = startGID;
            this.endValue = this.startValue + nLeft;
            this.startMappedValue = first;
            this.endMappedValue = this.startMappedValue + nLeft;
        }

        boolean isInRange(int value) {
            return value >= this.startValue && value <= this.endValue;
        }

        boolean isInReverseRange(int value) {
            return value >= this.startMappedValue && value <= this.endMappedValue;
        }

        int mapValue(int value) {
            if (isInRange(value)) {
                return this.startMappedValue + (value - this.startValue);
            }
            return 0;
        }

        int mapReverseValue(int value) {
            if (isInReverseRange(value)) {
                return this.startValue + (value - this.startMappedValue);
            }
            return 0;
        }

        public String toString() {
            return getClass().getName() + "[start value=" + this.startValue + ", end value=" + this.endValue + ", start mapped-value=" + this.startMappedValue + ", end mapped-value=" + this.endMappedValue + "]";
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + this.debugFontName + "]";
    }
}
