package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/COSDictionaryMap.class */
public class COSDictionaryMap<K, V> implements Map<K, V> {
    private final COSDictionary map;
    private final Map<K, V> actuals;

    public COSDictionaryMap(Map<K, V> actualsMap, COSDictionary dicMap) {
        this.actuals = actualsMap;
        this.map = dicMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.map.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.actuals.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.actuals.containsValue(value);
    }

    @Override // java.util.Map
    public V get(Object key) {
        return this.actuals.get(key);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public V put(K k, V value) {
        COSObjectable object = (COSObjectable) value;
        this.map.setItem(COSName.getPDFName((String) k), object.getCOSObject());
        return this.actuals.put(k, value);
    }

    @Override // java.util.Map
    public V remove(Object key) {
        this.map.removeItem(COSName.getPDFName((String) key));
        return this.actuals.remove(key);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> t) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // java.util.Map
    public void clear() {
        this.map.clear();
        this.actuals.clear();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this.actuals.keySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return this.actuals.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(this.actuals.entrySet());
    }

    @Override // java.util.Map
    public boolean equals(Object o) {
        boolean retval = false;
        if (o instanceof COSDictionaryMap) {
            COSDictionaryMap<K, V> other = (COSDictionaryMap) o;
            retval = other.map.equals(this.map);
        }
        return retval;
    }

    public String toString() {
        return this.actuals.toString();
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.map.hashCode();
    }

    public static COSDictionary convert(Map<String, ?> someMap) {
        COSDictionary dic = new COSDictionary();
        for (Map.Entry<String, ?> entry : someMap.entrySet()) {
            String name = entry.getKey();
            COSObjectable object = (COSObjectable) entry.getValue();
            dic.setItem(COSName.getPDFName(name), object.getCOSObject());
        }
        return dic;
    }

    public static COSDictionaryMap<String, Object> convertBasicTypesToMap(COSDictionary map) throws IOException {
        Object objValueOf;
        COSDictionaryMap<String, Object> retval = null;
        if (map != null) {
            Map<String, Object> actualMap = new HashMap<>();
            for (COSName key : map.keySet()) {
                COSBase cosObj = map.getDictionaryObject(key);
                if (cosObj instanceof COSString) {
                    objValueOf = ((COSString) cosObj).getString();
                } else if (cosObj instanceof COSInteger) {
                    objValueOf = Integer.valueOf(((COSInteger) cosObj).intValue());
                } else if (cosObj instanceof COSName) {
                    objValueOf = ((COSName) cosObj).getName();
                } else if (cosObj instanceof COSFloat) {
                    objValueOf = Float.valueOf(((COSFloat) cosObj).floatValue());
                } else if (cosObj instanceof COSBoolean) {
                    objValueOf = ((COSBoolean) cosObj).getValue() ? Boolean.TRUE : Boolean.FALSE;
                } else {
                    throw new IOException("Error:unknown type of object to convert:" + cosObj);
                }
                Object actualObject = objValueOf;
                actualMap.put(key.getName(), actualObject);
            }
            retval = new COSDictionaryMap<>(actualMap, map);
        }
        return retval;
    }
}
