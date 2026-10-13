package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAppearanceCharacteristicsDictionary.class */
public class PDAppearanceCharacteristicsDictionary implements COSObjectable {
    private final COSDictionary dictionary;

    public PDAppearanceCharacteristicsDictionary(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public int getRotation() {
        return getCOSObject().getInt(COSName.R, 0);
    }

    public void setRotation(int rotation) {
        getCOSObject().setInt(COSName.R, rotation);
    }

    public PDColor getBorderColour() {
        return getColor(COSName.BC);
    }

    public void setBorderColour(PDColor c) {
        getCOSObject().setItem(COSName.BC, (COSBase) c.toCOSArray());
    }

    public PDColor getBackground() {
        return getColor(COSName.BG);
    }

    public void setBackground(PDColor c) {
        getCOSObject().setItem(COSName.BG, (COSBase) c.toCOSArray());
    }

    public String getNormalCaption() {
        return getCOSObject().getString(COSName.CA);
    }

    public void setNormalCaption(String caption) {
        getCOSObject().setString(COSName.CA, caption);
    }

    public String getRolloverCaption() {
        return getCOSObject().getString(COSName.RC);
    }

    public void setRolloverCaption(String caption) {
        getCOSObject().setString(COSName.RC, caption);
    }

    public String getAlternateCaption() {
        return getCOSObject().getString(COSName.AC);
    }

    public void setAlternateCaption(String caption) {
        getCOSObject().setString(COSName.AC, caption);
    }

    public PDFormXObject getNormalIcon() {
        COSBase i = getCOSObject().getDictionaryObject(COSName.I);
        if (i instanceof COSStream) {
            return new PDFormXObject((COSStream) i);
        }
        return null;
    }

    public PDFormXObject getRolloverIcon() {
        COSBase i = getCOSObject().getDictionaryObject(COSName.RI);
        if (i instanceof COSStream) {
            return new PDFormXObject((COSStream) i);
        }
        return null;
    }

    public PDFormXObject getAlternateIcon() {
        COSBase i = getCOSObject().getDictionaryObject(COSName.IX);
        if (i instanceof COSStream) {
            return new PDFormXObject((COSStream) i);
        }
        return null;
    }

    private PDColor getColor(COSName itemName) {
        PDColorSpace colorSpace;
        COSBase c = getCOSObject().getItem(itemName);
        if (c instanceof COSArray) {
            switch (((COSArray) c).size()) {
                case 1:
                    colorSpace = PDDeviceGray.INSTANCE;
                    break;
                case 2:
                default:
                    return null;
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
}
