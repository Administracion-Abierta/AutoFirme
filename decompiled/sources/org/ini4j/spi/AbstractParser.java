package org.ini4j.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;
import org.ini4j.Config;
import org.ini4j.InvalidFileFormatException;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/AbstractParser.class */
abstract class AbstractParser {
    private final String _comments;
    private Config _config = Config.getGlobal();
    private final String _operators;

    protected AbstractParser(String operators, String comments) {
        this._operators = operators;
        this._comments = comments;
    }

    protected Config getConfig() {
        return this._config;
    }

    protected void setConfig(Config value) {
        this._config = value;
    }

    protected void parseError(String line, int lineNumber) throws InvalidFileFormatException {
        throw new InvalidFileFormatException("parse error (at line: " + lineNumber + "): " + line);
    }

    IniSource newIniSource(InputStream input, HandlerBase handler) {
        return new IniSource(input, handler, this._comments, getConfig());
    }

    IniSource newIniSource(Reader input, HandlerBase handler) {
        return new IniSource(input, handler, this._comments, getConfig());
    }

    IniSource newIniSource(URL input, HandlerBase handler) throws IOException {
        return new IniSource(input, handler, this._comments, getConfig());
    }

    void parseOptionLine(String line, HandlerBase handler, int lineNumber) throws InvalidFileFormatException {
        int idx = indexOfOperator(line);
        String name = null;
        String value = null;
        if (idx < 0) {
            if (getConfig().isEmptyOption()) {
                name = line;
            } else {
                parseError(line, lineNumber);
            }
        } else {
            name = unescapeKey(line.substring(0, idx)).trim();
            value = unescapeValue(line.substring(idx + 1)).trim();
        }
        if (name.length() == 0) {
            parseError(line, lineNumber);
        }
        if (getConfig().isLowerCaseOption()) {
            name = name.toLowerCase(Locale.getDefault());
        }
        handler.handleOption(name, value);
    }

    String unescapeKey(String line) {
        return getConfig().isEscape() ? EscapeTool.getInstance().unescape(line) : line;
    }

    String unescapeValue(String line) {
        return (!getConfig().isEscape() || getConfig().isEscapeKeyOnly()) ? line : EscapeTool.getInstance().unescape(line);
    }

    private int indexOfOperator(String line) {
        int idx = -1;
        char[] arr$ = this._operators.toCharArray();
        for (char c : arr$) {
            int iIndexOf = line.indexOf(c);
            while (true) {
                int index = iIndexOf;
                if (index < 0) {
                    break;
                }
                if (index >= 0 && ((index == 0 || line.charAt(index - 1) != '\\') && (idx == -1 || index < idx))) {
                    idx = index;
                    break;
                }
                iIndexOf = index == line.length() - 1 ? -1 : line.indexOf(c, index + 1);
            }
        }
        return idx;
    }
}
