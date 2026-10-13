package org.apache.pdfbox.pdmodel.graphics.color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDDeviceNProcess.class */
public class PDDeviceNProcess {
    private final COSDictionary dictionary;

    public PDDeviceNProcess() {
        this.dictionary = new COSDictionary();
    }

    public PDDeviceNProcess(COSDictionary attributes) {
        this.dictionary = attributes;
    }

    public COSDictionary getCOSDictionary() {
        return this.dictionary;
    }

    public PDColorSpace getColorSpace() throws IOException {
        COSBase cosColorSpace = this.dictionary.getDictionaryObject(COSName.COLORSPACE);
        if (cosColorSpace == null) {
            return null;
        }
        return PDColorSpace.create(cosColorSpace);
    }

    public List<String> getComponents() {
        COSArray cosComponents = this.dictionary.getCOSArray(COSName.COMPONENTS);
        if (cosComponents == null) {
            return new ArrayList(0);
        }
        List<String> components = new ArrayList<>(cosComponents.size());
        for (COSBase name : cosComponents) {
            components.add(((COSName) name).getName());
        }
        return components;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Process{");
        try {
            sb.append(getColorSpace());
            for (String component : getComponents()) {
                sb.append(" \"");
                sb.append(component);
                sb.append('\"');
            }
        } catch (IOException e) {
            sb.append("ERROR");
        }
        sb.append('}');
        return sb.toString();
    }
}
