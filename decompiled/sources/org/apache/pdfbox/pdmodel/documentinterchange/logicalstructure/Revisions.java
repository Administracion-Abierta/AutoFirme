package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/Revisions.class */
public class Revisions<T> {
    private List<T> objects;
    private List<Integer> revisionNumbers;

    private List<T> getObjects() {
        if (this.objects == null) {
            this.objects = new ArrayList();
        }
        return this.objects;
    }

    private List<Integer> getRevisionNumbers() {
        if (this.revisionNumbers == null) {
            this.revisionNumbers = new ArrayList();
        }
        return this.revisionNumbers;
    }

    public T getObject(int index) {
        return getObjects().get(index);
    }

    public int getRevisionNumber(int index) {
        return getRevisionNumbers().get(index).intValue();
    }

    public void addObject(T object, int revisionNumber) {
        getObjects().add(object);
        getRevisionNumbers().add(Integer.valueOf(revisionNumber));
    }

    protected void setRevisionNumber(T object, int revisionNumber) {
        int index = getObjects().indexOf(object);
        if (index > -1) {
            getRevisionNumbers().set(index, Integer.valueOf(revisionNumber));
        }
    }

    public int size() {
        return getObjects().size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getObjects().size(); i++) {
            if (i > 0) {
                sb.append("; ");
            }
            sb.append("object=").append(getObjects().get(i)).append(", revisionNumber=").append(getRevisionNumber(i));
        }
        return sb.toString();
    }
}
