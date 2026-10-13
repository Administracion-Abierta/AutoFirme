package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/FavoritesAction.class */
public class FavoritesAction extends AbstractHelpAction implements ActionListener {
    private static final String NAME = "FavoritesAction";
    private JHelpFavoritesNavigator favorites;
    private ActionListener favoritesActionListener;

    public FavoritesAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        this.favorites = null;
        this.favoritesActionListener = null;
        if (obj instanceof JHelp) {
            JHelp jHelp = (JHelp) obj;
            Enumeration helpNavigators = jHelp.getHelpNavigators();
            while (helpNavigators.hasMoreElements()) {
                JHelpNavigator jHelpNavigator = (JHelpNavigator) helpNavigators.nextElement();
                if (jHelpNavigator instanceof JHelpFavoritesNavigator) {
                    this.favorites = (JHelpFavoritesNavigator) jHelpNavigator;
                    this.favoritesActionListener = this.favorites.getAddAction();
                }
            }
            setEnabled(this.favoritesActionListener != null);
            putValue("icon", UIManager.getIcon("FavoritesAction.icon"));
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.FavoritesAction"));
            putValue("access", HelpUtilities.getString(locale, "access.FavoritesAction"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (this.favoritesActionListener != null) {
            this.favoritesActionListener.actionPerformed(actionEvent);
        }
    }
}
