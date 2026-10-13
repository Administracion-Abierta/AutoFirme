package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfPCell;
import com.aowagie.text.pdf.PdfPTable;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Table.class */
public class Table extends Rectangle implements LargeElement {
    private int columns;
    private ArrayList rows;
    private Point curPosition;
    private Cell defaultCell;
    private int lastHeaderRow;
    private int alignment;
    private float cellpadding;
    private float cellspacing;
    private float width;
    private boolean locked;
    private float[] widths;
    private boolean mTableInserted;
    private boolean autoFillEmptyCells;
    private boolean tableFitsPage;
    private boolean cellsFitPage;
    private float offset;
    private boolean convert2pdfptable;
    private boolean notAddedYet;
    private boolean complete;

    public Table(int columns) throws BadElementException {
        this(columns, 1);
    }

    private Table(int columns, int rows) throws BadElementException {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.rows = new ArrayList();
        this.curPosition = new Point(0, 0);
        this.defaultCell = new Cell(true);
        this.lastHeaderRow = -1;
        this.alignment = 1;
        this.width = 80.0f;
        this.locked = false;
        this.mTableInserted = false;
        this.autoFillEmptyCells = false;
        this.tableFitsPage = false;
        this.cellsFitPage = false;
        this.offset = Float.NaN;
        this.convert2pdfptable = false;
        this.notAddedYet = true;
        this.complete = true;
        setBorder(15);
        setBorderWidth(1.0f);
        this.defaultCell.setBorder(15);
        if (columns <= 0) {
            throw new BadElementException("A table should have at least 1 column.");
        }
        this.columns = columns;
        for (int i = 0; i < rows; i++) {
            this.rows.add(new Row(columns));
        }
        this.curPosition = new Point(0, 0);
        this.widths = new float[columns];
        float width = 100.0f / columns;
        for (int i2 = 0; i2 < columns; i2++) {
            this.widths[i2] = width;
        }
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public int type() {
        return 22;
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public ArrayList getChunks() {
        return new ArrayList();
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    public int getColumns() {
        return this.columns;
    }

    public int size() {
        return this.rows.size();
    }

    public Dimension getDimension() {
        return new Dimension(this.columns, size());
    }

    public Cell getDefaultCell() {
        return this.defaultCell;
    }

    public void setDefaultCell(Cell value) {
        this.defaultCell = value;
    }

    public int getLastHeaderRow() {
        return this.lastHeaderRow;
    }

    public void setLastHeaderRow(int value) {
        this.lastHeaderRow = value;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int value) {
        this.alignment = value;
    }

    public void setAlignment(String alignment) {
        if (ElementTags.ALIGN_LEFT.equalsIgnoreCase(alignment)) {
            this.alignment = 0;
        } else if (ElementTags.RIGHT.equalsIgnoreCase(alignment)) {
            this.alignment = 2;
        } else {
            this.alignment = 1;
        }
    }

    public float getPadding() {
        return this.cellpadding;
    }

    public void setPadding(float value) {
        this.cellpadding = value;
    }

    public float getSpacing() {
        return this.cellspacing;
    }

    public void setSpacing(float value) {
        this.cellspacing = value;
    }

    public void setAutoFillEmptyCells(boolean aDoAutoFill) {
        this.autoFillEmptyCells = aDoAutoFill;
    }

    @Override // com.aowagie.text.Rectangle
    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public float[] getProportionalWidths() {
        return this.widths;
    }

    public void setWidths(float[] widths) throws BadElementException {
        if (widths.length != this.columns) {
            throw new BadElementException("Wrong number of columns.");
        }
        float hundredPercent = 0.0f;
        for (int i = 0; i < this.columns; i++) {
            hundredPercent += widths[i];
        }
        this.widths[this.columns - 1] = 100.0f;
        for (int i2 = 0; i2 < this.columns - 1; i2++) {
            float width = (100.0f * widths[i2]) / hundredPercent;
            this.widths[i2] = width;
            float[] fArr = this.widths;
            int i3 = this.columns - 1;
            fArr[i3] = fArr[i3] - width;
        }
    }

    public void setWidths(int[] widths) throws DocumentException {
        float[] tb = new float[widths.length];
        for (int k = 0; k < widths.length; k++) {
            tb[k] = widths[k];
        }
        setWidths(tb);
    }

    public boolean isTableFitsPage() {
        return this.tableFitsPage;
    }

    public void setTableFitsPage(boolean fitPage) {
        this.tableFitsPage = fitPage;
        if (fitPage) {
            setCellsFitPage(true);
        }
    }

    public boolean isCellsFitPage() {
        return this.cellsFitPage;
    }

    public void setCellsFitPage(boolean fitPage) {
        this.cellsFitPage = fitPage;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean isConvert2pdfptable() {
        return this.convert2pdfptable;
    }

    public void setConvert2pdfptable(boolean convert2pdfptable) {
        this.convert2pdfptable = convert2pdfptable;
    }

    private void addCell(Cell aCell, Point aLocation) throws BadElementException {
        if (aCell == null) {
            throw new NullPointerException("addCell - cell has null-value");
        }
        if (aLocation == null) {
            throw new NullPointerException("addCell - point has null-value");
        }
        if (aCell.isTable()) {
            insertTable((Table) aCell.getElements().next(), aLocation);
        }
        if (aLocation.x < 0) {
            throw new BadElementException("row coordinate of location must be >= 0");
        }
        if (aLocation.y <= 0 && aLocation.y > this.columns) {
            throw new BadElementException("column coordinate of location must be >= 0 and < nr of columns");
        }
        if (!isValidLocation(aCell, aLocation)) {
            throw new BadElementException("Adding a cell at the location (" + aLocation.x + "," + aLocation.y + ") with a colspan of " + aCell.getColspan() + " and a rowspan of " + aCell.getRowspan() + " is illegal (beyond boundaries/overlapping).");
        }
        if (aCell.getBorder() == -1) {
            aCell.setBorder(this.defaultCell.getBorder());
        }
        aCell.fill();
        placeCell(this.rows, aCell, aLocation);
        setCurrentLocationToNextValidPosition(aLocation);
    }

    public void addCell(Cell cell) {
        try {
            addCell(cell, this.curPosition);
        } catch (BadElementException e) {
        }
    }

    private void addCell(Phrase content, Point location) throws BadElementException {
        Cell cell = new Cell(content);
        cell.setBorder(this.defaultCell.getBorder());
        cell.setBorderWidth(this.defaultCell.getBorderWidth());
        cell.setBorderColor(this.defaultCell.getBorderColor());
        cell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        cell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
        cell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
        cell.setColspan(this.defaultCell.getColspan());
        cell.setRowspan(this.defaultCell.getRowspan());
        addCell(cell, location);
    }

    void insertTable(Table aTable) {
        if (aTable == null) {
            throw new NullPointerException("insertTable - table has null-value");
        }
        insertTable(aTable, this.curPosition);
    }

    private void insertTable(Table aTable, Point aLocation) {
        if (aTable == null) {
            throw new NullPointerException("insertTable - table has null-value");
        }
        if (aLocation == null) {
            throw new NullPointerException("insertTable - point has null-value");
        }
        this.mTableInserted = true;
        aTable.complete();
        if (aLocation.y > this.columns) {
            throw new IllegalArgumentException("insertTable -- wrong columnposition(" + aLocation.y + ") of location; max =" + this.columns);
        }
        int rowCount = (aLocation.x + 1) - this.rows.size();
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                this.rows.add(new Row(this.columns));
            }
        }
        ((Row) this.rows.get(aLocation.x)).setElement(aTable, aLocation.y);
        setCurrentLocationToNextValidPosition(aLocation);
    }

    public void addColumns(int aColumns) {
        ArrayList newRows = new ArrayList(this.rows.size());
        int newColumns = this.columns + aColumns;
        for (int i = 0; i < this.rows.size(); i++) {
            Row row = new Row(newColumns);
            for (int j = 0; j < this.columns; j++) {
                row.setElement(((Row) this.rows.get(i)).getCell(j), j);
            }
            for (int j2 = this.columns; j2 < newColumns && i < this.curPosition.x; j2++) {
                row.setElement(null, j2);
            }
            newRows.add(row);
        }
        float[] newWidths = new float[newColumns];
        System.arraycopy(this.widths, 0, newWidths, 0, this.columns);
        for (int j3 = this.columns; j3 < newColumns; j3++) {
            newWidths[j3] = 0.0f;
        }
        this.columns = newColumns;
        this.widths = newWidths;
        this.rows = newRows;
    }

    public void complete() {
        if (this.mTableInserted) {
            mergeInsertedTables();
            this.mTableInserted = false;
        }
        if (this.autoFillEmptyCells) {
            fillEmptyMatrixCells();
        }
    }

    private Object getElement(int row, int column) {
        return ((Row) this.rows.get(row)).getCell(column);
    }

    private void mergeInsertedTables() {
        int[] lDummyWidths = new int[this.columns];
        float[][] lDummyColumnWidths = new float[this.columns][];
        int[] lDummyHeights = new int[this.rows.size()];
        boolean isTable = false;
        int lTotalRows = 0;
        int lTotalColumns = 0;
        for (int j = 0; j < this.columns; j++) {
            int lNewMaxColumns = 1;
            float[] tmpWidths = null;
            for (int i = 0; i < this.rows.size(); i++) {
                if (Table.class.isInstance(((Row) this.rows.get(i)).getCell(j))) {
                    isTable = true;
                    Table lDummyTable = (Table) ((Row) this.rows.get(i)).getCell(j);
                    if (tmpWidths == null) {
                        tmpWidths = lDummyTable.widths;
                        lNewMaxColumns = tmpWidths.length;
                    } else {
                        int cols = lDummyTable.getDimension().width;
                        float[] tmpWidthsN = new float[cols * tmpWidths.length];
                        float totW = 0.0f;
                        int tpI = 0;
                        int btI = 0;
                        int totI = 0;
                        float tpW = ColumnText.GLOBAL_SPACE_CHAR_RATIO + tmpWidths[0];
                        float btW = ColumnText.GLOBAL_SPACE_CHAR_RATIO + lDummyTable.widths[0];
                        while (tpI < tmpWidths.length && btI < cols) {
                            if (btW > tpW) {
                                tmpWidthsN[totI] = tpW - totW;
                                tpI++;
                                if (tpI < tmpWidths.length) {
                                    tpW += tmpWidths[tpI];
                                }
                            } else {
                                tmpWidthsN[totI] = btW - totW;
                                btI++;
                                if (Math.abs(btW - tpW) < 1.0E-4d) {
                                    tpI++;
                                    if (tpI < tmpWidths.length) {
                                        tpW += tmpWidths[tpI];
                                    }
                                }
                                if (btI < cols) {
                                    btW += lDummyTable.widths[btI];
                                }
                            }
                            totW += tmpWidthsN[totI];
                            totI++;
                        }
                        tmpWidths = new float[totI];
                        System.arraycopy(tmpWidthsN, 0, tmpWidths, 0, totI);
                        lNewMaxColumns = totI;
                    }
                }
            }
            lDummyColumnWidths[j] = tmpWidths;
            lTotalColumns += lNewMaxColumns;
            lDummyWidths[j] = lNewMaxColumns;
        }
        for (int i2 = 0; i2 < this.rows.size(); i2++) {
            int lNewMaxRows = 1;
            for (int j2 = 0; j2 < this.columns; j2++) {
                if (Table.class.isInstance(((Row) this.rows.get(i2)).getCell(j2))) {
                    isTable = true;
                    Table lDummyTable2 = (Table) ((Row) this.rows.get(i2)).getCell(j2);
                    if (lDummyTable2.getDimension().height > lNewMaxRows) {
                        lNewMaxRows = lDummyTable2.getDimension().height;
                    }
                }
            }
            lTotalRows += lNewMaxRows;
            lDummyHeights[i2] = lNewMaxRows;
        }
        if (lTotalColumns != this.columns || lTotalRows != this.rows.size() || isTable) {
            float[] lNewWidths = new float[lTotalColumns];
            int lDummy = 0;
            for (int tel = 0; tel < this.widths.length; tel++) {
                if (lDummyWidths[tel] != 1) {
                    for (int tel2 = 0; tel2 < lDummyWidths[tel]; tel2++) {
                        lNewWidths[lDummy] = (this.widths[tel] * lDummyColumnWidths[tel][tel2]) / 100.0f;
                        lDummy++;
                    }
                } else {
                    lNewWidths[lDummy] = this.widths[tel];
                    lDummy++;
                }
            }
            ArrayList newRows = new ArrayList(lTotalRows);
            for (int i3 = 0; i3 < lTotalRows; i3++) {
                newRows.add(new Row(lTotalColumns));
            }
            int lDummyRow = 0;
            for (int i4 = 0; i4 < this.rows.size(); i4++) {
                int lDummyColumn = 0;
                for (int j3 = 0; j3 < this.columns; j3++) {
                    if (Table.class.isInstance(((Row) this.rows.get(i4)).getCell(j3))) {
                        Table lDummyTable3 = (Table) ((Row) this.rows.get(i4)).getCell(j3);
                        int[] colMap = new int[lDummyTable3.widths.length + 1];
                        int cb = 0;
                        int ct = 0;
                        while (cb < lDummyTable3.widths.length) {
                            colMap[cb] = lDummyColumn + ct;
                            float wb = lDummyTable3.widths[cb];
                            float wt = 0.0f;
                            while (ct < lDummyWidths[j3]) {
                                int i5 = ct;
                                ct++;
                                wt += lDummyColumnWidths[j3][i5];
                                if (Math.abs(wb - wt) < 1.0E-4d) {
                                    break;
                                }
                            }
                            cb++;
                        }
                        colMap[cb] = lDummyColumn + ct;
                        for (int k = 0; k < lDummyTable3.getDimension().height; k++) {
                            for (int l = 0; l < lDummyTable3.getDimension().width; l++) {
                                Object lDummyElement = lDummyTable3.getElement(k, l);
                                if (lDummyElement != null) {
                                    int col = lDummyColumn + l;
                                    if (Cell.class.isInstance(lDummyElement)) {
                                        Cell lDummyC = (Cell) lDummyElement;
                                        col = colMap[l];
                                        int ot = colMap[l + lDummyC.getColspan()];
                                        lDummyC.setColspan(ot - col);
                                    }
                                    ((Row) newRows.get(k + lDummyRow)).addElement(lDummyElement, col);
                                }
                            }
                        }
                    } else {
                        Object aElement = getElement(i4, j3);
                        if (Cell.class.isInstance(aElement)) {
                            ((Cell) aElement).setRowspan((((Cell) ((Row) this.rows.get(i4)).getCell(j3)).getRowspan() + lDummyHeights[i4]) - 1);
                            ((Cell) aElement).setColspan((((Cell) ((Row) this.rows.get(i4)).getCell(j3)).getColspan() + lDummyWidths[j3]) - 1);
                            placeCell(newRows, (Cell) aElement, new Point(lDummyRow, lDummyColumn));
                        }
                    }
                    lDummyColumn += lDummyWidths[j3];
                }
                lDummyRow += lDummyHeights[i4];
            }
            this.columns = lTotalColumns;
            this.rows = newRows;
            this.widths = lNewWidths;
        }
    }

    private void fillEmptyMatrixCells() {
        for (int i = 0; i < this.rows.size(); i++) {
            try {
                for (int j = 0; j < this.columns; j++) {
                    if (!((Row) this.rows.get(i)).isReserved(j)) {
                        addCell(this.defaultCell, new Point(i, j));
                    }
                }
            } catch (BadElementException bee) {
                throw new ExceptionConverter(bee);
            }
        }
    }

    private boolean isValidLocation(Cell aCell, Point aLocation) {
        if (aLocation.x < this.rows.size()) {
            if (aLocation.y + aCell.getColspan() > this.columns) {
                return false;
            }
            int difx = this.rows.size() - aLocation.x > aCell.getRowspan() ? aCell.getRowspan() : this.rows.size() - aLocation.x;
            int dify = this.columns - aLocation.y > aCell.getColspan() ? aCell.getColspan() : this.columns - aLocation.y;
            for (int i = aLocation.x; i < aLocation.x + difx; i++) {
                for (int j = aLocation.y; j < aLocation.y + dify; j++) {
                    if (((Row) this.rows.get(i)).isReserved(j)) {
                        return false;
                    }
                }
            }
            return true;
        }
        if (aLocation.y + aCell.getColspan() > this.columns) {
            return false;
        }
        return true;
    }

    private void assumeTableDefaults(Cell aCell) {
        if (aCell.getBorder() == -1) {
            aCell.setBorder(this.defaultCell.getBorder());
        }
        if (aCell.getBorderWidth() == -1.0f) {
            aCell.setBorderWidth(this.defaultCell.getBorderWidth());
        }
        if (aCell.getBorderColor() == null) {
            aCell.setBorderColor(this.defaultCell.getBorderColor());
        }
        if (aCell.getBackgroundColor() == null) {
            aCell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        }
        if (aCell.getHorizontalAlignment() == -1) {
            aCell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
        }
        if (aCell.getVerticalAlignment() == -1) {
            aCell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
        }
    }

    private void placeCell(ArrayList someRows, Cell aCell, Point aPosition) {
        int rowCount = (aPosition.x + aCell.getRowspan()) - someRows.size();
        assumeTableDefaults(aCell);
        if (aPosition.x + aCell.getRowspan() > someRows.size()) {
            for (int i = 0; i < rowCount; i++) {
                Row row = new Row(this.columns);
                someRows.add(row);
            }
        }
        for (int i2 = aPosition.x + 1; i2 < aPosition.x + aCell.getRowspan(); i2++) {
            if (!((Row) someRows.get(i2)).reserve(aPosition.y, aCell.getColspan())) {
                throw new RuntimeException("addCell - error in reserve");
            }
        }
        Row row2 = (Row) someRows.get(aPosition.x);
        row2.addElement(aCell, aPosition.y);
    }

    private void setCurrentLocationToNextValidPosition(Point aLocation) {
        int i = aLocation.x;
        int j = aLocation.y;
        do {
            if (j + 1 == this.columns) {
                i++;
                j = 0;
            } else {
                j++;
            }
            if (i >= this.rows.size() || j >= this.columns) {
                break;
            }
        } while (((Row) this.rows.get(i)).isReserved(j));
        this.curPosition = new Point(i, j);
    }

    public float[] getWidths(float left, float totalWidth) {
        float wPercentage;
        float[] w = new float[this.columns + 1];
        if (this.locked) {
            wPercentage = (100.0f * this.width) / totalWidth;
        } else {
            wPercentage = this.width;
        }
        switch (this.alignment) {
            case 0:
                w[0] = left;
                break;
            case 1:
            default:
                w[0] = left + ((totalWidth * (100.0f - wPercentage)) / 200.0f);
                break;
            case 2:
                w[0] = left + ((totalWidth * (100.0f - wPercentage)) / 100.0f);
                break;
        }
        float totalWidth2 = (totalWidth * wPercentage) / 100.0f;
        for (int i = 1; i < this.columns; i++) {
            w[i] = w[i - 1] + ((this.widths[i - 1] * totalWidth2) / 100.0f);
        }
        w[this.columns] = w[0] + totalWidth2;
        return w;
    }

    public Iterator iterator() {
        return this.rows.iterator();
    }

    public PdfPTable createPdfPTable() throws BadElementException {
        PdfPCell pcell;
        if (!this.convert2pdfptable) {
            throw new BadElementException("No error, just an old style table");
        }
        setAutoFillEmptyCells(true);
        complete();
        PdfPTable pdfptable = new PdfPTable(this.widths);
        pdfptable.setComplete(this.complete);
        if (isNotAddedYet()) {
            pdfptable.setSkipFirstHeader(true);
        }
        SimpleTable t_evt = new SimpleTable();
        t_evt.cloneNonPositionParameters(this);
        t_evt.setCellspacing(this.cellspacing);
        pdfptable.setTableEvent(t_evt);
        pdfptable.setHeaderRows(this.lastHeaderRow + 1);
        pdfptable.setSplitLate(this.cellsFitPage);
        pdfptable.setKeepTogether(this.tableFitsPage);
        if (!Float.isNaN(this.offset)) {
            pdfptable.setSpacingBefore(this.offset);
        }
        pdfptable.setHorizontalAlignment(this.alignment);
        if (this.locked) {
            pdfptable.setTotalWidth(this.width);
            pdfptable.setLockedWidth(true);
        } else {
            pdfptable.setWidthPercentage(this.width);
        }
        Iterator iterator = iterator();
        while (iterator.hasNext()) {
            Row row = (Row) iterator.next();
            for (int i = 0; i < row.getColumns(); i++) {
                Element cell = (Element) row.getCell(i);
                if (cell != null) {
                    if (cell instanceof Table) {
                        pcell = new PdfPCell(((Table) cell).createPdfPTable());
                    } else if (cell instanceof Cell) {
                        pcell = ((Cell) cell).createPdfPCell();
                        pcell.setPadding(this.cellpadding + (this.cellspacing / 2.0f));
                        SimpleCell c_evt = new SimpleCell(false);
                        c_evt.cloneNonPositionParameters((Cell) cell);
                        c_evt.setSpacing(this.cellspacing * 2.0f);
                        pcell.setCellEvent(c_evt);
                    } else {
                        pcell = new PdfPCell();
                    }
                    pdfptable.addCell(pcell);
                }
            }
        }
        return pdfptable;
    }

    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }

    public void setNotAddedYet(boolean notAddedYet) {
        this.notAddedYet = notAddedYet;
    }

    @Override // com.aowagie.text.LargeElement
    public void flushContent() {
        setNotAddedYet(false);
        ArrayList headerrows = new ArrayList();
        for (int i = 0; i < getLastHeaderRow() + 1; i++) {
            headerrows.add(this.rows.get(i));
        }
        this.rows = headerrows;
    }

    @Override // com.aowagie.text.LargeElement
    public boolean isComplete() {
        return this.complete;
    }

    @Override // com.aowagie.text.LargeElement
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Deprecated
    public Cell getDefaultLayout() {
        return getDefaultCell();
    }

    @Deprecated
    public void setDefaultLayout(Cell value) {
        this.defaultCell = value;
    }
}
