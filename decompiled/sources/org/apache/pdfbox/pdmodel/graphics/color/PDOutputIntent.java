package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDOutputIntent.class */
public final class PDOutputIntent implements COSObjectable {
    private final COSDictionary dictionary;

    public PDOutputIntent(PDDocument doc, InputStream colorProfile) throws IOException {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.TYPE, (COSBase) COSName.OUTPUT_INTENT);
        this.dictionary.setItem(COSName.S, (COSBase) COSName.GTS_PDFA1);
        PDStream destOutputIntent = configureOutputProfile(doc, colorProfile);
        this.dictionary.setItem(COSName.DEST_OUTPUT_PROFILE, destOutputIntent);
    }

    public PDOutputIntent(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.dictionary;
    }

    public COSStream getDestOutputIntent() {
        return (COSStream) this.dictionary.getDictionaryObject(COSName.DEST_OUTPUT_PROFILE);
    }

    public String getInfo() {
        return this.dictionary.getString(COSName.INFO);
    }

    public void setInfo(String value) {
        this.dictionary.setString(COSName.INFO, value);
    }

    public String getOutputCondition() {
        return this.dictionary.getString(COSName.OUTPUT_CONDITION);
    }

    public void setOutputCondition(String value) {
        this.dictionary.setString(COSName.OUTPUT_CONDITION, value);
    }

    public String getOutputConditionIdentifier() {
        return this.dictionary.getString(COSName.OUTPUT_CONDITION_IDENTIFIER);
    }

    public void setOutputConditionIdentifier(String value) {
        this.dictionary.setString(COSName.OUTPUT_CONDITION_IDENTIFIER, value);
    }

    public String getRegistryName() {
        return this.dictionary.getString(COSName.REGISTRY_NAME);
    }

    public void setRegistryName(String value) {
        this.dictionary.setString(COSName.REGISTRY_NAME, value);
    }

    private PDStream configureOutputProfile(PDDocument doc, InputStream colorProfile) throws IOException {
        ICC_Profile icc = ICC_Profile.getInstance(colorProfile);
        PDStream stream = new PDStream(doc, (InputStream) new ByteArrayInputStream(icc.getData()), COSName.FLATE_DECODE);
        stream.getCOSObject().setInt(COSName.N, icc.getNumComponents());
        return stream;
    }
}
