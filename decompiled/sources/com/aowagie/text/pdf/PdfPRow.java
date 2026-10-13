package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.Rectangle;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPRow.class */
class PdfPRow {
    static final float BOTTOM_LIMIT = -1.0737418E9f;
    static final float RIGHT_LIMIT = 20000.0f;
    private final PdfPCell[] cells;
    private float[] widths;
    private float[] extraHeights;
    private float maxHeight;
    private boolean calculated;
    private int[] canvasesPos;

    public PdfPRow(PdfPCell[] cells) {
        this.maxHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.calculated = false;
        this.cells = cells;
        this.widths = new float[cells.length];
        initExtraHeights();
    }

    public PdfPRow(PdfPRow row) {
        this.maxHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.calculated = false;
        this.maxHeight = row.maxHeight;
        this.calculated = row.calculated;
        this.cells = new PdfPCell[row.cells.length];
        for (int k = 0; k < this.cells.length; k++) {
            if (row.cells[k] != null) {
                this.cells[k] = new PdfPCell(row.cells[k]);
            }
        }
        this.widths = new float[this.cells.length];
        System.arraycopy(row.widths, 0, this.widths, 0, this.cells.length);
        initExtraHeights();
    }

    public boolean setWidths(float[] widths) {
        if (widths.length != this.cells.length) {
            return false;
        }
        System.arraycopy(widths, 0, this.widths, 0, this.cells.length);
        float total = 0.0f;
        this.calculated = false;
        int k = 0;
        while (k < widths.length) {
            PdfPCell cell = this.cells[k];
            if (cell == null) {
                total += widths[k];
            } else {
                cell.setLeft(total);
                int last = k + cell.getColspan();
                while (k < last) {
                    total += widths[k];
                    k++;
                }
                k--;
                cell.setRight(total);
                cell.setTop(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            }
            k++;
        }
        return true;
    }

    public void initExtraHeights() {
        this.extraHeights = new float[this.cells.length];
        for (int i = 0; i < this.extraHeights.length; i++) {
            this.extraHeights[i] = 0.0f;
        }
    }

    public void setExtraHeight(int cell, float height) {
        if (cell < 0 || cell >= this.cells.length) {
            return;
        }
        this.extraHeights[cell] = height;
    }

    private float calculateHeights() {
        this.maxHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        for (PdfPCell cell : this.cells) {
            if (cell != null) {
                float height = cell.getMaxHeight();
                if (height > this.maxHeight && cell.getRowspan() == 1) {
                    this.maxHeight = height;
                }
            }
        }
        this.calculated = true;
        return this.maxHeight;
    }

    private void writeBorderAndBackground(float xPos, float yPos, float currentMaxHeight, PdfPCell cell, PdfContentByte[] canvases) {
        Color background = cell.getBackgroundColor();
        if (background != null || cell.hasBorders()) {
            float right = cell.getRight() + xPos;
            float top = cell.getTop() + yPos;
            float left = cell.getLeft() + xPos;
            float bottom = top - currentMaxHeight;
            if (background != null) {
                PdfContentByte backgr = canvases[1];
                backgr.setColorFill(background);
                backgr.rectangle(left, bottom, right - left, top - bottom);
                backgr.fill();
            }
            if (cell.hasBorders()) {
                Rectangle newRect = new Rectangle(left, bottom, right, top);
                newRect.cloneNonPositionParameters(cell);
                newRect.setBackgroundColor(null);
                PdfContentByte lineCanvas = canvases[2];
                lineCanvas.rectangle(newRect);
            }
        }
    }

    private void saveAndRotateCanvases(PdfContentByte[] canvases, float a, float b, float c, float d, float e, float f) {
        if (this.canvasesPos == null) {
            this.canvasesPos = new int[8];
        }
        for (int k = 0; k < 4; k++) {
            ByteBuffer bb = canvases[k].getInternalBuffer();
            this.canvasesPos[k * 2] = bb.size();
            canvases[k].saveState();
            canvases[k].concatCTM(a, b, c, d, e, f);
            this.canvasesPos[(k * 2) + 1] = bb.size();
        }
    }

    private void restoreCanvases(PdfContentByte[] canvases) {
        for (int k = 0; k < 4; k++) {
            ByteBuffer bb = canvases[k].getInternalBuffer();
            int p1 = bb.size();
            canvases[k].restoreState();
            if (p1 == this.canvasesPos[(k * 2) + 1]) {
                bb.setSize(this.canvasesPos[k * 2]);
            }
        }
    }

    public static float setColumn(ColumnText ct, float left, float bottom, float right, float top) {
        if (left > right) {
            right = left;
        }
        if (bottom > top) {
            top = bottom;
        }
        ct.setSimpleColumn(left, bottom, right, top);
        return top;
    }

    public void writeCells(int colStart, int colEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        int colEnd2;
        float pivotX;
        float pivotX2;
        if (!this.calculated) {
            calculateHeights();
        }
        if (colEnd < 0) {
            colEnd2 = this.cells.length;
        } else {
            colEnd2 = Math.min(colEnd, this.cells.length);
        }
        if (colStart < 0) {
            colStart = 0;
        }
        if (colStart >= colEnd2) {
            return;
        }
        int newStart = colStart;
        while (newStart >= 0 && this.cells[newStart] == null) {
            if (newStart > 0) {
                xPos -= this.widths[newStart - 1];
            }
            newStart--;
        }
        if (newStart < 0) {
            newStart = 0;
        }
        if (this.cells[newStart] != null) {
            xPos -= this.cells[newStart].getLeft();
        }
        for (int k = newStart; k < colEnd2; k++) {
            PdfPCell cell = this.cells[k];
            if (cell != null) {
                float currentMaxHeight = this.maxHeight + this.extraHeights[k];
                writeBorderAndBackground(xPos, yPos, currentMaxHeight, cell, canvases);
                Image img = cell.getImage();
                float tly = (cell.getTop() + yPos) - cell.getEffectivePaddingTop();
                if (cell.getHeight() <= currentMaxHeight) {
                    switch (cell.getVerticalAlignment()) {
                        case 5:
                            tly = ((cell.getTop() + yPos) + ((cell.getHeight() - currentMaxHeight) / 2.0f)) - cell.getEffectivePaddingTop();
                            break;
                        case 6:
                            tly = (((cell.getTop() + yPos) - currentMaxHeight) + cell.getHeight()) - cell.getEffectivePaddingTop();
                            break;
                    }
                }
                if (img != null) {
                    if (cell.getRotation() != 0) {
                        img = Image.getInstance(img);
                        img.setRotation(img.getImageRotation() + ((float) ((((double) cell.getRotation()) * 3.141592653589793d) / 180.0d)));
                    }
                    boolean vf = false;
                    if (cell.getHeight() > currentMaxHeight) {
                        img.scalePercent(100.0f);
                        float scale = ((currentMaxHeight - cell.getEffectivePaddingTop()) - cell.getEffectivePaddingBottom()) / img.getScaledHeight();
                        img.scalePercent(scale * 100.0f);
                        vf = true;
                    }
                    float left = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                    if (vf) {
                        switch (cell.getHorizontalAlignment()) {
                            case 1:
                                left = xPos + (((((cell.getLeft() + cell.getEffectivePaddingLeft()) + cell.getRight()) - cell.getEffectivePaddingRight()) - img.getScaledWidth()) / 2.0f);
                                break;
                            case 2:
                                left = ((xPos + cell.getRight()) - cell.getEffectivePaddingRight()) - img.getScaledWidth();
                                break;
                        }
                        tly = (cell.getTop() + yPos) - cell.getEffectivePaddingTop();
                    }
                    img.setAbsolutePosition(left, tly - img.getScaledHeight());
                    try {
                        canvases[3].addImage(img);
                    } catch (DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                } else if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                    float netWidth = (currentMaxHeight - cell.getEffectivePaddingTop()) - cell.getEffectivePaddingBottom();
                    float netHeight = (cell.getWidth() - cell.getEffectivePaddingLeft()) - cell.getEffectivePaddingRight();
                    ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    ct.setCanvases(canvases);
                    ct.setSimpleColumn(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, netWidth + 0.001f, -netHeight);
                    try {
                        ct.go(true);
                        float calcHeight = -ct.getYLine();
                        if (netWidth <= ColumnText.GLOBAL_SPACE_CHAR_RATIO || netHeight <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            calcHeight = 0.0f;
                        }
                        if (calcHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            if (cell.isUseDescender()) {
                                calcHeight -= ct.getDescender();
                            }
                            ColumnText ct2 = ColumnText.duplicate(cell.getColumn());
                            ct2.setCanvases(canvases);
                            ct2.setSimpleColumn(-0.003f, -0.001f, netWidth + 0.003f, calcHeight);
                            if (cell.getRotation() == 90) {
                                float pivotY = ((cell.getTop() + yPos) - currentMaxHeight) + cell.getEffectivePaddingBottom();
                                switch (cell.getVerticalAlignment()) {
                                    case 5:
                                        pivotX2 = cell.getLeft() + xPos + ((((cell.getWidth() + cell.getEffectivePaddingLeft()) - cell.getEffectivePaddingRight()) + calcHeight) / 2.0f);
                                        break;
                                    case 6:
                                        pivotX2 = ((cell.getLeft() + xPos) + cell.getWidth()) - cell.getEffectivePaddingRight();
                                        break;
                                    default:
                                        pivotX2 = cell.getLeft() + xPos + cell.getEffectivePaddingLeft() + calcHeight;
                                        break;
                                }
                                saveAndRotateCanvases(canvases, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, -1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, pivotX2, pivotY);
                            } else {
                                float pivotY2 = (cell.getTop() + yPos) - cell.getEffectivePaddingTop();
                                switch (cell.getVerticalAlignment()) {
                                    case 5:
                                        pivotX = cell.getLeft() + xPos + ((((cell.getWidth() + cell.getEffectivePaddingLeft()) - cell.getEffectivePaddingRight()) - calcHeight) / 2.0f);
                                        break;
                                    case 6:
                                        pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                                        break;
                                    default:
                                        pivotX = (((cell.getLeft() + xPos) + cell.getWidth()) - cell.getEffectivePaddingRight()) - calcHeight;
                                        break;
                                }
                                saveAndRotateCanvases(canvases, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -1.0f, 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, pivotX, pivotY2);
                            }
                            try {
                                try {
                                    ct2.go();
                                    restoreCanvases(canvases);
                                } catch (DocumentException e2) {
                                    throw new ExceptionConverter(e2);
                                }
                            } catch (Throwable th) {
                                restoreCanvases(canvases);
                                throw th;
                            }
                        }
                    } catch (DocumentException e3) {
                        throw new ExceptionConverter(e3);
                    }
                } else {
                    float fixedHeight = cell.getFixedHeight();
                    float rightLimit = (cell.getRight() + xPos) - cell.getEffectivePaddingRight();
                    float leftLimit = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                    if (cell.isNoWrap()) {
                        switch (cell.getHorizontalAlignment()) {
                            case 1:
                                rightLimit += 10000.0f;
                                leftLimit -= 10000.0f;
                                break;
                            case 2:
                                if (cell.getRotation() == 180) {
                                    rightLimit += RIGHT_LIMIT;
                                } else {
                                    leftLimit -= RIGHT_LIMIT;
                                }
                                break;
                            default:
                                if (cell.getRotation() == 180) {
                                    leftLimit -= RIGHT_LIMIT;
                                } else {
                                    rightLimit += RIGHT_LIMIT;
                                }
                                break;
                        }
                    }
                    ColumnText ct3 = ColumnText.duplicate(cell.getColumn());
                    ct3.setCanvases(canvases);
                    float bry = tly - ((currentMaxHeight - cell.getEffectivePaddingTop()) - cell.getEffectivePaddingBottom());
                    if (fixedHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO && cell.getHeight() > currentMaxHeight) {
                        tly = (cell.getTop() + yPos) - cell.getEffectivePaddingTop();
                        bry = ((cell.getTop() + yPos) - currentMaxHeight) + cell.getEffectivePaddingBottom();
                    }
                    if ((tly > bry || ct3.zeroHeightElement()) && leftLimit < rightLimit) {
                        ct3.setSimpleColumn(leftLimit, bry - 0.001f, rightLimit, tly);
                        if (cell.getRotation() == 180) {
                            float shx = leftLimit + rightLimit;
                            float shy = (((yPos + yPos) - currentMaxHeight) + cell.getEffectivePaddingBottom()) - cell.getEffectivePaddingTop();
                            saveAndRotateCanvases(canvases, -1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -1.0f, shx, shy);
                        }
                        try {
                            try {
                                ct3.go();
                                if (cell.getRotation() == 180) {
                                    restoreCanvases(canvases);
                                }
                            } catch (Throwable th2) {
                                if (cell.getRotation() == 180) {
                                    restoreCanvases(canvases);
                                }
                                throw th2;
                            }
                        } catch (DocumentException e4) {
                            throw new ExceptionConverter(e4);
                        }
                    }
                }
                PdfPCellEvent evt = cell.getCellEvent();
                if (evt != null) {
                    Rectangle rect = new Rectangle(cell.getLeft() + xPos, (cell.getTop() + yPos) - currentMaxHeight, cell.getRight() + xPos, cell.getTop() + yPos);
                    evt.cellLayout(cell, rect, canvases);
                }
            }
        }
    }

    public boolean isCalculated() {
        return this.calculated;
    }

    public float getMaxHeights() {
        if (this.calculated) {
            return this.maxHeight;
        }
        return calculateHeights();
    }

    public void setMaxHeights(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    float[] getEventWidth(float xPos) {
        int n = 0;
        for (PdfPCell pdfPCell : this.cells) {
            if (pdfPCell != null) {
                n++;
            }
        }
        float[] width = new float[n + 1];
        int n2 = 0 + 1;
        width[0] = xPos;
        for (PdfPCell cell : this.cells) {
            if (cell != null) {
                width[n2] = width[n2 - 1] + cell.getWidth();
                n2++;
            }
        }
        return width;
    }

    public PdfPRow splitRow(PdfPTable table, int rowIndex, float new_height) {
        float y;
        PdfPCell[] newCells = new PdfPCell[this.cells.length];
        float[] fixHs = new float[this.cells.length];
        float[] minHs = new float[this.cells.length];
        boolean allEmpty = true;
        for (int k = 0; k < this.cells.length; k++) {
            PdfPCell cell = this.cells[k];
            if (cell == null) {
                int index = rowIndex;
                if (table.rowSpanAbove(index, k)) {
                    float f = new_height;
                    float rowHeight = table.getRowHeight(index);
                    while (true) {
                        float newHeight = f + rowHeight;
                        index--;
                        if (table.rowSpanAbove(index, k)) {
                            f = newHeight;
                            rowHeight = table.getRowHeight(index);
                        } else {
                            PdfPRow row = table.getRow(index);
                            if (row != null && row.getCells()[k] != null) {
                                newCells[k] = new PdfPCell(row.getCells()[k]);
                                newCells[k].consumeHeight(newHeight);
                                newCells[k].setRowspan((row.getCells()[k].getRowspan() - rowIndex) + index);
                                allEmpty = false;
                            }
                        }
                    }
                }
            } else {
                fixHs[k] = cell.getFixedHeight();
                minHs[k] = cell.getMinimumHeight();
                Image img = cell.getImage();
                PdfPCell newCell = new PdfPCell(cell);
                if (img == null) {
                    ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    float left = cell.getLeft() + cell.getEffectivePaddingLeft();
                    float bottom = (cell.getTop() + cell.getEffectivePaddingBottom()) - new_height;
                    float right = cell.getRight() - cell.getEffectivePaddingRight();
                    float top = cell.getTop() - cell.getEffectivePaddingTop();
                    switch (cell.getRotation()) {
                        case 90:
                        case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                            y = setColumn(ct, bottom, left, top, right);
                            break;
                        default:
                            y = setColumn(ct, left, bottom, cell.isNoWrap() ? RIGHT_LIMIT : right, top);
                            break;
                    }
                    try {
                        int status = ct.go(true);
                        boolean thisEmpty = ct.getYLine() == y;
                        if (thisEmpty) {
                            newCell.setColumn(ColumnText.duplicate(cell.getColumn()));
                            ct.setFilledWidth(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                        } else if ((status & 1) == 0) {
                            newCell.setColumn(ct);
                            ct.setFilledWidth(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                        } else {
                            newCell.setPhrase(null);
                        }
                        allEmpty = allEmpty && thisEmpty;
                    } catch (DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                } else if (new_height > cell.getEffectivePaddingBottom() + cell.getEffectivePaddingTop() + 2.0f) {
                    newCell.setPhrase(null);
                    allEmpty = false;
                }
                newCells[k] = newCell;
                cell.setFixedHeight(new_height);
            }
        }
        if (allEmpty) {
            for (int k2 = 0; k2 < this.cells.length; k2++) {
                PdfPCell cell2 = this.cells[k2];
                if (cell2 != null) {
                    if (fixHs[k2] > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        cell2.setFixedHeight(fixHs[k2]);
                    } else {
                        cell2.setMinimumHeight(minHs[k2]);
                    }
                }
            }
            return null;
        }
        calculateHeights();
        PdfPRow split = new PdfPRow(newCells);
        split.widths = (float[]) this.widths.clone();
        split.calculateHeights();
        return split;
    }

    public PdfPCell[] getCells() {
        return this.cells;
    }
}
