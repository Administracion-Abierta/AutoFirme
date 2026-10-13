package javax.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:javax/json/JsonWriterFactory.class */
public interface JsonWriterFactory {
    JsonWriter createWriter(Writer writer);

    JsonWriter createWriter(OutputStream outputStream);

    JsonWriter createWriter(OutputStream outputStream, Charset charset);

    Map<String, ?> getConfigInUse();
}
