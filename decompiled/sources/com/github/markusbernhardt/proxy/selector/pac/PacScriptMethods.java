package com.github.markusbernhardt.proxy.selector.pac;

import com.github.markusbernhardt.proxy.util.Logger;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/PacScriptMethods.class */
public class PacScriptMethods implements ScriptMethods {
    public static final String OVERRIDE_LOCAL_IP = "com.btr.proxy.pac.overrideLocalIP";
    private String ipAddress = null;
    private String ipAddressEx = null;
    private static final String GMT = "GMT";
    private static final String DAY1 = "day1";
    private static final String DAY2 = "day2";
    private static final String MONTH1 = "month1";
    private static final String MONTH2 = "month2";
    private static final String YEAR1 = "year1";
    private static final String YEAR2 = "year2";
    private Calendar currentTime;
    private static final List<String> DAYS = Collections.unmodifiableList(Arrays.asList("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"));
    private static final List<String> MONTH = Collections.unmodifiableList(Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"));
    private static final BigInteger HIGH_32_INT = new BigInteger(new byte[]{-1, -1, -1, -1});
    private static final BigInteger HIGH_128_INT = new BigInteger(new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean isPlainHostName(String host) {
        return host.indexOf(".") < 0;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean dnsDomainIs(String host, String domain) {
        return host.endsWith(domain);
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean localHostOrDomainIs(String host, String domain) {
        return domain.startsWith(host);
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean isResolvable(String host) {
        try {
            InetAddress.getByName(host).getHostAddress();
            return true;
        } catch (UnknownHostException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "Hostname not resolveable {}.", host);
            return false;
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean isInNet(String host, String pattern, String mask) {
        String host2 = dnsResolve(host);
        if (host2 == null || host2.length() == 0) {
            return false;
        }
        long lhost = parseIpAddressToLong(host2);
        long lpattern = parseIpAddressToLong(pattern);
        long lmask = parseIpAddressToLong(mask);
        return (lhost & lmask) == lpattern;
    }

    private long parseIpAddressToLong(String address) {
        long result = 0;
        String[] parts = address.split("\\.");
        long shift = 24;
        for (String part : parts) {
            long lpart = Long.parseLong(part.trim());
            result |= lpart << ((int) shift);
            shift -= 8;
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public String dnsResolve(String host) {
        try {
            InetAddress ina = InetAddress.getByName(host);
            return ina.getHostAddress();
        } catch (UnknownHostException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "DNS name not resolvable {}.", host);
            return "";
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public String myIpAddress() {
        if (this.ipAddress == null || this.ipAddress.isEmpty()) {
            this.ipAddress = getLocalAddressOfType(Inet4Address.class);
        }
        return this.ipAddress;
    }

    private String getLocalAddressOfType(Class<? extends InetAddress> cl) {
        try {
            String overrideIP = System.getProperty(OVERRIDE_LOCAL_IP);
            if (overrideIP != null && overrideIP.trim().length() > 0) {
                return overrideIP.trim();
            }
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            List<InetAddress> localAddresses = new ArrayList<>();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                if (current.isUp() && !current.isLoopback() && !current.isVirtual()) {
                    Enumeration<InetAddress> addresses = current.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress adr = addresses.nextElement();
                        if (cl.isInstance(adr)) {
                            localAddresses.add(adr);
                        }
                    }
                }
            }
            if (localAddresses.isEmpty()) {
                return "";
            }
            InetAddress localAddress = localAddresses.get(0);
            if (localAddresses.size() > 1) {
                if (Inet4Address.class.equals(cl)) {
                    InetAddress localHost = Inet4Address.getLocalHost();
                    if (localAddresses.contains(localHost)) {
                        localAddress = localHost;
                    }
                } else if (Inet6Address.class.equals(cl)) {
                    InetAddress localHost2 = Inet6Address.getLocalHost();
                    if (localAddresses.contains(localHost2)) {
                        localAddress = localHost2;
                    }
                }
            }
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.TRACE, "Local address resolved to {}", localAddress);
            return localAddress.getHostAddress();
        } catch (IOException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "Local address not resolvable.", new Object[0]);
            return "";
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public int dnsDomainLevels(String host) {
        int count = 0;
        int startPos = 0;
        while (true) {
            int iIndexOf = host.indexOf(".", startPos + 1);
            startPos = iIndexOf;
            if (iIndexOf > -1) {
                count++;
            } else {
                return count;
            }
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean shExpMatch(String str, String shexp) {
        StringTokenizer tokenizer = new StringTokenizer(shexp, "*");
        int length = 0;
        while (true) {
            int startPos = length;
            if (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int temp = str.indexOf(token, startPos);
                if (startPos == 0 && !shexp.startsWith("*") && temp != 0) {
                    return false;
                }
                if ((!tokenizer.hasMoreTokens() && !shexp.endsWith("*") && !str.endsWith(token)) || temp == -1) {
                    return false;
                }
                length = temp + token.length();
            } else {
                return true;
            }
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean weekdayRange(String wd1, String wd2, String gmt) {
        boolean useGmt = GMT.equalsIgnoreCase(wd2) || GMT.equalsIgnoreCase(gmt);
        Calendar cal = getCurrentTime(useGmt);
        int currentDay = cal.get(7) - 1;
        int from = DAYS.indexOf(wd1 == null ? null : wd1.toUpperCase());
        int to = DAYS.indexOf(wd2 == null ? null : wd2.toUpperCase());
        if (to == -1) {
            to = from;
        }
        if (to < from) {
            return currentDay >= from || currentDay <= to;
        }
        return currentDay >= from && currentDay <= to;
    }

    public void setCurrentTime(Calendar cal) {
        this.currentTime = cal;
    }

    private Calendar getCurrentTime(boolean useGmt) {
        if (this.currentTime != null) {
            return (Calendar) this.currentTime.clone();
        }
        return Calendar.getInstance(useGmt ? TimeZone.getTimeZone(GMT) : TimeZone.getDefault());
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean dateRange(Object day1, Object month1, Object year1, Object day2, Object month2, Object year2, Object gmt) {
        Map<String, Integer> params = new HashMap<>();
        parseDateParam(params, day1);
        parseDateParam(params, month1);
        parseDateParam(params, year1);
        parseDateParam(params, day2);
        parseDateParam(params, month2);
        parseDateParam(params, year2);
        parseDateParam(params, gmt);
        boolean useGmt = params.get("gmt") != null;
        Calendar cal = getCurrentTime(useGmt);
        Date current = cal.getTime();
        if (params.get(DAY1) != null) {
            cal.set(5, params.get(DAY1).intValue());
        }
        if (params.get(MONTH1) != null) {
            cal.set(2, params.get(MONTH1).intValue());
        }
        if (params.get(YEAR1) != null) {
            cal.set(1, params.get(YEAR1).intValue());
        }
        Date from = cal.getTime();
        if (params.get(DAY2) != null) {
            cal.set(5, params.get(DAY2).intValue());
        }
        if (params.get(MONTH2) != null) {
            cal.set(2, params.get(MONTH2).intValue());
        }
        if (params.get(YEAR2) != null) {
            cal.set(1, params.get(YEAR2).intValue());
        }
        Date to = cal.getTime();
        if (to.before(from)) {
            cal.add(2, 1);
            to = cal.getTime();
        }
        if (to.before(from)) {
            cal.add(1, 1);
            cal.add(2, -1);
            to = cal.getTime();
        }
        return current.compareTo(from) >= 0 && current.compareTo(to) <= 0;
    }

    private void parseDateParam(Map<String, Integer> params, Object value) {
        int n;
        if (value instanceof Number) {
            int n2 = ((Number) value).intValue();
            if (n2 <= 31) {
                if (params.get(DAY1) == null) {
                    params.put(DAY1, Integer.valueOf(n2));
                } else {
                    params.put(DAY2, Integer.valueOf(n2));
                }
            } else if (params.get(YEAR1) == null) {
                params.put(YEAR1, Integer.valueOf(n2));
            } else {
                params.put(YEAR2, Integer.valueOf(n2));
            }
        }
        if ((value instanceof String) && (n = MONTH.indexOf(((String) value).toUpperCase())) > -1) {
            if (params.get(MONTH1) == null) {
                params.put(MONTH1, Integer.valueOf(n));
            } else {
                params.put(MONTH2, Integer.valueOf(n));
            }
        }
        if (GMT.equalsIgnoreCase(String.valueOf(value))) {
            params.put("gmt", 1);
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean timeRange(Object hour1, Object min1, Object sec1, Object hour2, Object min2, Object sec2, Object gmt) {
        Date from;
        Date to;
        boolean useGmt = GMT.equalsIgnoreCase(String.valueOf(min1)) || GMT.equalsIgnoreCase(String.valueOf(sec1)) || GMT.equalsIgnoreCase(String.valueOf(min2)) || GMT.equalsIgnoreCase(String.valueOf(gmt));
        Calendar cal = getCurrentTime(useGmt);
        cal.set(14, 0);
        Date current = cal.getTime();
        if (sec2 instanceof Number) {
            cal.set(11, ((Number) hour1).intValue());
            cal.set(12, ((Number) min1).intValue());
            cal.set(13, ((Number) sec1).intValue());
            from = cal.getTime();
            cal.set(11, ((Number) hour2).intValue());
            cal.set(12, ((Number) min2).intValue());
            cal.set(13, ((Number) sec2).intValue());
            to = cal.getTime();
        } else if (hour2 instanceof Number) {
            cal.set(11, ((Number) hour1).intValue());
            cal.set(12, ((Number) min1).intValue());
            cal.set(13, 0);
            from = cal.getTime();
            cal.set(11, ((Number) sec1).intValue());
            cal.set(12, ((Number) hour2).intValue());
            cal.set(13, 59);
            to = cal.getTime();
        } else if (min1 instanceof Number) {
            cal.set(11, ((Number) hour1).intValue());
            cal.set(12, 0);
            cal.set(13, 0);
            from = cal.getTime();
            cal.set(11, ((Number) min1).intValue());
            cal.set(12, 59);
            cal.set(13, 59);
            to = cal.getTime();
        } else {
            cal.set(11, ((Number) hour1).intValue());
            cal.set(12, 0);
            cal.set(13, 0);
            from = cal.getTime();
            cal.set(11, ((Number) hour1).intValue());
            cal.set(12, 59);
            cal.set(13, 59);
            to = cal.getTime();
        }
        if (to.before(from)) {
            cal.setTime(to);
            cal.add(5, 1);
            to = cal.getTime();
        }
        return current.compareTo(from) >= 0 && current.compareTo(to) <= 0;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean isResolvableEx(String host) {
        return isResolvable(host);
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public boolean isInNetEx(String ipOrHost, String cidr) {
        if (ipOrHost == null || ipOrHost.length() == 0 || cidr == null || cidr.length() == 0) {
            return false;
        }
        try {
            String[] cidrParts = cidr.split("/");
            if (cidrParts.length != 2) {
                return false;
            }
            String cidrRange = cidrParts[0];
            int cidrBits = Integer.parseInt(cidrParts[1]);
            byte[] addressBytes = InetAddress.getByName(ipOrHost).getAddress();
            BigInteger ip = new BigInteger(addressBytes);
            BigInteger mask = addressBytes.length == 4 ? HIGH_32_INT.shiftLeft(32 - cidrBits) : HIGH_128_INT.shiftLeft(128 - cidrBits);
            byte[] rangeBytes = InetAddress.getByName(cidrRange).getAddress();
            BigInteger range = new BigInteger(rangeBytes);
            BigInteger lowIP = range.and(mask);
            BigInteger highIP = lowIP.add(mask.not());
            return lowIP.compareTo(ip) <= 0 && highIP.compareTo(ip) >= 0;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public String dnsResolveEx(String host) {
        StringBuilder result = new StringBuilder();
        try {
            InetAddress[] list = InetAddress.getAllByName(host);
            for (InetAddress inetAddress : list) {
                result.append(inetAddress.getHostAddress());
                result.append("; ");
            }
        } catch (UnknownHostException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "DNS name not resolvable {}.", host);
        }
        return result.toString();
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public String myIpAddressEx() {
        if (this.ipAddressEx == null || this.ipAddressEx.isEmpty()) {
            this.ipAddressEx = getLocalAddressOfType(Inet6Address.class);
        }
        return this.ipAddressEx;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public String sortIpAddressList(String ipAddressList) {
        if (ipAddressList == null || ipAddressList.trim().length() == 0) {
            return "";
        }
        try {
            String[] ipAddressToken = ipAddressList.split(PolicyIdFilter.OID_SEPARATOR);
            TreeMap<byte[], String> sorting = new TreeMap<>((Comparator<? super byte[]>) new Comparator<byte[]>() { // from class: com.github.markusbernhardt.proxy.selector.pac.PacScriptMethods.1
                @Override // java.util.Comparator
                public int compare(byte[] b1, byte[] b2) {
                    if (b1.length != b2.length) {
                        return b2.length - b1.length;
                    }
                    return new BigInteger(b1).compareTo(new BigInteger(b2));
                }
            });
            for (String ip : ipAddressToken) {
                String cleanIP = ip.trim();
                sorting.put(InetAddress.getByName(cleanIP).getAddress(), cleanIP);
            }
            StringBuilder result = new StringBuilder();
            for (String ip2 : sorting.values()) {
                if (result.length() > 0) {
                    result.append(PolicyIdFilter.OID_SEPARATOR);
                }
                result.append(ip2);
            }
            return result.toString();
        } catch (Exception e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "Cannot sort invalid IP list: {}.", ipAddressList);
            return "";
        }
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.ScriptMethods
    public String getClientVersion() {
        return "1.0";
    }
}
