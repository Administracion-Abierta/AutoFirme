package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.ListItem;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfLine.class */
public class PdfLine {
    protected ArrayList line;
    protected float left;
    protected float width;
    protected int alignment;
    protected float height;
    protected Chunk listSymbol;
    protected float symbolIndent;
    protected boolean newlineSplit;
    protected float originalWidth;
    protected boolean isRTL;

    PdfLine(float left, float right, int alignment, float height) {
        this.listSymbol = null;
        this.newlineSplit = false;
        this.isRTL = false;
        this.left = left;
        this.width = right - left;
        this.originalWidth = this.width;
        this.alignment = alignment;
        this.height = height;
        this.line = new ArrayList();
    }

    PdfLine(float left, float originalWidth, float remainingWidth, int alignment, boolean newlineSplit, ArrayList line, boolean isRTL) {
        this.listSymbol = null;
        this.newlineSplit = false;
        this.isRTL = false;
        this.left = left;
        this.originalWidth = originalWidth;
        this.width = remainingWidth;
        this.alignment = alignment;
        this.line = line;
        this.newlineSplit = newlineSplit;
        this.isRTL = isRTL;
    }

    PdfChunk add(PdfChunk chunk) {
        if (chunk == null || chunk.toString().equals("")) {
            return null;
        }
        PdfChunk overflow = chunk.split(this.width);
        this.newlineSplit = chunk.isNewlineSplit() || overflow == null;
        if (chunk.isTab()) {
            Object[] tab = (Object[]) chunk.getAttribute(Chunk.TAB);
            float tabPosition = ((Float) tab[1]).floatValue();
            boolean newline = ((Boolean) tab[2]).booleanValue();
            if (newline && tabPosition < this.originalWidth - this.width) {
                return chunk;
            }
            this.width = this.originalWidth - tabPosition;
            chunk.adjustLeft(this.left);
            addToLine(chunk);
        } else if (chunk.length() > 0 || chunk.isImage()) {
            if (overflow != null) {
                chunk.trimLastSpace();
            }
            this.width -= chunk.width();
            addToLine(chunk);
        } else {
            if (this.line.size() < 1) {
                PdfChunk overflow2 = overflow.truncate(this.width);
                this.width -= overflow.width();
                if (overflow.length() > 0) {
                    addToLine(overflow);
                    return overflow2;
                }
                if (overflow2 != null) {
                    addToLine(overflow2);
                    return null;
                }
                return null;
            }
            this.width += ((PdfChunk) this.line.get(this.line.size() - 1)).trimLastSpace();
        }
        return overflow;
    }

    private void addToLine(PdfChunk chunk) {
        if (chunk.changeLeading && chunk.isImage()) {
            float f = chunk.getImage().getScaledHeight() + chunk.getImageOffsetY() + chunk.getImage().getBorderWidthTop();
            if (f > this.height) {
                this.height = f;
            }
        }
        this.line.add(chunk);
    }

    public int size() {
        return this.line.size();
    }

    public Iterator iterator() {
        return this.line.iterator();
    }

    float height() {
        return this.height;
    }

    float indentLeft() {
        if (this.isRTL) {
            switch (this.alignment) {
                case 0:
                    return this.left + this.width;
                case 1:
                    return this.left + (this.width / 2.0f);
                default:
                    return this.left;
            }
        }
        if (getSeparatorCount() == 0) {
            switch (this.alignment) {
                case 1:
                    return this.left + (this.width / 2.0f);
                case 2:
                    return this.left + this.width;
            }
        }
        return this.left;
    }

    public boolean hasToBeJustified() {
        return (this.alignment == 3 || this.alignment == 8) && this.width != ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public void resetAlignment() {
        if (this.alignment == 3) {
            this.alignment = 0;
        }
    }

    void setExtraIndent(float extra) {
        this.left += extra;
        this.width -= extra;
    }

    float widthLeft() {
        return this.width;
    }

    int numberOfSpaces() {
        String string = toString();
        int length = string.length();
        int numberOfSpaces = 0;
        for (int i = 0; i < length; i++) {
            if (string.charAt(i) == ' ') {
                numberOfSpaces++;
            }
        }
        return numberOfSpaces;
    }

    public void setListItem(ListItem listItem) {
        this.listSymbol = listItem.getListSymbol();
        this.symbolIndent = listItem.getIndentationLeft();
    }

    public Chunk listSymbol() {
        return this.listSymbol;
    }

    public float listIndent() {
        return this.symbolIndent;
    }

    public String toString() {
        StringBuffer tmp = new StringBuffer();
        Iterator i = this.line.iterator();
        while (i.hasNext()) {
            tmp.append(((PdfChunk) i.next()).toString());
        }
        return tmp.toString();
    }

    public int GetLineLengthUtf32() {
        int total = 0;
        Iterator i = this.line.iterator();
        while (i.hasNext()) {
            total += ((PdfChunk) i.next()).lengthUtf32();
        }
        return total;
    }

    public boolean isNewlineSplit() {
        return this.newlineSplit && this.alignment != 8;
    }

    public int getLastStrokeChunk() {
        int lastIdx = this.line.size() - 1;
        while (lastIdx >= 0) {
            PdfChunk chunk = (PdfChunk) this.line.get(lastIdx);
            if (chunk.isStroked()) {
                break;
            }
            lastIdx--;
        }
        return lastIdx;
    }

    public PdfChunk getChunk(int idx) {
        if (idx < 0 || idx >= this.line.size()) {
            return null;
        }
        return (PdfChunk) this.line.get(idx);
    }

    public float getOriginalWidth() {
        return this.originalWidth;
    }

    float[] getMaxSize() {
        float normal_leading = 0.0f;
        float image_leading = -10000.0f;
        for (int k = 0; k < this.line.size(); k++) {
            PdfChunk chunk = (PdfChunk) this.line.get(k);
            if (!chunk.isImage()) {
                normal_leading = Math.max(chunk.font().size(), normal_leading);
            } else {
                image_leading = Math.max(chunk.getImage().getScaledHeight() + chunk.getImageOffsetY(), image_leading);
            }
        }
        return new float[]{normal_leading, image_leading};
    }

    boolean isRTL() {
        return this.isRTL;
    }

    int getSeparatorCount() {
        int s = 0;
        for (PdfChunk ck : this.line) {
            if (ck.isTab()) {
                return 0;
            }
            if (ck.isHorizontalSeparator()) {
                s++;
            }
        }
        return s;
    }

    public float getWidthCorrected(float charSpacing, float wordSpacing) {
        float total = 0.0f;
        for (int k = 0; k < this.line.size(); k++) {
            PdfChunk ck = (PdfChunk) this.line.get(k);
            total += ck.getWidthCorrected(charSpacing, wordSpacing);
        }
        return total;
    }

    public float getAscender() {
        float fMax;
        float ascender = 0.0f;
        for (int k = 0; k < this.line.size(); k++) {
            PdfChunk ck = (PdfChunk) this.line.get(k);
            if (ck.isImage()) {
                fMax = Math.max(ascender, ck.getImage().getScaledHeight() + ck.getImageOffsetY());
            } else {
                PdfFont font = ck.font();
                fMax = Math.max(ascender, font.getFont().getFontDescriptor(1, font.size()));
            }
            ascender = fMax;
        }
        return ascender;
    }

    public float getDescender() {
        float fMin;
        float descender = 0.0f;
        for (int k = 0; k < this.line.size(); k++) {
            PdfChunk ck = (PdfChunk) this.line.get(k);
            if (ck.isImage()) {
                fMin = Math.min(descender, ck.getImageOffsetY());
            } else {
                PdfFont font = ck.font();
                fMin = Math.min(descender, font.getFont().getFontDescriptor(3, font.size()));
            }
            descender = fMin;
        }
        return descender;
    }
}
