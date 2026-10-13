package com.github.markusbernhardt.proxy.selector.whitelist;

import java.util.regex.Pattern;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/IPWithSubnetChecker.class */
public class IPWithSubnetChecker {
    private static Pattern IP_SUB_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])/(\\d|([12]\\d|3[0-2]))$");
    private static Pattern IP6_SUB_PATTERN = Pattern.compile("^[a-f0-9:]*/[0-9]+$");

    public static boolean isValidIP4Range(String possibleIPAddress) {
        return IP_SUB_PATTERN.matcher(possibleIPAddress).matches();
    }

    public static boolean isValidIP6Range(String possibleIPAddress) {
        return IP6_SUB_PATTERN.matcher(possibleIPAddress).matches();
    }
}
