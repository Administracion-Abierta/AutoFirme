package javax.help.plaf.basic;

import java.awt.Component;
import java.util.Locale;
import javax.help.FavoritesItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesCellRenderer.class */
public class BasicFavoritesCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree jTree, Object obj, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        Locale locale;
        String name = "";
        FavoritesItem favoritesItem = (FavoritesItem) ((DefaultMutableTreeNode) obj).getUserObject();
        if (favoritesItem != null) {
            name = favoritesItem.getName();
        }
        if (favoritesItem != null && (locale = favoritesItem.getLocale()) != null) {
            setLocale(locale);
        }
        setText(name);
        if (z) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }
        this.selected = z;
        if (z3) {
            setIcon(getDefaultLeafIcon());
        } else if (z2) {
            setIcon(getDefaultOpenIcon());
        } else {
            setIcon(getDefaultClosedIcon());
        }
        return this;
    }
}
