package org.codehaus.stax2.osgi;

import org.codehaus.stax2.XMLOutputFactory2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/osgi/Stax2OutputFactoryProvider.class */
public interface Stax2OutputFactoryProvider {
    public static final String OSGI_SVC_PROP_IMPL_NAME = "org.codehaus.stax2.implName";
    public static final String OSGI_SVC_PROP_IMPL_VERSION = "org.codehaus.stax2.implVersion";

    XMLOutputFactory2 createOutputFactory();
}
