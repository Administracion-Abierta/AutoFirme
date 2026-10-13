package com.ctc.wstx.msv;

import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/msv/BaseSchemaFactory.class */
public abstract class BaseSchemaFactory extends XMLValidationSchemaFactory {
    protected static SAXParserFactory sSaxFactory;
    protected final ValidatorConfig mConfig;

    protected abstract XMLValidationSchema loadSchema(InputSource inputSource, Object obj) throws XMLStreamException;

    protected BaseSchemaFactory(String schemaType) {
        super(schemaType);
        this.mConfig = ValidatorConfig.createDefaults();
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public boolean isPropertySupported(String propName) {
        return this.mConfig.isPropertySupported(propName);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public boolean setProperty(String propName, Object value) {
        return this.mConfig.setProperty(propName, value);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public Object getProperty(String propName) {
        return this.mConfig.getProperty(propName);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(InputStream in, String encoding, String publicId, String systemId) throws XMLStreamException {
        InputSource src = new InputSource(in);
        src.setEncoding(encoding);
        src.setPublicId(publicId);
        src.setSystemId(systemId);
        return loadSchema(src, systemId);
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(Reader r, String publicId, String systemId) throws XMLStreamException {
        InputSource src = new InputSource(r);
        src.setPublicId(publicId);
        src.setSystemId(systemId);
        return loadSchema(src, systemId);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(URL url) throws XMLStreamException {
        try {
            InputStream in = URLUtil.inputStreamFromURL(url);
            InputSource src = new InputSource(in);
            src.setSystemId(url.toExternalForm());
            return loadSchema(src, url);
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidationSchemaFactory
    public XMLValidationSchema createSchema(File f) throws XMLStreamException {
        try {
            return createSchema(f.toURL());
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    protected static synchronized SAXParserFactory getSaxFactory() {
        if (sSaxFactory == null) {
            sSaxFactory = SAXParserFactory.newInstance();
            sSaxFactory.setNamespaceAware(true);
        }
        return sSaxFactory;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/msv/BaseSchemaFactory$MyGrammarController.class */
    static final class MyGrammarController extends IgnoreController {
        public String mErrorMsg = null;

        @Override // com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController, com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
        public void error(Locator[] locs, String msg, Exception nestedException) {
            if (this.mErrorMsg == null) {
                this.mErrorMsg = msg;
            } else {
                this.mErrorMsg += "; " + msg;
            }
        }
    }
}
