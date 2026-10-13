package javax.json;

import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:javax/json/JsonBuilderFactory.class */
public interface JsonBuilderFactory {
    JsonObjectBuilder createObjectBuilder();

    JsonArrayBuilder createArrayBuilder();

    Map<String, ?> getConfigInUse();
}
