package com.ctc.wstx.stax;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.evt.SimpleStartElement;
import com.ctc.wstx.evt.WDTD;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.Stax2EventFactoryImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/stax/WstxEventFactory.class */
@ServiceProvider(XMLEventFactory.class)
public final class WstxEventFactory extends Stax2EventFactoryImpl {
    @Override // org.codehaus.stax2.ri.Stax2EventFactoryImpl
    public DTD createDTD(String dtd) {
        return new WDTD(this.mLocation, dtd);
    }

    @Override // org.codehaus.stax2.ri.Stax2EventFactoryImpl
    protected QName createQName(String nsURI, String localName) {
        return new QName(nsURI, localName);
    }

    @Override // org.codehaus.stax2.ri.Stax2EventFactoryImpl
    protected QName createQName(String nsURI, String localName, String prefix) {
        return QNameCreator.create(nsURI, localName, prefix);
    }

    @Override // org.codehaus.stax2.ri.Stax2EventFactoryImpl
    protected StartElement createStartElement(QName name, Iterator<?> attr, Iterator<?> ns, NamespaceContext ctxt) {
        return SimpleStartElement.construct(this.mLocation, name, (Iterator<Attribute>) attr, (Iterator<Namespace>) ns, ctxt);
    }
}
