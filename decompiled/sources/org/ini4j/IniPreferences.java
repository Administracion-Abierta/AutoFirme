package org.ini4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/IniPreferences.class */
public class IniPreferences extends AbstractPreferences {
    private static final String[] EMPTY = new String[0];
    private final Ini _ini;

    public IniPreferences(Ini ini) {
        super(null, "");
        this._ini = ini;
    }

    public IniPreferences(Reader input) throws IOException {
        super(null, "");
        this._ini = new Ini(input);
    }

    public IniPreferences(InputStream input) throws IOException {
        super(null, "");
        this._ini = new Ini(input);
    }

    public IniPreferences(URL input) throws IOException {
        super(null, "");
        this._ini = new Ini(input);
    }

    protected Ini getIni() {
        return this._ini;
    }

    @Override // java.util.prefs.AbstractPreferences
    protected String getSpi(String key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.prefs.AbstractPreferences
    protected String[] childrenNamesSpi() throws BackingStoreException {
        List<String> names = new ArrayList<>();
        for (String name : this._ini.keySet()) {
            if (name.indexOf(this._ini.getPathSeparator()) < 0) {
                names.add(name);
            }
        }
        return (String[]) names.toArray(EMPTY);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.util.prefs.AbstractPreferences
    public SectionPreferences childSpi(String name) {
        Profile.Section sec = this._ini.get(name);
        boolean isNew = sec == null;
        if (isNew) {
            sec = this._ini.add(name);
        }
        return new SectionPreferences(this, sec, isNew);
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void flushSpi() throws BackingStoreException {
    }

    @Override // java.util.prefs.AbstractPreferences
    protected String[] keysSpi() throws BackingStoreException {
        return EMPTY;
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void putSpi(String key, String value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void removeNodeSpi() throws UnsupportedOperationException, BackingStoreException {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void removeSpi(String key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void syncSpi() throws BackingStoreException {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/IniPreferences$SectionPreferences.class */
    protected class SectionPreferences extends AbstractPreferences {
        private final Profile.Section _section;

        SectionPreferences(AbstractPreferences parent, Profile.Section section, boolean isNew) {
            super(parent, section.getSimpleName());
            this._section = section;
            this.newNode = isNew;
        }

        @Override // java.util.prefs.AbstractPreferences, java.util.prefs.Preferences
        public void flush() throws BackingStoreException {
            parent().flush();
        }

        @Override // java.util.prefs.AbstractPreferences, java.util.prefs.Preferences
        public void sync() throws BackingStoreException {
            parent().sync();
        }

        @Override // java.util.prefs.AbstractPreferences
        protected String getSpi(String key) {
            return this._section.fetch(key);
        }

        @Override // java.util.prefs.AbstractPreferences
        protected String[] childrenNamesSpi() throws BackingStoreException {
            return this._section.childrenNames();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.util.prefs.AbstractPreferences
        public SectionPreferences childSpi(String name) throws UnsupportedOperationException {
            Profile.Section child = this._section.getChild(name);
            boolean isNew = child == null;
            if (isNew) {
                child = this._section.addChild(name);
            }
            return IniPreferences.this.new SectionPreferences(this, child, isNew);
        }

        @Override // java.util.prefs.AbstractPreferences
        protected void flushSpi() throws BackingStoreException {
        }

        @Override // java.util.prefs.AbstractPreferences
        protected String[] keysSpi() throws BackingStoreException {
            return (String[]) this._section.keySet().toArray(IniPreferences.EMPTY);
        }

        @Override // java.util.prefs.AbstractPreferences
        protected void putSpi(String key, String value) {
            this._section.put((Object) key, (Object) value);
        }

        @Override // java.util.prefs.AbstractPreferences
        protected void removeNodeSpi() throws BackingStoreException {
            IniPreferences.this._ini.remove(this._section);
        }

        @Override // java.util.prefs.AbstractPreferences
        protected void removeSpi(String key) {
            this._section.remove(key);
        }

        @Override // java.util.prefs.AbstractPreferences
        protected void syncSpi() throws BackingStoreException {
        }
    }
}
