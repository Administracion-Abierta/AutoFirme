package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/PatternFacet.class */
public final class PatternFacet extends DataTypeWithLexicalConstraintFacet {
    private transient RegExp[] exps;
    public final String[] patterns;
    private static final long serialVersionUID = 1;

    public RegExp[] getRegExps() {
        return this.exps;
    }

    public PatternFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_PATTERN, facets.isFixed(XSDatatype.FACET_PATTERN));
        Vector regExps = facets.getVector(XSDatatype.FACET_PATTERN);
        this.patterns = (String[]) regExps.toArray(new String[regExps.size()]);
        try {
            compileRegExps();
        } catch (ParseException pe) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_PARSE_ERROR, pe.getMessage()));
        }
    }

    private void compileRegExps() throws ParseException {
        this.exps = new RegExp[this.patterns.length];
        RegExpFactory factory = RegExpFactory.createFactory();
        for (int i = 0; i < this.exps.length; i++) {
            this.exps[i] = factory.compile(this.patterns[i]);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if (checkLexicalConstraint(content)) {
            return;
        }
        if (this.exps.length == 1) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_PATTERN_1, this.patterns[0]));
        }
        throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_PATTERN_MANY));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithLexicalConstraintFacet
    protected final boolean checkLexicalConstraint(String literal) {
        synchronized (this) {
            for (int i = 0; i < this.exps.length; i++) {
                if (this.exps[i].matches(literal)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        try {
            compileRegExps();
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}
