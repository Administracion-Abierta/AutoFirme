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
import org.ini4j.spi.OptionsBuilder;
import org.ini4j.spi.OptionsFormatter;
import org.ini4j.spi.OptionsHandler;
import org.ini4j.spi.OptionsParser;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Options.class */
public class Options extends BasicOptionMap implements Persistable, Configurable {
    private static final long serialVersionUID = -1119753444859181822L;
    private String _comment;
    private Config _config;
    private File _file;

    public Options() {
        this._config = Config.getGlobal().m2267clone();
        this._config.setEmptyOption(true);
    }

    public Options(Reader input) throws IOException {
        this();
        load(input);
    }

    public Options(InputStream input) throws IOException {
        this();
        load(input);
    }

    public Options(URL input) throws IOException {
        this();
        load(input);
    }

    public Options(File input) throws IOException {
        this();
        this._file = input;
        load();
    }

    public String getComment() {
        return this._comment;
    }

    public void setComment(String value) {
        this._comment = value;
    }

    @Override // org.ini4j.Configurable
    public Config getConfig() {
        return this._config;
    }

    @Override // org.ini4j.Configurable
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
        OptionsParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override // org.ini4j.Persistable
    public void load(URL input) throws IOException {
        OptionsParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override // org.ini4j.Persistable
    public void load(File input) throws IOException {
        load(input.toURI().toURL());
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
        store(OptionsFormatter.newInstance(output, getConfig()));
    }

    @Override // org.ini4j.Persistable
    public void store(File output) throws IOException {
        OutputStream stream = new FileOutputStream(output);
        store(stream);
        stream.close();
    }

    protected OptionsHandler newBuilder() {
        return OptionsBuilder.newInstance(this);
    }

    protected void store(OptionsHandler formatter) throws IOException {
        formatter.startOptions();
        storeComment(formatter, this._comment);
        for (String name : keySet()) {
            storeComment(formatter, getComment(name));
            int n = getConfig().isMultiOption() ? length(name) : 1;
            for (int i = 0; i < n; i++) {
                String value = get(name, i);
                formatter.handleOption(name, value);
            }
        }
        formatter.endOptions();
    }

    @Override // org.ini4j.BasicOptionMap
    boolean isPropertyFirstUpper() {
        return getConfig().isPropertyFirstUpper();
    }

    private void storeComment(OptionsHandler formatter, String comment) {
        formatter.handleComment(comment);
    }
}
