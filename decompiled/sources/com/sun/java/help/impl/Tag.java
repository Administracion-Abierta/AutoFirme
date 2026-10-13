package com.sun.java.help.impl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/Tag.class */
public class Tag {
    public String name;
    public boolean isEnd;
    public boolean isEmpty;
    public TagProperties atts;

    public Tag(String str, TagProperties tagProperties, boolean z, boolean z2) {
        this.name = str;
        this.atts = tagProperties;
        this.isEnd = z;
        this.isEmpty = z2;
    }
}
