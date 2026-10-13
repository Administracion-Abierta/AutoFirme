package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.AnyURIType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DurationType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GDayType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GMonthDayType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GMonthType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GYearMonthType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GYearType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NormalizedStringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NumberType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.Uri;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReader.class */
public abstract class GrammarReader extends XMLFilterImpl implements IDContextProvider2 {
    private Locator locator;
    public final Controller controller;
    public final SAXParserFactory parserFactory;
    public final ExpressionPool pool;
    public static final PrefixResolver basePrefixResolver = new PrefixResolver() { // from class: com.ctc.wstx.shaded.msv_core.reader.GrammarReader.1
        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.PrefixResolver
        public String resolve(String prefix) {
            if (prefix.equals(CanonicalizerBase.XML)) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            return null;
        }
    };
    private InclusionContext pendingIncludes;
    public static final String ERR_MALPLACED_ELEMENT = "GrammarReader.MalplacedElement";
    public static final String ERR_CHARACTERS = "GrammarReader.Characters";
    public static final String ERR_DISALLOWED_ATTRIBUTE = "GrammarReader.DisallowedAttribute";
    public static final String ERR_MISSING_ATTRIBUTE = "GrammarReader.MissingAttribute";
    public static final String ERR_BAD_ATTRIBUTE_VALUE = "GrammarReader.BadAttributeValue";
    public static final String ERR_MISSING_ATTRIBUTE_2 = "GrammarReader.MissingAttribute.2";
    public static final String ERR_CONFLICTING_ATTRIBUTES = "GrammarReader.ConflictingAttribute";
    public static final String ERR_RECURSIVE_INCLUDE = "GrammarReader.RecursiveInclude";
    public static final String ERR_FRAGMENT_IDENTIFIER = "GrammarReader.FragmentIdentifier";
    public static final String ERR_UNDEFINED_DATATYPE = "GrammarReader.UndefinedDataType";
    public static final String ERR_DATATYPE_ALREADY_DEFINED = "GrammarReader.DataTypeAlreadyDefined";
    public static final String ERR_MISSING_CHILD_EXPRESSION = "GrammarReader.Abstract.MissingChildExpression";
    public static final String ERR_MORE_THAN_ONE_CHILD_EXPRESSION = "GrammarReader.Abstract.MoreThanOneChildExpression";
    public static final String ERR_MORE_THAN_ONE_CHILD_TYPE = "GrammarReader.Abstract.MoreThanOneChildType";
    public static final String ERR_MISSING_CHILD_TYPE = "GrammarReader.Abstract.MissingChildType";
    public static final String ERR_ILLEGAL_FINAL_VALUE = "GrammarReader.IllegalFinalValue";
    public static final String ERR_RUNAWAY_EXPRESSION = "GrammarReader.Abstract.RunAwayExpression";
    public static final String ERR_MISSING_TOPLEVEL = "GrammarReader.Abstract.MissingTopLevel";
    public static final String WRN_MAYBE_WRONG_NAMESPACE = "GrammarReader.Warning.MaybeWrongNamespace";
    public static final String WRN_DEPRECATED_TYPENAME = "GrammarReader.Warning.DeprecatedTypeName";
    public static final String ERR_BAD_TYPE = "GrammarReader.BadType";
    public static final String ERR_RECURSIVE_DATATYPE = "GrammarReader.RecursiveDatatypeDefinition";
    public PrefixResolver prefixResolver = basePrefixResolver;
    public final BackwardReferenceMap backwardReference = new BackwardReferenceMap();
    private final Map declaredLocations = new HashMap();
    private final Vector backPatchJobs = new Vector();
    private final Vector delayedBackPatchJobs = new Vector();

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReader$BackPatch.class */
    public interface BackPatch {
        void patch();

        State getOwnerState();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReader$PrefixResolver.class */
    public interface PrefixResolver {
        String resolve(String str);
    }

    public abstract Grammar getResultAsGrammar();

    protected abstract boolean isGrammarElement(StartTagInfo startTagInfo);

    public abstract State createExpressionChildState(State state, StartTagInfo startTagInfo);

    protected abstract String localizeMessage(String str, Object[] objArr);

    protected static SAXParserFactory createParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }

    protected GrammarReader(GrammarReaderController _controller, SAXParserFactory parserFactory, ExpressionPool pool, State initialState) {
        this.controller = new Controller(_controller);
        this.parserFactory = parserFactory;
        if (parserFactory != null && !parserFactory.isNamespaceAware()) {
            throw new IllegalArgumentException("parser factory must be namespace-aware");
        }
        this.pool = pool;
        pushState(initialState, null, null);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReader$ChainPrefixResolver.class */
    public class ChainPrefixResolver implements PrefixResolver {
        public final PrefixResolver previous;
        public final String prefix;
        public final String uri;

        public ChainPrefixResolver(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
            this.previous = GrammarReader.this.prefixResolver;
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.PrefixResolver
        public String resolve(String p) {
            return p.equals(this.prefix) ? this.uri : this.previous.resolve(p);
        }
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.prefixResolver = new ChainPrefixResolver(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
        this.prefixResolver = ((ChainPrefixResolver) this.prefixResolver).previous;
        super.endPrefixMapping(prefix);
    }

    public Iterator iterateInscopeNamespaces() {
        return new Iterator() { // from class: com.ctc.wstx.shaded.msv_core.reader.GrammarReader.2
            private PrefixResolver resolver;

            {
                this.resolver = proceed(GrammarReader.this.prefixResolver);
            }

            @Override // java.util.Iterator
            public Object next() {
                final ChainPrefixResolver cpr = (ChainPrefixResolver) this.resolver;
                this.resolver = proceed(cpr.previous);
                return new Map.Entry() { // from class: com.ctc.wstx.shaded.msv_core.reader.GrammarReader.2.1
                    @Override // java.util.Map.Entry
                    public Object getKey() {
                        return cpr.prefix;
                    }

                    @Override // java.util.Map.Entry
                    public Object getValue() {
                        return cpr.uri;
                    }

                    @Override // java.util.Map.Entry
                    public Object setValue(Object o) {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.resolver instanceof ChainPrefixResolver;
            }

            private PrefixResolver proceed(PrefixResolver resolver) {
                while (resolver instanceof ChainPrefixResolver) {
                    ChainPrefixResolver cpr = (ChainPrefixResolver) resolver;
                    if (GrammarReader.this.resolveNamespacePrefix(cpr.prefix) == cpr.uri) {
                        return resolver;
                    }
                    resolver = cpr.previous;
                }
                return resolver;
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String[] splitQName(String qName) {
        int idx = qName.indexOf(58);
        if (idx < 0) {
            String ns = this.prefixResolver.resolve("");
            if (ns == null) {
                ns = "";
            }
            return new String[]{ns, qName, qName};
        }
        String uri = this.prefixResolver.resolve(qName.substring(0, idx));
        if (uri == null) {
            return null;
        }
        return new String[]{uri, qName.substring(idx + 1), qName};
    }

    protected Expression interceptExpression(State state, Expression exp) {
        return exp;
    }

    public XSDatatype getBackwardCompatibleType(String typeName) {
        XSDatatype dt = null;
        if (typeName.equals("uriReference")) {
            dt = AnyURIType.theInstance;
        } else if (typeName.equals("number")) {
            dt = NumberType.theInstance;
        } else if (typeName.equals("timeDuration")) {
            dt = DurationType.theInstance;
        } else if (typeName.equals("CDATA")) {
            dt = NormalizedStringType.theInstance;
        } else if (typeName.equals("year")) {
            dt = GYearType.theInstance;
        } else if (typeName.equals("yearMonth")) {
            dt = GYearMonthType.theInstance;
        } else if (typeName.equals("month")) {
            dt = GMonthType.theInstance;
        } else if (typeName.equals("monthDay")) {
            dt = GMonthDayType.theInstance;
        } else if (typeName.equals("day")) {
            dt = GDayType.theInstance;
        }
        if (dt != null) {
            reportWarning(WRN_DEPRECATED_TYPENAME, typeName, dt.displayName());
        }
        return dt;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReader$InclusionContext.class */
    private class InclusionContext {
        final PrefixResolver prefixResolver;
        final Locator locator;
        final String systemId;
        final InclusionContext previousContext;

        InclusionContext(PrefixResolver prefix, Locator loc, String sysId, InclusionContext prev) {
            this.prefixResolver = prefix;
            this.locator = loc;
            this.systemId = sysId;
            this.previousContext = prev;
        }
    }

    private void pushInclusionContext() {
        this.pendingIncludes = new InclusionContext(this.prefixResolver, getLocator(), getLocator().getSystemId(), this.pendingIncludes);
        this.prefixResolver = basePrefixResolver;
        setLocator(null);
    }

    private void popInclusionContext() {
        this.prefixResolver = this.pendingIncludes.prefixResolver;
        setLocator(this.pendingIncludes.locator);
        this.pendingIncludes = this.pendingIncludes.previousContext;
    }

    public final InputSource resolveLocation(State sourceState, String uri) throws AbortException {
        try {
            String uri2 = combineURI(sourceState.getBaseURI(), uri);
            InputSource source = this.controller.resolveEntity(null, uri2);
            return source == null ? new InputSource(uri2) : source;
        } catch (IOException e) {
            this.controller.error(e, getLocator());
            throw AbortException.theInstance;
        } catch (SAXException e2) {
            this.controller.error(e2, getLocator());
            throw AbortException.theInstance;
        }
    }

    public final String combineURI(String baseURI, String relativeURI) {
        return Uri.resolve(baseURI, relativeURI);
    }

    public final String combineURL(String baseURI, String relativeURI) {
        return Uri.resolve(baseURI, relativeURI);
    }

    public void switchSource(State sourceState, String url, State newState) throws AbortException {
        if (url.indexOf(35) >= 0) {
            reportError(ERR_FRAGMENT_IDENTIFIER, url);
            throw AbortException.theInstance;
        }
        switchSource(resolveLocation(sourceState, url), newState);
    }

    public void switchSource(InputSource source, State newState) {
        switchSource(new SAXSource(source), newState);
    }

    public void switchSource(Source source, State newState) {
        String url = source.getSystemId();
        InclusionContext inclusionContext = this.pendingIncludes;
        while (true) {
            InclusionContext ic = inclusionContext;
            if (ic != null) {
                if (ic.systemId == null || !ic.systemId.equals(url)) {
                    inclusionContext = ic.previousContext;
                } else {
                    String s = "";
                    InclusionContext inclusionContext2 = this.pendingIncludes;
                    while (true) {
                        InclusionContext i = inclusionContext2;
                        if (i != ic) {
                            s = i.systemId + " > " + s;
                            inclusionContext2 = i.previousContext;
                        } else {
                            reportError(ERR_RECURSIVE_INCLUDE, url + " > " + s + url);
                            return;
                        }
                    }
                }
            } else {
                pushInclusionContext();
                State currentState = getCurrentState();
                try {
                    pushState(newState, null, null);
                    try {
                        try {
                            parse(source);
                        } catch (TransformerException e) {
                            this.controller.error("transform error", e);
                        }
                    } catch (TransformerConfigurationException e2) {
                        this.controller.error("transform error", e2);
                    }
                    super.setContentHandler(currentState);
                    popInclusionContext();
                    return;
                } catch (Throwable th) {
                    super.setContentHandler(currentState);
                    popInclusionContext();
                    throw th;
                }
            }
        }
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.XMLReader
    public final void parse(String source) {
        _parse(source, null);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.XMLReader
    public final void parse(InputSource source) {
        _parse(source, null);
    }

    public void parse(Source source) throws TransformerException {
        InputSource saxSource = SAXSource.sourceToInputSource(source);
        if (saxSource != null) {
            parse(saxSource);
            return;
        }
        LocatorImpl sourceLocator = new LocatorImpl();
        sourceLocator.setSystemId(source.getSystemId());
        setLocator(sourceLocator);
        TransformerFactory factory = TransformerFactory.newInstance();
        SAXResult result = new SAXResult(this);
        factory.newTransformer().transform(source, result);
    }

    public final void _parse(Object source, Locator errorSource) {
        try {
            XMLReader reader = this.parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(this);
            reader.setErrorHandler(this.controller);
            reader.setEntityResolver(this.controller);
            if (source instanceof InputSource) {
                reader.parse((InputSource) source);
            }
            if (source instanceof String) {
                reader.parse((String) source);
            }
        } catch (IOException e) {
            this.controller.error(e, errorSource);
        } catch (ParserConfigurationException e2) {
            this.controller.error(e2, errorSource);
        } catch (SAXParseException e3) {
            this.controller.error(e3);
        } catch (SAXException e4) {
            this.controller.error(e4, errorSource);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReader$BackwardReferenceMap.class */
    public class BackwardReferenceMap {
        private final Map impl = new HashMap();

        public BackwardReferenceMap() {
        }

        public void memorizeLink(Object target) {
            ArrayList list;
            if (this.impl.containsKey(target)) {
                list = (ArrayList) this.impl.get(target);
            } else {
                list = new ArrayList();
                this.impl.put(target, list);
            }
            list.add(new LocatorImpl(GrammarReader.this.getLocator()));
        }

        public Locator[] getReferer(Object target) {
            if (this.impl.containsKey(target)) {
                ArrayList lst = (ArrayList) this.impl.get(target);
                Locator[] locs = new Locator[lst.size()];
                lst.toArray(locs);
                return locs;
            }
            return null;
        }
    }

    public void setDeclaredLocationOf(Object o) {
        this.declaredLocations.put(o, new LocatorImpl(getLocator()));
    }

    public Locator getDeclaredLocationOf(Object o) {
        return (Locator) this.declaredLocations.get(o);
    }

    public void detectUndefinedOnes(ReferenceContainer container, String errMsg) {
        Iterator itr = container.iterator();
        while (itr.hasNext()) {
            ReferenceExp ref = (ReferenceExp) itr.next();
            if (!ref.isDefined()) {
                reportError(this.backwardReference.getReferer(ref), errMsg, new Object[]{ref.name});
                ref.exp = Expression.nullSet;
            }
        }
    }

    public void pushState(State newState, State parentState, StartTagInfo startTag) {
        super.setContentHandler(newState);
        newState.init(this, parentState, startTag);
    }

    public void popState() {
        State currentState = getCurrentState();
        if (currentState.parentState != null) {
            super.setContentHandler(currentState.parentState);
        } else {
            super.setContentHandler(new DefaultHandler());
        }
    }

    public final State getCurrentState() {
        return (State) super.getContentHandler();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator loc) {
        super.setDocumentLocator(loc);
        setLocator(loc);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String resolveNamespacePrefix(String prefix) {
        return this.prefixResolver.resolve(prefix);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isUnparsedEntity(String entityName) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isNotation(String notationName) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String getBaseUri() {
        return getCurrentState().getBaseURI();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2
    public final void onID(Datatype dt, StringToken token) {
    }

    public final void addBackPatchJob(BackPatch job) {
        this.backPatchJobs.add(job);
    }

    public final void addBackPatchJob(XSDatatypeExp job) {
        this.delayedBackPatchJobs.add(job);
    }

    public final void runBackPatchJob() {
        Locator oldLoc = getLocator();
        runBackPatchJob(this.backPatchJobs);
        runBackPatchJob(this.delayedBackPatchJobs);
        setLocator(oldLoc);
    }

    private final void runBackPatchJob(Vector vec) {
        Iterator itr = vec.iterator();
        while (itr.hasNext()) {
            BackPatch job = (BackPatch) itr.next();
            setLocator(job.getOwnerState().getLocation());
            job.patch();
        }
    }

    public final void reportError(String propertyName) {
        reportError(propertyName, (Object[]) null, (Exception) null, (Locator[]) null);
    }

    public final void reportError(String propertyName, Object arg1) {
        reportError(propertyName, new Object[]{arg1}, (Exception) null, (Locator[]) null);
    }

    public final void reportError(String propertyName, Object arg1, Object arg2) {
        reportError(propertyName, new Object[]{arg1, arg2}, (Exception) null, (Locator[]) null);
    }

    public final void reportError(String propertyName, Object arg1, Object arg2, Object arg3) {
        reportError(propertyName, new Object[]{arg1, arg2, arg3}, (Exception) null, (Locator[]) null);
    }

    public final void reportError(Exception nestedException, String propertyName) {
        reportError(propertyName, (Object[]) null, nestedException, (Locator[]) null);
    }

    public final void reportError(Exception nestedException, String propertyName, Object arg1) {
        reportError(propertyName, new Object[]{arg1}, nestedException, (Locator[]) null);
    }

    public final void reportError(Locator[] locs, String propertyName, Object[] args) {
        reportError(propertyName, args, (Exception) null, locs);
    }

    public final void reportWarning(String propertyName) {
        reportWarning(propertyName, (Object[]) null, (Locator[]) null);
    }

    public final void reportWarning(String propertyName, Object arg1) {
        reportWarning(propertyName, new Object[]{arg1}, (Locator[]) null);
    }

    public final void reportWarning(String propertyName, Object arg1, Object arg2) {
        reportWarning(propertyName, new Object[]{arg1, arg2}, (Locator[]) null);
    }

    private Locator[] prepareLocation(Locator[] param) {
        if (param == null) {
            return getLocator() != null ? new Locator[]{getLocator()} : new Locator[0];
        }
        int cnt = 0;
        for (Locator locator : param) {
            if (locator != null) {
                cnt++;
            }
        }
        if (param.length == cnt) {
            return param;
        }
        Locator[] locs = new Locator[cnt];
        int cnt2 = 0;
        for (int i = 0; i < param.length; i++) {
            if (param[i] != null) {
                int i2 = cnt2;
                cnt2++;
                locs[i2] = param[i];
            }
        }
        return locs;
    }

    public final void reportError(String propertyName, Object[] args, Exception nestedException, Locator[] errorLocations) {
        this.controller.error(prepareLocation(errorLocations), localizeMessage(propertyName, args), nestedException);
    }

    public final void reportWarning(String propertyName, Object[] args, Locator[] locations) {
        this.controller.warning(prepareLocation(locations), localizeMessage(propertyName, args));
    }

    public static Source inputSourceFromLSInput(LSInput input) {
        Source source;
        if (input instanceof DOMLSInput) {
            DOMLSInput domLSInput = (DOMLSInput) input;
            source = new DOMSource(domLSInput.getElement());
        } else {
            InputSource inputSource = new InputSource();
            if (input.getCharacterStream() != null) {
                inputSource.setCharacterStream(input.getCharacterStream());
            }
            if (input.getByteStream() != null) {
                inputSource.setByteStream(input.getByteStream());
            }
            if (input.getStringData() != null) {
                inputSource.setCharacterStream(new StringReader(input.getStringData()));
            }
            if (input.getPublicId() != null) {
                inputSource.setPublicId(input.getPublicId());
            }
            source = new SAXSource(inputSource);
        }
        if (input.getSystemId() != null) {
            source.setSystemId(input.getSystemId());
        }
        return source;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public Locator getLocator() {
        return this.locator;
    }
}
