package com.ctc.wstx.shaded.msv_core.grammar.trex;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/trex/Localizer.class */
class Localizer {
    Localizer() {
    }

    public static String localize(String prop, Object[] args) {
        return MessageFormat.format(ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.grammar.trex.Messages").getString(prop), args);
    }

    public static String localize(String prop) {
        return localize(prop, (Object[]) null);
    }

    public static String localize(String prop, Object arg1) {
        return localize(prop, new Object[]{arg1});
    }
}
