package com.aowagie.text;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Row.class */
public class Row implements Element {
    public static final int NULL = 0;
    public static final int CELL = 1;
    public static final int TABLE = 2;
    protected int columns;
    protected int currentColumn = 0;
    protected boolean[] reserved;
    protected Object[] cells;
    protected int horizontalAlignment;

    protected Row(int columns) {
        this.columns = columns;
        this.reserved = new boolean[columns];
        this.cells = new Object[columns];
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
        return 21;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        return new ArrayList();
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return false;
    }

    void deleteColumn(int column) {
        if (column >= this.columns || column < 0) {
            throw new IndexOutOfBoundsException("getCell at illegal index : " + column);
        }
        this.columns--;
        boolean[] newReserved = new boolean[this.columns];
        Object[] newCells = new Cell[this.columns];
        for (int i = 0; i < column; i++) {
            newReserved[i] = this.reserved[i];
            newCells[i] = this.cells[i];
            if (newCells[i] != null && i + ((Cell) newCells[i]).getColspan() > column) {
                ((Cell) newCells[i]).setColspan(((Cell) this.cells[i]).getColspan() - 1);
            }
        }
        for (int i2 = column; i2 < this.columns; i2++) {
            newReserved[i2] = this.reserved[i2 + 1];
            newCells[i2] = this.cells[i2 + 1];
        }
        if (this.cells[column] != null && ((Cell) this.cells[column]).getColspan() > 1) {
            newCells[column] = this.cells[column];
            ((Cell) newCells[column]).setColspan(((Cell) newCells[column]).getColspan() - 1);
        }
        this.reserved = newReserved;
        this.cells = newCells;
    }

    int addElement(Object element) {
        return addElement(element, this.currentColumn);
    }

    int addElement(Object element, int column) {
        if (element == null) {
            throw new NullPointerException("addCell - null argument");
        }
        if (column < 0 || column > this.columns) {
            throw new IndexOutOfBoundsException("addCell - illegal column argument");
        }
        if (getObjectID(element) != 1 && getObjectID(element) != 2) {
            throw new IllegalArgumentException("addCell - only Cells or Tables allowed");
        }
        int lColspan = Cell.class.isInstance(element) ? ((Cell) element).getColspan() : 1;
        if (!reserve(column, lColspan)) {
            return -1;
        }
        this.cells[column] = element;
        this.currentColumn += lColspan - 1;
        return column;
    }

    void setElement(Object aElement, int column) {
        if (this.reserved[column]) {
            throw new IllegalArgumentException("setElement - position already taken");
        }
        this.cells[column] = aElement;
        if (aElement != null) {
            this.reserved[column] = true;
        }
    }

    boolean reserve(int column) {
        return reserve(column, 1);
    }

    boolean reserve(int column, int size) {
        if (column < 0 || column + size > this.columns) {
            throw new IndexOutOfBoundsException("reserve - incorrect column/size");
        }
        for (int i = column; i < column + size; i++) {
            if (this.reserved[i]) {
                for (int j = i; j >= column; j--) {
                    this.reserved[j] = false;
                }
                return false;
            }
            this.reserved[i] = true;
        }
        return true;
    }

    boolean isReserved(int column) {
        return this.reserved[column];
    }

    int getElementID(int column) {
        if (this.cells[column] == null) {
            return 0;
        }
        if (Cell.class.isInstance(this.cells[column])) {
            return 1;
        }
        if (Table.class.isInstance(this.cells[column])) {
            return 2;
        }
        return -1;
    }

    int getObjectID(Object element) {
        if (element == null) {
            return 0;
        }
        if (Cell.class.isInstance(element)) {
            return 1;
        }
        if (Table.class.isInstance(element)) {
            return 2;
        }
        return -1;
    }

    public Object getCell(int column) {
        if (column < 0 || column > this.columns) {
            throw new IndexOutOfBoundsException("getCell at illegal index :" + column + " max is " + this.columns);
        }
        return this.cells[column];
    }

    public boolean isEmpty() {
        for (int i = 0; i < this.columns; i++) {
            if (this.cells[i] != null) {
                return false;
            }
        }
        return true;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setHorizontalAlignment(int value) {
        this.horizontalAlignment = value;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
}
