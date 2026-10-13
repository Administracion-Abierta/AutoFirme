package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/InternalImpl.class */
public final class InternalImpl extends RegExpFactory {
    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory
    public RegExp compile(String exp) throws java.text.ParseException {
        try {
            final RegularExpression re = new RegularExpression(exp, "X");
            return new RegExp() { // from class: com.ctc.wstx.shaded.msv_core.datatype.regexp.InternalImpl.1
                @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp
                public boolean matches(String text) {
                    return re.matches(text);
                }
            };
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getLocation());
        }
    }
}
