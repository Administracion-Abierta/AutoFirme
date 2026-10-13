package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfPCell;
import com.sun.jna.platform.win32.WinNT;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Cell.class */
public class Cell extends Rectangle implements TextElementArray {
    private ArrayList arrayList;
    private int horizontalAlignment;
    private int verticalAlignment;
    private float width;
    private boolean percentage;
    private int colspan;
    private int rowspan;
    private float leading;
    private boolean header;
    private int maxLines;
    private String showTruncation;
    private boolean useAscender;
    private boolean useDescender;
    private boolean useBorderPadding;
    private boolean groupChange;

    public Cell() {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.arrayList = null;
        this.horizontalAlignment = -1;
        this.verticalAlignment = -1;
        this.percentage = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.leading = Float.NaN;
        this.maxLines = WinNT.MAXLONG;
        this.useAscender = false;
        this.useDescender = false;
        this.groupChange = true;
        setBorder(-1);
        setBorderWidth(0.5f);
        this.arrayList = new ArrayList();
    }

    Cell(boolean dummy) {
        this();
        this.arrayList.add(new Paragraph(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
    }

    Cell(Element element) throws BadElementException {
        this();
        if (element instanceof Phrase) {
            setLeading(((Phrase) element).getLeading());
        }
        addElement(element);
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
        return 20;
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public ArrayList getChunks() {
        ArrayList tmp = new ArrayList();
        Iterator i = this.arrayList.iterator();
        while (i.hasNext()) {
            tmp.addAll(((Element) i.next()).getChunks());
        }
        return tmp;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int value) {
        this.horizontalAlignment = value;
    }

    public void setHorizontalAlignment(String alignment) {
        setHorizontalAlignment(ElementTags.alignmentValue(alignment));
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int value) {
        this.verticalAlignment = value;
    }

    public void setVerticalAlignment(String alignment) {
        setVerticalAlignment(ElementTags.alignmentValue(alignment));
    }

    public void setWidth(float value) {
        this.width = value;
    }

    public void setWidth(String value) {
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
            this.percentage = true;
        }
        this.width = Integer.parseInt(value);
    }

    @Override // com.aowagie.text.Rectangle
    public float getWidth() {
        return this.width;
    }

    public String getWidthAsString() {
        String w = String.valueOf(this.width);
        if (w.endsWith(".0")) {
            w = w.substring(0, w.length() - 2);
        }
        if (this.percentage) {
            w = w + "%";
        }
        return w;
    }

    public void setColspan(int value) {
        this.colspan = value;
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setRowspan(int value) {
        this.rowspan = value;
    }

    public int getRowspan() {
        return this.rowspan;
    }

    public void setLeading(float value) {
        this.leading = value;
    }

    public float getLeading() {
        if (Float.isNaN(this.leading)) {
            return 16.0f;
        }
        return this.leading;
    }

    public void setHeader(boolean value) {
        this.header = value;
    }

    public boolean isHeader() {
        return this.header;
    }

    public void setMaxLines(int value) {
        this.maxLines = value;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setShowTruncation(String value) {
        this.showTruncation = value;
    }

    public String getShowTruncation() {
        return this.showTruncation;
    }

    public void setUseAscender(boolean use) {
        this.useAscender = use;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseDescender(boolean use) {
        this.useDescender = use;
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseBorderPadding(boolean use) {
        this.useBorderPadding = use;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public boolean getGroupChange() {
        return this.groupChange;
    }

    public void setGroupChange(boolean value) {
        this.groupChange = value;
    }

    private int size() {
        return this.arrayList.size();
    }

    public Iterator getElements() {
        return this.arrayList.iterator();
    }

    private void clear() {
        this.arrayList.clear();
    }

    public boolean isEmpty() {
        switch (size()) {
            case 0:
                return true;
            case 1:
                Element element = (Element) this.arrayList.get(0);
                switch (element.type()) {
                    case 10:
                        return ((Chunk) element).isEmpty();
                    case 11:
                    case 12:
                    case 17:
                        return ((Phrase) element).isEmpty();
                    case 13:
                    case 15:
                    case 16:
                    default:
                        return false;
                    case 14:
                        return ((List) element).isEmpty();
                }
            default:
                return false;
        }
    }

    void fill() {
        if (size() == 0) {
            this.arrayList.add(new Paragraph(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        }
    }

    public boolean isTable() {
        return size() == 1 && ((Element) this.arrayList.get(0)).type() == 22;
    }

    private void addElement(Element element) throws BadElementException {
        if (isTable()) {
            Table table = (Table) this.arrayList.get(0);
            Cell tmp = new Cell(element);
            tmp.setBorder(0);
            tmp.setColspan(table.getColumns());
            table.addCell(tmp);
            return;
        }
        switch (element.type()) {
            case 10:
                if (((Chunk) element).isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            case 11:
            case 12:
            case 17:
                Phrase p = (Phrase) element;
                if (Float.isNaN(this.leading)) {
                    setLeading(p.getLeading());
                }
                if (p.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            case 13:
            case 16:
            case 18:
            case 19:
            default:
                this.arrayList.add(element);
                return;
            case 14:
                List list = (List) element;
                if (Float.isNaN(this.leading)) {
                    setLeading(list.getTotalLeading());
                }
                if (list.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            case 15:
            case 20:
            case 21:
                throw new BadElementException("You can't add listitems, rows or cells to a cell.");
            case 22:
                Table table2 = new Table(3);
                float[] widths = new float[3];
                widths[1] = ((Table) element).getWidth();
                switch (((Table) element).getAlignment()) {
                    case 0:
                        widths[0] = 0.0f;
                        widths[2] = 100.0f - widths[1];
                        break;
                    case 1:
                        widths[0] = (100.0f - widths[1]) / 2.0f;
                        widths[2] = widths[0];
                        break;
                    case 2:
                        widths[0] = 100.0f - widths[1];
                        widths[2] = 0.0f;
                        break;
                }
                table2.setWidths(widths);
                if (this.arrayList.isEmpty()) {
                    table2.addCell(getDummyCell());
                } else {
                    Cell tmp2 = new Cell();
                    tmp2.setBorder(0);
                    tmp2.setColspan(3);
                    Iterator i = this.arrayList.iterator();
                    while (i.hasNext()) {
                        tmp2.add(i.next());
                    }
                    table2.addCell(tmp2);
                }
                Cell tmp3 = new Cell();
                tmp3.setBorder(0);
                table2.addCell(tmp3);
                table2.insertTable((Table) element);
                Cell tmp4 = new Cell();
                tmp4.setBorder(0);
                table2.addCell(tmp4);
                table2.addCell(getDummyCell());
                clear();
                this.arrayList.add(table2);
                return;
        }
    }

    @Override // com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        try {
            addElement((Element) o);
            return true;
        } catch (BadElementException bee) {
            throw new ClassCastException(bee.getMessage());
        } catch (ClassCastException e) {
            throw new ClassCastException("You can only add objects that implement the Element interface.");
        }
    }

    private static Cell getDummyCell() {
        Cell cell = new Cell(true);
        cell.setColspan(3);
        cell.setBorder(0);
        return cell;
    }

    PdfPCell createPdfPCell() throws BadElementException {
        if (this.rowspan > 1) {
            throw new BadElementException("PdfPCells can't have a rowspan > 1");
        }
        if (isTable()) {
            return new PdfPCell(((Table) this.arrayList.get(0)).createPdfPTable());
        }
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(this.verticalAlignment);
        cell.setHorizontalAlignment(this.horizontalAlignment);
        cell.setColspan(this.colspan);
        cell.setUseBorderPadding(this.useBorderPadding);
        cell.setUseDescender(this.useDescender);
        cell.setLeading(getLeading(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        cell.cloneNonPositionParameters(this);
        cell.setNoWrap(getMaxLines() == 1);
        Iterator i = getElements();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            if (e.type() == 11 || e.type() == 12) {
                Paragraph p = new Paragraph((Phrase) e);
                p.setAlignment(this.horizontalAlignment);
                e = p;
            }
            cell.addElement(e);
        }
        return cell;
    }

    @Override // com.aowagie.text.Rectangle
    public float getTop() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    @Override // com.aowagie.text.Rectangle
    public float getBottom() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    @Override // com.aowagie.text.Rectangle
    public float getLeft() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    @Override // com.aowagie.text.Rectangle
    public float getRight() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public void setTop(int value) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }

    public void setBottom(int value) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }

    public void setLeft(int value) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }

    public void setRight(int value) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }
}
