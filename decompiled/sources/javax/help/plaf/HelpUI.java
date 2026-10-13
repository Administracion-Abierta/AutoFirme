package javax.help.plaf;

import javax.help.JHelpNavigator;
import javax.swing.plaf.ComponentUI;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/HelpUI.class */
public abstract class HelpUI extends ComponentUI {
    public abstract void addNavigator(JHelpNavigator jHelpNavigator);

    public abstract void removeNavigator(JHelpNavigator jHelpNavigator);

    public abstract void setCurrentNavigator(JHelpNavigator jHelpNavigator);

    public abstract JHelpNavigator getCurrentNavigator();
}
