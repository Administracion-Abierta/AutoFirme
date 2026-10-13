package org.ini4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicProfileSection.class */
class BasicProfileSection extends BasicOptionMap implements Profile.Section {
    private static final long serialVersionUID = 985800697957194374L;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final char REGEXP_ESCAPE_CHAR = '\\';
    private final Pattern _childPattern;
    private final String _name;
    private final BasicProfile _profile;

    protected BasicProfileSection(BasicProfile profile, String name) {
        this._profile = profile;
        this._name = name;
        this._childPattern = newChildPattern(name);
    }

    @Override // org.ini4j.Profile.Section
    public Profile.Section getChild(String key) {
        return this._profile.get(childName(key));
    }

    @Override // org.ini4j.Profile.Section
    public String getName() {
        return this._name;
    }

    @Override // org.ini4j.Profile.Section
    public Profile.Section getParent() {
        Profile.Section ret = null;
        int idx = this._name.lastIndexOf(this._profile.getPathSeparator());
        if (idx >= 0) {
            String name = this._name.substring(0, idx);
            ret = this._profile.get(name);
        }
        return ret;
    }

    @Override // org.ini4j.Profile.Section
    public String getSimpleName() {
        int idx = this._name.lastIndexOf(this._profile.getPathSeparator());
        return idx < 0 ? this._name : this._name.substring(idx + 1);
    }

    @Override // org.ini4j.Profile.Section
    public Profile.Section addChild(String key) {
        String name = childName(key);
        return this._profile.add(name);
    }

    @Override // org.ini4j.Profile.Section
    public String[] childrenNames() {
        List<String> names = new ArrayList<>();
        for (String key : this._profile.keySet()) {
            if (this._childPattern.matcher(key).matches()) {
                names.add(key.substring(this._name.length() + 1));
            }
        }
        return (String[]) names.toArray(EMPTY_STRING_ARRAY);
    }

    @Override // org.ini4j.Profile.Section
    public Profile.Section lookup(String... parts) {
        StringBuilder buff = new StringBuilder();
        for (String part : parts) {
            if (buff.length() != 0) {
                buff.append(this._profile.getPathSeparator());
            }
            buff.append(part);
        }
        return this._profile.get(childName(buff.toString()));
    }

    @Override // org.ini4j.Profile.Section
    public void removeChild(String key) {
        String name = childName(key);
        this._profile.remove(name);
    }

    @Override // org.ini4j.BasicOptionMap
    boolean isPropertyFirstUpper() {
        return this._profile.isPropertyFirstUpper();
    }

    @Override // org.ini4j.BasicOptionMap
    void resolve(StringBuilder buffer) {
        this._profile.resolve(buffer, this);
    }

    private String childName(String key) {
        return this._name + this._profile.getPathSeparator() + key;
    }

    private Pattern newChildPattern(String name) {
        return Pattern.compile('^' + Pattern.quote(name) + '\\' + this._profile.getPathSeparator() + "[^\\" + this._profile.getPathSeparator() + "]+$");
    }
}
