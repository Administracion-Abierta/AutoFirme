package javax.help.plaf;

import javax.help.Map;
import javax.help.NavigatorView;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.ComponentUI;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/HelpNavigatorUI.class */
public abstract class HelpNavigatorUI extends ComponentUI {
    private Icon icon;

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public void merge(NavigatorView navigatorView) {
        throw new UnsupportedOperationException("merge is not supported");
    }

    public void remove(NavigatorView navigatorView) {
        throw new UnsupportedOperationException("remove is not supported");
    }

    public ImageIcon getImageIcon(NavigatorView navigatorView) {
        ImageIcon imageIcon = null;
        Map.ID imageID = navigatorView.getImageID();
        if (imageID != null) {
            try {
                imageIcon = new ImageIcon(navigatorView.getHelpSet().getCombinedMap().getURLFromID(imageID));
            } catch (Exception e) {
            }
        }
        return imageIcon;
    }

    public Action getAddAction() {
        throw new UnsupportedOperationException("getAddAction is not supported");
    }
}
