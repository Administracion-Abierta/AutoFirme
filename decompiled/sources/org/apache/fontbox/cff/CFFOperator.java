package org.apache.fontbox.cff;

import com.ctc.wstx.cfg.XmlConsts;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.fontbox.afm.AFMParser;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFOperator.class */
public final class CFFOperator {
    private Key operatorKey = null;
    private String operatorName = null;
    private static Map<Key, CFFOperator> keyMap = new LinkedHashMap(52);
    private static Map<String, CFFOperator> nameMap = new LinkedHashMap(52);

    private CFFOperator(Key key, String name) {
        setKey(key);
        setName(name);
    }

    public Key getKey() {
        return this.operatorKey;
    }

    private void setKey(Key key) {
        this.operatorKey = key;
    }

    public String getName() {
        return this.operatorName;
    }

    private void setName(String name) {
        this.operatorName = name;
    }

    public String toString() {
        return getName();
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof CFFOperator) {
            CFFOperator that = (CFFOperator) object;
            return getKey().equals(that.getKey());
        }
        return false;
    }

    private static void register(Key key, String name) {
        CFFOperator operator = new CFFOperator(key, name);
        keyMap.put(key, operator);
        nameMap.put(name, operator);
    }

    public static CFFOperator getOperator(Key key) {
        return keyMap.get(key);
    }

    public static CFFOperator getOperator(String name) {
        return nameMap.get(name);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFOperator$Key.class */
    public static class Key {
        private int[] value;

        public Key(int b0) {
            this(new int[]{b0});
        }

        public Key(int b0, int b1) {
            this(new int[]{b0, b1});
        }

        private Key(int[] value) {
            this.value = null;
            setValue(value);
        }

        public int[] getValue() {
            return this.value;
        }

        private void setValue(int[] value) {
            this.value = value;
        }

        public String toString() {
            return Arrays.toString(getValue());
        }

        public int hashCode() {
            return Arrays.hashCode(getValue());
        }

        public boolean equals(Object object) {
            if (object instanceof Key) {
                Key that = (Key) object;
                return Arrays.equals(getValue(), that.getValue());
            }
            return false;
        }
    }

    static {
        register(new Key(0), XmlConsts.XML_DECL_KW_VERSION);
        register(new Key(1), AFMParser.NOTICE);
        register(new Key(12, 0), "Copyright");
        register(new Key(2), AFMParser.FULL_NAME);
        register(new Key(3), AFMParser.FAMILY_NAME);
        register(new Key(4), AFMParser.WEIGHT);
        register(new Key(12, 1), "isFixedPitch");
        register(new Key(12, 2), AFMParser.ITALIC_ANGLE);
        register(new Key(12, 3), AFMParser.UNDERLINE_POSITION);
        register(new Key(12, 4), AFMParser.UNDERLINE_THICKNESS);
        register(new Key(12, 5), "PaintType");
        register(new Key(12, 6), "CharstringType");
        register(new Key(12, 7), "FontMatrix");
        register(new Key(13), "UniqueID");
        register(new Key(5), AFMParser.FONT_BBOX);
        register(new Key(12, 8), "StrokeWidth");
        register(new Key(14), "XUID");
        register(new Key(15), "charset");
        register(new Key(16), "Encoding");
        register(new Key(17), "CharStrings");
        register(new Key(18), StandardStructureTypes.PRIVATE);
        register(new Key(12, 20), "SyntheticBase");
        register(new Key(12, 21), "PostScript");
        register(new Key(12, 22), "BaseFontName");
        register(new Key(12, 23), "BaseFontBlend");
        register(new Key(12, 30), "ROS");
        register(new Key(12, 31), "CIDFontVersion");
        register(new Key(12, 32), "CIDFontRevision");
        register(new Key(12, 33), "CIDFontType");
        register(new Key(12, 34), "CIDCount");
        register(new Key(12, 35), "UIDBase");
        register(new Key(12, 36), "FDArray");
        register(new Key(12, 37), "FDSelect");
        register(new Key(12, 38), AFMParser.FONT_NAME);
        register(new Key(6), "BlueValues");
        register(new Key(7), "OtherBlues");
        register(new Key(8), "FamilyBlues");
        register(new Key(9), "FamilyOtherBlues");
        register(new Key(12, 9), "BlueScale");
        register(new Key(12, 10), "BlueShift");
        register(new Key(12, 11), "BlueFuzz");
        register(new Key(10), AFMParser.STD_HW);
        register(new Key(11), AFMParser.STD_VW);
        register(new Key(12, 12), "StemSnapH");
        register(new Key(12, 13), "StemSnapV");
        register(new Key(12, 14), "ForceBold");
        register(new Key(12, 15), "LanguageGroup");
        register(new Key(12, 16), "ExpansionFactor");
        register(new Key(12, 17), "initialRandomSeed");
        register(new Key(19), "Subrs");
        register(new Key(20), "defaultWidthX");
        register(new Key(21), "nominalWidthX");
    }
}
