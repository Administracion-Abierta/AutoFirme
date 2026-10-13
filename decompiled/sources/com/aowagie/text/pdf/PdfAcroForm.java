package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Rectangle;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfAcroForm.class */
public class PdfAcroForm extends PdfDictionary {
    private final PdfWriter writer;
    private final HashMap fieldTemplates = new LinkedHashMap();
    private final PdfArray documentFields = new PdfArray();
    private final PdfArray calculationOrder = new PdfArray();
    private int sigFlags = 0;

    public PdfAcroForm(PdfWriter writer) {
        this.writer = writer;
    }

    public void setNeedAppearances(boolean value) {
        put(PdfName.NEEDAPPEARANCES, new PdfBoolean(value));
    }

    public void addFieldTemplates(HashMap ft) {
        this.fieldTemplates.putAll(ft);
    }

    public void addDocumentField(PdfIndirectReference ref) {
        this.documentFields.add(ref);
    }

    public boolean isValid() {
        if (this.documentFields.size() == 0) {
            return false;
        }
        put(PdfName.FIELDS, this.documentFields);
        if (this.sigFlags != 0) {
            put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
        }
        if (this.calculationOrder.size() > 0) {
            put(PdfName.CO, this.calculationOrder);
        }
        if (this.fieldTemplates.isEmpty()) {
            return true;
        }
        PdfDictionary dic = new PdfDictionary();
        for (PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dic, (PdfDictionary) template.getResources());
        }
        put(PdfName.DR, dic);
        put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        PdfDictionary fonts = (PdfDictionary) dic.get(PdfName.FONT);
        if (fonts != null) {
            this.writer.eliminateFontSubset(fonts);
            return true;
        }
        return true;
    }

    public void addCalculationOrder(PdfFormField formField) {
        this.calculationOrder.add(formField.getIndirectReference());
    }

    public void setSigFlags(int f) {
        this.sigFlags |= f;
    }

    private void addFormField(PdfFormField formField) {
        this.writer.addAnnotation(formField);
    }

    private void setButtonParams(PdfFormField button, int characteristics, String name, String value) {
        button.setButton(characteristics);
        button.setFlags(4);
        button.setPage();
        button.setFieldName(name);
        if (value != null) {
            button.setValueAsString(value);
        }
    }

    private void drawButton(PdfFormField button, String caption, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAppearance pa = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        pa.drawButton(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly, caption, font, fontSize);
        button.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pa);
    }

    private void setTextFieldParams(PdfFormField field, String text, String name, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setValueAsString(text);
        field.setDefaultValueAsString(text);
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
    }

    private void drawSingleLineOfText(PdfFormField field, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        PdfAppearance tp2 = (PdfAppearance) tp.getDuplicate();
        tp2.setFontAndSize(font, fontSize);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tp.drawTextField(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(3.0f, 3.0f, (urx - llx) - 6.0f, (ury - lly) - 6.0f);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.resetRGBColorFill();
        tp.setTextMatrix(4.0f, ((ury - lly) / 2.0f) - (fontSize * 0.3f));
        tp.showText(text);
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }

    private void drawMultiLineOfText(PdfFormField field, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        PdfAppearance tp2 = (PdfAppearance) tp.getDuplicate();
        tp2.setFontAndSize(font, fontSize);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tp.drawTextField(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(3.0f, 3.0f, (urx - llx) - 6.0f, (ury - lly) - 6.0f);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.resetRGBColorFill();
        tp.setTextMatrix(4.0f, 5.0f);
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        float yPos = ury - lly;
        while (tokenizer.hasMoreTokens()) {
            yPos -= fontSize * 1.2f;
            tp.showTextAligned(0, tokenizer.nextToken(), 3.0f, yPos, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }

    private void setCheckBoxParams(PdfFormField field, String name, String value, boolean status, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_TOGGLE);
        field.setFieldName(name);
        if (status) {
            field.setValueAsName(value);
            field.setAppearanceState(value);
        } else {
            field.setValueAsName("Off");
            field.setAppearanceState("Off");
        }
        field.setFlags(4);
        field.setPage();
        field.setBorderStyle(new PdfBorderDictionary(1.0f, 0));
    }

    private void drawCheckBoxAppearences(PdfFormField field, String value, float llx, float lly, float urx, float ury) {
        try {
            BaseFont font = BaseFont.createFont("ZapfDingbats", "Cp1252", false);
            float size = ury - lly;
            PdfAppearance tpOn = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
            PdfAppearance tp2 = (PdfAppearance) tpOn.getDuplicate();
            tp2.setFontAndSize(font, size);
            tp2.resetRGBColorFill();
            field.setDefaultAppearanceString(tp2);
            tpOn.drawTextField(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly);
            tpOn.saveState();
            tpOn.resetRGBColorFill();
            tpOn.beginText();
            tpOn.setFontAndSize(font, size);
            tpOn.showTextAligned(1, TlbConst.TYPELIB_MINOR_VERSION_WORD, (urx - llx) / 2.0f, ((ury - lly) / 2.0f) - (size * 0.3f), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            tpOn.endText();
            tpOn.restoreState();
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value, tpOn);
            PdfAppearance tpOff = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
            tpOff.drawTextField(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly);
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void drawRadioAppearences(PdfFormField field, String value, float llx, float lly, float urx, float ury) {
        PdfAppearance tpOn = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOn.drawRadioField(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly, true);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value, tpOn);
        PdfAppearance tpOff = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOff.drawRadioField(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx - llx, ury - lly, false);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
    }

    private void setChoiceParams(PdfFormField field, String name, String defaultValue, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        if (defaultValue != null) {
            field.setValueAsString(defaultValue);
            field.setDefaultValueAsString(defaultValue);
        }
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
        field.setBorderStyle(new PdfBorderDictionary(2.0f, 0));
    }
}
