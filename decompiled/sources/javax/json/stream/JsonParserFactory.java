package javax.json.stream;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;

/* JADX INFO: loaded from: autofirma.jar:javax/json/stream/JsonParserFactory.class */
public interface JsonParserFactory {
    JsonParser createParser(Reader reader);

    JsonParser createParser(InputStream inputStream);

    JsonParser createParser(InputStream inputStream, Charset charset);

    JsonParser createParser(JsonObject jsonObject);

    JsonParser createParser(JsonArray jsonArray);

    Map<String, ?> getConfigInUse();
}
