package com.aowagie.text;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Meta.class */
public class Meta implements Element {
    private final int type;
    private final StringBuffer content;

    Meta(int type, String content) {
        this.type = type;
        this.content = new StringBuffer(content);
    }

    public Meta(String tag, String content) {
        this.type = getType(tag);
        this.content = new StringBuffer(content);
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return this.type;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        return new ArrayList();
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return false;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return false;
    }

    public StringBuffer append(String string) {
        return this.content.append(string);
    }

    public String getContent() {
        return this.content.toString();
    }

    public String getName() {
        switch (this.type) {
            case 1:
                return "title";
            case 2:
                return "subject";
            case 3:
                return "keywords";
            case 4:
                return "author";
            case 5:
                return "producer";
            case 6:
                return "creationdate";
            default:
                return "unknown";
        }
    }

    public static int getType(String tag) {
        if ("subject".equals(tag)) {
            return 2;
        }
        if ("keywords".equals(tag)) {
            return 3;
        }
        if ("author".equals(tag)) {
            return 4;
        }
        if ("title".equals(tag)) {
            return 1;
        }
        if ("producer".equals(tag)) {
            return 5;
        }
        if ("creationdate".equals(tag)) {
            return 6;
        }
        return 0;
    }
}
