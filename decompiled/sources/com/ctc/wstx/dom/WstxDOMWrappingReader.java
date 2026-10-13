package com.ctc.wstx.dom;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.exc.WstxParsingException;
import java.util.Collections;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import org.codehaus.stax2.ri.dom.DOMWrappingReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dom/WstxDOMWrappingReader.class */
public class WstxDOMWrappingReader extends DOMWrappingReader {
    protected final ReaderConfig mConfig;

    protected WstxDOMWrappingReader(DOMSource src, ReaderConfig cfg) throws XMLStreamException {
        super(src, cfg.willSupportNamespaces(), cfg.willCoalesceText());
        this.mConfig = cfg;
        if (cfg.hasInternNamesBeenEnabled()) {
            setInternNames(true);
        }
        if (cfg.hasInternNsURIsBeenEnabled()) {
            setInternNsURIs(true);
        }
    }

    public static WstxDOMWrappingReader createFrom(DOMSource src, ReaderConfig cfg) throws XMLStreamException {
        return new WstxDOMWrappingReader(src, cfg);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingReader, org.codehaus.stax2.XMLStreamReader2
    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingReader
    public Object getProperty(String name) {
        if (name.equals("javax.xml.stream.entities")) {
            return Collections.EMPTY_LIST;
        }
        if (name.equals("javax.xml.stream.notations")) {
            return Collections.EMPTY_LIST;
        }
        return this.mConfig.getProperty(name);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingReader, org.codehaus.stax2.XMLStreamReader2
    public boolean setProperty(String name, Object value) {
        return this.mConfig.setProperty(name, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingReader
    protected void throwStreamException(String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            throw new WstxParsingException(msg);
        }
        throw new WstxParsingException(msg, loc);
    }
}
