package com.aowagie.text;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Header.class */
public class Header extends Meta {
    private final StringBuffer name;

    public Header(String name, String content) {
        super(0, content);
        this.name = new StringBuffer(name);
    }

    @Override // com.aowagie.text.Meta
    public String getName() {
        return this.name.toString();
    }
}
