package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/DefaultInputResolver.class */
public final class DefaultInputResolver {
    private DefaultInputResolver() {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static WstxInputSource resolveEntity(WstxInputSource parent, URL pathCtxt, String entityName, String publicId, String systemId, XMLResolver customResolver, ReaderConfig cfg, int xmlVersion) throws XMLStreamException, IOException {
        Object source;
        if (pathCtxt == null) {
            pathCtxt = parent.getSource();
            if (pathCtxt == null) {
                pathCtxt = URLUtil.urlFromCurrentDir();
            }
        }
        if (customResolver != null && (source = customResolver.resolveEntity(publicId, systemId, pathCtxt.toExternalForm(), entityName)) != null) {
            return sourceFrom(parent, cfg, entityName, xmlVersion, source);
        }
        if (systemId == null) {
            throw new XMLStreamException("Can not resolve " + (entityName == null ? "[External DTD subset]" : "entity '" + entityName + OperatorName.SHOW_TEXT_LINE) + " without a system id (public id '" + publicId + "')");
        }
        URL url = URLUtil.urlFromSystemId(systemId, pathCtxt);
        return sourceFromURL(parent, cfg, entityName, xmlVersion, url, publicId);
    }

    public static WstxInputSource resolveEntityUsing(WstxInputSource refCtxt, String entityName, String publicId, String systemId, XMLResolver resolver, ReaderConfig cfg, int xmlVersion) throws XMLStreamException, IOException {
        URL ctxt = refCtxt == null ? null : refCtxt.getSource();
        if (ctxt == null) {
            ctxt = URLUtil.urlFromCurrentDir();
        }
        Object source = resolver.resolveEntity(publicId, systemId, ctxt.toExternalForm(), entityName);
        if (source == null) {
            return null;
        }
        return sourceFrom(refCtxt, cfg, entityName, xmlVersion, source);
    }

    protected static WstxInputSource sourceFrom(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, Object o) throws XMLStreamException, IOException, IllegalArgumentException {
        if (o instanceof Source) {
            if (o instanceof StreamSource) {
                return sourceFromSS(parent, cfg, refName, xmlVersion, (StreamSource) o);
            }
            throw new IllegalArgumentException("Can not use other Source objects than StreamSource: got " + o.getClass());
        }
        if (o instanceof URL) {
            return sourceFromURL(parent, cfg, refName, xmlVersion, (URL) o, null);
        }
        if (o instanceof InputStream) {
            return sourceFromIS(parent, cfg, refName, xmlVersion, (InputStream) o, null, null);
        }
        if (o instanceof Reader) {
            return sourceFromR(parent, cfg, refName, xmlVersion, (Reader) o, null, null);
        }
        if (o instanceof String) {
            return sourceFromString(parent, cfg, refName, xmlVersion, (String) o);
        }
        if (o instanceof File) {
            URL u = URLUtil.toURL((File) o);
            return sourceFromURL(parent, cfg, refName, xmlVersion, u, null);
        }
        throw new IllegalArgumentException("Unrecognized input argument type for sourceFrom(): " + o.getClass());
    }

    public static Reader constructOptimizedReader(ReaderConfig cfg, InputStream in, boolean isXml11, String encoding) throws XMLStreamException {
        BaseReader r;
        int inputBufLen = cfg.getInputBufferLength();
        String normEnc = CharsetNames.normalize(encoding);
        if (normEnc == "UTF-8") {
            r = new UTF8Reader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, true);
        } else if (normEnc == "ISO-8859-1") {
            r = new ISOLatinReader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, true);
        } else if (normEnc == CharsetNames.CS_US_ASCII) {
            r = new AsciiReader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, true);
        } else if (normEnc.startsWith(CharsetNames.CS_UTF32)) {
            boolean isBE = normEnc == CharsetNames.CS_UTF32BE;
            r = new UTF32Reader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, true, isBE);
        } else {
            try {
                return new InputStreamReader(in, encoding);
            } catch (UnsupportedEncodingException ex) {
                throw new XMLStreamException("[unsupported encoding]: " + ex);
            }
        }
        if (isXml11) {
            r.setXmlCompliancy(272);
        }
        return r;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private static WstxInputSource sourceFromSS(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, StreamSource ssrc) throws XMLStreamException, IOException {
        InputBootstrapper bs;
        Reader r = ssrc.getReader();
        String pubId = ssrc.getPublicId();
        String sysId0 = ssrc.getSystemId();
        URL ctxt = parent == null ? null : parent.getSource();
        URL url = (sysId0 == null || sysId0.length() == 0) ? null : URLUtil.urlFromSystemId(sysId0, ctxt);
        SystemId systemId = SystemId.construct(sysId0, url == null ? ctxt : url);
        if (r == null) {
            InputStream in = ssrc.getInputStream();
            if (in == null) {
                if (url == null) {
                    throw new IllegalArgumentException("Can not create Stax reader for a StreamSource -- neither reader, input stream nor system id was set.");
                }
                in = URLUtil.inputStreamFromURL(url);
            }
            bs = StreamBootstrapper.getInstance(pubId, systemId, in);
        } else {
            bs = ReaderBootstrapper.getInstance(pubId, systemId, r, null);
        }
        Reader r2 = bs.bootstrapInput(cfg, false, xmlVersion);
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, systemId, xmlVersion, r2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private static WstxInputSource sourceFromURL(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, URL url, String pubId) throws XMLStreamException, IOException {
        InputStream in = URLUtil.inputStreamFromURL(url);
        SystemId sysId = SystemId.construct(url);
        StreamBootstrapper bs = StreamBootstrapper.getInstance(pubId, sysId, in);
        Reader r = bs.bootstrapInput(cfg, false, xmlVersion);
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, sysId, xmlVersion, r);
    }

    public static WstxInputSource sourceFromString(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, String refContent) throws XMLStreamException, IOException {
        return sourceFromR(parent, cfg, refName, xmlVersion, new StringReader(refContent), null, refName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private static WstxInputSource sourceFromIS(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, InputStream is, String pubId, String sysId) throws XMLStreamException, IOException {
        StreamBootstrapper bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), is);
        Reader r = bs.bootstrapInput(cfg, false, xmlVersion);
        URL ctxt = parent.getSource();
        if (sysId != null && sysId.length() > 0) {
            ctxt = URLUtil.urlFromSystemId(sysId, ctxt);
        }
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, SystemId.construct(sysId, ctxt), xmlVersion, r);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private static WstxInputSource sourceFromR(WstxInputSource parent, ReaderConfig cfg, String refName, int xmlVersion, Reader r, String pubId, String sysId) throws XMLStreamException, IOException {
        ReaderBootstrapper rbs = ReaderBootstrapper.getInstance(pubId, SystemId.construct(sysId), r, null);
        Reader r2 = rbs.bootstrapInput(cfg, false, xmlVersion);
        URL ctxt = parent == null ? null : parent.getSource();
        if (sysId != null && sysId.length() > 0) {
            ctxt = URLUtil.urlFromSystemId(sysId, ctxt);
        }
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, rbs, pubId, SystemId.construct(sysId, ctxt), xmlVersion, r2);
    }
}
