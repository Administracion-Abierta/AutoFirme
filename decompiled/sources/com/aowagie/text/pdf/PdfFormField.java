package com.aowagie.text.pdf;

import com.aowagie.text.Rectangle;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfFormField.class */
public class PdfFormField extends PdfAnnotation {
    public static final int FF_READ_ONLY = 1;
    public static final int FF_REQUIRED = 2;
    public static final int FF_NO_EXPORT = 4;
    public static final int FF_NO_TOGGLE_TO_OFF = 16384;
    public static final int FF_RADIO = 32768;
    public static final int FF_PUSHBUTTON = 65536;
    public static final int FF_MULTILINE = 4096;
    public static final int FF_PASSWORD = 8192;
    public static final int FF_COMBO = 131072;
    public static final int FF_EDIT = 262144;
    public static final int FF_FILESELECT = 1048576;
    public static final int FF_MULTISELECT = 2097152;
    public static final int FF_DONOTSPELLCHECK = 4194304;
    public static final int FF_DONOTSCROLL = 8388608;
    public static final int FF_COMB = 16777216;
    public static final int FF_RADIOSINUNISON = 33554432;
    public static final int Q_LEFT = 0;
    public static final int Q_CENTER = 1;
    public static final int Q_RIGHT = 2;
    public static final int MK_NO_ICON = 0;
    public static final int MK_NO_CAPTION = 1;
    public static final int MK_CAPTION_BELOW = 2;
    public static final int MK_CAPTION_ABOVE = 3;
    public static final int MK_CAPTION_RIGHT = 4;
    public static final int MK_CAPTION_LEFT = 5;
    public static final int MK_CAPTION_OVERLAID = 6;
    public static final boolean MULTILINE = true;
    public static final boolean SINGLELINE = false;
    public static final boolean PLAINTEXT = false;
    public static final boolean PASSWORD = true;
    protected PdfFormField parent;
    protected ArrayList kids;
    public static final PdfName IF_SCALE_ALWAYS = PdfName.A;
    public static final PdfName IF_SCALE_BIGGER = PdfName.B;
    public static final PdfName IF_SCALE_SMALLER = PdfName.S;
    public static final PdfName IF_SCALE_NEVER = PdfName.N;
    public static final PdfName IF_SCALE_ANAMORPHIC = PdfName.A;
    public static final PdfName IF_SCALE_PROPORTIONAL = PdfName.P;
    static PdfName[] mergeTarget = {PdfName.FONT, PdfName.XOBJECT, PdfName.COLORSPACE, PdfName.PATTERN};

    public PdfFormField(PdfWriter writer, float llx, float lly, float urx, float ury, PdfAction action) {
        super(writer, llx, lly, urx, ury, action);
        put(PdfName.TYPE, PdfName.ANNOT);
        put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.annotation = true;
    }

    protected PdfFormField(PdfWriter writer) {
        super(writer, null);
        this.form = true;
        this.annotation = false;
    }

    public void setWidget(Rectangle rect, PdfName highlight) {
        put(PdfName.TYPE, PdfName.ANNOT);
        put(PdfName.SUBTYPE, PdfName.WIDGET);
        put(PdfName.RECT, new PdfRectangle(rect));
        this.annotation = true;
        if (highlight != null && !highlight.equals(HIGHLIGHT_INVERT)) {
            put(PdfName.H, highlight);
        }
    }

    public static PdfFormField createEmpty(PdfWriter writer) {
        PdfFormField field = new PdfFormField(writer);
        return field;
    }

    public void setButton(int flags) {
        put(PdfName.FT, PdfName.BTN);
        if (flags != 0) {
            put(PdfName.FF, new PdfNumber(flags));
        }
    }

    protected static PdfFormField createButton(PdfWriter writer, int flags) {
        PdfFormField field = new PdfFormField(writer);
        field.setButton(flags);
        return field;
    }

    public static PdfFormField createPushButton(PdfWriter writer) {
        return createButton(writer, 65536);
    }

    public static PdfFormField createCheckBox(PdfWriter writer) {
        return createButton(writer, 0);
    }

    public static PdfFormField createRadioButton(PdfWriter writer, boolean noToggleToOff) {
        return createButton(writer, 32768 + (noToggleToOff ? 16384 : 0));
    }

    public static PdfFormField createTextField(PdfWriter writer, boolean multiline, boolean password, int maxLen) {
        PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.TX);
        int flags = multiline ? 4096 : 0;
        field.put(PdfName.FF, new PdfNumber(flags + (password ? 8192 : 0)));
        if (maxLen > 0) {
            field.put(PdfName.MAXLEN, new PdfNumber(maxLen));
        }
        return field;
    }

    protected static PdfFormField createChoice(PdfWriter writer, int flags, PdfArray options, int topIndex) {
        PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.CH);
        field.put(PdfName.FF, new PdfNumber(flags));
        field.put(PdfName.OPT, options);
        if (topIndex > 0) {
            field.put(PdfName.TI, new PdfNumber(topIndex));
        }
        return field;
    }

    public static PdfFormField createList(PdfWriter writer, String[] options, int topIndex) {
        return createChoice(writer, 0, processOptions(options), topIndex);
    }

    public static PdfFormField createList(PdfWriter writer, String[][] options, int topIndex) {
        return createChoice(writer, 0, processOptions(options), topIndex);
    }

    public static PdfFormField createCombo(PdfWriter writer, boolean edit, String[] options, int topIndex) {
        return createChoice(writer, 131072 + (edit ? 262144 : 0), processOptions(options), topIndex);
    }

    public static PdfFormField createCombo(PdfWriter writer, boolean edit, String[][] options, int topIndex) {
        return createChoice(writer, 131072 + (edit ? 262144 : 0), processOptions(options), topIndex);
    }

    protected static PdfArray processOptions(String[] options) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option, PdfObject.TEXT_UNICODE));
        }
        return array;
    }

    protected static PdfArray processOptions(String[][] options) {
        PdfArray array = new PdfArray();
        for (String[] subOption : options) {
            PdfArray ar2 = new PdfArray(new PdfString(subOption[0], PdfObject.TEXT_UNICODE));
            ar2.add(new PdfString(subOption[1], PdfObject.TEXT_UNICODE));
            array.add(ar2);
        }
        return array;
    }

    public static PdfFormField createSignature(PdfWriter writer) {
        PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.SIG);
        return field;
    }

    public PdfFormField getParent() {
        return this.parent;
    }

    public void addKid(PdfFormField field) {
        field.parent = this;
        if (this.kids == null) {
            this.kids = new ArrayList();
        }
        this.kids.add(field);
    }

    public ArrayList getKids() {
        return this.kids;
    }

    public int setFieldFlags(int flags) {
        int old;
        PdfNumber obj = (PdfNumber) get(PdfName.FF);
        if (obj == null) {
            old = 0;
        } else {
            old = obj.intValue();
        }
        int v = old | flags;
        put(PdfName.FF, new PdfNumber(v));
        return old;
    }

    public void setValueAsString(String s) {
        put(PdfName.V, new PdfString(s, PdfObject.TEXT_UNICODE));
    }

    public void setValueAsName(String s) {
        put(PdfName.V, new PdfName(s));
    }

    public void setValue(PdfSignature sig) {
        put(PdfName.V, sig);
    }

    public void setDefaultValueAsString(String s) {
        put(PdfName.DV, new PdfString(s, PdfObject.TEXT_UNICODE));
    }

    public void setDefaultValueAsName(String s) {
        put(PdfName.DV, new PdfName(s));
    }

    public void setFieldName(String s) {
        if (s != null) {
            put(PdfName.T, new PdfString(s, PdfObject.TEXT_UNICODE));
        }
    }

    public void setUserName(String s) {
        put(PdfName.TU, new PdfString(s, PdfObject.TEXT_UNICODE));
    }

    public void setMappingName(String s) {
        put(PdfName.TM, new PdfString(s, PdfObject.TEXT_UNICODE));
    }

    public void setQuadding(int v) {
        put(PdfName.Q, new PdfNumber(v));
    }

    static void mergeResources(PdfDictionary result, PdfDictionary source, PdfStamperImp writer) {
        for (PdfName element : mergeTarget) {
            PdfDictionary pdfDict = source.getAsDict(element);
            if (pdfDict != null) {
                PdfDictionary pdfDictionary = (PdfDictionary) PdfReader.getPdfObject(result.get(element), result);
                PdfDictionary res = pdfDictionary;
                if (pdfDictionary == null) {
                    res = new PdfDictionary();
                }
                res.mergeDifferent(pdfDict);
                result.put(element, res);
                if (writer != null) {
                    writer.markUsed(res);
                }
            }
        }
    }

    static void mergeResources(PdfDictionary result, PdfDictionary source) {
        mergeResources(result, source, null);
    }

    @Override // com.aowagie.text.pdf.PdfAnnotation
    public void setUsed() {
        this.used = true;
        if (this.parent != null) {
            put(PdfName.PARENT, this.parent.getIndirectReference());
        }
        if (this.kids != null) {
            PdfArray array = new PdfArray();
            for (int k = 0; k < this.kids.size(); k++) {
                array.add(((PdfFormField) this.kids.get(k)).getIndirectReference());
            }
            put(PdfName.KIDS, array);
        }
        if (this.templates == null) {
            return;
        }
        PdfDictionary dic = new PdfDictionary();
        for (PdfTemplate template : this.templates.keySet()) {
            mergeResources(dic, (PdfDictionary) template.getResources());
        }
        put(PdfName.DR, dic);
    }

    public static PdfAnnotation shallowDuplicate(PdfAnnotation annot) {
        PdfAnnotation dup;
        if (annot.isForm()) {
            dup = new PdfFormField(annot.writer);
            PdfFormField dupField = (PdfFormField) dup;
            PdfFormField srcField = (PdfFormField) annot;
            dupField.parent = srcField.parent;
            dupField.kids = srcField.kids;
        } else {
            dup = new PdfAnnotation(annot.writer, null);
        }
        dup.merge(annot);
        dup.form = annot.form;
        dup.annotation = annot.annotation;
        dup.templates = annot.templates;
        return dup;
    }
}
