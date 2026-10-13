package com.aowagie.text.pdf;

import com.aowagie.text.Rectangle;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfAnnotation.class */
public class PdfAnnotation extends PdfDictionary {
    static final int FLAGS_HIDDEN = 2;
    public static final int FLAGS_PRINT = 4;
    static final int FLAGS_NOVIEW = 32;
    static final int FLAGS_LOCKED = 128;
    private static final int MARKUP_UNDERLINE = 1;
    private static final int MARKUP_STRIKEOUT = 2;
    private static final int MARKUP_SQUIGGLY = 3;
    protected PdfWriter writer;
    private PdfIndirectReference reference;
    protected HashMap templates;
    protected boolean form = false;
    protected boolean annotation = true;
    protected boolean used = false;
    private int placeInPage = -1;
    static final PdfName HIGHLIGHT_INVERT = PdfName.I;
    static final PdfName HIGHLIGHT_TOGGLE = PdfName.T;
    static final PdfName APPEARANCE_NORMAL = PdfName.N;

    PdfAnnotation(PdfWriter writer, Rectangle rect) {
        this.writer = writer;
        if (rect != null) {
            put(PdfName.RECT, new PdfRectangle(rect));
        }
    }

    public PdfAnnotation(PdfWriter writer, float llx, float lly, float urx, float ury, PdfString title, PdfString content) {
        this.writer = writer;
        put(PdfName.SUBTYPE, PdfName.TEXT);
        put(PdfName.T, title);
        put(PdfName.RECT, new PdfRectangle(llx, lly, urx, ury));
        put(PdfName.CONTENTS, content);
    }

    public PdfAnnotation(PdfWriter writer, float llx, float lly, float urx, float ury, PdfAction action) {
        this.writer = writer;
        put(PdfName.SUBTYPE, PdfName.LINK);
        put(PdfName.RECT, new PdfRectangle(llx, lly, urx, ury));
        put(PdfName.A, action);
        put(PdfName.BORDER, new PdfBorderArray(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        put(PdfName.C, new PdfColor(0, 0, 255));
    }

    public static PdfAnnotation createScreen(PdfWriter writer, Rectangle rect, String clipTitle, PdfFileSpecification fs, String mimeType, boolean playOnDisplay) throws IOException {
        PdfAnnotation ann = new PdfAnnotation(writer, rect);
        ann.put(PdfName.SUBTYPE, PdfName.SCREEN);
        ann.put(PdfName.F, new PdfNumber(4));
        ann.put(PdfName.TYPE, PdfName.ANNOT);
        ann.setPage();
        PdfIndirectReference ref = ann.getIndirectReference();
        PdfAction action = PdfAction.rendition(clipTitle, fs, mimeType, ref);
        PdfIndirectReference actionRef = writer.addToBody(action).getIndirectReference();
        if (playOnDisplay) {
            PdfDictionary aa = new PdfDictionary();
            aa.put(new PdfName("PV"), actionRef);
            ann.put(PdfName.AA, aa);
        }
        ann.put(PdfName.A, actionRef);
        return ann;
    }

    public PdfIndirectReference getIndirectReference() {
        if (this.reference == null) {
            this.reference = this.writer.getPdfIndirectReference();
        }
        return this.reference;
    }

    private static PdfAnnotation createLink(PdfWriter writer, Rectangle rect, PdfName highlight) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.LINK);
        if (!highlight.equals(HIGHLIGHT_INVERT)) {
            annot.put(PdfName.H, highlight);
        }
        return annot;
    }

    private static PdfAnnotation createFileAttachment(PdfWriter writer, Rectangle rect, String contents, PdfFileSpecification fs) throws IOException {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.FILEATTACHMENT);
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, PdfObject.TEXT_UNICODE));
        }
        annot.put(PdfName.FS, fs.getReference());
        return annot;
    }

    public void setDefaultAppearanceString(PdfContentByte cb) {
        byte[] b = cb.getInternalBuffer().toByteArray();
        int len = b.length;
        for (int k = 0; k < len; k++) {
            if (b[k] == 10) {
                b[k] = 32;
            }
        }
        put(PdfName.DA, new PdfString(b));
    }

    public void setFlags(int flags) {
        if (flags == 0) {
            remove(PdfName.F);
        } else {
            put(PdfName.F, new PdfNumber(flags));
        }
    }

    public void setBorder(PdfBorderArray border) {
        put(PdfName.BORDER, border);
    }

    public void setBorderStyle(PdfBorderDictionary border) {
        put(PdfName.BS, border);
    }

    public void setHighlighting(PdfName highlight) {
        if (highlight.equals(HIGHLIGHT_INVERT)) {
            remove(PdfName.H);
        } else {
            put(PdfName.H, highlight);
        }
    }

    void setAppearance(PdfName ap, PdfTemplate template) {
        PdfDictionary dic = (PdfDictionary) get(PdfName.AP);
        if (dic == null) {
            dic = new PdfDictionary();
        }
        dic.put(ap, template.getIndirectReference());
        put(PdfName.AP, dic);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new LinkedHashMap();
        }
        this.templates.put(template, null);
    }

    void setAppearance(PdfName ap, String state, PdfTemplate template) {
        PdfDictionary dic;
        PdfDictionary dicAp = (PdfDictionary) get(PdfName.AP);
        if (dicAp == null) {
            dicAp = new PdfDictionary();
        }
        PdfObject obj = dicAp.get(ap);
        if (obj != null && obj.isDictionary()) {
            dic = (PdfDictionary) obj;
        } else {
            dic = new PdfDictionary();
        }
        dic.put(new PdfName(state), template.getIndirectReference());
        dicAp.put(ap, dic);
        put(PdfName.AP, dicAp);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new LinkedHashMap();
        }
        this.templates.put(template, null);
    }

    public void setAppearanceState(String state) {
        if (state == null) {
            remove(PdfName.AS);
        } else {
            put(PdfName.AS, new PdfName(state));
        }
    }

    public void setColor(Color color) {
        put(PdfName.C, new PdfColor(color));
    }

    public void setTitle(String title) {
        if (title == null) {
            remove(PdfName.T);
        } else {
            put(PdfName.T, new PdfString(title, PdfObject.TEXT_UNICODE));
        }
    }

    public void setPopup(PdfAnnotation popup) {
        put(PdfName.POPUP, popup.getIndirectReference());
        popup.put(PdfName.PARENT, getIndirectReference());
    }

    public void setAction(PdfAction action) {
        put(PdfName.A, action);
    }

    public boolean isUsed() {
        return this.used;
    }

    public void setUsed() {
        this.used = true;
    }

    public HashMap getTemplates() {
        return this.templates;
    }

    public boolean isForm() {
        return this.form;
    }

    public boolean isAnnotation() {
        return this.annotation;
    }

    public void setPage(int page) {
        put(PdfName.P, this.writer.getPageReference(page));
    }

    void setPage() {
        put(PdfName.P, this.writer.getCurrentPage());
    }

    public int getPlaceInPage() {
        return this.placeInPage;
    }

    public void setPlaceInPage(int placeInPage) {
        this.placeInPage = placeInPage;
    }

    public void setRotate(int v) {
        put(PdfName.ROTATE, new PdfNumber(v));
    }

    private PdfDictionary getMK() {
        PdfDictionary mk = (PdfDictionary) get(PdfName.MK);
        if (mk == null) {
            mk = new PdfDictionary();
            put(PdfName.MK, mk);
        }
        return mk;
    }

    public void setMKRotation(int rotation) {
        getMK().put(PdfName.R, new PdfNumber(rotation));
    }

    static PdfArray getMKColor(Color color) {
        PdfArray array = new PdfArray();
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1:
                array.add(new PdfNumber(((GrayColor) color).getGray()));
                break;
            case 2:
                CMYKColor cmyk = (CMYKColor) color;
                array.add(new PdfNumber(cmyk.getCyan()));
                array.add(new PdfNumber(cmyk.getMagenta()));
                array.add(new PdfNumber(cmyk.getYellow()));
                array.add(new PdfNumber(cmyk.getBlack()));
                break;
            case 3:
            case 4:
            case 5:
                throw new RuntimeException("Separations, patterns and shadings are not allowed in MK dictionary.");
            default:
                array.add(new PdfNumber(color.getRed() / 255.0f));
                array.add(new PdfNumber(color.getGreen() / 255.0f));
                array.add(new PdfNumber(color.getBlue() / 255.0f));
                break;
        }
        return array;
    }

    public void setMKBorderColor(Color color) {
        if (color == null) {
            getMK().remove(PdfName.BC);
        } else {
            getMK().put(PdfName.BC, getMKColor(color));
        }
    }

    public void setMKBackgroundColor(Color color) {
        if (color == null) {
            getMK().remove(PdfName.BG);
        } else {
            getMK().put(PdfName.BG, getMKColor(color));
        }
    }

    public void setMKNormalCaption(String caption) {
        getMK().put(PdfName.CA, new PdfString(caption, PdfObject.TEXT_UNICODE));
    }

    public void setMKRolloverCaption(String caption) {
        getMK().put(PdfName.RC, new PdfString(caption, PdfObject.TEXT_UNICODE));
    }

    public void setMKAlternateCaption(String caption) {
        getMK().put(PdfName.AC, new PdfString(caption, PdfObject.TEXT_UNICODE));
    }

    public void setMKNormalIcon(PdfTemplate template) {
        getMK().put(PdfName.I, template.getIndirectReference());
    }

    public void setMKRolloverIcon(PdfTemplate template) {
        getMK().put(PdfName.RI, template.getIndirectReference());
    }

    public void setMKAlternateIcon(PdfTemplate template) {
        getMK().put(PdfName.IX, template.getIndirectReference());
    }

    void setMKIconFit(PdfName scale, PdfName scalingType, float leftoverLeft, float leftoverBottom, boolean fitInBounds) {
        PdfDictionary dic = new PdfDictionary();
        if (!scale.equals(PdfName.A)) {
            dic.put(PdfName.SW, scale);
        }
        if (!scalingType.equals(PdfName.P)) {
            dic.put(PdfName.S, scalingType);
        }
        if (leftoverLeft != 0.5f || leftoverBottom != 0.5f) {
            PdfArray array = new PdfArray(new PdfNumber(leftoverLeft));
            array.add(new PdfNumber(leftoverBottom));
            dic.put(PdfName.A, array);
        }
        if (fitInBounds) {
            dic.put(PdfName.FB, PdfBoolean.PDFTRUE);
        }
        getMK().put(PdfName.IF, dic);
    }

    public void setMKTextPosition(int tp) {
        getMK().put(PdfName.TP, new PdfNumber(tp));
    }

    public void setLayer(PdfOCG layer) {
        put(PdfName.OC, layer.getRef());
    }

    public void setName(String name) {
        put(PdfName.NM, new PdfString(name));
    }
}
