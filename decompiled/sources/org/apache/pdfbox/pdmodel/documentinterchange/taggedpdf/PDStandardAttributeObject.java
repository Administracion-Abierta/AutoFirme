package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDStandardAttributeObject.class */
public abstract class PDStandardAttributeObject extends PDAttributeObject {
    protected static final float UNSPECIFIED = -1.0f;

    public PDStandardAttributeObject() {
    }

    public PDStandardAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public boolean isSpecified(String name) {
        return getCOSObject().getDictionaryObject(name) != null;
    }

    protected String getString(String name) {
        return getCOSObject().getString(name);
    }

    protected void setString(String name, String value) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        getCOSObject().setString(name, value);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected String[] getArrayOfString(String name) {
        COSBase v = getCOSObject().getDictionaryObject(name);
        if (v instanceof COSArray) {
            COSArray array = (COSArray) v;
            String[] strings = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                strings[i] = ((COSName) array.getObject(i)).getName();
            }
            return strings;
        }
        return null;
    }

    protected void setArrayOfString(String name, String[] values) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        COSArray array = new COSArray();
        for (String value : values) {
            array.add((COSBase) new COSString(value));
        }
        getCOSObject().setItem(name, (COSBase) array);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected String getName(String name) {
        return getCOSObject().getNameAsString(name);
    }

    protected String getName(String name, String defaultValue) {
        return getCOSObject().getNameAsString(name, defaultValue);
    }

    protected Object getNameOrArrayOfName(String name, String defaultValue) {
        COSBase v = getCOSObject().getDictionaryObject(name);
        if (v instanceof COSArray) {
            COSArray array = (COSArray) v;
            String[] names = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                COSBase item = array.getObject(i);
                if (item instanceof COSName) {
                    names[i] = ((COSName) item).getName();
                }
            }
            return names;
        }
        if (v instanceof COSName) {
            return ((COSName) v).getName();
        }
        return defaultValue;
    }

    protected void setName(String name, String value) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        getCOSObject().setName(name, value);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected void setArrayOfName(String name, String[] values) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        COSArray array = new COSArray();
        for (String value : values) {
            array.add((COSBase) COSName.getPDFName(value));
        }
        getCOSObject().setItem(name, (COSBase) array);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected Object getNumberOrName(String name, String defaultValue) {
        COSBase value = getCOSObject().getDictionaryObject(name);
        if (value instanceof COSNumber) {
            return Float.valueOf(((COSNumber) value).floatValue());
        }
        if (value instanceof COSName) {
            return ((COSName) value).getName();
        }
        return defaultValue;
    }

    protected int getInteger(String name, int defaultValue) {
        return getCOSObject().getInt(name, defaultValue);
    }

    protected void setInteger(String name, int value) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        getCOSObject().setInt(name, value);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected float getNumber(String name, float defaultValue) {
        return getCOSObject().getFloat(name, defaultValue);
    }

    protected float getNumber(String name) {
        return getCOSObject().getFloat(name);
    }

    protected Object getNumberOrArrayOfNumber(String name, float defaultValue) {
        COSBase v = getCOSObject().getDictionaryObject(name);
        if (v instanceof COSArray) {
            COSArray array = (COSArray) v;
            float[] values = new float[array.size()];
            for (int i = 0; i < array.size(); i++) {
                COSBase item = array.getObject(i);
                if (item instanceof COSNumber) {
                    values[i] = ((COSNumber) item).floatValue();
                }
            }
            return values;
        }
        if (v instanceof COSNumber) {
            return Float.valueOf(((COSNumber) v).floatValue());
        }
        if (defaultValue == -1.0f) {
            return null;
        }
        return Float.valueOf(defaultValue);
    }

    protected void setNumber(String name, float value) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        getCOSObject().setFloat(name, value);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected void setNumber(String name, int value) {
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        getCOSObject().setInt(name, value);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected void setArrayOfNumber(String name, float[] values) {
        COSArray array = new COSArray();
        for (float value : values) {
            array.add((COSBase) new COSFloat(value));
        }
        COSBase oldBase = getCOSObject().getDictionaryObject(name);
        getCOSObject().setItem(name, (COSBase) array);
        COSBase newBase = getCOSObject().getDictionaryObject(name);
        potentiallyNotifyChanged(oldBase, newBase);
    }

    protected PDGamma getColor(String name) {
        COSArray c = (COSArray) getCOSObject().getDictionaryObject(name);
        if (c != null) {
            return new PDGamma(c);
        }
        return null;
    }

    protected Object getColorOrFourColors(String name) {
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(name);
        if (array == null) {
            return null;
        }
        if (array.size() == 3) {
            return new PDGamma(array);
        }
        if (array.size() == 4) {
            return new PDFourColours(array);
        }
        return null;
    }

    protected void setColor(String name, PDGamma value) {
        COSBase oldValue = getCOSObject().getDictionaryObject(name);
        getCOSObject().setItem(name, value);
        COSBase newValue = value == null ? null : value.getCOSObject();
        potentiallyNotifyChanged(oldValue, newValue);
    }

    protected void setFourColors(String name, PDFourColours value) {
        COSBase oldValue = getCOSObject().getDictionaryObject(name);
        getCOSObject().setItem(name, value);
        COSBase newValue = value == null ? null : value.getCOSObject();
        potentiallyNotifyChanged(oldValue, newValue);
    }
}
