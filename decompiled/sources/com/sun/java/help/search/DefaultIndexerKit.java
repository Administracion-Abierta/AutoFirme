package com.sun.java.help.search;

import java.io.IOException;
import java.io.Reader;
import java.text.BreakIterator;
import java.util.Locale;
import javax.help.search.ConfigFile;
import javax.help.search.IndexBuilder;
import javax.help.search.IndexerKit;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DefaultIndexerKit.class */
public class DefaultIndexerKit extends IndexerKit {
    protected boolean documentStarted;
    private boolean debugFlag = false;

    public DefaultIndexerKit() {
        this.locale = Locale.getDefault();
    }

    @Override // javax.help.search.IndexerKit
    public Object clone() {
        return new DefaultIndexerKit();
    }

    @Override // javax.help.search.IndexerKit
    public String getContentType() {
        return "";
    }

    @Override // javax.help.search.IndexerKit
    public void parse(Reader reader, String str, boolean z, IndexBuilder indexBuilder, ConfigFile configFile) throws IOException {
        debug(new StringBuffer().append("parsing ").append(str).toString());
    }

    @Override // javax.help.search.IndexerKit
    public int parseIntoTokens(String str, int i) {
        int type;
        int length = i;
        try {
            BreakIterator wordInstance = BreakIterator.getWordInstance(this.locale);
            wordInstance.setText(str);
            int iFirst = wordInstance.first();
            for (int next = wordInstance.next(); next != -1; next = wordInstance.next()) {
                String lowerCase = new String(str.substring(iFirst, next)).trim().toLowerCase(this.locale);
                if (lowerCase.length() > 1) {
                    storeToken(lowerCase, length + iFirst);
                } else if (lowerCase.length() == 1 && ((type = Character.getType(lowerCase.charAt(0))) == 9 || type == 10 || type == 2 || type == 5 || type == 11 || type == 3 || type == 0 || type == 1)) {
                    storeToken(lowerCase, length + iFirst);
                }
                iFirst = next;
            }
            length += str.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    @Override // javax.help.search.IndexerKit
    protected void startStoreDocument(String str) throws Exception {
        if (this.config == null || this.builder == null) {
            throw new Exception("ConfigFile and/or IndexBuilder not set");
        }
        this.builder.openDocument(this.config.getURLString(str));
    }

    @Override // javax.help.search.IndexerKit
    protected void endStoreDocument() throws Exception {
        if (this.config == null || this.builder == null) {
            throw new Exception("ConfigFile and/or IndexBuilder not set");
        }
        this.builder.closeDocument();
    }

    @Override // javax.help.search.IndexerKit
    protected void storeToken(String str, int i) throws Exception {
        if (this.config == null || this.builder == null) {
            throw new Exception("ConfigFile and/or IndexBuilder not set");
        }
        if (!this.documentStarted) {
            try {
                startStoreDocument(this.file);
                this.documentStarted = true;
            } catch (Exception e) {
                if (this.debugFlag) {
                    e.printStackTrace();
                }
                throw new IOException("Can't store Document");
            }
        }
        this.builder.storeLocation(str, i);
    }

    @Override // javax.help.search.IndexerKit
    protected void storeTitle(String str) throws Exception {
        if (this.config == null || this.builder == null) {
            throw new Exception("ConfigFile and/or IndexBuilder not set");
        }
        this.builder.storeTitle(str);
    }

    private void debug(String str) {
        if (this.debugFlag) {
            System.err.println(new StringBuffer().append("DefaultIndexKit: ").append(str).toString());
        }
    }
}
