package com.github.markusbernhardt.proxy.selector.whitelist;

import com.github.markusbernhardt.proxy.util.UriFilter;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/IpRangeFilter.class */
public class IpRangeFilter implements UriFilter {
    private byte[] matchTo;
    int numOfBits;

    public IpRangeFilter(String matchTo) {
        String[] parts = matchTo.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("IP range is not valid:" + matchTo);
        }
        try {
            InetAddress address = InetAddress.getByName(parts[0].trim());
            this.matchTo = address.getAddress();
            this.numOfBits = Integer.parseInt(parts[1].trim());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("IP range is not valid:" + matchTo);
        }
    }

    @Override // com.github.markusbernhardt.proxy.util.UriFilter
    public boolean accept(URI uri) {
        if (uri == null || uri.getHost() == null) {
            return false;
        }
        try {
            InetAddress address = InetAddress.getByName(uri.getHost());
            byte[] addr = address.getAddress();
            if (addr.length != this.matchTo.length) {
                return false;
            }
            int bit = 0;
            for (int nibble = 0; nibble < addr.length; nibble++) {
                for (int nibblePos = 7; nibblePos >= 0; nibblePos--) {
                    int mask = 1 << nibblePos;
                    if ((this.matchTo[nibble] & mask) != (addr[nibble] & mask)) {
                        return false;
                    }
                    bit++;
                    if (bit >= this.numOfBits) {
                        return true;
                    }
                }
            }
            return false;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
