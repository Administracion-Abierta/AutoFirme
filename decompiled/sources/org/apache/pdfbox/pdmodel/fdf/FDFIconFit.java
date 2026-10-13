package org.apache.pdfbox.pdmodel.fdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFIconFit.class */
public class FDFIconFit implements COSObjectable {
    private COSDictionary fit;
    public static final String SCALE_OPTION_ALWAYS = "A";
    public static final String SCALE_OPTION_ONLY_WHEN_ICON_IS_BIGGER = "B";
    public static final String SCALE_OPTION_ONLY_WHEN_ICON_IS_SMALLER = "S";
    public static final String SCALE_OPTION_NEVER = "N";
    public static final String SCALE_TYPE_ANAMORPHIC = "A";
    public static final String SCALE_TYPE_PROPORTIONAL = "P";

    public FDFIconFit() {
        this.fit = new COSDictionary();
    }

    public FDFIconFit(COSDictionary f) {
        this.fit = f;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.fit;
    }

    public String getScaleOption() {
        String retval = this.fit.getNameAsString(COSName.SW);
        if (retval == null) {
            retval = "A";
        }
        return retval;
    }

    public void setScaleOption(String option) {
        this.fit.setName(COSName.SW, option);
    }

    public String getScaleType() {
        String retval = this.fit.getNameAsString(COSName.S);
        if (retval == null) {
            retval = "P";
        }
        return retval;
    }

    public void setScaleType(String scale) {
        this.fit.setName(COSName.S, scale);
    }

    public PDRange getFractionalSpaceToAllocate() {
        PDRange retval;
        COSArray array = (COSArray) this.fit.getDictionaryObject(COSName.A);
        if (array == null) {
            retval = new PDRange();
            retval.setMin(0.5f);
            retval.setMax(0.5f);
            setFractionalSpaceToAllocate(retval);
        } else {
            retval = new PDRange(array);
        }
        return retval;
    }

    public void setFractionalSpaceToAllocate(PDRange space) {
        this.fit.setItem(COSName.A, space);
    }

    public boolean shouldScaleToFitAnnotation() {
        return this.fit.getBoolean(COSName.FB, false);
    }

    public void setScaleToFitAnnotation(boolean value) {
        this.fit.setBoolean(COSName.FB, value);
    }
}
