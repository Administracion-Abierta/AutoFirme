package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDNameTreeNode.class */
public abstract class PDNameTreeNode<T extends COSObjectable> implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDNameTreeNode.class);
    private final COSDictionary node;
    private PDNameTreeNode<T> parent;

    protected abstract T convertCOSToPD(COSBase cOSBase) throws IOException;

    protected abstract PDNameTreeNode<T> createChildNode(COSDictionary cOSDictionary);

    protected PDNameTreeNode() {
        this.node = new COSDictionary();
    }

    protected PDNameTreeNode(COSDictionary dict) {
        this.node = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.node;
    }

    public PDNameTreeNode<T> getParent() {
        return this.parent;
    }

    public void setParent(PDNameTreeNode<T> parentNode) {
        this.parent = parentNode;
        calculateLimits();
    }

    public boolean isRootNode() {
        return this.parent == null;
    }

    public List<PDNameTreeNode<T>> getKids() {
        List<PDNameTreeNode<T>> retval = null;
        COSArray kids = this.node.getCOSArray(COSName.KIDS);
        if (kids != null) {
            List<PDNameTreeNode<T>> pdObjects = new ArrayList<>(kids.size());
            for (int i = 0; i < kids.size(); i++) {
                pdObjects.add(createChildNode((COSDictionary) kids.getObject(i)));
            }
            retval = new COSArrayList<>(pdObjects, kids);
        }
        return retval;
    }

    public void setKids(List<? extends PDNameTreeNode<T>> kids) {
        if (kids != null && !kids.isEmpty()) {
            for (PDNameTreeNode<T> kidsNode : kids) {
                kidsNode.setParent(this);
            }
            this.node.setItem(COSName.KIDS, (COSBase) COSArrayList.converterToCOSArray(kids));
            if (isRootNode()) {
                this.node.setItem(COSName.NAMES, (COSBase) null);
            }
        } else {
            this.node.setItem(COSName.KIDS, (COSBase) null);
            this.node.setItem(COSName.LIMITS, (COSBase) null);
        }
        calculateLimits();
    }

    private void calculateLimits() {
        if (isRootNode()) {
            this.node.setItem(COSName.LIMITS, (COSBase) null);
            return;
        }
        List<PDNameTreeNode<T>> kids = getKids();
        if (kids != null && !kids.isEmpty()) {
            PDNameTreeNode<T> firstKid = kids.get(0);
            PDNameTreeNode<T> lastKid = kids.get(kids.size() - 1);
            String lowerLimit = firstKid.getLowerLimit();
            setLowerLimit(lowerLimit);
            String upperLimit = lastKid.getUpperLimit();
            setUpperLimit(upperLimit);
            return;
        }
        try {
            Map<String, T> names = getNames();
            if (names != null && names.size() > 0) {
                Set<String> strings = names.keySet();
                String[] keys = (String[]) strings.toArray(new String[strings.size()]);
                String lowerLimit2 = keys[0];
                setLowerLimit(lowerLimit2);
                String upperLimit2 = keys[keys.length - 1];
                setUpperLimit(upperLimit2);
            } else {
                this.node.setItem(COSName.LIMITS, (COSBase) null);
            }
        } catch (IOException exception) {
            this.node.setItem(COSName.LIMITS, (COSBase) null);
            LOG.error("Error while calculating the Limits of a PageNameTreeNode:", exception);
        }
    }

    public T getValue(String str) throws IOException {
        Map<String, T> names = getNames();
        if (names != null) {
            return names.get(str);
        }
        List<PDNameTreeNode<T>> kids = getKids();
        if (kids != null) {
            for (int i = 0; i < kids.size(); i++) {
                PDNameTreeNode<T> pDNameTreeNode = kids.get(i);
                String upperLimit = pDNameTreeNode.getUpperLimit();
                String lowerLimit = pDNameTreeNode.getLowerLimit();
                if (upperLimit == null || lowerLimit == null || upperLimit.compareTo(lowerLimit) < 0 || (lowerLimit.compareTo(str) <= 0 && upperLimit.compareTo(str) >= 0)) {
                    return (T) pDNameTreeNode.getValue(str);
                }
            }
            return null;
        }
        LOG.warn("NameTreeNode does not have \"names\" nor \"kids\" objects.");
        return null;
    }

    public Map<String, T> getNames() throws IOException {
        COSArray namesArray = this.node.getCOSArray(COSName.NAMES);
        if (namesArray != null) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            if (namesArray.size() % 2 != 0) {
                LOG.warn("Names array has odd size: " + namesArray.size());
            }
            for (int i = 0; i + 1 < namesArray.size(); i += 2) {
                COSBase base = namesArray.getObject(i);
                if (!(base instanceof COSString)) {
                    throw new IOException("Expected string, found " + base + " in name tree at index " + i);
                }
                COSString key = (COSString) base;
                COSBase cosValue = namesArray.getObject(i + 1);
                linkedHashMap.put(key.getString(), convertCOSToPD(cosValue));
            }
            return Collections.unmodifiableMap(linkedHashMap);
        }
        return null;
    }

    public void setNames(Map<String, T> names) {
        if (names == null) {
            this.node.setItem(COSName.NAMES, (COSObjectable) null);
            this.node.setItem(COSName.LIMITS, (COSObjectable) null);
            return;
        }
        COSArray array = new COSArray();
        List<String> keys = new ArrayList<>(names.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            array.add((COSBase) new COSString(key));
            array.add(names.get(key));
        }
        this.node.setItem(COSName.NAMES, (COSBase) array);
        calculateLimits();
    }

    public String getUpperLimit() {
        String retval = null;
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr != null) {
            retval = arr.getString(1);
        }
        return retval;
    }

    private void setUpperLimit(String upper) {
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add((COSBase) null);
            arr.add((COSBase) null);
            this.node.setItem(COSName.LIMITS, (COSBase) arr);
        }
        arr.setString(1, upper);
    }

    public String getLowerLimit() {
        String retval = null;
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr != null) {
            retval = arr.getString(0);
        }
        return retval;
    }

    private void setLowerLimit(String lower) {
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add((COSBase) null);
            arr.add((COSBase) null);
            this.node.setItem(COSName.LIMITS, (COSBase) arr);
        }
        arr.setString(0, lower);
    }
}
