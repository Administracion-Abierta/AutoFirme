package org.ini4j;

import org.ini4j.spi.IniHandler;
import org.ini4j.spi.RegEscapeTool;
import org.ini4j.spi.TypeValuesPair;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicRegistry.class */
public class BasicRegistry extends BasicProfile implements Registry {
    private static final long serialVersionUID = -6432826330714504802L;
    private String _version = Registry.VERSION;

    @Override // org.ini4j.Registry
    public String getVersion() {
        return this._version;
    }

    @Override // org.ini4j.Registry
    public void setVersion(String value) {
        this._version = value;
    }

    @Override // org.ini4j.BasicProfile, org.ini4j.Profile
    public Registry.Key add(String name) {
        return (Registry.Key) super.add(name);
    }

    @Override // org.ini4j.BasicMultiMap, java.util.Map
    public Registry.Key get(Object key) {
        return (Registry.Key) super.get(key);
    }

    @Override // org.ini4j.BasicMultiMap, org.ini4j.MultiMap
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public Profile.Section get2(Object key, int index) {
        return (Registry.Key) super.get2(key, index);
    }

    @Override // org.ini4j.BasicMultiMap, java.util.Map
    public Registry.Key put(String key, Profile.Section value) {
        return (Registry.Key) super.put(key, value);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.ini4j.BasicMultiMap, org.ini4j.MultiMap
    public Registry.Key put(String key, Profile.Section value, int index) {
        return (Registry.Key) super.put(key, value, index);
    }

    @Override // org.ini4j.BasicProfile, org.ini4j.Profile
    public Registry.Key remove(Profile.Section section) {
        return (Registry.Key) super.remove(section);
    }

    @Override // org.ini4j.CommonMultiMap, org.ini4j.BasicMultiMap, java.util.Map
    public Registry.Key remove(Object key) {
        return (Registry.Key) super.remove(key);
    }

    @Override // org.ini4j.CommonMultiMap, org.ini4j.BasicMultiMap, org.ini4j.MultiMap
    /* JADX INFO: renamed from: remove, reason: merged with bridge method [inline-methods] */
    public Profile.Section remove2(Object key, int index) {
        return (Registry.Key) super.remove2(key, index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.ini4j.BasicProfile
    public Registry.Key newSection(String name) {
        return new BasicRegistryKey(this, name);
    }

    @Override // org.ini4j.BasicProfile
    void store(IniHandler formatter, Profile.Section section, String option) {
        store(formatter, section.getComment(option));
        Registry.Type type = ((Registry.Key) section).getType(option, Registry.Type.REG_SZ);
        String rawName = option.equals(Registry.Key.DEFAULT_NAME) ? option : RegEscapeTool.getInstance().quote(option);
        String[] values = new String[section.length(option)];
        for (int i = 0; i < values.length; i++) {
            values[i] = section.get2(option, i);
        }
        String rawValue = RegEscapeTool.getInstance().encode(new TypeValuesPair(type, values));
        formatter.handleOption(rawName, rawValue);
    }
}
