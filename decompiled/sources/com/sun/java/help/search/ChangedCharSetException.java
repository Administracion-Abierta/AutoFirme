package com.sun.java.help.search;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ChangedCharSetException.class */
public class ChangedCharSetException extends IOException {
    String charSetSpec;
    boolean charSetKey;

    public ChangedCharSetException(String str, boolean z) {
        this.charSetSpec = str;
        this.charSetKey = z;
    }

    public String getCharSetSpec() {
        return this.charSetSpec;
    }

    public boolean keyEqualsCharSet() {
        return this.charSetKey;
    }
}
