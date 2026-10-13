package org.ini4j.spi;

import java.io.PrintWriter;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/AbstractFormatter.class */
abstract class AbstractFormatter implements HandlerBase {
    private static final char OPERATOR = '=';
    private static final char COMMENT = '#';
    private static final char SPACE = ' ';
    private Config _config = Config.getGlobal();
    private boolean _header = true;
    private PrintWriter _output;

    AbstractFormatter() {
    }

    @Override // org.ini4j.spi.HandlerBase
    public void handleComment(String comment) {
        if (getConfig().isComment() && ((!this._header || getConfig().isHeaderComment()) && comment != null && comment.length() != 0)) {
            String[] arr$ = comment.split(getConfig().getLineSeparator());
            for (String line : arr$) {
                getOutput().print('#');
                getOutput().print(line);
                getOutput().print(getConfig().getLineSeparator());
            }
            if (this._header) {
                getOutput().print(getConfig().getLineSeparator());
            }
        }
        this._header = false;
    }

    @Override // org.ini4j.spi.HandlerBase
    public void handleOption(String optionName, String optionValue) {
        if (getConfig().isStrictOperator()) {
            if (getConfig().isEmptyOption() || optionValue != null) {
                getOutput().print(escapeKey(optionName));
                getOutput().print('=');
            }
            if (optionValue != null) {
                getOutput().print(escapeValue(optionValue));
            }
            if (getConfig().isEmptyOption() || optionValue != null) {
                getOutput().print(getConfig().getLineSeparator());
            }
        } else {
            String value = (optionValue == null && getConfig().isEmptyOption()) ? "" : optionValue;
            if (value != null) {
                getOutput().print(escapeKey(optionName));
                getOutput().print(' ');
                getOutput().print('=');
                getOutput().print(' ');
                getOutput().print(escapeValue(value));
                getOutput().print(getConfig().getLineSeparator());
            }
        }
        setHeader(false);
    }

    protected Config getConfig() {
        return this._config;
    }

    protected void setConfig(Config value) {
        this._config = value;
    }

    protected PrintWriter getOutput() {
        return this._output;
    }

    protected void setOutput(PrintWriter value) {
        this._output = value;
    }

    void setHeader(boolean value) {
        this._header = value;
    }

    String escapeKey(String input) {
        return getConfig().isEscape() ? EscapeTool.getInstance().escape(input) : input;
    }

    String escapeValue(String input) {
        return (!getConfig().isEscape() || getConfig().isEscapeKeyOnly()) ? input : EscapeTool.getInstance().escape(input);
    }
}
