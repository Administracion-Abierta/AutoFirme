package com.ctc.wstx.msv;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.XSREDocDecl;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/msv/W3CSchema.class */
public class W3CSchema implements XMLValidationSchema {
    protected final XMLSchemaGrammar mGrammar;

    public W3CSchema(XMLSchemaGrammar grammar) {
        this.mGrammar = grammar;
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchema
    public String getSchemaType() {
        return "http://www.w3.org/2001/XMLSchema";
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchema
    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        XSREDocDecl dd = new XSREDocDecl(this.mGrammar);
        return new GenericMsvValidator(this, ctxt, dd);
    }
}
