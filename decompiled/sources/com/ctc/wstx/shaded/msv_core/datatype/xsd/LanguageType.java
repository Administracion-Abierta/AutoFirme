package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/LanguageType.class */
public class LanguageType extends TokenType {
    public static final LanguageType theInstance = new LanguageType();
    private static final long serialVersionUID = 1;

    private LanguageType() {
        super("language", false);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType, com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return TokenType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        int len = content.length();
        int i = 0;
        int tokenSize = 0;
        while (i < len) {
            int i2 = i;
            i++;
            char ch = content.charAt(i2);
            if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')) {
                tokenSize++;
                if (tokenSize == 9) {
                    return null;
                }
            } else {
                if (ch != '-' || tokenSize == 0) {
                    return null;
                }
                tokenSize = 0;
            }
        }
        if (tokenSize == 0) {
            return null;
        }
        return content.toLowerCase();
    }
}
