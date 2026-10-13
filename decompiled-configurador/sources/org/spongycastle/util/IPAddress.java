package org.spongycastle.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/IPAddress.class */
public class IPAddress {
    public static boolean isValid(String address) {
        return isValidIPv4(address) || isValidIPv6(address);
    }

    public static boolean isValidWithNetMask(String address) {
        return isValidIPv4WithNetmask(address) || isValidIPv6WithNetmask(address);
    }

    public static boolean isValidIPv4(String address) {
        int pos;
        if (address.length() == 0) {
            return false;
        }
        int octets = 0;
        String temp = address + ".";
        int start = 0;
        while (start < temp.length() && (pos = temp.indexOf(46, start)) > start) {
            if (octets == 4) {
                return false;
            }
            try {
                int octet = Integer.parseInt(temp.substring(start, pos));
                if (octet < 0 || octet > 255) {
                    return false;
                }
                start = pos + 1;
                octets++;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return octets == 4;
    }

    public static boolean isValidIPv4WithNetmask(String address) {
        int index = address.indexOf("/");
        String mask = address.substring(index + 1);
        return index > 0 && isValidIPv4(address.substring(0, index)) && (isValidIPv4(mask) || isMaskValue(mask, 32));
    }

    public static boolean isValidIPv6WithNetmask(String address) {
        int index = address.indexOf("/");
        String mask = address.substring(index + 1);
        return index > 0 && isValidIPv6(address.substring(0, index)) && (isValidIPv6(mask) || isMaskValue(mask, 128));
    }

    private static boolean isMaskValue(String component, int size) {
        try {
            int value = Integer.parseInt(component);
            return value >= 0 && value <= size;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidIPv6(String address) {
        int pos;
        if (address.length() == 0) {
            return false;
        }
        int octets = 0;
        String temp = address + ":";
        boolean doubleColonFound = false;
        int start = 0;
        while (start < temp.length() && (pos = temp.indexOf(58, start)) >= start) {
            if (octets == 8) {
                return false;
            }
            if (start != pos) {
                String value = temp.substring(start, pos);
                if (pos == temp.length() - 1 && value.indexOf(46) > 0) {
                    if (!isValidIPv4(value)) {
                        return false;
                    }
                    octets++;
                } else {
                    try {
                        int octet = Integer.parseInt(temp.substring(start, pos), 16);
                        if (octet < 0 || octet > 65535) {
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            } else {
                if (pos != 1 && pos != temp.length() - 1 && doubleColonFound) {
                    return false;
                }
                doubleColonFound = true;
            }
            start = pos + 1;
            octets++;
        }
        return octets == 8 || doubleColonFound;
    }
}
