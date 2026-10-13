package javax.json.stream;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:javax/json/stream/JsonGeneratorFactory.class */
public interface JsonGeneratorFactory {
    JsonGenerator createGenerator(Writer writer);

    JsonGenerator createGenerator(OutputStream outputStream);

    JsonGenerator createGenerator(OutputStream outputStream, Charset charset);

    Map<String, ?> getConfigInUse();
}
