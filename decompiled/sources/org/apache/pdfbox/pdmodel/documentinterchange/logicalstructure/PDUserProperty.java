package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDUserProperty.class */
public class PDUserProperty extends PDDictionaryWrapper {
    private final PDUserAttributeObject userAttributeObject;

    public PDUserProperty(PDUserAttributeObject userAttributeObject) {
        this.userAttributeObject = userAttributeObject;
    }

    public PDUserProperty(COSDictionary dictionary, PDUserAttributeObject userAttributeObject) {
        super(dictionary);
        this.userAttributeObject = userAttributeObject;
    }

    public String getName() {
        return getCOSObject().getNameAsString(COSName.N);
    }

    public void setName(String name) {
        potentiallyNotifyChanged(getName(), name);
        getCOSObject().setName(COSName.N, name);
    }

    public COSBase getValue() {
        return getCOSObject().getDictionaryObject(COSName.V);
    }

    public void setValue(COSBase value) {
        potentiallyNotifyChanged(getValue(), value);
        getCOSObject().setItem(COSName.V, value);
    }

    public String getFormattedValue() {
        return getCOSObject().getString(COSName.F);
    }

    public void setFormattedValue(String formattedValue) {
        potentiallyNotifyChanged(getFormattedValue(), formattedValue);
        getCOSObject().setString(COSName.F, formattedValue);
    }

    public boolean isHidden() {
        return getCOSObject().getBoolean(COSName.H, false);
    }

    public void setHidden(boolean hidden) {
        potentiallyNotifyChanged(Boolean.valueOf(isHidden()), Boolean.valueOf(hidden));
        getCOSObject().setBoolean(COSName.H, hidden);
    }

    public String toString() {
        return "Name=" + getName() + ", Value=" + getValue() + ", FormattedValue=" + getFormattedValue() + ", Hidden=" + isHidden();
    }

    private void potentiallyNotifyChanged(Object oldEntry, Object newEntry) {
        if (isEntryChanged(oldEntry, newEntry)) {
            this.userAttributeObject.userPropertyChanged(this);
        }
    }

    private boolean isEntryChanged(Object oldEntry, Object newEntry) {
        if (oldEntry == null) {
            return newEntry != null;
        }
        return !oldEntry.equals(newEntry);
    }

    @Override // org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + (this.userAttributeObject == null ? 0 : this.userAttributeObject.hashCode());
    }

    @Override // org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        PDUserProperty other = (PDUserProperty) obj;
        if (this.userAttributeObject == null) {
            if (other.userAttributeObject != null) {
                return false;
            }
            return true;
        }
        if (!this.userAttributeObject.equals(other.userAttributeObject)) {
            return false;
        }
        return true;
    }
}
