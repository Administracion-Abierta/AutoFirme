package org.ini4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.ini4j.spi.WinEscapeTool;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Wini.class */
public class Wini extends Ini {
    private static final long serialVersionUID = -2781377824232440728L;
    public static final char PATH_SEPARATOR = '\\';

    public Wini() {
        Config cfg = Config.getGlobal().m2267clone();
        cfg.setEscape(false);
        cfg.setEscapeNewline(false);
        cfg.setGlobalSection(true);
        cfg.setEmptyOption(true);
        cfg.setMultiOption(false);
        cfg.setPathSeparator('\\');
        setConfig(cfg);
    }

    public Wini(File input) throws IOException {
        this();
        setFile(input);
        load();
    }

    public Wini(URL input) throws IOException {
        this();
        load(input);
    }

    public Wini(InputStream input) throws IOException {
        this();
        load(input);
    }

    public Wini(Reader input) throws IOException {
        this();
        load(input);
    }

    public String escape(String value) {
        return WinEscapeTool.getInstance().escape(value);
    }

    public String unescape(String value) {
        return WinEscapeTool.getInstance().unescape(value);
    }
}
