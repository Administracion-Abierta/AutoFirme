package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Section.class */
public class Section extends ArrayList implements TextElementArray, LargeElement {
    public static final int NUMBERSTYLE_DOTTED = 0;
    public static final int NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT = 1;
    private static final long serialVersionUID = 3324172577544748043L;
    protected Paragraph title;
    protected String bookmarkTitle;
    protected int numberDepth;
    protected int numberStyle;
    protected float indentationLeft;
    protected float indentationRight;
    protected float indentation;
    protected boolean bookmarkOpen;
    protected boolean triggerNewPage;
    protected int subsections;
    protected ArrayList numbers;
    protected boolean complete;
    protected boolean addedCompletely;
    protected boolean notAddedYet;

    protected Section() {
        this.numberStyle = 0;
        this.bookmarkOpen = true;
        this.triggerNewPage = false;
        this.subsections = 0;
        this.numbers = null;
        this.complete = true;
        this.addedCompletely = false;
        this.notAddedYet = true;
        this.title = new Paragraph();
        this.numberDepth = 1;
    }

    protected Section(Paragraph title, int numberDepth) {
        this.numberStyle = 0;
        this.bookmarkOpen = true;
        this.triggerNewPage = false;
        this.subsections = 0;
        this.numbers = null;
        this.complete = true;
        this.addedCompletely = false;
        this.notAddedYet = true;
        this.numberDepth = numberDepth;
        this.title = title;
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            Iterator i = iterator();
            while (i.hasNext()) {
                Element element = (Element) i.next();
                listener.add(element);
            }
            return true;
        } catch (DocumentException e) {
            return false;
        }
    }

    public int type() {
        return 13;
    }

    public boolean isChapter() {
        return type() == 16;
    }

    public boolean isSection() {
        return type() == 13;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        ArrayList tmp = new ArrayList();
        Iterator i = iterator();
        while (i.hasNext()) {
            tmp.addAll(((Element) i.next()).getChunks());
        }
        return tmp;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return false;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public void add(int index, Object o) {
        if (isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        try {
            Element element = (Element) o;
            if (element.isNestable()) {
                super.add(index, element);
                return;
            }
            throw new ClassCastException("You can't add a " + element.getClass().getName() + " to a Section.");
        } catch (ClassCastException cce) {
            throw new ClassCastException("Insertion of illegal Element: " + cce.getMessage());
        }
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List, com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        if (isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        try {
            Element element = (Element) o;
            if (element.type() == 13) {
                Section section = (Section) o;
                int i = this.subsections + 1;
                this.subsections = i;
                section.setNumbers(i, this.numbers);
                return super.add(section);
            }
            if ((o instanceof MarkedSection) && ((MarkedObject) o).element.type() == 13) {
                MarkedSection mo = (MarkedSection) o;
                Section section2 = (Section) mo.element;
                int i2 = this.subsections + 1;
                this.subsections = i2;
                section2.setNumbers(i2, this.numbers);
                return super.add(mo);
            }
            if (element.isNestable()) {
                return super.add(o);
            }
            throw new ClassCastException("You can't add a " + element.getClass().getName() + " to a Section.");
        } catch (ClassCastException cce) {
            throw new ClassCastException("Insertion of illegal Element: " + cce.getMessage());
        }
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            add(iterator.next());
        }
        return true;
    }

    public Section addSection(float indentation, Paragraph title, int numberDepth) {
        if (isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        Section section = new Section(title, numberDepth);
        section.setIndentation(indentation);
        add(section);
        return section;
    }

    public Section addSection(float indentation, Paragraph title) {
        return addSection(indentation, title, this.numberDepth + 1);
    }

    public Section addSection(Paragraph title, int numberDepth) {
        return addSection(ColumnText.GLOBAL_SPACE_CHAR_RATIO, title, numberDepth);
    }

    public MarkedSection addMarkedSection() {
        MarkedSection section = new MarkedSection(new Section(null, this.numberDepth + 1));
        add(section);
        return section;
    }

    public Section addSection(Paragraph title) {
        return addSection(ColumnText.GLOBAL_SPACE_CHAR_RATIO, title, this.numberDepth + 1);
    }

    public Section addSection(float indentation, String title, int numberDepth) {
        return addSection(indentation, new Paragraph(title), numberDepth);
    }

    public Section addSection(String title, int numberDepth) {
        return addSection(new Paragraph(title), numberDepth);
    }

    public Section addSection(float indentation, String title) {
        return addSection(indentation, new Paragraph(title));
    }

    public Section addSection(String title) {
        return addSection(new Paragraph(title));
    }

    public void setTitle(Paragraph title) {
        this.title = title;
    }

    public Paragraph getTitle() {
        return constructTitle(this.title, this.numbers, this.numberDepth, this.numberStyle);
    }

    public static Paragraph constructTitle(Paragraph title, ArrayList numbers, int numberDepth, int numberStyle) {
        if (title == null) {
            return null;
        }
        int depth = Math.min(numbers.size(), numberDepth);
        if (depth < 1) {
            return title;
        }
        StringBuffer buf = new StringBuffer(" ");
        for (int i = 0; i < depth; i++) {
            buf.insert(0, ".");
            buf.insert(0, ((Integer) numbers.get(i)).intValue());
        }
        if (numberStyle == 1) {
            buf.deleteCharAt(buf.length() - 2);
        }
        Paragraph result = new Paragraph(title);
        result.add(0, new Chunk(buf.toString(), title.getFont()));
        return result;
    }

    public void setNumberDepth(int numberDepth) {
        this.numberDepth = numberDepth;
    }

    public int getNumberDepth() {
        return this.numberDepth;
    }

    public void setNumberStyle(int numberStyle) {
        this.numberStyle = numberStyle;
    }

    public int getNumberStyle() {
        return this.numberStyle;
    }

    public void setIndentationLeft(float indentation) {
        this.indentationLeft = indentation;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public void setIndentationRight(float indentation) {
        this.indentationRight = indentation;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public void setIndentation(float indentation) {
        this.indentation = indentation;
    }

    public float getIndentation() {
        return this.indentation;
    }

    public void setBookmarkOpen(boolean bookmarkOpen) {
        this.bookmarkOpen = bookmarkOpen;
    }

    public boolean isBookmarkOpen() {
        return this.bookmarkOpen;
    }

    public void setTriggerNewPage(boolean triggerNewPage) {
        this.triggerNewPage = triggerNewPage;
    }

    public boolean isTriggerNewPage() {
        return this.triggerNewPage && this.notAddedYet;
    }

    public void setBookmarkTitle(String bookmarkTitle) {
        this.bookmarkTitle = bookmarkTitle;
    }

    public Paragraph getBookmarkTitle() {
        if (this.bookmarkTitle == null) {
            return getTitle();
        }
        return new Paragraph(this.bookmarkTitle);
    }

    public void setChapterNumber(int number) {
        this.numbers.set(this.numbers.size() - 1, Integer.valueOf(number));
        for (Object s : this) {
            if (s instanceof Section) {
                ((Section) s).setChapterNumber(number);
            }
        }
    }

    public int getDepth() {
        return this.numbers.size();
    }

    private void setNumbers(int number, ArrayList numbers) {
        this.numbers = new ArrayList();
        this.numbers.add(Integer.valueOf(number));
        this.numbers.addAll(numbers);
    }

    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }

    public void setNotAddedYet(boolean notAddedYet) {
        this.notAddedYet = notAddedYet;
    }

    protected boolean isAddedCompletely() {
        return this.addedCompletely;
    }

    protected void setAddedCompletely(boolean addedCompletely) {
        this.addedCompletely = addedCompletely;
    }

    @Override // com.aowagie.text.LargeElement
    public void flushContent() {
        setNotAddedYet(false);
        this.title = null;
        Iterator i = iterator();
        while (i.hasNext()) {
            Element element = (Element) i.next();
            if (element instanceof Section) {
                Section s = (Section) element;
                if (!s.isComplete() && size() == 1) {
                    s.flushContent();
                    return;
                }
                s.setAddedCompletely(true);
            }
            i.remove();
        }
    }

    @Override // com.aowagie.text.LargeElement
    public boolean isComplete() {
        return this.complete;
    }

    @Override // com.aowagie.text.LargeElement
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void newPage() {
        add(Chunk.NEXTPAGE);
    }
}
