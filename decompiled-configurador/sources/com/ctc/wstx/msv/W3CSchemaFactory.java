package com.ctc.wstx.msv;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/msv/W3CSchemaFactory.class */
@ServiceProvider(XMLValidationSchemaFactory.class)
public class W3CSchemaFactory extends BaseSchemaFactory {
    protected final GrammarReaderController mDummyController;

    public W3CSchemaFactory() {
        super("http://www.w3.org/2001/XMLSchema");
        this.mDummyController = new IgnoreController();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.msv.BaseSchemaFactory
    protected XMLValidationSchema loadSchema(InputSource src, Object sysRef) throws XMLStreamException {
        SAXParserFactory saxFactory = getSaxFactory();
        BaseSchemaFactory.MyGrammarController ctrl = new BaseSchemaFactory.MyGrammarController();
        XMLSchemaGrammar grammar = XMLSchemaReader.parse(src, saxFactory, ctrl);
        if (grammar == null) {
            String msg = "Failed to load W3C Schema from '" + sysRef + "'";
            String emsg = ctrl.mErrorMsg;
            if (emsg != null) {
                msg = msg + ": " + emsg;
            }
            throw new XMLStreamException(msg);
        }
        return new W3CSchema(grammar);
    }
}
