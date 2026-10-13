package com.ctc.wstx.dtd;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDSchemaFactory.class */
@ServiceProvider(XMLValidationSchemaFactory.class)
public class DTDSchemaFactory extends XMLValidationSchemaFactory {
    static final SymbolTable mRootSymbols = DefaultXmlSymbolTable.getInstance();
    protected final ValidatorConfig mSchemaConfig;
    protected final ReaderConfig mReaderConfig;

    static {
        mRootSymbols.setInternStrings(true);
    }

    public DTDSchemaFactory() {
        super("http://www.w3.org/XML/1998/namespace");
        this.mReaderConfig = ReaderConfig.createFullDefaults();
        this.mSchemaConfig = ValidatorConfig.createDefaults();
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public boolean isPropertySupported(String propName) {
        return this.mSchemaConfig.isPropertySupported(propName);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public boolean setProperty(String propName, Object value) {
        return this.mSchemaConfig.setProperty(propName, value);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public Object getProperty(String propName) {
        return this.mSchemaConfig.getProperty(propName);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(InputStream in, String encoding, String publicId, String systemId) throws XMLStreamException {
        ReaderConfig rcfg = createPrivateReaderConfig();
        return doCreateSchema(rcfg, StreamBootstrapper.getInstance(publicId, SystemId.construct(systemId), in), publicId, systemId, null);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(Reader r, String publicId, String systemId) throws XMLStreamException {
        ReaderConfig rcfg = createPrivateReaderConfig();
        return doCreateSchema(rcfg, ReaderBootstrapper.getInstance(publicId, SystemId.construct(systemId), r, null), publicId, systemId, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(URL url) throws XMLStreamException {
        ReaderConfig rcfg = createPrivateReaderConfig();
        try {
            InputStream in = URLUtil.inputStreamFromURL(url);
            return doCreateSchema(rcfg, StreamBootstrapper.getInstance(null, null, in), null, url.toExternalForm(), url);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(File f) throws XMLStreamException {
        ReaderConfig rcfg = createPrivateReaderConfig();
        try {
            URL url = URLUtil.toURL(f);
            return doCreateSchema(rcfg, StreamBootstrapper.getInstance(null, null, new FileInputStream(f)), null, url.toExternalForm(), url);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected XMLValidationSchema doCreateSchema(ReaderConfig rcfg, InputBootstrapper bs, String publicId, String systemIdStr, URL ctxt) throws XMLStreamException {
        try {
            Reader r = bs.bootstrapInput(rcfg, false, 0);
            if (bs.declaredXml11()) {
                rcfg.enableXml11(true);
            }
            if (ctxt == null) {
                ctxt = URLUtil.urlFromCurrentDir();
            }
            SystemId systemId = SystemId.construct(systemIdStr, ctxt);
            WstxInputSource src = InputSourceFactory.constructEntitySource(rcfg, null, null, bs, publicId, systemId, 0, r);
            return FullDTDReader.readExternalSubset(src, rcfg, null, true, bs.getDeclaredVersion());
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    private ReaderConfig createPrivateReaderConfig() {
        return this.mReaderConfig.createNonShared(mRootSymbols.makeChild());
    }
}
