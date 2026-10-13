package org.ini4j;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/CommonMultiMap.class */
public class CommonMultiMap<K, V> extends BasicMultiMap<K, V> implements CommentedMap<K, V> {
    private static final long serialVersionUID = 3012579878005541746L;
    private static final String SEPARATOR = ";#;";
    private static final String FIRST_CATEGORY = "";
    private static final String LAST_CATEGORY = "zzzzzzzzzzzzzzzzzzzzzz";
    private static final String META_COMMENT = "comment";
    private SortedMap<String, Object> _meta;

    @Override // org.ini4j.CommentedMap
    public String getComment(Object key) {
        return (String) getMeta("comment", key);
    }

    @Override // org.ini4j.BasicMultiMap, java.util.Map
    public void clear() {
        super.clear();
        if (this._meta != null) {
            this._meta.clear();
        }
    }

    @Override // org.ini4j.BasicMultiMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        Map<String, String> meta;
        super.putAll(map);
        if ((map instanceof CommonMultiMap) && (meta = ((CommonMultiMap) map)._meta) != null) {
            meta().putAll(meta);
        }
    }

    @Override // org.ini4j.CommentedMap
    public String putComment(K key, String comment) {
        return (String) putMeta("comment", key, comment);
    }

    @Override // org.ini4j.BasicMultiMap, java.util.Map
    public V remove(Object obj) {
        V v = (V) super.remove(obj);
        removeMeta(obj);
        return v;
    }

    @Override // org.ini4j.BasicMultiMap, org.ini4j.MultiMap
    public V remove(Object obj, int i) {
        V v = (V) super.remove(obj, i);
        if (length(obj) == 0) {
            removeMeta(obj);
        }
        return v;
    }

    @Override // org.ini4j.CommentedMap
    public String removeComment(Object key) {
        return (String) removeMeta("comment", key);
    }

    Object getMeta(String category, Object key) {
        if (this._meta == null) {
            return null;
        }
        return this._meta.get(makeKey(category, key));
    }

    Object putMeta(String category, K key, Object value) {
        return meta().put(makeKey(category, key), value);
    }

    void removeMeta(Object key) {
        if (this._meta != null) {
            this._meta.subMap(makeKey("", key), makeKey(LAST_CATEGORY, key)).clear();
        }
    }

    Object removeMeta(String category, Object key) {
        if (this._meta == null) {
            return null;
        }
        return this._meta.remove(makeKey(category, key));
    }

    private String makeKey(String category, Object key) {
        return String.valueOf(key) + SEPARATOR + category;
    }

    private Map<String, Object> meta() {
        if (this._meta == null) {
            this._meta = new TreeMap();
        }
        return this._meta;
    }
}
