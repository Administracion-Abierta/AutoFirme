package com.aowagie.text.pdf;

import com.aowagie.text.Anchor;
import com.aowagie.text.Cell;
import com.aowagie.text.Chunk;
import com.aowagie.text.Element;
import com.aowagie.text.Image;
import com.aowagie.text.List;
import com.aowagie.text.ListItem;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCell.class */
class PdfCell extends Rectangle {
    private ArrayList lines;
    private PdfLine line;
    private final ArrayList images;
    private final float leading;
    private final int rownumber;
    private final int rowspan;
    private final float cellspacing;
    private final float cellpadding;
    private boolean header;
    private float contentHeight;
    private boolean useAscender;
    private boolean useDescender;
    private boolean useBorderPadding;
    private final int verticalAlignment;
    private PdfLine firstLine;
    private PdfLine lastLine;
    private int groupNumber;

    public PdfCell(Cell cell, int rownumber, float left, float right, float top, float cellspacing, float cellpadding) {
        String more;
        super(left, top, right, top);
        this.header = false;
        this.contentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        cloneNonPositionParameters(cell);
        this.cellpadding = cellpadding;
        this.cellspacing = cellspacing;
        this.verticalAlignment = cell.getVerticalAlignment();
        this.useAscender = cell.isUseAscender();
        this.useDescender = cell.isUseDescender();
        this.useBorderPadding = cell.isUseBorderPadding();
        this.lines = new ArrayList();
        this.images = new ArrayList();
        this.leading = cell.getLeading();
        int alignment = cell.getHorizontalAlignment();
        float left2 = left + cellspacing + cellpadding + getBorderWidthInside(4);
        float right2 = (right - (cellspacing + cellpadding)) - getBorderWidthInside(8);
        this.contentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.rowspan = cell.getRowspan();
        Iterator i = cell.getElements();
        while (i.hasNext()) {
            Element element = (Element) i.next();
            switch (element.type()) {
                case 14:
                    if (this.line != null && this.line.size() > 0) {
                        this.line.resetAlignment();
                        addLine(this.line);
                    }
                    addList((List) element, left2, right2, alignment);
                    this.line = new PdfLine(left2, right2, alignment, this.leading);
                    break;
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    addImage((Image) element, left2, right2, 0.4f * this.leading, alignment);
                    break;
                default:
                    ArrayList allActions = new ArrayList();
                    processActions(element, null, allActions);
                    int aCounter = 0;
                    float currentLineLeading = this.leading;
                    float currentLeft = left2;
                    float currentRight = right2;
                    currentLineLeading = element instanceof Phrase ? ((Phrase) element).getLeading() : currentLineLeading;
                    if (element instanceof Paragraph) {
                        Paragraph p = (Paragraph) element;
                        currentLeft += p.getIndentationLeft();
                        currentRight -= p.getIndentationRight();
                    }
                    if (this.line == null) {
                        this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                    }
                    ArrayList<Chunk> chunks = element.getChunks();
                    if (chunks.isEmpty()) {
                        addLine(this.line);
                        this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                    } else {
                        for (Chunk c : chunks) {
                            int i2 = aCounter;
                            aCounter++;
                            PdfChunk pdfChunk = new PdfChunk(c, (PdfAction) allActions.get(i2));
                            while (true) {
                                PdfChunk chunk = pdfChunk;
                                PdfChunk overflow = this.line.add(chunk);
                                if (overflow != null) {
                                    addLine(this.line);
                                    this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                                    pdfChunk = overflow;
                                }
                            }
                        }
                    }
                    switch (element.type()) {
                        case 12:
                        case 13:
                        case 16:
                            this.line.resetAlignment();
                            flushCurrentLine();
                            break;
                    }
                    break;
            }
        }
        flushCurrentLine();
        if (this.lines.size() > cell.getMaxLines()) {
            while (this.lines.size() > cell.getMaxLines()) {
                removeLine(this.lines.size() - 1);
            }
            if (cell.getMaxLines() > 0 && (more = cell.getShowTruncation()) != null && more.length() > 0) {
                this.lastLine = (PdfLine) this.lines.get(this.lines.size() - 1);
                if (this.lastLine.size() >= 0) {
                    PdfChunk lastChunk = this.lastLine.getChunk(this.lastLine.size() - 1);
                    float moreWidth = new PdfChunk(more, lastChunk).width();
                    while (lastChunk.toString().length() > 0 && lastChunk.width() + moreWidth > right2 - left2) {
                        lastChunk.setValue(lastChunk.toString().substring(0, lastChunk.length() - 1));
                    }
                    lastChunk.setValue(lastChunk.toString() + more);
                } else {
                    this.lastLine.add(new PdfChunk(new Chunk(more), (PdfAction) null));
                }
            }
        }
        if (this.useDescender && this.lastLine != null) {
            this.contentHeight -= this.lastLine.getDescender();
        }
        if (!this.lines.isEmpty()) {
            this.firstLine = (PdfLine) this.lines.get(0);
            float firstLineRealHeight = firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = firstLineRealHeight;
            this.contentHeight += firstLineRealHeight;
        }
        float newBottom = ((top - this.contentHeight) - (2.0f * cellpadding())) - (2.0f * cellspacing());
        setBottom(newBottom - (getBorderWidthInside(1) + getBorderWidthInside(2)));
        this.rownumber = rownumber;
    }

    private void addList(List list, float left, float right, int alignment) {
        ArrayList allActions = new ArrayList();
        processActions(list, null, allActions);
        int aCounter = 0;
        for (Element ele : list.getItems()) {
            switch (ele.type()) {
                case 14:
                    List sublist = (List) ele;
                    addList(sublist, left + sublist.getIndentationLeft(), right, alignment);
                    break;
                case 15:
                    ListItem item = (ListItem) ele;
                    this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, item.getLeading());
                    this.line.setListItem(item);
                    Iterator j = item.getChunks().iterator();
                    while (j.hasNext()) {
                        int i = aCounter;
                        aCounter++;
                        PdfChunk pdfChunk = new PdfChunk((Chunk) j.next(), (PdfAction) allActions.get(i));
                        while (true) {
                            PdfChunk chunk = pdfChunk;
                            PdfChunk overflow = this.line.add(chunk);
                            if (overflow != null) {
                                addLine(this.line);
                                this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, item.getLeading());
                                pdfChunk = overflow;
                            }
                        }
                        this.line.resetAlignment();
                        addLine(this.line);
                        this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, this.leading);
                    }
                    break;
            }
        }
    }

    @Override // com.aowagie.text.Rectangle
    public void setBottom(float value) {
        float extraHeight;
        super.setBottom(value);
        float firstLineRealHeight = firstLineRealHeight();
        float totalHeight = this.ury - value;
        float nonContentHeight = (cellpadding() * 2.0f) + (cellspacing() * 2.0f);
        float interiorHeight = totalHeight - (nonContentHeight + (getBorderWidthInside(1) + getBorderWidthInside(2)));
        switch (this.verticalAlignment) {
            case 5:
                extraHeight = (interiorHeight - this.contentHeight) / 2.0f;
                break;
            case 6:
                extraHeight = interiorHeight - this.contentHeight;
                break;
            default:
                extraHeight = 0.0f;
                break;
        }
        float extraHeight2 = extraHeight + cellpadding() + cellspacing() + getBorderWidthInside(1);
        if (this.firstLine != null) {
            this.firstLine.height = firstLineRealHeight + extraHeight2;
        }
    }

    @Override // com.aowagie.text.Rectangle
    public float getLeft() {
        return super.getLeft(this.cellspacing);
    }

    @Override // com.aowagie.text.Rectangle
    public float getRight() {
        return super.getRight(this.cellspacing);
    }

    @Override // com.aowagie.text.Rectangle
    public float getTop() {
        return super.getTop(this.cellspacing);
    }

    @Override // com.aowagie.text.Rectangle
    public float getBottom() {
        return super.getBottom(this.cellspacing);
    }

    private void addLine(PdfLine line) {
        this.lines.add(line);
        this.contentHeight += line.height();
        this.lastLine = line;
        this.line = null;
    }

    private PdfLine removeLine(int index) {
        PdfLine oldLine = (PdfLine) this.lines.remove(index);
        this.contentHeight -= oldLine.height();
        if (index == 0 && !this.lines.isEmpty()) {
            this.firstLine = (PdfLine) this.lines.get(0);
            float firstLineRealHeight = firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = firstLineRealHeight;
            this.contentHeight += firstLineRealHeight;
        }
        return oldLine;
    }

    private void flushCurrentLine() {
        if (this.line != null && this.line.size() > 0) {
            addLine(this.line);
        }
    }

    private float firstLineRealHeight() {
        PdfChunk chunk;
        float firstLineRealHeight = 0.0f;
        if (this.firstLine != null && (chunk = this.firstLine.getChunk(0)) != null) {
            Image image = chunk.getImage();
            if (image != null) {
                firstLineRealHeight = this.firstLine.getChunk(0).getImage().getScaledHeight();
            } else {
                firstLineRealHeight = this.useAscender ? this.firstLine.getAscender() : this.leading;
            }
        }
        return firstLineRealHeight;
    }

    private float getBorderWidthInside(int side) {
        float width = 0.0f;
        if (this.useBorderPadding) {
            switch (side) {
                case 1:
                    width = getBorderWidthTop();
                    break;
                case 4:
                    width = getBorderWidthLeft();
                    break;
                case 8:
                    width = getBorderWidthRight();
                    break;
                default:
                    width = getBorderWidthBottom();
                    break;
            }
            if (!isUseVariableBorders()) {
                width /= 2.0f;
            }
        }
        return width;
    }

    private float addImage(Image i, float left, float right, float extraHeight, int alignment) {
        Image image = Image.getInstance(i);
        if (image.getScaledWidth() > right - left) {
            image.scaleToFit(right - left, Float.MAX_VALUE);
        }
        flushCurrentLine();
        if (this.line == null) {
            this.line = new PdfLine(left, right, alignment, this.leading);
        }
        PdfLine imageLine = this.line;
        float right2 = right - left;
        float left2 = 0.0f;
        if ((image.getAlignment() & 2) == 2) {
            left2 = right2 - image.getScaledWidth();
        } else if ((image.getAlignment() & 1) == 1) {
            left2 = ColumnText.GLOBAL_SPACE_CHAR_RATIO + (((right2 - ColumnText.GLOBAL_SPACE_CHAR_RATIO) - image.getScaledWidth()) / 2.0f);
        }
        Chunk imageChunk = new Chunk(image, left2, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        imageLine.add(new PdfChunk(imageChunk, (PdfAction) null));
        addLine(imageLine);
        return imageLine.height();
    }

    public ArrayList getLines(float top, float bottom) {
        float currentPosition = Math.min(getTop(), top);
        setTop(currentPosition + this.cellspacing);
        ArrayList result = new ArrayList();
        if (getTop() < bottom) {
            return result;
        }
        int size = this.lines.size();
        boolean aboveBottom = true;
        for (int i = 0; i < size && aboveBottom; i++) {
            this.line = (PdfLine) this.lines.get(i);
            float lineHeight = this.line.height();
            currentPosition -= lineHeight;
            if (currentPosition > bottom + this.cellpadding + getBorderWidthInside(2)) {
                result.add(this.line);
            } else {
                aboveBottom = false;
            }
        }
        float difference = 0.0f;
        if (!this.header) {
            if (aboveBottom) {
                this.lines = new ArrayList();
                this.contentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            } else {
                int size2 = result.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    this.line = removeLine(0);
                    difference += this.line.height();
                }
            }
        }
        if (difference > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            for (Image image : this.images) {
                image.setAbsolutePosition(image.getAbsoluteX(), (image.getAbsoluteY() - difference) - this.leading);
            }
        }
        return result;
    }

    public ArrayList getImages(float top, float bottom) {
        if (getTop() < bottom) {
            return new ArrayList();
        }
        float top2 = Math.min(getTop(), top);
        ArrayList result = new ArrayList();
        Iterator i = this.images.iterator();
        while (i.hasNext() && !this.header) {
            Image image = (Image) i.next();
            float height = image.getAbsoluteY();
            if (top2 - height > bottom + this.cellpadding) {
                image.setAbsolutePosition(image.getAbsoluteX(), top2 - height);
                result.add(image);
                i.remove();
            }
        }
        return result;
    }

    boolean isHeader() {
        return this.header;
    }

    void setHeader() {
        this.header = true;
    }

    boolean mayBeRemoved() {
        return this.header || (this.lines.isEmpty() && this.images.isEmpty());
    }

    private float remainingLinesHeight() {
        if (this.lines.isEmpty()) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        float result = 0.0f;
        int size = this.lines.size();
        for (int i = 0; i < size; i++) {
            PdfLine line = (PdfLine) this.lines.get(i);
            result += line.height();
        }
        return result;
    }

    public float remainingHeight() {
        float result = 0.0f;
        for (Image image : this.images) {
            result += image.getScaledHeight();
        }
        return remainingLinesHeight() + this.cellspacing + (2.0f * this.cellpadding) + result;
    }

    int rownumber() {
        return this.rownumber;
    }

    public int rowspan() {
        return this.rowspan;
    }

    private float cellspacing() {
        return this.cellspacing;
    }

    private float cellpadding() {
        return this.cellpadding;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void processActions(Element element, PdfAction action, ArrayList allActions) {
        String url;
        if (element.type() == 17 && (url = ((Anchor) element).getReference()) != null) {
            action = new PdfAction(url);
        }
        switch (element.type()) {
            case 10:
                allActions.add(action);
                break;
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 17:
                Iterator i = ((ArrayList) element).iterator();
                while (i.hasNext()) {
                    processActions((Element) i.next(), action, allActions);
                }
                break;
            case 14:
                Iterator i2 = ((List) element).getItems().iterator();
                while (i2.hasNext()) {
                    processActions((Element) i2.next(), action, allActions);
                }
                break;
            default:
                int n = element.getChunks().size();
                while (true) {
                    int i3 = n;
                    n--;
                    if (i3 > 0) {
                        allActions.add(action);
                    }
                    break;
                }
                break;
        }
    }

    public int getGroupNumber() {
        return this.groupNumber;
    }

    void setGroupNumber(int number) {
        this.groupNumber = number;
    }

    @Override // com.aowagie.text.Rectangle
    public Rectangle rectangle(float top, float bottom) {
        Rectangle tmp = new Rectangle(getLeft(), getBottom(), getRight(), getTop());
        tmp.cloneNonPositionParameters(this);
        if (getTop() > top) {
            tmp.setTop(top);
            tmp.setBorder(this.border - (this.border & 1));
        }
        if (getBottom() < bottom) {
            tmp.setBottom(bottom);
            tmp.setBorder(this.border - (this.border & 2));
        }
        return tmp;
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
}
