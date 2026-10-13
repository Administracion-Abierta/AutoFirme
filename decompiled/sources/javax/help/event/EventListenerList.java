package javax.help.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/event/EventListenerList.class */
public class EventListenerList implements Serializable {
    private static final Object[] NULL_ARRAY = new Object[0];
    protected transient Object[] listenerList = NULL_ARRAY;

    public Object[] getListenerList() {
        return this.listenerList;
    }

    public int getListenerCount() {
        return this.listenerList.length / 2;
    }

    public int getListenerCount(Class cls) {
        int i = 0;
        Object[] objArr = this.listenerList;
        for (int i2 = 0; i2 < objArr.length; i2 += 2) {
            if (cls == ((Class) objArr[i2])) {
                i++;
            }
        }
        return i;
    }

    public synchronized void add(Class cls, EventListener eventListener) {
        if (eventListener == null || cls == null) {
            throw new IllegalArgumentException(new StringBuffer().append("Listener ").append(eventListener).append(" is null").toString());
        }
        if (!cls.isInstance(eventListener)) {
            throw new IllegalArgumentException(new StringBuffer().append("Listener ").append(eventListener).append(" is not of type ").append(cls).toString());
        }
        if (this.listenerList == NULL_ARRAY) {
            this.listenerList = new Object[]{cls, eventListener};
            return;
        }
        int length = this.listenerList.length;
        Object[] objArr = new Object[length + 2];
        System.arraycopy(this.listenerList, 0, objArr, 0, length);
        objArr[length] = cls;
        objArr[length + 1] = eventListener;
        this.listenerList = objArr;
    }

    public synchronized void remove(Class cls, EventListener eventListener) {
        if (eventListener == null || cls == null) {
            throw new IllegalArgumentException(new StringBuffer().append("Listener ").append(eventListener).append(" is null").toString());
        }
        if (!cls.isInstance(eventListener)) {
            throw new IllegalArgumentException(new StringBuffer().append("Listener ").append(eventListener).append(" is not of type ").append(cls).toString());
        }
        int i = -1;
        for (int length = this.listenerList.length - 2; length >= 0; length -= 2) {
            if (this.listenerList[length] == cls && this.listenerList[length + 1] == eventListener) {
                i = length;
                break;
            }
        }
        if (i != -1) {
            Object[] objArr = new Object[this.listenerList.length - 2];
            System.arraycopy(this.listenerList, 0, objArr, 0, i);
            if (i < objArr.length) {
                System.arraycopy(this.listenerList, i + 2, objArr, i, objArr.length - i);
            }
            this.listenerList = objArr.length == 0 ? NULL_ARRAY : objArr;
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        Object[] objArr = this.listenerList;
        objectOutputStream.defaultWriteObject();
        for (int i = 0; i < objArr.length; i += 2) {
            Class cls = (Class) objArr[i];
            EventListener eventListener = (EventListener) objArr[i + 1];
            if (eventListener != null && (eventListener instanceof Serializable)) {
                objectOutputStream.writeObject(cls.getName());
                objectOutputStream.writeObject(eventListener);
            }
        }
        objectOutputStream.writeObject(null);
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        this.listenerList = NULL_ARRAY;
        objectInputStream.defaultReadObject();
        while (true) {
            Object object = objectInputStream.readObject();
            if (null != object) {
                add(Class.forName((String) object), (EventListener) objectInputStream.readObject());
            } else {
                return;
            }
        }
    }

    public String toString() {
        Object[] objArr = this.listenerList;
        String string = new StringBuffer().append("EventListenerList: ").append(objArr.length / 2).append(" listeners: ").toString();
        for (int i = 0; i <= objArr.length - 2; i += 2) {
            string = new StringBuffer().append(new StringBuffer().append(string).append(" type ").append(((Class) objArr[i]).getName()).toString()).append(" listener ").append(objArr[i + 1]).toString();
        }
        return string;
    }
}
