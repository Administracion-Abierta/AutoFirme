package org.codehaus.stax2.osgi;

import org.codehaus.stax2.XMLInputFactory2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/osgi/Stax2InputFactoryProvider.class */
public interface Stax2InputFactoryProvider {
    public static final String OSGI_SVC_PROP_IMPL_NAME = "org.codehaus.stax2.implName";
    public static final String OSGI_SVC_PROP_IMPL_VERSION = "org.codehaus.stax2.implVersion";

    XMLInputFactory2 createInputFactory();
}
