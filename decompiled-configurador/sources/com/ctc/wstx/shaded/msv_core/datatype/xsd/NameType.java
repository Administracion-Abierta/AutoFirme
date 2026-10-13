package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NameType.class */
public class NameType extends TokenType {
    public static final NameType theInstance = new NameType();
    private static final long serialVersionUID = 1;

    private NameType() {
        super("Name", false);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType, com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return TokenType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        if (XmlNames.isName(content)) {
            return content;
        }
        return null;
    }
}
