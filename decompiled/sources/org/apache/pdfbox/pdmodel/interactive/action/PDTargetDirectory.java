package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDTargetDirectory.class */
public class PDTargetDirectory implements COSObjectable {
    private final COSDictionary dict;

    public PDTargetDirectory() {
        this.dict = new COSDictionary();
    }

    public PDTargetDirectory(COSDictionary dictionary) {
        this.dict = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    public COSName getRelationship() {
        COSBase base = this.dict.getItem(COSName.R);
        if (base instanceof COSName) {
            return (COSName) base;
        }
        return null;
    }

    public void setRelationship(COSName relationship) {
        if (!COSName.P.equals(relationship) && !COSName.C.equals(relationship)) {
            throw new IllegalArgumentException("The only valid are P or C, not " + relationship.getName());
        }
        this.dict.setItem(COSName.R, (COSBase) relationship);
    }

    public String getFilename() {
        return this.dict.getString(COSName.N);
    }

    public void setFilename(String filename) {
        this.dict.setString(COSName.N, filename);
    }

    public PDTargetDirectory getTargetDirectory() {
        COSBase base = this.dict.getDictionaryObject(COSName.T);
        if (base instanceof COSDictionary) {
            return new PDTargetDirectory((COSDictionary) base);
        }
        return null;
    }

    public void setTargetDirectory(PDTargetDirectory targetDirectory) {
        this.dict.setItem(COSName.T, targetDirectory);
    }

    public int getPageNumber() {
        COSBase base = this.dict.getDictionaryObject(COSName.P);
        if (base instanceof COSInteger) {
            return ((COSInteger) base).intValue();
        }
        return -1;
    }

    public void setPageNumber(int pageNumber) {
        if (pageNumber < 0) {
            this.dict.removeItem(COSName.P);
        } else {
            this.dict.setInt(COSName.P, pageNumber);
        }
    }

    public PDNamedDestination getNamedDestination() {
        COSBase base = this.dict.getDictionaryObject(COSName.P);
        if (base instanceof COSString) {
            return new PDNamedDestination((COSString) base);
        }
        return null;
    }

    public void setNamedDestination(PDNamedDestination dest) {
        if (dest == null) {
            this.dict.removeItem(COSName.P);
        } else {
            this.dict.setItem(COSName.P, dest);
        }
    }

    public int getAnnotationIndex() {
        COSBase base = this.dict.getDictionaryObject(COSName.A);
        if (base instanceof COSInteger) {
            return ((COSInteger) base).intValue();
        }
        return -1;
    }

    public void setAnnotationIndex(int index) {
        if (index < 0) {
            this.dict.removeItem(COSName.A);
        } else {
            this.dict.setInt(COSName.A, index);
        }
    }

    public String getAnnotationName() {
        COSBase base = this.dict.getDictionaryObject(COSName.A);
        if (base instanceof COSString) {
            return ((COSString) base).getString();
        }
        return null;
    }

    public void setAnnotationName(String name) {
        this.dict.setString(COSName.A, name);
    }
}
