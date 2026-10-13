package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NonNegativeIntegerType.class */
public class NonNegativeIntegerType extends IntegerType {
    public static final NonNegativeIntegerType theInstance = new NonNegativeIntegerType();
    private static final long serialVersionUID = 1;

    private NonNegativeIntegerType() {
        super("nonNegativeInteger", createRangeFacet(IntegerType.theInstance, IntegerValueType.create("0"), null));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        Object o = super._createValue(lexicalValue, context);
        if (o == null) {
            return null;
        }
        IntegerValueType v = (IntegerValueType) o;
        if (v.isNonNegative()) {
            return v;
        }
        return null;
    }
}
