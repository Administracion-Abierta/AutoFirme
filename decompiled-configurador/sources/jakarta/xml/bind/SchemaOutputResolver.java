package jakarta.xml.bind;

import java.io.IOException;
import javax.xml.transform.Result;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/SchemaOutputResolver.class */
public abstract class SchemaOutputResolver {
    public abstract Result createOutput(String str, String str2) throws IOException;
}
