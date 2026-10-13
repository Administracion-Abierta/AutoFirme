package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDStructureNode.class */
public abstract class PDStructureNode implements COSObjectable {
    private final COSDictionary dictionary;

    protected PDStructureNode(String type) {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, type);
    }

    protected PDStructureNode(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public static PDStructureNode create(COSDictionary node) {
        String type = node.getNameAsString(COSName.TYPE);
        if ("StructTreeRoot".equals(type)) {
            return new PDStructureTreeRoot(node);
        }
        if (type == null || PDStructureElement.TYPE.equals(type)) {
            return new PDStructureElement(node);
        }
        throw new IllegalArgumentException("Dictionary must not include a Type entry with a value that is neither StructTreeRoot nor StructElem.");
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getType() {
        return getCOSObject().getNameAsString(COSName.TYPE);
    }

    public List<Object> getKids() {
        List<Object> kidObjects = new ArrayList<>();
        COSBase k = getCOSObject().getDictionaryObject(COSName.K);
        if (k instanceof COSArray) {
            for (COSBase kid : (COSArray) k) {
                Object kidObject = createObject(kid);
                if (kidObject != null) {
                    kidObjects.add(kidObject);
                }
            }
        } else {
            Object kidObject2 = createObject(k);
            if (kidObject2 != null) {
                kidObjects.add(kidObject2);
            }
        }
        return kidObjects;
    }

    public void setKids(List<Object> kids) {
        getCOSObject().setItem(COSName.K, (COSBase) COSArrayList.converterToCOSArray(kids));
    }

    public void appendKid(PDStructureElement structureElement) {
        appendObjectableKid(structureElement);
        structureElement.setParent(this);
    }

    protected void appendObjectableKid(COSObjectable objectable) {
        if (objectable == null) {
            return;
        }
        appendKid(objectable.getCOSObject());
    }

    protected void appendKid(COSBase object) {
        if (object == null) {
            return;
        }
        COSBase k = getCOSObject().getDictionaryObject(COSName.K);
        if (k == null) {
            getCOSObject().setItem(COSName.K, object);
            return;
        }
        if (k instanceof COSArray) {
            ((COSArray) k).add(object);
            return;
        }
        COSArray array = new COSArray();
        array.add(k);
        array.add(object);
        getCOSObject().setItem(COSName.K, (COSBase) array);
    }

    public void insertBefore(PDStructureElement newKid, Object refKid) {
        insertObjectableBefore(newKid, refKid);
    }

    protected void insertObjectableBefore(COSObjectable newKid, Object refKid) {
        if (newKid == null) {
            return;
        }
        insertBefore(newKid.getCOSObject(), refKid);
    }

    protected void insertBefore(COSBase newKid, Object refKid) {
        COSBase k;
        if (newKid == null || refKid == null || (k = getCOSObject().getDictionaryObject(COSName.K)) == null) {
            return;
        }
        COSBase refKidBase = null;
        if (refKid instanceof COSObjectable) {
            refKidBase = ((COSObjectable) refKid).getCOSObject();
        } else if (refKid instanceof COSInteger) {
            refKidBase = (COSBase) refKid;
        }
        if (k instanceof COSArray) {
            COSArray array = (COSArray) k;
            int refIndex = array.indexOfObject(refKidBase);
            array.add(refIndex, newKid.getCOSObject());
            return;
        }
        boolean onlyKid = k.equals(refKidBase);
        if (!onlyKid && (k instanceof COSObject)) {
            COSBase kObj = ((COSObject) k).getObject();
            onlyKid = kObj.equals(refKidBase);
        }
        if (onlyKid) {
            COSArray array2 = new COSArray();
            array2.add(newKid);
            array2.add(refKidBase);
            getCOSObject().setItem(COSName.K, (COSBase) array2);
        }
    }

    public boolean removeKid(PDStructureElement structureElement) {
        boolean removed = removeObjectableKid(structureElement);
        if (removed) {
            structureElement.setParent(null);
        }
        return removed;
    }

    protected boolean removeObjectableKid(COSObjectable objectable) {
        if (objectable == null) {
            return false;
        }
        return removeKid(objectable.getCOSObject());
    }

    protected boolean removeKid(COSBase object) {
        COSBase k;
        if (object == null || (k = getCOSObject().getDictionaryObject(COSName.K)) == null) {
            return false;
        }
        if (k instanceof COSArray) {
            COSArray array = (COSArray) k;
            boolean removed = array.removeObject(object);
            if (array.size() == 1) {
                getCOSObject().setItem(COSName.K, array.getObject(0));
            }
            return removed;
        }
        boolean onlyKid = k.equals(object);
        if (!onlyKid && (k instanceof COSObject)) {
            COSBase kObj = ((COSObject) k).getObject();
            onlyKid = kObj.equals(object);
        }
        if (onlyKid) {
            getCOSObject().setItem(COSName.K, (COSBase) null);
            return true;
        }
        return false;
    }

    protected Object createObject(COSBase kid) {
        COSDictionary kidDic = null;
        if (kid instanceof COSDictionary) {
            kidDic = (COSDictionary) kid;
        } else if (kid instanceof COSObject) {
            COSBase base = ((COSObject) kid).getObject();
            if (base instanceof COSDictionary) {
                kidDic = (COSDictionary) base;
            }
        }
        if (kidDic != null) {
            return createObjectFromDic(kidDic);
        }
        if (kid instanceof COSInteger) {
            COSInteger mcid = (COSInteger) kid;
            return Integer.valueOf(mcid.intValue());
        }
        return null;
    }

    private COSObjectable createObjectFromDic(COSDictionary kidDic) {
        String type = kidDic.getNameAsString(COSName.TYPE);
        if (type == null || PDStructureElement.TYPE.equals(type)) {
            return new PDStructureElement(kidDic);
        }
        if (PDObjectReference.TYPE.equals(type)) {
            return new PDObjectReference(kidDic);
        }
        if (PDMarkedContentReference.TYPE.equals(type)) {
            return new PDMarkedContentReference(kidDic);
        }
        return null;
    }
}
