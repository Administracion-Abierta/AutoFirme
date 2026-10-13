package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/grammar/relax/Localizer.class */
public class Localizer {
    public static final String WRN_ANYOTHER_NAMESPACE_IGNORED = "AnyOtherElementExp.Warning.AnyOtherNamespaceIgnored";

    public static String localize(String propertyName, Object[] args) {
        String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.Messages").getString(propertyName);
        return MessageFormat.format(format, args);
    }

    public static String localize(String prop) {
        return localize(prop, (Object[]) null);
    }

    public static String localize(String prop, Object arg1) {
        return localize(prop, new Object[]{arg1});
    }

    public static String localize(String prop, Object arg1, Object arg2) {
        return localize(prop, new Object[]{arg1, arg2});
    }
}
