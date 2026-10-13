package org.ini4j;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Registry.class */
public interface Registry extends Profile {
    public static final char ESCAPE_CHAR = '\\';
    public static final Charset FILE_ENCODING = Charset.forName("UnicodeLittle");
    public static final char KEY_SEPARATOR = '\\';
    public static final String LINE_SEPARATOR = "\r\n";
    public static final char TYPE_SEPARATOR = ':';
    public static final String VERSION = "Windows Registry Editor Version 5.00";

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/Registry$Hive.class */
    public enum Hive {
        HKEY_CLASSES_ROOT,
        HKEY_CURRENT_CONFIG,
        HKEY_CURRENT_USER,
        HKEY_LOCAL_MACHINE,
        HKEY_USERS
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/Registry$Key.class */
    public interface Key extends Profile.Section {
        public static final String DEFAULT_NAME = "@";

        @Override // org.ini4j.Profile.Section
        Key getChild(String str);

        @Override // org.ini4j.Profile.Section
        Key getParent();

        Type getType(Object obj);

        Type getType(Object obj, Type type);

        @Override // org.ini4j.Profile.Section
        Key addChild(String str);

        @Override // org.ini4j.Profile.Section
        Key lookup(String... strArr);

        Type putType(String str, Type type);

        Type removeType(Object obj);
    }

    String getVersion();

    void setVersion(String str);

    @Override // java.util.Map
    Key get(Object obj);

    @Override // org.ini4j.MultiMap
    Profile.Section get(Object obj, int i);

    Key put(String str, Profile.Section section);

    Key put(String str, Profile.Section section, int i);

    @Override // java.util.Map
    Key remove(Object obj);

    @Override // org.ini4j.MultiMap
    Profile.Section remove(Object obj, int i);

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/Registry$Type.class */
    public enum Type {
        REG_NONE("hex(0)"),
        REG_SZ(""),
        REG_EXPAND_SZ("hex(2)"),
        REG_BINARY("hex"),
        REG_DWORD("dword"),
        REG_DWORD_BIG_ENDIAN("hex(5)"),
        REG_LINK("hex(6)"),
        REG_MULTI_SZ("hex(7)"),
        REG_RESOURCE_LIST("hex(8)"),
        REG_FULL_RESOURCE_DESCRIPTOR("hex(9)"),
        REG_RESOURCE_REQUIREMENTS_LIST("hex(a)"),
        REG_QWORD("hex(b)");

        private static final Map<String, Type> MAPPING = new HashMap();
        public static final char SEPARATOR_CHAR = ':';
        public static final String SEPARATOR;
        public static final char REMOVE_CHAR = '-';
        public static final String REMOVE;
        private final String _prefix;

        static {
            Type[] arr$ = values();
            for (Type t : arr$) {
                MAPPING.put(t.toString(), t);
            }
            SEPARATOR = String.valueOf(':');
            REMOVE = String.valueOf('-');
        }

        Type(String prefix) {
            this._prefix = prefix;
        }

        public static Type fromString(String str) {
            return MAPPING.get(str);
        }

        @Override // java.lang.Enum
        public String toString() {
            return this._prefix;
        }
    }
}
