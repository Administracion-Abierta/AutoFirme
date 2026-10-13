package com.aowagie.text;

import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/MarkedSection.class */
public class MarkedSection extends MarkedObject {
    protected MarkedObject title;

    public MarkedSection(Section section) {
        this.title = null;
        if (section.title != null) {
            this.title = new MarkedObject(section.title);
            section.setTitle(null);
        }
        this.element = section;
    }

    public void add(int index, Object o) {
        ((Section) this.element).add(index, o);
    }

    public boolean add(Object o) {
        return ((Section) this.element).add(o);
    }

    @Override // com.aowagie.text.MarkedObject, com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            for (Element element : (Section) this.element) {
                listener.add(element);
            }
            return true;
        } catch (DocumentException e) {
            return false;
        }
    }

    public boolean addAll(Collection collection) {
        return ((Section) this.element).addAll(collection);
    }

    public MarkedSection addSection(float indentation, int numberDepth) {
        MarkedSection section = ((Section) this.element).addMarkedSection();
        section.setIndentation(indentation);
        section.setNumberDepth(numberDepth);
        return section;
    }

    public MarkedSection addSection(float indentation) {
        MarkedSection section = ((Section) this.element).addMarkedSection();
        section.setIndentation(indentation);
        return section;
    }

    public MarkedSection addSection(int numberDepth) {
        MarkedSection section = ((Section) this.element).addMarkedSection();
        section.setNumberDepth(numberDepth);
        return section;
    }

    public MarkedSection addSection() {
        return ((Section) this.element).addMarkedSection();
    }

    public void setTitle(MarkedObject title) {
        if (title.element instanceof Paragraph) {
            this.title = title;
        }
    }

    public MarkedObject getTitle() {
        Paragraph result = Section.constructTitle((Paragraph) this.title.element, ((Section) this.element).numbers, ((Section) this.element).numberDepth, ((Section) this.element).numberStyle);
        MarkedObject mo = new MarkedObject(result);
        mo.markupAttributes = this.title.markupAttributes;
        return mo;
    }

    public void setNumberDepth(int numberDepth) {
        ((Section) this.element).setNumberDepth(numberDepth);
    }

    public void setIndentationLeft(float indentation) {
        ((Section) this.element).setIndentationLeft(indentation);
    }

    public void setIndentationRight(float indentation) {
        ((Section) this.element).setIndentationRight(indentation);
    }

    public void setIndentation(float indentation) {
        ((Section) this.element).setIndentation(indentation);
    }

    public void setBookmarkOpen(boolean bookmarkOpen) {
        ((Section) this.element).setBookmarkOpen(bookmarkOpen);
    }

    public void setTriggerNewPage(boolean triggerNewPage) {
        ((Section) this.element).setTriggerNewPage(triggerNewPage);
    }

    public void setBookmarkTitle(String bookmarkTitle) {
        ((Section) this.element).setBookmarkTitle(bookmarkTitle);
    }

    public void newPage() {
        ((Section) this.element).newPage();
    }
}
