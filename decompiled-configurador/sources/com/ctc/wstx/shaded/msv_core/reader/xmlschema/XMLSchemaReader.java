package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.OccurrenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChoiceState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.InterleaveState;
import com.ctc.wstx.shaded.msv_core.reader.RunAwayExpressionChecker;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.SimpleTypeState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.XSFactoryImpl;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import org.apache.commons.codec.language.bm.Languages;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/XMLSchemaReader.class */
public class XMLSchemaReader extends GrammarReader implements XSDatatypeResolver {
    private Map<String, String> additionalNamespaceMap;
    protected static Schema xmlSchema4XmlSchema = null;
    public final ReferenceExp xsiSchemaLocationExp;
    public static final String XMLSchemaSchemaLocationAttributes = "____internal_XML_schema_SchemaLocation_attributes";
    public final ComplexTypeExp complexUrType;
    protected String attributeFormDefault;
    protected String elementFormDefault;
    protected String finalDefault;
    protected String blockDefault;
    protected final XMLSchemaGrammar grammar;
    protected XMLSchemaSchema currentSchema;
    protected final XMLSchemaSchema xsdSchema;
    public final Map<String, Set<String>> parsedFiles;
    public final StateFactory sfactory;
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";
    public static final String XMLSchemaNamespace_old = "http://www.w3.org/2000/10/XMLSchema";
    private boolean issuedOldNamespaceWarning;
    private final Set<XMLSchemaSchema> definedSchemata;
    protected String chameleonTargetNamespace;
    public boolean doDuplicateDefinitionCheck;
    public static final String ERR_MAXOCCURS_IS_NECESSARY = "XMLSchemaReader.MaxOccursIsNecessary";
    public static final String ERR_UNIMPLEMENTED_FEATURE = "XMLSchemaReader.UnimplementedFeature";
    public static final String ERR_UNDECLARED_PREFIX = "XMLSchemaReader.UndeclaredPrefix";
    public static final String ERR_INCONSISTENT_TARGETNAMESPACE = "XMLSchemaReader.InconsistentTargetNamespace";
    public static final String ERR_IMPORTING_SAME_NAMESPACE = "XMLSchemaReader.ImportingSameNamespace";
    public static final String ERR_DUPLICATE_SCHEMA_DEFINITION = "XMLSchemaReader.DuplicateSchemaDefinition";
    public static final String ERR_UNDEFINED_ELEMENTTYPE = "XMLSchemaReader.UndefinedElementType";
    public static final String ERR_UNDEFINED_ATTRIBUTE_DECL = "XMLSchemaReader.UndefinedAttributeDecl";
    public static final String ERR_UNDEFINED_ATTRIBUTE_GROUP = "XMLSchemaReader.UndefinedAttributeGroup";
    public static final String ERR_UNDEFINED_COMPLEX_TYPE = "XMLSchemaReader.UndefinedComplexType";
    public static final String ERR_UNDEFINED_SIMPLE_TYPE = "XMLSchemaReader.UndefinedSimpleType";
    public static final String ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE = "XMLSchemaReader.UndefinedComplexOrSimpleType";
    public static final String ERR_UNDEFINED_ELEMENT_DECL = "XMLSchemaReader.UndefinedElementDecl";
    public static final String ERR_UNDEFINED_GROUP = "XMLSchemaReader.UndefinedGroup";
    public static final String ERR_UNDEFINED_SCHEMA = "XMLSchemaReader.UndefinedSchema";
    public static final String WRN_UNSUPPORTED_ANYELEMENT = "XMLSchemaReader.Warning.UnsupportedAnyElement";
    public static final String WRN_OBSOLETED_NAMESPACE = "XMLSchemaReader.Warning.ObsoletedNamespace";
    public static final String ERR_UNDEFINED_OR_FORWARD_REFERENCED_TYPE = "XMLSchemaReader.UndefinedOrForwardReferencedType";
    public static final String ERR_REDEFINE_UNDEFINED = "XMLSchemaReader.RedefineUndefined";
    public static final String ERR_DUPLICATE_ATTRIBUTE_DEFINITION = "XMLSchemaReader.DuplicateAttributeDefinition";
    public static final String ERR_DUPLICATE_COMPLEXTYPE_DEFINITION = "XMLSchemaReader.DuplicateComplexTypeDefinition";
    public static final String ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION = "XMLSchemaReader.DuplicateAttributeGroupDefinition";
    public static final String ERR_DUPLICATE_GROUP_DEFINITION = "XMLSchemaReader.DuplicateGroupDefinition";
    public static final String ERR_DUPLICATE_ELEMENT_DEFINITION = "XMLSchemaReader.DuplicateElementDefinition";
    public static final String ERR_DUPLICATE_IDENTITY_CONSTRAINT_DEFINITION = "XMLSchemaReader.DuplicateIdentityConstraintDefinition";
    public static final String ERR_BAD_XPATH = "XMLSchemaReader.BadXPath";
    public static final String ERR_UNDEFINED_KEY = "XMLSchemaReader.UndefinedKey";
    public static final String ERR_INVALID_BASETYPE_FOR_SIMPLECONTENT = "XMLSchemaReader.InvalidBasetypeForSimpleContent";
    public static final String ERR_KEY_FIELD_NUMBER_MISMATCH = "XMLSchemaReader.KeyFieldNumberMismatch";
    public static final String ERR_KEYREF_REFERRING_NON_KEY = "XMLSchemaReader.KeyrefReferringNonKey";
    public static final String ERR_UNRELATED_TYPES_IN_SUBSTITUTIONGROUP = "XMLSchemaReader.UnrelatedTypesInSubstitutionGroup";
    public static final String ERR_RECURSIVE_SUBSTITUTION_GROUP = "XMLSchemaReader.RecursiveSubstitutionGroup";
    public static final String WRN_IMPLICIT_URTYPE_FOR_ELEMENT = "XMLSchemaReader.Warning.ImplicitUrTypeForElement";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/XMLSchemaReader$RefResolver.class */
    public interface RefResolver {
        ReferenceContainer get(XMLSchemaSchema xMLSchemaSchema);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/XMLSchemaReader$Type.class */
    private interface Type {
        int getDerivationMethod();

        int getBlockValue();

        Type getBaseType();

        Object getCore();
    }

    public static XMLSchemaGrammar parse(String grammarURL, SAXParserFactory factory, GrammarReaderController controller) {
        XMLSchemaReader reader = new XMLSchemaReader(controller, factory);
        reader.parse(grammarURL);
        return reader.getResult();
    }

    public static XMLSchemaGrammar parse(InputSource grammar, SAXParserFactory factory, GrammarReaderController controller) {
        XMLSchemaReader reader = new XMLSchemaReader(controller, factory);
        reader.parse(grammar);
        return reader.getResult();
    }

    public static XMLSchemaGrammar parse(Source schema, GrammarReaderController controller) throws TransformerException {
        XMLSchemaReader reader = new XMLSchemaReader(controller);
        reader.parse(schema);
        return reader.getResult();
    }

    public XMLSchemaReader(GrammarReaderController controller) {
        this(controller, createParserFactory());
    }

    public XMLSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory) {
        this(controller, parserFactory, new ExpressionPool());
    }

    public XMLSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        this(controller, parserFactory, new StateFactory(), pool);
    }

    public XMLSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool) {
        super(controller, parserFactory, pool, new RootState(stateFactory.schemaHead(null)));
        this.parsedFiles = new HashMap();
        this.issuedOldNamespaceWarning = false;
        this.definedSchemata = new HashSet();
        this.chameleonTargetNamespace = null;
        this.doDuplicateDefinitionCheck = true;
        this.sfactory = stateFactory;
        ReferenceExp exp = new ReferenceExp(XMLSchemaSchemaLocationAttributes);
        this.xsiSchemaLocationExp = exp;
        exp.exp = pool.createSequence(pool.createOptional(pool.createAttribute(new SimpleNameClass("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"))), pool.createOptional(pool.createAttribute(new SimpleNameClass("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation"))));
        this.grammar = new XMLSchemaGrammar(pool);
        this.xsdSchema = new XMLSchemaSchema("http://www.w3.org/2001/XMLSchema", this.grammar);
        ElementPattern e = new ElementPattern(NameClass.ALL, Expression.nullSet);
        e.contentModel = pool.createMixed(pool.createZeroOrMore(pool.createChoice(pool.createAttribute(NameClass.ALL), e)));
        this.complexUrType = this.xsdSchema.complexTypes.getOrCreate("anyType");
        this.complexUrType.body.exp = e.contentModel;
        this.complexUrType.complexBaseType = this.complexUrType;
        this.complexUrType.derivationMethod = 1;
    }

    public static Schema getXmlSchemaForXmlSchema() {
        if (xmlSchema4XmlSchema == null) {
            try {
                XSFactoryImpl factory = new XSFactoryImpl();
                factory.setEntityResolver(new EntityResolver() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.1
                    @Override // org.xml.sax.EntityResolver
                    public InputSource resolveEntity(String publicId, String systemId) {
                        if (systemId.endsWith("datatypes.xsd")) {
                            return new InputSource(XMLSchemaReader.class.getResourceAsStream("datatypes.xsd"));
                        }
                        if (systemId.endsWith("xml.xsd")) {
                            return new InputSource(XMLSchemaReader.class.getResourceAsStream("xml.xsd"));
                        }
                        System.out.println("unexpected system ID: " + systemId);
                        return null;
                    }
                });
                xmlSchema4XmlSchema = factory.compileSchema(XMLSchemaReader.class.getResourceAsStream("xmlschema.xsd"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("unable to load schema-for-schema for W3C XML Schema");
            }
        }
        return xmlSchema4XmlSchema;
    }

    public final XMLSchemaGrammar getResult() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public Grammar getResultAsGrammar() {
        return getResult();
    }

    public XMLSchemaSchema getOrCreateSchema(String namespaceURI) {
        XMLSchemaSchema g = this.grammar.getByNamespace(namespaceURI);
        if (g != null) {
            return g;
        }
        XMLSchemaSchema g2 = new XMLSchemaSchema(namespaceURI, this.grammar);
        this.backwardReference.memorizeLink(g2);
        return g2;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/XMLSchemaReader$StateFactory.class */
    public static class StateFactory {
        public State schemaHead(String expectedNamespace) {
            return new SchemaState(expectedNamespace);
        }

        public State schemaIncluded(State parent, String expectedNamespace) {
            return new SchemaIncludedState(expectedNamespace);
        }

        public State simpleType(State parent, StartTagInfo tag) {
            return new SimpleTypeState();
        }

        public State all(State parent, StartTagInfo tag) {
            return new InterleaveState();
        }

        public State choice(State parent, StartTagInfo tag) {
            return new ChoiceState(true);
        }

        public State sequence(State parent, StartTagInfo tag) {
            return new SequenceState(true);
        }

        public State group(State parent, StartTagInfo tag) {
            return new GroupState();
        }

        public State complexTypeDecl(State parent, StartTagInfo tag) {
            return new ComplexTypeDeclState();
        }

        public State attribute(State parent, StartTagInfo tag) {
            return new AttributeState();
        }

        public State attributeGroup(State parent, StartTagInfo tag) {
            return new AttributeGroupState();
        }

        public State elementDecl(State parent, StartTagInfo tag) {
            return new ElementDeclState();
        }

        public State elementRef(State parent, StartTagInfo tag) {
            return new ElementRefState();
        }

        public State any(State parent, StartTagInfo tag) {
            return new AnyElementState();
        }

        public State anyAttribute(State parent, StartTagInfo tag) {
            return new AnyAttributeState();
        }

        public State include(State parent, StartTagInfo tag) {
            return new IncludeState();
        }

        public State import_(State parent, StartTagInfo tag) {
            return new ImportState();
        }

        public State redefine(State parent, StartTagInfo tag) {
            return new RedefineState();
        }

        public State notation(State parent, StartTagInfo tag) {
            return new IgnoreState();
        }

        public State facets(State parent, StartTagInfo tag) {
            return new FacetState();
        }

        public State unique(State parent, StartTagInfo tag) {
            return new IdentityConstraintState();
        }

        public State key(State parent, StartTagInfo tag) {
            return new IdentityConstraintState();
        }

        public State keyref(State parent, StartTagInfo tag) {
            return new IdentityConstraintState();
        }

        public State complexContent(State parent, StartTagInfo tag, ComplexTypeExp decl) {
            return new ComplexContentState(decl);
        }

        public State complexRst(State parent, StartTagInfo tag, ComplexTypeExp decl) {
            return new ComplexContentBodyState(decl, false);
        }

        public State complexExt(State parent, StartTagInfo tag, ComplexTypeExp decl) {
            return new ComplexContentBodyState(decl, true);
        }

        public State simpleContent(State parent, StartTagInfo tag, ComplexTypeExp decl) {
            return new SimpleContentState(decl);
        }

        public State simpleRst(State parent, StartTagInfo tag, ComplexTypeExp decl) {
            return new SimpleContentRestrictionState(decl);
        }

        public State simpleExt(State parent, StartTagInfo tag, ComplexTypeExp decl) {
            return new SimpleContentExtensionState(decl);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("element")) {
            return tag.containsAttribute("ref") ? this.sfactory.elementRef(parent, tag) : this.sfactory.elementDecl(parent, tag);
        }
        return tag.localName.equals(Languages.ANY) ? this.sfactory.any(parent, tag) : createModelGroupState(parent, tag);
    }

    public State createModelGroupState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("all")) {
            return this.sfactory.all(parent, tag);
        }
        if (tag.localName.equals("choice")) {
            return this.sfactory.choice(parent, tag);
        }
        if (tag.localName.equals("sequence")) {
            return this.sfactory.sequence(parent, tag);
        }
        if (tag.localName.equals("group")) {
            return this.sfactory.group(parent, tag);
        }
        return null;
    }

    public State createAttributeState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("attribute")) {
            return this.sfactory.attribute(parent, tag);
        }
        if (tag.localName.equals("anyAttribute")) {
            return this.sfactory.anyAttribute(parent, tag);
        }
        if (tag.localName.equals("attributeGroup")) {
            return this.sfactory.attributeGroup(parent, tag);
        }
        return null;
    }

    public State createFacetState(State parent, StartTagInfo tag) {
        if (FacetState.facetNames.contains(tag.localName)) {
            return this.sfactory.facets(parent, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected boolean isGrammarElement(StartTagInfo tag) {
        return isSchemaNamespace(tag.namespaceURI) && !tag.localName.equals("annotation");
    }

    public final void markSchemaAsDefined(XMLSchemaSchema schema) {
        this.definedSchemata.add(schema);
    }

    public final boolean isSchemaDefined(XMLSchemaSchema schema) {
        return this.definedSchemata.contains(schema);
    }

    protected String resolveNamespaceOfAttributeDecl(String formValue) {
        return resolveNamespaceOfDeclaration(formValue, this.attributeFormDefault);
    }

    protected String resolveNamespaceOfElementDecl(String formValue) {
        return resolveNamespaceOfDeclaration(formValue, this.elementFormDefault);
    }

    private String resolveNamespaceOfDeclaration(String formValue, String defaultValue) {
        if ("qualified".equals(formValue)) {
            return this.currentSchema.targetNamespace;
        }
        if ("unqualified".equals(formValue)) {
            return "";
        }
        if (formValue != null) {
            reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "form", formValue);
            return "$$recover$$";
        }
        return defaultValue;
    }

    public XSDatatype resolveBuiltinDataType(String typeLocalName) {
        try {
            return DatatypeFactory.getTypeByName(typeLocalName);
        } catch (DatatypeException e) {
            return null;
        }
    }

    public SimpleTypeExp resolveBuiltinSimpleType(String typeLocalName) {
        try {
            XSDatatype dt = DatatypeFactory.getTypeByName(typeLocalName);
            SimpleTypeExp sexp = this.xsdSchema.simpleTypes.getOrCreate(typeLocalName);
            if (!sexp.isDefined()) {
                sexp.set(new XSDatatypeExp(dt, this.pool));
            }
            return sexp;
        } catch (DatatypeException e) {
            return null;
        }
    }

    public boolean isSchemaNamespace(String ns) {
        if (ns.equals("http://www.w3.org/2001/XMLSchema")) {
            return true;
        }
        if (ns.equals(XMLSchemaNamespace_old)) {
            if (!this.issuedOldNamespaceWarning) {
                reportWarning(WRN_OBSOLETED_NAMESPACE, null);
            }
            this.issuedOldNamespaceWarning = true;
            return true;
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver
    public XSDatatypeExp resolveXSDatatype(String typeQName) {
        XSDatatype dt;
        String[] r = splitQName(typeQName);
        if (r == null) {
            reportError(ERR_UNDECLARED_PREFIX, typeQName);
            return new XSDatatypeExp(StringType.theInstance, this.pool);
        }
        if (isSchemaNamespace(r[0]) && (dt = resolveBuiltinDataType(r[1])) != null) {
            return new XSDatatypeExp(dt, this.pool);
        }
        final SimpleTypeExp sexp = getOrCreateSchema(r[0]).simpleTypes.getOrCreate(r[1]);
        this.backwardReference.memorizeLink(sexp);
        return new XSDatatypeExp(r[0], r[1], this, new XSDatatypeExp.Renderer() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.2
            @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.Renderer
            public XSDatatype render(XSDatatypeExp.RenderingContext context) {
                if (sexp.getType() != null) {
                    return sexp.getType().getType(context);
                }
                return StringType.theInstance;
            }
        });
    }

    public Expression resolveQNameRef(StartTagInfo tag, String attName, RefResolver resolver) {
        String refQName = tag.getAttribute(attName);
        if (refQName == null) {
            reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, tag.qName, attName);
            return null;
        }
        String[] r = splitQName(refQName);
        if (r == null) {
            reportError(ERR_UNDECLARED_PREFIX, refQName);
            return null;
        }
        Expression e = resolver.get(getOrCreateSchema(r[0]))._getOrCreate(r[1]);
        this.backwardReference.memorizeLink(e);
        return e;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public String[] splitQName(String qName) {
        String[] r = super.splitQName(qName);
        if (r == null) {
            int idx = qName.indexOf(58);
            if (idx > 0) {
                String prefix = qName.substring(0, idx);
                String uri = this.additionalNamespaceMap.get(prefix);
                if (uri != null) {
                    return new String[]{uri, qName.substring(idx + 1), qName};
                }
                return null;
            }
            return null;
        }
        if (r[0].length() == 0 && this.chameleonTargetNamespace != null) {
            r[0] = this.chameleonTargetNamespace;
        }
        return r;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected Expression interceptExpression(State state, Expression exp) {
        if ((state instanceof SequenceState) || (state instanceof ChoiceState) || (state instanceof InterleaveState) || (state instanceof AnyElementState) || (state instanceof ElementDeclState) || (state instanceof ElementRefState) || (state instanceof GroupState)) {
            return processOccurs(state.getStartTag(), exp);
        }
        return exp;
    }

    public Expression processOccurs(StartTagInfo startTag, Expression item) {
        int maxOccursValue;
        String minOccurs = startTag.getAttribute("minOccurs");
        int minOccursValue = 1;
        if (minOccurs != null) {
            try {
                minOccursValue = Integer.parseInt(minOccurs);
                if (minOccursValue < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "minOccurs", minOccurs);
                minOccursValue = 1;
            }
        }
        String maxOccurs = startTag.getAttribute("maxOccurs");
        if (maxOccurs == null) {
            if (minOccursValue > 1) {
                reportError(ERR_MAXOCCURS_IS_NECESSARY);
            }
            maxOccursValue = 1;
        } else if (maxOccurs.equals("unbounded")) {
            maxOccursValue = -1;
        } else {
            try {
                maxOccursValue = Integer.parseInt(maxOccurs);
                if (maxOccursValue < 0 || maxOccursValue < minOccursValue) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e2) {
                reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "maxOccurs", maxOccurs);
                maxOccursValue = 1;
            }
        }
        return processOccurs(item, minOccursValue, maxOccursValue);
    }

    public Expression processOccurs(Expression item, int minOccurs, int maxOccurs) {
        Expression precise = _processOccurs(item, minOccurs, maxOccurs);
        if (maxOccurs == 1) {
            return precise;
        }
        return (maxOccurs != -1 || minOccurs > 1) ? new OccurrenceExp(precise, maxOccurs, minOccurs, item) : precise;
    }

    private Expression _processOccurs(Expression item, int minOccurs, int maxOccurs) {
        Expression exp = Expression.epsilon;
        for (int i = 0; i < minOccurs; i++) {
            exp = this.pool.createSequence(item, exp);
        }
        if (maxOccurs == -1) {
            if (minOccurs == 1) {
                return this.pool.createOneOrMore(item);
            }
            return this.pool.createSequence(exp, this.pool.createZeroOrMore(item));
        }
        Expression tmp = Expression.epsilon;
        for (int i2 = minOccurs; i2 < maxOccurs; i2++) {
            tmp = this.pool.createOptional(this.pool.createSequence(item, tmp));
        }
        return this.pool.createSequence(exp, tmp);
    }

    protected void switchSource(State sourceState, State newRootState) throws AbortException {
        String schemaLocation = sourceState.getStartTag().getAttribute("schemaLocation");
        if (schemaLocation == null) {
            LSResourceResolver resolver = this.controller.getLSResourceResolver();
            if (resolver != null) {
                String namespaceURI = sourceState.getStartTag().getAttribute("namespace");
                if (namespaceURI == null) {
                    reportError("XmlSchemaReader.noLocation", sourceState.getStartTag().qName);
                    return;
                }
                LSInput resolved = resolver.resolveResource("http://www.w3.org/2001/XMLSchema", namespaceURI, null, null, sourceState.getBaseURI());
                if (resolved == null) {
                    reportError("XmlSchemaReader.unresolvedSchema", sourceState.getStartTag().qName, namespaceURI);
                    return;
                } else {
                    Source source = GrammarReader.inputSourceFromLSInput(resolved);
                    switchSource(source, newRootState);
                    return;
                }
            }
            return;
        }
        switchSource(sourceState, schemaLocation, newRootState);
    }

    protected void wrapUp() {
        markSchemaAsDefined(this.xsdSchema);
        Expression grammarTopLevel = Expression.nullSet;
        Iterator itr = this.grammar.iterateSchemas();
        while (itr.hasNext()) {
            XMLSchemaSchema schema = (XMLSchemaSchema) itr.next();
            if (!isSchemaDefined(schema)) {
                reportError(this.backwardReference.getReferer(schema), ERR_UNDEFINED_SCHEMA, new Object[]{schema.targetNamespace});
                return;
            }
            detectUndefinedOnes(schema.attributeDecls, ERR_UNDEFINED_ATTRIBUTE_DECL);
            detectUndefinedOnes(schema.attributeGroups, ERR_UNDEFINED_ATTRIBUTE_GROUP);
            detectUndefinedOnes(schema.complexTypes, ERR_UNDEFINED_COMPLEX_TYPE);
            detectUndefinedOnes(schema.elementDecls, ERR_UNDEFINED_ELEMENT_DECL);
            detectUndefinedOnes(schema.groupDecls, ERR_UNDEFINED_GROUP);
            detectUndefinedOnes(schema.simpleTypes, ERR_UNDEFINED_SIMPLE_TYPE);
            Expression exp = Expression.nullSet;
            for (ReferenceExp referenceExp : schema.elementDecls.getAll()) {
                exp = this.pool.createChoice(exp, referenceExp);
            }
            schema.topLevel = exp;
            grammarTopLevel = this.pool.createChoice(grammarTopLevel, exp);
        }
        this.grammar.topLevel = grammarTopLevel;
        runBackPatchJob();
        Set<ElementDeclExp> recursiveSubstBuffer = new HashSet<>();
        Iterator itr2 = this.grammar.iterateSchemas();
        while (itr2.hasNext()) {
            ReferenceExp[] elems = ((XMLSchemaSchema) itr2.next()).elementDecls.getAll();
            for (ReferenceExp referenceExp2 : elems) {
                ElementDeclExp e = (ElementDeclExp) referenceExp2;
                recursiveSubstBuffer.clear();
                if (!this.controller.hadError()) {
                    ElementDeclExp elementDeclExp = e.substitutionAffiliation;
                    while (true) {
                        ElementDeclExp c = elementDeclExp;
                        if (c == null) {
                            break;
                        }
                        if (!recursiveSubstBuffer.add(c)) {
                            reportError(new Locator[]{getDeclaredLocationOf(c), getDeclaredLocationOf(e)}, ERR_RECURSIVE_SUBSTITUTION_GROUP, new Object[]{c.name, e.name});
                            break;
                        }
                        if (isSubstitutable(c, e)) {
                            if (Debug.debug) {
                                System.out.println(c.name + "<-" + e.name);
                            }
                            c.substitutions.exp = this.pool.createChoice(c.substitutions.exp, e.body);
                        } else if (Debug.debug) {
                            System.out.println(c.name + "<-X-" + e.name);
                        }
                        elementDeclExp = c.substitutionAffiliation;
                    }
                }
            }
        }
        if (this.controller.hadError()) {
            return;
        }
        RunAwayExpressionChecker.check(this, this.grammar.topLevel);
        if (!this.controller.hadError()) {
            AttributeWildcardComputer.compute(this, this.grammar.topLevel);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Type getType(XMLSchemaTypeExp exp) {
        if (exp instanceof ComplexTypeExp) {
            final ComplexTypeExp cexp = (ComplexTypeExp) exp;
            return new Type() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.3
                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
                public int getDerivationMethod() {
                    return cexp.derivationMethod;
                }

                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
                public int getBlockValue() {
                    return cexp.block;
                }

                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
                public Type getBaseType() {
                    if (cexp.complexBaseType != null) {
                        return XMLSchemaReader.this.getType(cexp.complexBaseType);
                    }
                    return cexp.simpleBaseType != null ? XMLSchemaReader.this.getType(cexp.simpleBaseType.getCreatedType()) : XMLSchemaReader.this.getType(XMLSchemaReader.this.complexUrType);
                }

                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
                public Object getCore() {
                    return cexp;
                }
            };
        }
        return getType(((SimpleTypeExp) exp).getDatatype());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Type getType(final XSDatatype dt) {
        if (dt == null) {
            throw new Error();
        }
        return new Type() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.4
            @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
            public int getDerivationMethod() {
                return 1;
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
            public int getBlockValue() {
                return 0;
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
            public Type getBaseType() {
                XSDatatype base = dt.getBaseType();
                return base == null ? XMLSchemaReader.this.getType(XMLSchemaReader.this.complexUrType) : XMLSchemaReader.this.getType(base);
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.Type
            public Object getCore() {
                return dt;
            }
        };
    }

    private boolean isSubstitutable(ElementDeclExp c, ElementDeclExp d) {
        if (c.isSubstitutionBlocked()) {
            return false;
        }
        Type cType = getType(c.getTypeDefinition());
        int constraint = c.block;
        int derivationMethod = 0;
        for (Type dType = getType(d.getTypeDefinition()); dType.getCore() != cType.getCore(); dType = dType.getBaseType()) {
            derivationMethod |= dType.getDerivationMethod();
            constraint |= dType.getBlockValue();
            if (dType.getCore() == this.complexUrType) {
                reportError(new Locator[]{getDeclaredLocationOf(c), getDeclaredLocationOf(d)}, ERR_UNRELATED_TYPES_IN_SUBSTITUTIONGROUP, new Object[]{c.name, d.name});
                return false;
            }
        }
        if ((constraint & derivationMethod) == 0) {
            return true;
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        String format;
        try {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.xmlschema.Messages").getString(propertyName);
        } catch (Exception e) {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.Messages").getString(propertyName);
        }
        return MessageFormat.format(format, args);
    }

    public Map<String, String> getAdditionalNamespaceMap() {
        return this.additionalNamespaceMap;
    }

    public void setAdditionalNamespaceMap(Map<String, String> additionalNamespaceMap) {
        this.additionalNamespaceMap = additionalNamespaceMap;
    }
}
