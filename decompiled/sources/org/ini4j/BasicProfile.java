package org.ini4j;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ini4j.spi.AbstractBeanInvocationHandler;
import org.ini4j.spi.BeanTool;
import org.ini4j.spi.IniHandler;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicProfile.class */
public class BasicProfile extends CommonMultiMap<String, Profile.Section> implements Profile {
    private static final String SECTION_SYSTEM_PROPERTIES = "@prop";
    private static final String SECTION_ENVIRONMENT = "@env";
    private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[\\}]+)(\\[([0-9]+)\\])?/)?([^\\[^/\\}]+)(\\[(([0-9]+))\\])?\\}");
    private static final int G_SECTION = 2;
    private static final int G_SECTION_IDX = 4;
    private static final int G_OPTION = 5;
    private static final int G_OPTION_IDX = 7;
    private static final long serialVersionUID = -1817521505004015256L;
    private String _comment;
    private final boolean _propertyFirstUpper;
    private final boolean _treeMode;

    public BasicProfile() {
        this(false, false);
    }

    public BasicProfile(boolean treeMode, boolean propertyFirstUpper) {
        this._treeMode = treeMode;
        this._propertyFirstUpper = propertyFirstUpper;
    }

    @Override // org.ini4j.Profile
    public String getComment() {
        return this._comment;
    }

    @Override // org.ini4j.Profile
    public void setComment(String value) {
        this._comment = value;
    }

    @Override // org.ini4j.Profile
    public Profile.Section add(String name) {
        int idx;
        if (isTreeMode() && (idx = name.lastIndexOf(getPathSeparator())) > 0) {
            String parent = name.substring(0, idx);
            if (!containsKey(parent)) {
                add(parent);
            }
        }
        Profile.Section section = newSection(name);
        add(name, section);
        return section;
    }

    @Override // org.ini4j.Profile
    public void add(String section, String option, Object value) {
        getOrAdd(section).add(option, value);
    }

    @Override // org.ini4j.Profile
    public <T> T as(Class<T> cls) {
        return (T) as(cls, null);
    }

    @Override // org.ini4j.Profile
    public <T> T as(Class<T> clazz, String prefix) {
        return clazz.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new BeanInvocationHandler(prefix)));
    }

    @Override // org.ini4j.Profile
    public String fetch(Object sectionName, Object optionName) {
        Profile.Section sec = get(sectionName);
        if (sec == null) {
            return null;
        }
        return sec.fetch(optionName);
    }

    @Override // org.ini4j.Profile
    public <T> T fetch(Object obj, Object obj2, Class<T> cls) {
        Profile.Section section = get(obj);
        return section == null ? (T) BeanTool.getInstance().zero(cls) : (T) section.fetch(obj2, cls);
    }

    @Override // org.ini4j.Profile
    public String get(Object sectionName, Object optionName) {
        Profile.Section sec = get(sectionName);
        if (sec == null) {
            return null;
        }
        return (String) sec.get(optionName);
    }

    @Override // org.ini4j.Profile
    public <T> T get(Object obj, Object obj2, Class<T> cls) {
        Profile.Section section = get(obj);
        return section == null ? (T) BeanTool.getInstance().zero(cls) : (T) section.get(obj2, cls);
    }

    @Override // org.ini4j.Profile
    public String put(String sectionName, String optionName, Object value) {
        return getOrAdd(sectionName).put(optionName, value);
    }

    @Override // org.ini4j.Profile
    public Profile.Section remove(Profile.Section section) {
        return remove(section.getName());
    }

    @Override // java.util.Map, org.ini4j.Profile
    public String remove(Object sectionName, Object optionName) {
        Profile.Section sec = get(sectionName);
        if (sec == null) {
            return null;
        }
        return (String) sec.remove(optionName);
    }

    boolean isTreeMode() {
        return this._treeMode;
    }

    char getPathSeparator() {
        return '/';
    }

    boolean isPropertyFirstUpper() {
        return this._propertyFirstUpper;
    }

    Profile.Section newSection(String name) {
        return new BasicProfileSection(this, name);
    }

    void resolve(StringBuilder buffer, Profile.Section owner) {
        Matcher m = EXPRESSION.matcher(buffer);
        while (m.find()) {
            String sectionName = m.group(2);
            String optionName = m.group(5);
            int optionIndex = parseOptionIndex(m);
            Profile.Section section = parseSection(m, owner);
            String value = null;
            if (SECTION_ENVIRONMENT.equals(sectionName)) {
                value = Config.getEnvironment(optionName);
            } else if (SECTION_SYSTEM_PROPERTIES.equals(sectionName)) {
                value = Config.getSystemProperty(optionName);
            } else if (section != null) {
                value = optionIndex == -1 ? section.fetch(optionName) : section.fetch(optionName, optionIndex);
            }
            if (value != null) {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    void store(IniHandler formatter) {
        formatter.startIni();
        store(formatter, getComment());
        for (Profile.Section s : values()) {
            store(formatter, s);
        }
        formatter.endIni();
    }

    void store(IniHandler formatter, Profile.Section s) {
        store(formatter, getComment(s.getName()));
        formatter.startSection(s.getName());
        for (String name : s.keySet()) {
            store(formatter, s, name);
        }
        formatter.endSection();
    }

    void store(IniHandler formatter, String comment) {
        formatter.handleComment(comment);
    }

    void store(IniHandler formatter, Profile.Section section, String option) {
        store(formatter, section.getComment(option));
        int n = section.length(option);
        for (int i = 0; i < n; i++) {
            store(formatter, section, option, i);
        }
    }

    void store(IniHandler formatter, Profile.Section section, String option, int index) {
        formatter.handleOption(option, section.get(option, index));
    }

    private Profile.Section getOrAdd(String sectionName) {
        Profile.Section section = get(sectionName);
        return section == null ? add(sectionName) : section;
    }

    private int parseOptionIndex(Matcher m) {
        if (m.group(7) == null) {
            return -1;
        }
        return Integer.parseInt(m.group(7));
    }

    private Profile.Section parseSection(Matcher m, Profile.Section owner) {
        String sectionName = m.group(2);
        int sectionIndex = parseSectionIndex(m);
        if (sectionName == null) {
            return owner;
        }
        return sectionIndex == -1 ? get(sectionName) : get(sectionName, sectionIndex);
    }

    private int parseSectionIndex(Matcher m) {
        if (m.group(4) == null) {
            return -1;
        }
        return Integer.parseInt(m.group(4));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicProfile$BeanInvocationHandler.class */
    private final class BeanInvocationHandler extends AbstractBeanInvocationHandler {
        private final String _prefix;

        private BeanInvocationHandler(String prefix) {
            this._prefix = prefix;
        }

        @Override // org.ini4j.spi.AbstractBeanInvocationHandler
        protected Object getPropertySpi(String property, Class<?> clazz) {
            String key = transform(property);
            Object o = null;
            if (BasicProfile.this.containsKey(key)) {
                if (clazz.isArray()) {
                    o = Array.newInstance(clazz.getComponentType(), BasicProfile.this.length(key));
                    for (int i = 0; i < BasicProfile.this.length(key); i++) {
                        Array.set(o, i, BasicProfile.this.get(key, i).as(clazz.getComponentType()));
                    }
                } else {
                    o = BasicProfile.this.get(key).as(clazz);
                }
            }
            return o;
        }

        @Override // org.ini4j.spi.AbstractBeanInvocationHandler
        protected void setPropertySpi(String property, Object value, Class<?> clazz) {
            String key = transform(property);
            BasicProfile.this.remove(key);
            if (value != null) {
                if (clazz.isArray()) {
                    for (int i = 0; i < Array.getLength(value); i++) {
                        Profile.Section sec = BasicProfile.this.add(key);
                        sec.from(Array.get(value, i));
                    }
                    return;
                }
                Profile.Section sec2 = BasicProfile.this.add(key);
                sec2.from(value);
            }
        }

        @Override // org.ini4j.spi.AbstractBeanInvocationHandler
        protected boolean hasPropertySpi(String property) {
            return BasicProfile.this.containsKey(transform(property));
        }

        String transform(String property) {
            String ret = this._prefix == null ? property : this._prefix + property;
            if (BasicProfile.this.isPropertyFirstUpper()) {
                ret = Character.toUpperCase(property.charAt(0)) + property.substring(1);
            }
            return ret;
        }
    }
}
