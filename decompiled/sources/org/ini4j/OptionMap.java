package org.ini4j;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/OptionMap.class */
public interface OptionMap extends MultiMap<String, String>, CommentedMap<String, String> {
    <T> T getAll(Object obj, Class<T> cls);

    void add(String str, Object obj);

    void add(String str, Object obj, int i);

    <T> T as(Class<T> cls);

    <T> T as(Class<T> cls, String str);

    String fetch(Object obj);

    String fetch(Object obj, String str);

    String fetch(Object obj, int i);

    <T> T fetch(Object obj, Class<T> cls);

    <T> T fetch(Object obj, Class<T> cls, T t);

    <T> T fetch(Object obj, int i, Class<T> cls);

    <T> T fetchAll(Object obj, Class<T> cls);

    void from(Object obj);

    void from(Object obj, String str);

    String get(Object obj, String str);

    <T> T get(Object obj, Class<T> cls);

    <T> T get(Object obj, Class<T> cls, T t);

    <T> T get(Object obj, int i, Class<T> cls);

    String put(String str, Object obj);

    String put(String str, Object obj, int i);

    void putAll(String str, Object obj);

    void to(Object obj);

    void to(Object obj, String str);
}
