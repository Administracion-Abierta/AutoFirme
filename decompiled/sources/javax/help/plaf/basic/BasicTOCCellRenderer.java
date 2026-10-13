package javax.help.plaf.basic;

import java.awt.Component;
import java.util.Locale;
import javax.help.Map;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicTOCCellRenderer.class */
public class BasicTOCCellRenderer extends DefaultTreeCellRenderer {
    protected Map map;
    protected TOCView view;

    public BasicTOCCellRenderer(Map map) {
        this(map, null);
    }

    public BasicTOCCellRenderer(Map map, TOCView tOCView) {
        this.map = map;
        this.view = tOCView;
    }

    public Component getTreeCellRendererComponent(JTree jTree, Object obj, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        Locale locale;
        Map.ID imageID;
        String name = "";
        try {
            this.hasFocus = z4;
        } catch (IllegalAccessError e) {
        }
        TOCItem tOCItem = (TOCItem) ((DefaultMutableTreeNode) obj).getUserObject();
        if (tOCItem != null) {
            name = tOCItem.getName();
        }
        setText(name);
        if (z) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }
        ImageIcon imageIcon = null;
        if (tOCItem != null && (imageID = tOCItem.getImageID()) != null) {
            try {
                imageIcon = new ImageIcon(this.map.getURLFromID(imageID));
            } catch (Exception e2) {
            }
        }
        if (tOCItem != null && (locale = tOCItem.getLocale()) != null) {
            setLocale(locale);
        }
        if (imageIcon != null) {
            setIcon(imageIcon);
        } else if (z3) {
            setIcon(getLeafIcon());
        } else if (z2) {
            setIcon(getOpenIcon());
        } else {
            setIcon(getClosedIcon());
        }
        this.selected = z;
        return this;
    }

    public Icon getLeafIcon() {
        Map.ID topicImageID;
        if (this.view != null && (topicImageID = this.view.getTopicImageID()) != null) {
            try {
                return new ImageIcon(this.map.getURLFromID(topicImageID));
            } catch (Exception e) {
            }
        }
        return super.getLeafIcon();
    }

    public Icon getOpenIcon() {
        Map.ID categoryOpenImageID;
        if (this.view != null && (categoryOpenImageID = this.view.getCategoryOpenImageID()) != null) {
            try {
                return new ImageIcon(this.map.getURLFromID(categoryOpenImageID));
            } catch (Exception e) {
            }
        }
        return super.getOpenIcon();
    }

    public Icon getClosedIcon() {
        Map.ID categoryClosedImageID;
        if (this.view != null && (categoryClosedImageID = this.view.getCategoryClosedImageID()) != null) {
            try {
                return new ImageIcon(this.map.getURLFromID(categoryClosedImageID));
            } catch (Exception e) {
            }
        }
        return super.getClosedIcon();
    }
}
