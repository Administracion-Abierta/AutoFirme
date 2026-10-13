package org.apache.pdfbox.pdmodel.graphics.state;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDFontSetting;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/state/PDExtendedGraphicsState.class */
public class PDExtendedGraphicsState implements COSObjectable {
    private final COSDictionary dict;

    public PDExtendedGraphicsState() {
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.TYPE, (COSBase) COSName.EXT_G_STATE);
    }

    public PDExtendedGraphicsState(COSDictionary dictionary) {
        this.dict = dictionary;
    }

    public void copyIntoGraphicsState(PDGraphicsState gs) throws IOException {
        for (COSName key : this.dict.keySet()) {
            if (key.equals(COSName.LW)) {
                gs.setLineWidth(defaultIfNull(getLineWidth(), 1.0f));
            } else if (key.equals(COSName.LC)) {
                gs.setLineCap(getLineCapStyle());
            } else if (key.equals(COSName.LJ)) {
                gs.setLineJoin(getLineJoinStyle());
            } else if (key.equals(COSName.ML)) {
                gs.setMiterLimit(defaultIfNull(getMiterLimit(), 10.0f));
            } else if (key.equals(COSName.D)) {
                gs.setLineDashPattern(getLineDashPattern());
            } else if (key.equals(COSName.RI)) {
                gs.setRenderingIntent(getRenderingIntent());
            } else if (key.equals(COSName.OPM)) {
                gs.setOverprintMode(defaultIfNull(getOverprintMode(), ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            } else if (key.equals(COSName.OP)) {
                gs.setOverprint(getStrokingOverprintControl());
            } else if (key.equals(COSName.OP_NS)) {
                gs.setNonStrokingOverprint(getNonStrokingOverprintControl());
            } else if (key.equals(COSName.FONT)) {
                PDFontSetting setting = getFontSetting();
                if (setting != null) {
                    gs.getTextState().setFont(setting.getFont());
                    gs.getTextState().setFontSize(setting.getFontSize());
                }
            } else if (key.equals(COSName.FL)) {
                gs.setFlatness(defaultIfNull(getFlatnessTolerance(), 1.0f));
            } else if (key.equals(COSName.SM)) {
                gs.setSmoothness(defaultIfNull(getSmoothnessTolerance(), ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            } else if (key.equals(COSName.SA)) {
                gs.setStrokeAdjustment(getAutomaticStrokeAdjustment());
            } else if (key.equals(COSName.CA)) {
                gs.setAlphaConstant(defaultIfNull(getStrokingAlphaConstant(), 1.0f));
            } else if (key.equals(COSName.CA_NS)) {
                gs.setNonStrokeAlphaConstant(defaultIfNull(getNonStrokingAlphaConstant(), 1.0f));
            } else if (key.equals(COSName.AIS)) {
                gs.setAlphaSource(getAlphaSourceFlag());
            } else if (key.equals(COSName.TK)) {
                gs.getTextState().setKnockoutFlag(getTextKnockoutFlag());
            } else if (key.equals(COSName.SMASK)) {
                PDSoftMask softmask = getSoftMask();
                if (softmask != null) {
                    softmask.setInitialTransformationMatrix(gs.getCurrentTransformationMatrix().m1288clone());
                }
                gs.setSoftMask(softmask);
            } else if (key.equals(COSName.BM)) {
                gs.setBlendMode(getBlendMode());
            } else if (key.equals(COSName.TR)) {
                if (!this.dict.containsKey(COSName.TR2)) {
                    gs.setTransfer(getTransfer());
                }
            } else if (key.equals(COSName.TR2)) {
                gs.setTransfer(getTransfer2());
            }
        }
    }

    private float defaultIfNull(Float standardValue, float defaultValue) {
        return standardValue != null ? standardValue.floatValue() : defaultValue;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    public Float getLineWidth() {
        return getFloatItem(COSName.LW);
    }

    public void setLineWidth(Float width) {
        setFloatItem(COSName.LW, width);
    }

    public int getLineCapStyle() {
        return this.dict.getInt(COSName.LC);
    }

    public void setLineCapStyle(int style) {
        this.dict.setInt(COSName.LC, style);
    }

    public int getLineJoinStyle() {
        return this.dict.getInt(COSName.LJ);
    }

    public void setLineJoinStyle(int style) {
        this.dict.setInt(COSName.LJ, style);
    }

    public Float getMiterLimit() {
        return getFloatItem(COSName.ML);
    }

    public void setMiterLimit(Float miterLimit) {
        setFloatItem(COSName.ML, miterLimit);
    }

    public PDLineDashPattern getLineDashPattern() {
        PDLineDashPattern retval = null;
        COSBase dp = this.dict.getDictionaryObject(COSName.D);
        if ((dp instanceof COSArray) && ((COSArray) dp).size() == 2) {
            COSBase dashArray = ((COSArray) dp).getObject(0);
            COSBase phase = ((COSArray) dp).getObject(1);
            if ((dashArray instanceof COSArray) && (phase instanceof COSNumber)) {
                retval = new PDLineDashPattern((COSArray) dashArray, ((COSNumber) phase).intValue());
            }
        }
        return retval;
    }

    public void setLineDashPattern(PDLineDashPattern dashPattern) {
        this.dict.setItem(COSName.D, dashPattern.getCOSObject());
    }

    public RenderingIntent getRenderingIntent() {
        String ri = this.dict.getNameAsString("RI");
        if (ri != null) {
            return RenderingIntent.fromString(ri);
        }
        return null;
    }

    public void setRenderingIntent(String ri) {
        this.dict.setName("RI", ri);
    }

    public boolean getStrokingOverprintControl() {
        return this.dict.getBoolean(COSName.OP, false);
    }

    public void setStrokingOverprintControl(boolean op) {
        this.dict.setBoolean(COSName.OP, op);
    }

    public boolean getNonStrokingOverprintControl() {
        return this.dict.getBoolean(COSName.OP_NS, getStrokingOverprintControl());
    }

    public void setNonStrokingOverprintControl(boolean op) {
        this.dict.setBoolean(COSName.OP_NS, op);
    }

    public Float getOverprintMode() {
        return getFloatItem(COSName.OPM);
    }

    public void setOverprintMode(Float overprintMode) {
        setFloatItem(COSName.OPM, overprintMode);
    }

    public PDFontSetting getFontSetting() {
        PDFontSetting setting = null;
        COSBase base = this.dict.getDictionaryObject(COSName.FONT);
        if (base instanceof COSArray) {
            COSArray font = (COSArray) base;
            setting = new PDFontSetting(font);
        }
        return setting;
    }

    public void setFontSetting(PDFontSetting fs) {
        this.dict.setItem(COSName.FONT, fs);
    }

    public Float getFlatnessTolerance() {
        return getFloatItem(COSName.FL);
    }

    public void setFlatnessTolerance(Float flatness) {
        setFloatItem(COSName.FL, flatness);
    }

    public Float getSmoothnessTolerance() {
        return getFloatItem(COSName.SM);
    }

    public void setSmoothnessTolerance(Float smoothness) {
        setFloatItem(COSName.SM, smoothness);
    }

    public boolean getAutomaticStrokeAdjustment() {
        return this.dict.getBoolean(COSName.SA, false);
    }

    public void setAutomaticStrokeAdjustment(boolean sa) {
        this.dict.setBoolean(COSName.SA, sa);
    }

    public Float getStrokingAlphaConstant() {
        return getFloatItem(COSName.CA);
    }

    public void setStrokingAlphaConstant(Float alpha) {
        setFloatItem(COSName.CA, alpha);
    }

    public Float getNonStrokingAlphaConstant() {
        return getFloatItem(COSName.CA_NS);
    }

    public void setNonStrokingAlphaConstant(Float alpha) {
        setFloatItem(COSName.CA_NS, alpha);
    }

    public boolean getAlphaSourceFlag() {
        return this.dict.getBoolean(COSName.AIS, false);
    }

    public void setAlphaSourceFlag(boolean alpha) {
        this.dict.setBoolean(COSName.AIS, alpha);
    }

    public BlendMode getBlendMode() {
        return BlendMode.getInstance(this.dict.getDictionaryObject(COSName.BM));
    }

    public void setBlendMode(BlendMode bm) {
        this.dict.setItem(COSName.BM, (COSBase) BlendMode.getCOSName(bm));
    }

    public PDSoftMask getSoftMask() {
        if (!this.dict.containsKey(COSName.SMASK)) {
            return null;
        }
        return PDSoftMask.create(this.dict.getDictionaryObject(COSName.SMASK));
    }

    public boolean getTextKnockoutFlag() {
        return this.dict.getBoolean(COSName.TK, true);
    }

    public void setTextKnockoutFlag(boolean tk) {
        this.dict.setBoolean(COSName.TK, tk);
    }

    private Float getFloatItem(COSName key) {
        Float retval = null;
        COSBase base = this.dict.getDictionaryObject(key);
        if (base instanceof COSNumber) {
            COSNumber value = (COSNumber) base;
            retval = Float.valueOf(value.floatValue());
        }
        return retval;
    }

    private void setFloatItem(COSName key, Float value) {
        if (value == null) {
            this.dict.removeItem(key);
        } else {
            this.dict.setItem(key, (COSBase) new COSFloat(value.floatValue()));
        }
    }

    public COSBase getTransfer() {
        COSBase base = this.dict.getDictionaryObject(COSName.TR);
        if ((base instanceof COSArray) && ((COSArray) base).size() != 4) {
            return null;
        }
        return base;
    }

    public void setTransfer(COSBase transfer) {
        this.dict.setItem(COSName.TR, transfer);
    }

    public COSBase getTransfer2() {
        COSBase base = this.dict.getDictionaryObject(COSName.TR2);
        if ((base instanceof COSArray) && ((COSArray) base).size() != 4) {
            return null;
        }
        return base;
    }

    public void setTransfer2(COSBase transfer2) {
        this.dict.setItem(COSName.TR2, transfer2);
    }
}
