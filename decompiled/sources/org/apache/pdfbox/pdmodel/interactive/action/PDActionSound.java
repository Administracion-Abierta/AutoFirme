package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionSound.class */
public class PDActionSound extends PDAction {
    public static final String SUB_TYPE = "Sound";

    public PDActionSound() {
        setSubType("Sound");
    }

    public PDActionSound(COSDictionary a) {
        super(a);
    }

    @Deprecated
    public String getS() {
        return this.action.getNameAsString(COSName.S);
    }

    @Deprecated
    public void setS(String s) {
        this.action.setName(COSName.S, s);
    }

    public void setSound(COSStream sound) {
        this.action.setItem(COSName.SOUND, (COSBase) sound);
    }

    public COSStream getSound() {
        COSBase base = this.action.getDictionaryObject(COSName.SOUND);
        if (base instanceof COSStream) {
            return (COSStream) base;
        }
        return null;
    }

    public void setVolume(float volume) {
        if (volume < -1.0f || volume > 1.0f) {
            throw new IllegalArgumentException("volume outside of the range −1.0 to 1.0");
        }
        this.action.setFloat(COSName.VOLUME, volume);
    }

    public float getVolume() {
        COSBase base = this.action.getDictionaryObject(COSName.VOLUME);
        if (base instanceof COSNumber) {
            float volume = ((COSNumber) base).floatValue();
            if (volume < -1.0f || volume > 1.0f) {
                volume = 1.0f;
            }
            return volume;
        }
        return 1.0f;
    }

    public void setSynchronous(boolean synchronous) {
        this.action.setBoolean(COSName.SYNCHRONOUS, synchronous);
    }

    public boolean getSynchronous() {
        COSBase base = this.action.getDictionaryObject(COSName.SYNCHRONOUS);
        if (base instanceof COSBoolean) {
            return ((COSBoolean) base).getValue();
        }
        return false;
    }

    public void setRepeat(boolean repeat) {
        this.action.setBoolean(COSName.REPEAT, repeat);
    }

    public boolean getRepeat() {
        COSBase base = this.action.getDictionaryObject(COSName.REPEAT);
        if (base instanceof COSBoolean) {
            return ((COSBoolean) base).getValue();
        }
        return false;
    }

    public void setMix(boolean mix) {
        this.action.setBoolean(COSName.MIX, mix);
    }

    public boolean getMix() {
        COSBase base = this.action.getDictionaryObject(COSName.MIX);
        if (base instanceof COSBoolean) {
            return ((COSBoolean) base).getValue();
        }
        return false;
    }
}
