package org.apache.pdfbox.cos;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSArray.class */
public class COSArray extends COSBase implements Iterable<COSBase>, COSUpdateInfo {
    private final List<COSBase> objects = new ArrayList();
    private boolean needToBeUpdated;

    public void add(COSBase object) {
        this.objects.add(object);
    }

    public void add(COSObjectable object) {
        this.objects.add(object.getCOSObject());
    }

    public void add(int i, COSBase object) {
        this.objects.add(i, object);
    }

    public void clear() {
        this.objects.clear();
    }

    public void removeAll(Collection<COSBase> objectsList) {
        this.objects.removeAll(objectsList);
    }

    public void retainAll(Collection<COSBase> objectsList) {
        this.objects.retainAll(objectsList);
    }

    public void addAll(Collection<COSBase> objectsList) {
        this.objects.addAll(objectsList);
    }

    public void addAll(COSArray objectList) {
        if (objectList != null) {
            this.objects.addAll(objectList.objects);
        }
    }

    public void addAll(int i, Collection<COSBase> objectList) {
        this.objects.addAll(i, objectList);
    }

    public void set(int index, COSBase object) {
        this.objects.set(index, object);
    }

    public void set(int index, int intVal) {
        this.objects.set(index, COSInteger.get(intVal));
    }

    public void set(int index, COSObjectable object) {
        COSBase base = null;
        if (object != null) {
            base = object.getCOSObject();
        }
        this.objects.set(index, base);
    }

    public COSBase getObject(int index) {
        COSBase obj = this.objects.get(index);
        if (obj instanceof COSObject) {
            obj = ((COSObject) obj).getObject();
        }
        if (obj instanceof COSNull) {
            obj = null;
        }
        return obj;
    }

    public COSBase get(int index) {
        return this.objects.get(index);
    }

    public int getInt(int index) {
        return getInt(index, -1);
    }

    public int getInt(int index, int defaultValue) {
        int retval = defaultValue;
        if (index < size()) {
            Object obj = this.objects.get(index);
            if (obj instanceof COSNumber) {
                retval = ((COSNumber) obj).intValue();
            }
        }
        return retval;
    }

    public void setInt(int index, int value) {
        set(index, (COSBase) COSInteger.get(value));
    }

    public void setName(int index, String name) {
        set(index, (COSBase) COSName.getPDFName(name));
    }

    public String getName(int index) {
        return getName(index, null);
    }

    public String getName(int index, String defaultValue) {
        String retval = defaultValue;
        if (index < size()) {
            Object obj = this.objects.get(index);
            if (obj instanceof COSName) {
                retval = ((COSName) obj).getName();
            }
        }
        return retval;
    }

    public void setString(int index, String string) {
        if (string != null) {
            set(index, (COSBase) new COSString(string));
        } else {
            set(index, (COSBase) null);
        }
    }

    public String getString(int index) {
        return getString(index, null);
    }

    public String getString(int index, String defaultValue) {
        String retval = defaultValue;
        if (index < size()) {
            Object obj = this.objects.get(index);
            if (obj instanceof COSString) {
                retval = ((COSString) obj).getString();
            }
        }
        return retval;
    }

    public int size() {
        return this.objects.size();
    }

    public COSBase remove(int i) {
        return this.objects.remove(i);
    }

    public boolean remove(COSBase o) {
        return this.objects.remove(o);
    }

    public boolean removeObject(COSBase o) {
        boolean removed = remove(o);
        if (!removed) {
            for (int i = 0; i < size(); i++) {
                COSBase entry = get(i);
                if (entry instanceof COSObject) {
                    COSObject objEntry = (COSObject) entry;
                    if (objEntry.getObject().equals(o)) {
                        return remove(entry);
                    }
                }
            }
        }
        return removed;
    }

    public String toString() {
        return "COSArray{" + this.objects + "}";
    }

    @Override // java.lang.Iterable
    public Iterator<COSBase> iterator() {
        return this.objects.iterator();
    }

    public int indexOf(COSBase object) {
        for (int i = 0; i < size(); i++) {
            COSBase item = get(i);
            if (item == null) {
                if (object == null) {
                    return i;
                }
            } else if (item.equals(object)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfObject(COSBase object) {
        for (int i = 0; i < size(); i++) {
            COSBase item = get(i);
            if (item == null) {
                if (item == object) {
                    return i;
                }
            } else if (item.equals(object) || ((item instanceof COSObject) && ((COSObject) item).getObject().equals(object))) {
                return i;
            }
        }
        return -1;
    }

    public void growToSize(int size) {
        growToSize(size, null);
    }

    public void growToSize(int size, COSBase object) {
        while (size() < size) {
            add(object);
        }
    }

    @Override // org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromArray(this);
    }

    @Override // org.apache.pdfbox.cos.COSUpdateInfo
    public boolean isNeedToBeUpdated() {
        return this.needToBeUpdated;
    }

    @Override // org.apache.pdfbox.cos.COSUpdateInfo
    public void setNeedToBeUpdated(boolean flag) {
        this.needToBeUpdated = flag;
    }

    public float[] toFloatArray() {
        float[] retval = new float[size()];
        for (int i = 0; i < size(); i++) {
            COSBase base = getObject(i);
            retval[i] = base instanceof COSNumber ? ((COSNumber) base).floatValue() : ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        return retval;
    }

    public void setFloatArray(float[] value) {
        clear();
        for (float aValue : value) {
            add((COSBase) new COSFloat(aValue));
        }
    }

    public List<? extends COSBase> toList() {
        return new ArrayList(this.objects);
    }
}
