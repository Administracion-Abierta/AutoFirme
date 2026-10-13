package com.ctc.wstx.stax;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.OutputConfigFlags;
import com.ctc.wstx.dom.WstxDOMWrappingWriter;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.io.UTF8Writer;
import com.ctc.wstx.sw.AsciiXmlWriter;
import com.ctc.wstx.sw.BufferingXmlWriter;
import com.ctc.wstx.sw.ISOLatin1XmlWriter;
import com.ctc.wstx.sw.NonNsStreamWriter;
import com.ctc.wstx.sw.RepairingNsStreamWriter;
import com.ctc.wstx.sw.SimpleNsStreamWriter;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.URLUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.io.Stax2Result;
import org.codehaus.stax2.ri.Stax2EventWriterImpl;
import org.codehaus.stax2.ri.Stax2WriterAdapter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/stax/WstxOutputFactory.class */
@ServiceProvider(XMLOutputFactory.class)
public class WstxOutputFactory extends XMLOutputFactory2 implements OutputConfigFlags {
    protected final WriterConfig mConfig = WriterConfig.createFullDefaults();

    public XMLEventWriter createXMLEventWriter(OutputStream out) throws XMLStreamException {
        return createXMLEventWriter(out, (String) null);
    }

    public XMLEventWriter createXMLEventWriter(OutputStream out, String enc) throws XMLStreamException {
        if (out == null) {
            throw new IllegalArgumentException("Null OutputStream is not a valid argument");
        }
        return new Stax2EventWriterImpl(createSW(out, null, enc, false));
    }

    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return new Stax2EventWriterImpl(createSW(result));
    }

    public XMLEventWriter createXMLEventWriter(Writer w) throws XMLStreamException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer is not a valid argument");
        }
        return new Stax2EventWriterImpl(createSW(null, w, null, false));
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
        return createXMLStreamWriter(out, (String) null);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream out, String enc) throws XMLStreamException {
        if (out == null) {
            throw new IllegalArgumentException("Null OutputStream is not a valid argument");
        }
        return createSW(out, null, enc, false);
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return createSW(result);
    }

    public XMLStreamWriter createXMLStreamWriter(Writer w) throws XMLStreamException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer is not a valid argument");
        }
        return createSW(null, w, null, false);
    }

    public Object getProperty(String name) {
        return this.mConfig.getProperty(name);
    }

    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    public void setProperty(String name, Object value) {
        this.mConfig.setProperty(name, value);
    }

    @Override // org.codehaus.stax2.XMLOutputFactory2
    public XMLEventWriter createXMLEventWriter(Writer w, String enc) throws XMLStreamException {
        return new Stax2EventWriterImpl(createSW(null, w, enc, false));
    }

    @Override // org.codehaus.stax2.XMLOutputFactory2
    public XMLEventWriter createXMLEventWriter(XMLStreamWriter sw) throws XMLStreamException {
        XMLStreamWriter2 sw2 = Stax2WriterAdapter.wrapIfNecessary(sw);
        return new Stax2EventWriterImpl(sw2);
    }

    @Override // org.codehaus.stax2.XMLOutputFactory2
    public XMLStreamWriter2 createXMLStreamWriter(Writer w, String enc) throws XMLStreamException {
        return createSW(null, w, enc, false);
    }

    @Override // org.codehaus.stax2.XMLOutputFactory2
    public void configureForXmlConformance() {
        this.mConfig.configureForXmlConformance();
    }

    @Override // org.codehaus.stax2.XMLOutputFactory2
    public void configureForRobustness() {
        this.mConfig.configureForRobustness();
    }

    @Override // org.codehaus.stax2.XMLOutputFactory2
    public void configureForSpeed() {
        this.mConfig.configureForSpeed();
    }

    public WriterConfig getConfig() {
        return this.mConfig;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private XMLStreamWriter2 createSW(OutputStream out, Writer w, String enc, boolean requireAutoClose) throws XMLStreamException {
        XmlWriter xw;
        WriterConfig cfg = this.mConfig.createNonShared();
        boolean autoCloseOutput = requireAutoClose || this.mConfig.willAutoCloseOutput();
        if (w == null) {
            if (enc == null) {
                enc = "UTF-8";
            } else if (enc != "UTF-8" && enc != "ISO-8859-1" && enc != CharsetNames.CS_US_ASCII) {
                enc = CharsetNames.normalize(enc);
            }
            try {
                if (enc == "UTF-8") {
                    xw = new BufferingXmlWriter(new UTF8Writer(cfg, out, autoCloseOutput), cfg, enc, autoCloseOutput, out, 16);
                } else if (enc == "ISO-8859-1") {
                    xw = new ISOLatin1XmlWriter(out, cfg, autoCloseOutput);
                } else if (enc == CharsetNames.CS_US_ASCII) {
                    xw = new AsciiXmlWriter(out, cfg, autoCloseOutput);
                } else {
                    xw = new BufferingXmlWriter(new OutputStreamWriter(out, enc), cfg, enc, autoCloseOutput, out, -1);
                }
            } catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        } else {
            if (enc == null) {
                enc = CharsetNames.findEncodingFor(w);
            }
            try {
                xw = new BufferingXmlWriter(w, cfg, enc, autoCloseOutput, null, -1);
            } catch (IOException ex2) {
                throw new XMLStreamException(ex2);
            }
        }
        return createSW(enc, cfg, xw);
    }

    protected XMLStreamWriter2 createSW(String enc, WriterConfig cfg, XmlWriter xw) {
        if (cfg.willSupportNamespaces()) {
            if (cfg.automaticNamespacesEnabled()) {
                return new RepairingNsStreamWriter(xw, enc, cfg);
            }
            return new SimpleNsStreamWriter(xw, enc, cfg);
        }
        return new NonNsStreamWriter(xw, enc, cfg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private XMLStreamWriter2 createSW(Result res) throws XMLStreamException {
        boolean requireAutoClose;
        OutputStream out = null;
        Writer w = null;
        String sysId = null;
        if (res instanceof Stax2Result) {
            Stax2Result sr = (Stax2Result) res;
            try {
                out = sr.constructOutputStream();
                if (out == null) {
                    w = sr.constructWriter();
                }
                requireAutoClose = true;
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        } else if (res instanceof StreamResult) {
            StreamResult sr2 = (StreamResult) res;
            out = sr2.getOutputStream();
            sysId = sr2.getSystemId();
            if (out == null) {
                w = sr2.getWriter();
            }
            requireAutoClose = false;
        } else {
            if (!(res instanceof SAXResult)) {
                if (res instanceof DOMResult) {
                    return WstxDOMWrappingWriter.createFrom(this.mConfig.createNonShared(), (DOMResult) res);
                }
                throw new IllegalArgumentException("Can not instantiate a writer for XML result type " + res.getClass() + " (unrecognized type)");
            }
            sysId = ((SAXResult) res).getSystemId();
            if (sysId == null || sysId.length() == 0) {
                throw new XMLStreamException("Can not create a stream writer for a SAXResult that does not have System Id (support for using SAX input source not implemented)");
            }
            requireAutoClose = true;
        }
        if (out != null) {
            return createSW(out, null, null, requireAutoClose);
        }
        if (w != null) {
            return createSW(null, w, null, requireAutoClose);
        }
        if (sysId != null && sysId.length() > 0) {
            try {
                OutputStream out2 = URLUtil.outputStreamFromURL(URLUtil.urlFromSystemId(sysId));
                return createSW(out2, null, null, true);
            } catch (IOException ioe2) {
                throw new WstxIOException(ioe2);
            }
        }
        throw new XMLStreamException("Can not create Stax writer for passed-in Result -- neither writer, output stream or system id was accessible");
    }
}
