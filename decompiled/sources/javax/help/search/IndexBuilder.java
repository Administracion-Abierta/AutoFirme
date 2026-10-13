package javax.help.search;

import java.io.File;
import java.util.Enumeration;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/IndexBuilder.class */
public abstract class IndexBuilder {
    protected String indexDir;
    private boolean debug = false;

    public abstract void close() throws Exception;

    public abstract void storeStopWords(Enumeration enumeration);

    public abstract Enumeration getStopWords();

    public abstract void openDocument(String str) throws Exception;

    public abstract void closeDocument() throws Exception;

    public abstract void storeLocation(String str, int i) throws Exception;

    public abstract void storeTitle(String str) throws Exception;

    public IndexBuilder(String str) throws Exception {
        debug(new StringBuffer().append("indexDir=").append(str).toString());
        this.indexDir = str;
        File file = new File(str);
        try {
            if (!file.exists()) {
                debug(new StringBuffer().append("file ").append(str).append(" didn't exist - creating").toString());
                file.mkdirs();
            }
        } catch (SecurityException e) {
        }
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("IndexBuilder: ").append(str).toString());
        }
    }
}
