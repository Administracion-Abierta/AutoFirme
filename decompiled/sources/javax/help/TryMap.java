package javax.help;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:javax/help/TryMap.class */
public class TryMap implements Map, Serializable {
    private Vector maps = new Vector();
    private static final boolean debug = false;

    public void add(Map map) {
        this.maps.addElement(map);
    }

    public boolean remove(Map map) {
        return this.maps.removeElement(map);
    }

    public Enumeration getMaps() {
        return this.maps.elements();
    }

    @Override // javax.help.Map
    public boolean isValidID(String str, HelpSet helpSet) {
        debug(new StringBuffer().append("isValidID ").append(str).toString());
        Enumeration enumerationElements = this.maps.elements();
        while (enumerationElements.hasMoreElements()) {
            if (((Map) enumerationElements.nextElement()).isValidID(str, helpSet)) {
                return true;
            }
        }
        return false;
    }

    @Override // javax.help.Map
    public Enumeration getAllIDs() {
        return new TryEnumeration(this.maps.elements(), null);
    }

    @Override // javax.help.Map
    public URL getURLFromID(Map.ID id) throws MalformedURLException {
        debug(new StringBuffer().append("getURLFromID(").append(id).append(")").toString());
        URL uRLFromID = null;
        Enumeration enumerationElements = this.maps.elements();
        while (enumerationElements.hasMoreElements()) {
            uRLFromID = ((Map) enumerationElements.nextElement()).getURLFromID(id);
            if (uRLFromID != null) {
                return uRLFromID;
            }
        }
        return uRLFromID;
    }

    @Override // javax.help.Map
    public boolean isID(URL url) {
        Enumeration enumerationElements = this.maps.elements();
        while (enumerationElements.hasMoreElements()) {
            if (((Map) enumerationElements.nextElement()).isID(url)) {
                return true;
            }
        }
        return false;
    }

    @Override // javax.help.Map
    public Map.ID getIDFromURL(URL url) {
        debug(new StringBuffer().append("getIDFromURL(").append(url).append(")").toString());
        Enumeration enumerationElements = this.maps.elements();
        while (enumerationElements.hasMoreElements()) {
            Map.ID iDFromURL = ((Map) enumerationElements.nextElement()).getIDFromURL(url);
            if (iDFromURL != null) {
                return iDFromURL;
            }
        }
        return null;
    }

    @Override // javax.help.Map
    public Map.ID getClosestID(URL url) {
        String ref;
        Map.ID iDFromURL = getIDFromURL(url);
        if (iDFromURL != null) {
            return iDFromURL;
        }
        if ((iDFromURL != null || url != null) && (ref = url.getRef()) != null) {
            String externalForm = url.toExternalForm();
            try {
                URL url2 = new URL(externalForm.substring(0, externalForm.lastIndexOf(ref) - 1));
                Enumeration enumerationElements = this.maps.elements();
                while (enumerationElements.hasMoreElements()) {
                    Map.ID iDFromURL2 = ((Map) enumerationElements.nextElement()).getIDFromURL(url2);
                    if (iDFromURL2 != null) {
                        return iDFromURL2;
                    }
                }
                return null;
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    @Override // javax.help.Map
    public Enumeration getIDs(URL url) {
        return new TryEnumeration(this.maps.elements(), url);
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/TryMap$TryEnumeration.class */
    private static class TryEnumeration implements Enumeration {
        private Enumeration e;
        private Enumeration k = null;
        private URL url;

        public TryEnumeration(Enumeration enumeration, URL url) {
            this.e = enumeration;
            this.url = url;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            while (true) {
                if (this.k == null || !this.k.hasMoreElements()) {
                    if (!this.e.hasMoreElements()) {
                        return false;
                    }
                    Map map = (Map) this.e.nextElement();
                    if (this.url == null) {
                        this.k = map.getAllIDs();
                    } else {
                        this.k = map.getIDs(this.url);
                    }
                } else {
                    return this.k.hasMoreElements();
                }
            }
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            return this.k.nextElement();
        }
    }

    private static void debug(String str) {
    }
}
