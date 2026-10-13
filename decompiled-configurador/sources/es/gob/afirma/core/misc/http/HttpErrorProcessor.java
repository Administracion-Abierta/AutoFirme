package es.gob.afirma.core.misc.http;

import java.io.IOException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/http/HttpErrorProcessor.class */
public interface HttpErrorProcessor {
    byte[] processHttpError(IOException iOException, UrlHttpManager urlHttpManager, String str, int i, UrlHttpMethod urlHttpMethod, Properties properties) throws IOException;
}
