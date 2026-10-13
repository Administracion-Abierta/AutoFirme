package com.aowagie.text;

import com.aowagie.text.factories.RomanAlphabetFactory;
import com.aowagie.text.pdf.ColumnText;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/List.class */
public class List implements TextElementArray {
    public static final boolean ORDERED = true;
    public static final boolean UNORDERED = false;
    public static final boolean NUMERICAL = false;
    public static final boolean ALPHABETICAL = true;
    public static final boolean UPPERCASE = false;
    public static final boolean LOWERCASE = true;
    protected ArrayList list;
    protected boolean numbered;
    protected boolean lettered;
    protected boolean lowercase;
    protected boolean autoindent;
    protected boolean alignindent;
    protected int first;
    protected Chunk symbol;
    protected String preSymbol;
    protected String postSymbol;
    protected float indentationLeft;
    protected float indentationRight;
    protected float symbolIndent;

    public List() {
        this(false, false);
    }

    public List(float symbolIndent) {
        this.list = new ArrayList();
        this.numbered = false;
        this.lettered = false;
        this.lowercase = false;
        this.autoindent = false;
        this.alignindent = false;
        this.first = 1;
        this.symbol = new Chunk("- ");
        this.preSymbol = "";
        this.postSymbol = ". ";
        this.indentationLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentationRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.symbolIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.symbolIndent = symbolIndent;
    }

    public List(boolean numbered) {
        this(numbered, false);
    }

    public List(boolean numbered, boolean lettered) {
        this.list = new ArrayList();
        this.numbered = false;
        this.lettered = false;
        this.lowercase = false;
        this.autoindent = false;
        this.alignindent = false;
        this.first = 1;
        this.symbol = new Chunk("- ");
        this.preSymbol = "";
        this.postSymbol = ". ";
        this.indentationLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentationRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.symbolIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.numbered = numbered;
        this.lettered = lettered;
        this.autoindent = true;
        this.alignindent = true;
    }

    public List(boolean numbered, float symbolIndent) {
        this(numbered, false, symbolIndent);
    }

    public List(boolean numbered, boolean lettered, float symbolIndent) {
        this.list = new ArrayList();
        this.numbered = false;
        this.lettered = false;
        this.lowercase = false;
        this.autoindent = false;
        this.alignindent = false;
        this.first = 1;
        this.symbol = new Chunk("- ");
        this.preSymbol = "";
        this.postSymbol = ". ";
        this.indentationLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentationRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.symbolIndent = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.numbered = numbered;
        this.lettered = lettered;
        this.symbolIndent = symbolIndent;
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            Iterator i = this.list.iterator();
            while (i.hasNext()) {
                listener.add((Element) i.next());
            }
            return true;
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return 14;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        ArrayList tmp = new ArrayList();
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            tmp.addAll(((Element) i.next()).getChunks());
        }
        return tmp;
    }

    @Override // com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        if (o instanceof ListItem) {
            ListItem item = (ListItem) o;
            if (this.numbered || this.lettered) {
                Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
                int index = this.first + this.list.size();
                if (this.lettered) {
                    chunk.append(RomanAlphabetFactory.getString(index, this.lowercase));
                } else {
                    chunk.append(String.valueOf(index));
                }
                chunk.append(this.postSymbol);
                item.setListSymbol(chunk);
            } else {
                item.setListSymbol(this.symbol);
            }
            item.setIndentationLeft(this.symbolIndent, this.autoindent);
            item.setIndentationRight(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            return this.list.add(item);
        }
        if (o instanceof List) {
            List nested = (List) o;
            nested.setIndentationLeft(nested.getIndentationLeft() + this.symbolIndent);
            this.first--;
            return this.list.add(nested);
        }
        if (o instanceof String) {
            return add(new ListItem((String) o));
        }
        return false;
    }

    public void normalizeIndentation() {
        float max = 0.0f;
        for (Element o : this.list) {
            if (o instanceof ListItem) {
                max = Math.max(max, ((ListItem) o).getIndentationLeft());
            }
        }
        for (Element o2 : this.list) {
            if (o2 instanceof ListItem) {
                ((ListItem) o2).setIndentationLeft(max);
            }
        }
    }

    public void setNumbered(boolean numbered) {
        this.numbered = numbered;
    }

    public void setLettered(boolean lettered) {
        this.lettered = lettered;
    }

    public void setLowercase(boolean uppercase) {
        this.lowercase = uppercase;
    }

    public void setAutoindent(boolean autoindent) {
        this.autoindent = autoindent;
    }

    public void setAlignindent(boolean alignindent) {
        this.alignindent = alignindent;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public void setListSymbol(Chunk symbol) {
        this.symbol = symbol;
    }

    public void setListSymbol(String symbol) {
        this.symbol = new Chunk(symbol);
    }

    public void setIndentationLeft(float indentation) {
        this.indentationLeft = indentation;
    }

    public void setIndentationRight(float indentation) {
        this.indentationRight = indentation;
    }

    public void setSymbolIndent(float symbolIndent) {
        this.symbolIndent = symbolIndent;
    }

    public ArrayList getItems() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public float getTotalLeading() {
        if (this.list.size() < 1) {
            return -1.0f;
        }
        ListItem item = (ListItem) this.list.get(0);
        return item.getTotalLeading();
    }

    public boolean isNumbered() {
        return this.numbered;
    }

    public boolean isLettered() {
        return this.lettered;
    }

    public boolean isLowercase() {
        return this.lowercase;
    }

    public boolean isAutoindent() {
        return this.autoindent;
    }

    public boolean isAlignindent() {
        return this.alignindent;
    }

    public int getFirst() {
        return this.first;
    }

    public Chunk getSymbol() {
        return this.symbol;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public float getSymbolIndent() {
        return this.symbolIndent;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    public String getPostSymbol() {
        return this.postSymbol;
    }

    public void setPostSymbol(String postSymbol) {
        this.postSymbol = postSymbol;
    }

    public String getPreSymbol() {
        return this.preSymbol;
    }

    public void setPreSymbol(String preSymbol) {
        this.preSymbol = preSymbol;
    }
}
