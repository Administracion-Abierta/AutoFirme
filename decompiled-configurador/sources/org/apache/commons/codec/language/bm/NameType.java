package org.apache.commons.codec.language.bm;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/NameType.class */
public enum NameType {
    ASHKENAZI("ash"),
    GENERIC("gen"),
    SEPHARDIC("sep");

    private final String name;

    NameType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
