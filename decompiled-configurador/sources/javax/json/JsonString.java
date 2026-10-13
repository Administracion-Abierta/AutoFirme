package javax.json;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:javax/json/JsonString.class */
public interface JsonString extends JsonValue {
    String getString();

    CharSequence getChars();

    boolean equals(Object obj);

    int hashCode();
}
