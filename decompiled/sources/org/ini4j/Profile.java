package org.ini4j;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Profile.class */
public interface Profile extends MultiMap<String, Section>, CommentedMap<String, Section> {
    public static final char PATH_SEPARATOR = '/';

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/Profile$Section.class */
    public interface Section extends OptionMap {
        Section getChild(String str);

        String getName();

        Section getParent();

        String getSimpleName();

        Section addChild(String str);

        String[] childrenNames();

        Section lookup(String... strArr);

        void removeChild(String str);
    }

    String getComment();

    void setComment(String str);

    Section add(String str);

    void add(String str, String str2, Object obj);

    <T> T as(Class<T> cls);

    <T> T as(Class<T> cls, String str);

    String fetch(Object obj, Object obj2);

    <T> T fetch(Object obj, Object obj2, Class<T> cls);

    String get(Object obj, Object obj2);

    <T> T get(Object obj, Object obj2, Class<T> cls);

    String put(String str, String str2, Object obj);

    Section remove(Section section);

    String remove(Object obj, Object obj2);
}
