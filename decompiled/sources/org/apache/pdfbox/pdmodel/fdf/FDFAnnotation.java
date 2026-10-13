package org.apache.pdfbox.pdmodel.fdf;

import com.aowagie.text.ElementTags;
import com.aowagie.text.html.Markup;
import com.aowagie.text.pdf.ColumnText;
import java.awt.Color;
import java.io.IOException;
import java.util.Calendar;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDWindowsLaunchParams;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.DateConverter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotation.class */
public abstract class FDFAnnotation implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(FDFAnnotation.class);
    private static final int FLAG_INVISIBLE = 1;
    private static final int FLAG_HIDDEN = 2;
    private static final int FLAG_PRINTED = 4;
    private static final int FLAG_NO_ZOOM = 8;
    private static final int FLAG_NO_ROTATE = 16;
    private static final int FLAG_NO_VIEW = 32;
    private static final int FLAG_READ_ONLY = 64;
    private static final int FLAG_LOCKED = 128;
    private static final int FLAG_TOGGLE_NO_VIEW = 256;
    private static final int FLAG_LOCKED_CONTENTS = 512;
    protected COSDictionary annot;

    public FDFAnnotation() {
        this.annot = new COSDictionary();
        this.annot.setItem(COSName.TYPE, (COSBase) COSName.ANNOT);
    }

    public FDFAnnotation(COSDictionary a) {
        this.annot = a;
    }

    public FDFAnnotation(Element element) throws IOException {
        this();
        String page = element.getAttribute("page");
        if (page == null || page.isEmpty()) {
            throw new IOException("Error: missing required attribute 'page'");
        }
        setPage(Integer.parseInt(page));
        String color = element.getAttribute("color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setColor(new Color(colorValue));
        }
        setDate(element.getAttribute("date"));
        String flags = element.getAttribute("flags");
        if (flags != null) {
            String[] flagTokens = flags.split(",");
            for (String flagToken : flagTokens) {
                if (flagToken.equals("invisible")) {
                    setInvisible(true);
                } else if (flagToken.equals("hidden")) {
                    setHidden(true);
                } else if (flagToken.equals(PDWindowsLaunchParams.OPERATION_PRINT)) {
                    setPrinted(true);
                } else if (flagToken.equals("nozoom")) {
                    setNoZoom(true);
                } else if (flagToken.equals("norotate")) {
                    setNoRotate(true);
                } else if (flagToken.equals("noview")) {
                    setNoView(true);
                } else if (flagToken.equals("readonly")) {
                    setReadOnly(true);
                } else if (flagToken.equals("locked")) {
                    setLocked(true);
                } else if (flagToken.equals("togglenoview")) {
                    setToggleNoView(true);
                }
            }
        }
        setName(element.getAttribute("name"));
        String rect = element.getAttribute("rect");
        if (rect == null) {
            throw new IOException("Error: missing attribute 'rect'");
        }
        String[] rectValues = rect.split(",");
        if (rectValues.length != 4) {
            throw new IOException("Error: wrong amount of numbers in attribute 'rect'");
        }
        float[] values = new float[4];
        for (int i = 0; i < 4; i++) {
            values[i] = Float.parseFloat(rectValues[i]);
        }
        COSArray array = new COSArray();
        array.setFloatArray(values);
        setRectangle(new PDRectangle(array));
        setTitle(element.getAttribute("title"));
        setCreationDate(DateConverter.toCalendar(element.getAttribute("creationdate")));
        String opac = element.getAttribute("opacity");
        if (opac != null && !opac.isEmpty()) {
            setOpacity(Float.parseFloat(opac));
        }
        setSubject(element.getAttribute("subject"));
        String intent = element.getAttribute("intent");
        setIntent(intent.isEmpty() ? element.getAttribute("IT") : intent);
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            setContents(xpath.evaluate("contents[1]", element));
        } catch (XPathExpressionException e) {
            LOG.debug("Error while evaluating XPath expression for richtext contents");
        }
        try {
            Node richContents = (Node) xpath.evaluate("contents-richtext[1]", element, XPathConstants.NODE);
            if (richContents != null) {
                setRichContents(richContentsToString(richContents, true));
                setContents(richContents.getTextContent().trim());
            }
        } catch (XPathExpressionException e2) {
            LOG.debug("Error while evaluating XPath expression for richtext contents");
        }
        PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
        String width = element.getAttribute(ElementTags.WIDTH);
        if (width != null && !width.isEmpty()) {
            borderStyle.setWidth(Float.parseFloat(width));
        }
        if (borderStyle.getWidth() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            String style = element.getAttribute(Markup.HTML_ATTR_STYLE);
            if (style != null && !style.isEmpty()) {
                if (style.equals("dash")) {
                    borderStyle.setStyle("D");
                } else if (style.equals("bevelled")) {
                    borderStyle.setStyle("B");
                } else if (style.equals("inset")) {
                    borderStyle.setStyle("I");
                } else if (style.equals(Markup.CSS_VALUE_UNDERLINE)) {
                    borderStyle.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
                } else if (style.equals("cloudy")) {
                    borderStyle.setStyle("S");
                    PDBorderEffectDictionary borderEffect = new PDBorderEffectDictionary();
                    borderEffect.setStyle("C");
                    String intensity = element.getAttribute("intensity");
                    if (intensity != null && !intensity.isEmpty()) {
                        borderEffect.setIntensity(Float.parseFloat(element.getAttribute("intensity")));
                    }
                    setBorderEffect(borderEffect);
                } else {
                    borderStyle.setStyle("S");
                }
            }
            String dashes = element.getAttribute("dashes");
            if (dashes != null && !dashes.isEmpty()) {
                String[] dashesValues = dashes.split(",");
                COSArray dashPattern = new COSArray();
                for (String dashesValue : dashesValues) {
                    dashPattern.add((COSBase) COSNumber.get(dashesValue));
                }
                borderStyle.setDashStyle(dashPattern);
            }
            setBorderStyle(borderStyle);
        }
    }

    public static FDFAnnotation create(COSDictionary fdfDic) throws IOException {
        FDFAnnotation retval = null;
        if (fdfDic != null) {
            String fdfDicName = fdfDic.getNameAsString(COSName.SUBTYPE);
            if ("Text".equals(fdfDicName)) {
                retval = new FDFAnnotationText(fdfDic);
            } else if ("Caret".equals(fdfDicName)) {
                retval = new FDFAnnotationCaret(fdfDic);
            } else if ("FreeText".equals(fdfDicName)) {
                retval = new FDFAnnotationFreeText(fdfDic);
            } else if ("FileAttachment".equals(fdfDicName)) {
                retval = new FDFAnnotationFileAttachment(fdfDic);
            } else if ("Highlight".equals(fdfDicName)) {
                retval = new FDFAnnotationHighlight(fdfDic);
            } else if ("Ink".equals(fdfDicName)) {
                retval = new FDFAnnotationInk(fdfDic);
            } else if ("Line".equals(fdfDicName)) {
                retval = new FDFAnnotationLine(fdfDic);
            } else if ("Link".equals(fdfDicName)) {
                retval = new FDFAnnotationLink(fdfDic);
            } else if ("Circle".equals(fdfDicName)) {
                retval = new FDFAnnotationCircle(fdfDic);
            } else if ("Square".equals(fdfDicName)) {
                retval = new FDFAnnotationSquare(fdfDic);
            } else if ("Polygon".equals(fdfDicName)) {
                retval = new FDFAnnotationPolygon(fdfDic);
            } else if (FDFAnnotationPolyline.SUBTYPE.equals(fdfDicName)) {
                retval = new FDFAnnotationPolyline(fdfDic);
            } else if ("Sound".equals(fdfDicName)) {
                retval = new FDFAnnotationSound(fdfDic);
            } else if ("Squiggly".equals(fdfDicName)) {
                retval = new FDFAnnotationSquiggly(fdfDic);
            } else if ("Stamp".equals(fdfDicName)) {
                retval = new FDFAnnotationStamp(fdfDic);
            } else if ("StrikeOut".equals(fdfDicName)) {
                retval = new FDFAnnotationStrikeOut(fdfDic);
            } else if ("Underline".equals(fdfDicName)) {
                retval = new FDFAnnotationUnderline(fdfDic);
            } else {
                LOG.warn("Unknown or unsupported annotation type '" + fdfDicName + OperatorName.SHOW_TEXT_LINE);
            }
        }
        return retval;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.annot;
    }

    public Integer getPage() {
        Integer retval = null;
        COSNumber page = (COSNumber) this.annot.getDictionaryObject(COSName.PAGE);
        if (page != null) {
            retval = Integer.valueOf(page.intValue());
        }
        return retval;
    }

    public final void setPage(int page) {
        this.annot.setInt(COSName.PAGE, page);
    }

    public Color getColor() {
        Color retval = null;
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.C);
        if (array != null) {
            float[] rgb = array.toFloatArray();
            if (rgb.length >= 3) {
                retval = new Color(rgb[0], rgb[1], rgb[2]);
            }
        }
        return retval;
    }

    public final void setColor(Color c) {
        COSArray color = null;
        if (c != null) {
            float[] colors = c.getRGBColorComponents((float[]) null);
            color = new COSArray();
            color.setFloatArray(colors);
        }
        this.annot.setItem(COSName.C, (COSBase) color);
    }

    public String getDate() {
        return this.annot.getString(COSName.M);
    }

    public final void setDate(String date) {
        this.annot.setString(COSName.M, date);
    }

    public boolean isInvisible() {
        return this.annot.getFlag(COSName.F, 1);
    }

    public final void setInvisible(boolean invisible) {
        this.annot.setFlag(COSName.F, 1, invisible);
    }

    public boolean isHidden() {
        return this.annot.getFlag(COSName.F, 2);
    }

    public final void setHidden(boolean hidden) {
        this.annot.setFlag(COSName.F, 2, hidden);
    }

    public boolean isPrinted() {
        return this.annot.getFlag(COSName.F, 4);
    }

    public final void setPrinted(boolean printed) {
        this.annot.setFlag(COSName.F, 4, printed);
    }

    public boolean isNoZoom() {
        return this.annot.getFlag(COSName.F, 8);
    }

    public final void setNoZoom(boolean noZoom) {
        this.annot.setFlag(COSName.F, 8, noZoom);
    }

    public boolean isNoRotate() {
        return this.annot.getFlag(COSName.F, 16);
    }

    public final void setNoRotate(boolean noRotate) {
        this.annot.setFlag(COSName.F, 16, noRotate);
    }

    public boolean isNoView() {
        return this.annot.getFlag(COSName.F, 32);
    }

    public final void setNoView(boolean noView) {
        this.annot.setFlag(COSName.F, 32, noView);
    }

    public boolean isReadOnly() {
        return this.annot.getFlag(COSName.F, 64);
    }

    public final void setReadOnly(boolean readOnly) {
        this.annot.setFlag(COSName.F, 64, readOnly);
    }

    public boolean isLocked() {
        return this.annot.getFlag(COSName.F, 128);
    }

    public final void setLocked(boolean locked) {
        this.annot.setFlag(COSName.F, 128, locked);
    }

    public boolean isToggleNoView() {
        return this.annot.getFlag(COSName.F, 256);
    }

    public final void setToggleNoView(boolean toggleNoView) {
        this.annot.setFlag(COSName.F, 256, toggleNoView);
    }

    public boolean isLockedContents() {
        return this.annot.getFlag(COSName.F, 512);
    }

    public void setLockedContents(boolean lockedContents) {
        this.annot.setFlag(COSName.F, 512, lockedContents);
    }

    public final void setName(String name) {
        this.annot.setString(COSName.NM, name);
    }

    public String getName() {
        return this.annot.getString(COSName.NM);
    }

    public final void setRectangle(PDRectangle rectangle) {
        this.annot.setItem(COSName.RECT, rectangle);
    }

    public PDRectangle getRectangle() {
        PDRectangle retval = null;
        COSArray rectArray = (COSArray) this.annot.getDictionaryObject(COSName.RECT);
        if (rectArray != null) {
            retval = new PDRectangle(rectArray);
        }
        return retval;
    }

    public final void setContents(String contents) {
        this.annot.setString(COSName.CONTENTS, contents);
    }

    public String getContents() {
        return this.annot.getString(COSName.CONTENTS);
    }

    public final void setTitle(String title) {
        this.annot.setString(COSName.T, title);
    }

    public String getTitle() {
        return this.annot.getString(COSName.T);
    }

    public Calendar getCreationDate() throws IOException {
        return this.annot.getDate(COSName.CREATION_DATE);
    }

    public final void setCreationDate(Calendar date) {
        this.annot.setDate(COSName.CREATION_DATE, date);
    }

    public final void setOpacity(float opacity) {
        this.annot.setFloat(COSName.CA, opacity);
    }

    public float getOpacity() {
        return this.annot.getFloat(COSName.CA, 1.0f);
    }

    public final void setSubject(String subject) {
        this.annot.setString(COSName.SUBJ, subject);
    }

    public String getSubject() {
        return this.annot.getString(COSName.SUBJ);
    }

    public final void setIntent(String intent) {
        this.annot.setName(COSName.IT, intent);
    }

    public String getIntent() {
        return this.annot.getNameAsString(COSName.IT);
    }

    public String getRichContents() {
        return getStringOrStream(this.annot.getDictionaryObject(COSName.RC));
    }

    public final void setRichContents(String rc) {
        this.annot.setItem(COSName.RC, (COSBase) new COSString(rc));
    }

    public final void setBorderStyle(PDBorderStyleDictionary bs) {
        this.annot.setItem(COSName.BS, bs);
    }

    public PDBorderStyleDictionary getBorderStyle() {
        COSDictionary bs = (COSDictionary) this.annot.getDictionaryObject(COSName.BS);
        if (bs != null) {
            return new PDBorderStyleDictionary(bs);
        }
        return null;
    }

    public final void setBorderEffect(PDBorderEffectDictionary be) {
        this.annot.setItem(COSName.BE, be);
    }

    public PDBorderEffectDictionary getBorderEffect() {
        COSDictionary be = (COSDictionary) this.annot.getDictionaryObject(COSName.BE);
        if (be != null) {
            return new PDBorderEffectDictionary(be);
        }
        return null;
    }

    protected final String getStringOrStream(COSBase base) {
        if (base == null) {
            return "";
        }
        if (base instanceof COSString) {
            return ((COSString) base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream) base).toTextString();
        }
        return "";
    }

    private String richContentsToString(Node node, boolean root) {
        StringBuilder sb = new StringBuilder();
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node child = nodelist.item(i);
            if (child instanceof Element) {
                sb.append(richContentsToString(child, false));
            } else if (child instanceof CDATASection) {
                sb.append("<![CDATA[").append(((CDATASection) child).getData()).append("]]>");
            } else if (child instanceof Text) {
                String cdata = ((Text) child).getData();
                if (cdata != null) {
                    cdata = cdata.replace("&", "&amp;").replace("<", "&lt;");
                }
                sb.append(cdata);
            }
        }
        if (root) {
            return sb.toString();
        }
        NamedNodeMap attributes = node.getAttributes();
        StringBuilder builder = new StringBuilder();
        for (int i2 = 0; i2 < attributes.getLength(); i2++) {
            Node attribute = attributes.item(i2);
            String attributeNodeValue = attribute.getNodeValue();
            if (attributeNodeValue != null) {
                attributeNodeValue = attributeNodeValue.replace(OperatorName.SHOW_TEXT_LINE_AND_SPACE, "&quot;");
            }
            builder.append(String.format(" %s=\"%s\"", attribute.getNodeName(), attributeNodeValue));
        }
        return String.format("<%s%s>%s</%s>", node.getNodeName(), builder.toString(), sb.toString(), node.getNodeName());
    }
}
