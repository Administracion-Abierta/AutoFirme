package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HomeAction.class */
public class HomeAction extends AbstractHelpAction implements ActionListener {
    private static final String NAME = "HomeAction";

    public HomeAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        putValue("icon", UIManager.getIcon("HomeAction.icon"));
        if (obj instanceof JHelp) {
            try {
                locale = ((JHelp) obj).getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.HomeAction"));
            putValue("access", HelpUtilities.getString(locale, "access.HomeAction"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        try {
            JHelp jHelp = (JHelp) getControl();
            HelpSet helpSet = jHelp.getModel().getHelpSet();
            jHelp.setCurrentID(helpSet.getHomeID(), HelpUtilities.getString(helpSet.getLocale(), "history.homePage"), jHelp.getCurrentNavigator());
        } catch (Exception e) {
        }
    }
}
