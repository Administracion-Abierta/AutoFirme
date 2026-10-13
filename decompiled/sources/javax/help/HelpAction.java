package javax.help;

import java.beans.PropertyChangeListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpAction.class */
public interface HelpAction {
    boolean isEnabled();

    void setEnabled(boolean z);

    Object getControl();

    Object getValue(String str);

    void putValue(String str, Object obj);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
