package org.apache.pdfbox.cos;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSObject.class */
public class COSObject extends COSBase implements COSUpdateInfo {
    private COSBase baseObject;
    private long objectNumber;
    private int generationNumber;
    private boolean needToBeUpdated;

    public COSObject(COSBase object) throws IOException {
        setObject(object);
    }

    public COSBase getDictionaryObject(COSName key) {
        COSBase retval = null;
        if (this.baseObject instanceof COSDictionary) {
            retval = ((COSDictionary) this.baseObject).getDictionaryObject(key);
        }
        return retval;
    }

    public COSBase getItem(COSName key) {
        COSBase retval = null;
        if (this.baseObject instanceof COSDictionary) {
            retval = ((COSDictionary) this.baseObject).getItem(key);
        }
        return retval;
    }

    public COSBase getObject() {
        return this.baseObject;
    }

    public final void setObject(COSBase object) throws IOException {
        this.baseObject = object;
    }

    public String toString() {
        return "COSObject{" + this.objectNumber + ", " + this.generationNumber + "}";
    }

    public long getObjectNumber() {
        return this.objectNumber;
    }

    public void setObjectNumber(long objectNum) {
        this.objectNumber = objectNum;
    }

    public int getGenerationNumber() {
        return this.generationNumber;
    }

    public void setGenerationNumber(int generationNumberValue) {
        this.generationNumber = generationNumberValue;
    }

    @Override // org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        COSBase object = getObject();
        return object != null ? object.accept(visitor) : COSNull.NULL.accept(visitor);
    }

    @Override // org.apache.pdfbox.cos.COSUpdateInfo
    public boolean isNeedToBeUpdated() {
        return this.needToBeUpdated;
    }

    @Override // org.apache.pdfbox.cos.COSUpdateInfo
    public void setNeedToBeUpdated(boolean flag) {
        this.needToBeUpdated = flag;
    }
}
