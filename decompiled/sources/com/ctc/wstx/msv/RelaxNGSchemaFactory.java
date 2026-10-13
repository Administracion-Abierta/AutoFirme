package com.ctc.wstx.msv;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/msv/RelaxNGSchemaFactory.class */
@ServiceProvider(XMLValidationSchemaFactory.class)
public class RelaxNGSchemaFactory extends BaseSchemaFactory {
    protected final GrammarReaderController mDummyController;

    public RelaxNGSchemaFactory() {
        super(XMLValidationSchema.SCHEMA_ID_RELAXNG);
        this.mDummyController = new IgnoreController();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.msv.BaseSchemaFactory
    protected XMLValidationSchema loadSchema(InputSource src, Object sysRef) throws XMLStreamException {
        SAXParserFactory saxFactory = getSaxFactory();
        BaseSchemaFactory.MyGrammarController ctrl = new BaseSchemaFactory.MyGrammarController();
        TREXGrammar grammar = RELAXNGReader.parse(src, saxFactory, ctrl);
        if (grammar == null) {
            String msg = "Failed to load RelaxNG schema from '" + sysRef + OperatorName.SHOW_TEXT_LINE;
            String emsg = ctrl.mErrorMsg;
            if (emsg != null) {
                msg = msg + ": " + emsg;
            }
            throw new XMLStreamException(msg);
        }
        return new RelaxNGSchema(grammar);
    }
}
