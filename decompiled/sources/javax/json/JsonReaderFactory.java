package javax.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:javax/json/JsonReaderFactory.class */
public interface JsonReaderFactory {
    JsonReader createReader(Reader reader);

    JsonReader createReader(InputStream inputStream);

    JsonReader createReader(InputStream inputStream, Charset charset);

    Map<String, ?> getConfigInUse();
}
