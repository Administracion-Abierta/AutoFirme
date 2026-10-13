package org.apache.pdfbox.pdmodel;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;
import org.apache.fontbox.ttf.OpenTypeScript;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDDocumentInformation.class */
public class PDDocumentInformation implements COSObjectable {
    private final COSDictionary info;

    public PDDocumentInformation() {
        this.info = new COSDictionary();
    }

    public PDDocumentInformation(COSDictionary dic) {
        this.info = dic;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.info;
    }

    public Object getPropertyStringValue(String propertyKey) {
        return this.info.getString(propertyKey);
    }

    public String getTitle() {
        return this.info.getString(COSName.TITLE);
    }

    public void setTitle(String title) {
        this.info.setString(COSName.TITLE, title);
    }

    public String getAuthor() {
        return this.info.getString(COSName.AUTHOR);
    }

    public void setAuthor(String author) {
        this.info.setString(COSName.AUTHOR, author);
    }

    public String getSubject() {
        return this.info.getString(COSName.SUBJECT);
    }

    public void setSubject(String subject) {
        this.info.setString(COSName.SUBJECT, subject);
    }

    public String getKeywords() {
        return this.info.getString(COSName.KEYWORDS);
    }

    public void setKeywords(String keywords) {
        this.info.setString(COSName.KEYWORDS, keywords);
    }

    public String getCreator() {
        return this.info.getString(COSName.CREATOR);
    }

    public void setCreator(String creator) {
        this.info.setString(COSName.CREATOR, creator);
    }

    public String getProducer() {
        return this.info.getString(COSName.PRODUCER);
    }

    public void setProducer(String producer) {
        this.info.setString(COSName.PRODUCER, producer);
    }

    public Calendar getCreationDate() {
        return this.info.getDate(COSName.CREATION_DATE);
    }

    public void setCreationDate(Calendar date) {
        this.info.setDate(COSName.CREATION_DATE, date);
    }

    public Calendar getModificationDate() {
        return this.info.getDate(COSName.MOD_DATE);
    }

    public void setModificationDate(Calendar date) {
        this.info.setDate(COSName.MOD_DATE, date);
    }

    public String getTrapped() {
        return this.info.getNameAsString(COSName.TRAPPED);
    }

    public Set<String> getMetadataKeys() {
        Set<String> keys = new TreeSet<>();
        for (COSName key : this.info.keySet()) {
            keys.add(key.getName());
        }
        return keys;
    }

    public String getCustomMetadataValue(String fieldName) {
        return this.info.getString(fieldName);
    }

    public void setCustomMetadataValue(String fieldName, String fieldValue) {
        this.info.setString(fieldName, fieldValue);
    }

    public void setTrapped(String value) {
        if (value != null && !value.equals("True") && !value.equals("False") && !value.equals(OpenTypeScript.UNKNOWN)) {
            throw new IllegalArgumentException("Valid values for trapped are 'True', 'False', or 'Unknown'");
        }
        this.info.setName(COSName.TRAPPED, value);
    }
}
