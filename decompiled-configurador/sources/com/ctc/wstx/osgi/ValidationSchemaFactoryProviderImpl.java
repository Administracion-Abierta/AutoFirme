package com.ctc.wstx.osgi;

import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.dtd.DTDSchemaFactory;
import com.ctc.wstx.msv.RelaxNGSchemaFactory;
import com.ctc.wstx.msv.W3CSchemaFactory;
import java.util.Properties;
import org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/osgi/ValidationSchemaFactoryProviderImpl.class */
public abstract class ValidationSchemaFactoryProviderImpl implements Stax2ValidationSchemaFactoryProvider {
    final String mSchemaType;

    @Override // org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider
    public abstract XMLValidationSchemaFactory createValidationSchemaFactory();

    protected ValidationSchemaFactoryProviderImpl(String st) {
        this.mSchemaType = st;
    }

    public static ValidationSchemaFactoryProviderImpl[] createAll() {
        return new ValidationSchemaFactoryProviderImpl[]{new DTD(), new RelaxNG(), new W3CSchema()};
    }

    @Override // org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider
    public String getSchemaType() {
        return this.mSchemaType;
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("org.codehaus.stax2.implName", ValidatorConfig.getImplName());
        props.setProperty("org.codehaus.stax2.implVersion", ValidatorConfig.getImplVersion());
        props.setProperty(Stax2ValidationSchemaFactoryProvider.OSGI_SVC_PROP_SCHEMA_TYPE, this.mSchemaType);
        return props;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/osgi/ValidationSchemaFactoryProviderImpl$DTD.class */
    static final class DTD extends ValidationSchemaFactoryProviderImpl {
        DTD() {
            super("http://www.w3.org/XML/1998/namespace");
        }

        @Override // com.ctc.wstx.osgi.ValidationSchemaFactoryProviderImpl, org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider
        public XMLValidationSchemaFactory createValidationSchemaFactory() {
            return new DTDSchemaFactory();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/osgi/ValidationSchemaFactoryProviderImpl$RelaxNG.class */
    static final class RelaxNG extends ValidationSchemaFactoryProviderImpl {
        RelaxNG() {
            super(XMLValidationSchema.SCHEMA_ID_RELAXNG);
        }

        @Override // com.ctc.wstx.osgi.ValidationSchemaFactoryProviderImpl, org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider
        public XMLValidationSchemaFactory createValidationSchemaFactory() {
            return new RelaxNGSchemaFactory();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/osgi/ValidationSchemaFactoryProviderImpl$W3CSchema.class */
    static final class W3CSchema extends ValidationSchemaFactoryProviderImpl {
        W3CSchema() {
            super("http://www.w3.org/2001/XMLSchema");
        }

        @Override // com.ctc.wstx.osgi.ValidationSchemaFactoryProviderImpl, org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider
        public XMLValidationSchemaFactory createValidationSchemaFactory() {
            return new W3CSchemaFactory();
        }
    }
}
