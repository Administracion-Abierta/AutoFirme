package org.codehaus.stax2.ri.evt;

import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/NamespaceEventImpl.class */
public class NamespaceEventImpl extends AttributeEventImpl implements Namespace {
    final String mPrefix;
    final String mURI;

    protected NamespaceEventImpl(Location loc, String nsURI) {
        super(loc, CanonicalizerBase.XMLNS, "http://www.w3.org/2000/xmlns/", null, nsURI, true);
        this.mPrefix = "";
        this.mURI = nsURI;
    }

    protected NamespaceEventImpl(Location loc, String nsPrefix, String nsURI) {
        super(loc, nsPrefix, "http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, nsURI, true);
        this.mPrefix = nsPrefix;
        this.mURI = nsURI;
    }

    public static NamespaceEventImpl constructDefaultNamespace(Location loc, String nsURI) {
        return new NamespaceEventImpl(loc, nsURI);
    }

    public static NamespaceEventImpl constructNamespace(Location loc, String nsPrefix, String nsURI) {
        if (nsPrefix == null || nsPrefix.length() == 0) {
            return new NamespaceEventImpl(loc, nsURI);
        }
        return new NamespaceEventImpl(loc, nsPrefix, nsURI);
    }

    public String getNamespaceURI() {
        return this.mURI;
    }

    public String getPrefix() {
        return this.mPrefix;
    }

    public boolean isDefaultNamespaceDeclaration() {
        return this.mPrefix.length() == 0;
    }

    @Override // org.codehaus.stax2.ri.evt.AttributeEventImpl, org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 13;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean isNamespace() {
        return true;
    }
}
