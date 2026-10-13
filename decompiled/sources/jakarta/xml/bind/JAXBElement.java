package jakarta.xml.bind;

import java.io.Serializable;
import javax.xml.namespace.QName;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXBElement.class */
public class JAXBElement<T> implements Serializable {
    protected final QName name;
    protected final Class<T> declaredType;
    protected final Class scope;
    protected T value;
    protected boolean nil;
    private static final long serialVersionUID = 1;

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXBElement$GlobalScope.class */
    public static final class GlobalScope {
    }

    public JAXBElement(QName name, Class<T> declaredType, Class scope, T value) {
        this.nil = false;
        if (declaredType == null || name == null) {
            throw new IllegalArgumentException();
        }
        this.declaredType = declaredType;
        this.scope = scope == null ? GlobalScope.class : scope;
        this.name = name;
        setValue(value);
    }

    public JAXBElement(QName name, Class<T> declaredType, T value) {
        this(name, declaredType, GlobalScope.class, value);
    }

    public Class<T> getDeclaredType() {
        return this.declaredType;
    }

    public QName getName() {
        return this.name;
    }

    public void setValue(T t) {
        this.value = t;
    }

    public T getValue() {
        return this.value;
    }

    public Class getScope() {
        return this.scope;
    }

    public boolean isNil() {
        return this.value == null || this.nil;
    }

    public void setNil(boolean value) {
        this.nil = value;
    }

    public boolean isGlobalScope() {
        return this.scope == GlobalScope.class;
    }

    public boolean isTypeSubstituted() {
        return (this.value == null || this.value.getClass() == this.declaredType) ? false : true;
    }
}
