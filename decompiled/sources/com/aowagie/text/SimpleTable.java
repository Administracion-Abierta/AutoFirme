package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import com.aowagie.text.pdf.PdfPTable;
import com.aowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/SimpleTable.class */
public class SimpleTable extends Rectangle implements PdfPTableEvent, TextElementArray {
    private final ArrayList content;
    private float width;
    private float widthpercentage;
    private float cellspacing;
    private float cellpadding;
    private int alignment;

    public SimpleTable() {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.content = new ArrayList();
        this.width = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.widthpercentage = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        setBorder(15);
        setBorderWidth(2.0f);
    }

    private void addElement(SimpleCell element) throws BadElementException {
        if (!element.isCellgroup()) {
            throw new BadElementException("You can't add cells to a table directly, add them to a row first.");
        }
        this.content.add(element);
    }

    public PdfPTable createPdfPTable() throws DocumentException {
        if (this.content.isEmpty()) {
            throw new BadElementException("Trying to create a table without rows.");
        }
        int columns = 0;
        Iterator i = ((SimpleCell) this.content.get(0)).getContent().iterator();
        while (i.hasNext()) {
            columns += ((SimpleCell) i.next()).getColspan();
        }
        float[] widths = new float[columns];
        float[] widthpercentages = new float[columns];
        PdfPTable table = new PdfPTable(columns);
        table.setTableEvent(this);
        table.setHorizontalAlignment(this.alignment);
        for (SimpleCell row : this.content) {
            int pos = 0;
            for (SimpleCell cell : row.getContent()) {
                if (Float.isNaN(cell.getSpacing_left())) {
                    cell.setSpacing_left(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_right())) {
                    cell.setSpacing_right(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_top())) {
                    cell.setSpacing_top(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_bottom())) {
                    cell.setSpacing_bottom(this.cellspacing / 2.0f);
                }
                cell.setPadding(this.cellpadding);
                table.addCell(cell.createPdfPCell(row));
                if (cell.getColspan() == 1) {
                    if (cell.getWidth() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        widths[pos] = cell.getWidth();
                    }
                    if (cell.getWidthpercentage() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        widthpercentages[pos] = cell.getWidthpercentage();
                    }
                }
                pos += cell.getColspan();
            }
        }
        float sumWidths = 0.0f;
        for (int i2 = 0; i2 < columns; i2++) {
            if (widths[i2] == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                sumWidths = 0.0f;
                break;
            }
            sumWidths += widths[i2];
        }
        if (sumWidths > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            table.setTotalWidth(sumWidths);
            table.setWidths(widths);
        } else {
            for (int i3 = 0; i3 < columns; i3++) {
                if (widthpercentages[i3] == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    sumWidths = 0.0f;
                    break;
                }
                sumWidths += widthpercentages[i3];
            }
            if (sumWidths > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                table.setWidths(widthpercentages);
            }
        }
        if (this.width > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            table.setTotalWidth(this.width);
        }
        if (this.widthpercentage > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            table.setWidthPercentage(this.widthpercentage);
        }
        return table;
    }

    @Override // com.aowagie.text.pdf.PdfPTableEvent
    public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
        float[] width = widths[0];
        Rectangle rect = new Rectangle(width[0], heights[heights.length - 1], width[width.length - 1], heights[0]);
        rect.cloneNonPositionParameters(this);
        int bd = rect.getBorder();
        rect.setBorder(0);
        canvases[1].rectangle(rect);
        rect.setBorder(bd);
        rect.setBackgroundColor(null);
        canvases[2].rectangle(rect);
    }

    public float getCellpadding() {
        return this.cellpadding;
    }

    public void setCellpadding(float cellpadding) {
        this.cellpadding = cellpadding;
    }

    public float getCellspacing() {
        return this.cellspacing;
    }

    public void setCellspacing(float cellspacing) {
        this.cellspacing = cellspacing;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
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

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public int type() {
        return 22;
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    @Override // com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        try {
            addElement((SimpleCell) o);
            return true;
        } catch (BadElementException e) {
            throw new ExceptionConverter(e);
        } catch (ClassCastException e2) {
            return false;
        }
    }
}
