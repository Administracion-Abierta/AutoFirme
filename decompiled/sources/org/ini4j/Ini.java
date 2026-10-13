package org.ini4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import org.ini4j.spi.IniBuilder;
import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.IniHandler;
import org.ini4j.spi.IniParser;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Ini.class */
public class Ini extends BasicProfile implements Persistable, Configurable {
    private static final long serialVersionUID = -6029486578113700585L;
    private Config _config;
    private File _file;

    public Ini() {
        this._config = Config.getGlobal();
    }

    public Ini(Reader input) throws IOException {
        this();
        load(input);
    }

    public Ini(InputStream input) throws IOException {
        this();
        load(input);
    }

    public Ini(URL input) throws IOException {
        this();
        load(input);
    }

    public Ini(File input) throws IOException {
        this();
        this._file = input;
        load();
    }

    @Override // org.ini4j.Configurable
    public Config getConfig() {
        return this._config;
    }

    public void setConfig(Config value) {
        this._config = value;
    }

    @Override // org.ini4j.Persistable
    public File getFile() {
        return this._file;
    }

    @Override // org.ini4j.Persistable
    public void setFile(File value) {
        this._file = value;
    }

    @Override // org.ini4j.Persistable
    public void load() throws IOException {
        if (this._file == null) {
            throw new FileNotFoundException();
        }
        load(this._file);
    }

    @Override // org.ini4j.Persistable
    public void load(InputStream input) throws IOException {
        load(new InputStreamReader(input, getConfig().getFileEncoding()));
    }

    @Override // org.ini4j.Persistable
    public void load(Reader input) throws IOException {
        IniParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override // org.ini4j.Persistable
    public void load(File input) throws IOException {
        load(input.toURI().toURL());
    }

    @Override // org.ini4j.Persistable
    public void load(URL input) throws IOException {
        IniParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override // org.ini4j.Persistable
    public void store() throws IOException {
        if (this._file == null) {
            throw new FileNotFoundException();
        }
        store(this._file);
    }

    @Override // org.ini4j.Persistable
    public void store(OutputStream output) throws IOException {
        store(new OutputStreamWriter(output, getConfig().getFileEncoding()));
    }

    @Override // org.ini4j.Persistable
    public void store(Writer output) throws IOException {
        store(IniFormatter.newInstance(output, getConfig()));
    }

    @Override // org.ini4j.Persistable
    public void store(File output) throws IOException {
        OutputStream stream = new FileOutputStream(output);
        store(stream);
        stream.close();
    }

    protected IniHandler newBuilder() {
        return IniBuilder.newInstance(this);
    }

    @Override // org.ini4j.BasicProfile
    protected void store(IniHandler formatter, Profile.Section section) {
        if (getConfig().isEmptySection() || section.size() != 0) {
            super.store(formatter, section);
        }
    }

    @Override // org.ini4j.BasicProfile
    protected void store(IniHandler formatter, Profile.Section section, String option, int index) {
        if (getConfig().isMultiOption() || index == section.length(option) - 1) {
            super.store(formatter, section, option, index);
        }
    }

    @Override // org.ini4j.BasicProfile
    boolean isTreeMode() {
        return getConfig().isTree();
    }

    @Override // org.ini4j.BasicProfile
    char getPathSeparator() {
        return getConfig().getPathSeparator();
    }

    @Override // org.ini4j.BasicProfile
    boolean isPropertyFirstUpper() {
        return getConfig().isPropertyFirstUpper();
    }
}
