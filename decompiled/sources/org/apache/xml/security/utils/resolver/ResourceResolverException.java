package org.apache.xml.security.utils.resolver;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/resolver/ResourceResolverException.class */
public class ResourceResolverException extends XMLSecurityException {
    private static final long serialVersionUID = 1;
    private String uri;
    private String baseURI;

    public ResourceResolverException(String msgID, String uri, String baseURI) {
        super(msgID);
        this.uri = uri;
        this.baseURI = baseURI;
    }

    public ResourceResolverException(String msgID, Object[] exArgs, String uri, String baseURI) {
        super(msgID, exArgs);
        this.uri = uri;
        this.baseURI = baseURI;
    }

    public ResourceResolverException(Exception originalException, String uri, String baseURI, String msgID) {
        super(originalException, msgID);
        this.uri = uri;
        this.baseURI = baseURI;
    }

    @Deprecated
    public ResourceResolverException(String msgID, Exception originalException, String uri, String baseURI) {
        this(originalException, uri, baseURI, msgID);
    }

    public ResourceResolverException(Exception originalException, String uri, String baseURI, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
        this.uri = uri;
        this.baseURI = baseURI;
    }

    @Deprecated
    public ResourceResolverException(String msgID, Object[] exArgs, Exception originalException, String uri, String baseURI) {
        this(originalException, uri, baseURI, msgID, exArgs);
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getURI() {
        return this.uri;
    }

    public void setbaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getbaseURI() {
        return this.baseURI;
    }
}
