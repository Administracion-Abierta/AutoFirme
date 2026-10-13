package org.glassfish.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.json.JsonException;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl.class */
public class JsonParserImpl implements JsonParser {
    private Context currentContext;
    private JsonParser.Event currentEvent;
    private final Stack stack;
    private final StateIterator stateIterator;
    private final JsonTokenizer tokenizer;

    public JsonParserImpl(Reader reader, BufferPool bufferPool) {
        this.currentContext = new NoneContext();
        this.stack = new Stack();
        this.tokenizer = new JsonTokenizer(reader, bufferPool);
        this.stateIterator = new StateIterator();
    }

    public JsonParserImpl(InputStream in, BufferPool bufferPool) {
        this.currentContext = new NoneContext();
        this.stack = new Stack();
        UnicodeDetectingInputStream uin = new UnicodeDetectingInputStream(in);
        this.tokenizer = new JsonTokenizer(new InputStreamReader(uin, uin.getCharset()), bufferPool);
        this.stateIterator = new StateIterator();
    }

    public JsonParserImpl(InputStream in, Charset encoding, BufferPool bufferPool) {
        this.currentContext = new NoneContext();
        this.stack = new Stack();
        this.tokenizer = new JsonTokenizer(new InputStreamReader(in, encoding), bufferPool);
        this.stateIterator = new StateIterator();
    }

    @Override // javax.json.stream.JsonParser
    public String getString() {
        if (this.currentEvent == JsonParser.Event.KEY_NAME || this.currentEvent == JsonParser.Event.VALUE_STRING || this.currentEvent == JsonParser.Event.VALUE_NUMBER) {
            return this.tokenizer.getValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.currentEvent));
    }

    @Override // javax.json.stream.JsonParser
    public boolean isIntegralNumber() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_ISINTEGRALNUMBER_ERR(this.currentEvent));
        }
        return this.tokenizer.isIntegral();
    }

    @Override // javax.json.stream.JsonParser
    public int getInt() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_GETINT_ERR(this.currentEvent));
        }
        return this.tokenizer.getInt();
    }

    boolean isDefinitelyInt() {
        return this.tokenizer.isDefinitelyInt();
    }

    @Override // javax.json.stream.JsonParser
    public long getLong() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_GETLONG_ERR(this.currentEvent));
        }
        return this.tokenizer.getBigDecimal().longValue();
    }

    @Override // javax.json.stream.JsonParser
    public BigDecimal getBigDecimal() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_GETBIGDECIMAL_ERR(this.currentEvent));
        }
        return this.tokenizer.getBigDecimal();
    }

    @Override // javax.json.stream.JsonParser
    public JsonLocation getLocation() {
        return this.tokenizer.getLocation();
    }

    public JsonLocation getLastCharLocation() {
        return this.tokenizer.getLastCharLocation();
    }

    @Override // javax.json.stream.JsonParser
    public boolean hasNext() {
        return this.stateIterator.hasNext();
    }

    @Override // javax.json.stream.JsonParser
    public JsonParser.Event next() {
        return this.stateIterator.next();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl$StateIterator.class */
    private class StateIterator implements Iterator<JsonParser.Event> {
        private StateIterator() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (JsonParserImpl.this.stack.isEmpty()) {
                if (JsonParserImpl.this.currentEvent == JsonParser.Event.END_ARRAY || JsonParserImpl.this.currentEvent == JsonParser.Event.END_OBJECT) {
                    JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
                    if (token != JsonTokenizer.JsonToken.EOF) {
                        throw new JsonParsingException(JsonMessages.PARSER_EXPECTED_EOF(token), JsonParserImpl.this.getLastCharLocation());
                    }
                    return false;
                }
                return true;
            }
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public JsonParser.Event next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return JsonParserImpl.this.currentEvent = JsonParserImpl.this.currentContext.getNextEvent();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override // javax.json.stream.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            this.tokenizer.close();
        } catch (IOException e) {
            throw new JsonException(JsonMessages.PARSER_TOKENIZER_CLOSE_IO(), e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl$Stack.class */
    private static final class Stack {
        private Context head;

        private Stack() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void push(Context context) {
            context.next = this.head;
            this.head = context;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Context pop() {
            if (this.head == null) {
                throw new NoSuchElementException();
            }
            Context temp = this.head;
            this.head = this.head.next;
            return temp;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEmpty() {
            return this.head == null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl$Context.class */
    private abstract class Context {
        Context next;

        abstract JsonParser.Event getNextEvent();

        private Context() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl$NoneContext.class */
    private final class NoneContext extends Context {
        private NoneContext() {
            super();
        }

        @Override // org.glassfish.json.JsonParserImpl.Context
        public JsonParser.Event getNextEvent() {
            JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
            if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ObjectContext();
                return JsonParser.Event.START_OBJECT;
            }
            if (token != JsonTokenizer.JsonToken.SQUAREOPEN) {
                throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN]");
            }
            JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
            JsonParserImpl.this.currentContext = new ArrayContext();
            return JsonParser.Event.START_ARRAY;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JsonParsingException parsingException(JsonTokenizer.JsonToken token, String expectedTokens) {
        JsonLocation location = getLastCharLocation();
        return new JsonParsingException(JsonMessages.PARSER_INVALID_TOKEN(token, location, expectedTokens), location);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl$ObjectContext.class */
    private final class ObjectContext extends Context {
        private boolean firstValue;

        private ObjectContext() {
            super();
            this.firstValue = true;
        }

        @Override // org.glassfish.json.JsonParserImpl.Context
        public JsonParser.Event getNextEvent() {
            JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
            if (JsonParserImpl.this.currentEvent == JsonParser.Event.KEY_NAME) {
                if (token != JsonTokenizer.JsonToken.COLON) {
                    throw JsonParserImpl.this.parsingException(token, "[COLON]");
                }
                JsonTokenizer.JsonToken token2 = JsonParserImpl.this.tokenizer.nextToken();
                if (token2.isValue()) {
                    return token2.getEvent();
                }
                if (token2 == JsonTokenizer.JsonToken.CURLYOPEN) {
                    JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                    JsonParserImpl.this.currentContext = JsonParserImpl.this.new ObjectContext();
                    return JsonParser.Event.START_OBJECT;
                }
                if (token2 != JsonTokenizer.JsonToken.SQUAREOPEN) {
                    throw JsonParserImpl.this.parsingException(token2, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
                }
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ArrayContext();
                return JsonParser.Event.START_ARRAY;
            }
            if (token == JsonTokenizer.JsonToken.CURLYCLOSE) {
                JsonParserImpl.this.currentContext = JsonParserImpl.this.stack.pop();
                return JsonParser.Event.END_OBJECT;
            }
            if (this.firstValue) {
                this.firstValue = false;
            } else {
                if (token != JsonTokenizer.JsonToken.COMMA) {
                    throw JsonParserImpl.this.parsingException(token, "[COMMA]");
                }
                token = JsonParserImpl.this.tokenizer.nextToken();
            }
            if (token != JsonTokenizer.JsonToken.STRING) {
                throw JsonParserImpl.this.parsingException(token, "[STRING]");
            }
            return JsonParser.Event.KEY_NAME;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonParserImpl$ArrayContext.class */
    private final class ArrayContext extends Context {
        private boolean firstValue;

        private ArrayContext() {
            super();
            this.firstValue = true;
        }

        @Override // org.glassfish.json.JsonParserImpl.Context
        public JsonParser.Event getNextEvent() {
            JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
            if (token == JsonTokenizer.JsonToken.SQUARECLOSE) {
                JsonParserImpl.this.currentContext = JsonParserImpl.this.stack.pop();
                return JsonParser.Event.END_ARRAY;
            }
            if (this.firstValue) {
                this.firstValue = false;
            } else {
                if (token != JsonTokenizer.JsonToken.COMMA) {
                    throw JsonParserImpl.this.parsingException(token, "[COMMA]");
                }
                token = JsonParserImpl.this.tokenizer.nextToken();
            }
            if (token.isValue()) {
                return token.getEvent();
            }
            if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ObjectContext();
                return JsonParser.Event.START_OBJECT;
            }
            if (token != JsonTokenizer.JsonToken.SQUAREOPEN) {
                throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
            }
            JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
            JsonParserImpl.this.currentContext = JsonParserImpl.this.new ArrayContext();
            return JsonParser.Event.START_ARRAY;
        }
    }
}
