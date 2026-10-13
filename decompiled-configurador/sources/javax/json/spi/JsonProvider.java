package javax.json.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:javax/json/spi/JsonProvider.class */
public abstract class JsonProvider {
    private static final String DEFAULT_PROVIDER = "org.glassfish.json.JsonProviderImpl";

    public abstract JsonParser createParser(Reader reader);

    public abstract JsonParser createParser(InputStream inputStream);

    public abstract JsonParserFactory createParserFactory(Map<String, ?> map);

    public abstract JsonGenerator createGenerator(Writer writer);

    public abstract JsonGenerator createGenerator(OutputStream outputStream);

    public abstract JsonGeneratorFactory createGeneratorFactory(Map<String, ?> map);

    public abstract JsonReader createReader(Reader reader);

    public abstract JsonReader createReader(InputStream inputStream);

    public abstract JsonWriter createWriter(Writer writer);

    public abstract JsonWriter createWriter(OutputStream outputStream);

    public abstract JsonWriterFactory createWriterFactory(Map<String, ?> map);

    public abstract JsonReaderFactory createReaderFactory(Map<String, ?> map);

    public abstract JsonObjectBuilder createObjectBuilder();

    public abstract JsonArrayBuilder createArrayBuilder();

    public abstract JsonBuilderFactory createBuilderFactory(Map<String, ?> map);

    protected JsonProvider() {
    }

    public static JsonProvider provider() {
        ServiceLoader<JsonProvider> loader = ServiceLoader.load(JsonProvider.class);
        Iterator<JsonProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        try {
            Class<?> clazz = Class.forName(DEFAULT_PROVIDER);
            return (JsonProvider) clazz.newInstance();
        } catch (ClassNotFoundException x) {
            throw new JsonException("Provider org.glassfish.json.JsonProviderImpl not found", x);
        } catch (Exception x2) {
            throw new JsonException("Provider org.glassfish.json.JsonProviderImpl could not be instantiated: " + x2, x2);
        }
    }
}
