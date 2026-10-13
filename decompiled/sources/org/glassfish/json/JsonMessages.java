package org.glassfish.json;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/JsonMessages.class */
final class JsonMessages {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.glassfish.json.messages");

    JsonMessages() {
    }

    static String TOKENIZER_UNEXPECTED_CHAR(int unexpected, JsonLocation location) {
        return localize("tokenizer.unexpected.char", Integer.valueOf(unexpected), location);
    }

    static String TOKENIZER_EXPECTED_CHAR(int unexpected, JsonLocation location, char expected) {
        return localize("tokenizer.expected.char", Integer.valueOf(unexpected), location, Character.valueOf(expected));
    }

    static String TOKENIZER_IO_ERR() {
        return localize("tokenizer.io.err", new Object[0]);
    }

    static String PARSER_GETSTRING_ERR(JsonParser.Event event) {
        return localize("parser.getString.err", event);
    }

    static String PARSER_ISINTEGRALNUMBER_ERR(JsonParser.Event event) {
        return localize("parser.isIntegralNumber.err", event);
    }

    static String PARSER_GETINT_ERR(JsonParser.Event event) {
        return localize("parser.getInt.err", event);
    }

    static String PARSER_GETLONG_ERR(JsonParser.Event event) {
        return localize("parser.getLong.err", event);
    }

    static String PARSER_GETBIGDECIMAL_ERR(JsonParser.Event event) {
        return localize("parser.getBigDecimal.err", event);
    }

    static String PARSER_EXPECTED_EOF(JsonTokenizer.JsonToken token) {
        return localize("parser.expected.eof", token);
    }

    static String PARSER_TOKENIZER_CLOSE_IO() {
        return localize("parser.tokenizer.close.io", new Object[0]);
    }

    static String PARSER_INVALID_TOKEN(JsonTokenizer.JsonToken token, JsonLocation location, String expectedTokens) {
        return localize("parser.invalid.token", token, location, expectedTokens);
    }

    static String GENERATOR_FLUSH_IO_ERR() {
        return localize("generator.flush.io.err", new Object[0]);
    }

    static String GENERATOR_CLOSE_IO_ERR() {
        return localize("generator.close.io.err", new Object[0]);
    }

    static String GENERATOR_WRITE_IO_ERR() {
        return localize("generator.write.io.err", new Object[0]);
    }

    static String GENERATOR_ILLEGAL_METHOD(Object scope) {
        return localize("generator.illegal.method", scope);
    }

    static String GENERATOR_DOUBLE_INFINITE_NAN() {
        return localize("generator.double.infinite.nan", new Object[0]);
    }

    static String GENERATOR_INCOMPLETE_JSON() {
        return localize("generator.incomplete.json", new Object[0]);
    }

    static String GENERATOR_ILLEGAL_MULTIPLE_TEXT() {
        return localize("generator.illegal.multiple.text", new Object[0]);
    }

    static String WRITER_WRITE_ALREADY_CALLED() {
        return localize("writer.write.already.called", new Object[0]);
    }

    static String READER_READ_ALREADY_CALLED() {
        return localize("reader.read.already.called", new Object[0]);
    }

    static String READER_EXPECTED_ARRAY_GOT_OBJECT() {
        return localize("reader.expected.array.got.object", new Object[0]);
    }

    static String READER_EXPECTED_OBJECT_GOT_ARRAY() {
        return localize("reader.expected.object.got.array", new Object[0]);
    }

    static String OBJBUILDER_NAME_NULL() {
        return localize("objbuilder.name.null", new Object[0]);
    }

    static String OBJBUILDER_VALUE_NULL() {
        return localize("objbuilder.value.null", new Object[0]);
    }

    static String OBJBUILDER_OBJECT_BUILDER_NULL() {
        return localize("objbuilder.object.builder.null", new Object[0]);
    }

    static String OBJBUILDER_ARRAY_BUILDER_NULL() {
        return localize("objbuilder.array.builder.null", new Object[0]);
    }

    static String ARRBUILDER_VALUE_NULL() {
        return localize("arrbuilder.value.null", new Object[0]);
    }

    static String ARRBUILDER_OBJECT_BUILDER_NULL() {
        return localize("arrbuilder.object.builder.null", new Object[0]);
    }

    static String ARRBUILDER_ARRAY_BUILDER_NULL() {
        return localize("arrbuilder.array.builder.null", new Object[0]);
    }

    private static String localize(String key, Object... args) {
        try {
            String msg = BUNDLE.getString(key);
            return MessageFormat.format(msg, args);
        } catch (Exception e) {
            return getDefaultMessage(key, args);
        }
    }

    private static String getDefaultMessage(String key, Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[failed to localize] ");
        sb.append(key);
        if (args != null) {
            sb.append('(');
            for (int i = 0; i < args.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(String.valueOf(args[i]));
            }
            sb.append(')');
        }
        return sb.toString();
    }
}
