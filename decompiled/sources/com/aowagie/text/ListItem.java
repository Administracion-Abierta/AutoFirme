package com.aowagie.text;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ListItem.class */
public class ListItem extends Paragraph {
    private static final long serialVersionUID = 1970670787169329006L;
    private Chunk symbol;

    public ListItem() {
    }

    ListItem(String string) {
        super(string);
    }

    public ListItem(Phrase phrase) {
        super(phrase);
    }

    @Override // com.aowagie.text.Paragraph, com.aowagie.text.Phrase, com.aowagie.text.Element
    public int type() {
        return 15;
    }

    public void setListSymbol(Chunk symbol) {
        if (this.symbol == null) {
            this.symbol = symbol;
            if (this.symbol.getFont().isStandardFont()) {
                this.symbol.setFont(this.font);
            }
        }
    }

    void setIndentationLeft(float indentation, boolean autoindent) {
        if (autoindent) {
            setIndentationLeft(getListSymbol().getWidthPoint());
        } else {
            setIndentationLeft(indentation);
        }
    }

    public Chunk getListSymbol() {
        return this.symbol;
    }
}
