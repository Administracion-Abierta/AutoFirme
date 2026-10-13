package org.ini4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import org.apache.fontbox.ttf.OpenTypeScript;
import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.IniHandler;
import org.ini4j.spi.IniParser;
import org.ini4j.spi.RegBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Reg.class */
public class Reg extends BasicRegistry implements Registry, Persistable, Configurable {
    private static final long serialVersionUID = -1485602876922985912L;
    protected static final String DEFAULT_SUFFIX = ".reg";
    protected static final String TMP_PREFIX = "reg-";
    private static final int STDERR_BUFF_SIZE = 8192;
    private static final String PROP_OS_NAME = "os.name";
    private static final boolean WINDOWS = Config.getSystemProperty(PROP_OS_NAME, OpenTypeScript.UNKNOWN).startsWith("Windows");
    private static final char CR = '\r';
    private static final char LF = '\n';
    private Config _config;
    private File _file;

    public Reg() {
        Config cfg = Config.getGlobal().m2267clone();
        cfg.setEscape(false);
        cfg.setGlobalSection(false);
        cfg.setEmptyOption(true);
        cfg.setMultiOption(true);
        cfg.setStrictOperator(true);
        cfg.setEmptySection(true);
        cfg.setPathSeparator('\\');
        cfg.setFileEncoding(FILE_ENCODING);
        cfg.setLineSeparator("\r\n");
        this._config = cfg;
    }

    public Reg(String registryKey) throws IOException {
        this();
        read(registryKey);
    }

    public Reg(File input) throws IOException {
        this();
        this._file = input;
        load();
    }

    public Reg(URL input) throws IOException {
        this();
        load(input);
    }

    public Reg(InputStream input) throws IOException {
        this();
        load(input);
    }

    public Reg(Reader input) throws IOException {
        this();
        load(input);
    }

    public static boolean isWindows() {
        return WINDOWS;
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
    public void load(URL input) throws IOException {
        load(new InputStreamReader(input.openStream(), getConfig().getFileEncoding()));
    }

    @Override // org.ini4j.Persistable
    public void load(Reader input) throws IOException {
        int newline = 2;
        StringBuilder buff = new StringBuilder();
        int i = input.read();
        while (true) {
            int c = i;
            if (c == -1) {
                break;
            }
            if (c == 10) {
                newline--;
                if (newline == 0) {
                    break;
                }
            } else if (c != 13 && newline != 1) {
                buff.append((char) c);
            }
            i = input.read();
        }
        if (buff.length() == 0) {
            throw new InvalidFileFormatException("Missing version header");
        }
        if (!buff.toString().equals(getVersion())) {
            throw new InvalidFileFormatException("Unsupported version: " + buff.toString());
        }
        IniParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override // org.ini4j.Persistable
    public void load(File input) throws IOException {
        load(input.toURI().toURL());
    }

    public void read(String registryKey) throws IOException {
        File tmp = createTempFile();
        try {
            regExport(registryKey, tmp);
            load(tmp);
        } finally {
            tmp.delete();
        }
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
        output.write(getVersion());
        output.write(getConfig().getLineSeparator());
        output.write(getConfig().getLineSeparator());
        store(IniFormatter.newInstance(output, getConfig()));
    }

    @Override // org.ini4j.Persistable
    public void store(File output) throws IOException {
        OutputStream stream = new FileOutputStream(output);
        store(stream);
        stream.close();
    }

    public void write() throws IOException {
        File tmp = createTempFile();
        try {
            store(tmp);
            regImport(tmp);
        } finally {
            tmp.delete();
        }
    }

    protected IniHandler newBuilder() {
        return RegBuilder.newInstance(this);
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

    void exec(String[] args) throws IOException {
        Process proc = Runtime.getRuntime().exec(args);
        try {
            int status = proc.waitFor();
            if (status != 0) {
                Reader in = new InputStreamReader(proc.getErrorStream());
                char[] buff = new char[8192];
                int n = in.read(buff);
                in.close();
                throw new IOException(new String(buff, 0, n).trim());
            }
        } catch (InterruptedException x) {
            throw ((IOException) new InterruptedIOException().initCause(x));
        }
    }

    private File createTempFile() throws IOException {
        File ret = File.createTempFile(TMP_PREFIX, DEFAULT_SUFFIX);
        ret.deleteOnExit();
        return ret;
    }

    private void regExport(String registryKey, File file) throws IOException {
        requireWindows();
        exec(new String[]{"cmd", "/c", "reg", "export", registryKey, file.getAbsolutePath()});
    }

    private void regImport(File file) throws IOException {
        requireWindows();
        exec(new String[]{"cmd", "/c", "reg", "import", file.getAbsolutePath()});
    }

    private void requireWindows() {
        if (!WINDOWS) {
            throw new UnsupportedOperationException("Unsupported operating system or runtime environment");
        }
    }
}
