package javax.help;

import com.sun.java.help.impl.JHelpPrintHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Locale;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/PrintAction.class */
public class PrintAction extends AbstractHelpAction implements PropertyChangeListener, ActionListener {
    private static final String NAME = "PrintAction";
    private JHelpPrintHandler handler;

    public PrintAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        this.handler = null;
        if (obj instanceof JHelp) {
            JHelp jHelp = (JHelp) obj;
            this.handler = JHelpPrintHandler.getJHelpPrintHandler(jHelp);
            this.handler.addPropertyChangeListener(this);
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.PrintAction"));
            putValue("access", HelpUtilities.getString(locale, "access.PrintAction"));
        }
        putValue("icon", UIManager.getIcon("PrintAction.icon"));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (this.handler != null) {
            JHelp jHelp = (JHelp) getControl();
            URL[] urlArr = null;
            TreeItem[] selectedItems = jHelp.getSelectedItems();
            if (selectedItems != null) {
                urlArr = new URL[selectedItems.length];
                for (int i = 0; i < selectedItems.length; i++) {
                    urlArr[i] = selectedItems[i].getURL();
                }
            }
            if (urlArr != null && urlArr.length > 0) {
                this.handler.print(urlArr);
            } else {
                this.handler.print(jHelp.getModel().getCurrentURL());
            }
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("enabled")) {
            setEnabled(((Boolean) propertyChangeEvent.getNewValue()).booleanValue());
        }
    }
}
