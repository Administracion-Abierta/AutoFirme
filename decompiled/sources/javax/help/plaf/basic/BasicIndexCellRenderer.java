package javax.help.plaf.basic;

import java.awt.Component;
import java.util.Locale;
import javax.help.IndexItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicIndexCellRenderer.class */
public class BasicIndexCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree jTree, Object obj, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        Locale locale;
        try {
            this.hasFocus = z4;
        } catch (IllegalAccessError e) {
        }
        IndexItem indexItem = (IndexItem) ((DefaultMutableTreeNode) obj).getUserObject();
        String name = "";
        if (indexItem != null) {
            name = indexItem.getName();
        }
        setText(name);
        if (z) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }
        setIcon(null);
        this.selected = z;
        if (indexItem != null && (locale = indexItem.getLocale()) != null) {
            setLocale(locale);
        }
        return this;
    }
}
