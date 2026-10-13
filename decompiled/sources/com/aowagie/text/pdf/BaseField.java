package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Rectangle;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/BaseField.class */
abstract class BaseField {
    private static final float BORDER_WIDTH_THIN = 1.0f;
    static final int VISIBLE = 0;
    public static final int HIDDEN = 1;
    static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    static final int HIDDEN_BUT_PRINTABLE = 3;
    static final int READ_ONLY = 1;
    static final int REQUIRED = 2;
    static final int MULTILINE = 4096;
    static final int DO_NOT_SCROLL = 8388608;
    static final int PASSWORD = 8192;
    static final int FILE_SELECTION = 1048576;
    static final int DO_NOT_SPELL_CHECK = 4194304;
    public static final int EDIT = 262144;
    public static final int COMB = 16777216;
    protected Color borderColor;
    protected Color backgroundColor;
    protected Color textColor;
    private BaseFont font;
    protected PdfWriter writer;
    protected String text;
    protected Rectangle box;
    protected int visibility;
    protected String fieldName;
    protected int options;
    protected int maxCharacterLength;
    private static final HashMap fieldKeys = new LinkedHashMap();
    protected float borderWidth = BORDER_WIDTH_THIN;
    protected int borderStyle = 0;
    protected float fontSize = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    protected int alignment = 0;
    protected int rotation = 0;

    static {
        fieldKeys.putAll(PdfCopyFieldsImp.fieldKeys);
        fieldKeys.put(PdfName.T, 1);
    }

    public BaseField(PdfWriter writer, Rectangle box, String fieldName) {
        this.writer = writer;
        setBox(box);
        this.fieldName = fieldName;
    }

    protected BaseFont getRealFont() throws IOException, DocumentException {
        if (this.font == null) {
            return BaseFont.createFont("Helvetica", "Cp1252", false);
        }
        return this.font;
    }

    protected PdfAppearance getBorderAppearance() {
        PdfAppearance app = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
        switch (this.rotation) {
            case 90:
                app.setMatrix(ColumnText.GLOBAL_SPACE_CHAR_RATIO, BORDER_WIDTH_THIN, -1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.box.getHeight(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                break;
            case 180:
                app.setMatrix(-1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -1.0f, this.box.getWidth(), this.box.getHeight());
                break;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                app.setMatrix(ColumnText.GLOBAL_SPACE_CHAR_RATIO, -1.0f, BORDER_WIDTH_THIN, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.box.getWidth());
                break;
        }
        app.saveState();
        if (this.backgroundColor != null) {
            app.setColorFill(this.backgroundColor);
            app.rectangle(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.box.getWidth(), this.box.getHeight());
            app.fill();
        }
        if (this.borderStyle == 4) {
            if (this.borderWidth != ColumnText.GLOBAL_SPACE_CHAR_RATIO && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.moveTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.borderWidth / 2.0f);
                app.lineTo(this.box.getWidth(), this.borderWidth / 2.0f);
                app.stroke();
            }
        } else if (this.borderStyle == 2) {
            if (this.borderWidth != ColumnText.GLOBAL_SPACE_CHAR_RATIO && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                app.stroke();
            }
            Color actual = this.backgroundColor;
            if (actual == null) {
                actual = Color.white;
            }
            app.setGrayFill(BORDER_WIDTH_THIN);
            drawTopFrame(app);
            app.setColorFill(actual.darker());
            drawBottomFrame(app);
        } else if (this.borderStyle == 3) {
            if (this.borderWidth != ColumnText.GLOBAL_SPACE_CHAR_RATIO && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                app.stroke();
            }
            app.setGrayFill(0.5f);
            drawTopFrame(app);
            app.setGrayFill(0.75f);
            drawBottomFrame(app);
        } else if (this.borderWidth != ColumnText.GLOBAL_SPACE_CHAR_RATIO && this.borderColor != null) {
            if (this.borderStyle == 1) {
                app.setLineDash(3.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            }
            app.setColorStroke(this.borderColor);
            app.setLineWidth(this.borderWidth);
            app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
            app.stroke();
            if ((this.options & 16777216) != 0 && this.maxCharacterLength > 1) {
                float step = this.box.getWidth() / this.maxCharacterLength;
                float yb = this.borderWidth / 2.0f;
                float yt = this.box.getHeight() - (this.borderWidth / 2.0f);
                for (int k = 1; k < this.maxCharacterLength; k++) {
                    float x = step * k;
                    app.moveTo(x, yb);
                    app.lineTo(x, yt);
                }
                app.stroke();
            }
        }
        app.restoreState();
        return app;
    }

    private static void trimRight(StringBuffer buf) {
        int len = buf.length();
        while (len != 0) {
            len--;
            if (buf.charAt(len) != ' ') {
                return;
            } else {
                buf.setLength(len);
            }
        }
    }

    private void drawTopFrame(PdfAppearance app) {
        app.moveTo(this.borderWidth, this.borderWidth);
        app.lineTo(this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - (2.0f * this.borderWidth), this.box.getHeight() - (2.0f * this.borderWidth));
        app.lineTo(2.0f * this.borderWidth, this.box.getHeight() - (2.0f * this.borderWidth));
        app.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(this.borderWidth, this.borderWidth);
        app.fill();
    }

    private void drawBottomFrame(PdfAppearance app) {
        app.moveTo(this.borderWidth, this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - (2.0f * this.borderWidth), this.box.getHeight() - (2.0f * this.borderWidth));
        app.lineTo(this.box.getWidth() - (2.0f * this.borderWidth), 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(this.borderWidth, this.borderWidth);
        app.fill();
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getBorderStyle() {
        return this.borderStyle;
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public BaseFont getFont() {
        return this.font;
    }

    public void setFont(BaseFont font) {
        this.font = font;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rectangle getBox() {
        return this.box;
    }

    public void setBox(Rectangle box) {
        if (box == null) {
            this.box = null;
        } else {
            this.box = new Rectangle(box);
            this.box.normalize();
        }
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation) {
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90.");
        }
        int rotation2 = rotation % 360;
        if (rotation2 < 0) {
            rotation2 += 360;
        }
        this.rotation = rotation2;
    }

    public void setRotationFromPage(Rectangle page) {
        setRotation(page.getRotation());
    }

    public int getVisibility() {
        return this.visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getOptions() {
        return this.options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public int getMaxCharacterLength() {
        return this.maxCharacterLength;
    }

    public void setMaxCharacterLength(int maxCharacterLength) {
        this.maxCharacterLength = maxCharacterLength;
    }

    public PdfWriter getWriter() {
        return this.writer;
    }

    public void setWriter(PdfWriter writer) {
        this.writer = writer;
    }
}
