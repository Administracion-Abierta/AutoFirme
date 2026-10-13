package javax.help;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:javax/help/AbstractHelpAction.class */
public abstract class AbstractHelpAction implements HelpAction {
    private boolean enabled = true;
    private Object control;
    private Hashtable table;
    private PropertyChangeSupport propertyChangeSupport;

    AbstractHelpAction(Object obj, String str) {
        this.control = obj;
        putValue("name", str);
    }

    @Override // javax.help.HelpAction
    public synchronized void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        this.propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    @Override // javax.help.HelpAction
    public synchronized void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        this.propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    protected void firePropertyChange(String str, Object obj, Object obj2) {
        if (this.propertyChangeSupport == null) {
            return;
        }
        this.propertyChangeSupport.firePropertyChange(str, obj, obj2);
    }

    @Override // javax.help.HelpAction
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override // javax.help.HelpAction
    public void setEnabled(boolean z) {
        boolean z2 = this.enabled;
        this.enabled = z;
        firePropertyChange("enabled", new Boolean(z2), new Boolean(z));
    }

    @Override // javax.help.HelpAction
    public Object getControl() {
        return this.control;
    }

    @Override // javax.help.HelpAction
    public Object getValue(String str) {
        if (this.table == null) {
            return null;
        }
        return this.table.get(str);
    }

    @Override // javax.help.HelpAction
    public void putValue(String str, Object obj) {
        Object objPut;
        if (this.table == null) {
            this.table = new Hashtable();
        }
        if (obj == null) {
            objPut = this.table.remove(str);
        } else {
            objPut = this.table.put(str, obj);
        }
        firePropertyChange(str, objPut, obj);
    }
}
