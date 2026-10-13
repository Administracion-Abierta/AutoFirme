package javax.help;

import com.sun.java.help.impl.JHelpPrintHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/PrintSetupAction.class */
public class PrintSetupAction extends AbstractHelpAction implements PropertyChangeListener, ActionListener {
    private static final String NAME = "PrintSetupAction";
    private JHelpPrintHandler handler;

    public PrintSetupAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        this.handler = null;
        if (getControl() instanceof JHelp) {
            JHelp jHelp = (JHelp) obj;
            this.handler = JHelpPrintHandler.getJHelpPrintHandler(jHelp);
            this.handler.addPropertyChangeListener(this);
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.PrintSetupAction"));
            putValue("access", HelpUtilities.getString(locale, "access.PrintSetupAction"));
        }
        putValue("icon", UIManager.getIcon("PrintSetupAction.icon"));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (this.handler != null) {
            this.handler.printSetup();
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("enabled")) {
            setEnabled(((Boolean) propertyChangeEvent.getNewValue()).booleanValue());
        }
    }
}
