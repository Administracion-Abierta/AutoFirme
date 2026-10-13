package com.github.markusbernhardt.proxy.selector.whitelist;

import com.github.markusbernhardt.proxy.util.UriFilter;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/WhiteListParser.class */
public interface WhiteListParser {
    List<UriFilter> parseWhiteList(String str);
}
