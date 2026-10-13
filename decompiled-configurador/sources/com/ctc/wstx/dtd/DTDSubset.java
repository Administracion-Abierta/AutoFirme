package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDSubset.class */
public abstract class DTDSubset implements DTDValidationSchema {
    public abstract DTDSubset combineWithExternalSubset(InputProblemReporter inputProblemReporter, DTDSubset dTDSubset) throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.XMLValidationSchema
    public abstract XMLValidator createValidator(ValidationContext validationContext) throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.DTDValidationSchema
    public abstract int getEntityCount();

    @Override // org.codehaus.stax2.validation.DTDValidationSchema
    public abstract int getNotationCount();

    public abstract boolean isCachable();

    public abstract boolean isReusableWith(DTDSubset dTDSubset);

    public abstract HashMap<String, EntityDecl> getGeneralEntityMap();

    public abstract List<EntityDecl> getGeneralEntityList();

    public abstract HashMap<String, EntityDecl> getParameterEntityMap();

    public abstract HashMap<String, NotationDeclaration> getNotationMap();

    public abstract List<NotationDeclaration> getNotationList();

    public abstract HashMap<PrefixedName, DTDElement> getElementMap();

    protected DTDSubset() {
    }

    @Override // org.codehaus.stax2.validation.XMLValidationSchema
    public String getSchemaType() {
        return "http://www.w3.org/XML/1998/namespace";
    }
}
