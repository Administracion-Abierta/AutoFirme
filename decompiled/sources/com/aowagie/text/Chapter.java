package com.aowagie.text;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Chapter.class */
public class Chapter extends Section {
    private static final long serialVersionUID = 1791000695779357361L;

    private Chapter(Paragraph title, int number) {
        super(title, 1);
        this.numbers = new ArrayList();
        this.numbers.add(Integer.valueOf(number));
        this.triggerNewPage = true;
    }

    Chapter(String title, int number) {
        this(new Paragraph(title), number);
    }

    @Override // com.aowagie.text.Section, com.aowagie.text.Element
    public int type() {
        return 16;
    }

    @Override // com.aowagie.text.Section, com.aowagie.text.Element
    public boolean isNestable() {
        return false;
    }
}
