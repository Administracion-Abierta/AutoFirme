package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationText.class */
public class FDFAnnotationText extends FDFAnnotation {
    public static final String SUBTYPE = "Text";

    public FDFAnnotationText() {
        this.annot.setName(COSName.SUBTYPE, "Text");
    }

    public FDFAnnotationText(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationText(Element element) throws IOException {
        String statemodel;
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Text");
        String icon = element.getAttribute("icon");
        if (icon != null && !icon.isEmpty()) {
            setIcon(element.getAttribute("icon"));
        }
        String state = element.getAttribute("state");
        if (state != null && !state.isEmpty() && (statemodel = element.getAttribute("statemodel")) != null && !statemodel.isEmpty()) {
            setState(element.getAttribute("state"));
            setStateModel(element.getAttribute("statemodel"));
        }
    }

    public void setIcon(String icon) {
        this.annot.setName(COSName.NAME, icon);
    }

    public String getIcon() {
        return this.annot.getNameAsString(COSName.NAME, "Note");
    }

    public String getState() {
        return this.annot.getString(COSName.STATE);
    }

    public void setState(String state) {
        this.annot.setString(COSName.STATE, state);
    }

    public String getStateModel() {
        return this.annot.getString(COSName.STATE_MODEL);
    }

    public void setStateModel(String stateModel) {
        this.annot.setString(COSName.STATE_MODEL, stateModel);
    }
}
