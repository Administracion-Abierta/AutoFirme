package org.ini4j;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicRegistryKey.class */
class BasicRegistryKey extends BasicProfileSection implements Registry.Key {
    private static final long serialVersionUID = -1390060044244350928L;
    private static final String META_TYPE = "type";

    public BasicRegistryKey(BasicRegistry registry, String name) {
        super(registry, name);
    }

    @Override // org.ini4j.BasicProfileSection, org.ini4j.Profile.Section
    public Registry.Key getChild(String key) {
        return (Registry.Key) super.getChild(key);
    }

    @Override // org.ini4j.BasicProfileSection, org.ini4j.Profile.Section
    public Registry.Key getParent() {
        return (Registry.Key) super.getParent();
    }

    @Override // org.ini4j.Registry.Key
    public Registry.Type getType(Object key) {
        return (Registry.Type) getMeta(META_TYPE, key);
    }

    @Override // org.ini4j.Registry.Key
    public Registry.Type getType(Object key, Registry.Type defaultType) {
        Registry.Type type = getType(key);
        return type == null ? defaultType : type;
    }

    @Override // org.ini4j.BasicProfileSection, org.ini4j.Profile.Section
    public Registry.Key addChild(String key) {
        return (Registry.Key) super.addChild(key);
    }

    @Override // org.ini4j.BasicProfileSection, org.ini4j.Profile.Section
    public Registry.Key lookup(String... path) {
        return (Registry.Key) super.lookup(path);
    }

    @Override // org.ini4j.Registry.Key
    public Registry.Type putType(String key, Registry.Type type) {
        return (Registry.Type) putMeta(META_TYPE, key, type);
    }

    @Override // org.ini4j.Registry.Key
    public Registry.Type removeType(Object key) {
        return (Registry.Type) removeMeta(META_TYPE, key);
    }
}
