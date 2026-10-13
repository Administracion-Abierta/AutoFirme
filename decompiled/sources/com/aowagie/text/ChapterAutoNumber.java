package com.aowagie.text;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ChapterAutoNumber.class */
public class ChapterAutoNumber extends Chapter {
    private static final long serialVersionUID = -9217457637987854167L;
    private boolean numberSet;

    public ChapterAutoNumber(String title) {
        super(title, 0);
        this.numberSet = false;
    }

    @Override // com.aowagie.text.Section
    public Section addSection(String title) {
        if (isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        return addSection(title, 2);
    }

    @Override // com.aowagie.text.Section
    public Section addSection(Paragraph title) {
        if (isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        return addSection(title, 2);
    }

    int setAutomaticNumber(int number) {
        if (!this.numberSet) {
            number++;
            super.setChapterNumber(number);
            this.numberSet = true;
        }
        return number;
    }
}
