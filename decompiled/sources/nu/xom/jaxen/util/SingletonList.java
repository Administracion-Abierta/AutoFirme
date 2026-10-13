package nu.xom.jaxen.util;

import java.util.AbstractList;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/util/SingletonList.class */
public class SingletonList extends AbstractList {
    private final Object element;

    public SingletonList(Object obj) {
        this.element = obj;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return 1;
    }

    @Override // java.util.AbstractList, java.util.List
    public Object get(int i) {
        if (i == 0) {
            return this.element;
        }
        throw new IndexOutOfBoundsException(new StringBuffer().append(i).append(" != 0").toString());
    }
}
