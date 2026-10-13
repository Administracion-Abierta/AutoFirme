package com.sun.java.help.impl;

import java.util.EventObject;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/ParserEvent.class */
public class ParserEvent extends EventObject {
    private Tag tag;
    private String text;
    private String target;
    private String data;
    private String root;
    private String publicId;
    private String systemId;

    public ParserEvent(Object obj, Tag tag) {
        super(obj);
        this.tag = tag;
    }

    public ParserEvent(Object obj, String str) {
        super(obj);
        this.text = str;
    }

    public ParserEvent(Object obj, String str, String str2) {
        super(obj);
        this.target = str;
        this.data = str2;
    }

    public ParserEvent(Object obj, String str, String str2, String str3) {
        super(obj);
        this.root = str;
        this.publicId = str2;
        this.systemId = str3;
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getText() {
        return this.text;
    }

    public String getTarget() {
        return this.target;
    }

    public String getData() {
        return this.data;
    }

    public String getRoot() {
        return this.root;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }
}
