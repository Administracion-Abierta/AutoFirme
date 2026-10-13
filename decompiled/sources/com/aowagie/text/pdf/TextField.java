package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Font;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TextField.class */
public class TextField extends BaseField {
    private String defaultText;
    private String[] choices;
    private String[] choiceExports;
    private int choiceSelection;
    private int topFirst;
    private float extraMarginLeft;
    private float extraMarginTop;
    private ArrayList substitutionFonts;
    private BaseFont extensionFont;

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setWriter(PdfWriter pdfWriter) {
        super.setWriter(pdfWriter);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ PdfWriter getWriter() {
        return super.getWriter();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setMaxCharacterLength(int i) {
        super.setMaxCharacterLength(i);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ int getMaxCharacterLength() {
        return super.getMaxCharacterLength();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setOptions(int i) {
        super.setOptions(i);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ int getOptions() {
        return super.getOptions();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setFieldName(String str) {
        super.setFieldName(str);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ String getFieldName() {
        return super.getFieldName();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setVisibility(int i) {
        super.setVisibility(i);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ int getVisibility() {
        return super.getVisibility();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setRotationFromPage(Rectangle rectangle) {
        super.setRotationFromPage(rectangle);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setRotation(int i) {
        super.setRotation(i);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ int getRotation() {
        return super.getRotation();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setBox(Rectangle rectangle) {
        super.setBox(rectangle);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ Rectangle getBox() {
        return super.getBox();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setText(String str) {
        super.setText(str);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ String getText() {
        return super.getText();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setAlignment(int i) {
        super.setAlignment(i);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ int getAlignment() {
        return super.getAlignment();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setFontSize(float f) {
        super.setFontSize(f);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ float getFontSize() {
        return super.getFontSize();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setFont(BaseFont baseFont) {
        super.setFont(baseFont);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ BaseFont getFont() {
        return super.getFont();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setTextColor(Color color) {
        super.setTextColor(color);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ Color getTextColor() {
        return super.getTextColor();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setBackgroundColor(Color color) {
        super.setBackgroundColor(color);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ Color getBackgroundColor() {
        return super.getBackgroundColor();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setBorderColor(Color color) {
        super.setBorderColor(color);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ Color getBorderColor() {
        return super.getBorderColor();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setBorderStyle(int i) {
        super.setBorderStyle(i);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ int getBorderStyle() {
        return super.getBorderStyle();
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ void setBorderWidth(float f) {
        super.setBorderWidth(f);
    }

    @Override // com.aowagie.text.pdf.BaseField
    public /* bridge */ /* synthetic */ float getBorderWidth() {
        return super.getBorderWidth();
    }

    public TextField(PdfWriter writer, Rectangle box, String fieldName) {
        super(writer, box, fieldName);
    }

    private static boolean checkRTL(String text) {
        if (text == null || text.length() == 0) {
            return false;
        }
        char[] cc = text.toCharArray();
        for (char c : cc) {
            if (c >= 1424 && c < 1920) {
                return true;
            }
        }
        return false;
    }

    private static void changeFontSize(Phrase p, float size) {
        for (int k = 0; k < p.size(); k++) {
            ((Chunk) p.get(k)).getFont().setSize(size);
        }
    }

    private Phrase composePhrase(String text, BaseFont ufont, Color color, float fontSize) {
        Phrase phrase;
        if (this.extensionFont == null && (this.substitutionFonts == null || this.substitutionFonts.isEmpty())) {
            phrase = new Phrase(new Chunk(text, new Font(ufont, fontSize, 0, color)));
        } else {
            FontSelector fs = new FontSelector();
            fs.addFont(new Font(ufont, fontSize, 0, color));
            if (this.extensionFont != null) {
                fs.addFont(new Font(this.extensionFont, fontSize, 0, color));
            }
            if (this.substitutionFonts != null) {
                for (int k = 0; k < this.substitutionFonts.size(); k++) {
                    fs.addFont(new Font((BaseFont) this.substitutionFonts.get(k), fontSize, 0, color));
                }
            }
            phrase = fs.process(text);
        }
        return phrase;
    }

    public static String removeCRLF(String text) {
        if (text.indexOf(10) >= 0 || text.indexOf(13) >= 0) {
            char[] p = text.toCharArray();
            StringBuffer sb = new StringBuffer(p.length);
            int k = 0;
            while (k < p.length) {
                char c = p[k];
                if (c == '\n') {
                    sb.append(' ');
                } else if (c == '\r') {
                    sb.append(' ');
                    if (k < p.length - 1 && p[k + 1] == '\n') {
                        k++;
                    }
                } else {
                    sb.append(c);
                }
                k++;
            }
            return sb.toString();
        }
        return text;
    }

    public static String obfuscatePassword(String text) {
        char[] pchar = new char[text.length()];
        for (int i = 0; i < text.length(); i++) {
            pchar[i] = '*';
        }
        return new String(pchar);
    }

    public PdfAppearance getAppearance() throws IOException, DocumentException {
        String ptext;
        float x;
        PdfAppearance app = getBorderAppearance();
        app.beginVariableText();
        if (this.text == null || this.text.length() == 0) {
            app.endVariableText();
            return app;
        }
        boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = (this.box.getHeight() - (this.borderWidth * 2.0f)) - this.extraMarginTop;
        float bw2 = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            bw2 *= 2.0f;
        }
        float offsetX = Math.max(bw2, 1.0f);
        float offX = Math.min(bw2, offsetX);
        app.saveState();
        app.rectangle(offX, offX, this.box.getWidth() - (2.0f * offX), this.box.getHeight() - (2.0f * offX));
        app.clip();
        app.newPath();
        if ((this.options & 8192) != 0) {
            ptext = obfuscatePassword(this.text);
        } else if ((this.options & 4096) == 0) {
            ptext = removeCRLF(this.text);
        } else {
            ptext = this.text;
        }
        BaseFont ufont = getRealFont();
        Color fcolor = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
        int rtl = checkRTL(ptext) ? 2 : 1;
        float usize = this.fontSize;
        Phrase phrase = composePhrase(ptext, ufont, fcolor, usize);
        if ((this.options & 4096) != 0) {
            float width = (this.box.getWidth() - (4.0f * offsetX)) - this.extraMarginLeft;
            float factor = ufont.getFontDescriptor(8, 1.0f) - ufont.getFontDescriptor(6, 1.0f);
            ColumnText ct = new ColumnText(null);
            if (usize == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                usize = h / factor;
                if (usize > 4.0f) {
                    if (usize > 12.0f) {
                        usize = 12.0f;
                    }
                    float step = Math.max((usize - 4.0f) / 10.0f, 0.2f);
                    ct.setSimpleColumn(ColumnText.GLOBAL_SPACE_CHAR_RATIO, -h, width, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    ct.setAlignment(this.alignment);
                    ct.setRunDirection(rtl);
                    while (usize > 4.0f) {
                        ct.setYLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                        changeFontSize(phrase, usize);
                        ct.setText(phrase);
                        ct.setLeading(factor * usize);
                        int status = ct.go(true);
                        if ((status & 2) == 0) {
                            break;
                        }
                        usize -= step;
                    }
                }
                if (usize < 4.0f) {
                    usize = 4.0f;
                }
            }
            changeFontSize(phrase, usize);
            ct.setCanvas(app);
            float leading = usize * factor;
            ct.setSimpleColumn(this.extraMarginLeft + (2.0f * offsetX), -20000.0f, this.box.getWidth() - (2.0f * offsetX), ((offsetX + h) - ufont.getFontDescriptor(8, usize)) + leading);
            ct.setLeading(leading);
            ct.setAlignment(this.alignment);
            ct.setRunDirection(rtl);
            ct.setText(phrase);
            ct.go();
        } else {
            if (usize == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                float maxCalculatedSize = h / (ufont.getFontDescriptor(7, 1.0f) - ufont.getFontDescriptor(6, 1.0f));
                changeFontSize(phrase, 1.0f);
                float wd = ColumnText.getWidth(phrase, rtl, 0);
                if (wd == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    usize = maxCalculatedSize;
                } else {
                    usize = Math.min(maxCalculatedSize, ((this.box.getWidth() - this.extraMarginLeft) - (4.0f * offsetX)) / wd);
                }
                if (usize < 4.0f) {
                    usize = 4.0f;
                }
            }
            changeFontSize(phrase, usize);
            float offsetY = offX + (((this.box.getHeight() - (2.0f * offX)) - ufont.getFontDescriptor(1, usize)) / 2.0f);
            if (offsetY < offX) {
                offsetY = offX;
            }
            if (offsetY - offX < (-ufont.getFontDescriptor(3, usize))) {
                float ny = (-ufont.getFontDescriptor(3, usize)) + offX;
                float dy = (this.box.getHeight() - offX) - ufont.getFontDescriptor(1, usize);
                offsetY = Math.min(ny, Math.max(offsetY, dy));
            }
            if ((this.options & 16777216) != 0 && this.maxCharacterLength > 0) {
                int textLen = Math.min(this.maxCharacterLength, ptext.length());
                int position = 0;
                if (this.alignment == 2) {
                    position = this.maxCharacterLength - textLen;
                } else if (this.alignment == 1) {
                    position = (this.maxCharacterLength - textLen) / 2;
                }
                float step2 = (this.box.getWidth() - this.extraMarginLeft) / this.maxCharacterLength;
                float start = (step2 / 2.0f) + (position * step2);
                if (this.textColor == null) {
                    app.setGrayFill(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                } else {
                    app.setColorFill(this.textColor);
                }
                app.beginText();
                for (int k = 0; k < phrase.size(); k++) {
                    Chunk ck = (Chunk) phrase.get(k);
                    BaseFont bf = ck.getFont().getBaseFont();
                    app.setFontAndSize(bf, usize);
                    StringBuffer sb = ck.append("");
                    for (int j = 0; j < sb.length(); j++) {
                        String c = sb.substring(j, j + 1);
                        app.setTextMatrix((this.extraMarginLeft + start) - (bf.getWidthPoint(c, usize) / 2.0f), offsetY - this.extraMarginTop);
                        app.showText(c);
                        start += step2;
                    }
                }
                app.endText();
            } else {
                switch (this.alignment) {
                    case 1:
                        x = this.extraMarginLeft + (this.box.getWidth() / 2.0f);
                        break;
                    case 2:
                        x = (this.extraMarginLeft + this.box.getWidth()) - (2.0f * offsetX);
                        break;
                    default:
                        x = this.extraMarginLeft + (2.0f * offsetX);
                        break;
                }
                ColumnText.showTextAligned(app, this.alignment, phrase, x, offsetY - this.extraMarginTop, ColumnText.GLOBAL_SPACE_CHAR_RATIO, rtl, 0);
            }
        }
        app.restoreState();
        app.endVariableText();
        return app;
    }

    PdfAppearance getListAppearance() throws IOException, DocumentException {
        PdfAppearance app = getBorderAppearance();
        app.beginVariableText();
        if (this.choices == null || this.choices.length == 0) {
            app.endVariableText();
            return app;
        }
        int topChoice = this.choiceSelection;
        if (topChoice >= this.choices.length) {
            topChoice = this.choices.length - 1;
        }
        if (topChoice < 0) {
            topChoice = 0;
        }
        BaseFont ufont = getRealFont();
        float usize = this.fontSize;
        if (usize == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            usize = 12.0f;
        }
        boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = this.box.getHeight() - (this.borderWidth * 2.0f);
        float offsetX = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            offsetX *= 2.0f;
        }
        float leading = ufont.getFontDescriptor(8, usize) - ufont.getFontDescriptor(6, usize);
        int maxFit = ((int) (h / leading)) + 1;
        int last = topChoice + (maxFit / 2) + 1;
        int first = last - maxFit;
        if (first < 0) {
            int i = last + first;
            first = 0;
        }
        int last2 = first + maxFit;
        if (last2 > this.choices.length) {
            last2 = this.choices.length;
        }
        this.topFirst = first;
        app.saveState();
        app.rectangle(offsetX, offsetX, this.box.getWidth() - (2.0f * offsetX), this.box.getHeight() - (2.0f * offsetX));
        app.clip();
        app.newPath();
        Color fcolor = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
        app.setColorFill(new Color(10, 36, 106));
        app.rectangle(offsetX, (offsetX + h) - (((topChoice - first) + 1) * leading), this.box.getWidth() - (2.0f * offsetX), leading);
        app.fill();
        float xp = offsetX * 2.0f;
        float yp = (offsetX + h) - ufont.getFontDescriptor(8, usize);
        int idx = first;
        while (idx < last2) {
            String ptext = this.choices[idx];
            int rtl = checkRTL(ptext) ? 2 : 1;
            Phrase phrase = composePhrase(removeCRLF(ptext), ufont, idx == topChoice ? GrayColor.GRAYWHITE : fcolor, usize);
            ColumnText.showTextAligned(app, 0, phrase, xp, yp, ColumnText.GLOBAL_SPACE_CHAR_RATIO, rtl, 0);
            idx++;
            yp -= leading;
        }
        app.restoreState();
        app.endVariableText();
        return app;
    }

    public PdfFormField getTextField() throws IOException, DocumentException {
        if (this.maxCharacterLength <= 0) {
            this.options &= -16777217;
        }
        if ((this.options & 16777216) != 0) {
            this.options &= -4097;
        }
        PdfFormField field = PdfFormField.createTextField(this.writer, false, false, this.maxCharacterLength);
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        switch (this.alignment) {
            case 1:
                field.setQuadding(1);
                break;
            case 2:
                field.setQuadding(2);
                break;
        }
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if (!"".equals(this.text)) {
                field.setValueAsString(this.text);
            }
            if (this.defaultText != null) {
                field.setDefaultValueAsString(this.defaultText);
            }
            if ((this.options & 1) != 0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                field.setFieldFlags(2);
            }
            if ((this.options & 4096) != 0) {
                field.setFieldFlags(4096);
            }
            if ((this.options & 8388608) != 0) {
                field.setFieldFlags(8388608);
            }
            if ((this.options & 8192) != 0) {
                field.setFieldFlags(8192);
            }
            if ((this.options & 1048576) != 0) {
                field.setFieldFlags(1048576);
            }
            if ((this.options & 4194304) != 0) {
                field.setFieldFlags(4194304);
            }
            if ((this.options & 16777216) != 0) {
                field.setFieldFlags(16777216);
            }
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance tp = getAppearance();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        PdfAppearance da = (PdfAppearance) tp.getDuplicate();
        da.setFontAndSize(getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        } else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1:
                field.setFlags(6);
                break;
            case 2:
                break;
            case 3:
                field.setFlags(36);
                break;
            default:
                field.setFlags(4);
                break;
        }
        return field;
    }

    public PdfFormField getComboField() throws IOException, DocumentException {
        return getChoiceField(false);
    }

    public PdfFormField getListField() throws IOException, DocumentException {
        return getChoiceField(true);
    }

    protected PdfFormField getChoiceField(boolean isList) throws IOException, DocumentException {
        PdfFormField field;
        PdfAppearance tp;
        this.options &= -16781313;
        String[] uchoices = this.choices;
        if (uchoices == null) {
            uchoices = new String[0];
        }
        int topChoice = this.choiceSelection;
        if (topChoice >= uchoices.length) {
            topChoice = uchoices.length - 1;
        }
        if (this.text == null) {
            this.text = "";
        }
        if (topChoice >= 0) {
            this.text = uchoices[topChoice];
        }
        if (topChoice < 0) {
            topChoice = 0;
        }
        String[][] mix = (String[][]) null;
        if (this.choiceExports == null) {
            if (isList) {
                field = PdfFormField.createList(this.writer, uchoices, topChoice);
            } else {
                field = PdfFormField.createCombo(this.writer, (this.options & 262144) != 0, uchoices, topChoice);
            }
        } else {
            mix = new String[uchoices.length][2];
            for (int k = 0; k < mix.length; k++) {
                String[] strArr = mix[k];
                String[] strArr2 = mix[k];
                String str = uchoices[k];
                strArr2[1] = str;
                strArr[0] = str;
            }
            int top = Math.min(uchoices.length, this.choiceExports.length);
            for (int k2 = 0; k2 < top; k2++) {
                if (this.choiceExports[k2] != null) {
                    mix[k2][0] = this.choiceExports[k2];
                }
            }
            if (isList) {
                field = PdfFormField.createList(this.writer, mix, topChoice);
            } else {
                field = PdfFormField.createCombo(this.writer, (this.options & 262144) != 0, mix, topChoice);
            }
        }
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if (uchoices.length > 0) {
                if (mix != null) {
                    field.setValueAsString(mix[topChoice][0]);
                    field.setDefaultValueAsString(mix[topChoice][0]);
                } else {
                    field.setValueAsString(this.text);
                    field.setDefaultValueAsString(this.text);
                }
            }
            if ((this.options & 1) != 0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                field.setFieldFlags(2);
            }
            if ((this.options & 4194304) != 0) {
                field.setFieldFlags(4194304);
            }
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        if (isList) {
            tp = getListAppearance();
            if (this.topFirst > 0) {
                field.put(PdfName.TI, new PdfNumber(this.topFirst));
            }
        } else {
            tp = getAppearance();
        }
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        PdfAppearance da = (PdfAppearance) tp.getDuplicate();
        da.setFontAndSize(getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        } else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1:
                field.setFlags(6);
                break;
            case 2:
                break;
            case 3:
                field.setFlags(36);
                break;
            default:
                field.setFlags(4);
                break;
        }
        return field;
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String[] getChoices() {
        return this.choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public String[] getChoiceExports() {
        return this.choiceExports;
    }

    public void setChoiceExports(String[] choiceExports) {
        this.choiceExports = choiceExports;
    }

    public int getChoiceSelection() {
        return this.choiceSelection;
    }

    public void setChoiceSelection(int choiceSelection) {
        this.choiceSelection = choiceSelection;
    }

    int getTopFirst() {
        return this.topFirst;
    }

    public void setExtraMargin(float extraMarginLeft, float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }

    public ArrayList getSubstitutionFonts() {
        return this.substitutionFonts;
    }

    public void setSubstitutionFonts(ArrayList substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }

    public BaseFont getExtensionFont() {
        return this.extensionFont;
    }

    public void setExtensionFont(BaseFont extensionFont) {
        this.extensionFont = extensionFont;
    }
}
