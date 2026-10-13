package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Element;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.events.PdfPCellEventForwarder;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPCell.class */
public class PdfPCell extends Rectangle {
    private ColumnText column;
    private int verticalAlignment;
    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;
    private float fixedHeight;
    private float minimumHeight;
    private boolean noWrap;
    private PdfPTable table;
    private int colspan;
    private int rowspan;
    private Image image;
    private PdfPCellEvent cellEvent;
    private boolean useDescender;
    private boolean useBorderPadding;
    protected Phrase phrase;
    private int rotation;

    public PdfPCell() {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.column = new ColumnText(null);
        this.verticalAlignment = 4;
        this.paddingLeft = 2.0f;
        this.paddingRight = 2.0f;
        this.paddingTop = 2.0f;
        this.paddingBottom = 2.0f;
        this.fixedHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.noWrap = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.useBorderPadding = false;
        this.borderWidth = 0.5f;
        this.border = 15;
        this.column.setLeading(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f);
    }

    public PdfPCell(Phrase phrase) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.column = new ColumnText(null);
        this.verticalAlignment = 4;
        this.paddingLeft = 2.0f;
        this.paddingRight = 2.0f;
        this.paddingTop = 2.0f;
        this.paddingBottom = 2.0f;
        this.fixedHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.noWrap = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.useBorderPadding = false;
        this.borderWidth = 0.5f;
        this.border = 15;
        ColumnText columnText = this.column;
        this.phrase = phrase;
        columnText.addText(phrase);
        this.column.setLeading(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f);
    }

    public PdfPCell(Image image) {
        this(image, false);
    }

    public PdfPCell(Image image, boolean fit) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.column = new ColumnText(null);
        this.verticalAlignment = 4;
        this.paddingLeft = 2.0f;
        this.paddingRight = 2.0f;
        this.paddingTop = 2.0f;
        this.paddingBottom = 2.0f;
        this.fixedHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.noWrap = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.useBorderPadding = false;
        this.borderWidth = 0.5f;
        this.border = 15;
        if (fit) {
            this.image = image;
            this.column.setLeading(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f);
            setPadding(this.borderWidth / 2.0f);
        } else {
            ColumnText columnText = this.column;
            Phrase phrase = new Phrase(new Chunk(image, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            this.phrase = phrase;
            columnText.addText(phrase);
            this.column.setLeading(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f);
            setPadding(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
    }

    public PdfPCell(PdfPTable table) {
        this(table, (PdfPCell) null);
    }

    public PdfPCell(PdfPTable table, PdfPCell style) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.column = new ColumnText(null);
        this.verticalAlignment = 4;
        this.paddingLeft = 2.0f;
        this.paddingRight = 2.0f;
        this.paddingTop = 2.0f;
        this.paddingBottom = 2.0f;
        this.fixedHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.noWrap = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.useBorderPadding = false;
        this.borderWidth = 0.5f;
        this.border = 15;
        this.column.setLeading(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f);
        this.table = table;
        table.setWidthPercentage(100.0f);
        table.setExtendLastRow(true);
        this.column.addElement(table);
        if (style != null) {
            cloneNonPositionParameters(style);
            this.verticalAlignment = style.verticalAlignment;
            this.paddingLeft = style.paddingLeft;
            this.paddingRight = style.paddingRight;
            this.paddingTop = style.paddingTop;
            this.paddingBottom = style.paddingBottom;
            this.colspan = style.colspan;
            this.rowspan = style.rowspan;
            this.cellEvent = style.cellEvent;
            this.useDescender = style.useDescender;
            this.useBorderPadding = style.useBorderPadding;
            this.rotation = style.rotation;
            return;
        }
        setPadding(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public PdfPCell(PdfPCell cell) {
        super(cell.llx, cell.lly, cell.urx, cell.ury);
        this.column = new ColumnText(null);
        this.verticalAlignment = 4;
        this.paddingLeft = 2.0f;
        this.paddingRight = 2.0f;
        this.paddingTop = 2.0f;
        this.paddingBottom = 2.0f;
        this.fixedHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.noWrap = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.useBorderPadding = false;
        cloneNonPositionParameters(cell);
        this.verticalAlignment = cell.verticalAlignment;
        this.paddingLeft = cell.paddingLeft;
        this.paddingRight = cell.paddingRight;
        this.paddingTop = cell.paddingTop;
        this.paddingBottom = cell.paddingBottom;
        this.phrase = cell.phrase;
        this.fixedHeight = cell.fixedHeight;
        this.minimumHeight = cell.minimumHeight;
        this.noWrap = cell.noWrap;
        this.colspan = cell.colspan;
        this.rowspan = cell.rowspan;
        if (cell.table != null) {
            this.table = new PdfPTable(cell.table);
        }
        this.image = Image.getInstance(cell.image);
        this.cellEvent = cell.cellEvent;
        this.useDescender = cell.useDescender;
        this.column = ColumnText.duplicate(cell.column);
        this.useBorderPadding = cell.useBorderPadding;
        this.rotation = cell.rotation;
    }

    public void addElement(Element element) {
        if (this.table != null) {
            this.table = null;
            this.column.setText(null);
        }
        this.column.addElement(element);
    }

    public Phrase getPhrase() {
        return this.phrase;
    }

    public void setPhrase(Phrase phrase) {
        this.table = null;
        this.image = null;
        ColumnText columnText = this.column;
        this.phrase = phrase;
        columnText.setText(phrase);
    }

    public int getHorizontalAlignment() {
        return this.column.getAlignment();
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.column.setAlignment(horizontalAlignment);
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        if (this.table != null) {
            this.table.setExtendLastRow(verticalAlignment == 4);
        }
        this.verticalAlignment = verticalAlignment;
    }

    public float getEffectivePaddingLeft() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthLeft() / (isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingLeft + border;
        }
        return this.paddingLeft;
    }

    public float getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public float getEffectivePaddingRight() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthRight() / (isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingRight + border;
        }
        return this.paddingRight;
    }

    public float getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public float getEffectivePaddingTop() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthTop() / (isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingTop + border;
        }
        return this.paddingTop;
    }

    public float getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public float getEffectivePaddingBottom() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthBottom() / (isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingBottom + border;
        }
        return this.paddingBottom;
    }

    public float getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setPadding(float padding) {
        this.paddingBottom = padding;
        this.paddingTop = padding;
        this.paddingLeft = padding;
        this.paddingRight = padding;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public void setUseBorderPadding(boolean use) {
        this.useBorderPadding = use;
    }

    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.column.setLeading(fixedLeading, multipliedLeading);
    }

    public float getLeading() {
        return this.column.getLeading();
    }

    public float getMultipliedLeading() {
        return this.column.getMultipliedLeading();
    }

    public void setIndent(float indent) {
        this.column.setIndent(indent);
    }

    public float getIndent() {
        return this.column.getIndent();
    }

    public float getExtraParagraphSpace() {
        return this.column.getExtraParagraphSpace();
    }

    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.column.setExtraParagraphSpace(extraParagraphSpace);
    }

    public void setFixedHeight(float fixedHeight) {
        this.fixedHeight = fixedHeight;
        this.minimumHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public float getFixedHeight() {
        return this.fixedHeight;
    }

    public boolean hasFixedHeight() {
        return getFixedHeight() > ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
        this.fixedHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public float getMinimumHeight() {
        return this.minimumHeight;
    }

    public boolean hasMinimumHeight() {
        return getMinimumHeight() > ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public boolean isNoWrap() {
        return this.noWrap;
    }

    public void setNoWrap(boolean noWrap) {
        this.noWrap = noWrap;
    }

    public PdfPTable getTable() {
        return this.table;
    }

    void setTable(PdfPTable table) {
        this.table = table;
        this.column.setText(null);
        this.image = null;
        if (table != null) {
            table.setExtendLastRow(this.verticalAlignment == 4);
            this.column.addElement(table);
            table.setWidthPercentage(100.0f);
        }
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public int getRowspan() {
        return this.rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public void setFollowingIndent(float indent) {
        this.column.setFollowingIndent(indent);
    }

    public float getFollowingIndent() {
        return this.column.getFollowingIndent();
    }

    public void setRightIndent(float indent) {
        this.column.setRightIndent(indent);
    }

    public float getRightIndent() {
        return this.column.getRightIndent();
    }

    public float getSpaceCharRatio() {
        return this.column.getSpaceCharRatio();
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.column.setSpaceCharRatio(spaceCharRatio);
    }

    public void setRunDirection(int runDirection) {
        this.column.setRunDirection(runDirection);
    }

    public int getRunDirection() {
        return this.column.getRunDirection();
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.column.setText(null);
        this.table = null;
        this.image = image;
    }

    public PdfPCellEvent getCellEvent() {
        return this.cellEvent;
    }

    public void setCellEvent(PdfPCellEvent cellEvent) {
        if (cellEvent == null) {
            this.cellEvent = null;
            return;
        }
        if (this.cellEvent == null) {
            this.cellEvent = cellEvent;
            return;
        }
        if (this.cellEvent instanceof PdfPCellEventForwarder) {
            ((PdfPCellEventForwarder) this.cellEvent).addCellEvent(cellEvent);
            return;
        }
        PdfPCellEventForwarder forward = new PdfPCellEventForwarder();
        forward.addCellEvent(this.cellEvent);
        forward.addCellEvent(cellEvent);
        this.cellEvent = forward;
    }

    public int getArabicOptions() {
        return this.column.getArabicOptions();
    }

    public void setArabicOptions(int arabicOptions) {
        this.column.setArabicOptions(arabicOptions);
    }

    public boolean isUseAscender() {
        return this.column.isUseAscender();
    }

    public void setUseAscender(boolean useAscender) {
        this.column.setUseAscender(useAscender);
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseDescender(boolean useDescender) {
        this.useDescender = useDescender;
    }

    public ColumnText getColumn() {
        return this.column;
    }

    public List getCompositeElements() {
        return getColumn().compositeElements;
    }

    public void setColumn(ColumnText column) {
        this.column = column;
    }

    @Override // com.aowagie.text.Rectangle
    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation) {
        int rotation2 = rotation % 360;
        if (rotation2 < 0) {
            rotation2 += 360;
        }
        if (rotation2 % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90.");
        }
        this.rotation = rotation2;
    }

    void consumeHeight(float height) {
        float rightLimit = getRight() - getEffectivePaddingRight();
        float leftLimit = getLeft() + getEffectivePaddingLeft();
        float bry = (height - getEffectivePaddingTop()) - getEffectivePaddingBottom();
        if (getRotation() != 90 && getRotation() != 270) {
            this.column.setSimpleColumn(leftLimit, bry + 0.001f, rightLimit, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        } else {
            this.column.setSimpleColumn(ColumnText.GLOBAL_SPACE_CHAR_RATIO, leftLimit, bry + 0.001f, rightLimit);
        }
        try {
            this.column.go(true);
        } catch (DocumentException e) {
        }
    }

    public float getMaxHeight() {
        float right;
        float top;
        float left;
        float bottom;
        boolean pivoted = getRotation() == 90 || getRotation() == 270;
        Image img = getImage();
        if (img != null) {
            img.scalePercent(100.0f);
            float refWidth = pivoted ? img.getScaledHeight() : img.getScaledWidth();
            float scale = (((getRight() - getEffectivePaddingRight()) - getEffectivePaddingLeft()) - getLeft()) / refWidth;
            img.scalePercent(scale * 100.0f);
            float refHeight = pivoted ? img.getScaledWidth() : img.getScaledHeight();
            setBottom(((getTop() - getEffectivePaddingTop()) - getEffectivePaddingBottom()) - refHeight);
        } else if (pivoted && hasFixedHeight()) {
            setBottom(getTop() - getFixedHeight());
        } else {
            ColumnText ct = ColumnText.duplicate(getColumn());
            if (pivoted) {
                right = 20000.0f;
                top = getRight() - getEffectivePaddingRight();
                left = 0.0f;
                bottom = getLeft() + getEffectivePaddingLeft();
            } else {
                right = isNoWrap() ? 20000.0f : getRight() - getEffectivePaddingRight();
                top = getTop() - getEffectivePaddingTop();
                left = getLeft() + getEffectivePaddingLeft();
                bottom = hasFixedHeight() ? (top + getEffectivePaddingBottom()) - getFixedHeight() : -1.0737418E9f;
            }
            PdfPRow.setColumn(ct, left, bottom, right, top);
            try {
                ct.go(true);
                if (pivoted) {
                    setBottom(((getTop() - getEffectivePaddingTop()) - getEffectivePaddingBottom()) - ct.getFilledWidth());
                } else {
                    float yLine = ct.getYLine();
                    if (isUseDescender()) {
                        yLine += ct.getDescender();
                    }
                    setBottom(yLine - getEffectivePaddingBottom());
                }
            } catch (DocumentException e) {
                throw new ExceptionConverter(e);
            }
        }
        float height = getHeight();
        if (height < getFixedHeight()) {
            height = getFixedHeight();
        } else if (height < getMinimumHeight()) {
            height = getMinimumHeight();
        }
        return height;
    }
}
