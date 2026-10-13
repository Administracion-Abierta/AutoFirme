package javax.help.search;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/IndexerKit.class */
public abstract class IndexerKit implements Cloneable {
    protected IndexBuilder builder;
    protected ConfigFile config;
    protected String file;
    protected Locale locale;
    private boolean debugFlag = false;

    public abstract Object clone();

    public abstract String getContentType();

    public abstract void parse(Reader reader, String str, boolean z, IndexBuilder indexBuilder, ConfigFile configFile) throws IOException;

    public abstract int parseIntoTokens(String str, int i);

    protected abstract void startStoreDocument(String str) throws Exception;

    protected abstract void endStoreDocument() throws Exception;

    protected abstract void storeToken(String str, int i) throws Exception;

    protected abstract void storeTitle(String str) throws Exception;

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(String str) {
        Locale locale;
        if (str == null) {
            setLocale((Locale) null);
            return;
        }
        int iIndexOf = str.indexOf("_");
        if (iIndexOf == -1) {
            locale = new Locale(str, "");
        } else {
            String strSubstring = str.substring(0, iIndexOf);
            int iIndexOf2 = str.indexOf("_", iIndexOf + 1);
            if (iIndexOf2 == -1) {
                locale = new Locale(strSubstring, str.substring(iIndexOf + 1));
            } else {
                locale = new Locale(strSubstring, str.substring(iIndexOf + 1, iIndexOf2), str.substring(iIndexOf2 + 1));
            }
        }
        setLocale(locale);
    }

    public Locale getLocale() {
        return this.locale;
    }

    private void debug(String str) {
        if (this.debugFlag) {
            System.err.println(new StringBuffer().append("IndexerKit: ").append(str).toString());
        }
    }
}
