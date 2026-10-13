package org.apache.pdfbox.pdmodel.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSString;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/COSArrayList.class */
public class COSArrayList<E> implements List<E> {
    private final COSArray array;
    private final List<E> actual;
    private boolean isFiltered;
    private COSDictionary parentDict;
    private COSName dictKey;

    public COSArrayList() {
        this.isFiltered = false;
        this.array = new COSArray();
        this.actual = new ArrayList();
    }

    public COSArrayList(List<E> actualList, COSArray cosArray) {
        this.isFiltered = false;
        this.actual = actualList;
        this.array = cosArray;
        if (this.actual.size() != this.array.size()) {
            this.isFiltered = true;
        }
    }

    public COSArrayList(COSDictionary dictionary, COSName dictionaryKey) {
        this.isFiltered = false;
        this.array = new COSArray();
        this.actual = new ArrayList();
        this.parentDict = dictionary;
        this.dictKey = dictionaryKey;
    }

    public COSArrayList(E actualObject, COSBase item, COSDictionary dictionary, COSName dictionaryKey) {
        this.isFiltered = false;
        this.array = new COSArray();
        this.array.add(item);
        this.actual = new ArrayList();
        this.actual.add(actualObject);
        this.parentDict = dictionary;
        this.dictKey = dictionaryKey;
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.actual.size();
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.actual.isEmpty();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.actual.contains(o);
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return this.actual.iterator();
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return this.actual.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <X> X[] toArray(X[] xArr) {
        return (X[]) this.actual.toArray(xArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.List, java.util.Collection
    public boolean add(E e) {
        if (this.parentDict != null) {
            this.parentDict.setItem(this.dictKey, (COSBase) this.array);
            this.parentDict = null;
        }
        if (e instanceof String) {
            this.array.add((COSBase) new COSString((String) e));
        } else if (this.array != null) {
            this.array.add(((COSObjectable) e).getCOSObject());
        }
        return this.actual.add(e);
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("removing entries from a filtered List is not permitted");
        }
        boolean retval = true;
        int index = this.actual.indexOf(o);
        if (index >= 0) {
            this.actual.remove(index);
            this.array.remove(index);
        } else {
            retval = false;
        }
        return retval;
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.actual.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Adding to a filtered List is not permitted");
        }
        if (this.parentDict != null && c.size() > 0) {
            this.parentDict.setItem(this.dictKey, (COSBase) this.array);
            this.parentDict = null;
        }
        this.array.addAll(toCOSObjectList(c));
        return this.actual.addAll(c);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Inserting to a filtered List is not permitted");
        }
        if (this.parentDict != null && c.size() > 0) {
            this.parentDict.setItem(this.dictKey, (COSBase) this.array);
            this.parentDict = null;
        }
        this.array.addAll(index, toCOSObjectList(c));
        return this.actual.addAll(index, c);
    }

    public static List<Integer> convertIntegerCOSArrayToList(COSArray intArray) {
        COSBase object;
        List<Integer> retval = null;
        if (intArray != null) {
            List<Integer> numbers = new ArrayList<>();
            for (int i = 0; i < intArray.size(); i++) {
                if (intArray.get(i) instanceof COSObject) {
                    object = ((COSObject) intArray.get(i)).getObject();
                } else {
                    object = intArray.get(i);
                }
                COSNumber num = (COSNumber) object;
                numbers.add(Integer.valueOf(num.intValue()));
            }
            retval = new COSArrayList<>(numbers, intArray);
        }
        return retval;
    }

    public static List<Float> convertFloatCOSArrayToList(COSArray floatArray) {
        List<Float> retval = null;
        if (floatArray != null) {
            List<Float> numbers = new ArrayList<>(floatArray.size());
            for (int i = 0; i < floatArray.size(); i++) {
                COSBase base = floatArray.getObject(i);
                if (base instanceof COSNumber) {
                    numbers.add(Float.valueOf(((COSNumber) base).floatValue()));
                } else {
                    numbers.add(null);
                }
            }
            retval = new COSArrayList<>(numbers, floatArray);
        }
        return retval;
    }

    public static List<String> convertCOSNameCOSArrayToList(COSArray nameArray) {
        List<String> retval = null;
        if (nameArray != null) {
            List<String> names = new ArrayList<>();
            for (int i = 0; i < nameArray.size(); i++) {
                names.add(((COSName) nameArray.getObject(i)).getName());
            }
            retval = new COSArrayList<>(names, nameArray);
        }
        return retval;
    }

    public static List<String> convertCOSStringCOSArrayToList(COSArray stringArray) {
        List<String> retval = null;
        if (stringArray != null) {
            List<String> string = new ArrayList<>();
            for (int i = 0; i < stringArray.size(); i++) {
                string.add(((COSString) stringArray.getObject(i)).getString());
            }
            retval = new COSArrayList<>(string, stringArray);
        }
        return retval;
    }

    public static COSArray convertStringListToCOSNameCOSArray(List<String> strings) {
        COSArray retval = new COSArray();
        for (String string : strings) {
            retval.add((COSBase) COSName.getPDFName(string));
        }
        return retval;
    }

    public static COSArray convertStringListToCOSStringCOSArray(List<String> strings) {
        COSArray retval = new COSArray();
        for (String string : strings) {
            retval.add((COSBase) new COSString(string));
        }
        return retval;
    }

    public static COSArray converterToCOSArray(List<?> cosObjectableList) {
        COSArray array = null;
        if (cosObjectableList != null) {
            if (cosObjectableList instanceof COSArrayList) {
                array = ((COSArrayList) cosObjectableList).array;
            } else {
                array = new COSArray();
                for (Object next : cosObjectableList) {
                    if (next instanceof String) {
                        array.add((COSBase) new COSString((String) next));
                    } else if ((next instanceof Integer) || (next instanceof Long)) {
                        array.add((COSBase) COSInteger.get(((Number) next).longValue()));
                    } else if ((next instanceof Float) || (next instanceof Double)) {
                        array.add((COSBase) new COSFloat(((Number) next).floatValue()));
                    } else if (next instanceof COSObjectable) {
                        COSObjectable object = (COSObjectable) next;
                        array.add(object.getCOSObject());
                    } else if (next == null) {
                        array.add((COSBase) COSNull.NULL);
                    } else {
                        throw new IllegalArgumentException("Error: Don't know how to convert type to COSBase '" + next.getClass().getName() + OperatorName.SHOW_TEXT_LINE);
                    }
                }
            }
        }
        return array;
    }

    private List<COSBase> toCOSObjectList(Collection<?> list) {
        List<COSBase> cosObjects = new ArrayList<>(list.size());
        for (Object next : list) {
            if (next instanceof String) {
                cosObjects.add(new COSString((String) next));
            } else {
                COSObjectable cos = (COSObjectable) next;
                cosObjects.add(cos.getCOSObject());
            }
        }
        return cosObjects;
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        Iterator<?> it = c.iterator();
        while (it.hasNext()) {
            COSBase itemCOSBase = ((COSObjectable) it.next()).getCOSObject();
            for (int i = this.array.size() - 1; i >= 0; i--) {
                if (itemCOSBase.equals(this.array.getObject(i))) {
                    this.array.remove(i);
                }
            }
        }
        return this.actual.removeAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        Iterator<?> it = c.iterator();
        while (it.hasNext()) {
            COSBase itemCOSBase = ((COSObjectable) it.next()).getCOSObject();
            for (int i = this.array.size() - 1; i >= 0; i--) {
                if (!itemCOSBase.equals(this.array.getObject(i))) {
                    this.array.remove(i);
                }
            }
        }
        return this.actual.retainAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        if (this.parentDict != null) {
            this.parentDict.setItem(this.dictKey, (COSBase) null);
        }
        this.actual.clear();
        this.array.clear();
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object o) {
        return this.actual.equals(o);
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        return this.actual.hashCode();
    }

    @Override // java.util.List
    public E get(int index) {
        return this.actual.get(index);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.List
    public E set(int index, E e) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Replacing an element in a filtered List is not permitted");
        }
        if (e instanceof String) {
            COSString item = new COSString((String) e);
            if (this.parentDict != null && index == 0) {
                this.parentDict.setItem(this.dictKey, (COSBase) item);
            }
            this.array.set(index, (COSBase) item);
        } else {
            if (this.parentDict != null && index == 0) {
                this.parentDict.setItem(this.dictKey, ((COSObjectable) e).getCOSObject());
            }
            this.array.set(index, ((COSObjectable) e).getCOSObject());
        }
        return this.actual.set(index, e);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.List
    public void add(int index, E e) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Adding an element in a filtered List is not permitted");
        }
        if (this.parentDict != null) {
            this.parentDict.setItem(this.dictKey, (COSBase) this.array);
            this.parentDict = null;
        }
        this.actual.add(index, e);
        if (e instanceof String) {
            this.array.add(index, new COSString((String) e));
        } else {
            this.array.add(index, ((COSObjectable) e).getCOSObject());
        }
    }

    @Override // java.util.List
    public E remove(int index) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("removing entries from a filtered List is not permitted");
        }
        this.array.remove(index);
        return this.actual.remove(index);
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.actual.indexOf(o);
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.actual.lastIndexOf(o);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return this.actual.listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return this.actual.listIterator(index);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return this.actual.subList(fromIndex, toIndex);
    }

    public String toString() {
        return "COSArrayList{" + this.array.toString() + "}";
    }

    public COSArray getCOSArray() {
        return this.array;
    }

    @Deprecated
    public COSArray toList() {
        return this.array;
    }
}
