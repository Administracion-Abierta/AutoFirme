package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/UnsignedShortType.class */
public class UnsignedShortType extends IntType {
    public static final UnsignedShortType theInstance = new UnsignedShortType();
    private static final int upperBound = 65535;
    private static final long serialVersionUID = 1;

    private UnsignedShortType() {
        super("unsignedShort", createRangeFacet(UnsignedIntType.theInstance, null, new Integer(65535)));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return UnsignedIntType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        try {
            Integer v = (Integer) super._createValue(lexicalValue, context);
            if (v != null && v.intValue() >= 0 && v.intValue() <= 65535) {
                return v;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
