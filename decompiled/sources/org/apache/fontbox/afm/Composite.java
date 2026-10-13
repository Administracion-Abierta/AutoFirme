package org.apache.fontbox.afm;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/Composite.class */
public class Composite {
    private String name;
    private List<CompositePart> parts = new ArrayList();

    public String getName() {
        return this.name;
    }

    public void setName(String nameValue) {
        this.name = nameValue;
    }

    public void addPart(CompositePart part) {
        this.parts.add(part);
    }

    public List<CompositePart> getParts() {
        return this.parts;
    }

    public void setParts(List<CompositePart> partsList) {
        this.parts = partsList;
    }
}
