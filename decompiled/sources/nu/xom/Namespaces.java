package nu.xom;

import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Namespaces.class */
class Namespaces {
    private HashMap namespaces = new HashMap(1);
    private ArrayList prefixes = new ArrayList(1);

    Namespaces() {
    }

    void put(String str, String str2) {
        this.namespaces.put(str, str2);
        this.prefixes.remove(str);
        this.prefixes.add(str);
    }

    void remove(String str) {
        if (str == null) {
            str = "";
        }
        this.namespaces.remove(str);
        this.prefixes.remove(str);
    }

    String getURI(String str) {
        return (String) this.namespaces.get(str);
    }

    ArrayList getPrefixes() {
        return this.prefixes;
    }

    Namespaces copy() {
        Namespaces namespaces = new Namespaces();
        namespaces.namespaces = (HashMap) this.namespaces.clone();
        namespaces.prefixes = (ArrayList) this.prefixes.clone();
        return namespaces;
    }

    int size() {
        return this.prefixes.size();
    }

    String getPrefix(int i) {
        return (String) this.prefixes.get(i);
    }
}
