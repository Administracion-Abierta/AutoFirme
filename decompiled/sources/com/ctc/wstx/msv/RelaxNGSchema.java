package com.ctc.wstx.msv;

import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/msv/RelaxNGSchema.class */
public class RelaxNGSchema implements XMLValidationSchema {
    protected final TREXGrammar mGrammar;

    public RelaxNGSchema(TREXGrammar grammar) {
        this.mGrammar = grammar;
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchema
    public String getSchemaType() {
        return XMLValidationSchema.SCHEMA_ID_RELAXNG;
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchema
    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        REDocumentDeclaration dd = new REDocumentDeclaration(this.mGrammar);
        return new GenericMsvValidator(this, ctxt, dd);
    }
}
