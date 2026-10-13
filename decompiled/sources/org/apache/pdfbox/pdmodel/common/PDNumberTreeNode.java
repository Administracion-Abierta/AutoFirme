package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDNumberTreeNode.class */
public class PDNumberTreeNode implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDNumberTreeNode.class);
    private final COSDictionary node;
    private Class<? extends COSObjectable> valueType;

    public PDNumberTreeNode(Class<? extends COSObjectable> valueClass) {
        this.valueType = null;
        this.node = new COSDictionary();
        this.valueType = valueClass;
    }

    public PDNumberTreeNode(COSDictionary dict, Class<? extends COSObjectable> valueClass) {
        this.valueType = null;
        this.node = dict;
        this.valueType = valueClass;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.node;
    }

    public List<PDNumberTreeNode> getKids() {
        List<PDNumberTreeNode> retval = null;
        COSArray kids = (COSArray) this.node.getDictionaryObject(COSName.KIDS);
        if (kids != null) {
            List<PDNumberTreeNode> pdObjects = new ArrayList<>();
            for (int i = 0; i < kids.size(); i++) {
                pdObjects.add(createChildNode((COSDictionary) kids.getObject(i)));
            }
            retval = new COSArrayList<>(pdObjects, kids);
        }
        return retval;
    }

    public void setKids(List<? extends PDNumberTreeNode> kids) {
        if (kids != null && !kids.isEmpty()) {
            PDNumberTreeNode firstKid = kids.get(0);
            PDNumberTreeNode lastKid = kids.get(kids.size() - 1);
            Integer lowerLimit = firstKid.getLowerLimit();
            setLowerLimit(lowerLimit);
            Integer upperLimit = lastKid.getUpperLimit();
            setUpperLimit(upperLimit);
        } else if (this.node.getDictionaryObject(COSName.NUMS) == null) {
            this.node.setItem(COSName.LIMITS, (COSBase) null);
        }
        this.node.setItem(COSName.KIDS, (COSBase) COSArrayList.converterToCOSArray(kids));
    }

    public Object getValue(Integer index) throws IOException {
        Map<Integer, COSObjectable> numbers = getNumbers();
        if (numbers != null) {
            return numbers.get(index);
        }
        Object retval = null;
        List<PDNumberTreeNode> kids = getKids();
        if (kids != null) {
            for (int i = 0; i < kids.size() && retval == null; i++) {
                PDNumberTreeNode childNode = kids.get(i);
                if (childNode.getLowerLimit().compareTo(index) <= 0 && childNode.getUpperLimit().compareTo(index) >= 0) {
                    retval = childNode.getValue(index);
                }
            }
        } else {
            LOG.warn("NumberTreeNode does not have \"nums\" nor \"kids\" objects.");
        }
        return retval;
    }

    public Map<Integer, COSObjectable> getNumbers() throws IOException {
        Map<Integer, COSObjectable> indices = null;
        COSBase numBase = this.node.getDictionaryObject(COSName.NUMS);
        if (numBase instanceof COSArray) {
            COSArray numbersArray = (COSArray) numBase;
            Map<Integer, COSObjectable> indices2 = new HashMap<>();
            if (numbersArray.size() % 2 != 0) {
                LOG.warn("Numbers array has odd size: " + numbersArray.size());
            }
            for (int i = 0; i + 1 < numbersArray.size(); i += 2) {
                COSBase base = numbersArray.getObject(i);
                if (!(base instanceof COSInteger)) {
                    LOG.error("page labels ignored, index " + i + " should be a number, but is " + base);
                    return null;
                }
                COSInteger key = (COSInteger) base;
                COSBase cosValue = numbersArray.getObject(i + 1);
                indices2.put(Integer.valueOf(key.intValue()), cosValue == null ? null : convertCOSToPD(cosValue));
            }
            indices = Collections.unmodifiableMap(indices2);
        }
        return indices;
    }

    protected COSObjectable convertCOSToPD(COSBase base) throws IOException {
        try {
            return this.valueType.getDeclaredConstructor(base.getClass()).newInstance(base);
        } catch (Throwable t) {
            throw new IOException("Error while trying to create value in number tree:" + t.getMessage(), t);
        }
    }

    protected PDNumberTreeNode createChildNode(COSDictionary dic) {
        return new PDNumberTreeNode(dic, this.valueType);
    }

    public void setNumbers(Map<Integer, ? extends COSObjectable> numbers) {
        if (numbers == null) {
            this.node.setItem(COSName.NUMS, (COSObjectable) null);
            this.node.setItem(COSName.LIMITS, (COSObjectable) null);
            return;
        }
        List<Integer> keys = new ArrayList<>(numbers.keySet());
        Collections.sort(keys);
        COSArray array = new COSArray();
        for (Integer key : keys) {
            array.add((COSBase) COSInteger.get(key.intValue()));
            COSObjectable obj = numbers.get(key);
            array.add(obj == null ? COSNull.NULL : obj);
        }
        Integer lower = null;
        Integer upper = null;
        if (!keys.isEmpty()) {
            lower = keys.get(0);
            upper = keys.get(keys.size() - 1);
        }
        setUpperLimit(upper);
        setLowerLimit(lower);
        this.node.setItem(COSName.NUMS, (COSBase) array);
    }

    public Integer getUpperLimit() {
        Integer retval = null;
        COSArray arr = (COSArray) this.node.getDictionaryObject(COSName.LIMITS);
        if (arr != null && arr.get(1) != null) {
            retval = Integer.valueOf(arr.getInt(1));
        }
        return retval;
    }

    private void setUpperLimit(Integer upper) {
        COSArray arr = (COSArray) this.node.getDictionaryObject(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add((COSBase) null);
            arr.add((COSBase) null);
            this.node.setItem(COSName.LIMITS, (COSBase) arr);
        }
        if (upper != null) {
            arr.setInt(1, upper.intValue());
        } else {
            arr.set(1, (COSBase) null);
        }
    }

    public Integer getLowerLimit() {
        Integer retval = null;
        COSArray arr = (COSArray) this.node.getDictionaryObject(COSName.LIMITS);
        if (arr != null && arr.get(0) != null) {
            retval = Integer.valueOf(arr.getInt(0));
        }
        return retval;
    }

    private void setLowerLimit(Integer lower) {
        COSArray arr = (COSArray) this.node.getDictionaryObject(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add((COSBase) null);
            arr.add((COSBase) null);
            this.node.setItem(COSName.LIMITS, (COSBase) arr);
        }
        if (lower != null) {
            arr.setInt(0, lower.intValue());
        } else {
            arr.set(0, (COSBase) null);
        }
    }
}
