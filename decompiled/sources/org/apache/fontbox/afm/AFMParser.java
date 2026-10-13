package org.apache.fontbox.afm;

import com.aowagie.text.pdf.ColumnText;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.fontbox.util.BoundingBox;
import org.apache.fontbox.util.Charsets;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/AFMParser.class */
public class AFMParser {
    public static final String COMMENT = "Comment";
    public static final String START_FONT_METRICS = "StartFontMetrics";
    public static final String END_FONT_METRICS = "EndFontMetrics";
    public static final String FONT_NAME = "FontName";
    public static final String FULL_NAME = "FullName";
    public static final String FAMILY_NAME = "FamilyName";
    public static final String WEIGHT = "Weight";
    public static final String FONT_BBOX = "FontBBox";
    public static final String VERSION = "Version";
    public static final String NOTICE = "Notice";
    public static final String ENCODING_SCHEME = "EncodingScheme";
    public static final String MAPPING_SCHEME = "MappingScheme";
    public static final String ESC_CHAR = "EscChar";
    public static final String CHARACTER_SET = "CharacterSet";
    public static final String CHARACTERS = "Characters";
    public static final String IS_BASE_FONT = "IsBaseFont";
    public static final String V_VECTOR = "VVector";
    public static final String IS_FIXED_V = "IsFixedV";
    public static final String CAP_HEIGHT = "CapHeight";
    public static final String X_HEIGHT = "XHeight";
    public static final String ASCENDER = "Ascender";
    public static final String DESCENDER = "Descender";
    public static final String UNDERLINE_POSITION = "UnderlinePosition";
    public static final String UNDERLINE_THICKNESS = "UnderlineThickness";
    public static final String ITALIC_ANGLE = "ItalicAngle";
    public static final String CHAR_WIDTH = "CharWidth";
    public static final String IS_FIXED_PITCH = "IsFixedPitch";
    public static final String START_CHAR_METRICS = "StartCharMetrics";
    public static final String END_CHAR_METRICS = "EndCharMetrics";
    public static final String CHARMETRICS_C = "C";
    public static final String CHARMETRICS_CH = "CH";
    public static final String CHARMETRICS_WX = "WX";
    public static final String CHARMETRICS_W0X = "W0X";
    public static final String CHARMETRICS_W1X = "W1X";
    public static final String CHARMETRICS_WY = "WY";
    public static final String CHARMETRICS_W0Y = "W0Y";
    public static final String CHARMETRICS_W1Y = "W1Y";
    public static final String CHARMETRICS_W = "W";
    public static final String CHARMETRICS_W0 = "W0";
    public static final String CHARMETRICS_W1 = "W1";
    public static final String CHARMETRICS_VV = "VV";
    public static final String CHARMETRICS_N = "N";
    public static final String CHARMETRICS_B = "B";
    public static final String CHARMETRICS_L = "L";
    public static final String STD_HW = "StdHW";
    public static final String STD_VW = "StdVW";
    public static final String START_TRACK_KERN = "StartTrackKern";
    public static final String END_TRACK_KERN = "EndTrackKern";
    public static final String START_KERN_DATA = "StartKernData";
    public static final String END_KERN_DATA = "EndKernData";
    public static final String START_KERN_PAIRS = "StartKernPairs";
    public static final String END_KERN_PAIRS = "EndKernPairs";
    public static final String START_KERN_PAIRS0 = "StartKernPairs0";
    public static final String START_KERN_PAIRS1 = "StartKernPairs1";
    public static final String START_COMPOSITES = "StartComposites";
    public static final String END_COMPOSITES = "EndComposites";
    public static final String CC = "CC";
    public static final String PCC = "PCC";
    public static final String KERN_PAIR_KP = "KP";
    public static final String KERN_PAIR_KPH = "KPH";
    public static final String KERN_PAIR_KPX = "KPX";
    public static final String KERN_PAIR_KPY = "KPY";
    private static final int BITS_IN_HEX = 16;
    private final InputStream input;

    public AFMParser(InputStream in) {
        this.input = in;
    }

    public FontMetrics parse() throws IOException {
        return parseFontMetric(false);
    }

    public FontMetrics parse(boolean reducedDataset) throws IOException {
        return parseFontMetric(reducedDataset);
    }

    private FontMetrics parseFontMetric(boolean reducedDataset) throws IOException {
        FontMetrics fontMetrics = new FontMetrics();
        String startFontMetrics = readString();
        if (!START_FONT_METRICS.equals(startFontMetrics)) {
            throw new IOException("Error: The AFM file should start with StartFontMetrics and not '" + startFontMetrics + OperatorName.SHOW_TEXT_LINE);
        }
        fontMetrics.setAFMVersion(readFloat());
        boolean charMetricsRead = false;
        while (true) {
            String nextCommand = readString();
            if (END_FONT_METRICS.equals(nextCommand)) {
                break;
            }
            if (FONT_NAME.equals(nextCommand)) {
                fontMetrics.setFontName(readLine());
            } else if (FULL_NAME.equals(nextCommand)) {
                fontMetrics.setFullName(readLine());
            } else if (FAMILY_NAME.equals(nextCommand)) {
                fontMetrics.setFamilyName(readLine());
            } else if (WEIGHT.equals(nextCommand)) {
                fontMetrics.setWeight(readLine());
            } else if (FONT_BBOX.equals(nextCommand)) {
                BoundingBox bBox = new BoundingBox();
                bBox.setLowerLeftX(readFloat());
                bBox.setLowerLeftY(readFloat());
                bBox.setUpperRightX(readFloat());
                bBox.setUpperRightY(readFloat());
                fontMetrics.setFontBBox(bBox);
            } else if (VERSION.equals(nextCommand)) {
                fontMetrics.setFontVersion(readLine());
            } else if (NOTICE.equals(nextCommand)) {
                fontMetrics.setNotice(readLine());
            } else if (ENCODING_SCHEME.equals(nextCommand)) {
                fontMetrics.setEncodingScheme(readLine());
            } else if (MAPPING_SCHEME.equals(nextCommand)) {
                fontMetrics.setMappingScheme(readInt());
            } else if (ESC_CHAR.equals(nextCommand)) {
                fontMetrics.setEscChar(readInt());
            } else if (CHARACTER_SET.equals(nextCommand)) {
                fontMetrics.setCharacterSet(readLine());
            } else if (CHARACTERS.equals(nextCommand)) {
                fontMetrics.setCharacters(readInt());
            } else if (IS_BASE_FONT.equals(nextCommand)) {
                fontMetrics.setIsBaseFont(readBoolean());
            } else if (V_VECTOR.equals(nextCommand)) {
                float[] vector = {readFloat(), readFloat()};
                fontMetrics.setVVector(vector);
            } else if (IS_FIXED_V.equals(nextCommand)) {
                fontMetrics.setIsFixedV(readBoolean());
            } else if (CAP_HEIGHT.equals(nextCommand)) {
                fontMetrics.setCapHeight(readFloat());
            } else if (X_HEIGHT.equals(nextCommand)) {
                fontMetrics.setXHeight(readFloat());
            } else if (ASCENDER.equals(nextCommand)) {
                fontMetrics.setAscender(readFloat());
            } else if (DESCENDER.equals(nextCommand)) {
                fontMetrics.setDescender(readFloat());
            } else if (STD_HW.equals(nextCommand)) {
                fontMetrics.setStandardHorizontalWidth(readFloat());
            } else if (STD_VW.equals(nextCommand)) {
                fontMetrics.setStandardVerticalWidth(readFloat());
            } else if ("Comment".equals(nextCommand)) {
                fontMetrics.addComment(readLine());
            } else if (UNDERLINE_POSITION.equals(nextCommand)) {
                fontMetrics.setUnderlinePosition(readFloat());
            } else if (UNDERLINE_THICKNESS.equals(nextCommand)) {
                fontMetrics.setUnderlineThickness(readFloat());
            } else if (ITALIC_ANGLE.equals(nextCommand)) {
                fontMetrics.setItalicAngle(readFloat());
            } else if (CHAR_WIDTH.equals(nextCommand)) {
                float[] widths = {readFloat(), readFloat()};
                fontMetrics.setCharWidth(widths);
            } else if (IS_FIXED_PITCH.equals(nextCommand)) {
                fontMetrics.setFixedPitch(readBoolean());
            } else if (START_CHAR_METRICS.equals(nextCommand)) {
                int count = readInt();
                List<CharMetric> charMetrics = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    CharMetric charMetric = parseCharMetric();
                    charMetrics.add(charMetric);
                }
                String end = readString();
                if (!end.equals(END_CHAR_METRICS)) {
                    throw new IOException("Error: Expected 'EndCharMetrics' actual '" + end + OperatorName.SHOW_TEXT_LINE);
                }
                charMetricsRead = true;
                fontMetrics.setCharMetrics(charMetrics);
            } else if (!reducedDataset && START_COMPOSITES.equals(nextCommand)) {
                int count2 = readInt();
                for (int i2 = 0; i2 < count2; i2++) {
                    Composite part = parseComposite();
                    fontMetrics.addComposite(part);
                }
                String end2 = readString();
                if (!end2.equals(END_COMPOSITES)) {
                    throw new IOException("Error: Expected 'EndComposites' actual '" + end2 + OperatorName.SHOW_TEXT_LINE);
                }
            } else if (!reducedDataset && START_KERN_DATA.equals(nextCommand)) {
                parseKernData(fontMetrics);
            } else {
                if (reducedDataset && charMetricsRead) {
                    break;
                }
                throw new IOException("Unknown AFM key '" + nextCommand + OperatorName.SHOW_TEXT_LINE);
            }
        }
        return fontMetrics;
    }

    private void parseKernData(FontMetrics fontMetrics) throws IOException {
        while (true) {
            String nextCommand = readString();
            if (!nextCommand.equals(END_KERN_DATA)) {
                if (START_TRACK_KERN.equals(nextCommand)) {
                    int count = readInt();
                    for (int i = 0; i < count; i++) {
                        TrackKern kern = new TrackKern();
                        kern.setDegree(readInt());
                        kern.setMinPointSize(readFloat());
                        kern.setMinKern(readFloat());
                        kern.setMaxPointSize(readFloat());
                        kern.setMaxKern(readFloat());
                        fontMetrics.addTrackKern(kern);
                    }
                    String end = readString();
                    if (!end.equals(END_TRACK_KERN)) {
                        throw new IOException("Error: Expected 'EndTrackKern' actual '" + end + OperatorName.SHOW_TEXT_LINE);
                    }
                } else if (START_KERN_PAIRS.equals(nextCommand)) {
                    int count2 = readInt();
                    for (int i2 = 0; i2 < count2; i2++) {
                        KernPair pair = parseKernPair();
                        fontMetrics.addKernPair(pair);
                    }
                    String end2 = readString();
                    if (!end2.equals(END_KERN_PAIRS)) {
                        throw new IOException("Error: Expected 'EndKernPairs' actual '" + end2 + OperatorName.SHOW_TEXT_LINE);
                    }
                } else if (START_KERN_PAIRS0.equals(nextCommand)) {
                    int count3 = readInt();
                    for (int i3 = 0; i3 < count3; i3++) {
                        KernPair pair2 = parseKernPair();
                        fontMetrics.addKernPair0(pair2);
                    }
                    String end3 = readString();
                    if (!end3.equals(END_KERN_PAIRS)) {
                        throw new IOException("Error: Expected 'EndKernPairs' actual '" + end3 + OperatorName.SHOW_TEXT_LINE);
                    }
                } else if (START_KERN_PAIRS1.equals(nextCommand)) {
                    int count4 = readInt();
                    for (int i4 = 0; i4 < count4; i4++) {
                        KernPair pair3 = parseKernPair();
                        fontMetrics.addKernPair1(pair3);
                    }
                    String end4 = readString();
                    if (!end4.equals(END_KERN_PAIRS)) {
                        throw new IOException("Error: Expected 'EndKernPairs' actual '" + end4 + OperatorName.SHOW_TEXT_LINE);
                    }
                } else {
                    throw new IOException("Unknown kerning data type '" + nextCommand + OperatorName.SHOW_TEXT_LINE);
                }
            } else {
                return;
            }
        }
    }

    private KernPair parseKernPair() throws IOException {
        KernPair kernPair = new KernPair();
        String cmd = readString();
        if (KERN_PAIR_KP.equals(cmd)) {
            kernPair.setFirstKernCharacter(readString());
            kernPair.setSecondKernCharacter(readString());
            kernPair.setX(readFloat());
            kernPair.setY(readFloat());
        } else if (KERN_PAIR_KPH.equals(cmd)) {
            kernPair.setFirstKernCharacter(hexToString(readString()));
            kernPair.setSecondKernCharacter(hexToString(readString()));
            kernPair.setX(readFloat());
            kernPair.setY(readFloat());
        } else if (KERN_PAIR_KPX.equals(cmd)) {
            kernPair.setFirstKernCharacter(readString());
            kernPair.setSecondKernCharacter(readString());
            kernPair.setX(readFloat());
            kernPair.setY(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        } else if (KERN_PAIR_KPY.equals(cmd)) {
            kernPair.setFirstKernCharacter(readString());
            kernPair.setSecondKernCharacter(readString());
            kernPair.setX(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            kernPair.setY(readFloat());
        } else {
            throw new IOException("Error expected kern pair command actual='" + cmd + OperatorName.SHOW_TEXT_LINE);
        }
        return kernPair;
    }

    private String hexToString(String hexString) throws IOException {
        if (hexString.length() < 2) {
            throw new IOException("Error: Expected hex string of length >= 2 not='" + hexString);
        }
        if (hexString.charAt(0) != '<' || hexString.charAt(hexString.length() - 1) != '>') {
            throw new IOException("String should be enclosed by angle brackets '" + hexString + OperatorName.SHOW_TEXT_LINE);
        }
        String hexString2 = hexString.substring(1, hexString.length() - 1);
        byte[] data = new byte[hexString2.length() / 2];
        for (int i = 0; i < hexString2.length(); i += 2) {
            String hex = Character.toString(hexString2.charAt(i)) + hexString2.charAt(i + 1);
            try {
                data[i / 2] = (byte) Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                throw new IOException("Error parsing AFM file:" + e);
            }
        }
        return new String(data, Charsets.ISO_8859_1);
    }

    private Composite parseComposite() throws IOException {
        Composite composite = new Composite();
        String partData = readLine();
        StringTokenizer tokenizer = new StringTokenizer(partData, " ;");
        String cc = tokenizer.nextToken();
        if (!cc.equals(CC)) {
            throw new IOException("Expected 'CC' actual='" + cc + OperatorName.SHOW_TEXT_LINE);
        }
        String name = tokenizer.nextToken();
        composite.setName(name);
        try {
            int partCount = Integer.parseInt(tokenizer.nextToken());
            for (int i = 0; i < partCount; i++) {
                CompositePart part = new CompositePart();
                String pcc = tokenizer.nextToken();
                if (!pcc.equals(PCC)) {
                    throw new IOException("Expected 'PCC' actual='" + pcc + OperatorName.SHOW_TEXT_LINE);
                }
                String partName = tokenizer.nextToken();
                try {
                    int x = Integer.parseInt(tokenizer.nextToken());
                    int y = Integer.parseInt(tokenizer.nextToken());
                    part.setName(partName);
                    part.setXDisplacement(x);
                    part.setYDisplacement(y);
                    composite.addPart(part);
                } catch (NumberFormatException e) {
                    throw new IOException("Error parsing AFM document:" + e);
                }
            }
            return composite;
        } catch (NumberFormatException e2) {
            throw new IOException("Error parsing AFM document:" + e2);
        }
    }

    private CharMetric parseCharMetric() throws IOException {
        CharMetric charMetric = new CharMetric();
        String metrics = readLine();
        StringTokenizer metricsTokenizer = new StringTokenizer(metrics);
        while (metricsTokenizer.hasMoreTokens()) {
            try {
                String nextCommand = metricsTokenizer.nextToken();
                if (nextCommand.equals("C")) {
                    String charCode = metricsTokenizer.nextToken();
                    charMetric.setCharacterCode(Integer.parseInt(charCode));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_CH)) {
                    String charCode2 = metricsTokenizer.nextToken();
                    charMetric.setCharacterCode(Integer.parseInt(charCode2, 16));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_WX)) {
                    charMetric.setWx(Float.parseFloat(metricsTokenizer.nextToken()));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_W0X)) {
                    charMetric.setW0x(Float.parseFloat(metricsTokenizer.nextToken()));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_W1X)) {
                    charMetric.setW1x(Float.parseFloat(metricsTokenizer.nextToken()));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_WY)) {
                    charMetric.setWy(Float.parseFloat(metricsTokenizer.nextToken()));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_W0Y)) {
                    charMetric.setW0y(Float.parseFloat(metricsTokenizer.nextToken()));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_W1Y)) {
                    charMetric.setW1y(Float.parseFloat(metricsTokenizer.nextToken()));
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals("W")) {
                    float[] w = {Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setW(w);
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_W0)) {
                    float[] w0 = {Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setW0(w0);
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_W1)) {
                    float[] w1 = {Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setW1(w1);
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals(CHARMETRICS_VV)) {
                    float[] vv = {Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setVv(vv);
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals("N")) {
                    charMetric.setName(metricsTokenizer.nextToken());
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals("B")) {
                    BoundingBox box = new BoundingBox();
                    box.setLowerLeftX(Float.parseFloat(metricsTokenizer.nextToken()));
                    box.setLowerLeftY(Float.parseFloat(metricsTokenizer.nextToken()));
                    box.setUpperRightX(Float.parseFloat(metricsTokenizer.nextToken()));
                    box.setUpperRightY(Float.parseFloat(metricsTokenizer.nextToken()));
                    charMetric.setBoundingBox(box);
                    verifySemicolon(metricsTokenizer);
                } else if (nextCommand.equals("L")) {
                    Ligature lig = new Ligature();
                    lig.setSuccessor(metricsTokenizer.nextToken());
                    lig.setLigature(metricsTokenizer.nextToken());
                    charMetric.addLigature(lig);
                    verifySemicolon(metricsTokenizer);
                } else {
                    throw new IOException("Unknown CharMetrics command '" + nextCommand + OperatorName.SHOW_TEXT_LINE);
                }
            } catch (NumberFormatException e) {
                throw new IOException("Error: Corrupt AFM document:" + e);
            }
        }
        return charMetric;
    }

    private void verifySemicolon(StringTokenizer tokenizer) throws IOException {
        if (tokenizer.hasMoreTokens()) {
            String semicolon = tokenizer.nextToken();
            if (!PolicyIdFilter.OID_SEPARATOR.equals(semicolon)) {
                throw new IOException("Error: Expected semicolon in stream actual='" + semicolon + OperatorName.SHOW_TEXT_LINE);
            }
            return;
        }
        throw new IOException("CharMetrics is missing a semicolon after a command");
    }

    private boolean readBoolean() throws IOException {
        String theBoolean = readString();
        return Boolean.parseBoolean(theBoolean);
    }

    private int readInt() throws IOException {
        String theInt = readString();
        try {
            return Integer.parseInt(theInt);
        } catch (NumberFormatException e) {
            throw new IOException("Error parsing AFM document:" + e);
        }
    }

    private float readFloat() throws IOException {
        String theFloat = readString();
        return Float.parseFloat(theFloat);
    }

    private String readLine() throws IOException {
        int nextByte;
        StringBuilder buf = new StringBuilder(60);
        int i = this.input.read();
        while (true) {
            nextByte = i;
            if (!isWhitespace(nextByte)) {
                break;
            }
            i = this.input.read();
        }
        buf.append((char) nextByte);
        int i2 = this.input.read();
        while (true) {
            int nextByte2 = i2;
            if (nextByte2 == -1 || isEOL(nextByte2)) {
                break;
            }
            buf.append((char) nextByte2);
            i2 = this.input.read();
        }
        return buf.toString();
    }

    private String readString() throws IOException {
        int nextByte;
        StringBuilder buf = new StringBuilder(24);
        int i = this.input.read();
        while (true) {
            nextByte = i;
            if (!isWhitespace(nextByte)) {
                break;
            }
            i = this.input.read();
        }
        buf.append((char) nextByte);
        int i2 = this.input.read();
        while (true) {
            int nextByte2 = i2;
            if (nextByte2 == -1 || isWhitespace(nextByte2)) {
                break;
            }
            buf.append((char) nextByte2);
            i2 = this.input.read();
        }
        return buf.toString();
    }

    private boolean isEOL(int character) {
        return character == 13 || character == 10;
    }

    private boolean isWhitespace(int character) {
        return character == 32 || character == 9 || character == 13 || character == 10;
    }
}
