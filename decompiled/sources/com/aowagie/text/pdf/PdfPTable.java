package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.ElementListener;
import com.aowagie.text.Image;
import com.aowagie.text.LargeElement;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.events.PdfPTableEventForwarder;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPTable.class */
public class PdfPTable implements LargeElement {
    public static final int BASECANVAS = 0;
    public static final int BACKGROUNDCANVAS = 1;
    public static final int LINECANVAS = 2;
    public static final int TEXTCANVAS = 3;
    protected PdfPCell[] currentRow;
    protected float[] relativeWidths;
    protected float[] absoluteWidths;
    protected PdfPTableEvent tableEvent;
    protected int headerRows;
    protected float spacingBefore;
    protected float spacingAfter;
    private boolean extendLastRow;
    private boolean headersInEvent;
    private boolean keepTogether;
    private int footerRows;
    protected ArrayList rows = new ArrayList();
    protected float totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    protected int currentRowIdx = 0;
    protected PdfPCell defaultCell = new PdfPCell((Phrase) null);
    protected float totalWidth = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    protected float widthPercentage = 80.0f;
    private int horizontalAlignment = 1;
    private boolean skipFirstHeader = false;
    private boolean skipLastFooter = false;
    protected boolean isColspan = false;
    protected int runDirection = 0;
    private boolean lockedWidth = false;
    private boolean splitRows = true;
    private boolean splitLate = true;
    protected boolean complete = true;
    protected boolean rowCompleted = true;

    protected PdfPTable() {
    }

    public PdfPTable(float[] relativeWidths) {
        if (relativeWidths == null) {
            throw new NullPointerException("The widths array in PdfPTable constructor can not be null.");
        }
        if (relativeWidths.length == 0) {
            throw new IllegalArgumentException("The widths array in PdfPTable constructor can not have zero length.");
        }
        this.relativeWidths = new float[relativeWidths.length];
        System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
        this.absoluteWidths = new float[relativeWidths.length];
        calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }

    public PdfPTable(int numColumns) {
        if (numColumns <= 0) {
            throw new IllegalArgumentException("The number of columns in PdfPTable constructor must be greater than zero.");
        }
        this.relativeWidths = new float[numColumns];
        for (int k = 0; k < numColumns; k++) {
            this.relativeWidths[k] = 1.0f;
        }
        this.absoluteWidths = new float[this.relativeWidths.length];
        calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }

    public PdfPTable(PdfPTable table) {
        copyFormat(table);
        for (int k = 0; k < this.currentRow.length && table.currentRow[k] != null; k++) {
            this.currentRow[k] = new PdfPCell(table.currentRow[k]);
        }
        for (int k2 = 0; k2 < table.rows.size(); k2++) {
            PdfPRow row = (PdfPRow) table.rows.get(k2);
            if (row != null) {
                row = new PdfPRow(row);
            }
            this.rows.add(row);
        }
    }

    public static PdfPTable shallowCopy(PdfPTable table) {
        PdfPTable nt = new PdfPTable();
        nt.copyFormat(table);
        return nt;
    }

    protected void copyFormat(PdfPTable sourceTable) {
        this.relativeWidths = new float[sourceTable.getNumberOfColumns()];
        this.absoluteWidths = new float[sourceTable.getNumberOfColumns()];
        System.arraycopy(sourceTable.relativeWidths, 0, this.relativeWidths, 0, getNumberOfColumns());
        System.arraycopy(sourceTable.absoluteWidths, 0, this.absoluteWidths, 0, getNumberOfColumns());
        this.totalWidth = sourceTable.totalWidth;
        this.totalHeight = sourceTable.totalHeight;
        this.currentRowIdx = 0;
        this.tableEvent = sourceTable.tableEvent;
        this.runDirection = sourceTable.runDirection;
        this.defaultCell = new PdfPCell(sourceTable.defaultCell);
        this.currentRow = new PdfPCell[sourceTable.currentRow.length];
        this.isColspan = sourceTable.isColspan;
        this.splitRows = sourceTable.splitRows;
        this.spacingAfter = sourceTable.spacingAfter;
        this.spacingBefore = sourceTable.spacingBefore;
        this.headerRows = sourceTable.headerRows;
        this.footerRows = sourceTable.footerRows;
        this.lockedWidth = sourceTable.lockedWidth;
        this.extendLastRow = sourceTable.extendLastRow;
        this.headersInEvent = sourceTable.headersInEvent;
        this.widthPercentage = sourceTable.widthPercentage;
        this.splitLate = sourceTable.splitLate;
        this.skipFirstHeader = sourceTable.skipFirstHeader;
        this.skipLastFooter = sourceTable.skipLastFooter;
        this.horizontalAlignment = sourceTable.horizontalAlignment;
        this.keepTogether = sourceTable.keepTogether;
        this.complete = sourceTable.complete;
    }

    public void setWidths(float[] relativeWidths) throws DocumentException {
        if (relativeWidths.length != getNumberOfColumns()) {
            throw new DocumentException("Wrong number of columns.");
        }
        this.relativeWidths = new float[relativeWidths.length];
        System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
        this.absoluteWidths = new float[relativeWidths.length];
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        calculateWidths();
        calculateHeights(true);
    }

    public void setWidths(int[] relativeWidths) throws DocumentException {
        float[] tb = new float[relativeWidths.length];
        for (int k = 0; k < relativeWidths.length; k++) {
            tb[k] = relativeWidths[k];
        }
        setWidths(tb);
    }

    protected void calculateWidths() {
        if (this.totalWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return;
        }
        float total = 0.0f;
        int numCols = getNumberOfColumns();
        for (int k = 0; k < numCols; k++) {
            total += this.relativeWidths[k];
        }
        for (int k2 = 0; k2 < numCols; k2++) {
            this.absoluteWidths[k2] = (this.totalWidth * this.relativeWidths[k2]) / total;
        }
    }

    public void setTotalWidth(float totalWidth) {
        if (this.totalWidth == totalWidth) {
            return;
        }
        this.totalWidth = totalWidth;
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        calculateWidths();
        calculateHeights(true);
    }

    public void setTotalWidth(float[] columnWidth) throws DocumentException {
        if (columnWidth.length != getNumberOfColumns()) {
            throw new DocumentException("Wrong number of columns.");
        }
        this.totalWidth = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        for (float element : columnWidth) {
            this.totalWidth += element;
        }
        setWidths(columnWidth);
    }

    public void setWidthPercentage(float[] columnWidth, Rectangle pageSize) throws DocumentException {
        if (columnWidth.length != getNumberOfColumns()) {
            throw new IllegalArgumentException("Wrong number of columns.");
        }
        float totalWidth = 0.0f;
        for (float element : columnWidth) {
            totalWidth += element;
        }
        this.widthPercentage = (totalWidth / (pageSize.getRight() - pageSize.getLeft())) * 100.0f;
        setWidths(columnWidth);
    }

    public float getTotalWidth() {
        return this.totalWidth;
    }

    public float calculateHeights(boolean firsttime) {
        if (this.totalWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        for (int k = 0; k < this.rows.size(); k++) {
            this.totalHeight += getRowHeight(k, firsttime);
        }
        return this.totalHeight;
    }

    public void calculateHeightsFast() {
        calculateHeights(false);
    }

    public PdfPCell getDefaultCell() {
        return this.defaultCell;
    }

    public void addCell(PdfPCell cell) {
        this.rowCompleted = false;
        PdfPCell ncell = new PdfPCell(cell);
        int colspan = Math.min(Math.max(ncell.getColspan(), 1), this.currentRow.length - this.currentRowIdx);
        ncell.setColspan(colspan);
        if (colspan != 1) {
            this.isColspan = true;
        }
        int rdir = ncell.getRunDirection();
        if (rdir == 0) {
            ncell.setRunDirection(this.runDirection);
        }
        skipColsWithRowspanAbove();
        boolean cellAdded = false;
        if (this.currentRowIdx < this.currentRow.length) {
            this.currentRow[this.currentRowIdx] = ncell;
            this.currentRowIdx += colspan;
            cellAdded = true;
        }
        skipColsWithRowspanAbove();
        if (this.currentRowIdx >= this.currentRow.length) {
            int numCols = getNumberOfColumns();
            if (this.runDirection == 3) {
                PdfPCell[] rtlRow = new PdfPCell[numCols];
                int rev = this.currentRow.length;
                int k = 0;
                while (k < this.currentRow.length) {
                    PdfPCell rcell = this.currentRow[k];
                    int cspan = rcell.getColspan();
                    rev -= cspan;
                    rtlRow[rev] = rcell;
                    k = k + (cspan - 1) + 1;
                }
                this.currentRow = rtlRow;
            }
            PdfPRow row = new PdfPRow(this.currentRow);
            if (this.totalWidth > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                row.setWidths(this.absoluteWidths);
                this.totalHeight += row.getMaxHeights();
            }
            this.rows.add(row);
            this.currentRow = new PdfPCell[numCols];
            this.currentRowIdx = 0;
            this.rowCompleted = true;
        }
        if (!cellAdded) {
            this.currentRow[this.currentRowIdx] = ncell;
            this.currentRowIdx += colspan;
        }
    }

    private void skipColsWithRowspanAbove() {
        int direction = 1;
        if (this.runDirection == 3) {
            direction = -1;
        }
        while (rowSpanAbove(this.rows.size(), this.currentRowIdx)) {
            this.currentRowIdx += direction;
        }
    }

    boolean rowSpanAbove(int currRow, int currCol) {
        PdfPCell aboveCell;
        PdfPCell aboveCell2;
        if (currCol >= getNumberOfColumns() || currCol < 0 || currRow == 0) {
            return false;
        }
        int row = currRow - 1;
        PdfPRow aboveRow = (PdfPRow) this.rows.get(row);
        if (aboveRow == null) {
            return false;
        }
        PdfPCell pdfPCell = aboveRow.getCells()[currCol];
        while (true) {
            aboveCell = pdfPCell;
            if (aboveCell != null || row <= 0) {
                break;
            }
            row--;
            aboveRow = (PdfPRow) this.rows.get(row);
            if (aboveRow == null) {
                return false;
            }
            pdfPCell = aboveRow.getCells()[currCol];
        }
        int distance = currRow - row;
        if (aboveCell == null) {
            int col = currCol - 1;
            PdfPCell pdfPCell2 = aboveRow.getCells()[col];
            while (true) {
                aboveCell2 = pdfPCell2;
                if (aboveCell2 != null || row <= 0) {
                    break;
                }
                col--;
                pdfPCell2 = aboveRow.getCells()[col];
            }
            return aboveCell2 != null && aboveCell2.getRowspan() > distance;
        }
        if (aboveCell.getRowspan() == 1 && distance > 1) {
            int col2 = currCol - 1;
            PdfPRow aboveRow2 = (PdfPRow) this.rows.get(row + 1);
            distance--;
            PdfPCell pdfPCell3 = aboveRow2.getCells()[col2];
            while (true) {
                aboveCell = pdfPCell3;
                if (aboveCell != null || col2 <= 0) {
                    break;
                }
                col2--;
                pdfPCell3 = aboveRow2.getCells()[col2];
            }
        }
        return aboveCell != null && aboveCell.getRowspan() > distance;
    }

    public void addCell(String text) {
        addCell(new Phrase(text));
    }

    public void addCell(PdfPTable table) {
        this.defaultCell.setTable(table);
        addCell(this.defaultCell);
        this.defaultCell.setTable(null);
    }

    public void addCell(Image image) {
        this.defaultCell.setImage(image);
        addCell(this.defaultCell);
        this.defaultCell.setImage(null);
    }

    public void addCell(Phrase phrase) {
        this.defaultCell.setPhrase(phrase);
        addCell(this.defaultCell);
        this.defaultCell.setPhrase(null);
    }

    public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        return writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvases);
    }

    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        int rowEnd2;
        int colStart2;
        int colEnd2;
        if (this.totalWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            throw new RuntimeException("The table width must be greater than zero.");
        }
        int totalRows = this.rows.size();
        if (rowStart < 0) {
            rowStart = 0;
        }
        if (rowEnd < 0) {
            rowEnd2 = totalRows;
        } else {
            rowEnd2 = Math.min(rowEnd, totalRows);
        }
        if (rowStart >= rowEnd2) {
            return yPos;
        }
        int totalCols = getNumberOfColumns();
        if (colStart < 0) {
            colStart2 = 0;
        } else {
            colStart2 = Math.min(colStart, totalCols);
        }
        if (colEnd < 0) {
            colEnd2 = totalCols;
        } else {
            colEnd2 = Math.min(colEnd, totalCols);
        }
        for (int k = rowStart; k < rowEnd2; k++) {
            PdfPRow row = (PdfPRow) this.rows.get(k);
            if (row != null) {
                row.writeCells(colStart2, colEnd2, xPos, yPos, canvases);
                yPos -= row.getMaxHeights();
            }
        }
        if (this.tableEvent != null && colStart2 == 0 && colEnd2 == totalCols) {
            float[] heights = new float[(rowEnd2 - rowStart) + 1];
            heights[0] = yPos;
            for (int k2 = rowStart; k2 < rowEnd2; k2++) {
                PdfPRow row2 = (PdfPRow) this.rows.get(k2);
                float hr = 0.0f;
                if (row2 != null) {
                    hr = row2.getMaxHeights();
                }
                heights[(k2 - rowStart) + 1] = heights[k2 - rowStart] - hr;
            }
            this.tableEvent.tableLayout(this, getEventWidths(xPos, rowStart, rowEnd2, this.headersInEvent), heights, this.headersInEvent ? this.headerRows : 0, rowStart, canvases);
        }
        return yPos;
    }

    public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
        return writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvas);
    }

    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
        int colStart2;
        int colEnd2;
        int totalCols = getNumberOfColumns();
        if (colStart < 0) {
            colStart2 = 0;
        } else {
            colStart2 = Math.min(colStart, totalCols);
        }
        if (colEnd < 0) {
            colEnd2 = totalCols;
        } else {
            colEnd2 = Math.min(colEnd, totalCols);
        }
        boolean clip = (colStart2 == 0 && colEnd2 == totalCols) ? false : true;
        if (clip) {
            float w = 0.0f;
            for (int k = colStart2; k < colEnd2; k++) {
                w += this.absoluteWidths[k];
            }
            canvas.saveState();
            float lx = colStart2 == 0 ? 10000.0f : ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            float rx = colEnd2 == totalCols ? 10000.0f : ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            canvas.rectangle(xPos - lx, -10000.0f, w + lx + rx, 20000.0f);
            canvas.clip();
            canvas.newPath();
        }
        PdfContentByte[] canvases = beginWritingRows(canvas);
        float y = writeSelectedRows(colStart2, colEnd2, rowStart, rowEnd, xPos, yPos, canvases);
        endWritingRows(canvases);
        if (clip) {
            canvas.restoreState();
        }
        return y;
    }

    public static PdfContentByte[] beginWritingRows(PdfContentByte canvas) {
        return new PdfContentByte[]{canvas, canvas.getDuplicate(), canvas.getDuplicate(), canvas.getDuplicate()};
    }

    public static void endWritingRows(PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[0];
        canvas.saveState();
        canvas.add(canvases[1]);
        canvas.restoreState();
        canvas.saveState();
        canvas.setLineCap(2);
        canvas.resetRGBColorStroke();
        canvas.add(canvases[2]);
        canvas.restoreState();
        canvas.add(canvases[3]);
    }

    public int size() {
        return this.rows.size();
    }

    public float getTotalHeight() {
        return this.totalHeight;
    }

    public float getRowHeight(int idx) {
        return getRowHeight(idx, false);
    }

    public float getRowHeight(int idx, boolean firsttime) {
        PdfPRow row;
        if (this.totalWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO || idx < 0 || idx >= this.rows.size() || (row = (PdfPRow) this.rows.get(idx)) == null) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (firsttime) {
            row.setWidths(this.absoluteWidths);
        }
        float height = row.getMaxHeights();
        for (int i = 0; i < this.relativeWidths.length; i++) {
            if (rowSpanAbove(idx, i)) {
                int rs = 1;
                while (rowSpanAbove(idx - rs, i)) {
                    rs++;
                }
                PdfPRow tmprow = (PdfPRow) this.rows.get(idx - rs);
                PdfPCell cell = tmprow.getCells()[i];
                float tmp = 0.0f;
                if (cell.getRowspan() == rs + 1) {
                    tmp = cell.getMaxHeight();
                    while (rs > 0) {
                        tmp -= getRowHeight(idx - rs);
                        rs--;
                    }
                }
                if (tmp > height) {
                    height = tmp;
                }
            }
        }
        row.setMaxHeights(height);
        return height;
    }

    public float getRowspanHeight(int rowIndex, int cellIndex) {
        PdfPRow row;
        PdfPCell cell;
        if (this.totalWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO || rowIndex < 0 || rowIndex >= this.rows.size() || (row = (PdfPRow) this.rows.get(rowIndex)) == null || cellIndex >= row.getCells().length || (cell = row.getCells()[cellIndex]) == null) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        float rowspanHeight = 0.0f;
        for (int j = 0; j < cell.getRowspan(); j++) {
            rowspanHeight += getRowHeight(rowIndex + j);
        }
        return rowspanHeight;
    }

    public float getHeaderHeight() {
        float total = 0.0f;
        int size = Math.min(this.rows.size(), this.headerRows);
        for (int k = 0; k < size; k++) {
            PdfPRow row = (PdfPRow) this.rows.get(k);
            if (row != null) {
                total += row.getMaxHeights();
            }
        }
        return total;
    }

    public float getFooterHeight() {
        float total = 0.0f;
        int start = Math.max(0, this.headerRows - this.footerRows);
        int size = Math.min(this.rows.size(), this.headerRows);
        for (int k = start; k < size; k++) {
            PdfPRow row = (PdfPRow) this.rows.get(k);
            if (row != null) {
                total += row.getMaxHeights();
            }
        }
        return total;
    }

    public boolean deleteRow(int rowNumber) {
        PdfPRow row;
        if (rowNumber < 0 || rowNumber >= this.rows.size()) {
            return false;
        }
        if (this.totalWidth > ColumnText.GLOBAL_SPACE_CHAR_RATIO && (row = (PdfPRow) this.rows.get(rowNumber)) != null) {
            this.totalHeight -= row.getMaxHeights();
        }
        this.rows.remove(rowNumber);
        if (rowNumber < this.headerRows) {
            this.headerRows--;
            if (rowNumber >= this.headerRows - this.footerRows) {
                this.footerRows--;
                return true;
            }
            return true;
        }
        return true;
    }

    public boolean deleteLastRow() {
        return deleteRow(this.rows.size() - 1);
    }

    public void deleteBodyRows() {
        ArrayList rows2 = new ArrayList();
        for (int k = 0; k < this.headerRows; k++) {
            rows2.add(this.rows.get(k));
        }
        this.rows = rows2;
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        if (this.totalWidth > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            this.totalHeight = getHeaderHeight();
        }
    }

    public int getNumberOfColumns() {
        return this.relativeWidths.length;
    }

    public int getHeaderRows() {
        return this.headerRows;
    }

    public void setHeaderRows(int headerRows) {
        if (headerRows < 0) {
            headerRows = 0;
        }
        this.headerRows = headerRows;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        return new ArrayList();
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return 23;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException e) {
            return false;
        }
    }

    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public PdfPRow getRow(int idx) {
        return (PdfPRow) this.rows.get(idx);
    }

    public ArrayList getRows() {
        return this.rows;
    }

    public ArrayList getRows(int start, int end) {
        PdfPCell replaceCell;
        ArrayList list = new ArrayList();
        if (start < 0 || end > size()) {
            return list;
        }
        PdfPRow firstRow = adjustCellsInRow(start, end);
        int colIndex = 0;
        while (colIndex < getNumberOfColumns()) {
            int rowIndex = start;
            while (true) {
                int i = rowIndex;
                rowIndex--;
                if (!rowSpanAbove(i, colIndex)) {
                    break;
                }
                PdfPRow row = getRow(rowIndex);
                if (row != null && (replaceCell = row.getCells()[colIndex]) != null) {
                    firstRow.getCells()[colIndex] = new PdfPCell(replaceCell);
                    float extra = 0.0f;
                    int stop = Math.min(rowIndex + replaceCell.getRowspan(), end);
                    for (int j = start + 1; j < stop; j++) {
                        extra += getRowHeight(j);
                    }
                    firstRow.setExtraHeight(colIndex, extra);
                    float diff = (getRowspanHeight(rowIndex, colIndex) - getRowHeight(start)) - extra;
                    firstRow.getCells()[colIndex].consumeHeight(diff);
                }
            }
            PdfPCell cell = firstRow.getCells()[colIndex];
            if (cell == null) {
                colIndex++;
            } else {
                colIndex += cell.getColspan();
            }
        }
        list.add(firstRow);
        for (int i2 = start + 1; i2 < end; i2++) {
            list.add(adjustCellsInRow(i2, end));
        }
        return list;
    }

    protected PdfPRow adjustCellsInRow(int start, int end) {
        PdfPRow row = new PdfPRow(getRow(start));
        row.initExtraHeights();
        PdfPCell[] cells = row.getCells();
        for (int i = 0; i < cells.length; i++) {
            PdfPCell cell = cells[i];
            if (cell != null && cell.getRowspan() != 1) {
                int stop = Math.min(end, start + cell.getRowspan());
                float extra = 0.0f;
                for (int k = start + 1; k < stop; k++) {
                    extra += getRowHeight(k);
                }
                row.setExtraHeight(i, extra);
            }
        }
        return row;
    }

    public void setTableEvent(PdfPTableEvent event) {
        if (event == null) {
            this.tableEvent = null;
            return;
        }
        if (this.tableEvent == null) {
            this.tableEvent = event;
            return;
        }
        if (this.tableEvent instanceof PdfPTableEventForwarder) {
            ((PdfPTableEventForwarder) this.tableEvent).addTableEvent(event);
            return;
        }
        PdfPTableEventForwarder forward = new PdfPTableEventForwarder();
        forward.addTableEvent(this.tableEvent);
        forward.addTableEvent(event);
        this.tableEvent = forward;
    }

    public PdfPTableEvent getTableEvent() {
        return this.tableEvent;
    }

    public float[] getAbsoluteWidths() {
        return this.absoluteWidths;
    }

    float[][] getEventWidths(float xPos, int firstRow, int lastRow, boolean includeHeaders) {
        if (includeHeaders) {
            firstRow = Math.max(firstRow, this.headerRows);
            lastRow = Math.max(lastRow, this.headerRows);
        }
        float[][] widths = new float[((includeHeaders ? this.headerRows : 0) + lastRow) - firstRow][];
        if (this.isColspan) {
            int n = 0;
            if (includeHeaders) {
                for (int k = 0; k < this.headerRows; k++) {
                    PdfPRow row = (PdfPRow) this.rows.get(k);
                    if (row == null) {
                        n++;
                    } else {
                        int i = n;
                        n++;
                        widths[i] = row.getEventWidth(xPos);
                    }
                }
            }
            while (firstRow < lastRow) {
                PdfPRow row2 = (PdfPRow) this.rows.get(firstRow);
                if (row2 == null) {
                    n++;
                } else {
                    int i2 = n;
                    n++;
                    widths[i2] = row2.getEventWidth(xPos);
                }
                firstRow++;
            }
        } else {
            int numCols = getNumberOfColumns();
            float[] width = new float[numCols + 1];
            width[0] = xPos;
            for (int k2 = 0; k2 < numCols; k2++) {
                width[k2 + 1] = width[k2] + this.absoluteWidths[k2];
            }
            for (int k3 = 0; k3 < widths.length; k3++) {
                widths[k3] = width;
            }
        }
        return widths;
    }

    public boolean isSkipFirstHeader() {
        return this.skipFirstHeader;
    }

    public boolean isSkipLastFooter() {
        return this.skipLastFooter;
    }

    public void setSkipFirstHeader(boolean skipFirstHeader) {
        this.skipFirstHeader = skipFirstHeader;
    }

    public void setSkipLastFooter(boolean skipLastFooter) {
        this.skipLastFooter = skipLastFooter;
    }

    public void setRunDirection(int runDirection) {
        switch (runDirection) {
            case 0:
            case 1:
            case 2:
            case 3:
                this.runDirection = runDirection;
                return;
            default:
                throw new RuntimeException("Invalid run direction: " + runDirection);
        }
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public boolean isLockedWidth() {
        return this.lockedWidth;
    }

    public void setLockedWidth(boolean lockedWidth) {
        this.lockedWidth = lockedWidth;
    }

    public boolean isSplitRows() {
        return this.splitRows;
    }

    public void setSplitRows(boolean splitRows) {
        this.splitRows = splitRows;
    }

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    public float spacingBefore() {
        return this.spacingBefore;
    }

    public float spacingAfter() {
        return this.spacingAfter;
    }

    public boolean isExtendLastRow() {
        return this.extendLastRow;
    }

    public void setExtendLastRow(boolean extendLastRow) {
        this.extendLastRow = extendLastRow;
    }

    public boolean isHeadersInEvent() {
        return this.headersInEvent;
    }

    public void setHeadersInEvent(boolean headersInEvent) {
        this.headersInEvent = headersInEvent;
    }

    public boolean isSplitLate() {
        return this.splitLate;
    }

    public void setSplitLate(boolean splitLate) {
        this.splitLate = splitLate;
    }

    public void setKeepTogether(boolean keepTogether) {
        this.keepTogether = keepTogether;
    }

    public boolean getKeepTogether() {
        return this.keepTogether;
    }

    public int getFooterRows() {
        return this.footerRows;
    }

    public void setFooterRows(int footerRows) {
        if (footerRows < 0) {
            footerRows = 0;
        }
        this.footerRows = footerRows;
    }

    public void completeRow() {
        while (!this.rowCompleted) {
            addCell(this.defaultCell);
        }
    }

    @Override // com.aowagie.text.LargeElement
    public void flushContent() {
        deleteBodyRows();
        setSkipFirstHeader(true);
    }

    @Override // com.aowagie.text.LargeElement
    public boolean isComplete() {
        return this.complete;
    }

    @Override // com.aowagie.text.LargeElement
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
