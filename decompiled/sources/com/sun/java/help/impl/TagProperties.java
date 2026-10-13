package com.sun.java.help.impl;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/TagProperties.class */
public class TagProperties implements Cloneable {
    protected Hashtable hashtable;
    protected int initialSize;
    static int count1 = 0;
    static int count2 = 0;

    public TagProperties() {
        this(7);
    }

    public TagProperties(int i) {
        this.initialSize = i;
    }

    public String getProperty(String str) {
        return (String) get(str);
    }

    public String getProperty(String str, String str2) {
        String property = getProperty(str);
        return property == null ? str2 : property;
    }

    public Enumeration propertyNames() {
        Hashtable hashtable = new Hashtable(11);
        enumerate(hashtable);
        return hashtable.keys();
    }

    public void list(PrintStream printStream) {
        printStream.println("-- listing properties --");
        Hashtable hashtable = new Hashtable(11);
        enumerate(hashtable);
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            String string = (String) hashtable.get(str);
            if (string.length() > 40) {
                string = new StringBuffer().append(string.substring(0, 37)).append("...").toString();
            }
            printStream.println(new StringBuffer().append(str).append("=").append(string).toString());
        }
    }

    public void list(PrintWriter printWriter) {
        printWriter.println("-- listing properties --");
        Hashtable hashtable = new Hashtable(11);
        enumerate(hashtable);
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            String string = (String) hashtable.get(str);
            if (string.length() > 40) {
                string = new StringBuffer().append(string.substring(0, 37)).append("...").toString();
            }
            printWriter.println(new StringBuffer().append(str).append("=").append(string).toString());
        }
    }

    private synchronized void enumerate(Hashtable hashtable) {
        Enumeration enumerationKeys = keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            hashtable.put(str, get(str));
        }
    }

    public int size() {
        if (this.hashtable != null) {
            return this.hashtable.size();
        }
        return 0;
    }

    public boolean isEmpty() {
        if (this.hashtable != null) {
            return this.hashtable.isEmpty();
        }
        return true;
    }

    public synchronized Enumeration keys() {
        if (this.hashtable != null) {
            return this.hashtable.keys();
        }
        return new EmptyEnumerator(this);
    }

    public synchronized Enumeration elements() {
        if (this.hashtable != null) {
            return this.hashtable.elements();
        }
        return new EmptyEnumerator(this);
    }

    public synchronized boolean contains(Object obj) {
        if (this.hashtable != null) {
            return this.hashtable.contains(obj);
        }
        return false;
    }

    public synchronized boolean containsKey(Object obj) {
        if (this.hashtable != null) {
            return this.hashtable.containsKey(obj);
        }
        return false;
    }

    public synchronized Object get(Object obj) {
        if (this.hashtable != null) {
            return this.hashtable.get(obj);
        }
        return null;
    }

    public synchronized Object put(Object obj, Object obj2) {
        if (this.hashtable == null) {
            this.hashtable = new Hashtable(this.initialSize);
        }
        return this.hashtable.put(obj, obj2);
    }

    public synchronized Object remove(Object obj) {
        if (this.hashtable != null) {
            return this.hashtable.remove(obj);
        }
        return null;
    }

    public synchronized void clear() {
        if (this.hashtable != null) {
            this.hashtable.clear();
        }
    }

    protected void setHashtable(Hashtable hashtable) {
        this.hashtable = hashtable;
    }

    public Hashtable getHashtable() {
        return this.hashtable;
    }

    public synchronized Object clone() {
        try {
            TagProperties tagProperties = (TagProperties) super.clone();
            if (this.hashtable != null) {
                tagProperties.setHashtable((Hashtable) this.hashtable.clone());
            }
            return tagProperties;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public synchronized String toString() {
        if (this.hashtable != null) {
            return this.hashtable.toString();
        }
        return "{ }";
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/TagProperties$EmptyEnumerator.class */
    class EmptyEnumerator implements Enumeration {
        private final TagProperties this$0;

        EmptyEnumerator(TagProperties tagProperties) {
            this.this$0 = tagProperties;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            throw new NoSuchElementException("EmptyEnumerator");
        }
    }
}
