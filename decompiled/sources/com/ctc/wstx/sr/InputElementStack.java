package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.XmlConsts;
import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.EmptyNamespaceContext;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.util.TextBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.ValidatorPair;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/InputElementStack.class */
public final class InputElementStack implements AttributeInfo, NamespaceContext, ValidationContext {
    static final int ID_ATTR_NONE = -1;
    protected final boolean mNsAware;
    protected final AttributeCollector mAttrCollector;
    protected final ReaderConfig mConfig;
    protected NsDefaultProvider mNsDefaultProvider;
    protected Element mCurrElement;
    protected InputProblemReporter mReporter = null;
    protected int mDepth = 0;
    protected long mTotalElements = 0;
    protected final StringVector mNamespaces = new StringVector(64);
    protected boolean mMayHaveNsDefaults = false;
    protected XMLValidator mValidator = null;
    protected int mIdAttrIndex = -1;
    protected String mLastLocalName = null;
    protected String mLastPrefix = null;
    protected String mLastNsURI = null;
    protected QName mLastName = null;
    protected BaseNsContext mLastNsContext = null;
    protected Element mFreeElement = null;

    protected InputElementStack(ReaderConfig cfg, boolean nsAware) {
        this.mConfig = cfg;
        this.mNsAware = nsAware;
        this.mAttrCollector = new AttributeCollector(cfg, nsAware);
    }

    protected void connectReporter(InputProblemReporter rep) {
        this.mReporter = rep;
    }

    protected XMLValidator addValidator(XMLValidator vld) {
        if (this.mValidator == null) {
            this.mValidator = vld;
        } else {
            this.mValidator = new ValidatorPair(this.mValidator, vld);
        }
        return vld;
    }

    protected void setAutomaticDTDValidator(XMLValidator validator, NsDefaultProvider nsDefs) {
        this.mNsDefaultProvider = nsDefs;
        addValidator(validator);
    }

    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return addValidator(schema.createValidator(this));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        if (ValidatorPair.removeValidator(this.mValidator, schema, results)) {
            XMLValidator found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            return found;
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        if (ValidatorPair.removeValidator(this.mValidator, validator, results)) {
            XMLValidator found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            return found;
        }
        return null;
    }

    protected boolean reallyValidating() {
        if (this.mValidator == null) {
            return false;
        }
        if (!(this.mValidator instanceof DTDValidatorBase)) {
            return true;
        }
        return ((DTDValidatorBase) this.mValidator).reallyValidating();
    }

    public final AttributeCollector getAttrCollector() {
        return this.mAttrCollector;
    }

    public BaseNsContext createNonTransientNsContext(Location loc) {
        if (this.mLastNsContext != null) {
            return this.mLastNsContext;
        }
        int totalNsSize = this.mNamespaces.size();
        if (totalNsSize < 1) {
            EmptyNamespaceContext emptyNamespaceContext = EmptyNamespaceContext.getInstance();
            this.mLastNsContext = emptyNamespaceContext;
            return emptyNamespaceContext;
        }
        int localCount = getCurrentNsCount() << 1;
        BaseNsContext nsCtxt = new CompactNsContext(loc, this.mNamespaces.asArray(), totalNsSize, totalNsSize - localCount);
        if (localCount == 0) {
            this.mLastNsContext = nsCtxt;
        }
        return nsCtxt;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final void push(String prefix, String localName) throws XMLStreamException {
        int i = this.mDepth + 1;
        this.mDepth = i;
        if (i > this.mConfig.getMaxElementDepth()) {
            throw new XMLStreamException("Maximum Element Depth limit (" + this.mConfig.getMaxElementDepth() + ") Exceeded");
        }
        long j = this.mTotalElements + 1;
        this.mTotalElements = j;
        if (j > this.mConfig.getMaxElementCount()) {
            throw new XMLStreamException("Maximum Element Count limit (" + this.mConfig.getMaxElementCount() + ") Exceeded");
        }
        String defaultNs = this.mCurrElement == null ? "" : this.mCurrElement.mDefaultNsURI;
        if (this.mCurrElement != null) {
            this.mCurrElement.mChildCount++;
            int max = this.mConfig.getMaxChildrenPerElement();
            if (max > 0 && this.mCurrElement.mChildCount > max) {
                throw new XMLStreamException("Maximum Number of Child Elements limit (" + max + ") Exceeded");
            }
        }
        if (this.mFreeElement == null) {
            this.mCurrElement = new Element(this.mCurrElement, this.mNamespaces.size(), prefix, localName);
        } else {
            Element newElem = this.mFreeElement;
            this.mFreeElement = newElem.mParent;
            newElem.reset(this.mCurrElement, this.mNamespaces.size(), prefix, localName);
            this.mCurrElement = newElem;
        }
        this.mCurrElement.mDefaultNsURI = defaultNs;
        this.mAttrCollector.reset();
        if (this.mNsDefaultProvider != null) {
            this.mMayHaveNsDefaults = this.mNsDefaultProvider.mayHaveNsDefaults(prefix, localName);
        }
    }

    public final boolean pop() throws XMLStreamException {
        if (this.mCurrElement == null) {
            throw new IllegalStateException("Popping from empty stack");
        }
        this.mDepth--;
        Element child = this.mCurrElement;
        Element parent = child.mParent;
        this.mCurrElement = parent;
        child.relink(this.mFreeElement);
        this.mFreeElement = child;
        int nsCount = this.mNamespaces.size() - child.mNsOffset;
        if (nsCount > 0) {
            this.mLastNsContext = null;
            this.mNamespaces.removeLast(nsCount);
        }
        return parent != null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int resolveAndValidateElement() throws XMLStreamException {
        String ns;
        if (this.mDepth == 0) {
            throw new IllegalStateException("Calling validate() on empty stack.");
        }
        AttributeCollector ac = this.mAttrCollector;
        int nsCount = ac.getNsCount();
        if (nsCount > 0) {
            this.mLastNsContext = null;
            boolean internNsUris = this.mConfig.willInternNsURIs();
            for (int i = 0; i < nsCount; i++) {
                Attribute ns2 = ac.resolveNamespaceDecl(i, internNsUris);
                String nsUri = ns2.mNamespaceURI;
                String prefix = ns2.mLocalName;
                if (prefix == CanonicalizerBase.XMLNS) {
                    this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS);
                } else if (prefix == "xml") {
                    if (!"http://www.w3.org/XML/1998/namespace".equals(nsUri)) {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML, nsUri, null);
                    }
                } else {
                    if (nsUri == null || nsUri.length() == 0) {
                        nsUri = "";
                    }
                    if (prefix == null) {
                        this.mCurrElement.mDefaultNsURI = nsUri;
                    }
                    if (internNsUris) {
                        if (nsUri == "http://www.w3.org/XML/1998/namespace") {
                            this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix, null);
                        } else if (nsUri == "http://www.w3.org/2000/xmlns/") {
                            this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI);
                        }
                    } else if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix, null);
                    } else if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI);
                    }
                    this.mNamespaces.addStrings(prefix, nsUri);
                }
            }
        }
        if (this.mMayHaveNsDefaults) {
            this.mNsDefaultProvider.checkNsDefaults(this);
        }
        String prefix2 = this.mCurrElement.mPrefix;
        if (prefix2 == null) {
            ns = this.mCurrElement.mDefaultNsURI;
        } else if (prefix2 == "xml") {
            ns = "http://www.w3.org/XML/1998/namespace";
        } else {
            ns = this.mNamespaces.findLastFromMap(prefix2);
            if (ns == null || ns.length() == 0) {
                this.mReporter.throwParseError(ErrorConsts.ERR_NS_UNDECLARED, prefix2, null);
            }
        }
        this.mCurrElement.mNamespaceURI = ns;
        int xmlidIx = ac.resolveNamespaces(this.mReporter, this.mNamespaces);
        this.mIdAttrIndex = xmlidIx;
        XMLValidator vld = this.mValidator;
        if (vld == null) {
            if (xmlidIx >= 0) {
                ac.normalizeSpacesInValue(xmlidIx);
                return 4;
            }
            return 4;
        }
        vld.validateElementStart(this.mCurrElement.mLocalName, this.mCurrElement.mNamespaceURI, this.mCurrElement.mPrefix);
        int attrLen = ac.getCount();
        if (attrLen > 0) {
            for (int i2 = 0; i2 < attrLen; i2++) {
                ac.validateAttribute(i2, this.mValidator);
            }
        }
        return this.mValidator.validateElementAndAttributes();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int validateEndElement() throws XMLStreamException {
        if (this.mValidator == null) {
            return 4;
        }
        int result = this.mValidator.validateElementEnd(this.mCurrElement.mLocalName, this.mCurrElement.mNamespaceURI, this.mCurrElement.mPrefix);
        if (this.mDepth == 1) {
            this.mValidator.validationCompleted(true);
        }
        return result;
    }

    @Override // org.codehaus.stax2.AttributeInfo, org.codehaus.stax2.validation.ValidationContext
    public final int getAttributeCount() {
        return this.mAttrCollector.getCount();
    }

    @Override // org.codehaus.stax2.AttributeInfo, org.codehaus.stax2.validation.ValidationContext
    public final int findAttributeIndex(String nsURI, String localName) {
        return this.mAttrCollector.findIndex(nsURI, localName);
    }

    @Override // org.codehaus.stax2.AttributeInfo
    public final int getIdAttributeIndex() {
        if (this.mIdAttrIndex >= 0) {
            return this.mIdAttrIndex;
        }
        if (this.mValidator == null) {
            return -1;
        }
        return this.mValidator.getIdAttrIndex();
    }

    @Override // org.codehaus.stax2.AttributeInfo
    public final int getNotationAttributeIndex() {
        if (this.mValidator == null) {
            return -1;
        }
        return this.mValidator.getNotationAttrIndex();
    }

    @Override // javax.xml.namespace.NamespaceContext, org.codehaus.stax2.validation.ValidationContext
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
        }
        if (prefix.length() == 0) {
            if (this.mDepth == 0) {
                return "";
            }
            return this.mCurrElement.mDefaultNsURI;
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals(CanonicalizerBase.XMLNS)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return this.mNamespaces.findLastNonInterned(prefix);
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return CanonicalizerBase.XMLNS;
        }
        String prefix = null;
        String[] strs = this.mNamespaces.getInternalArray();
        int len = this.mNamespaces.size();
        loop0: for (int index = len - 1; index > 0; index -= 2) {
            if (nsURI.equals(strs[index])) {
                prefix = strs[index - 1];
                int j = index + 1;
                while (true) {
                    if (j < len) {
                        if (strs[j] != prefix) {
                            j += 2;
                        } else {
                            prefix = null;
                            break;
                        }
                    } else {
                        if (prefix != null) {
                            break loop0;
                        }
                        prefix = "";
                        break loop0;
                    }
                }
            }
        }
        return prefix;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return DataUtil.singletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return DataUtil.singletonIterator(CanonicalizerBase.XMLNS);
        }
        String[] strs = this.mNamespaces.getInternalArray();
        int len = this.mNamespaces.size();
        ArrayList<String> l = null;
        for (int index = len - 1; index > 0; index -= 2) {
            if (nsURI.equals(strs[index])) {
                String prefix = strs[index - 1];
                int j = index + 1;
                while (true) {
                    if (j < len) {
                        if (strs[j] == prefix) {
                            break;
                        }
                        j += 2;
                    } else {
                        if (l == null) {
                            l = new ArrayList<>();
                        }
                        l.add(prefix);
                        break;
                    }
                }
            }
        }
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public final String getXmlVersion() {
        return this.mConfig.isXml11() ? XmlConsts.XML_V_11_STR : "1.0";
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeLocalName(int index) {
        return getAttrCollector().getLocalName(index);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeNamespace(int index) {
        return getAttrCollector().getURI(index);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributePrefix(int index) {
        return getAttrCollector().getPrefix(index);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeValue(int index) {
        return getAttrCollector().getValue(index);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getAttributeValue(String nsURI, String localName) {
        int ix = findAttributeIndex(nsURI, localName);
        if (ix < 0) {
            return null;
        }
        return getAttributeValue(ix);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public boolean isNotationDeclared(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public boolean isUnparsedEntityDeclared(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public String getBaseUri() {
        return null;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public final QName getCurrentElementName() {
        if (this.mDepth == 0) {
            return null;
        }
        String prefix = this.mCurrElement.mPrefix;
        if (prefix == null) {
            prefix = "";
        }
        String nsURI = this.mCurrElement.mNamespaceURI;
        String ln = this.mCurrElement.mLocalName;
        if (ln != this.mLastLocalName) {
            this.mLastLocalName = ln;
            this.mLastPrefix = prefix;
            this.mLastNsURI = nsURI;
        } else if (prefix != this.mLastPrefix) {
            this.mLastPrefix = prefix;
            this.mLastNsURI = nsURI;
        } else if (nsURI != this.mLastNsURI) {
            this.mLastNsURI = nsURI;
        } else {
            return this.mLastName;
        }
        QName n = QNameCreator.create(nsURI, ln, prefix);
        this.mLastName = n;
        return n;
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public Location getValidationLocation() {
        return this.mReporter.getLocation();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.ValidationContext
    public void reportProblem(XMLValidationProblem problem) throws XMLStreamException {
        this.mReporter.reportValidationProblem(problem);
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public int addDefaultAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        return this.mAttrCollector.addDefaultAttribute(localName, uri, prefix, value);
    }

    public boolean isPrefixLocallyDeclared(String internedPrefix) {
        if (internedPrefix != null && internedPrefix.length() == 0) {
            internedPrefix = null;
        }
        int len = this.mNamespaces.size();
        for (int offset = this.mCurrElement.mNsOffset; offset < len; offset += 2) {
            String thisPrefix = this.mNamespaces.getString(offset);
            if (thisPrefix == internedPrefix) {
                return true;
            }
        }
        return false;
    }

    public void addNsBinding(String prefix, String uri) {
        if (uri == null || uri.length() == 0) {
            uri = null;
        }
        if (prefix == null || prefix.length() == 0) {
            prefix = null;
            this.mCurrElement.mDefaultNsURI = uri;
        }
        this.mNamespaces.addStrings(prefix, uri);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final void validateText(TextBuffer tb, boolean lastTextSegment) throws XMLStreamException {
        tb.validateText(this.mValidator, lastTextSegment);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final void validateText(String contents, boolean lastTextSegment) throws XMLStreamException {
        this.mValidator.validateText(contents, lastTextSegment);
    }

    public final boolean isNamespaceAware() {
        return this.mNsAware;
    }

    public final boolean isEmpty() {
        return this.mDepth == 0;
    }

    public final int getDepth() {
        return this.mDepth;
    }

    public final String getDefaultNsURI() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mDefaultNsURI;
    }

    public final String getNsURI() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mNamespaceURI;
    }

    public final String getPrefix() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mPrefix;
    }

    public final String getLocalName() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mLocalName;
    }

    public final boolean matches(String prefix, String localName) {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        String thisPrefix = this.mCurrElement.mPrefix;
        if (prefix == null || prefix.length() == 0) {
            if (thisPrefix != null && thisPrefix.length() > 0) {
                return false;
            }
        } else if (thisPrefix != prefix && !prefix.equals(thisPrefix)) {
            return false;
        }
        String thisName = this.mCurrElement.mLocalName;
        return thisName == localName || thisName.equals(localName);
    }

    public final String getTopElementDesc() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        String name = this.mCurrElement.mLocalName;
        String prefix = this.mCurrElement.mPrefix;
        if (prefix == null) {
            return name;
        }
        return prefix + ":" + name;
    }

    public final int getTotalNsCount() {
        return this.mNamespaces.size() >> 1;
    }

    public final int getCurrentNsCount() {
        return (this.mNamespaces.size() - this.mCurrElement.mNsOffset) >> 1;
    }

    public final String getLocalNsPrefix(int index) {
        int offset = this.mCurrElement.mNsOffset;
        int localCount = this.mNamespaces.size() - offset;
        int index2 = index << 1;
        if (index2 < 0 || index2 >= localCount) {
            throwIllegalIndex(index2 >> 1, localCount >> 1);
        }
        return this.mNamespaces.getString(offset + index2);
    }

    public final String getLocalNsURI(int index) {
        int offset = this.mCurrElement.mNsOffset;
        int localCount = this.mNamespaces.size() - offset;
        int index2 = index << 1;
        if (index2 < 0 || index2 >= localCount) {
            throwIllegalIndex(index2 >> 1, localCount >> 1);
        }
        return this.mNamespaces.getString(offset + index2 + 1);
    }

    private void throwIllegalIndex(int index, int localCount) {
        throw new IllegalArgumentException("Illegal namespace index " + (index >> 1) + "; current scope only has " + (localCount >> 1) + " namespace declarations.");
    }

    @Override // org.codehaus.stax2.validation.ValidationContext
    public final String getAttributeType(int index) {
        if (index != this.mIdAttrIndex || index < 0) {
            return this.mValidator == null ? "CDATA" : this.mValidator.getAttributeType(index);
        }
        return "ID";
    }
}
