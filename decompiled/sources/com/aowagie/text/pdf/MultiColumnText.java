package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Element;
import com.aowagie.text.ElementListener;
import com.aowagie.text.Phrase;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/MultiColumnText.class */
public class MultiColumnText implements Element {
    public static final float AUTOMATIC = -1.0f;
    private final float desiredHeight;
    private float totalHeight;
    private boolean overflow;
    private float top;
    private final ColumnText columnText;
    private final ArrayList columnDefs;
    private boolean simple;
    private int currentColumn;
    private float nextY;
    private boolean columnsRightToLeft;
    private PdfDocument document;

    public MultiColumnText() {
        this(-1.0f);
    }

    public MultiColumnText(float height) {
        this.simple = true;
        this.currentColumn = 0;
        this.nextY = -1.0f;
        this.columnsRightToLeft = false;
        this.columnDefs = new ArrayList();
        this.desiredHeight = height;
        this.top = -1.0f;
        this.columnText = new ColumnText(null);
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public MultiColumnText(float top, float height) {
        this.simple = true;
        this.currentColumn = 0;
        this.nextY = -1.0f;
        this.columnsRightToLeft = false;
        this.columnDefs = new ArrayList();
        this.desiredHeight = height;
        this.top = top;
        this.nextY = top;
        this.columnText = new ColumnText(null);
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public boolean isOverflow() {
        return this.overflow;
    }

    public void useColumnParams(ColumnText sourceColumn) {
        this.columnText.setSimpleVars(sourceColumn);
    }

    public void addColumn(float[] left, float[] right) {
        ColumnDef nextDef = new ColumnDef(left, right);
        if (!nextDef.isSimple()) {
            this.simple = false;
        }
        this.columnDefs.add(nextDef);
    }

    public void addSimpleColumn(float left, float right) {
        ColumnDef newCol = new ColumnDef(left, right);
        this.columnDefs.add(newCol);
    }

    public void addRegularColumns(float left, float right, float gutterWidth, int numColumns) {
        float currX = left;
        float width = right - left;
        float colWidth = (width - (gutterWidth * (numColumns - 1))) / numColumns;
        for (int i = 0; i < numColumns; i++) {
            addSimpleColumn(currX, currX + colWidth);
            currX += colWidth + gutterWidth;
        }
    }

    public void addText(Phrase phrase) {
        this.columnText.addText(phrase);
    }

    public void addText(Chunk chunk) {
        this.columnText.addText(chunk);
    }

    public void addElement(Element element) throws DocumentException {
        if (this.simple) {
            this.columnText.addElement(element);
        } else if (element instanceof Phrase) {
            this.columnText.addText((Phrase) element);
        } else {
            if (element instanceof Chunk) {
                this.columnText.addText((Chunk) element);
                return;
            }
            throw new DocumentException("Can't add " + element.getClass() + " to MultiColumnText with complex columns");
        }
    }

    public float write(PdfContentByte canvas, PdfDocument document, float documentY) throws DocumentException {
        this.document = document;
        this.columnText.setCanvas(canvas);
        if (this.columnDefs.isEmpty()) {
            throw new DocumentException("MultiColumnText has no columns");
        }
        this.overflow = false;
        float currentHeight = 0.0f;
        boolean done = false;
        while (!done) {
            try {
                if (this.top == -1.0f) {
                    this.top = document.getVerticalPosition(true);
                } else if (this.nextY == -1.0f) {
                    this.nextY = document.getVerticalPosition(true);
                }
                ColumnDef currentDef = (ColumnDef) this.columnDefs.get(getCurrentColumn());
                this.columnText.setYLine(this.top);
                float[] left = currentDef.resolvePositions(4);
                float[] right = currentDef.resolvePositions(8);
                if (document.isMarginMirroring() && document.getPageNumber() % 2 == 0) {
                    float delta = document.rightMargin() - document.left();
                    left = (float[]) left.clone();
                    right = (float[]) right.clone();
                    for (int i = 0; i < left.length; i += 2) {
                        int i2 = i;
                        left[i2] = left[i2] - delta;
                    }
                    for (int i3 = 0; i3 < right.length; i3 += 2) {
                        int i4 = i3;
                        right[i4] = right[i4] - delta;
                    }
                }
                currentHeight = Math.max(currentHeight, getHeight(left, right));
                if (currentDef.isSimple()) {
                    this.columnText.setSimpleColumn(left[2], left[3], right[0], right[1]);
                } else {
                    this.columnText.setColumns(left, right);
                }
                int result = this.columnText.go();
                if ((result & 1) != 0) {
                    done = true;
                    this.top = this.columnText.getYLine();
                } else if (shiftCurrentColumn()) {
                    this.top = this.nextY;
                } else {
                    this.totalHeight += currentHeight;
                    if (this.desiredHeight != -1.0f && this.totalHeight >= this.desiredHeight) {
                        this.overflow = true;
                        break;
                    }
                    documentY = this.nextY;
                    newPage();
                    currentHeight = 0.0f;
                }
            } catch (DocumentException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
        if (this.desiredHeight == -1.0f && this.columnDefs.size() == 1) {
            currentHeight = documentY - this.columnText.getYLine();
        }
        return currentHeight;
    }

    private void newPage() throws DocumentException {
        resetCurrentColumn();
        if (this.desiredHeight == -1.0f) {
            this.nextY = -1.0f;
            this.top = -1.0f;
        } else {
            this.top = this.nextY;
        }
        this.totalHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        if (this.document != null) {
            this.document.newPage();
        }
    }

    private float getHeight(float[] left, float[] right) {
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < left.length; i += 2) {
            min = Math.min(min, left[i + 1]);
            max = Math.max(max, left[i + 1]);
        }
        for (int i2 = 0; i2 < right.length; i2 += 2) {
            min = Math.min(min, right[i2 + 1]);
            max = Math.max(max, right[i2 + 1]);
        }
        return max - min;
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
        return 40;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        return null;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getColumnBottom() {
        if (this.desiredHeight == -1.0f) {
            return this.document.bottom();
        }
        return Math.max(this.top - (this.desiredHeight - this.totalHeight), this.document.bottom());
    }

    public void nextColumn() throws DocumentException {
        this.currentColumn = (this.currentColumn + 1) % this.columnDefs.size();
        this.top = this.nextY;
        if (this.currentColumn == 0) {
            newPage();
        }
    }

    public int getCurrentColumn() {
        if (this.columnsRightToLeft) {
            return (this.columnDefs.size() - this.currentColumn) - 1;
        }
        return this.currentColumn;
    }

    public void resetCurrentColumn() {
        this.currentColumn = 0;
    }

    public boolean shiftCurrentColumn() {
        if (this.currentColumn + 1 < this.columnDefs.size()) {
            this.currentColumn++;
            return true;
        }
        return false;
    }

    public void setColumnsRightToLeft(boolean direction) {
        this.columnsRightToLeft = direction;
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.columnText.setSpaceCharRatio(spaceCharRatio);
    }

    public void setRunDirection(int runDirection) {
        this.columnText.setRunDirection(runDirection);
    }

    public void setArabicOptions(int arabicOptions) {
        this.columnText.setArabicOptions(arabicOptions);
    }

    public void setAlignment(int alignment) {
        this.columnText.setAlignment(alignment);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/MultiColumnText$ColumnDef.class */
    private class ColumnDef {
        private final float[] left;
        private final float[] right;

        ColumnDef(float[] newLeft, float[] newRight) {
            this.left = newLeft;
            this.right = newRight;
        }

        ColumnDef(float leftPosition, float rightPosition) {
            this.left = new float[4];
            this.left[0] = leftPosition;
            this.left[1] = MultiColumnText.this.top;
            this.left[2] = leftPosition;
            if (MultiColumnText.this.desiredHeight != -1.0f && MultiColumnText.this.top != -1.0f) {
                this.left[3] = MultiColumnText.this.top - MultiColumnText.this.desiredHeight;
            } else {
                this.left[3] = -1.0f;
            }
            this.right = new float[4];
            this.right[0] = rightPosition;
            this.right[1] = MultiColumnText.this.top;
            this.right[2] = rightPosition;
            if (MultiColumnText.this.desiredHeight != -1.0f && MultiColumnText.this.top != -1.0f) {
                this.right[3] = MultiColumnText.this.top - MultiColumnText.this.desiredHeight;
            } else {
                this.right[3] = -1.0f;
            }
        }

        float[] resolvePositions(int side) {
            if (side == 4) {
                return resolvePositions(this.left);
            }
            return resolvePositions(this.right);
        }

        private float[] resolvePositions(float[] positions) {
            if (!isSimple()) {
                positions[1] = MultiColumnText.this.top;
                return positions;
            }
            if (MultiColumnText.this.top == -1.0f) {
                throw new RuntimeException("resolvePositions called with top=AUTOMATIC (-1).  Top position must be set befure lines can be resolved");
            }
            positions[1] = MultiColumnText.this.top;
            positions[3] = MultiColumnText.this.getColumnBottom();
            return positions;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isSimple() {
            return this.left.length == 4 && this.right.length == 4 && this.left[0] == this.left[2] && this.right[0] == this.right[2];
        }
    }
}
