package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NegativeIntegerType.class */
public class NegativeIntegerType extends IntegerType {
    public static final NegativeIntegerType theInstance = new NegativeIntegerType();
    private static final long serialVersionUID = 1;

    private NegativeIntegerType() {
        super("negativeInteger", createRangeFacet(NonPositiveIntegerType.theInstance, null, IntegerValueType.create("-1")));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return NonPositiveIntegerType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        Object o = super._createValue(lexicalValue, context);
        if (o == null) {
            return null;
        }
        IntegerValueType v = (IntegerValueType) o;
        if (v.isNegative()) {
            return v;
        }
        return null;
    }
}
