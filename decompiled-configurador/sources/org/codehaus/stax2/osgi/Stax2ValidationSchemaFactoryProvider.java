package org.codehaus.stax2.osgi;

import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/osgi/Stax2ValidationSchemaFactoryProvider.class */
public interface Stax2ValidationSchemaFactoryProvider {
    public static final String OSGI_SVC_PROP_IMPL_NAME = "org.codehaus.stax2.implName";
    public static final String OSGI_SVC_PROP_IMPL_VERSION = "org.codehaus.stax2.implVersion";
    public static final String OSGI_SVC_PROP_SCHEMA_TYPE = "org.codehaus.stax2.validation.schemaType";

    String getSchemaType();

    XMLValidationSchemaFactory createValidationSchemaFactory();
}
