package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.Iterator;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDStructureElement.class */
public class PDStructureElement extends PDStructureNode {
    public static final String TYPE = "StructElem";

    public PDStructureElement(String structureType, PDStructureNode parent) {
        super(TYPE);
        setStructureType(structureType);
        setParent(parent);
    }

    public PDStructureElement(COSDictionary dic) {
        super(dic);
    }

    public String getStructureType() {
        return getCOSObject().getNameAsString(COSName.S);
    }

    public final void setStructureType(String structureType) {
        getCOSObject().setName(COSName.S, structureType);
    }

    public PDStructureNode getParent() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.P);
        if (base instanceof COSDictionary) {
            return PDStructureNode.create((COSDictionary) base);
        }
        return null;
    }

    public final void setParent(PDStructureNode structureNode) {
        getCOSObject().setItem(COSName.P, structureNode);
    }

    public String getElementIdentifier() {
        return getCOSObject().getString(COSName.ID);
    }

    public void setElementIdentifier(String id) {
        getCOSObject().setString(COSName.ID, id);
    }

    public PDPage getPage() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.PG);
        if (base instanceof COSDictionary) {
            return new PDPage((COSDictionary) base);
        }
        return null;
    }

    public void setPage(PDPage page) {
        getCOSObject().setItem(COSName.PG, page);
    }

    public Revisions<PDAttributeObject> getAttributes() {
        Revisions<PDAttributeObject> attributes = new Revisions<>();
        COSBase a = getCOSObject().getDictionaryObject(COSName.A);
        if (a instanceof COSArray) {
            COSArray aa = (COSArray) a;
            Iterator<COSBase> it = aa.iterator();
            PDAttributeObject ao = null;
            while (it.hasNext()) {
                COSBase item = it.next();
                if (item instanceof COSObject) {
                    item = ((COSObject) item).getObject();
                }
                if (item instanceof COSDictionary) {
                    ao = PDAttributeObject.create((COSDictionary) item);
                    ao.setStructureElement(this);
                    attributes.addObject(ao, 0);
                } else if (item instanceof COSInteger) {
                    attributes.setRevisionNumber(ao, ((COSNumber) item).intValue());
                }
            }
        }
        if (a instanceof COSDictionary) {
            PDAttributeObject ao2 = PDAttributeObject.create((COSDictionary) a);
            ao2.setStructureElement(this);
            attributes.addObject(ao2, 0);
        }
        return attributes;
    }

    public void setAttributes(Revisions<PDAttributeObject> attributes) {
        COSName key = COSName.A;
        if (attributes.size() == 1 && attributes.getRevisionNumber(0) == 0) {
            PDAttributeObject attributeObject = attributes.getObject(0);
            attributeObject.setStructureElement(this);
            getCOSObject().setItem(key, attributeObject);
            return;
        }
        COSArray array = new COSArray();
        for (int i = 0; i < attributes.size(); i++) {
            PDAttributeObject attributeObject2 = attributes.getObject(i);
            attributeObject2.setStructureElement(this);
            int revisionNumber = attributes.getRevisionNumber(i);
            if (revisionNumber < 0) {
                throw new IllegalArgumentException("The revision number shall be > -1");
            }
            array.add(attributeObject2);
            array.add(COSInteger.get(revisionNumber));
        }
        getCOSObject().setItem(key, (COSBase) array);
    }

    public void addAttribute(PDAttributeObject attributeObject) {
        COSArray array;
        COSName key = COSName.A;
        attributeObject.setStructureElement(this);
        COSBase a = getCOSObject().getDictionaryObject(key);
        if (a instanceof COSArray) {
            array = (COSArray) a;
        } else {
            array = new COSArray();
            if (a != null) {
                array.add(a);
                array.add((COSBase) COSInteger.get(0L));
            }
        }
        getCOSObject().setItem(key, (COSBase) array);
        array.add(attributeObject);
        array.add((COSBase) COSInteger.get(getRevisionNumber()));
    }

    public void removeAttribute(PDAttributeObject attributeObject) {
        COSName key = COSName.A;
        COSBase a = getCOSObject().getDictionaryObject(key);
        if (a instanceof COSArray) {
            COSArray array = (COSArray) a;
            array.remove(attributeObject.getCOSObject());
            if (array.size() == 2 && array.getInt(1) == 0) {
                getCOSObject().setItem(key, array.getObject(0));
            }
        } else {
            COSBase directA = a;
            if (a instanceof COSObject) {
                directA = ((COSObject) a).getObject();
            }
            if (attributeObject.getCOSObject().equals(directA)) {
                getCOSObject().setItem(key, (COSBase) null);
            }
        }
        attributeObject.setStructureElement(null);
    }

    public void attributeChanged(PDAttributeObject attributeObject) {
        COSName key = COSName.A;
        COSBase a = getCOSObject().getDictionaryObject(key);
        if (a instanceof COSArray) {
            COSArray array = (COSArray) a;
            for (int i = 0; i < array.size(); i++) {
                COSBase entry = array.getObject(i);
                if (entry.equals(attributeObject.getCOSObject())) {
                    COSBase next = array.get(i + 1);
                    if (next instanceof COSInteger) {
                        array.set(i + 1, (COSBase) COSInteger.get(getRevisionNumber()));
                    }
                }
            }
            return;
        }
        COSArray array2 = new COSArray();
        array2.add(a);
        array2.add((COSBase) COSInteger.get(getRevisionNumber()));
        getCOSObject().setItem(key, (COSBase) array2);
    }

    public Revisions<String> getClassNames() {
        COSName key = COSName.C;
        Revisions<String> classNames = new Revisions<>();
        COSBase c = getCOSObject().getDictionaryObject(key);
        if (c instanceof COSName) {
            classNames.addObject(((COSName) c).getName(), 0);
        }
        if (c instanceof COSArray) {
            COSArray array = (COSArray) c;
            Iterator<COSBase> it = array.iterator();
            String className = null;
            while (it.hasNext()) {
                COSBase item = it.next();
                if (item instanceof COSObject) {
                    item = ((COSObject) item).getObject();
                }
                if (item instanceof COSName) {
                    className = ((COSName) item).getName();
                    classNames.addObject(className, 0);
                } else if (item instanceof COSInteger) {
                    classNames.setRevisionNumber(className, ((COSInteger) item).intValue());
                }
            }
        }
        return classNames;
    }

    public void setClassNames(Revisions<String> classNames) {
        if (classNames == null) {
            return;
        }
        COSName key = COSName.C;
        if (classNames.size() == 1 && classNames.getRevisionNumber(0) == 0) {
            String className = classNames.getObject(0);
            getCOSObject().setName(key, className);
            return;
        }
        COSArray array = new COSArray();
        for (int i = 0; i < classNames.size(); i++) {
            String className2 = classNames.getObject(i);
            int revisionNumber = classNames.getRevisionNumber(i);
            if (revisionNumber < 0) {
                throw new IllegalArgumentException("The revision number shall be > -1");
            }
            array.add((COSBase) COSName.getPDFName(className2));
            array.add((COSBase) COSInteger.get(revisionNumber));
        }
        getCOSObject().setItem(key, (COSBase) array);
    }

    public void addClassName(String className) {
        COSArray array;
        if (className == null) {
            return;
        }
        COSName key = COSName.C;
        COSBase c = getCOSObject().getDictionaryObject(key);
        if (c instanceof COSArray) {
            array = (COSArray) c;
        } else {
            array = new COSArray();
            if (c != null) {
                array.add(c);
                array.add((COSBase) COSInteger.get(0L));
            }
        }
        getCOSObject().setItem(key, (COSBase) array);
        array.add((COSBase) COSName.getPDFName(className));
        array.add((COSBase) COSInteger.get(getRevisionNumber()));
    }

    public void removeClassName(String className) {
        if (className == null) {
            return;
        }
        COSName key = COSName.C;
        COSBase c = getCOSObject().getDictionaryObject(key);
        COSName name = COSName.getPDFName(className);
        if (c instanceof COSArray) {
            COSArray array = (COSArray) c;
            array.remove(name);
            if (array.size() == 2 && array.getInt(1) == 0) {
                getCOSObject().setItem(key, array.getObject(0));
                return;
            }
            return;
        }
        COSBase directC = c;
        if (c instanceof COSObject) {
            directC = ((COSObject) c).getObject();
        }
        if (name.equals(directC)) {
            getCOSObject().setItem(key, (COSBase) null);
        }
    }

    public int getRevisionNumber() {
        return getCOSObject().getInt(COSName.R, 0);
    }

    public void setRevisionNumber(int revisionNumber) {
        if (revisionNumber < 0) {
            throw new IllegalArgumentException("The revision number shall be > -1");
        }
        getCOSObject().setInt(COSName.R, revisionNumber);
    }

    public void incrementRevisionNumber() {
        setRevisionNumber(getRevisionNumber() + 1);
    }

    public String getTitle() {
        return getCOSObject().getString(COSName.T);
    }

    public void setTitle(String title) {
        getCOSObject().setString(COSName.T, title);
    }

    public String getLanguage() {
        return getCOSObject().getString(COSName.LANG);
    }

    public void setLanguage(String language) {
        getCOSObject().setString(COSName.LANG, language);
    }

    public String getAlternateDescription() {
        return getCOSObject().getString(COSName.ALT);
    }

    public void setAlternateDescription(String alternateDescription) {
        getCOSObject().setString(COSName.ALT, alternateDescription);
    }

    public String getExpandedForm() {
        return getCOSObject().getString(COSName.E);
    }

    public void setExpandedForm(String expandedForm) {
        getCOSObject().setString(COSName.E, expandedForm);
    }

    public String getActualText() {
        return getCOSObject().getString(COSName.ACTUAL_TEXT);
    }

    public void setActualText(String actualText) {
        getCOSObject().setString(COSName.ACTUAL_TEXT, actualText);
    }

    public String getStandardStructureType() {
        String type = getStructureType();
        Map<String, Object> roleMap = getRoleMap();
        if (roleMap.containsKey(type)) {
            Object mappedValue = getRoleMap().get(type);
            if (mappedValue instanceof String) {
                type = (String) mappedValue;
            }
        }
        return type;
    }

    public void appendKid(PDMarkedContent markedContent) {
        if (markedContent == null) {
            return;
        }
        appendKid(COSInteger.get(markedContent.getMCID()));
    }

    public void appendKid(PDMarkedContentReference markedContentReference) {
        appendObjectableKid(markedContentReference);
    }

    public void appendKid(PDObjectReference objectReference) {
        appendObjectableKid(objectReference);
    }

    public void insertBefore(COSInteger markedContentIdentifier, Object refKid) {
        insertBefore((COSBase) markedContentIdentifier, refKid);
    }

    public void insertBefore(PDMarkedContentReference markedContentReference, Object refKid) {
        insertObjectableBefore(markedContentReference, refKid);
    }

    public void insertBefore(PDObjectReference objectReference, Object refKid) {
        insertObjectableBefore(objectReference, refKid);
    }

    public void removeKid(COSInteger markedContentIdentifier) {
        removeKid((COSBase) markedContentIdentifier);
    }

    public void removeKid(PDMarkedContentReference markedContentReference) {
        removeObjectableKid(markedContentReference);
    }

    public void removeKid(PDObjectReference objectReference) {
        removeObjectableKid(objectReference);
    }

    private PDStructureTreeRoot getStructureTreeRoot() {
        PDStructureNode parent;
        PDStructureNode parent2 = getParent();
        while (true) {
            parent = parent2;
            if (!(parent instanceof PDStructureElement)) {
                break;
            }
            parent2 = ((PDStructureElement) parent).getParent();
        }
        if (parent instanceof PDStructureTreeRoot) {
            return (PDStructureTreeRoot) parent;
        }
        return null;
    }

    private Map<String, Object> getRoleMap() {
        PDStructureTreeRoot root = getStructureTreeRoot();
        if (root != null) {
            return root.getRoleMap();
        }
        return null;
    }
}
