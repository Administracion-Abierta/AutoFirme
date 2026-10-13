package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotation.class */
public abstract class PDAnnotation implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDAnnotation.class);
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
    private final COSDictionary dictionary;

    public static PDAnnotation createAnnotation(COSBase base) throws IOException {
        PDAnnotation annot;
        if (base instanceof COSDictionary) {
            COSDictionary annotDic = (COSDictionary) base;
            String subtype = annotDic.getNameAsString(COSName.SUBTYPE);
            if ("FileAttachment".equals(subtype)) {
                annot = new PDAnnotationFileAttachment(annotDic);
            } else if ("Line".equals(subtype)) {
                annot = new PDAnnotationLine(annotDic);
            } else if ("Link".equals(subtype)) {
                annot = new PDAnnotationLink(annotDic);
            } else if (PDAnnotationPopup.SUB_TYPE.equals(subtype)) {
                annot = new PDAnnotationPopup(annotDic);
            } else if ("Stamp".equals(subtype)) {
                annot = new PDAnnotationRubberStamp(annotDic);
            } else if ("Square".equals(subtype) || "Circle".equals(subtype)) {
                annot = new PDAnnotationSquareCircle(annotDic);
            } else if ("Text".equals(subtype)) {
                annot = new PDAnnotationText(annotDic);
            } else if ("Highlight".equals(subtype) || "Underline".equals(subtype) || "Squiggly".equals(subtype) || "StrikeOut".equals(subtype)) {
                annot = new PDAnnotationTextMarkup(annotDic);
            } else if (PDAnnotationWidget.SUB_TYPE.equals(subtype)) {
                annot = new PDAnnotationWidget(annotDic);
            } else if ("FreeText".equals(subtype) || "Polygon".equals(subtype) || PDAnnotationMarkup.SUB_TYPE_POLYLINE.equals(subtype) || "Caret".equals(subtype) || "Ink".equals(subtype) || "Sound".equals(subtype)) {
                annot = new PDAnnotationMarkup(annotDic);
            } else {
                annot = new PDAnnotationUnknown(annotDic);
                LOG.debug("Unknown or unsupported annotation subtype " + subtype);
            }
            return annot;
        }
        throw new IOException("Error: Unknown annotation type " + base);
    }

    public PDAnnotation() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.TYPE, (COSBase) COSName.ANNOT);
    }

    public PDAnnotation(COSDictionary dict) {
        this.dictionary = dict;
        COSBase type = dict.getDictionaryObject(COSName.TYPE);
        if (type == null) {
            this.dictionary.setItem(COSName.TYPE, (COSBase) COSName.ANNOT);
        } else if (!COSName.ANNOT.equals(type)) {
            LOG.warn("Annotation has type " + type + ", further mayhem may follow");
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PDAnnotation)) {
            return false;
        }
        COSDictionary toBeCompared = ((PDAnnotation) o).getCOSObject();
        return toBeCompared.equals(getCOSObject());
    }

    public int hashCode() {
        return this.dictionary.hashCode();
    }

    public PDRectangle getRectangle() {
        COSArray rectArray = (COSArray) this.dictionary.getDictionaryObject(COSName.RECT);
        PDRectangle rectangle = null;
        if (rectArray != null) {
            if (rectArray.size() == 4 && (rectArray.getObject(0) instanceof COSNumber) && (rectArray.getObject(1) instanceof COSNumber) && (rectArray.getObject(2) instanceof COSNumber) && (rectArray.getObject(3) instanceof COSNumber)) {
                rectangle = new PDRectangle(rectArray);
            } else {
                LOG.warn(rectArray + " is not a rectangle array, returning null");
            }
        }
        return rectangle;
    }

    public void setRectangle(PDRectangle rectangle) {
        this.dictionary.setItem(COSName.RECT, (COSBase) rectangle.getCOSArray());
    }

    public int getAnnotationFlags() {
        return getCOSObject().getInt(COSName.F, 0);
    }

    public void setAnnotationFlags(int flags) {
        getCOSObject().setInt(COSName.F, flags);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public COSName getAppearanceState() {
        return getCOSObject().getCOSName(COSName.AS);
    }

    public void setAppearanceState(String as) {
        getCOSObject().setName(COSName.AS, as);
    }

    public PDAppearanceDictionary getAppearance() {
        COSBase base = this.dictionary.getDictionaryObject(COSName.AP);
        if (base instanceof COSDictionary) {
            return new PDAppearanceDictionary((COSDictionary) base);
        }
        return null;
    }

    public void setAppearance(PDAppearanceDictionary appearance) {
        this.dictionary.setItem(COSName.AP, appearance);
    }

    public PDAppearanceStream getNormalAppearanceStream() {
        PDAppearanceEntry normalAppearance;
        PDAppearanceDictionary appearanceDict = getAppearance();
        if (appearanceDict == null || (normalAppearance = appearanceDict.getNormalAppearance()) == null) {
            return null;
        }
        if (normalAppearance.isSubDictionary()) {
            COSName state = getAppearanceState();
            return normalAppearance.getSubDictionary().get(state);
        }
        return normalAppearance.getAppearanceStream();
    }

    public boolean isInvisible() {
        return getCOSObject().getFlag(COSName.F, 1);
    }

    public void setInvisible(boolean invisible) {
        getCOSObject().setFlag(COSName.F, 1, invisible);
    }

    public boolean isHidden() {
        return getCOSObject().getFlag(COSName.F, 2);
    }

    public void setHidden(boolean hidden) {
        getCOSObject().setFlag(COSName.F, 2, hidden);
    }

    public boolean isPrinted() {
        return getCOSObject().getFlag(COSName.F, 4);
    }

    public void setPrinted(boolean printed) {
        getCOSObject().setFlag(COSName.F, 4, printed);
    }

    public boolean isNoZoom() {
        return getCOSObject().getFlag(COSName.F, 8);
    }

    public void setNoZoom(boolean noZoom) {
        getCOSObject().setFlag(COSName.F, 8, noZoom);
    }

    public boolean isNoRotate() {
        return getCOSObject().getFlag(COSName.F, 16);
    }

    public void setNoRotate(boolean noRotate) {
        getCOSObject().setFlag(COSName.F, 16, noRotate);
    }

    public boolean isNoView() {
        return getCOSObject().getFlag(COSName.F, 32);
    }

    public void setNoView(boolean noView) {
        getCOSObject().setFlag(COSName.F, 32, noView);
    }

    public boolean isReadOnly() {
        return getCOSObject().getFlag(COSName.F, 64);
    }

    public void setReadOnly(boolean readOnly) {
        getCOSObject().setFlag(COSName.F, 64, readOnly);
    }

    public boolean isLocked() {
        return getCOSObject().getFlag(COSName.F, 128);
    }

    public void setLocked(boolean locked) {
        getCOSObject().setFlag(COSName.F, 128, locked);
    }

    public boolean isToggleNoView() {
        return getCOSObject().getFlag(COSName.F, 256);
    }

    public void setToggleNoView(boolean toggleNoView) {
        getCOSObject().setFlag(COSName.F, 256, toggleNoView);
    }

    public boolean isLockedContents() {
        return getCOSObject().getFlag(COSName.F, 512);
    }

    public void setLockedContents(boolean lockedContents) {
        getCOSObject().setFlag(COSName.F, 512, lockedContents);
    }

    public String getContents() {
        return this.dictionary.getString(COSName.CONTENTS);
    }

    public void setContents(String value) {
        this.dictionary.setString(COSName.CONTENTS, value);
    }

    public String getModifiedDate() {
        return getCOSObject().getString(COSName.M);
    }

    public void setModifiedDate(String m) {
        getCOSObject().setString(COSName.M, m);
    }

    public void setModifiedDate(Calendar c) {
        getCOSObject().setDate(COSName.M, c);
    }

    public String getAnnotationName() {
        return getCOSObject().getString(COSName.NM);
    }

    public void setAnnotationName(String nm) {
        getCOSObject().setString(COSName.NM, nm);
    }

    public int getStructParent() {
        return getCOSObject().getInt(COSName.STRUCT_PARENT);
    }

    public void setStructParent(int structParent) {
        getCOSObject().setInt(COSName.STRUCT_PARENT, structParent);
    }

    public PDPropertyList getOptionalContent() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.OC);
        if (base instanceof COSDictionary) {
            return PDPropertyList.create((COSDictionary) base);
        }
        return null;
    }

    public void setOptionalContent(PDPropertyList oc) {
        getCOSObject().setItem(COSName.OC, oc);
    }

    public COSArray getBorder() {
        COSArray border;
        COSBase base = getCOSObject().getDictionaryObject(COSName.BORDER);
        if (base instanceof COSArray) {
            border = (COSArray) base;
            if (border.size() < 3) {
                COSArray newBorder = new COSArray();
                newBorder.addAll(border);
                border = newBorder;
                while (border.size() < 3) {
                    border.add((COSBase) COSInteger.ZERO);
                }
            }
        } else {
            border = new COSArray();
            border.add((COSBase) COSInteger.ZERO);
            border.add((COSBase) COSInteger.ZERO);
            border.add((COSBase) COSInteger.ONE);
        }
        return border;
    }

    public void setBorder(COSArray borderArray) {
        getCOSObject().setItem(COSName.BORDER, (COSBase) borderArray);
    }

    public void setColor(PDColor c) {
        getCOSObject().setItem(COSName.C, (COSBase) c.toCOSArray());
    }

    public PDColor getColor() {
        return getColor(COSName.C);
    }

    protected PDColor getColor(COSName itemName) {
        COSBase c = getCOSObject().getItem(itemName);
        if (c instanceof COSArray) {
            PDColorSpace colorSpace = null;
            switch (((COSArray) c).size()) {
                case 1:
                    colorSpace = PDDeviceGray.INSTANCE;
                    break;
                case 3:
                    colorSpace = PDDeviceRGB.INSTANCE;
                    break;
                case 4:
                    colorSpace = PDDeviceCMYK.INSTANCE;
                    break;
            }
            return new PDColor((COSArray) c, colorSpace);
        }
        return null;
    }

    public String getSubtype() {
        return getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    public void setPage(PDPage page) {
        getCOSObject().setItem(COSName.P, page);
    }

    public PDPage getPage() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.P);
        if (base instanceof COSDictionary) {
            return new PDPage((COSDictionary) base);
        }
        return null;
    }

    public void constructAppearances(PDDocument document) {
    }

    public void constructAppearances() {
    }
}
