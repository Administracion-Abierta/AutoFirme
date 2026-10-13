package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/UnsignedLongType.class */
public class UnsignedLongType extends IntegerType {
    public static final UnsignedLongType theInstance = new UnsignedLongType();
    private static final IntegerValueType upperBound = IntegerValueType.create("18446744073709551615");
    private static final long serialVersionUID = 1;

    private UnsignedLongType() {
        super("unsignedLong", createRangeFacet(NonNegativeIntegerType.theInstance, null, IntegerValueType.create("18446744073709551615")));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return NonNegativeIntegerType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        IntegerValueType v = IntegerValueType.create(lexicalValue);
        if (v != null && v.isNonNegative() && upperBound.compareTo(v) >= 0) {
            return v;
        }
        return null;
    }
}
