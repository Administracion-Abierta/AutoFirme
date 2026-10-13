package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NcnameType.class */
public class NcnameType extends TokenType {
    public static final NcnameType theInstance = new NcnameType("NCName");
    private static final long serialVersionUID = 1;

    protected NcnameType(String typeName) {
        super(typeName, false);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType, com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return NameType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        if (XmlNames.isNCName(content)) {
            return content;
        }
        return null;
    }
}
