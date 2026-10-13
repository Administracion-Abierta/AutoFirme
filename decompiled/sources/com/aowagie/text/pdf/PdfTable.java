package com.aowagie.text.pdf;

import com.aowagie.text.Cell;
import com.aowagie.text.Rectangle;
import com.aowagie.text.Row;
import com.aowagie.text.Table;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfTable.class */
class PdfTable extends Rectangle {
    private final int columns;
    private final ArrayList headercells;
    private final ArrayList cells;
    private final Table table;
    private final float[] positions;

    PdfTable(Table table, float left, float right, float top) {
        super(left, top, right, top);
        this.table = table;
        table.complete();
        cloneNonPositionParameters(table);
        this.columns = table.getColumns();
        this.positions = table.getWidths(left, right - left);
        setLeft(this.positions[0]);
        setRight(this.positions[this.positions.length - 1]);
        this.headercells = new ArrayList();
        this.cells = new ArrayList();
        updateRowAdditionsInternal();
    }

    /* JADX WARN: Code duplicated, block: B:28:0x011c A[Catch: ArrayIndexOutOfBoundsException -> 0x0138, TryCatch #1 {ArrayIndexOutOfBoundsException -> 0x0138, blocks: (B:26:0x00ff, B:28:0x011c), top: B:51:0x00ff }] */
    /* JADX WARN: Code duplicated, block: B:51:0x00ff A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private void updateRowAdditionsInternal() {
        int prevRows = rows();
        int rowNumber = 0;
        int groupNumber = 0;
        int firstDataRow = this.table.getLastHeaderRow() + 1;
        ArrayList newCells = new ArrayList();
        int rows = this.table.size() + 1;
        float[] offsets = new float[rows];
        for (int i = 0; i < rows; i++) {
            offsets[i] = getBottom();
        }
        for (Row row : this.table) {
            boolean groupChange = false;
            if (row.isEmpty()) {
                if (rowNumber < rows - 1 && offsets[rowNumber + 1] > offsets[rowNumber]) {
                    offsets[rowNumber + 1] = offsets[rowNumber];
                }
            } else {
                for (int i2 = 0; i2 < row.getColumns(); i2++) {
                    Cell cell = (Cell) row.getCell(i2);
                    if (cell != null) {
                        PdfCell currentCell = new PdfCell(cell, rowNumber + prevRows, this.positions[i2], this.positions[i2 + cell.getColspan()], offsets[rowNumber], cellspacing(), cellpadding());
                        if (rowNumber < firstDataRow) {
                            currentCell.setHeader();
                            this.headercells.add(currentCell);
                            if (this.table.isNotAddedYet()) {
                                try {
                                    if ((offsets[rowNumber] - currentCell.getHeight()) - cellpadding() < offsets[rowNumber + currentCell.rowspan()]) {
                                        offsets[rowNumber + currentCell.rowspan()] = (offsets[rowNumber] - currentCell.getHeight()) - cellpadding();
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    if (offsets[rowNumber] - currentCell.getHeight() < offsets[rows - 1]) {
                                        offsets[rows - 1] = offsets[rowNumber] - currentCell.getHeight();
                                    }
                                }
                                currentCell.setGroupNumber(groupNumber);
                                groupChange |= cell.getGroupChange();
                                newCells.add(currentCell);
                            }
                        } else {
                            if ((offsets[rowNumber] - currentCell.getHeight()) - cellpadding() < offsets[rowNumber + currentCell.rowspan()]) {
                                offsets[rowNumber + currentCell.rowspan()] = (offsets[rowNumber] - currentCell.getHeight()) - cellpadding();
                            }
                            currentCell.setGroupNumber(groupNumber);
                            groupChange |= cell.getGroupChange();
                            newCells.add(currentCell);
                        }
                    }
                }
            }
            rowNumber++;
            if (groupChange) {
                groupNumber++;
            }
        }
        int n = newCells.size();
        for (int i3 = 0; i3 < n; i3++) {
            PdfCell currentCell2 = (PdfCell) newCells.get(i3);
            try {
                currentCell2.setBottom(offsets[(currentCell2.rownumber() - prevRows) + currentCell2.rowspan()]);
            } catch (ArrayIndexOutOfBoundsException e2) {
                currentCell2.setBottom(offsets[rows - 1]);
            }
        }
        this.cells.addAll(newCells);
        setBottom(offsets[rows - 1]);
    }

    private int rows() {
        if (this.cells.isEmpty()) {
            return 0;
        }
        return ((PdfCell) this.cells.get(this.cells.size() - 1)).rownumber() + 1;
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public int type() {
        return 22;
    }

    ArrayList getHeaderCells() {
        return this.headercells;
    }

    ArrayList getCells() {
        return this.cells;
    }

    int columns() {
        return this.columns;
    }

    private final float cellpadding() {
        return this.table.getPadding();
    }

    final float cellspacing() {
        return this.table.getSpacing();
    }

    final boolean hasToFitPageCells() {
        return this.table.isCellsFitPage();
    }

    public float getOffset() {
        return this.table.getOffset();
    }
}
