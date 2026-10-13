package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/UnsignedByteType.class */
public class UnsignedByteType extends ShortType {
    public static final UnsignedByteType theInstance = new UnsignedByteType();
    private static final short upperBound = 255;
    private static final long serialVersionUID = 1;

    private UnsignedByteType() {
        super("unsignedByte", createRangeFacet(UnsignedShortType.theInstance, null, new Short((short) 255)));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ShortType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return UnsignedShortType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ShortType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        try {
            Short v = (Short) super._createValue(lexicalValue, context);
            if (v != null && v.shortValue() >= 0 && v.shortValue() <= 255) {
                return v;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
