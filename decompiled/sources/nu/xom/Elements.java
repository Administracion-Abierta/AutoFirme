package nu.xom;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Elements.class */
public final class Elements {
    private List elements = new ArrayList(1);

    Elements() {
    }

    public int size() {
        return this.elements.size();
    }

    public Element get(int i) {
        return (Element) this.elements.get(i);
    }

    void add(Element element) {
        this.elements.add(element);
    }
}
