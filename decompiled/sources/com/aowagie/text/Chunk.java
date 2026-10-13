package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.HyphenationEvent;
import com.aowagie.text.pdf.PdfAction;
import com.aowagie.text.pdf.PdfAnnotation;
import com.aowagie.text.pdf.draw.DrawInterface;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Chunk.class */
public class Chunk implements Element {
    public static final String OBJECT_REPLACEMENT_CHARACTER = "￼";
    public static final Chunk NEWLINE = new Chunk("\n");
    public static final Chunk NEXTPAGE = new Chunk("");
    protected StringBuffer content;
    protected Font font;
    protected HashMap attributes;
    public static final String SEPARATOR = "SEPARATOR";
    public static final String TAB = "TAB";
    public static final String HSCALE = "HSCALE";
    public static final String UNDERLINE = "UNDERLINE";
    public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
    public static final String SKEW = "SKEW";
    public static final String BACKGROUND = "BACKGROUND";
    public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
    public static final String SPLITCHARACTER = "SPLITCHARACTER";
    public static final String HYPHENATION = "HYPHENATION";
    public static final String REMOTEGOTO = "REMOTEGOTO";
    public static final String LOCALGOTO = "LOCALGOTO";
    public static final String LOCALDESTINATION = "LOCALDESTINATION";
    public static final String GENERICTAG = "GENERICTAG";
    public static final String IMAGE = "IMAGE";
    public static final String ACTION = "ACTION";
    public static final String NEWPAGE = "NEWPAGE";
    public static final String PDFANNOTATION = "PDFANNOTATION";
    public static final String COLOR = "COLOR";
    public static final String ENCODING = "ENCODING";

    static {
        NEXTPAGE.setNewPage();
    }

    public Chunk() {
        this.content = null;
        this.font = null;
        this.attributes = null;
        this.content = new StringBuffer();
        this.font = new Font();
    }

    public Chunk(Chunk ck) {
        this.content = null;
        this.font = null;
        this.attributes = null;
        if (ck.content != null) {
            this.content = new StringBuffer(ck.content.toString());
        }
        if (ck.font != null) {
            this.font = new Font(ck.font);
        }
        if (ck.attributes != null) {
            this.attributes = new HashMap(ck.attributes);
        }
    }

    public Chunk(String content, Font font) {
        this.content = null;
        this.font = null;
        this.attributes = null;
        this.content = new StringBuffer(content);
        this.font = font;
    }

    public Chunk(String content) {
        this(content, new Font());
    }

    public Chunk(char c, Font font) {
        this.content = null;
        this.font = null;
        this.attributes = null;
        this.content = new StringBuffer();
        this.content.append(c);
        this.font = font;
    }

    public Chunk(char c) {
        this(c, new Font());
    }

    public Chunk(Image image, float offsetX, float offsetY) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        Image copyImage = Image.getInstance(image);
        copyImage.setAbsolutePosition(Float.NaN, Float.NaN);
        setAttribute(IMAGE, new Object[]{copyImage, Float.valueOf(offsetX), Float.valueOf(offsetY), Boolean.FALSE});
    }

    public Chunk(DrawInterface separator) {
        this(separator, false);
    }

    public Chunk(DrawInterface separator, boolean vertical) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        setAttribute(SEPARATOR, new Object[]{separator, Boolean.valueOf(vertical)});
    }

    public Chunk(DrawInterface separator, float tabPosition) {
        this(separator, tabPosition, false);
    }

    public Chunk(DrawInterface separator, float tabPosition, boolean newline) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        if (tabPosition < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            throw new IllegalArgumentException("A tab position may not be lower than 0; yours is " + tabPosition);
        }
        setAttribute(TAB, new Object[]{separator, Float.valueOf(tabPosition), Boolean.valueOf(newline), Float.valueOf(ColumnText.GLOBAL_SPACE_CHAR_RATIO)});
    }

    public Chunk(Image image, float offsetX, float offsetY, boolean changeLeading) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        setAttribute(IMAGE, new Object[]{image, Float.valueOf(offsetX), Float.valueOf(offsetY), Boolean.valueOf(changeLeading)});
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return 10;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        ArrayList tmp = new ArrayList();
        tmp.add(this);
        return tmp;
    }

    public StringBuffer append(String string) {
        return this.content.append(string);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return this.font;
    }

    public String getContent() {
        return this.content.toString();
    }

    @Override // com.aowagie.text.Element
    public String toString() {
        return getContent();
    }

    public boolean isEmpty() {
        return this.content.toString().trim().length() == 0 && this.content.toString().indexOf("\n") == -1 && this.attributes == null;
    }

    public float getWidthPoint() {
        if (getImage() != null) {
            return getImage().getScaledWidth();
        }
        return this.font.getCalculatedBaseFont(true).getWidthPoint(getContent(), this.font.getCalculatedSize()) * getHorizontalScaling();
    }

    public boolean hasAttributes() {
        return this.attributes != null;
    }

    public HashMap getAttributes() {
        return this.attributes;
    }

    public void setAttributes(HashMap attributes) {
        this.attributes = attributes;
    }

    private Chunk setAttribute(String name, Object obj) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        this.attributes.put(name, obj);
        return this;
    }

    public Chunk setHorizontalScaling(float scale) {
        return setAttribute(HSCALE, Float.valueOf(scale));
    }

    public float getHorizontalScaling() {
        Float f;
        if (this.attributes == null || (f = (Float) this.attributes.get(HSCALE)) == null) {
            return 1.0f;
        }
        return f.floatValue();
    }

    public Chunk setUnderline(float thickness, float yPosition) {
        return setUnderline(null, thickness, ColumnText.GLOBAL_SPACE_CHAR_RATIO, yPosition, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0);
    }

    public Chunk setUnderline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int cap) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        Object[] obj = {color, new float[]{thickness, thicknessMul, yPosition, yPositionMul, cap}};
        Object[][] unders = Utilities.addToArray((Object[][]) this.attributes.get(UNDERLINE), obj);
        return setAttribute(UNDERLINE, unders);
    }

    public Chunk setTextRise(float rise) {
        return setAttribute(SUBSUPSCRIPT, Float.valueOf(rise));
    }

    public float getTextRise() {
        if (this.attributes != null && this.attributes.containsKey(SUBSUPSCRIPT)) {
            Float f = (Float) this.attributes.get(SUBSUPSCRIPT);
            return f.floatValue();
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public Chunk setSkew(float alpha, float beta) {
        return setAttribute(SKEW, new float[]{(float) Math.tan((((double) alpha) * 3.141592653589793d) / 180.0d), (float) Math.tan((((double) beta) * 3.141592653589793d) / 180.0d)});
    }

    public Chunk setBackground(Color color) {
        return setBackground(color, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public Chunk setBackground(Color color, float extraLeft, float extraBottom, float extraRight, float extraTop) {
        return setAttribute(BACKGROUND, new Object[]{color, new float[]{extraLeft, extraBottom, extraRight, extraTop}});
    }

    public Chunk setTextRenderMode(int mode, float strokeWidth, Color strokeColor) {
        return setAttribute(TEXTRENDERMODE, new Object[]{Integer.valueOf(mode), Float.valueOf(strokeWidth), strokeColor});
    }

    public Chunk setSplitCharacter(SplitCharacter splitCharacter) {
        return setAttribute(SPLITCHARACTER, splitCharacter);
    }

    public Chunk setHyphenation(HyphenationEvent hyphenation) {
        return setAttribute(HYPHENATION, hyphenation);
    }

    public Chunk setRemoteGoto(String filename, String name) {
        return setAttribute(REMOTEGOTO, new Object[]{filename, name});
    }

    public Chunk setRemoteGoto(String filename, int page) {
        return setAttribute(REMOTEGOTO, new Object[]{filename, Integer.valueOf(page)});
    }

    public Chunk setLocalGoto(String name) {
        return setAttribute(LOCALGOTO, name);
    }

    public Chunk setLocalDestination(String name) {
        return setAttribute(LOCALDESTINATION, name);
    }

    public Chunk setGenericTag(String text) {
        return setAttribute(GENERICTAG, text);
    }

    public Image getImage() {
        Object[] obj;
        if (this.attributes == null || (obj = (Object[]) this.attributes.get(IMAGE)) == null) {
            return null;
        }
        return (Image) obj[0];
    }

    public Chunk setAction(PdfAction action) {
        return setAttribute(ACTION, action);
    }

    public Chunk setAnchor(URL url) {
        return setAttribute(ACTION, new PdfAction(url.toExternalForm()));
    }

    public Chunk setAnchor(String url) {
        return setAttribute(ACTION, new PdfAction(url));
    }

    public Chunk setNewPage() {
        return setAttribute(NEWPAGE, null);
    }

    public Chunk setAnnotation(PdfAnnotation annotation) {
        return setAttribute(PDFANNOTATION, annotation);
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    public HyphenationEvent getHyphenation() {
        if (this.attributes == null) {
            return null;
        }
        return (HyphenationEvent) this.attributes.get(HYPHENATION);
    }
}
