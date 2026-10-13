package org.ini4j.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URL;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/IniSource.class */
class IniSource {
    public static final char INCLUDE_BEGIN = '<';
    public static final char INCLUDE_END = '>';
    public static final char INCLUDE_OPTIONAL = '?';
    private static final char ESCAPE_CHAR = '\\';
    private URL _base;
    private IniSource _chain;
    private final String _commentChars;
    private final Config _config;
    private final HandlerBase _handler;
    private final LineNumberReader _reader;

    IniSource(InputStream input, HandlerBase handler, String comments, Config config) {
        this(new UnicodeInputStreamReader(input, config.getFileEncoding()), handler, comments, config);
    }

    IniSource(Reader input, HandlerBase handler, String comments, Config config) {
        this._reader = new LineNumberReader(input);
        this._handler = handler;
        this._commentChars = comments;
        this._config = config;
    }

    IniSource(URL input, HandlerBase handler, String comments, Config config) throws IOException {
        this(new UnicodeInputStreamReader(input.openStream(), config.getFileEncoding()), handler, comments, config);
        this._base = input;
    }

    int getLineNumber() {
        int ret;
        if (this._chain == null) {
            ret = this._reader.getLineNumber();
        } else {
            ret = this._chain.getLineNumber();
        }
        return ret;
    }

    String readLine() throws IOException {
        String line;
        if (this._chain == null) {
            line = readLineLocal();
        } else {
            line = this._chain.readLine();
            if (line == null) {
                this._chain = null;
                line = readLine();
            }
        }
        return line;
    }

    private void close() throws IOException {
        this._reader.close();
    }

    private int countEndingEscapes(String line) {
        int escapeCount = 0;
        for (int i = line.length() - 1; i >= 0 && line.charAt(i) == '\\'; i--) {
            escapeCount++;
        }
        return escapeCount;
    }

    private void handleComment(StringBuilder buff) {
        if (buff.length() != 0) {
            buff.deleteCharAt(buff.length() - 1);
            this._handler.handleComment(buff.toString());
            buff.delete(0, buff.length());
        }
    }

    private String handleInclude(String input) throws IOException {
        String line = input;
        if (this._config.isInclude() && line.length() > 2 && line.charAt(0) == '<' && line.charAt(line.length() - 1) == '>') {
            String line2 = line.substring(1, line.length() - 1).trim();
            boolean optional = line2.charAt(0) == '?';
            if (optional) {
                line2 = line2.substring(1).trim();
            }
            URL loc = this._base == null ? new URL(line2) : new URL(this._base, line2);
            if (optional) {
                try {
                    this._chain = new IniSource(loc, this._handler, this._commentChars, this._config);
                    line = readLine();
                } catch (IOException e) {
                    line = readLine();
                } catch (Throwable th) {
                    readLine();
                    throw th;
                }
            } else {
                this._chain = new IniSource(loc, this._handler, this._commentChars, this._config);
                line = readLine();
            }
        }
        return line;
    }

    private String readLineLocal() throws IOException {
        String line = readLineSkipComments();
        if (line == null) {
            close();
        } else {
            line = handleInclude(line);
        }
        return line;
    }

    private String readLineSkipComments() throws IOException {
        String line;
        StringBuilder comment = new StringBuilder();
        StringBuilder buff = new StringBuilder();
        String line2 = this._reader.readLine();
        while (true) {
            line = line2;
            if (line == null) {
                break;
            }
            String line3 = line.trim();
            if (line3.length() == 0) {
                handleComment(comment);
            } else if (this._commentChars.indexOf(line3.charAt(0)) >= 0 && buff.length() == 0) {
                comment.append(line3.substring(1));
                comment.append(this._config.getLineSeparator());
            } else {
                handleComment(comment);
                if (!this._config.isEscapeNewline() || (countEndingEscapes(line3) & 1) == 0) {
                    buff.append(line3);
                    line = buff.toString();
                    break;
                }
                buff.append(line3.subSequence(0, line3.length() - 1));
            }
            line2 = this._reader.readLine();
        }
        if (line == null && comment.length() != 0) {
            handleComment(comment);
        }
        return line;
    }
}
