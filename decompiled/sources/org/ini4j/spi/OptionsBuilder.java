package org.ini4j.spi;

import org.ini4j.Config;
import org.ini4j.Options;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/OptionsBuilder.class */
public class OptionsBuilder implements OptionsHandler {
    private boolean _header;
    private String _lastComment;
    private Options _options;

    public static OptionsBuilder newInstance(Options opts) {
        OptionsBuilder instance = newInstance();
        instance.setOptions(opts);
        return instance;
    }

    public void setOptions(Options value) {
        this._options = value;
    }

    @Override // org.ini4j.spi.OptionsHandler
    public void endOptions() {
        if (this._lastComment != null && this._header) {
            setHeaderComment();
        }
    }

    @Override // org.ini4j.spi.OptionsHandler, org.ini4j.spi.HandlerBase
    public void handleComment(String comment) {
        if (this._lastComment != null && this._header) {
            setHeaderComment();
            this._header = false;
        }
        this._lastComment = comment;
    }

    @Override // org.ini4j.spi.OptionsHandler, org.ini4j.spi.HandlerBase
    public void handleOption(String name, String value) {
        if (getConfig().isMultiOption()) {
            this._options.add(name, value);
        } else {
            this._options.put(name, value);
        }
        if (this._lastComment != null) {
            if (this._header) {
                setHeaderComment();
            } else {
                putComment(name);
            }
            this._lastComment = null;
        }
        this._header = false;
    }

    @Override // org.ini4j.spi.OptionsHandler
    public void startOptions() {
        if (getConfig().isHeaderComment()) {
            this._header = true;
        }
    }

    protected static OptionsBuilder newInstance() {
        return (OptionsBuilder) ServiceFinder.findService(OptionsBuilder.class);
    }

    private Config getConfig() {
        return this._options.getConfig();
    }

    private void setHeaderComment() {
        if (getConfig().isComment()) {
            this._options.setComment(this._lastComment);
        }
    }

    private void putComment(String key) {
        if (getConfig().isComment()) {
            this._options.putComment(key, this._lastComment);
        }
    }
}
