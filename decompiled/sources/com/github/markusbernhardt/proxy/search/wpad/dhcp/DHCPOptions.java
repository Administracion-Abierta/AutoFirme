package com.github.markusbernhardt.proxy.search.wpad.dhcp;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/wpad/dhcp/DHCPOptions.class */
public class DHCPOptions {
    public static final int OPTION_PAD = 0;
    public static final int OPTION_NETMASK = 1;
    public static final int OPTION_TIME_OFFSET = 2;
    public static final int OPTION_ROUTERS = 3;
    public static final int OPTION_TIME_SERVERS = 4;
    public static final int OPTION_NAME_SERVERS = 5;
    public static final int OPTION_DNS_SERVERS = 6;
    public static final int OPTION_LOG_SERVERS = 7;
    public static final int OPTION_COOKIE_SERVERS = 8;
    public static final int OPTION_LPR_SERVERS = 9;
    public static final int OPTION_IMPRESS_SERVERS = 10;
    public static final int OPTION_RESSOURCE_LOCATION_SERVERS = 11;
    public static final int OPTION_HOSTNAME = 12;
    public static final int OPTION_BOOT_FILESIZE = 13;
    public static final int OPTION_MERIT_DUMPFILE = 14;
    public static final int OPTION_DOMAIN_NAME = 15;
    public static final int OPTION_SWAP_SERVER = 16;
    public static final int OPTION_ROOT_PATH = 17;
    public static final int OPTION_EXTENSIONS_PATH = 18;
    public static final int OPTION_END = 255;
    public static final int OPTION_IP_HOST_FORWARDING_ENABLE = 19;
    public static final int OPTION_IP_HOST_NON_LOCAL_SOURCE_ROUTING_ENABLE = 20;
    public static final int OPTION_IP_HOST_POLICY_FILTERS = 21;
    public static final int OPTION_IP_HOST_MAXIMUM_DATAGRAM_REASSEMBLY_SIZE = 22;
    public static final int OPTION_IP_HOST_DEFAULT_TTL = 23;
    public static final int OPTION_IP_HOST_MTU_AGEING_TIMEOUT = 24;
    public static final int OPTION_IP_HOST_MTU_PLATEAU_TABLE = 25;
    public static final int OPTION_IP_INTERFACE_MTU = 26;
    public static final int OPTION_IP_INTERFACE_ALL_SUBNETS_LOCAL_ENABLE = 27;
    public static final int OPTION_IP_INTERFACE_BROADCAST_ADDRESS = 28;
    public static final int OPTION_IP_INTERFACE_PERFORM_MASK_DISCOVERY_ENABLE = 29;
    public static final int OPTION_IP_INTERFACE_MASK_SUPPLIER_ENABLE = 30;
    public static final int OPTION_IP_INTERFACE_PERFORM_ROUTER_DISCOVERY_ENABLE = 31;
    public static final int OPTION_IP_INTERFACE_ROUTER_SOLICITATION_ADDRESS = 32;
    public static final int OPTION_IP_INTERFACE_STATIC_ROUTES = 33;
    public static final int OPTION_LINK_TRAILER_ENCAPSULATION_ENABLE = 34;
    public static final int OPTION_LINK_ARP_CACHE_TIMEOUT = 35;
    public static final int OPTION_LINK_ETHERNET_ENCAPSULATION_ENABLE = 36;
    public static final int OPTION_TCP_DEFAULT_TTL = 37;
    public static final int OPTION_TCP_KEEP_ALIVE_INTERVAL = 38;
    public static final int OPTION_TCP_KEEP_ALIVE_GERBAGE_ENABLE = 39;
    public static final int OPTION_NIS_DOMAIN = 40;
    public static final int OPTION_NIS_SERVERS = 41;
    public static final int OPTION_NTP_SERVERS = 42;
    public static final int OPTION_SERVICE_VENDOR_SPECIFIC_INFORMATIONS = 43;
    public static final int OPTION_SERVICE_NETBOIS_NAME_SERVERS = 44;
    public static final int OPTION_SERVICE_NETBOIS_DATAGRAM_DISTRIBUTION_SERVERS = 45;
    public static final int OPTION_SERVICE_NETBOIS_NODE_TYPE = 46;
    public static final int OPTION_SERVICE_NETBOIS_SCOPE_TYPE = 47;
    public static final int OPTION_SERVICE_X_FONT_SERVERS = 48;
    public static final int OPTION_SERVICE_X_DISPLAY_MANAGERS = 49;
    public static final int OPTION_DHCP_IP_ADRESS_REQUESTED = 50;
    public static final int OPTION_DHCP_IP_LEASE_TIME = 51;
    public static final int OPTION_DHCP_OVERLOAD = 52;
    public static final int OPTION_DHCP_MESSAGE_TYPE = 53;
    public static final int OPTION_DHCP_SERVER_IDENTIFIER = 54;
    public static final int OPTION_DHCP_PARAMETER_REQUEST_LIST = 55;
    public static final int OPTION_DHCP_MESSAGE = 56;
    public static final int OPTION_DHCP_MAXIMUM_MESSAGE_SIZE = 57;
    public static final int OPTION_DHCP_RENEWAL_TIME = 58;
    public static final int OPTION_DHCP_REBIND_TIME = 59;
    public static final int OPTION_DHCP_CLASS_IDENTIFIER = 60;
    public static final int OPTION_DHCP_CLIENT_IDENTIFIER = 61;
    public static final int OPTION_PROXY_AUTODISCOVERY = 252;
    private Map<Byte, DHCPOptionsEntry> optionsTable;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/wpad/dhcp/DHCPOptions$DHCPOptionsEntry.class */
    class DHCPOptionsEntry {
        protected byte code;
        protected byte length;
        protected byte[] content;

        public DHCPOptionsEntry(byte entryCode, byte entryLength, byte[] entryContent) {
            this.code = entryCode;
            this.length = entryLength;
            this.content = entryContent;
        }

        public String toString() {
            return "Code: " + ((int) this.code) + "\nContent: " + new String(this.content);
        }
    }

    public DHCPOptions() {
        this.optionsTable = null;
        this.optionsTable = new HashMap();
    }

    public void removeOption(byte entryCode) {
        this.optionsTable.remove(new Byte(entryCode));
    }

    public boolean contains(byte entryCode) {
        return this.optionsTable.containsKey(new Byte(entryCode));
    }

    public boolean isEmpty() {
        return this.optionsTable.isEmpty();
    }

    public byte[] getOption(byte entryCode) {
        if (contains(entryCode)) {
            DHCPOptionsEntry ent = this.optionsTable.get(Byte.valueOf(entryCode));
            return ent.content;
        }
        return null;
    }

    public void setOption(byte entryCode, byte[] value) {
        DHCPOptionsEntry opt = new DHCPOptionsEntry(entryCode, (byte) value.length, value);
        this.optionsTable.put(new Byte(entryCode), opt);
    }

    private byte[] getArrayOption(int length, int position, byte[] options) {
        byte[] value = new byte[length];
        for (int i = 0; i < length; i++) {
            value[i] = options[position + i];
        }
        return value;
    }

    public void internalize(byte[] optionsArray) {
        int i = 4;
        while (true) {
            int pos = i;
            if (optionsArray[pos] != -1) {
                int pos2 = pos + 1;
                byte code = optionsArray[pos];
                int pos3 = pos2 + 1;
                byte length = optionsArray[pos2];
                byte[] value = getArrayOption(length, pos3, optionsArray);
                setOption(code, value);
                i = pos3 + length;
            } else {
                return;
            }
        }
    }

    public byte[] externalize() {
        byte[] options = new byte[312];
        options[0] = 99;
        options[1] = -126;
        options[2] = 83;
        options[3] = 99;
        int position = 4;
        for (Map.Entry<Byte, DHCPOptionsEntry> entry : this.optionsTable.entrySet()) {
            int i = position;
            int position2 = position + 1;
            options[i] = entry.getValue().code;
            position = position2 + 1;
            options[position2] = entry.getValue().length;
            for (int i2 = 0; i2 < entry.getValue().length; i2++) {
                int i3 = position;
                position++;
                options[i3] = entry.getValue().content[i2];
            }
        }
        options[position] = -1;
        return options;
    }

    public void printList() {
        System.out.println(this.optionsTable.toString());
    }
}
