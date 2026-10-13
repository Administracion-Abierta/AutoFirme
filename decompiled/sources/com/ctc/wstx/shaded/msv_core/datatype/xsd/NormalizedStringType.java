package com.ctc.wstx.shaded.msv_core.datatype.xsd;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NormalizedStringType.class */
public class NormalizedStringType extends StringType {
    public static final NormalizedStringType theInstance = new NormalizedStringType("normalizedString", true);
    private static final long serialVersionUID = 1;

    protected NormalizedStringType(String typeName, boolean isAlwaysValid) {
        super(typeName, WhiteSpaceProcessor.theReplace, isAlwaysValid);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return StringType.theInstance;
    }
}
