package com.github.markusbernhardt.proxy.selector.pac;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/PacScriptParser.class */
public interface PacScriptParser {
    PacScriptSource getScriptSource();

    String evaluate(String str, String str2) throws ProxyEvaluationException;
}
