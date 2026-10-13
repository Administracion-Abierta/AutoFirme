package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/Base64BinaryType.class */
public class Base64BinaryType extends BinaryBaseType {
    private static final byte PADDING = 127;
    private static final long serialVersionUID = 1;
    public static final Base64BinaryType theInstance = new Base64BinaryType();
    private static final byte[] decodeMap = initDecodeMap();
    private static final char[] encodeMap = initEncodeMap();

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public /* bridge */ /* synthetic */ Class getJavaObjectType() {
        return super.getJavaObjectType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType, com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public /* bridge */ /* synthetic */ Object _createJavaObject(String x0, ValidationContext x1) {
        return super._createJavaObject(x0, x1);
    }

    private Base64BinaryType() {
        super("base64Binary");
    }

    private static byte[] initDecodeMap() {
        byte[] map = new byte[256];
        for (int i = 0; i < 256; i++) {
            map[i] = -1;
        }
        for (int i2 = 65; i2 <= 90; i2++) {
            map[i2] = (byte) (i2 - 65);
        }
        for (int i3 = 97; i3 <= 122; i3++) {
            map[i3] = (byte) ((i3 - 97) + 26);
        }
        for (int i4 = 48; i4 <= 57; i4++) {
            map[i4] = (byte) ((i4 - 48) + 52);
        }
        map[43] = 62;
        map[47] = 63;
        map[61] = 127;
        return map;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        byte[] buf = load(lexicalValue);
        if (buf == null) {
            return null;
        }
        return new BinaryValueType(buf);
    }

    public static byte[] load(String lexicalValue) {
        char[] buf = lexicalValue.toCharArray();
        int outlen = calcLength(buf);
        if (outlen == -1) {
            return null;
        }
        byte[] out = new byte[outlen];
        int o = 0;
        byte[] quadruplet = new byte[4];
        int q = 0;
        for (char c : buf) {
            byte v = decodeMap[c];
            if (v != -1) {
                int i = q;
                q++;
                quadruplet[i] = v;
            }
            if (q == 4) {
                int i2 = o;
                o++;
                out[i2] = (byte) ((quadruplet[0] << 2) | (quadruplet[1] >> 4));
                if (quadruplet[2] != 127) {
                    o++;
                    out[o] = (byte) ((quadruplet[1] << 4) | (quadruplet[2] >> 2));
                }
                if (quadruplet[3] != 127) {
                    int i3 = o;
                    o++;
                    out[i3] = (byte) ((quadruplet[2] << 6) | quadruplet[3]);
                }
                q = 0;
            }
        }
        if (q != 0) {
            throw new IllegalStateException();
        }
        return out;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String lexicalValue, ValidationContext context) {
        return calcLength(lexicalValue.toCharArray()) != -1;
    }

    private static int calcLength(char[] buf) {
        int len = buf.length;
        int base64count = 0;
        int paddingCount = 0;
        int i = 0;
        while (i < len && buf[i] != '=') {
            if (buf[i] >= 256) {
                return -1;
            }
            if (decodeMap[buf[i]] != -1) {
                base64count++;
            }
            i++;
        }
        while (i < len) {
            if (buf[i] == '=') {
                paddingCount++;
            } else if (buf[i] >= 256 || decodeMap[buf[i]] != -1) {
                return -1;
            }
            i++;
        }
        if (paddingCount <= 2 && (base64count + paddingCount) % 4 == 0) {
            return (((base64count + paddingCount) / 4) * 3) - paddingCount;
        }
        return -1;
    }

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        for (int i = 0; i < 26; i++) {
            map[i] = (char) (65 + i);
        }
        for (int i2 = 26; i2 < 52; i2++) {
            map[i2] = (char) (97 + (i2 - 26));
        }
        for (int i3 = 52; i3 < 62; i3++) {
            map[i3] = (char) (48 + (i3 - 52));
        }
        map[62] = '+';
        map[63] = '/';
        return map;
    }

    protected static char encode(int i) {
        return encodeMap[i & 63];
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType, com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof byte[])) {
            throw new IllegalArgumentException();
        }
        return save((byte[]) value);
    }

    public static String save(byte[] input) {
        StringBuffer r = new StringBuffer((input.length * 4) / 3);
        for (int i = 0; i < input.length; i += 3) {
            switch (input.length - i) {
                case 1:
                    r.append(encode(input[i] >> 2));
                    r.append(encode((input[i] & 3) << 4));
                    r.append("==");
                    break;
                case 2:
                    r.append(encode(input[i] >> 2));
                    r.append(encode(((input[i] & 3) << 4) | ((input[i + 1] >> 4) & 15)));
                    r.append(encode((input[i + 1] & 15) << 2));
                    r.append("=");
                    break;
                default:
                    r.append(encode(input[i] >> 2));
                    r.append(encode(((input[i] & 3) << 4) | ((input[i + 1] >> 4) & 15)));
                    r.append(encode(((input[i + 1] & 15) << 2) | ((input[i + 2] >> 6) & 3)));
                    r.append(encode(input[i + 2] & 63));
                    break;
            }
        }
        return r.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof BinaryValueType)) {
            throw new IllegalArgumentException();
        }
        return serializeJavaObject(((BinaryValueType) value).rawData, context);
    }
}
