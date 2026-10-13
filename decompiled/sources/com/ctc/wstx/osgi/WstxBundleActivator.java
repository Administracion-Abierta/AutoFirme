package com.ctc.wstx.osgi;

import java.util.Dictionary;
import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.codehaus.stax2.osgi.Stax2OutputFactoryProvider;
import org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/osgi/WstxBundleActivator.class */
public class WstxBundleActivator implements BundleActivator {
    public void start(BundleContext ctxt) {
        InputFactoryProviderImpl inputP = new InputFactoryProviderImpl();
        Dictionary inputProps = inputP.getProperties();
        ctxt.registerService(Stax2InputFactoryProvider.class.getName(), inputP, inputProps);
        OutputFactoryProviderImpl outputP = new OutputFactoryProviderImpl();
        Dictionary outputProps = outputP.getProperties();
        ctxt.registerService(Stax2OutputFactoryProvider.class.getName(), outputP, outputProps);
        ValidationSchemaFactoryProviderImpl[] impls = ValidationSchemaFactoryProviderImpl.createAll();
        for (ValidationSchemaFactoryProviderImpl impl : impls) {
            Dictionary implProps = impl.getProperties();
            ctxt.registerService(Stax2ValidationSchemaFactoryProvider.class.getName(), impl, implProps);
        }
    }

    public void stop(BundleContext ctxt) {
    }
}
