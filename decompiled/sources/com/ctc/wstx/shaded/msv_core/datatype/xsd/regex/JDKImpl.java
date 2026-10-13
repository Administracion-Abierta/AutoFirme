package com.ctc.wstx.shaded.msv_core.datatype.xsd.regex;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/regex/JDKImpl.class */
final class JDKImpl extends RegExpFactory {
    private final Class regexp = getClass().getClassLoader().loadClass("com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression");
    private final Constructor ctor = this.regexp.getConstructor(String.class, String.class);
    private final Method matches = this.regexp.getMethod("matches", String.class);

    JDKImpl() throws Exception {
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory
    public RegExp compile(String exp) throws ParseException {
        try {
            final Object re = this.ctor.newInstance(exp, "X");
            return new RegExp() { // from class: com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.JDKImpl.1
                @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp
                public boolean matches(String text) {
                    try {
                        return ((Boolean) JDKImpl.this.matches.invoke(re, text)).booleanValue();
                    } catch (IllegalAccessException e) {
                        throw new IllegalAccessError(e.getMessage());
                    } catch (InvocationTargetException e2) {
                        throw new Error(e2);
                    }
                }
            };
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (InstantiationException e2) {
            throw new InstantiationError(e2.getMessage());
        } catch (InvocationTargetException e3) {
            throw new ParseException(e3.getTargetException().getMessage(), -1);
        }
    }
}
