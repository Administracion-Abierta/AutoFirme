package com.sun.java.help.search;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import javax.help.search.ConfigFile;
import javax.help.search.IndexBuilder;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/PlainTextIndexerKit.class */
public class PlainTextIndexerKit extends DefaultIndexerKit {
    private boolean debugFlag = false;

    public PlainTextIndexerKit() {
        this.locale = Locale.getDefault();
    }

    @Override // com.sun.java.help.search.DefaultIndexerKit, javax.help.search.IndexerKit
    public Object clone() {
        return new PlainTextIndexerKit();
    }

    @Override // com.sun.java.help.search.DefaultIndexerKit, javax.help.search.IndexerKit
    public String getContentType() {
        return "text/plain";
    }

    @Override // com.sun.java.help.search.DefaultIndexerKit, javax.help.search.IndexerKit
    public void parse(Reader reader, String str, boolean z, IndexBuilder indexBuilder, ConfigFile configFile) throws IOException {
        debug(new StringBuffer().append("parsing ").append(str).toString());
        this.builder = indexBuilder;
        this.config = configFile;
        this.file = str;
        this.documentStarted = false;
        int intoTokens = 1;
        char[] cArr = new char[4096];
        while (reader.read(cArr, 0, cArr.length) != -1) {
            intoTokens = parseIntoTokens(new String(cArr), intoTokens);
        }
        try {
            storeTitle("No Title");
            endStoreDocument();
            this.builder = null;
            this.config = null;
        } catch (Exception e) {
            throw new IOException("Can't store title");
        }
    }

    private void debug(String str) {
        if (this.debugFlag) {
            System.err.println(new StringBuffer().append("PlainTextIndexerKit: ").append(str).toString());
        }
    }
}
