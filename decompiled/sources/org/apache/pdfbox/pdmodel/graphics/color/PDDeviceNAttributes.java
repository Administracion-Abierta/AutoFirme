package org.apache.pdfbox.pdmodel.graphics.color;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSDictionaryMap;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDDeviceNAttributes.class */
public final class PDDeviceNAttributes {
    private final COSDictionary dictionary;

    public PDDeviceNAttributes() {
        this.dictionary = new COSDictionary();
    }

    public PDDeviceNAttributes(COSDictionary attributes) {
        this.dictionary = attributes;
    }

    public COSDictionary getCOSDictionary() {
        return this.dictionary;
    }

    public Map<String, PDSeparation> getColorants() throws IOException {
        Map<String, PDSeparation> actuals = new HashMap<>();
        COSDictionary colorants = this.dictionary.getCOSDictionary(COSName.COLORANTS);
        if (colorants == null) {
            colorants = new COSDictionary();
            this.dictionary.setItem(COSName.COLORANTS, (COSBase) colorants);
        } else {
            for (COSName name : colorants.keySet()) {
                COSBase value = colorants.getDictionaryObject(name);
                actuals.put(name.getName(), (PDSeparation) PDColorSpace.create(value));
            }
        }
        return new COSDictionaryMap(actuals, colorants);
    }

    public PDDeviceNProcess getProcess() {
        COSDictionary process = this.dictionary.getCOSDictionary(COSName.PROCESS);
        if (process == null) {
            return null;
        }
        return new PDDeviceNProcess(process);
    }

    public boolean isNChannel() {
        return "NChannel".equals(this.dictionary.getNameAsString(COSName.SUBTYPE));
    }

    public void setColorants(Map<String, PDColorSpace> colorants) {
        COSDictionary colorantDict = null;
        if (colorants != null) {
            colorantDict = COSDictionaryMap.convert(colorants);
        }
        this.dictionary.setItem(COSName.COLORANTS, (COSBase) colorantDict);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.dictionary.getNameAsString(COSName.SUBTYPE));
        sb.append('{');
        PDDeviceNProcess process = getProcess();
        if (process != null) {
            sb.append(getProcess());
            sb.append(' ');
        }
        try {
            Map<String, PDSeparation> colorants = getColorants();
            sb.append("Colorants{");
            for (Map.Entry<String, PDSeparation> col : colorants.entrySet()) {
                sb.append('\"');
                sb.append(col.getKey());
                sb.append("\": ");
                sb.append(col.getValue());
                sb.append(' ');
            }
            sb.append('}');
        } catch (IOException e) {
            sb.append("ERROR");
        }
        sb.append('}');
        return sb.toString();
    }
}
