package com.ctc.wstx.msv;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.TextAccumulator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/msv/GenericMsvValidator.class */
public final class GenericMsvValidator extends XMLValidator implements IDContextProvider2, XMLStreamConstants {
    final XMLValidationSchema mParentSchema;
    final ValidationContext mContext;
    final DocumentDeclaration mVGM;
    Acceptor mCurrAcceptor;
    ElementIdMap mIdDefs;
    String mCurrAttrPrefix;
    String mCurrAttrLocalName;
    XMLValidationProblem mProblem;
    final AttributeProxy mAttributeProxy;
    final ArrayList<Object> mAcceptors = new ArrayList<>();
    final TextAccumulator mTextAccumulator = new TextAccumulator();
    final StringRef mErrorRef = new StringRef();
    final StartTagInfo mStartTag = new StartTagInfo("", "", "", (Attributes) null, (IDContextProvider2) null);
    protected String mStartTagPrefix = "";

    public GenericMsvValidator(XMLValidationSchema parent, ValidationContext ctxt, DocumentDeclaration vgm) {
        this.mCurrAcceptor = null;
        this.mParentSchema = parent;
        this.mContext = ctxt;
        this.mVGM = vgm;
        this.mCurrAcceptor = this.mVGM.createAcceptor();
        this.mAttributeProxy = new AttributeProxy(ctxt);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String getBaseUri() {
        return this.mContext.getBaseUri();
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isNotation(String notationName) {
        return this.mContext.isNotationDeclared(notationName);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isUnparsedEntity(String entityName) {
        return this.mContext.isUnparsedEntityDeclared(entityName);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String resolveNamespacePrefix(String prefix) {
        return this.mContext.getNamespaceURI(prefix);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2
    public void onID(Datatype datatype, StringToken idToken) throws IllegalArgumentException {
        if (this.mIdDefs == null) {
            this.mIdDefs = new ElementIdMap();
        }
        int idType = datatype.getIdType();
        Location loc = this.mContext.getValidationLocation();
        PrefixedName elemPName = getElementPName();
        PrefixedName attrPName = getAttrPName();
        if (idType == 1) {
            String idStr = idToken.literal.trim();
            ElementId eid = this.mIdDefs.addDefined(idStr, loc, elemPName, attrPName);
            if (eid.getLocation() != loc) {
                this.mProblem = new XMLValidationProblem(loc, "Duplicate id '" + idStr + "', first declared at " + eid.getLocation());
                this.mProblem.setReporter(this);
                return;
            }
            return;
        }
        if (idType == 2) {
            this.mIdDefs.addReferenced(idToken.literal.trim(), loc, elemPName, attrPName);
        } else {
            if (idType == 3) {
                StringTokenizer tokens = new StringTokenizer(idToken.literal);
                while (tokens.hasMoreTokens()) {
                    this.mIdDefs.addReferenced(tokens.nextToken(), loc, elemPName, attrPName);
                }
                return;
            }
            throw new IllegalStateException("Internal error: unexpected ID datatype: " + datatype);
        }
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public XMLValidationSchema getSchema() {
        return this.mParentSchema;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        if (this.mCurrAcceptor == null) {
            return;
        }
        if (this.mTextAccumulator.hasText()) {
            doValidateText(this.mTextAccumulator);
        }
        if (uri == null) {
            uri = "";
        }
        this.mStartTag.reinit(uri, localName, localName, this.mAttributeProxy, this);
        this.mStartTagPrefix = prefix;
        this.mCurrAcceptor = this.mCurrAcceptor.createChildAcceptor(this.mStartTag, this.mErrorRef);
        if (this.mErrorRef.str != null) {
            reportError(this.mErrorRef, 1, _qname(uri, localName, prefix));
        }
        if (this.mProblem != null) {
            XMLValidationProblem p = this.mProblem;
            this.mProblem = null;
            this.mContext.reportProblem(p);
        }
        this.mAcceptors.add(this.mCurrAcceptor);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        this.mCurrAttrLocalName = localName;
        this.mCurrAttrPrefix = prefix;
        if (this.mCurrAcceptor != null) {
            if (uri == null) {
                uri = "";
            }
            if (!this.mCurrAcceptor.onAttribute2(uri, localName, localName, value, this, this.mErrorRef, null) || this.mErrorRef.str != null) {
                reportError(this.mErrorRef, 10, _qname(uri, localName, prefix));
            }
            if (this.mProblem != null) {
                XMLValidationProblem p = this.mProblem;
                this.mProblem = null;
                this.mContext.reportProblem(p);
                return null;
            }
            return null;
        }
        return null;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        int len = valueEnd - valueStart;
        return validateAttribute(localName, uri, prefix, new String(valueChars, valueStart, len));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public int validateElementAndAttributes() throws XMLStreamException {
        this.mCurrAttrPrefix = "";
        this.mCurrAttrLocalName = "";
        if (this.mCurrAcceptor != null) {
            if (!this.mCurrAcceptor.onEndAttributes(this.mStartTag, this.mErrorRef) || this.mErrorRef.str != null) {
                reportError(this.mErrorRef, 2, _startTagAsQName());
            }
            int stringChecks = this.mCurrAcceptor.getStringCareLevel();
            switch (stringChecks) {
                case 0:
                    return 1;
                case 1:
                    return 4;
                case 2:
                    return 3;
                default:
                    throw new IllegalArgumentException("Internal error: unexpected string care level value return by MSV: " + stringChecks);
            }
        }
        return 4;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        doValidateText(this.mTextAccumulator);
        int lastIx = this.mAcceptors.size() - 1;
        if (lastIx < 0) {
            return 1;
        }
        Acceptor acc = (Acceptor) this.mAcceptors.remove(lastIx);
        if (acc != null && (!acc.isAcceptState(this.mErrorRef) || this.mErrorRef.str != null)) {
            reportError(this.mErrorRef, 2, _qname(uri, localName, prefix));
        }
        if (lastIx == 0) {
            this.mCurrAcceptor = null;
        } else {
            this.mCurrAcceptor = (Acceptor) this.mAcceptors.get(lastIx - 1);
        }
        if (this.mCurrAcceptor != null && acc != null) {
            if (!this.mCurrAcceptor.stepForward(acc, this.mErrorRef) || this.mErrorRef.str != null) {
                reportError(this.mErrorRef, 2, _qname(uri, localName, prefix));
            }
            int stringChecks = this.mCurrAcceptor.getStringCareLevel();
            switch (stringChecks) {
                case 0:
                    return 1;
                case 1:
                    return 4;
                case 2:
                    return 3;
                default:
                    throw new IllegalArgumentException("Internal error: unexpected string care level value return by MSV: " + stringChecks);
            }
        }
        return 4;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(text);
        if (lastTextSegment) {
            doValidateText(this.mTextAccumulator);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(cbuf, textStart, textEnd);
        if (lastTextSegment) {
            doValidateText(this.mTextAccumulator);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validationCompleted(boolean eod) throws XMLStreamException {
        ElementId ref;
        if (eod && this.mIdDefs != null && (ref = this.mIdDefs.getFirstUndefined()) != null) {
            String msg = "Undefined ID '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + OperatorName.SHOW_TEXT_LINE;
            reportError(msg, ref.getLocation());
        }
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public String getAttributeType(int index) {
        return null;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public int getIdAttrIndex() {
        return -1;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public int getNotationAttrIndex() {
        return -1;
    }

    PrefixedName getElementPName() {
        return PrefixedName.valueOf(this.mContext.getCurrentElementName());
    }

    PrefixedName getAttrPName() {
        return new PrefixedName(this.mCurrAttrPrefix, this.mCurrAttrLocalName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    void doValidateText(TextAccumulator textAcc) throws XMLStreamException {
        if (this.mCurrAcceptor != null) {
            String str = textAcc.getAndClear();
            if (!this.mCurrAcceptor.onText2(str, this, this.mErrorRef, null) || this.mErrorRef.str != null) {
                reportError(this.mErrorRef, 12, _startTagAsQName());
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void reportError(StringRef errorRef, int type, QName name) throws XMLStreamException {
        String msg = errorRef.str;
        errorRef.str = null;
        if (msg == null || msg.isEmpty()) {
            switch (type) {
                case 1:
                    msg = "Unknown reason (at start element " + _name(name, "<", ">") + ")";
                    break;
                case 2:
                    msg = "Unknown reason (at end element " + _name(name, "</", ">") + ")";
                    break;
                case 10:
                    msg = "Unknown reason (at attribute " + _name(name, OperatorName.SHOW_TEXT_LINE, OperatorName.SHOW_TEXT_LINE) + ")";
                    break;
                case 12:
                default:
                    msg = "Unknown reason (at CDATA section, inside element " + _name(name, "<", ">") + ")";
                    break;
            }
        }
        reportError(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void reportError(String msg) throws XMLStreamException {
        reportError(msg, this.mContext.getValidationLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void reportError(String msg, Location loc) throws XMLStreamException {
        XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 2);
        prob.setReporter(this);
        this.mContext.reportProblem(prob);
    }

    private String _name(QName qn, String prefix, String suffix) {
        if (qn == null) {
            return "UNKNOWN";
        }
        String name = qn.getLocalPart();
        String p = qn.getPrefix();
        if (p != null && !p.isEmpty()) {
            name = p + ":" + name;
        }
        return prefix + name + suffix;
    }

    private QName _startTagAsQName() {
        return _qname(this.mStartTag.namespaceURI, this.mStartTag.localName, this.mStartTagPrefix);
    }

    private QName _qname(String ns, String local, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        if (ns == null) {
            ns = "";
        }
        if (local == null) {
            local = "";
        }
        return new QName(ns, local, prefix);
    }
}
