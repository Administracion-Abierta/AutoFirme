package javax.help;

import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.help.event.HelpModelListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpModel.class */
public interface HelpModel {
    void setHelpSet(HelpSet helpSet);

    HelpSet getHelpSet();

    void setCurrentID(Map.ID id) throws InvalidHelpSetContextException;

    void setCurrentID(Map.ID id, String str, JHelpNavigator jHelpNavigator) throws InvalidHelpSetContextException;

    Map.ID getCurrentID();

    void setCurrentURL(URL url);

    void setCurrentURL(URL url, String str, JHelpNavigator jHelpNavigator);

    URL getCurrentURL();

    void addHelpModelListener(HelpModelListener helpModelListener);

    void removeHelpModelListener(HelpModelListener helpModelListener);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
