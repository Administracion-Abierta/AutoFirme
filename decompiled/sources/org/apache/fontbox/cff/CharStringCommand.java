package org.apache.fontbox.cff;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CharStringCommand.class */
public class CharStringCommand {
    private Key commandKey = null;
    public static final Map<Key, String> TYPE1_VOCABULARY;
    public static final Map<Key, String> TYPE2_VOCABULARY;

    public CharStringCommand(int b0) {
        setKey(new Key(b0));
    }

    public CharStringCommand(int b0, int b1) {
        setKey(new Key(b0, b1));
    }

    public CharStringCommand(int[] values) {
        setKey(new Key(values));
    }

    public Key getKey() {
        return this.commandKey;
    }

    private void setKey(Key key) {
        this.commandKey = key;
    }

    public String toString() {
        String str = TYPE2_VOCABULARY.get(getKey());
        if (str == null) {
            str = TYPE1_VOCABULARY.get(getKey());
        }
        if (str == null) {
            return getKey().toString() + '|';
        }
        return str + '|';
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof CharStringCommand) {
            CharStringCommand that = (CharStringCommand) object;
            return getKey().equals(that.getKey());
        }
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CharStringCommand$Key.class */
    public static class Key {
        private int[] keyValues = null;

        public Key(int b0) {
            setValue(new int[]{b0});
        }

        public Key(int b0, int b1) {
            setValue(new int[]{b0, b1});
        }

        public Key(int[] values) {
            setValue(values);
        }

        public int[] getValue() {
            return this.keyValues;
        }

        private void setValue(int[] value) {
            this.keyValues = value;
        }

        public String toString() {
            return Arrays.toString(getValue());
        }

        public int hashCode() {
            if (this.keyValues[0] == 12 && this.keyValues.length > 1) {
                return this.keyValues[0] ^ this.keyValues[1];
            }
            return this.keyValues[0];
        }

        public boolean equals(Object object) {
            if (object instanceof Key) {
                Key that = (Key) object;
                if (this.keyValues[0] != 12 || that.keyValues[0] != 12) {
                    return this.keyValues[0] == that.keyValues[0];
                }
                if (this.keyValues.length <= 1 || that.keyValues.length <= 1) {
                    return this.keyValues.length == that.keyValues.length;
                }
                return this.keyValues[1] == that.keyValues[1];
            }
            return false;
        }
    }

    static {
        Map<Key, String> map = new LinkedHashMap<>(26);
        map.put(new Key(1), "hstem");
        map.put(new Key(3), "vstem");
        map.put(new Key(4), "vmoveto");
        map.put(new Key(5), "rlineto");
        map.put(new Key(6), "hlineto");
        map.put(new Key(7), "vlineto");
        map.put(new Key(8), "rrcurveto");
        map.put(new Key(9), "closepath");
        map.put(new Key(10), "callsubr");
        map.put(new Key(11), "return");
        map.put(new Key(12), Config.PROP_ESCAPE);
        map.put(new Key(12, 0), "dotsection");
        map.put(new Key(12, 1), "vstem3");
        map.put(new Key(12, 2), "hstem3");
        map.put(new Key(12, 6), "seac");
        map.put(new Key(12, 7), "sbw");
        map.put(new Key(12, 12), "div");
        map.put(new Key(12, 16), "callothersubr");
        map.put(new Key(12, 17), "pop");
        map.put(new Key(12, 33), "setcurrentpoint");
        map.put(new Key(13), "hsbw");
        map.put(new Key(14), "endchar");
        map.put(new Key(21), "rmoveto");
        map.put(new Key(22), "hmoveto");
        map.put(new Key(30), "vhcurveto");
        map.put(new Key(31), "hvcurveto");
        TYPE1_VOCABULARY = Collections.unmodifiableMap(map);
        Map<Key, String> map2 = new LinkedHashMap<>(48);
        map2.put(new Key(1), "hstem");
        map2.put(new Key(3), "vstem");
        map2.put(new Key(4), "vmoveto");
        map2.put(new Key(5), "rlineto");
        map2.put(new Key(6), "hlineto");
        map2.put(new Key(7), "vlineto");
        map2.put(new Key(8), "rrcurveto");
        map2.put(new Key(10), "callsubr");
        map2.put(new Key(11), "return");
        map2.put(new Key(12), Config.PROP_ESCAPE);
        map2.put(new Key(12, 3), "and");
        map2.put(new Key(12, 4), "or");
        map2.put(new Key(12, 5), "not");
        map2.put(new Key(12, 9), "abs");
        map2.put(new Key(12, 10), "add");
        map2.put(new Key(12, 11), "sub");
        map2.put(new Key(12, 12), "div");
        map2.put(new Key(12, 14), "neg");
        map2.put(new Key(12, 15), "eq");
        map2.put(new Key(12, 18), "drop");
        map2.put(new Key(12, 20), "put");
        map2.put(new Key(12, 21), "get");
        map2.put(new Key(12, 22), "ifelse");
        map2.put(new Key(12, 23), "random");
        map2.put(new Key(12, 24), "mul");
        map2.put(new Key(12, 26), "sqrt");
        map2.put(new Key(12, 27), "dup");
        map2.put(new Key(12, 28), "exch");
        map2.put(new Key(12, 29), "index");
        map2.put(new Key(12, 30), "roll");
        map2.put(new Key(12, 34), "hflex");
        map2.put(new Key(12, 35), "flex");
        map2.put(new Key(12, 36), "hflex1");
        map2.put(new Key(12, 37), "flex1");
        map2.put(new Key(14), "endchar");
        map2.put(new Key(18), "hstemhm");
        map2.put(new Key(19), "hintmask");
        map2.put(new Key(20), "cntrmask");
        map2.put(new Key(21), "rmoveto");
        map2.put(new Key(22), "hmoveto");
        map2.put(new Key(23), "vstemhm");
        map2.put(new Key(24), "rcurveline");
        map2.put(new Key(25), "rlinecurve");
        map2.put(new Key(26), "vvcurveto");
        map2.put(new Key(27), "hhcurveto");
        map2.put(new Key(28), "shortint");
        map2.put(new Key(29), "callgsubr");
        map2.put(new Key(30), "vhcurveto");
        map2.put(new Key(31), "hvcurveto");
        TYPE2_VOCABULARY = Collections.unmodifiableMap(map2);
    }
}
