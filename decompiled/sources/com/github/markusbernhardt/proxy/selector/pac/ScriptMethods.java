package com.github.markusbernhardt.proxy.selector.pac;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/ScriptMethods.class */
public interface ScriptMethods {
    boolean isPlainHostName(String str);

    boolean dnsDomainIs(String str, String str2);

    boolean localHostOrDomainIs(String str, String str2);

    boolean isResolvable(String str);

    boolean isResolvableEx(String str);

    boolean isInNet(String str, String str2, String str3);

    boolean isInNetEx(String str, String str2);

    String dnsResolve(String str);

    String dnsResolveEx(String str);

    String myIpAddress();

    String myIpAddressEx();

    int dnsDomainLevels(String str);

    boolean shExpMatch(String str, String str2);

    boolean weekdayRange(String str, String str2, String str3);

    boolean dateRange(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    boolean timeRange(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    String sortIpAddressList(String str);

    String getClientVersion();
}
