package org.ini4j;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ini4j.spi.BeanAccess;
import org.ini4j.spi.BeanTool;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicOptionMap.class */
public class BasicOptionMap extends CommonMultiMap<String, String> implements OptionMap {
    private static final char SUBST_CHAR = '$';
    private static final int G_OPTION = 2;
    private static final int G_INDEX = 4;
    private static final long serialVersionUID = 325469712293707584L;
    private BeanAccess _defaultBeanAccess;
    private final boolean _propertyFirstUpper;
    private static final String SYSTEM_PROPERTY_PREFIX = "@prop/";
    private static final int SYSTEM_PROPERTY_PREFIX_LEN = SYSTEM_PROPERTY_PREFIX.length();
    private static final String ENVIRONMENT_PREFIX = "@env/";
    private static final int ENVIRONMENT_PREFIX_LEN = ENVIRONMENT_PREFIX.length();
    private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[\\}]+)(\\[([0-9]+)\\])?)\\}");

    public BasicOptionMap() {
        this(false);
    }

    public BasicOptionMap(boolean propertyFirstUpper) {
        this._propertyFirstUpper = propertyFirstUpper;
    }

    @Override // org.ini4j.OptionMap
    public <T> T getAll(Object obj, Class<T> cls) {
        requireArray(cls);
        T t = (T) Array.newInstance(cls.getComponentType(), length(obj));
        for (int i = 0; i < length(obj); i++) {
            Array.set(t, i, BeanTool.getInstance().parse(get(obj, i), cls.getComponentType()));
        }
        return t;
    }

    @Override // org.ini4j.OptionMap
    public void add(String key, Object value) {
        super.add(key, (value == null || (value instanceof String)) ? (String) value : String.valueOf(value));
    }

    @Override // org.ini4j.OptionMap
    public void add(String key, Object value, int index) {
        super.add(key, (value == null || (value instanceof String)) ? (String) value : String.valueOf(value), index);
    }

    @Override // org.ini4j.OptionMap
    public <T> T as(Class<T> cls) {
        return (T) BeanTool.getInstance().proxy(cls, getDefaultBeanAccess());
    }

    @Override // org.ini4j.OptionMap
    public <T> T as(Class<T> cls, String str) {
        return (T) BeanTool.getInstance().proxy(cls, newBeanAccess(str));
    }

    @Override // org.ini4j.OptionMap
    public String fetch(Object key) {
        int len = length(key);
        if (len == 0) {
            return null;
        }
        return fetch(key, len - 1);
    }

    @Override // org.ini4j.OptionMap
    public String fetch(Object key, String defaultValue) {
        String str = get(key);
        return str == null ? defaultValue : str;
    }

    @Override // org.ini4j.OptionMap
    public String fetch(Object key, int index) {
        String value = get(key, index);
        if (value != null && value.indexOf(36) >= 0) {
            StringBuilder buffer = new StringBuilder(value);
            resolve(buffer);
            value = buffer.toString();
        }
        return value;
    }

    @Override // org.ini4j.OptionMap
    public <T> T fetch(Object obj, Class<T> cls) {
        return (T) BeanTool.getInstance().parse(fetch(obj), cls);
    }

    @Override // org.ini4j.OptionMap
    public <T> T fetch(Object obj, Class<T> cls, T t) {
        String strFetch = fetch(obj);
        return strFetch == null ? t : (T) BeanTool.getInstance().parse(strFetch, cls);
    }

    @Override // org.ini4j.OptionMap
    public <T> T fetch(Object obj, int i, Class<T> cls) {
        return (T) BeanTool.getInstance().parse(fetch(obj, i), cls);
    }

    @Override // org.ini4j.OptionMap
    public <T> T fetchAll(Object obj, Class<T> cls) {
        requireArray(cls);
        T t = (T) Array.newInstance(cls.getComponentType(), length(obj));
        for (int i = 0; i < length(obj); i++) {
            Array.set(t, i, BeanTool.getInstance().parse(fetch(obj, i), cls.getComponentType()));
        }
        return t;
    }

    @Override // org.ini4j.OptionMap
    public void from(Object bean) {
        BeanTool.getInstance().inject(getDefaultBeanAccess(), bean);
    }

    @Override // org.ini4j.OptionMap
    public void from(Object bean, String keyPrefix) {
        BeanTool.getInstance().inject(newBeanAccess(keyPrefix), bean);
    }

    @Override // org.ini4j.OptionMap
    public <T> T get(Object obj, Class<T> cls) {
        return (T) BeanTool.getInstance().parse(get(obj), cls);
    }

    @Override // org.ini4j.OptionMap
    public String get(Object key, String defaultValue) {
        String str = get(key);
        return str == null ? defaultValue : str;
    }

    @Override // org.ini4j.OptionMap
    public <T> T get(Object obj, Class<T> cls, T t) {
        String str = get(obj);
        return str == null ? t : (T) BeanTool.getInstance().parse(str, cls);
    }

    @Override // org.ini4j.OptionMap
    public <T> T get(Object obj, int i, Class<T> cls) {
        return (T) BeanTool.getInstance().parse(get(obj, i), cls);
    }

    @Override // org.ini4j.OptionMap
    public String put(String key, Object value) {
        return (String) super.put(key, (value == null || (value instanceof String)) ? (String) value : String.valueOf(value));
    }

    @Override // org.ini4j.OptionMap
    public String put(String key, Object value, int index) {
        return (String) super.put(key, (value == null || (value instanceof String)) ? (String) value : String.valueOf(value), index);
    }

    @Override // org.ini4j.OptionMap
    public void putAll(String key, Object value) {
        if (value != null) {
            requireArray(value.getClass());
        }
        remove(key);
        if (value != null) {
            int n = Array.getLength(value);
            for (int i = 0; i < n; i++) {
                add(key, Array.get(value, i));
            }
        }
    }

    @Override // org.ini4j.OptionMap
    public void to(Object bean) {
        BeanTool.getInstance().inject(bean, getDefaultBeanAccess());
    }

    @Override // org.ini4j.OptionMap
    public void to(Object bean, String keyPrefix) {
        BeanTool.getInstance().inject(bean, newBeanAccess(keyPrefix));
    }

    synchronized BeanAccess getDefaultBeanAccess() {
        if (this._defaultBeanAccess == null) {
            this._defaultBeanAccess = newBeanAccess();
        }
        return this._defaultBeanAccess;
    }

    boolean isPropertyFirstUpper() {
        return this._propertyFirstUpper;
    }

    BeanAccess newBeanAccess() {
        return new Access(this);
    }

    BeanAccess newBeanAccess(String propertyNamePrefix) {
        return new Access(propertyNamePrefix);
    }

    void resolve(StringBuilder buffer) {
        String value;
        Matcher m = EXPRESSION.matcher(buffer);
        while (m.find()) {
            String name = m.group(2);
            int index = m.group(4) == null ? -1 : Integer.parseInt(m.group(4));
            if (name.startsWith(ENVIRONMENT_PREFIX)) {
                value = Config.getEnvironment(name.substring(ENVIRONMENT_PREFIX_LEN));
            } else if (name.startsWith(SYSTEM_PROPERTY_PREFIX)) {
                value = Config.getSystemProperty(name.substring(SYSTEM_PROPERTY_PREFIX_LEN));
            } else {
                value = index == -1 ? fetch(name) : fetch(name, index);
            }
            if (value != null) {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    private void requireArray(Class clazz) {
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("Array required");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicOptionMap$Access.class */
    class Access implements BeanAccess {
        private final String _prefix;

        Access(BasicOptionMap basicOptionMap) {
            this(null);
        }

        Access(String prefix) {
            this._prefix = prefix;
        }

        @Override // org.ini4j.spi.BeanAccess
        public void propAdd(String propertyName, String value) {
            BasicOptionMap.this.add(transform(propertyName), value);
        }

        @Override // org.ini4j.spi.BeanAccess
        public String propDel(String propertyName) {
            return BasicOptionMap.this.remove(transform(propertyName));
        }

        @Override // org.ini4j.spi.BeanAccess
        public String propGet(String propertyName) {
            return BasicOptionMap.this.fetch(transform(propertyName));
        }

        @Override // org.ini4j.spi.BeanAccess
        public String propGet(String propertyName, int index) {
            return BasicOptionMap.this.fetch(transform(propertyName), index);
        }

        @Override // org.ini4j.spi.BeanAccess
        public int propLength(String propertyName) {
            return BasicOptionMap.this.length(transform(propertyName));
        }

        @Override // org.ini4j.spi.BeanAccess
        public String propSet(String propertyName, String value) {
            return BasicOptionMap.this.put(transform(propertyName), value);
        }

        @Override // org.ini4j.spi.BeanAccess
        public String propSet(String propertyName, String value, int index) {
            return BasicOptionMap.this.put(transform(propertyName), value, index);
        }

        private String transform(String orig) {
            String ret = orig;
            if ((this._prefix != null || BasicOptionMap.this.isPropertyFirstUpper()) && orig != null) {
                StringBuilder buff = new StringBuilder();
                if (this._prefix != null) {
                    buff.append(this._prefix);
                }
                if (BasicOptionMap.this.isPropertyFirstUpper()) {
                    buff.append(Character.toUpperCase(orig.charAt(0)));
                    buff.append(orig.substring(1));
                } else {
                    buff.append(orig);
                }
                ret = buff.toString();
            }
            return ret;
        }
    }
}
