package org.ini4j.spi;

import org.ini4j.CommentedMap;
import org.ini4j.Config;
import org.ini4j.Profile;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/AbstractProfileBuilder.class */
abstract class AbstractProfileBuilder implements IniHandler {
    private Profile.Section _currentSection;
    private boolean _header;
    private String _lastComment;

    abstract Config getConfig();

    abstract Profile getProfile();

    AbstractProfileBuilder() {
    }

    @Override // org.ini4j.spi.IniHandler
    public void endIni() {
        if (this._lastComment != null && this._header) {
            setHeaderComment();
        }
    }

    @Override // org.ini4j.spi.IniHandler
    public void endSection() {
        this._currentSection = null;
    }

    @Override // org.ini4j.spi.IniHandler, org.ini4j.spi.HandlerBase
    public void handleComment(String comment) {
        if (this._lastComment != null && this._header) {
            this._header = false;
            setHeaderComment();
        }
        this._lastComment = comment;
    }

    @Override // org.ini4j.spi.IniHandler, org.ini4j.spi.HandlerBase
    public void handleOption(String name, String value) {
        this._header = false;
        if (getConfig().isMultiOption()) {
            this._currentSection.add(name, value);
        } else {
            this._currentSection.put((Object) name, (Object) value);
        }
        if (this._lastComment != null) {
            putComment(this._currentSection, name);
            this._lastComment = null;
        }
    }

    @Override // org.ini4j.spi.IniHandler
    public void startIni() {
        if (getConfig().isHeaderComment()) {
            this._header = true;
        }
    }

    @Override // org.ini4j.spi.IniHandler
    public void startSection(String sectionName) {
        if (getConfig().isMultiSection()) {
            this._currentSection = getProfile().add(sectionName);
        } else {
            Profile.Section s = (Profile.Section) getProfile().get(sectionName);
            this._currentSection = s == null ? getProfile().add(sectionName) : s;
        }
        if (this._lastComment != null) {
            if (this._header) {
                setHeaderComment();
            } else {
                putComment(getProfile(), sectionName);
            }
            this._lastComment = null;
        }
        this._header = false;
    }

    Profile.Section getCurrentSection() {
        return this._currentSection;
    }

    private void setHeaderComment() {
        if (getConfig().isComment()) {
            getProfile().setComment(this._lastComment);
        }
    }

    private void putComment(CommentedMap<String, ?> map, String key) {
        if (getConfig().isComment()) {
            map.putComment(key, this._lastComment);
        }
    }
}
