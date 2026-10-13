package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import com.aowagie.text.pdf.PdfPCell;
import com.aowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/SimpleCell.class */
class SimpleCell extends Rectangle implements PdfPCellEvent, TextElementArray {
    static final boolean CELL = false;
    private final ArrayList content;
    private float width;
    private float widthpercentage;
    private float spacing_left;
    private float spacing_right;
    private float spacing_top;
    private float spacing_bottom;
    private float padding_left;
    private float padding_right;
    private float padding_top;
    private float padding_bottom;
    private int colspan;
    private int horizontalAlignment;
    private int verticalAlignment;
    private boolean cellgroup;
    private boolean useAscender;
    private boolean useDescender;
    private boolean useBorderPadding;

    public SimpleCell(boolean row) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.content = new ArrayList();
        this.width = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.widthpercentage = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.spacing_left = Float.NaN;
        this.spacing_right = Float.NaN;
        this.spacing_top = Float.NaN;
        this.spacing_bottom = Float.NaN;
        this.padding_left = Float.NaN;
        this.padding_right = Float.NaN;
        this.padding_top = Float.NaN;
        this.padding_bottom = Float.NaN;
        this.colspan = 1;
        this.horizontalAlignment = -1;
        this.verticalAlignment = -1;
        this.cellgroup = false;
        this.useAscender = false;
        this.useDescender = false;
        this.cellgroup = row;
        setBorder(15);
    }

    private void addElement(Element element) throws BadElementException {
        if (this.cellgroup) {
            if (element instanceof SimpleCell) {
                if (((SimpleCell) element).isCellgroup()) {
                    throw new BadElementException("You can't add one row to another row.");
                }
                this.content.add(element);
                return;
            }
            throw new BadElementException("You can only add cells to rows, no objects of type " + element.getClass().getName());
        }
        if (element.type() == 12 || element.type() == 11 || element.type() == 17 || element.type() == 10 || element.type() == 14 || element.type() == 50 || element.type() == 32 || element.type() == 33 || element.type() == 36 || element.type() == 34 || element.type() == 35) {
            this.content.add(element);
            return;
        }
        throw new BadElementException("You can't add an element of type " + element.getClass().getName() + " to a SimpleCell.");
    }

    public PdfPCell createPdfPCell(SimpleCell rowAttributes) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        SimpleCell tmp = new SimpleCell(false);
        tmp.setSpacing_left(this.spacing_left);
        tmp.setSpacing_right(this.spacing_right);
        tmp.setSpacing_top(this.spacing_top);
        tmp.setSpacing_bottom(this.spacing_bottom);
        tmp.cloneNonPositionParameters(rowAttributes);
        tmp.softCloneNonPositionParameters(this);
        cell.setCellEvent(tmp);
        cell.setHorizontalAlignment(rowAttributes.horizontalAlignment);
        cell.setVerticalAlignment(rowAttributes.verticalAlignment);
        cell.setUseAscender(rowAttributes.useAscender);
        cell.setUseBorderPadding(rowAttributes.useBorderPadding);
        cell.setUseDescender(rowAttributes.useDescender);
        cell.setColspan(this.colspan);
        if (this.horizontalAlignment != -1) {
            cell.setHorizontalAlignment(this.horizontalAlignment);
        }
        if (this.verticalAlignment != -1) {
            cell.setVerticalAlignment(this.verticalAlignment);
        }
        if (this.useAscender) {
            cell.setUseAscender(this.useAscender);
        }
        if (this.useBorderPadding) {
            cell.setUseBorderPadding(this.useBorderPadding);
        }
        if (this.useDescender) {
            cell.setUseDescender(this.useDescender);
        }
        float sp_left = this.spacing_left;
        if (Float.isNaN(sp_left)) {
            sp_left = 0.0f;
        }
        float sp_right = this.spacing_right;
        if (Float.isNaN(sp_right)) {
            sp_right = 0.0f;
        }
        float sp_top = this.spacing_top;
        if (Float.isNaN(sp_top)) {
            sp_top = 0.0f;
        }
        float sp_bottom = this.spacing_bottom;
        if (Float.isNaN(sp_bottom)) {
            sp_bottom = 0.0f;
        }
        float p = this.padding_left;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingLeft(p + sp_left);
        float p2 = this.padding_right;
        if (Float.isNaN(p2)) {
            p2 = 0.0f;
        }
        cell.setPaddingRight(p2 + sp_right);
        float p3 = this.padding_top;
        if (Float.isNaN(p3)) {
            p3 = 0.0f;
        }
        cell.setPaddingTop(p3 + sp_top);
        float p4 = this.padding_bottom;
        if (Float.isNaN(p4)) {
            p4 = 0.0f;
        }
        cell.setPaddingBottom(p4 + sp_bottom);
        for (Element element : this.content) {
            cell.addElement(element);
        }
        return cell;
    }

    @Override // com.aowagie.text.pdf.PdfPCellEvent
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        float sp_left = this.spacing_left;
        if (Float.isNaN(sp_left)) {
            sp_left = 0.0f;
        }
        float sp_right = this.spacing_right;
        if (Float.isNaN(sp_right)) {
            sp_right = 0.0f;
        }
        float sp_top = this.spacing_top;
        if (Float.isNaN(sp_top)) {
            sp_top = 0.0f;
        }
        float sp_bottom = this.spacing_bottom;
        if (Float.isNaN(sp_bottom)) {
            sp_bottom = 0.0f;
        }
        Rectangle rect = new Rectangle(position.getLeft(sp_left), position.getBottom(sp_bottom), position.getRight(sp_right), position.getTop(sp_top));
        rect.cloneNonPositionParameters(this);
        canvases[1].rectangle(rect);
        rect.setBackgroundColor(null);
        canvases[2].rectangle(rect);
    }

    public void setPadding(float padding) {
        if (Float.isNaN(this.padding_right)) {
            setPadding_right(padding);
        }
        if (Float.isNaN(this.padding_left)) {
            setPadding_left(padding);
        }
        if (Float.isNaN(this.padding_top)) {
            setPadding_top(padding);
        }
        if (Float.isNaN(this.padding_bottom)) {
            setPadding_bottom(padding);
        }
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setColspan(int colspan) {
        if (colspan > 0) {
            this.colspan = colspan;
        }
    }

    public float getPadding_bottom() {
        return this.padding_bottom;
    }

    public void setPadding_bottom(float padding_bottom) {
        this.padding_bottom = padding_bottom;
    }

    public float getPadding_left() {
        return this.padding_left;
    }

    public void setPadding_left(float padding_left) {
        this.padding_left = padding_left;
    }

    public float getPadding_right() {
        return this.padding_right;
    }

    public void setPadding_right(float padding_right) {
        this.padding_right = padding_right;
    }

    public float getPadding_top() {
        return this.padding_top;
    }

    public void setPadding_top(float padding_top) {
        this.padding_top = padding_top;
    }

    public float getSpacing_left() {
        return this.spacing_left;
    }

    public float getSpacing_right() {
        return this.spacing_right;
    }

    public float getSpacing_top() {
        return this.spacing_top;
    }

    public float getSpacing_bottom() {
        return this.spacing_bottom;
    }

    public void setSpacing(float spacing) {
        this.spacing_left = spacing;
        this.spacing_right = spacing;
        this.spacing_top = spacing;
        this.spacing_bottom = spacing;
    }

    public void setSpacing_left(float spacing) {
        this.spacing_left = spacing;
    }

    public void setSpacing_right(float spacing) {
        this.spacing_right = spacing;
    }

    public void setSpacing_top(float spacing) {
        this.spacing_top = spacing;
    }

    public void setSpacing_bottom(float spacing) {
        this.spacing_bottom = spacing;
    }

    public boolean isCellgroup() {
        return this.cellgroup;
    }

    public void setCellgroup(boolean cellgroup) {
        this.cellgroup = cellgroup;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    @Override // com.aowagie.text.Rectangle
    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidthpercentage() {
        return this.widthpercentage;
    }

    public void setWidthpercentage(float widthpercentage) {
        this.widthpercentage = widthpercentage;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseAscender(boolean useAscender) {
        this.useAscender = useAscender;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public void setUseBorderPadding(boolean useBorderPadding) {
        this.useBorderPadding = useBorderPadding;
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseDescender(boolean useDescender) {
        this.useDescender = useDescender;
    }

    ArrayList getContent() {
        return this.content;
    }

    @Override // com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        try {
            addElement((Element) o);
            return true;
        } catch (BadElementException e) {
            throw new ExceptionConverter(e);
        } catch (ClassCastException e2) {
            return false;
        }
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public int type() {
        return 20;
    }
}
