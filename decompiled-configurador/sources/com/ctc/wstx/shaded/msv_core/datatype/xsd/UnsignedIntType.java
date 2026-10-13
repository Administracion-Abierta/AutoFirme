package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/UnsignedIntType.class */
public class UnsignedIntType extends LongType {
    public static final UnsignedIntType theInstance = new UnsignedIntType();
    private static final long upperBound = 4294967295L;
    private static final long serialVersionUID = 1;

    private UnsignedIntType() {
        super("unsignedInt", createRangeFacet(UnsignedLongType.theInstance, null, new Long(4294967295L)));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.LongType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return UnsignedLongType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.LongType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        try {
            Long v = (Long) super._createValue(lexicalValue, context);
            if (v != null && v.longValue() >= 0 && v.longValue() <= 4294967295L) {
                return v;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
