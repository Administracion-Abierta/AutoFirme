package com.sun.java.help.impl;

import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/LangElement.class */
public class LangElement {
    Tag tag;
    Locale locale;

    public LangElement(Tag tag, Locale locale) {
        this.tag = tag;
        this.locale = locale;
    }

    public Tag getTag() {
        return this.tag;
    }

    public Locale getLocale() {
        return this.locale;
    }
}
