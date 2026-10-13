package org.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/json/JSONTokener.class */
public class JSONTokener {
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private final Reader reader;
    private boolean usePrevious;
    private long characterPreviousLine;

    public JSONTokener(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = (char) 0;
        this.index = 0L;
        this.character = 1L;
        this.characterPreviousLine = 0L;
        this.line = 1L;
    }

    public JSONTokener(InputStream inputStream) {
        this(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
    }

    public JSONTokener(String s) {
        this(new StringReader(s));
    }

    public void back() throws JSONException {
        if (this.usePrevious || this.index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        decrementIndexes();
        this.usePrevious = true;
        this.eof = false;
    }

    private void decrementIndexes() {
        this.index--;
        if (this.previous == '\r' || this.previous == '\n') {
            this.line--;
            this.character = this.characterPreviousLine;
        } else if (this.character > 0) {
            this.character--;
        }
    }

    public static int dehexchar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - '7';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'W';
        }
        return -1;
    }

    public boolean end() {
        return this.eof && !this.usePrevious;
    }

    public boolean more() throws JSONException {
        if (this.usePrevious) {
            return true;
        }
        try {
            this.reader.mark(1);
            try {
                if (this.reader.read() <= 0) {
                    this.eof = true;
                    return false;
                }
                this.reader.reset();
                return true;
            } catch (IOException e) {
                throw new JSONException("Unable to read the next character from the stream", e);
            }
        } catch (IOException e2) {
            throw new JSONException("Unable to preserve stream position", e2);
        }
    }

    public char next() throws JSONException {
        int c;
        if (this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
                throw new JSONException(exception);
            }
        }
        if (c <= 0) {
            this.eof = true;
            return (char) 0;
        }
        incrementIndexes(c);
        this.previous = (char) c;
        return this.previous;
    }

    protected char getPrevious() {
        return this.previous;
    }

    private void incrementIndexes(int c) {
        if (c > 0) {
            this.index++;
            if (c == 13) {
                this.line++;
                this.characterPreviousLine = this.character;
                this.character = 0L;
            } else {
                if (c == 10) {
                    if (this.previous != '\r') {
                        this.line++;
                        this.characterPreviousLine = this.character;
                    }
                    this.character = 0L;
                    return;
                }
                this.character++;
            }
        }
    }

    public char next(char c) throws JSONException {
        char n = next();
        if (n != c) {
            if (n > 0) {
                throw syntaxError("Expected '" + c + "' and instead saw '" + n + OperatorName.SHOW_TEXT_LINE);
            }
            throw syntaxError("Expected '" + c + "' and instead saw ''");
        }
        return n;
    }

    public String next(int n) throws JSONException {
        if (n == 0) {
            return "";
        }
        char[] chars = new char[n];
        for (int pos = 0; pos < n; pos++) {
            chars[pos] = next();
            if (end()) {
                throw syntaxError("Substring bounds error");
            }
        }
        return new String(chars);
    }

    public char nextClean() throws JSONException {
        char c;
        do {
            c = next();
            if (c == 0) {
                break;
            }
        } while (c <= ' ');
        return c;
    }

    public String nextString(char quote) throws JSONException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = next();
            switch (c) {
                case 0:
                case '\n':
                case '\r':
                    throw syntaxError("Unterminated string");
                case '\\':
                    char c2 = next();
                    switch (c2) {
                        case '\"':
                        case '\'':
                        case '/':
                        case '\\':
                            sb.append(c2);
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            try {
                                sb.append((char) Integer.parseInt(next(4), 16));
                            } catch (NumberFormatException e) {
                                throw syntaxError("Illegal escape.", e);
                            }
                            break;
                        default:
                            throw syntaxError("Illegal escape.");
                    }
                    break;
                default:
                    if (c == quote) {
                        return sb.toString();
                    }
                    sb.append(c);
                    break;
                    break;
            }
        }
    }

    public String nextTo(char delimiter) throws JSONException {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = next();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                break;
            }
            sb.append(c);
        }
        if (c != 0) {
            back();
        }
        return sb.toString().trim();
    }

    public String nextTo(String delimiters) throws JSONException {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = next();
            if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
                break;
            }
            sb.append(c);
        }
        if (c != 0) {
            back();
        }
        return sb.toString().trim();
    }

    public Object nextValue() throws JSONException {
        char c = nextClean();
        switch (c) {
            case '[':
                back();
                try {
                    return new JSONArray(this);
                } catch (StackOverflowError e) {
                    throw new JSONException("JSON Array or Object depth too large to process.", e);
                }
            case '{':
                back();
                try {
                    return new JSONObject(this);
                } catch (StackOverflowError e2) {
                    throw new JSONException("JSON Array or Object depth too large to process.", e2);
                }
            default:
                return nextSimpleValue(c);
        }
    }

    Object nextSimpleValue(char c) {
        switch (c) {
            case '\"':
            case '\'':
                return nextString(c);
            default:
                StringBuilder sb = new StringBuilder();
                while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
                    sb.append(c);
                    c = next();
                }
                if (!this.eof) {
                    back();
                }
                String string = sb.toString().trim();
                if ("".equals(string)) {
                    throw syntaxError("Missing value");
                }
                return JSONObject.stringToValue(string);
        }
    }

    public char skipTo(char to) throws JSONException {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                c = next();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return (char) 0;
                }
            } while (c != to);
            this.reader.mark(1);
            back();
            return c;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
    }

    public JSONException syntaxError(String message) {
        return new JSONException(message + toString());
    }

    public JSONException syntaxError(String message, Throwable causedBy) {
        return new JSONException(message + toString(), causedBy);
    }

    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
    }

    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
