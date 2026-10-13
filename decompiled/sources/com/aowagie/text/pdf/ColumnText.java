package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Element;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.List;
import com.aowagie.text.ListItem;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Phrase;
import com.aowagie.text.SimpleTable;
import com.aowagie.text.pdf.draw.DrawInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ColumnText.class */
public class ColumnText {
    public static final int AR_NOVOWEL = 1;
    public static final int AR_COMPOSEDTASHKEEL = 4;
    public static final int AR_LIG = 8;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    public static final float GLOBAL_SPACE_CHAR_RATIO = 0.0f;
    public static final int START_COLUMN = 0;
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected static final int LINE_STATUS_OK = 0;
    protected static final int LINE_STATUS_OFFLIMITS = 1;
    protected static final int LINE_STATUS_NOLINE = 2;
    protected float maxY;
    protected float minY;
    protected float leftX;
    protected float rightX;
    protected ArrayList leftWall;
    protected ArrayList rightWall;
    protected BidiLine bidiLine;
    protected float yLine;
    protected PdfContentByte canvas;
    protected PdfContentByte[] canvases;
    protected int lineStatus;
    private int linesWritten;
    private float firstLineY;
    protected float descender;
    protected ColumnText compositeColumn;
    protected LinkedList compositeElements;
    private boolean splittedRow;
    protected Phrase waitPhrase;
    private float filledWidth;
    protected int runDirection = 0;
    protected int alignment = 0;
    protected float currentLeading = 16.0f;
    protected float fixedLeading = 16.0f;
    protected float multipliedLeading = GLOBAL_SPACE_CHAR_RATIO;
    protected float indent = GLOBAL_SPACE_CHAR_RATIO;
    protected float followingIndent = GLOBAL_SPACE_CHAR_RATIO;
    protected float rightIndent = GLOBAL_SPACE_CHAR_RATIO;
    protected float extraParagraphSpace = GLOBAL_SPACE_CHAR_RATIO;
    protected float rectangularWidth = -1.0f;
    protected boolean rectangularMode = false;
    private float spaceCharRatio = GLOBAL_SPACE_CHAR_RATIO;
    private boolean lastWasNewline = true;
    private boolean firstLineYDone = false;
    private int arabicOptions = 0;
    protected boolean composite = false;
    protected int listIdx = 0;
    private boolean useAscender = false;
    private boolean adjustFirstLine = true;

    public ColumnText(PdfContentByte canvas) {
        this.canvas = canvas;
    }

    public static ColumnText duplicate(ColumnText org2) {
        ColumnText ct = new ColumnText(null);
        ct.setACopy(org2);
        return ct;
    }

    public ColumnText setACopy(ColumnText org2) {
        setSimpleVars(org2);
        if (org2.bidiLine != null) {
            this.bidiLine = new BidiLine(org2.bidiLine);
        }
        return this;
    }

    protected void setSimpleVars(ColumnText org2) {
        this.maxY = org2.maxY;
        this.minY = org2.minY;
        this.alignment = org2.alignment;
        this.leftWall = null;
        if (org2.leftWall != null) {
            this.leftWall = new ArrayList(org2.leftWall);
        }
        this.rightWall = null;
        if (org2.rightWall != null) {
            this.rightWall = new ArrayList(org2.rightWall);
        }
        this.yLine = org2.yLine;
        this.currentLeading = org2.currentLeading;
        this.fixedLeading = org2.fixedLeading;
        this.multipliedLeading = org2.multipliedLeading;
        this.canvas = org2.canvas;
        this.canvases = org2.canvases;
        this.lineStatus = org2.lineStatus;
        this.indent = org2.indent;
        this.followingIndent = org2.followingIndent;
        this.rightIndent = org2.rightIndent;
        this.extraParagraphSpace = org2.extraParagraphSpace;
        this.rectangularWidth = org2.rectangularWidth;
        this.rectangularMode = org2.rectangularMode;
        this.spaceCharRatio = org2.spaceCharRatio;
        this.lastWasNewline = org2.lastWasNewline;
        this.linesWritten = org2.linesWritten;
        this.arabicOptions = org2.arabicOptions;
        this.runDirection = org2.runDirection;
        this.descender = org2.descender;
        this.composite = org2.composite;
        this.splittedRow = org2.splittedRow;
        if (org2.composite) {
            this.compositeElements = new LinkedList(org2.compositeElements);
            if (this.splittedRow) {
                PdfPTable table = (PdfPTable) this.compositeElements.getFirst();
                this.compositeElements.set(0, new PdfPTable(table));
            }
            if (org2.compositeColumn != null) {
                this.compositeColumn = duplicate(org2.compositeColumn);
            }
        }
        this.listIdx = org2.listIdx;
        this.firstLineY = org2.firstLineY;
        this.leftX = org2.leftX;
        this.rightX = org2.rightX;
        this.firstLineYDone = org2.firstLineYDone;
        this.waitPhrase = org2.waitPhrase;
        this.useAscender = org2.useAscender;
        this.filledWidth = org2.filledWidth;
        this.adjustFirstLine = org2.adjustFirstLine;
    }

    private void addWaitingPhrase() {
        if (this.bidiLine == null && this.waitPhrase != null) {
            this.bidiLine = new BidiLine();
            Iterator j = this.waitPhrase.getChunks().iterator();
            while (j.hasNext()) {
                this.bidiLine.addChunk(new PdfChunk((Chunk) j.next(), (PdfAction) null));
            }
            this.waitPhrase = null;
        }
    }

    public void addText(Phrase phrase) {
        if (phrase == null || this.composite) {
            return;
        }
        addWaitingPhrase();
        if (this.bidiLine == null) {
            this.waitPhrase = phrase;
            return;
        }
        Iterator j = phrase.getChunks().iterator();
        while (j.hasNext()) {
            this.bidiLine.addChunk(new PdfChunk((Chunk) j.next(), (PdfAction) null));
        }
    }

    public void setText(Phrase phrase) {
        this.bidiLine = null;
        this.composite = false;
        this.compositeColumn = null;
        this.compositeElements = null;
        this.listIdx = 0;
        this.splittedRow = false;
        this.waitPhrase = phrase;
    }

    public void addText(Chunk chunk) {
        if (chunk == null || this.composite) {
            return;
        }
        addText(new Phrase(chunk));
    }

    public void addElement(Element element) {
        if (element == null) {
            return;
        }
        if (element instanceof Image) {
            Image img = (Image) element;
            PdfPTable t = new PdfPTable(1);
            float w = img.getWidthPercentage();
            if (w == GLOBAL_SPACE_CHAR_RATIO) {
                t.setTotalWidth(img.getScaledWidth());
                t.setLockedWidth(true);
            } else {
                t.setWidthPercentage(w);
            }
            t.setSpacingAfter(img.getSpacingAfter());
            t.setSpacingBefore(img.getSpacingBefore());
            switch (img.getAlignment()) {
                case 0:
                    t.setHorizontalAlignment(0);
                    break;
                case 2:
                    t.setHorizontalAlignment(2);
                    break;
                default:
                    t.setHorizontalAlignment(1);
                    break;
            }
            PdfPCell c = new PdfPCell(img, true);
            c.setPadding(GLOBAL_SPACE_CHAR_RATIO);
            c.setBorder(img.getBorder());
            c.setBorderColor(img.getBorderColor());
            c.setBorderWidth(img.getBorderWidth());
            c.setBackgroundColor(img.getBackgroundColor());
            t.addCell(c);
            element = t;
        }
        if (element.type() == 10) {
            element = new Paragraph((Chunk) element);
        } else if (element.type() == 11) {
            element = new Paragraph((Phrase) element);
        }
        if (element instanceof SimpleTable) {
            try {
                element = ((SimpleTable) element).createPdfPTable();
            } catch (DocumentException e) {
                throw new IllegalArgumentException("Element not allowed.");
            }
        } else if (element.type() != 12 && element.type() != 14 && element.type() != 23 && element.type() != 55) {
            throw new IllegalArgumentException("Element not allowed.");
        }
        if (!this.composite) {
            this.composite = true;
            this.compositeElements = new LinkedList();
            this.bidiLine = null;
            this.waitPhrase = null;
        }
        this.compositeElements.add(element);
    }

    protected ArrayList convertColumn(float[] cLine) {
        if (cLine.length < 4) {
            throw new RuntimeException("No valid column line found.");
        }
        ArrayList cc = new ArrayList();
        for (int k = 0; k < cLine.length - 2; k += 2) {
            float x1 = cLine[k];
            float y1 = cLine[k + 1];
            float x2 = cLine[k + 2];
            float y2 = cLine[k + 3];
            if (y1 != y2) {
                float a = (x1 - x2) / (y1 - y2);
                float b = x1 - (a * y1);
                float[] r = {Math.min(y1, y2), Math.max(y1, y2), a, b};
                cc.add(r);
                this.maxY = Math.max(this.maxY, r[1]);
                this.minY = Math.min(this.minY, r[0]);
            }
        }
        if (cc.isEmpty()) {
            throw new RuntimeException("No valid column line found.");
        }
        return cc;
    }

    protected float findLimitsPoint(ArrayList wall) {
        this.lineStatus = 0;
        if (this.yLine < this.minY || this.yLine > this.maxY) {
            this.lineStatus = 1;
            return GLOBAL_SPACE_CHAR_RATIO;
        }
        for (int k = 0; k < wall.size(); k++) {
            float[] r = (float[]) wall.get(k);
            if (this.yLine >= r[0] && this.yLine <= r[1]) {
                return (r[2] * this.yLine) + r[3];
            }
        }
        this.lineStatus = 2;
        return GLOBAL_SPACE_CHAR_RATIO;
    }

    protected float[] findLimitsOneLine() {
        float x1 = findLimitsPoint(this.leftWall);
        if (this.lineStatus == 1 || this.lineStatus == 2) {
            return null;
        }
        float x2 = findLimitsPoint(this.rightWall);
        if (this.lineStatus == 2) {
            return null;
        }
        return new float[]{x1, x2};
    }

    protected float[] findLimitsTwoLines() {
        boolean repeat = false;
        while (true) {
            if (repeat && this.currentLeading == GLOBAL_SPACE_CHAR_RATIO) {
                return null;
            }
            repeat = true;
            float[] x1 = findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            this.yLine -= this.currentLeading;
            if (this.lineStatus != 2) {
                float[] x2 = findLimitsOneLine();
                if (this.lineStatus == 1) {
                    return null;
                }
                if (this.lineStatus == 2) {
                    this.yLine -= this.currentLeading;
                } else if (x1[0] < x2[1] && x2[0] < x1[1]) {
                    return new float[]{x1[0], x1[1], x2[0], x2[1]};
                }
            }
        }
    }

    public void setColumns(float[] leftLine, float[] rightLine) {
        this.maxY = -1.0E21f;
        this.minY = 1.0E21f;
        setYLine(Math.max(leftLine[1], leftLine[leftLine.length - 1]));
        this.rightWall = convertColumn(rightLine);
        this.leftWall = convertColumn(leftLine);
        this.rectangularWidth = -1.0f;
        this.rectangularMode = false;
    }

    public void setSimpleColumn(Phrase phrase, float llx, float lly, float urx, float ury, float leading, int alignment) {
        addText(phrase);
        setSimpleColumn(llx, lly, urx, ury, leading, alignment);
    }

    public void setSimpleColumn(float llx, float lly, float urx, float ury, float leading, int alignment) {
        setLeading(leading);
        this.alignment = alignment;
        setSimpleColumn(llx, lly, urx, ury);
    }

    public void setSimpleColumn(float llx, float lly, float urx, float ury) {
        this.leftX = Math.min(llx, urx);
        this.maxY = Math.max(lly, ury);
        this.minY = Math.min(lly, ury);
        this.rightX = Math.max(llx, urx);
        this.yLine = this.maxY;
        this.rectangularWidth = this.rightX - this.leftX;
        if (this.rectangularWidth < GLOBAL_SPACE_CHAR_RATIO) {
            this.rectangularWidth = GLOBAL_SPACE_CHAR_RATIO;
        }
        this.rectangularMode = true;
    }

    public void setLeading(float leading) {
        this.fixedLeading = leading;
        this.multipliedLeading = GLOBAL_SPACE_CHAR_RATIO;
    }

    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.fixedLeading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }

    public float getLeading() {
        return this.fixedLeading;
    }

    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }

    public void setYLine(float yLine) {
        this.yLine = yLine;
    }

    public float getYLine() {
        return this.yLine;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setIndent(float indent) {
        this.indent = indent;
        this.lastWasNewline = true;
    }

    public float getIndent() {
        return this.indent;
    }

    public void setFollowingIndent(float indent) {
        this.followingIndent = indent;
        this.lastWasNewline = true;
    }

    public float getFollowingIndent() {
        return this.followingIndent;
    }

    public void setRightIndent(float indent) {
        this.rightIndent = indent;
        this.lastWasNewline = true;
    }

    public float getRightIndent() {
        return this.rightIndent;
    }

    public int go() throws DocumentException {
        return go(false);
    }

    /* JADX WARN: Code duplicated, block: B:88:0x0295  */
    /* JADX WARN: Code duplicated, block: B:90:0x02a7  */
    /* JADX WARN: Code duplicated, block: B:91:0x02ae  */
    /* JADX WARN: Code duplicated, block: B:95:0x02eb  */
    /* JADX WARN: Code duplicated, block: B:96:0x02f2  */
    public int go(boolean simulate) throws DocumentException {
        int status;
        PdfLine line;
        float x1;
        float f;
        float f2;
        if (this.composite) {
            return goComposite(simulate);
        }
        addWaitingPhrase();
        if (this.bidiLine == null) {
            return 1;
        }
        this.descender = GLOBAL_SPACE_CHAR_RATIO;
        this.linesWritten = 0;
        boolean dirty = false;
        float ratio = this.spaceCharRatio;
        Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        Float lastBaseFactor = Float.valueOf(GLOBAL_SPACE_CHAR_RATIO);
        currentValues[1] = lastBaseFactor;
        PdfDocument pdf = null;
        PdfContentByte graphics = null;
        PdfContentByte text = null;
        this.firstLineY = Float.NaN;
        int localRunDirection = 1;
        if (this.runDirection != 0) {
            localRunDirection = this.runDirection;
        }
        if (this.canvas != null) {
            graphics = this.canvas;
            pdf = this.canvas.getPdfDocument();
            text = this.canvas.getDuplicate();
        } else if (!simulate) {
            throw new NullPointerException("ColumnText.go with simulate==false and text==null.");
        }
        if (!simulate) {
            if (ratio == GLOBAL_SPACE_CHAR_RATIO) {
                ratio = text.getPdfWriter().getSpaceCharRatio();
            } else if (ratio < 0.001f) {
                ratio = 0.001f;
            }
        }
        while (true) {
            float firstIndent = this.lastWasNewline ? this.indent : this.followingIndent;
            if (!this.rectangularMode) {
                float yTemp = this.yLine;
                float[] xx = findLimitsTwoLines();
                if (xx == null) {
                    status = 2;
                    if (this.bidiLine.isEmpty()) {
                        status = 2 | 1;
                    }
                    this.yLine = yTemp;
                    break;
                }
                if (this.bidiLine.isEmpty()) {
                    status = 1;
                    this.yLine = yTemp;
                    break;
                }
                x1 = Math.max(xx[0], xx[2]);
                float x2 = Math.min(xx[1], xx[3]);
                if (x2 - x1 > firstIndent + this.rightIndent) {
                    if (!simulate && !dirty) {
                        text.beginText();
                        dirty = true;
                    }
                    line = this.bidiLine.processLine(x1, ((x2 - x1) - firstIndent) - this.rightIndent, this.alignment, localRunDirection, this.arabicOptions);
                    if (line == null) {
                        status = 1;
                        this.yLine = yTemp;
                        break;
                    }
                    if (!simulate) {
                        currentValues[0] = currentFont;
                        PdfContentByte pdfContentByte = text;
                        float f3 = x1;
                        if (line.isRTL()) {
                            f2 = this.rightIndent;
                        } else {
                            f2 = firstIndent;
                        }
                        pdfContentByte.setTextMatrix(f3 + f2 + line.indentLeft(), this.yLine);
                        pdf.writeLineToContent(line, text, graphics, currentValues, ratio);
                        currentFont = (PdfFont) currentValues[0];
                    }
                    this.lastWasNewline = line.isNewlineSplit();
                    float f4 = this.yLine;
                    if (line.isNewlineSplit()) {
                        f = this.extraParagraphSpace;
                    } else {
                        f = GLOBAL_SPACE_CHAR_RATIO;
                    }
                    this.yLine = f4 - f;
                    this.linesWritten++;
                    this.descender = line.getDescender();
                } else {
                    continue;
                }
            } else {
                if (this.rectangularWidth <= firstIndent + this.rightIndent) {
                    status = 2;
                    if (!this.bidiLine.isEmpty()) {
                        break;
                    }
                    status = 2 | 1;
                    break;
                }
                if (this.bidiLine.isEmpty()) {
                    status = 1;
                    break;
                }
                line = this.bidiLine.processLine(this.leftX, (this.rectangularWidth - firstIndent) - this.rightIndent, this.alignment, localRunDirection, this.arabicOptions);
                if (line == null) {
                    status = 1;
                    break;
                }
                float[] maxSize = line.getMaxSize();
                if (isUseAscender() && Float.isNaN(this.firstLineY)) {
                    this.currentLeading = line.getAscender();
                } else {
                    this.currentLeading = Math.max(this.fixedLeading + (maxSize[0] * this.multipliedLeading), maxSize[1]);
                }
                if (this.yLine > this.maxY || this.yLine - this.currentLeading < this.minY) {
                    status = 2;
                    this.bidiLine.restore();
                    break;
                }
                this.yLine -= this.currentLeading;
                if (!simulate && !dirty) {
                    text.beginText();
                    dirty = true;
                }
                if (Float.isNaN(this.firstLineY)) {
                    this.firstLineY = this.yLine;
                }
                updateFilledWidth(this.rectangularWidth - line.widthLeft());
                x1 = this.leftX;
                if (!simulate) {
                    currentValues[0] = currentFont;
                    PdfContentByte pdfContentByte2 = text;
                    float f5 = x1;
                    if (line.isRTL()) {
                        f2 = this.rightIndent;
                    } else {
                        f2 = firstIndent;
                    }
                    pdfContentByte2.setTextMatrix(f5 + f2 + line.indentLeft(), this.yLine);
                    pdf.writeLineToContent(line, text, graphics, currentValues, ratio);
                    currentFont = (PdfFont) currentValues[0];
                }
                this.lastWasNewline = line.isNewlineSplit();
                float f6 = this.yLine;
                if (line.isNewlineSplit()) {
                    f = this.extraParagraphSpace;
                } else {
                    f = GLOBAL_SPACE_CHAR_RATIO;
                }
                this.yLine = f6 - f;
                this.linesWritten++;
                this.descender = line.getDescender();
            }
        }
        if (dirty) {
            text.endText();
            this.canvas.add(text);
        }
        return status;
    }

    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }

    public void clearChunks() {
        if (this.bidiLine != null) {
            this.bidiLine.clearChunks();
        }
    }

    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.spaceCharRatio = spaceCharRatio;
    }

    public void setRunDirection(int runDirection) {
        if (runDirection < 0 || runDirection > 3) {
            throw new RuntimeException("Invalid run direction: " + runDirection);
        }
        this.runDirection = runDirection;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public int getLinesWritten() {
        return this.linesWritten;
    }

    public int getArabicOptions() {
        return this.arabicOptions;
    }

    public void setArabicOptions(int arabicOptions) {
        this.arabicOptions = arabicOptions;
    }

    public float getDescender() {
        return this.descender;
    }

    public static float getWidth(Phrase phrase, int runDirection, int arabicOptions) {
        ColumnText ct = new ColumnText(null);
        ct.addText(phrase);
        ct.addWaitingPhrase();
        PdfLine line = ct.bidiLine.processLine(GLOBAL_SPACE_CHAR_RATIO, 20000.0f, 0, runDirection, arabicOptions);
        if (line == null) {
            return GLOBAL_SPACE_CHAR_RATIO;
        }
        return 20000.0f - line.widthLeft();
    }

    public static float getWidth(Phrase phrase) {
        return getWidth(phrase, 1, 0);
    }

    public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation, int runDirection, int arabicOptions) {
        float llx;
        float urx;
        if (alignment != 0 && alignment != 1 && alignment != 2) {
            alignment = 0;
        }
        canvas.saveState();
        ColumnText ct = new ColumnText(canvas);
        float lly = -1.0f;
        float ury = 2.0f;
        switch (alignment) {
            case 0:
                llx = 0.0f;
                urx = 20000.0f;
                break;
            case 2:
                llx = -20000.0f;
                urx = 0.0f;
                break;
            default:
                llx = -20000.0f;
                urx = 20000.0f;
                break;
        }
        if (rotation == GLOBAL_SPACE_CHAR_RATIO) {
            llx += x;
            lly = (-1.0f) + y;
            urx += x;
            ury = 2.0f + y;
        } else {
            double alpha = (((double) rotation) * 3.141592653589793d) / 180.0d;
            float cos = (float) Math.cos(alpha);
            float sin = (float) Math.sin(alpha);
            canvas.concatCTM(cos, sin, -sin, cos, x, y);
        }
        ct.setSimpleColumn(phrase, llx, lly, urx, ury, 2.0f, alignment);
        if (runDirection == 3) {
            if (alignment == 0) {
                alignment = 2;
            } else if (alignment == 2) {
                alignment = 0;
            }
        }
        ct.setAlignment(alignment);
        ct.setArabicOptions(arabicOptions);
        ct.setRunDirection(runDirection);
        try {
            ct.go();
            canvas.restoreState();
        } catch (DocumentException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation) {
        showTextAligned(canvas, alignment, phrase, x, y, rotation, 1, 0);
    }

    /* JADX WARN: Code duplicated, block: B:108:0x0480  */
    /* JADX WARN: Code duplicated, block: B:115:0x0495  */
    /* JADX WARN: Code duplicated, block: B:138:0x0528  */
    /* JADX WARN: Code duplicated, block: B:142:0x055d  */
    /* JADX WARN: Code duplicated, block: B:211:0x070e  */
    /* JADX WARN: Code duplicated, block: B:214:0x071f  */
    /* JADX WARN: Code duplicated, block: B:236:0x07c4  */
    /* JADX WARN: Code duplicated, block: B:238:0x07cc  */
    /* JADX WARN: Code duplicated, block: B:245:0x07f2  */
    /* JADX WARN: Code duplicated, block: B:259:0x081e  */
    /* JADX WARN: Code duplicated, block: B:262:0x083f  */
    /* JADX WARN: Code duplicated, block: B:263:0x084e  */
    /* JADX WARN: Code duplicated, block: B:266:0x086f  */
    /* JADX WARN: Code duplicated, block: B:269:0x0879 A[LOOP:3: B:267:0x0872->B:269:0x0879, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:271:0x0893  */
    /* JADX WARN: Code duplicated, block: B:274:0x08b3  */
    /* JADX WARN: Code duplicated, block: B:275:0x08b7  */
    /* JADX WARN: Code duplicated, block: B:278:0x08c4  */
    /* JADX WARN: Code duplicated, block: B:289:0x0919  */
    /* JADX WARN: Code duplicated, block: B:292:0x093c  */
    /* JADX WARN: Code duplicated, block: B:293:0x094f  */
    /* JADX WARN: Code duplicated, block: B:296:0x0967  */
    /* JADX WARN: Code duplicated, block: B:298:0x0971  */
    /* JADX WARN: Code duplicated, block: B:310:0x09b2  */
    /* JADX WARN: Code duplicated, block: B:313:0x09dc  */
    /* JADX WARN: Code duplicated, block: B:316:0x09f0 A[LOOP:5: B:314:0x09e9->B:316:0x09f0, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:331:0x0230 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:334:0x04e5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:338:0x0581 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:339:0x034f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:353:0x0818 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:356:0x07e9 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:359:0x09d5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:375:0x0020 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:376:0x0020 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:59:0x020e  */
    /* JADX WARN: Code duplicated, block: B:88:0x033a  */
    /* JADX WARN: Code duplicated, block: B:90:0x034a  */
    /* JADX WARN: Code duplicated, block: B:93:0x035f  */
    /* JADX WARN: Code duplicated, block: B:95:0x0376  */
    /* JADX WARN: Code duplicated, block: B:96:0x037d  */
    /* JADX WARN: Code duplicated, block: B:99:0x040d  */
    protected int goComposite(boolean simulate) throws DocumentException {
        int status;
        int keep;
        boolean createHere;
        boolean z;
        boolean z2;
        boolean z3;
        float tableWidth;
        ArrayList rows;
        int i;
        PdfPTable nt;
        ArrayList sub;
        boolean z4;
        boolean showFooter;
        int j;
        float rowHeight;
        PdfPRow last;
        int j2;
        if (!this.rectangularMode) {
            throw new DocumentException("Irregular columns are not supported in composite mode.");
        }
        this.linesWritten = 0;
        this.descender = GLOBAL_SPACE_CHAR_RATIO;
        boolean firstPass = this.adjustFirstLine;
        while (!this.compositeElements.isEmpty()) {
            Element element = (Element) this.compositeElements.getFirst();
            if (element.type() == 12) {
                Paragraph para = (Paragraph) element;
                int status2 = 0;
                int keep2 = 0;
                while (keep2 < 2) {
                    float lastY = this.yLine;
                    boolean createHere2 = false;
                    if (this.compositeColumn == null) {
                        this.compositeColumn = new ColumnText(this.canvas);
                        this.compositeColumn.setUseAscender(firstPass ? this.useAscender : false);
                        this.compositeColumn.setAlignment(para.getAlignment());
                        this.compositeColumn.setIndent(para.getIndentationLeft() + para.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(para.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(para.getIndentationLeft());
                        this.compositeColumn.setRightIndent(para.getIndentationRight());
                        this.compositeColumn.setLeading(para.getLeading(), para.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText(para);
                        if (!firstPass) {
                            this.yLine -= para.getSpacingBefore();
                        }
                        createHere2 = true;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    boolean keepCandidate = para.getKeepTogether() && createHere2 && !firstPass;
                    status2 = this.compositeColumn.go(simulate || (keepCandidate && keep2 == 0));
                    updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((status2 & 1) == 0 && keepCandidate) {
                        this.compositeColumn = null;
                        this.yLine = lastY;
                        return 2;
                    }
                    if (!simulate && keepCandidate) {
                        if (keep2 == 0) {
                            this.compositeColumn = null;
                            this.yLine = lastY;
                        }
                        keep2++;
                    } else {
                        firstPass = false;
                        this.yLine = this.compositeColumn.yLine;
                        this.linesWritten += this.compositeColumn.linesWritten;
                        this.descender = this.compositeColumn.descender;
                        if ((status2 & 1) != 0) {
                            this.compositeColumn = null;
                            this.compositeElements.removeFirst();
                            this.yLine -= para.getSpacingAfter();
                        }
                        if ((status2 & 2) != 0) {
                            return 2;
                        }
                    }
                }
                firstPass = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if ((status2 & 1) != 0) {
                    this.compositeColumn = null;
                    this.compositeElements.removeFirst();
                    this.yLine -= para.getSpacingAfter();
                }
                if ((status2 & 2) != 0) {
                    return 2;
                }
            } else if (element.type() == 14) {
                List list = (List) element;
                ArrayList items = list.getItems();
                ListItem item = null;
                float listIndentation = list.getIndentationLeft();
                int count = 0;
                Stack stack = new Stack();
                int k = 0;
                while (k < items.size()) {
                    Object obj = items.get(k);
                    if (obj instanceof ListItem) {
                        if (count == this.listIdx) {
                            item = (ListItem) obj;
                            status = 0;
                            keep = 0;
                            while (true) {
                                if (keep < 2) {
                                    float lastY2 = this.yLine;
                                    createHere = false;
                                    if (this.compositeColumn == null) {
                                        if (item == null) {
                                            this.listIdx = 0;
                                            this.compositeElements.removeFirst();
                                        } else {
                                            this.compositeColumn = new ColumnText(this.canvas);
                                            ColumnText columnText = this.compositeColumn;
                                            if (firstPass) {
                                                z3 = this.useAscender;
                                            } else {
                                                z3 = false;
                                            }
                                            columnText.setUseAscender(z3);
                                            this.compositeColumn.setAlignment(item.getAlignment());
                                            this.compositeColumn.setIndent(item.getIndentationLeft() + listIndentation + item.getFirstLineIndent());
                                            this.compositeColumn.setExtraParagraphSpace(item.getExtraParagraphSpace());
                                            this.compositeColumn.setFollowingIndent(this.compositeColumn.getIndent());
                                            this.compositeColumn.setRightIndent(item.getIndentationRight() + list.getIndentationRight());
                                            this.compositeColumn.setLeading(item.getLeading(), item.getMultipliedLeading());
                                            this.compositeColumn.setRunDirection(this.runDirection);
                                            this.compositeColumn.setArabicOptions(this.arabicOptions);
                                            this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                                            this.compositeColumn.addText(item);
                                            if (!firstPass) {
                                                this.yLine -= item.getSpacingBefore();
                                            }
                                            createHere = true;
                                        }
                                    }
                                    this.compositeColumn.leftX = this.leftX;
                                    this.compositeColumn.rightX = this.rightX;
                                    this.compositeColumn.yLine = this.yLine;
                                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                                    this.compositeColumn.rectangularMode = this.rectangularMode;
                                    this.compositeColumn.minY = this.minY;
                                    this.compositeColumn.maxY = this.maxY;
                                    if (item.getKeepTogether() || !createHere || firstPass) {
                                        z = false;
                                    } else {
                                        z = true;
                                    }
                                    boolean keepCandidate2 = z;
                                    ColumnText columnText2 = this.compositeColumn;
                                    if (!simulate || (keepCandidate2 && keep == 0)) {
                                        z2 = true;
                                    } else {
                                        z2 = false;
                                    }
                                    status = columnText2.go(z2);
                                    updateFilledWidth(this.compositeColumn.filledWidth);
                                    if ((status & 1) != 0 && keepCandidate2) {
                                        this.compositeColumn = null;
                                        this.yLine = lastY2;
                                        return 2;
                                    }
                                    if (simulate && keepCandidate2) {
                                        if (keep == 0) {
                                            this.compositeColumn = null;
                                            this.yLine = lastY2;
                                        }
                                        keep++;
                                    }
                                }
                                firstPass = false;
                                this.yLine = this.compositeColumn.yLine;
                                this.linesWritten += this.compositeColumn.linesWritten;
                                this.descender = this.compositeColumn.descender;
                                if (!Float.isNaN(this.compositeColumn.firstLineY) && !this.compositeColumn.firstLineYDone) {
                                    if (!simulate) {
                                        showTextAligned(this.canvas, 0, new Phrase(item.getListSymbol()), this.compositeColumn.leftX + listIndentation, this.compositeColumn.firstLineY, GLOBAL_SPACE_CHAR_RATIO);
                                    }
                                    this.compositeColumn.firstLineYDone = true;
                                }
                                if ((status & 1) != 0) {
                                    this.compositeColumn = null;
                                    this.listIdx++;
                                    this.yLine -= item.getSpacingAfter();
                                }
                                if ((status & 2) != 0) {
                                    return 2;
                                }
                            }
                        } else {
                            count++;
                        }
                    } else {
                        if (obj instanceof List) {
                            stack.push(new Object[]{list, Integer.valueOf(k), Float.valueOf(listIndentation)});
                            list = (List) obj;
                            items = list.getItems();
                            listIndentation += list.getIndentationLeft();
                            k = -1;
                        }
                        k++;
                    }
                    if (k == items.size() - 1 && !stack.isEmpty()) {
                        Object[] objs = (Object[]) stack.pop();
                        list = (List) objs[0];
                        items = list.getItems();
                        k = ((Integer) objs[1]).intValue();
                        listIndentation = ((Float) objs[2]).floatValue();
                    }
                    k++;
                }
                status = 0;
                keep = 0;
                while (true) {
                    if (keep < 2) {
                        float lastY3 = this.yLine;
                        createHere = false;
                        if (this.compositeColumn == null) {
                            if (item == null) {
                                this.listIdx = 0;
                                this.compositeElements.removeFirst();
                            } else {
                                this.compositeColumn = new ColumnText(this.canvas);
                                ColumnText columnText3 = this.compositeColumn;
                                if (firstPass) {
                                    z3 = this.useAscender;
                                } else {
                                    z3 = false;
                                }
                                columnText3.setUseAscender(z3);
                                this.compositeColumn.setAlignment(item.getAlignment());
                                this.compositeColumn.setIndent(item.getIndentationLeft() + listIndentation + item.getFirstLineIndent());
                                this.compositeColumn.setExtraParagraphSpace(item.getExtraParagraphSpace());
                                this.compositeColumn.setFollowingIndent(this.compositeColumn.getIndent());
                                this.compositeColumn.setRightIndent(item.getIndentationRight() + list.getIndentationRight());
                                this.compositeColumn.setLeading(item.getLeading(), item.getMultipliedLeading());
                                this.compositeColumn.setRunDirection(this.runDirection);
                                this.compositeColumn.setArabicOptions(this.arabicOptions);
                                this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                                this.compositeColumn.addText(item);
                                if (!firstPass) {
                                    this.yLine -= item.getSpacingBefore();
                                }
                                createHere = true;
                            }
                        }
                        this.compositeColumn.leftX = this.leftX;
                        this.compositeColumn.rightX = this.rightX;
                        this.compositeColumn.yLine = this.yLine;
                        this.compositeColumn.rectangularWidth = this.rectangularWidth;
                        this.compositeColumn.rectangularMode = this.rectangularMode;
                        this.compositeColumn.minY = this.minY;
                        this.compositeColumn.maxY = this.maxY;
                        if (item.getKeepTogether()) {
                            z = false;
                        } else {
                            z = false;
                        }
                        boolean keepCandidate3 = z;
                        ColumnText columnText4 = this.compositeColumn;
                        if (simulate) {
                            z2 = true;
                        } else {
                            z2 = true;
                        }
                        status = columnText4.go(z2);
                        updateFilledWidth(this.compositeColumn.filledWidth);
                        if ((status & 1) != 0) {
                        }
                        if (simulate) {
                        }
                    }
                    firstPass = false;
                    this.yLine = this.compositeColumn.yLine;
                    this.linesWritten += this.compositeColumn.linesWritten;
                    this.descender = this.compositeColumn.descender;
                    if (!Float.isNaN(this.compositeColumn.firstLineY)) {
                        if (!simulate) {
                            showTextAligned(this.canvas, 0, new Phrase(item.getListSymbol()), this.compositeColumn.leftX + listIndentation, this.compositeColumn.firstLineY, GLOBAL_SPACE_CHAR_RATIO);
                        }
                        this.compositeColumn.firstLineYDone = true;
                    }
                    if ((status & 1) != 0) {
                        this.compositeColumn = null;
                        this.listIdx++;
                        this.yLine -= item.getSpacingAfter();
                    }
                    if ((status & 2) != 0) {
                        return 2;
                    }
                    keep++;
                }
            } else if (element.type() == 23) {
                if (this.yLine < this.minY || this.yLine > this.maxY) {
                    return 2;
                }
                PdfPTable table = (PdfPTable) element;
                if (table.size() <= table.getHeaderRows()) {
                    this.compositeElements.removeFirst();
                } else {
                    float yTemp = this.yLine;
                    if (!firstPass && this.listIdx == 0) {
                        yTemp -= table.spacingBefore();
                    }
                    float yLineWrite = yTemp;
                    if (yTemp < this.minY || yTemp > this.maxY) {
                        return 2;
                    }
                    this.currentLeading = GLOBAL_SPACE_CHAR_RATIO;
                    float x1 = this.leftX;
                    if (table.isLockedWidth()) {
                        tableWidth = table.getTotalWidth();
                        updateFilledWidth(tableWidth);
                    } else {
                        tableWidth = (this.rectangularWidth * table.getWidthPercentage()) / 100.0f;
                        table.setTotalWidth(tableWidth);
                    }
                    int headerRows = table.getHeaderRows();
                    int footerRows = table.getFooterRows();
                    if (footerRows > headerRows) {
                        footerRows = headerRows;
                    }
                    int realHeaderRows = headerRows - footerRows;
                    float headerHeight = table.getHeaderHeight();
                    float footerHeight = table.getFooterHeight();
                    boolean skipHeader = !firstPass && table.isSkipFirstHeader() && this.listIdx <= headerRows;
                    if (!skipHeader) {
                        yTemp -= headerHeight;
                        if (yTemp < this.minY || yTemp > this.maxY) {
                            if (firstPass) {
                                this.compositeElements.removeFirst();
                            } else {
                                return 2;
                            }
                        }
                    }
                    if (this.listIdx < headerRows) {
                        this.listIdx = headerRows;
                    }
                    if (!table.isComplete()) {
                        yTemp -= footerHeight;
                    }
                    int k2 = this.listIdx;
                    while (k2 < table.size()) {
                        float rowHeight2 = table.getRowHeight(k2);
                        if (yTemp - rowHeight2 >= this.minY) {
                            yTemp -= rowHeight2;
                            k2++;
                        } else {
                            if (!table.isComplete()) {
                                yTemp += footerHeight;
                            }
                            if (k2 >= table.size()) {
                                if (!table.isSplitRows() && (!table.isSplitLate() || (k2 == this.listIdx && firstPass))) {
                                    if (!this.splittedRow) {
                                        this.splittedRow = true;
                                        table = new PdfPTable(table);
                                        this.compositeElements.set(0, table);
                                        ArrayList rows2 = table.getRows();
                                        for (int i2 = headerRows; i2 < this.listIdx; i2++) {
                                            rows2.set(i2, null);
                                        }
                                    }
                                    float h = yTemp - this.minY;
                                    PdfPRow newRow = table.getRow(k2).splitRow(table, k2, h);
                                    if (newRow == null) {
                                        if (k2 == this.listIdx) {
                                            return 2;
                                        }
                                    } else {
                                        yTemp = this.minY;
                                        k2++;
                                        table.getRows().add(k2, newRow);
                                    }
                                } else if (table.isSplitRows() && k2 == this.listIdx && firstPass) {
                                    this.compositeElements.removeFirst();
                                    this.splittedRow = false;
                                } else if (k2 == this.listIdx && !firstPass && ((!table.isSplitRows() || table.isSplitLate()) && (table.getFooterRows() == 0 || table.isComplete()))) {
                                    return 2;
                                }
                            }
                            firstPass = false;
                            if (!simulate) {
                                switch (table.getHorizontalAlignment()) {
                                    case 0:
                                        break;
                                    case 2:
                                        x1 += this.rectangularWidth - tableWidth;
                                        break;
                                    default:
                                        x1 += (this.rectangularWidth - tableWidth) / 2.0f;
                                        break;
                                }
                                nt = PdfPTable.shallowCopy(table);
                                sub = nt.getRows();
                                if (!skipHeader) {
                                    for (j2 = 0; j2 < realHeaderRows; j2++) {
                                        PdfPRow headerRow = table.getRow(j2);
                                        sub.add(headerRow);
                                    }
                                } else {
                                    nt.setHeaderRows(footerRows);
                                }
                                sub.addAll(table.getRows(this.listIdx, k2));
                                if (table.isSkipLastFooter()) {
                                    z4 = false;
                                } else {
                                    z4 = true;
                                }
                                showFooter = z4;
                                if (k2 < table.size()) {
                                    nt.setComplete(true);
                                    showFooter = true;
                                }
                                for (j = 0; j < footerRows && nt.isComplete() && showFooter; j++) {
                                    sub.add(table.getRow(j + realHeaderRows));
                                }
                                rowHeight = 0.0f;
                                last = (PdfPRow) sub.get((sub.size() - 1) - footerRows);
                                if (table.isExtendLastRow()) {
                                    rowHeight = last.getMaxHeights();
                                    last.setMaxHeights((yTemp - this.minY) + rowHeight);
                                    yTemp = this.minY;
                                }
                                if (this.canvases != null) {
                                    nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvases);
                                } else {
                                    nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvas);
                                }
                                if (table.isExtendLastRow()) {
                                    last.setMaxHeights(rowHeight);
                                }
                            } else if (table.isExtendLastRow() && this.minY > -1.0737418E9f) {
                                yTemp = this.minY;
                            }
                            this.yLine = yTemp;
                            if (!skipHeader && !table.isComplete()) {
                                this.yLine += footerHeight;
                            }
                            if (k2 >= table.size()) {
                                this.yLine -= table.spacingAfter();
                                this.compositeElements.removeFirst();
                                this.splittedRow = false;
                                this.listIdx = 0;
                            } else {
                                if (this.splittedRow) {
                                    rows = table.getRows();
                                    for (i = this.listIdx; i < k2; i++) {
                                        rows.set(i, null);
                                    }
                                }
                                this.listIdx = k2;
                                return 2;
                            }
                        }
                    }
                    if (!table.isComplete()) {
                        yTemp += footerHeight;
                    }
                    if (k2 >= table.size()) {
                        if (!table.isSplitRows()) {
                            if (table.isSplitRows()) {
                            }
                            if (k2 == this.listIdx) {
                                return 2;
                            }
                        } else {
                            if (table.isSplitRows()) {
                            }
                            if (k2 == this.listIdx) {
                                return 2;
                            }
                        }
                    }
                    firstPass = false;
                    if (!simulate) {
                        switch (table.getHorizontalAlignment()) {
                            case 0:
                                break;
                            case 2:
                                x1 += this.rectangularWidth - tableWidth;
                                break;
                            default:
                                x1 += (this.rectangularWidth - tableWidth) / 2.0f;
                                break;
                        }
                        nt = PdfPTable.shallowCopy(table);
                        sub = nt.getRows();
                        if (!skipHeader) {
                            while (j2 < realHeaderRows) {
                                PdfPRow headerRow2 = table.getRow(j2);
                                sub.add(headerRow2);
                            }
                        } else {
                            nt.setHeaderRows(footerRows);
                        }
                        sub.addAll(table.getRows(this.listIdx, k2));
                        if (table.isSkipLastFooter()) {
                            z4 = true;
                        } else {
                            z4 = false;
                        }
                        showFooter = z4;
                        if (k2 < table.size()) {
                            nt.setComplete(true);
                            showFooter = true;
                        }
                        while (j < footerRows) {
                            sub.add(table.getRow(j + realHeaderRows));
                        }
                        rowHeight = 0.0f;
                        last = (PdfPRow) sub.get((sub.size() - 1) - footerRows);
                        if (table.isExtendLastRow()) {
                            rowHeight = last.getMaxHeights();
                            last.setMaxHeights((yTemp - this.minY) + rowHeight);
                            yTemp = this.minY;
                        }
                        if (this.canvases != null) {
                            nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvases);
                        } else {
                            nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvas);
                        }
                        if (table.isExtendLastRow()) {
                            last.setMaxHeights(rowHeight);
                        }
                    } else if (table.isExtendLastRow()) {
                        yTemp = this.minY;
                    }
                    this.yLine = yTemp;
                    if (!skipHeader) {
                        this.yLine += footerHeight;
                    }
                    if (k2 >= table.size()) {
                        this.yLine -= table.spacingAfter();
                        this.compositeElements.removeFirst();
                        this.splittedRow = false;
                        this.listIdx = 0;
                    } else {
                        if (this.splittedRow) {
                            rows = table.getRows();
                            while (i < k2) {
                                rows.set(i, null);
                            }
                        }
                        this.listIdx = k2;
                        return 2;
                    }
                }
            } else if (element.type() == 55) {
                if (!simulate) {
                    DrawInterface zh = (DrawInterface) element;
                    zh.draw(this.canvas, this.leftX, this.minY, this.rightX, this.maxY, this.yLine);
                }
                this.compositeElements.removeFirst();
            } else {
                this.compositeElements.removeFirst();
            }
        }
        return 1;
    }

    public PdfContentByte getCanvas() {
        return this.canvas;
    }

    public void setCanvas(PdfContentByte canvas) {
        this.canvas = canvas;
        this.canvases = null;
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvas(canvas);
        }
    }

    public void setCanvases(PdfContentByte[] canvases) {
        this.canvases = canvases;
        this.canvas = canvases[3];
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvases(canvases);
        }
    }

    public PdfContentByte[] getCanvases() {
        return this.canvases;
    }

    public boolean zeroHeightElement() {
        return this.composite && !this.compositeElements.isEmpty() && ((Element) this.compositeElements.getFirst()).type() == 55;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseAscender(boolean useAscender) {
        this.useAscender = useAscender;
    }

    public static boolean hasMoreText(int status) {
        return (status & 1) == 0;
    }

    public float getFilledWidth() {
        return this.filledWidth;
    }

    public void setFilledWidth(float filledWidth) {
        this.filledWidth = filledWidth;
    }

    public void updateFilledWidth(float w) {
        if (w > this.filledWidth) {
            this.filledWidth = w;
        }
    }

    public boolean isAdjustFirstLine() {
        return this.adjustFirstLine;
    }

    public void setAdjustFirstLine(boolean adjustFirstLine) {
        this.adjustFirstLine = adjustFirstLine;
    }
}
