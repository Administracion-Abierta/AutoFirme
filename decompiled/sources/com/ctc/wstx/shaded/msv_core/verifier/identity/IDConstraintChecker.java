package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyRefConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.util.LightStack;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.ErrorInfo;
import com.ctc.wstx.shaded.msv_core.verifier.ValidityViolation;
import com.ctc.wstx.shaded.msv_core.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.XSREDocDecl;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/IDConstraintChecker.class */
public class IDConstraintChecker extends Verifier {
    protected final XMLSchemaGrammar grammar;
    protected final Vector matchers;
    private final Map keyValues;
    private final Map referenceScope;
    private final Map activeScopes;
    public static final String ERR_UNMATCHED_KEY_FIELD = "IdentityConstraint.UnmatchedKeyField";
    public static final String ERR_NOT_UNIQUE = "IdentityConstraint.NotUnique";
    public static final String ERR_NOT_UNIQUE_DIAG = "IdentityConstraint.NotUnique.Diag";
    public static final String ERR_DOUBLE_MATCH = "IdentityConstraint.DoubleMatch";
    public static final String ERR_UNDEFINED_KEY = "IdentityConstraint.UndefinedKey";

    public IDConstraintChecker(XMLSchemaGrammar grammar, ErrorHandler errorHandler) {
        super(new XSREDocDecl(grammar), errorHandler);
        this.matchers = new Vector();
        this.keyValues = new HashMap();
        this.referenceScope = new HashMap();
        this.activeScopes = new HashMap();
        this.grammar = grammar;
    }

    protected void add(Matcher matcher) {
        this.matchers.add(matcher);
    }

    protected void remove(Matcher matcher) {
        this.matchers.remove(matcher);
    }

    protected SelectorMatcher getActiveScope(IdentityConstraint c) {
        LightStack s = (LightStack) this.activeScopes.get(c);
        if (s == null || s.size() == 0) {
            return null;
        }
        return (SelectorMatcher) s.top();
    }

    protected void pushActiveScope(IdentityConstraint c, SelectorMatcher matcher) {
        LightStack s = (LightStack) this.activeScopes.get(c);
        if (s == null) {
            Map map = this.activeScopes;
            LightStack lightStack = new LightStack();
            s = lightStack;
            map.put(c, lightStack);
        }
        s.push(matcher);
    }

    protected void popActiveScope(IdentityConstraint c, SelectorMatcher matcher) {
        LightStack s = (LightStack) this.activeScopes.get(c);
        if (s == null) {
            throw new Error();
        }
        if (s.pop() != matcher) {
            throw new Error();
        }
    }

    protected boolean addKeyValue(SelectorMatcher scope, KeyValue value) {
        Set keys = (Set) this.keyValues.get(scope);
        if (keys == null) {
            Map map = this.keyValues;
            HashSet hashSet = new HashSet();
            keys = hashSet;
            map.put(scope, hashSet);
        }
        return keys.add(value);
    }

    protected KeyValue[] getKeyValues(SelectorMatcher scope) {
        Set keys = (Set) this.keyValues.get(scope);
        if (keys == null) {
            return new KeyValue[0];
        }
        return (KeyValue[]) keys.toArray(new KeyValue[keys.size()]);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        super.startDocument();
        this.keyValues.clear();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        super.endDocument();
        Map.Entry[] scopes = (Map.Entry[]) this.keyValues.entrySet().toArray(new Map.Entry[this.keyValues.size()]);
        if (Debug.debug) {
            System.out.println("key/keyref check: there are " + this.keyValues.size() + " scope(s)");
        }
        for (int i = 0; i < scopes.length; i++) {
            SelectorMatcher key = (SelectorMatcher) scopes[i].getKey();
            Set value = (Set) scopes[i].getValue();
            if (key.idConst instanceof KeyRefConstraint) {
                Set keys = (Set) this.keyValues.get(this.referenceScope.get(key));
                KeyValue[] keyrefs = (KeyValue[]) value.toArray(new KeyValue[value.size()]);
                for (int j = 0; j < keyrefs.length; j++) {
                    if (keys == null || !keys.contains(keyrefs[j])) {
                        reportError(keyrefs[j].locator, null, ERR_UNDEFINED_KEY, new Object[]{key.idConst.namespaceURI, key.idConst.localName});
                    }
                }
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier
    protected void onNextAcceptorReady(StartTagInfo sti, Acceptor next) throws SAXException {
        int len = this.matchers.size();
        for (int i = 0; i < len; i++) {
            ((Matcher) this.matchers.get(i)).startElement(sti.namespaceURI, sti.localName);
        }
        Object e = next.getOwnerType();
        if (e instanceof ElementDeclExp.XSElementExp) {
            ElementDeclExp.XSElementExp exp = (ElementDeclExp.XSElementExp) e;
            if (exp.identityConstraints != null) {
                int m = exp.identityConstraints.size();
                for (int i2 = 0; i2 < m; i2++) {
                    add(new SelectorMatcher(this, (IdentityConstraint) exp.identityConstraints.get(i2), sti.namespaceURI, sti.localName));
                }
                for (int i3 = 0; i3 < m; i3++) {
                    IdentityConstraint c = (IdentityConstraint) exp.identityConstraints.get(i3);
                    if (c instanceof KeyRefConstraint) {
                        SelectorMatcher keyScope = getActiveScope(((KeyRefConstraint) c).key);
                        if (keyScope == null) {
                        }
                        this.referenceScope.put(getActiveScope(c), keyScope);
                    }
                }
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier
    protected Datatype[] feedAttribute(Acceptor child, String uri, String localName, String qName, String value) throws SAXException {
        Datatype[] result = super.feedAttribute(child, uri, localName, qName, value);
        int len = this.matchers.size();
        for (int i = 0; i < len; i++) {
            Matcher m = (Matcher) this.matchers.get(i);
            m.onAttribute(uri, localName, value, (result == null || result.length == 0) ? null : result[0]);
        }
        return result;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier, org.xml.sax.ContentHandler
    public void characters(char[] buf, int start, int len) throws SAXException {
        super.characters(buf, start, len);
        int m = this.matchers.size();
        for (int i = 0; i < m; i++) {
            ((Matcher) this.matchers.get(i)).characters(buf, start, len);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier, com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier, org.xml.sax.ContentHandler
    public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
        super.endElement(namespaceUri, localName, qName);
        Datatype[] lastType = getLastCharacterType();
        Datatype dt = (lastType == null || lastType.length == 0) ? null : getLastCharacterType()[0];
        int len = this.matchers.size();
        for (int i = len - 1; i >= 0; i--) {
            ((Matcher) this.matchers.get(i)).endElement(dt);
        }
    }

    protected void reportError(ErrorInfo ei, String propKey, Object[] args) throws SAXException {
        reportError(getLocator(), ei, propKey, args);
    }

    protected void reportError(Locator loc, ErrorInfo ei, String propKey, Object[] args) throws SAXException {
        this.hadError = true;
        this.errorHandler.error(new ValidityViolation(loc, localizeMessage(propKey, args), ei));
    }

    public static String localizeMessage(String propertyName, Object arg) {
        return localizeMessage(propertyName, new Object[]{arg});
    }

    public static String localizeMessage(String propertyName, Object[] args) {
        String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.verifier.identity.Messages").getString(propertyName);
        return MessageFormat.format(format, args);
    }
}
