package com.ctc.wstx.shaded.msv_core.datatype.xsd;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/TokenType.class */
public class TokenType extends StringType {
    public static final TokenType theInstance = new TokenType("token", true);
    private static final long serialVersionUID = 1;

    protected TokenType(String typeName, boolean isAlwaysValid) {
        super(typeName, WhiteSpaceProcessor.theCollapse, isAlwaysValid);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return NormalizedStringType.theInstance;
    }
}
