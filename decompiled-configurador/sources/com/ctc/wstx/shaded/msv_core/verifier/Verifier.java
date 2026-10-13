package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/Verifier.class */
public class Verifier extends AbstractVerifier implements IVerifier {
    protected Acceptor current;
    protected ErrorHandler errorHandler;
    protected boolean hadError;
    private boolean isFinished;
    protected final DocumentDeclaration docDecl;
    private static final int DEFAULT_PANIC_LEVEL = 3;
    private Set duplicateIds;
    public static final String ERR_UNEXPECTED_TEXT = "Verifier.Error.UnexpectedText";
    public static final String ERR_UNEXPECTED_ATTRIBUTE = "Verifier.Error.UnexpectedAttribute";
    public static final String ERR_MISSING_ATTRIBUTE = "Verifier.Error.MissingAttribute";
    public static final String ERR_UNEXPECTED_STARTTAG = "Verifier.Error.UnexpectedStartTag";
    public static final String ERR_UNCOMPLETED_CONTENT = "Verifier.Error.UncompletedContent";
    public static final String ERR_UNEXPECTED_ELEMENT = "Verifier.Error.UnexpectedElement";
    public static final String ERR_UNSOLD_IDREF = "Verifier.Error.UnsoldIDREF";
    public static final String ERR_DUPLICATE_ID = "Verifier.Error.DuplicateId";
    Context stack = null;
    private int stringCareLevel = 2;
    private StringBuffer text = new StringBuffer();
    private final StartTagInfo sti = new StartTagInfo((String) null, (String) null, (String) null, (Attributes) null, (IDContextProvider2) null);
    protected int panicLevel = 0;
    private int initialPanicLevel = 3;
    private final DatatypeRef characterType = new DatatypeRef();
    private final DatatypeRef attributeType = new DatatypeRef();

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/Verifier$Context.class */
    private static final class Context {
        final Context previous;
        final Acceptor acceptor;
        final int stringCareLevel;
        int panicLevel;

        Context(Context prev, Acceptor acc, int scl, int plv) {
            this.previous = prev;
            this.acceptor = acc;
            this.stringCareLevel = scl;
            this.panicLevel = plv;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier, com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler
    public final boolean isValid() {
        return !this.hadError && this.isFinished;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final void setPanicMode(boolean usePanicMode) {
        this.initialPanicLevel = usePanicMode ? 3 : 0;
    }

    public Verifier(DocumentDeclaration documentDecl, ErrorHandler errorHandler) {
        this.docDecl = documentDecl;
        this.errorHandler = errorHandler;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public Datatype[] getLastCharacterType() {
        return this.characterType.types;
    }

    protected void verifyText() throws SAXException {
        this.characterType.types = null;
        switch (this.stringCareLevel) {
            case 0:
                int len = this.text.length();
                for (int i = 0; i < len; i++) {
                    char ch = this.text.charAt(i);
                    if (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n') {
                        onError(null, localizeMessage(ERR_UNEXPECTED_TEXT, null), new ErrorInfo.BadText(this.text));
                    }
                    break;
                }
                break;
            case 1:
                if (this.text.length() != 0) {
                    throw new Error();
                }
                return;
            case 2:
                String txt = new String(this.text);
                if (!this.current.onText2(txt, this, null, this.characterType)) {
                    StringRef err = new StringRef();
                    this.characterType.types = null;
                    this.current.onText2(txt, this, err, this.characterType);
                    onError(err, localizeMessage(ERR_UNEXPECTED_TEXT, null), new ErrorInfo.BadText(this.text));
                }
                break;
            default:
                throw new Error();
        }
        if (this.text.length() != 0) {
            this.text = new StringBuffer();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier, org.xml.sax.ContentHandler
    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        if (namespaceUri == null) {
            namespaceUri = "";
        }
        if (localName == null || localName.length() == 0) {
            localName = qName;
        }
        if (qName == null || qName.length() == 0) {
            qName = localName;
        }
        super.startElement(namespaceUri, localName, qName, atts);
        if (Debug.debug) {
            System.out.println("\n-- startElement(" + qName + ")" + this.locator.getLineNumber() + ":" + this.locator.getColumnNumber());
        }
        verifyText();
        this.stack = new Context(this.stack, this.current, this.stringCareLevel, this.panicLevel);
        this.sti.reinit(namespaceUri, localName, qName, atts, this);
        Acceptor next = this.current.createChildAcceptor(this.sti, null);
        this.panicLevel = Math.max(this.panicLevel - 1, 0);
        if (next == null) {
            if (Debug.debug) {
                System.out.println("-- no children accepted: error recovery");
            }
            StringRef ref = new StringRef();
            next = this.current.createChildAcceptor(this.sti, ref);
            ValidityViolation vv = onError(ref, localizeMessage(ERR_UNEXPECTED_STARTTAG, new Object[]{qName}), new ErrorInfo.BadTagName(this.sti));
            if (next == null) {
                if (Debug.debug) {
                    System.out.println("-- unable to recover");
                }
                throw new ValidationUnrecoverableException(vv);
            }
        }
        onNextAcceptorReady(this.sti, next);
        int len = atts.getLength();
        for (int i = 0; i < len; i++) {
            feedAttribute(next, atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getValue(i));
        }
        if (!next.onEndAttributes(this.sti, null)) {
            if (Debug.debug) {
                System.out.println("-- required attributes missing: error recovery");
            }
            StringRef ref2 = new StringRef();
            next.onEndAttributes(this.sti, ref2);
            onError(ref2, localizeMessage(ERR_MISSING_ATTRIBUTE, new Object[]{qName}), new ErrorInfo.MissingAttribute(this.sti));
        }
        this.stack.panicLevel = this.panicLevel;
        this.stringCareLevel = next.getStringCareLevel();
        if (this.stringCareLevel == 1) {
            this.characterType.types = new Datatype[]{StringType.theInstance};
        }
        this.current = next;
    }

    protected void onNextAcceptorReady(StartTagInfo sti, Acceptor nextAcceptor) throws SAXException {
    }

    protected Datatype[] feedAttribute(Acceptor child, String uri, String localName, String qName, String value) throws SAXException {
        if (uri == null) {
            uri = "";
        }
        if (localName == null || localName.length() == 0) {
            localName = qName;
        }
        if (qName == null || qName.length() == 0) {
            qName = localName;
        }
        if (qName.startsWith("xmlns:") || qName.equals(CanonicalizerBase.XMLNS)) {
            return new Datatype[0];
        }
        if (Debug.debug) {
            System.out.println("-- processing attribute: @" + qName);
        }
        this.attributeType.types = null;
        if (!child.onAttribute2(uri, localName, qName, value, this, null, this.attributeType)) {
            if (Debug.debug) {
                System.out.println("-- bad attribute: error recovery");
            }
            StringRef ref = new StringRef();
            child.onAttribute2(uri, localName, qName, value, this, ref, null);
            onError(ref, localizeMessage(ERR_UNEXPECTED_ATTRIBUTE, new Object[]{qName}), new ErrorInfo.BadAttribute(this.sti, qName, uri, localName, value));
        }
        return this.attributeType.types;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier, org.xml.sax.ContentHandler
    public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
        if (namespaceUri == null) {
            namespaceUri = "";
        }
        if (localName == null || localName.length() == 0) {
            localName = qName;
        }
        if (qName == null || qName.length() == 0) {
            qName = localName;
        }
        if (Debug.debug) {
            System.out.println("\n-- endElement(" + qName + ")" + this.locator.getLineNumber() + ":" + this.locator.getColumnNumber());
        }
        verifyText();
        if (!this.current.isAcceptState(null) && this.panicLevel == 0) {
            StringRef errRef = new StringRef();
            this.current.isAcceptState(errRef);
            onError(errRef, localizeMessage(ERR_UNCOMPLETED_CONTENT, new Object[]{qName}), new ErrorInfo.IncompleteContentModel(qName, namespaceUri, localName));
        }
        Acceptor child = this.current;
        this.current = this.stack.acceptor;
        this.stringCareLevel = this.stack.stringCareLevel;
        this.panicLevel = Math.max(this.panicLevel, this.stack.panicLevel);
        this.stack = this.stack.previous;
        if (!this.current.stepForward(child, null)) {
            StringRef ref = new StringRef();
            this.current.stepForward(child, ref);
            onError(ref, localizeMessage(ERR_UNEXPECTED_ELEMENT, new Object[]{qName}), null);
        } else {
            this.panicLevel = Math.max(this.panicLevel - 1, 0);
        }
        super.endElement(namespaceUri, localName, qName);
    }

    protected ValidityViolation onError(StringRef ref, String defaultMsg, ErrorInfo ei) throws SAXException {
        if (ref == null) {
            return onError(defaultMsg, ei);
        }
        if (ref.str == null) {
            return onError(defaultMsg, ei);
        }
        return onError(ref.str, ei);
    }

    protected ValidityViolation onError(String msg, ErrorInfo ei) throws SAXException {
        ValidityViolation vv = new ValidityViolation(this.locator, msg, ei);
        this.hadError = true;
        if (this.errorHandler != null && this.panicLevel == 0) {
            this.errorHandler.error(vv);
        }
        this.panicLevel = this.initialPanicLevel;
        return vv;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public Object getCurrentElementType() {
        return this.current.getOwnerType();
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] buf, int start, int len) throws SAXException {
        if (this.stringCareLevel != 1) {
            this.text.append(buf, start, len);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] buf, int start, int len) throws SAXException {
        if (this.stringCareLevel != 1 && this.stringCareLevel != 0) {
            this.text.append(buf, start, len);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier
    protected void init() {
        super.init();
        this.hadError = false;
        this.isFinished = false;
        this.text = new StringBuffer();
        this.stack = null;
        if (this.duplicateIds != null) {
            this.duplicateIds.clear();
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        init();
        this.current = this.docDecl.createAcceptor();
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        if (this.performIDcheck) {
            if (!this.ids.keySet().containsAll(this.idrefs)) {
                this.hadError = true;
                for (Object idref : this.idrefs) {
                    if (!this.ids.keySet().contains(idref)) {
                        onError(localizeMessage(ERR_UNSOLD_IDREF, new Object[]{idref}), null);
                    }
                }
            }
            if (this.duplicateIds != null) {
                for (Object id : this.duplicateIds) {
                    onError(localizeMessage(ERR_DUPLICATE_ID, new Object[]{id}), null);
                }
            }
        }
        this.isFinished = true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier
    public void onDuplicateId(String id) {
        if (this.duplicateIds == null) {
            this.duplicateIds = new HashSet();
        }
        this.duplicateIds.add(id);
    }

    public static String localizeMessage(String propertyName, Object[] args) {
        String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.verifier.Messages").getString(propertyName);
        return MessageFormat.format(format, args);
    }
}
