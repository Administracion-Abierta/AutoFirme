package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDNonTerminalField.class */
public class PDNonTerminalField extends PDField {
    private static final Log LOG = LogFactory.getLog(PDNonTerminalField.class);

    public PDNonTerminalField(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDNonTerminalField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public int getFieldFlags() {
        int retval = 0;
        COSInteger ff = (COSInteger) getCOSObject().getDictionaryObject(COSName.FF);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    void importFDF(FDFField fdfField) throws IOException {
        super.importFDF(fdfField);
        List<FDFField> fdfKids = fdfField.getKids();
        List<PDField> children = getChildren();
        if (fdfKids == null) {
            return;
        }
        for (int i = 0; i < fdfKids.size(); i++) {
            for (PDField pdChild : children) {
                FDFField fdfChild = fdfKids.get(i);
                String fdfName = fdfChild.getPartialFieldName();
                if (fdfName != null && fdfName.equals(pdChild.getPartialName())) {
                    pdChild.importFDF(fdfChild);
                }
            }
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    FDFField exportFDF() throws IOException {
        FDFField fdfField = new FDFField();
        fdfField.setPartialFieldName(getPartialName());
        fdfField.setValue(getValue());
        List<PDField> children = getChildren();
        List<FDFField> fdfChildren = new ArrayList<>(children.size());
        for (PDField child : children) {
            fdfChildren.add(child.exportFDF());
        }
        fdfField.setKids(fdfChildren);
        return fdfField;
    }

    public List<PDField> getChildren() {
        List<PDField> children = new ArrayList<>();
        COSArray kids = getCOSObject().getCOSArray(COSName.KIDS);
        if (kids == null) {
            return children;
        }
        for (int i = 0; i < kids.size(); i++) {
            COSBase kid = kids.getObject(i);
            if (kid instanceof COSDictionary) {
                if (kid.getCOSObject() == getCOSObject()) {
                    LOG.warn("Child field is same object as parent");
                } else {
                    PDField field = PDField.fromDictionary(getAcroForm(), (COSDictionary) kid, this);
                    if (field != null) {
                        children.add(field);
                    }
                }
            }
        }
        return children;
    }

    public void setChildren(List<PDField> children) {
        COSArray kidsArray = COSArrayList.converterToCOSArray(children);
        getCOSObject().setItem(COSName.KIDS, (COSBase) kidsArray);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public String getFieldType() {
        return getCOSObject().getNameAsString(COSName.FT);
    }

    public COSBase getValue() {
        return getCOSObject().getDictionaryObject(COSName.V);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public String getValueAsString() {
        COSBase fieldValue = getCOSObject().getDictionaryObject(COSName.V);
        return fieldValue != null ? fieldValue.toString() : "";
    }

    public void setValue(COSBase object) throws IOException {
        getCOSObject().setItem(COSName.V, object);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public void setValue(String value) throws IOException {
        getCOSObject().setString(COSName.V, value);
    }

    public COSBase getDefaultValue() {
        return getCOSObject().getDictionaryObject(COSName.DV);
    }

    public void setDefaultValue(COSBase value) {
        getCOSObject().setItem(COSName.V, value);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public List<PDAnnotationWidget> getWidgets() {
        return Collections.emptyList();
    }
}
