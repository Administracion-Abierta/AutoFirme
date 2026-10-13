package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NmtokenType.class */
public class NmtokenType extends TokenType {
    public static final NmtokenType theInstance = new NmtokenType(DTDParser.TYPE_NMTOKEN);
    private static final long serialVersionUID = 1;

    protected NmtokenType(String typeName) {
        super(typeName, false);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType, com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return TokenType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        if (XmlNames.isNmtoken(content)) {
            return content;
        }
        return null;
    }
}
