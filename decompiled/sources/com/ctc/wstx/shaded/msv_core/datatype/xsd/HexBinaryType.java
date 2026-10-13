package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/HexBinaryType.class */
public class HexBinaryType extends BinaryBaseType {
    public static final HexBinaryType theInstance = new HexBinaryType();
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public /* bridge */ /* synthetic */ Class getJavaObjectType() {
        return super.getJavaObjectType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType, com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public /* bridge */ /* synthetic */ Object _createJavaObject(String x0, ValidationContext x1) {
        return super._createJavaObject(x0, x1);
    }

    private HexBinaryType() {
        super("hexBinary");
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return (ch - 'A') + 10;
        }
        if ('a' > ch || ch > 'f') {
            return -1;
        }
        return (ch - 'a') + 10;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        byte[] buf = load(lexicalValue);
        if (buf == null) {
            return null;
        }
        return new BinaryValueType(buf);
    }

    public static byte[] load(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            return null;
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                return null;
            }
            out[i / 2] = (byte) ((h * 16) + l);
        }
        return out;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String lexicalValue, ValidationContext context) {
        int len = lexicalValue.length();
        if (len % 2 != 0) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (hexToBin(lexicalValue.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType, com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof byte[])) {
            throw new IllegalArgumentException();
        }
        return save((byte[]) value);
    }

    public static String save(byte[] data) {
        StringBuffer r = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            r.append(encode(data[i] >> 4));
            r.append(encode(data[i] & 15));
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

    public static char encode(int ch) {
        int ch2 = ch & 15;
        return ch2 < 10 ? (char) (48 + ch2) : (char) (65 + (ch2 - 10));
    }
}
