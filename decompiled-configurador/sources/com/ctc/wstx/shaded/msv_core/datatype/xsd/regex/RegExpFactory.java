package com.ctc.wstx.shaded.msv_core.datatype.xsd.regex;

import java.text.ParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/regex/RegExpFactory.class */
public abstract class RegExpFactory {
    public abstract RegExp compile(String str) throws ParseException;

    public static RegExpFactory createFactory() {
        String[] classList = {"com.ctc.wstx.shaded.msv_core.datatype.regexp.InternalImpl", "com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.XercesImpl", "com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.JDKImpl"};
        for (String name : classList) {
            try {
                return (RegExpFactory) RegExpFactory.class.getClassLoader().loadClass(name).newInstance();
            } catch (Throwable th) {
            }
        }
        throw new Error("no implementation of regexp was found.");
    }
}
