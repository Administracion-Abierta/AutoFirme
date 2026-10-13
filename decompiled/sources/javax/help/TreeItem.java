package javax.help;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/TreeItem.class */
public class TreeItem implements Serializable {
    public static final int DEFAULT_EXPANSION = -1;
    public static final int COLLAPSE = 0;
    public static final int EXPAND = 1;
    private String name;
    private Map.ID id;
    protected Locale locale;
    private String mergeType;
    private int expand;
    private String presentation;
    private String presentationName;
    private HelpSet hs;

    public TreeItem(Map.ID id, HelpSet helpSet, Locale locale) {
        this.expand = -1;
        this.id = id;
        this.hs = helpSet;
        this.locale = locale;
    }

    public TreeItem(Map.ID id, Locale locale) {
        this(id, null, locale);
    }

    public TreeItem(String str) {
        this(null, null, null);
        setName(str);
    }

    public TreeItem() {
        this(null, null);
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public void setID(Map.ID id) {
        this.id = id;
    }

    public Map.ID getID() {
        return this.id;
    }

    public URL getURL() {
        try {
            return this.id.getURL();
        } catch (Exception e) {
            return null;
        }
    }

    public void setHelpSet(HelpSet helpSet) {
        this.hs = helpSet;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setMergeType(String str) {
        this.mergeType = str;
    }

    public String getMergeType() {
        return this.mergeType;
    }

    public void setExpansionType(int i) {
        if (i < -1 || i > 1) {
            throw new IllegalArgumentException("Invalid expansion type");
        }
        this.expand = i;
    }

    public int getExpansionType() {
        return this.expand;
    }

    public void setPresentation(String str) {
        this.presentation = str;
    }

    public String getPresentation() {
        return this.presentation;
    }

    public void setPresentationName(String str) {
        this.presentationName = str;
    }

    public String getPresentationName() {
        return this.presentationName;
    }

    public String toString() {
        return new StringBuffer().append(this.id).append("(").append(this.name).append(")").toString();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
    }
}
