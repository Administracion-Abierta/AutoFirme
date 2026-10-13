package org.apache.pdfbox.pdmodel.documentinterchange.markedcontent;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDArtifactMarkedContent;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.text.TextPosition;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/markedcontent/PDMarkedContent.class */
public class PDMarkedContent {
    private final String tag;
    private final COSDictionary properties;
    private final List<Object> contents;

    public static PDMarkedContent create(COSName tag, COSDictionary properties) {
        if (COSName.ARTIFACT.equals(tag)) {
            return new PDArtifactMarkedContent(properties);
        }
        return new PDMarkedContent(tag, properties);
    }

    public PDMarkedContent(COSName tag, COSDictionary properties) {
        this.tag = tag == null ? null : tag.getName();
        this.properties = properties;
        this.contents = new ArrayList();
    }

    public String getTag() {
        return this.tag;
    }

    public COSDictionary getProperties() {
        return this.properties;
    }

    public int getMCID() {
        if (getProperties() == null) {
            return -1;
        }
        return getProperties().getInt(COSName.MCID);
    }

    public String getLanguage() {
        if (getProperties() == null) {
            return null;
        }
        return getProperties().getNameAsString(COSName.LANG);
    }

    public String getActualText() {
        if (getProperties() == null) {
            return null;
        }
        return getProperties().getString(COSName.ACTUAL_TEXT);
    }

    public String getAlternateDescription() {
        if (getProperties() == null) {
            return null;
        }
        return getProperties().getString(COSName.ALT);
    }

    public String getExpandedForm() {
        if (getProperties() == null) {
            return null;
        }
        return getProperties().getString(COSName.E);
    }

    public List<Object> getContents() {
        return this.contents;
    }

    public void addText(TextPosition text) {
        getContents().add(text);
    }

    public void addMarkedContent(PDMarkedContent markedContent) {
        getContents().add(markedContent);
    }

    public void addXObject(PDXObject xobject) {
        getContents().add(xobject);
    }

    public String toString() {
        return "tag=" + this.tag + ", properties=" + this.properties + ", contents=" + this.contents;
    }
}
