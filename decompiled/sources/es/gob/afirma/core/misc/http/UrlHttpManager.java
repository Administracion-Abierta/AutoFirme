package es.gob.afirma.core.misc.http;

import java.io.IOException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/UrlHttpManager.class */
public interface UrlHttpManager {
    byte[] readUrl(String str, UrlHttpMethod urlHttpMethod) throws IOException;

    byte[] readUrl(String str, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor) throws IOException;

    byte[] readUrl(String str, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor, SSLConfig sSLConfig) throws IOException;

    byte[] readUrl(String str, int i, UrlHttpMethod urlHttpMethod, Properties properties) throws IOException;

    byte[] readUrl(String str, int i, String str2, String str3, UrlHttpMethod urlHttpMethod) throws IOException;

    byte[] readUrl(String str, int i, String str2, String str3, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor) throws IOException;

    byte[] readUrl(String str, int i, String str2, String str3, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor, SSLConfig sSLConfig) throws IOException;

    byte[] readUrl(String str, int i, UrlHttpMethod urlHttpMethod, Properties properties, HttpErrorProcessor httpErrorProcessor) throws IOException;

    byte[] readUrl(String str, int i, UrlHttpMethod urlHttpMethod, Properties properties, HttpErrorProcessor httpErrorProcessor, SSLConfig sSLConfig) throws IOException;
}
