package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DoubleType.class */
public class DoubleType extends FloatingNumberType {
    public static final DoubleType theInstance = new DoubleType();
    private static final long serialVersionUID = 1;

    private DoubleType() {
        super("double");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return load(lexicalValue);
    }

    public static Double load(String lexicalValue) {
        try {
            if (lexicalValue.equals("NaN")) {
                return new Double(Double.NaN);
            }
            if (lexicalValue.equals("INF")) {
                return new Double(Double.POSITIVE_INFINITY);
            }
            if (lexicalValue.equals("-INF")) {
                return new Double(Double.NEGATIVE_INFINITY);
            }
            if (lexicalValue.length() == 0 || !isDigitOrPeriodOrSign(lexicalValue.charAt(0)) || !isDigitOrPeriodOrSign(lexicalValue.charAt(lexicalValue.length() - 1))) {
                return null;
            }
            return Double.valueOf(lexicalValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof Double)) {
            throw new IllegalArgumentException();
        }
        return save((Double) value);
    }

    public static String save(Double value) {
        double v = value.doubleValue();
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        return v == Double.NEGATIVE_INFINITY ? "-INF" : value.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Double.class;
    }
}
