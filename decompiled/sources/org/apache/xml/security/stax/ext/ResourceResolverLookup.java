package org.apache.xml.security.stax.ext;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/ResourceResolverLookup.class */
public interface ResourceResolverLookup {
    ResourceResolverLookup canResolve(String str, String str2);

    ResourceResolver newInstance(String str, String str2);
}
