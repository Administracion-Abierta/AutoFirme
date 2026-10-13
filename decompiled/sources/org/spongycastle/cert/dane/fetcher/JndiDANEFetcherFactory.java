package org.spongycastle.cert.dane.fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.spongycastle.cert.dane.DANEEntry;
import org.spongycastle.cert.dane.DANEEntryFetcher;
import org.spongycastle.cert.dane.DANEEntryFetcherFactory;
import org.spongycastle.cert.dane.DANEException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/dane/fetcher/JndiDANEFetcherFactory.class */
public class JndiDANEFetcherFactory implements DANEEntryFetcherFactory {
    private static final String DANE_TYPE = "53";
    private List dnsServerList = new ArrayList();
    private boolean isAuthoritative;

    public JndiDANEFetcherFactory usingDNSServer(String dnsServer) {
        this.dnsServerList.add(dnsServer);
        return this;
    }

    public JndiDANEFetcherFactory setAuthoritative(boolean isAuthoritative) {
        this.isAuthoritative = isAuthoritative;
        return this;
    }

    @Override // org.spongycastle.cert.dane.DANEEntryFetcherFactory
    public DANEEntryFetcher build(final String domainName) {
        final Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.authoritative", this.isAuthoritative ? "true" : "false");
        if (this.dnsServerList.size() > 0) {
            StringBuffer dnsServers = new StringBuffer();
            Iterator it = this.dnsServerList.iterator();
            while (it.hasNext()) {
                if (dnsServers.length() > 0) {
                    dnsServers.append(" ");
                }
                dnsServers.append("dns://" + it.next());
            }
            env.put("java.naming.provider.url", dnsServers.toString());
        }
        return new DANEEntryFetcher() { // from class: org.spongycastle.cert.dane.fetcher.JndiDANEFetcherFactory.1
            /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.NamingException */
            @Override // org.spongycastle.cert.dane.DANEEntryFetcher
            public List getEntries() throws DANEException {
                List entries = new ArrayList();
                try {
                    InitialDirContext initialDirContext = new InitialDirContext(env);
                    if (domainName.indexOf("_smimecert.") > 0) {
                        Attributes attrs = initialDirContext.getAttributes(domainName, new String[]{JndiDANEFetcherFactory.DANE_TYPE});
                        Attribute smimeAttr = attrs.get(JndiDANEFetcherFactory.DANE_TYPE);
                        if (smimeAttr != null) {
                            JndiDANEFetcherFactory.this.addEntries(entries, domainName, smimeAttr);
                        }
                    } else {
                        NamingEnumeration bindings = initialDirContext.listBindings("_smimecert." + domainName);
                        while (bindings.hasMore()) {
                            Binding b = (Binding) bindings.next();
                            DirContext sc = (DirContext) b.getObject();
                            String name = sc.getNameInNamespace().substring(1, sc.getNameInNamespace().length() - 1);
                            Attributes attrs2 = initialDirContext.getAttributes(name, new String[]{JndiDANEFetcherFactory.DANE_TYPE});
                            Attribute smimeAttr2 = attrs2.get(JndiDANEFetcherFactory.DANE_TYPE);
                            if (smimeAttr2 != null) {
                                String fullName = sc.getNameInNamespace();
                                String domainName2 = fullName.substring(1, fullName.length() - 1);
                                JndiDANEFetcherFactory.this.addEntries(entries, domainName2, smimeAttr2);
                            }
                        }
                    }
                    return entries;
                } catch (NamingException e) {
                    throw new DANEException("Exception dealing with DNS: " + e.getMessage(), e);
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntries(List entries, String domainName, Attribute smimeAttr) throws DANEException, NamingException {
        for (int index = 0; index != smimeAttr.size(); index++) {
            byte[] data = (byte[]) smimeAttr.get(index);
            if (DANEEntry.isValidCertificate(data)) {
                try {
                    entries.add(new DANEEntry(domainName, data));
                } catch (IOException e) {
                    throw new DANEException("Exception parsing entry: " + e.getMessage(), e);
                }
            }
        }
    }
}
