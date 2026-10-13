package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDUserAttributeObject.class */
public class PDUserAttributeObject extends PDAttributeObject {
    public static final String OWNER_USER_PROPERTIES = "UserProperties";

    public PDUserAttributeObject() {
        setOwner(OWNER_USER_PROPERTIES);
    }

    public PDUserAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public List<PDUserProperty> getOwnerUserProperties() {
        COSArray p = (COSArray) getCOSObject().getDictionaryObject(COSName.P);
        List<PDUserProperty> properties = new ArrayList<>(p.size());
        for (int i = 0; i < p.size(); i++) {
            properties.add(new PDUserProperty((COSDictionary) p.getObject(i), this));
        }
        return properties;
    }

    public void setUserProperties(List<PDUserProperty> userProperties) {
        COSArray p = new COSArray();
        for (PDUserProperty userProperty : userProperties) {
            p.add(userProperty);
        }
        getCOSObject().setItem(COSName.P, (COSBase) p);
    }

    public void addUserProperty(PDUserProperty userProperty) {
        COSArray p = (COSArray) getCOSObject().getDictionaryObject(COSName.P);
        p.add(userProperty);
        notifyChanged();
    }

    public void removeUserProperty(PDUserProperty userProperty) {
        if (userProperty == null) {
            return;
        }
        COSArray p = (COSArray) getCOSObject().getDictionaryObject(COSName.P);
        p.remove(userProperty.getCOSObject());
        notifyChanged();
    }

    public void userPropertyChanged(PDUserProperty userProperty) {
    }

    @Override // org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject
    public String toString() {
        return super.toString() + ", userProperties=" + getOwnerUserProperties();
    }
}
