package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/FloatType.class */
public class FloatType extends FloatingNumberType {
    public static final FloatType theInstance = new FloatType();
    private static final long serialVersionUID = 1;

    private FloatType() {
        super("float");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return load(lexicalValue);
    }

    public static Float load(String s) {
        try {
            if (s.equals("NaN")) {
                return new Float(Float.NaN);
            }
            if (s.equals("INF")) {
                return new Float(Float.POSITIVE_INFINITY);
            }
            if (s.equals("-INF")) {
                return new Float(Float.NEGATIVE_INFINITY);
            }
            if (s.length() == 0 || !isDigitOrPeriodOrSign(s.charAt(0)) || !isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
                return null;
            }
            return Float.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Float.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof Float)) {
            throw new IllegalArgumentException();
        }
        return save((Float) value);
    }

    public static String save(Float value) {
        float v = value.floatValue();
        if (Float.isNaN(v)) {
            return "NaN";
        }
        if (v == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        return v == Float.NEGATIVE_INFINITY ? "-INF" : value.toString();
    }
}
