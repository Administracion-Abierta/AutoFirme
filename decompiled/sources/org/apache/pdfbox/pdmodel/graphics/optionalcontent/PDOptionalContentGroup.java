package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.rendering.RenderDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/optionalcontent/PDOptionalContentGroup.class */
public class PDOptionalContentGroup extends PDPropertyList {
    public PDOptionalContentGroup(String name) {
        this.dict.setItem(COSName.TYPE, (COSBase) COSName.OCG);
        setName(name);
    }

    public PDOptionalContentGroup(COSDictionary dict) {
        super(dict);
        if (!dict.getItem(COSName.TYPE).equals(COSName.OCG)) {
            throw new IllegalArgumentException("Provided dictionary is not of type '" + COSName.OCG + OperatorName.SHOW_TEXT_LINE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/optionalcontent/PDOptionalContentGroup$RenderState.class */
    public enum RenderState {
        ON(COSName.ON),
        OFF(COSName.OFF);

        private final COSName name;

        RenderState(COSName value) {
            this.name = value;
        }

        public static RenderState valueOf(COSName state) {
            if (state == null) {
                return null;
            }
            return valueOf(state.getName().toUpperCase());
        }

        public COSName getName() {
            return this.name;
        }
    }

    public String getName() {
        return this.dict.getString(COSName.NAME);
    }

    public void setName(String name) {
        this.dict.setString(COSName.NAME, name);
    }

    public RenderState getRenderState(RenderDestination destination) {
        COSName state = null;
        COSDictionary usage = (COSDictionary) this.dict.getDictionaryObject("Usage");
        if (usage != null) {
            if (RenderDestination.PRINT.equals(destination)) {
                COSDictionary print = (COSDictionary) usage.getDictionaryObject("Print");
                state = print == null ? null : (COSName) print.getDictionaryObject("PrintState");
            } else if (RenderDestination.VIEW.equals(destination)) {
                COSDictionary view = (COSDictionary) usage.getDictionaryObject("View");
                state = view == null ? null : (COSName) view.getDictionaryObject("ViewState");
            }
            if (state == null) {
                COSDictionary export = (COSDictionary) usage.getDictionaryObject("Export");
                state = export == null ? null : (COSName) export.getDictionaryObject("ExportState");
            }
        }
        if (state == null) {
            return null;
        }
        return RenderState.valueOf(state);
    }

    public String toString() {
        return super.toString() + " (" + getName() + ")";
    }
}
