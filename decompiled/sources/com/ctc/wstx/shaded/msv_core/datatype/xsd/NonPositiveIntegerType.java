package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NonPositiveIntegerType.class */
public class NonPositiveIntegerType extends IntegerType {
    public static final NonPositiveIntegerType theInstance = new NonPositiveIntegerType();
    private static final long serialVersionUID = 1;

    private NonPositiveIntegerType() {
        super("nonPositiveInteger", createRangeFacet(IntegerType.theInstance, null, IntegerValueType.create("0")));
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
        if (v.isNonPositive()) {
            return v;
        }
        return null;
    }
}
