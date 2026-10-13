package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/optionalcontent/PDOptionalContentMembershipDictionary.class */
public class PDOptionalContentMembershipDictionary extends PDPropertyList {
    public PDOptionalContentMembershipDictionary() {
        this.dict.setItem(COSName.TYPE, (COSBase) COSName.OCMD);
    }

    public PDOptionalContentMembershipDictionary(COSDictionary dict) {
        super(dict);
        if (!dict.getItem(COSName.TYPE).equals(COSName.OCMD)) {
            throw new IllegalArgumentException("Provided dictionary is not of type '" + COSName.OCMD + OperatorName.SHOW_TEXT_LINE);
        }
    }

    public List<PDPropertyList> getOCGs() {
        List<PDPropertyList> list = new ArrayList<>();
        COSBase base = this.dict.getDictionaryObject(COSName.OCGS);
        if (base instanceof COSDictionary) {
            list.add(PDPropertyList.create((COSDictionary) base));
        } else if (base instanceof COSArray) {
            COSArray ar = (COSArray) base;
            for (int i = 0; i < ar.size(); i++) {
                COSBase elem = ar.getObject(i);
                if (elem instanceof COSDictionary) {
                    list.add(PDPropertyList.create((COSDictionary) elem));
                }
            }
        }
        return list;
    }

    public void setOCGs(List<PDPropertyList> ocgs) {
        COSArray ar = new COSArray();
        for (PDPropertyList prop : ocgs) {
            ar.add(prop);
        }
        this.dict.setItem(COSName.OCGS, (COSBase) ar);
    }

    public COSName getVisibilityPolicy() {
        return this.dict.getCOSName(COSName.P, COSName.ANY_ON);
    }

    public void setVisibilityPolicy(COSName visibilityPolicy) {
        this.dict.setItem(COSName.P, (COSBase) visibilityPolicy);
    }
}
