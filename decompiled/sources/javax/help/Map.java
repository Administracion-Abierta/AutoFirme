package javax.help;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

/* JADX INFO: loaded from: autofirma.jar:javax/help/Map.class */
public interface Map {
    boolean isValidID(String str, HelpSet helpSet);

    Enumeration getAllIDs();

    URL getURLFromID(ID id) throws MalformedURLException;

    boolean isID(URL url);

    ID getIDFromURL(URL url);

    ID getClosestID(URL url);

    Enumeration getIDs(URL url);

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Map$ID.class */
    public static final class ID implements Serializable {
        public String id;
        public HelpSet hs;

        public static ID create(String str, HelpSet helpSet) throws BadIDException {
            if (helpSet == null || str == null) {
                return null;
            }
            Map combinedMap = helpSet.getCombinedMap();
            if (!combinedMap.isValidID(str, helpSet)) {
                throw new BadIDException(new StringBuffer().append("Not valid ID: ").append(str).toString(), combinedMap, str, helpSet);
            }
            return new ID(str, helpSet);
        }

        private ID(String str, HelpSet helpSet) throws BadIDException {
            this.id = str;
            this.hs = helpSet;
        }

        public HelpSet getHelpSet() {
            return this.hs;
        }

        public String getIDString() {
            return this.id;
        }

        public URL getURL() throws MalformedURLException {
            if (this.hs == null || this.id == null) {
                return null;
            }
            try {
                return this.hs.getCombinedMap().getURLFromID(this);
            } catch (MalformedURLException e) {
                throw e;
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof ID) {
                ID id = (ID) obj;
                return id.id.equals(this.id) && id.hs.equals(this.hs);
            }
            return false;
        }

        public String toString() {
            return new StringBuffer().append("ID: ").append(this.id).append(", ").append(this.hs).toString();
        }
    }
}
