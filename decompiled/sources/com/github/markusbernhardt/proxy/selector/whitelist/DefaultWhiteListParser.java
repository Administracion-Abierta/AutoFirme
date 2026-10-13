package com.github.markusbernhardt.proxy.selector.whitelist;

import com.github.markusbernhardt.proxy.search.browser.ie.IELocalByPassFilter;
import com.github.markusbernhardt.proxy.util.UriFilter;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/DefaultWhiteListParser.class */
public class DefaultWhiteListParser implements WhiteListParser {
    @Override // com.github.markusbernhardt.proxy.selector.whitelist.WhiteListParser
    public List<UriFilter> parseWhiteList(String whiteList) {
        List<UriFilter> result = new ArrayList<>();
        String[] token = whiteList.split("[, ]+");
        for (String str : token) {
            String tkn = str.trim();
            if (isIP4SubnetFilter(tkn) || isIP6SubnetFilter(tkn)) {
                result.add(new IpRangeFilter(tkn));
            } else if (tkn.endsWith("*")) {
                result.add(new HostnameFilter(HostnameFilter.Mode.BEGINS_WITH, tkn.substring(0, tkn.length() - 1)));
            } else if (tkn.trim().startsWith("*")) {
                result.add(new HostnameFilter(HostnameFilter.Mode.ENDS_WITH, tkn.substring(1)));
            } else if (tkn.trim().equals("<local>")) {
                result.add(new IELocalByPassFilter());
            } else {
                result.add(new HostnameFilter(HostnameFilter.Mode.ENDS_WITH, tkn));
            }
        }
        return result;
    }

    private boolean isIP6SubnetFilter(String token) {
        return IPWithSubnetChecker.isValidIP6Range(token);
    }

    private boolean isIP4SubnetFilter(String token) {
        return IPWithSubnetChecker.isValidIP4Range(token);
    }
}
