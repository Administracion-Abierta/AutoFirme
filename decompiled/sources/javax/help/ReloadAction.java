package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/ReloadAction.class */
public class ReloadAction extends AbstractHelpAction implements ActionListener {
    private static final String NAME = "ReloadAction";

    public ReloadAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        putValue("icon", UIManager.getIcon("ReloadAction.icon"));
        if (obj instanceof JHelp) {
            try {
                locale = ((JHelp) obj).getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.ReloadAction"));
            putValue("access", HelpUtilities.getString(locale, "access.ReloadAction"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        ((JHelp) getControl()).getContentViewer().reload();
    }
}
