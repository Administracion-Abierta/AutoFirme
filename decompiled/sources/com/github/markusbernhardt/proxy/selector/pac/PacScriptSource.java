package com.github.markusbernhardt.proxy.selector.pac;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/PacScriptSource.class */
public interface PacScriptSource {
    String getScriptContent() throws IOException;

    boolean isScriptValid();
}
