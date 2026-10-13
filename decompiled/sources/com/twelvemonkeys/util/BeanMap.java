package com.twelvemonkeys.util;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/BeanMap.class */
public final class BeanMap extends AbstractMap<String, Object> implements Serializable, Cloneable {
    private final Object bean;
    private transient Set<PropertyDescriptor> descriptors;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/BeanMap$Wrapped.class */
    private interface Wrapped {
        Object run() throws IllegalAccessException, InvocationTargetException;
    }

    public BeanMap(Object obj) throws IntrospectionException {
        if (obj == null) {
            throw new IllegalArgumentException("bean == null");
        }
        this.bean = obj;
        this.descriptors = initDescriptors(obj);
    }

    private static Set<PropertyDescriptor> initDescriptors(Object obj) throws IntrospectionException {
        HashSet hashSet = new HashSet();
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
            if ((!"class".equals(propertyDescriptor.getName()) || propertyDescriptor.getPropertyType() != Class.class) && !(propertyDescriptor instanceof IndexedPropertyDescriptor)) {
                hashSet.add(propertyDescriptor);
            }
        }
        return Collections.unmodifiableSet(hashSet);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<String, Object>> entrySet() {
        return new BeanSet();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object get(Object obj) {
        return super.get(obj);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object put(String str, Object obj) {
        checkKey(str);
        for (Map.Entry<String, Object> entry : entrySet()) {
            if (entry.getKey().equals(str)) {
                return entry.setValue(obj);
            }
        }
        return null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object remove(Object obj) {
        return super.remove(checkKey(obj));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.descriptors.size();
    }

    private String checkKey(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("key == null");
        }
        String str = (String) obj;
        if (!containsKey(str)) {
            throw new IllegalArgumentException("Bad key: " + obj);
        }
        return str;
    }

    private Object readResolve() throws IntrospectionException {
        this.descriptors = initDescriptors(this.bean);
        return this;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/BeanMap$BeanSet.class */
    private class BeanSet extends AbstractSet<Map.Entry<String, Object>> {
        private BeanSet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<String, Object>> iterator() {
            return BeanMap.this.new BeanIterator(BeanMap.this.descriptors.iterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return BeanMap.this.descriptors.size();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/BeanMap$BeanIterator.class */
    private class BeanIterator implements Iterator<Map.Entry<String, Object>> {
        private final Iterator<PropertyDescriptor> mIterator;

        public BeanIterator(Iterator<PropertyDescriptor> it) {
            this.mIterator = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mIterator.hasNext();
        }

        @Override // java.util.Iterator
        /* JADX INFO: renamed from: next, reason: merged with bridge method [inline-methods] */
        public Map.Entry<String, Object> next2() {
            return BeanMap.this.new BeanEntry(this.mIterator.next());
        }

        @Override // java.util.Iterator
        public void remove() {
            this.mIterator.remove();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/BeanMap$BeanEntry.class */
    private class BeanEntry implements Map.Entry<String, Object> {
        private final PropertyDescriptor mDescriptor;

        public BeanEntry(PropertyDescriptor propertyDescriptor) {
            this.mDescriptor = propertyDescriptor;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public String getKey() {
            return this.mDescriptor.getName();
        }

        @Override // java.util.Map.Entry
        public Object getValue() {
            return BeanMap.unwrap(new Wrapped() { // from class: com.twelvemonkeys.util.BeanMap.BeanEntry.1
                @Override // com.twelvemonkeys.util.BeanMap.Wrapped
                public Object run() throws IllegalAccessException, InvocationTargetException {
                    Method readMethod = BeanEntry.this.mDescriptor.getReadMethod();
                    if (readMethod == null) {
                        throw new UnsupportedOperationException("No getter: " + BeanEntry.this.mDescriptor.getName());
                    }
                    return readMethod.invoke(BeanMap.this.bean, new Object[0]);
                }
            });
        }

        @Override // java.util.Map.Entry
        public Object setValue(final Object obj) {
            return BeanMap.unwrap(new Wrapped() { // from class: com.twelvemonkeys.util.BeanMap.BeanEntry.2
                @Override // com.twelvemonkeys.util.BeanMap.Wrapped
                public Object run() throws IllegalAccessException, InvocationTargetException {
                    Method writeMethod = BeanEntry.this.mDescriptor.getWriteMethod();
                    if (writeMethod == null) {
                        throw new UnsupportedOperationException("No write method for property: " + BeanEntry.this.mDescriptor.getName());
                    }
                    Object value = BeanEntry.this.getValue();
                    writeMethod.invoke(BeanMap.this.bean, obj);
                    return value;
                }
            });
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            String key = getKey();
            Object key2 = entry.getKey();
            if (key == key2 || (key != null && key.equals(key2))) {
                Object value = getValue();
                Object value2 = entry.getValue();
                if (value == value2) {
                    return true;
                }
                if (value != null && value.equals(value2)) {
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
        }

        public String toString() {
            return getKey() + "=" + getValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object unwrap(Wrapped wrapped) {
        try {
            return wrapped.run();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e2) {
            throw ((RuntimeException) e2.getCause());
        }
    }
}
