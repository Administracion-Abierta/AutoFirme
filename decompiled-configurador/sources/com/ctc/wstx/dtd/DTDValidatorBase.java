package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.NsDefaultProvider;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDValidatorBase.class */
public abstract class DTDValidatorBase extends XMLValidator implements NsDefaultProvider {
    static final int DEFAULT_STACK_SIZE = 16;
    static final int EXP_MAX_ATTRS = 16;
    final boolean mHasNsDefaults;
    final DTDSubset mSchema;
    final ValidationContext mContext;
    final Map<PrefixedName, DTDElement> mElemSpecs;
    final Map<String, EntityDecl> mGeneralEntities;
    protected boolean mNormAttrs;
    protected DTDElement[] mElems;
    protected static final HashMap<PrefixedName, DTDAttribute> NO_ATTRS = new HashMap<>();
    protected static final HashMap<String, EntityDecl> EMPTY_MAP = new HashMap<>();
    protected DTDElement mCurrElem = null;
    protected int mElemCount = 0;
    protected HashMap<PrefixedName, DTDAttribute> mCurrAttrDefs = null;
    protected DTDAttribute[] mAttrSpecs = new DTDAttribute[16];
    protected int mAttrCount = 0;
    protected int mIdAttrIndex = -1;
    protected final transient PrefixedName mTmpKey = new PrefixedName(null, null);
    char[] mTmpAttrValueBuffer = null;

    public abstract boolean reallyValidating();

    @Override // org.codehaus.stax2.validation.XMLValidator
    public abstract void validateElementStart(String str, String str2, String str3) throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.XMLValidator
    public abstract String validateAttribute(String str, String str2, String str3, String str4) throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.XMLValidator
    public abstract String validateAttribute(String str, String str2, String str3, char[] cArr, int i, int i2) throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.XMLValidator
    public abstract int validateElementAndAttributes() throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.XMLValidator
    public abstract int validateElementEnd(String str, String str2, String str3) throws XMLStreamException;

    @Override // org.codehaus.stax2.validation.XMLValidator
    public abstract void validationCompleted(boolean z) throws XMLStreamException;

    protected abstract ElementIdMap getIdMap();

    public DTDValidatorBase(DTDSubset schema, ValidationContext ctxt, boolean hasNsDefaults, Map<PrefixedName, DTDElement> elemSpecs, Map<String, EntityDecl> genEntities) {
        this.mElems = null;
        this.mSchema = schema;
        this.mContext = ctxt;
        this.mHasNsDefaults = hasNsDefaults;
        if (elemSpecs == null || elemSpecs.size() == 0) {
            this.mElemSpecs = Collections.emptyMap();
        } else {
            this.mElemSpecs = elemSpecs;
        }
        this.mGeneralEntities = genEntities;
        this.mNormAttrs = true;
        this.mElems = new DTDElement[16];
    }

    public void setAttrValueNormalization(boolean state) {
        this.mNormAttrs = state;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public final XMLValidationSchema getSchema() {
        return this.mSchema;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public String getAttributeType(int index) {
        DTDAttribute attr = this.mAttrSpecs[index];
        return attr == null ? "CDATA" : attr.getValueTypeString();
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public int getIdAttrIndex() {
        DTDAttribute idAttr;
        int ix = this.mIdAttrIndex;
        if (ix == -2) {
            ix = -1;
            if (this.mCurrElem != null && (idAttr = this.mCurrElem.getIdAttribute()) != null) {
                DTDAttribute[] attrs = this.mAttrSpecs;
                int len = attrs.length;
                for (int i = 0; i < len; i++) {
                    if (attrs[i] == idAttr) {
                        ix = i;
                        break;
                    }
                }
            }
            this.mIdAttrIndex = ix;
        }
        return ix;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public int getNotationAttrIndex() {
        int len = this.mAttrCount;
        for (int i = 0; i < len; i++) {
            if (this.mAttrSpecs[i].typeIsNotation()) {
                return i;
            }
        }
        return -1;
    }

    @Override // com.ctc.wstx.sr.NsDefaultProvider
    public boolean mayHaveNsDefaults(String elemPrefix, String elemLN) {
        this.mTmpKey.reset(elemPrefix, elemLN);
        DTDElement elem = this.mElemSpecs.get(this.mTmpKey);
        this.mCurrElem = elem;
        return elem != null && elem.hasNsDefaults();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.NsDefaultProvider
    public void checkNsDefaults(InputElementStack nsStack) throws XMLStreamException {
        HashMap<String, DTDAttribute> m = this.mCurrElem.getNsDefaults();
        if (m != null) {
            for (Map.Entry<String, DTDAttribute> me : m.entrySet()) {
                String prefix = me.getKey();
                if (!nsStack.isPrefixLocallyDeclared(prefix)) {
                    DTDAttribute attr = me.getValue();
                    String uri = attr.getDefaultValue(this.mContext, this);
                    nsStack.addNsBinding(prefix, uri);
                }
            }
        }
    }

    PrefixedName getElemName() {
        DTDElement elem = this.mElems[this.mElemCount - 1];
        return elem.getName();
    }

    Location getLocation() {
        return this.mContext.getValidationLocation();
    }

    Map<String, EntityDecl> getEntityMap() {
        return this.mGeneralEntities;
    }

    char[] getTempAttrValueBuffer(int neededLength) {
        if (this.mTmpAttrValueBuffer == null || this.mTmpAttrValueBuffer.length < neededLength) {
            int size = neededLength < 100 ? 100 : neededLength;
            this.mTmpAttrValueBuffer = new char[size];
        }
        return this.mTmpAttrValueBuffer;
    }

    public boolean hasNsDefaults() {
        return this.mHasNsDefaults;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    void reportValidationProblem(String msg) throws XMLStreamException {
        doReportValidationProblem(msg, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    void reportValidationProblem(String msg, Location loc) throws XMLStreamException {
        doReportValidationProblem(msg, loc);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    void reportValidationProblem(String format, Object arg) throws XMLStreamException {
        doReportValidationProblem(MessageFormat.format(format, arg), null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    void reportValidationProblem(String format, Object arg1, Object arg2) throws XMLStreamException {
        doReportValidationProblem(MessageFormat.format(format, arg1, arg2), null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doReportValidationProblem(String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = getLocation();
        }
        XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 2);
        prob.setReporter(this);
        this.mContext.reportProblem(prob);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doAddDefaultValue(DTDAttribute attr) throws XMLStreamException {
        String def = attr.getDefaultValue(this.mContext, this);
        if (def == null) {
            ExceptionUtil.throwInternal("null default attribute value");
        }
        PrefixedName an = attr.getName();
        String prefix = an.getPrefix();
        String uri = "";
        if (prefix != null && prefix.length() > 0) {
            uri = this.mContext.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                reportValidationProblem("Unbound namespace prefix \"{0}\" for default attribute \"{1}\"", prefix, attr);
                uri = "";
            }
        }
        int defIx = this.mContext.addDefaultAttribute(an.getLocalName(), uri, prefix, def);
        if (defIx >= 0) {
            while (defIx >= this.mAttrSpecs.length) {
                this.mAttrSpecs = (DTDAttribute[]) DataUtil.growArrayBy50Pct(this.mAttrSpecs);
            }
            while (this.mAttrCount < defIx) {
                DTDAttribute[] dTDAttributeArr = this.mAttrSpecs;
                int i = this.mAttrCount;
                this.mAttrCount = i + 1;
                dTDAttributeArr[i] = null;
            }
            this.mAttrSpecs[defIx] = attr;
            this.mAttrCount = defIx + 1;
        }
    }
}
