package javax.help;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:javax/help/FavoritesItem.class */
public class FavoritesItem extends TreeItem implements Transferable, Serializable {
    private boolean isFolder;
    public static final DataFlavor FAVORITES_FLAVOR;
    static DataFlavor[] flavors;
    private FavoritesItem parent;
    private Vector children;
    private String url;
    private String target;
    private String title;
    private boolean emptyInitState;
    private boolean visible;
    static Class class$javax$help$FavoritesItem;

    static {
        Class clsClass$;
        if (class$javax$help$FavoritesItem == null) {
            clsClass$ = class$("javax.help.FavoritesItem");
            class$javax$help$FavoritesItem = clsClass$;
        } else {
            clsClass$ = class$javax$help$FavoritesItem;
        }
        FAVORITES_FLAVOR = new DataFlavor(clsClass$, "Favorites Item");
        flavors = new DataFlavor[]{FAVORITES_FLAVOR};
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public FavoritesItem(String str) {
        this.isFolder = false;
        this.parent = null;
        this.children = new Vector();
        this.url = null;
        this.target = null;
        this.title = null;
        this.emptyInitState = true;
        this.visible = true;
        setName(str);
    }

    public FavoritesItem() {
        this(null);
    }

    public FavoritesItem(String str, String str2, String str3, String str4, Locale locale) {
        this(str);
        this.target = str2;
        this.url = str3;
        this.locale = locale;
        this.title = str4;
    }

    public void setVisible(boolean z) {
        this.visible = z;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public String getTarget() {
        return this.target;
    }

    public String getURLSpec() {
        return this.url;
    }

    @Override // javax.help.TreeItem
    public URL getURL() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String getHelpSetTitle() {
        return this.title;
    }

    public void setAsFolder() {
        this.isFolder = true;
    }

    public boolean allowsChildren() {
        return isFolder();
    }

    public boolean isLeaf() {
        return !isFolder();
    }

    public boolean isFolder() {
        return this.isFolder;
    }

    public void add(FavoritesItem favoritesItem) {
        favoritesItem.setParent(this);
        this.children.add(favoritesItem);
        this.emptyInitState = false;
        this.isFolder = true;
    }

    public boolean emptyInitState() {
        return this.emptyInitState;
    }

    public void remove(FavoritesItem favoritesItem) {
        favoritesItem.setParent(null);
        this.children.remove(favoritesItem);
    }

    public FavoritesItem getParent() {
        return this.parent;
    }

    public void setParent(FavoritesItem favoritesItem) {
        this.parent = favoritesItem;
    }

    public Vector getChildren() {
        return this.children;
    }

    public Object clone() {
        return new FavoritesItem(getName(), this.target, this.url, this.title, this.locale);
    }

    @Override // javax.help.TreeItem
    public String toString() {
        return getName();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.datatransfer.UnsupportedFlavorException */
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (dataFlavor.equals(FAVORITES_FLAVOR)) {
            return this;
        }
        throw new UnsupportedFlavorException(dataFlavor);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return dataFlavor.equals(FAVORITES_FLAVOR);
    }
}
