package com.ctc.wstx.osgi;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.stax.WstxInputFactory;
import java.util.Properties;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/osgi/InputFactoryProviderImpl.class */
public class InputFactoryProviderImpl implements Stax2InputFactoryProvider {
    @Override // org.codehaus.stax2.osgi.Stax2InputFactoryProvider
    public XMLInputFactory2 createInputFactory() {
        return new WstxInputFactory();
    }

    protected Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("org.codehaus.stax2.implName", ReaderConfig.getImplName());
        props.setProperty("org.codehaus.stax2.implVersion", ReaderConfig.getImplVersion());
        return props;
    }
}
