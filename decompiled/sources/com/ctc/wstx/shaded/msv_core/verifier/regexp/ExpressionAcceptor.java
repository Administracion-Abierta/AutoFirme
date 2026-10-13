package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionPrinter;
import com.ctc.wstx.shaded.msv_core.grammar.util.IDContextProviderWrapper;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ExpressionAcceptor.class */
public abstract class ExpressionAcceptor implements Acceptor {
    private Expression expression;
    protected final REDocumentDeclaration docDecl;
    protected final boolean ignoreUndeclaredAttributes;

    protected abstract Acceptor createAcceptor(Expression expression, Expression expression2, ElementExp[] elementExpArr, int i);

    public Expression getExpression() {
        return this.expression;
    }

    public ExpressionAcceptor(REDocumentDeclaration docDecl, Expression exp, boolean ignoreUndeclaredAttributes) {
        this.docDecl = docDecl;
        this.expression = exp;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Acceptor createChildAcceptor(StartTagInfo tag, StringRef errRef) {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        CombinedChildContentExpCreator.ExpressionPair e = cccc.get(this.expression, tag);
        if (e.content != Expression.nullSet) {
            if (Debug.debug) {
                System.out.println("accept start tag <" + tag.qName + ">. combined content pattern is");
                System.out.println(ExpressionPrinter.printContentModel(e.content));
                if (e.continuation != null) {
                    System.out.println("continuation is:\n" + ExpressionPrinter.printContentModel(e.continuation));
                } else {
                    System.out.println("no continuation");
                }
            }
            return createAcceptor(e.content, e.continuation, cccc.getMatchedElements(), cccc.numMatchedElements());
        }
        if (errRef == null) {
            return null;
        }
        errRef.str = diagnoseBadTagName(tag);
        if (errRef.str == null) {
            errRef.str = this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_TAGNAME_GENERIC, tag.qName);
        }
        return createRecoveryAcceptors();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public final boolean onAttribute(String namespaceURI, String localName, String qName, String value, IDContextProvider context, StringRef refErr, DatatypeRef refType) {
        return onAttribute2(namespaceURI, localName, qName, value, IDContextProviderWrapper.create(context), refErr, refType);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public final boolean onAttribute2(String namespaceURI, String localName, String qName, String value, IDContextProvider2 context, StringRef refErr, DatatypeRef refType) {
        this.docDecl.attToken.reinit(namespaceURI, localName, qName, new StringToken(this.docDecl, value, context, refType));
        return onAttribute(this.docDecl.attToken, refErr);
    }

    protected boolean onAttribute(AttributeToken token, StringRef refErr) {
        Expression r = this.docDecl.attFeeder.feed(this.expression, token, this.ignoreUndeclaredAttributes);
        if (r != Expression.nullSet) {
            this.expression = r;
            if (Debug.debug) {
                System.out.println("-- residual after :" + ExpressionPrinter.printContentModel(r));
                return true;
            }
            return true;
        }
        if (refErr == null) {
            return false;
        }
        AttributeRecoveryToken rtoken = token.createRecoveryAttToken();
        Expression r2 = this.docDecl.attFeeder.feed(this.expression, rtoken, this.ignoreUndeclaredAttributes);
        if (r2 == Expression.nullSet) {
            if (this.expression == Expression.nullSet) {
                refErr.str = this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_CONTENT_MODEL_IS_NULLSET, (Object[]) null);
                return true;
            }
            refErr.str = this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_UNDECLARED_ATTRIBUTE, token.qName);
            return true;
        }
        refErr.str = diagnoseBadAttributeValue(rtoken);
        if (refErr.str == null) {
            refErr.str = this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_GENERIC, token.qName);
        }
        this.expression = r2;
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public boolean onEndAttributes(StartTagInfo sti, StringRef refErr) {
        Expression r = this.docDecl.attPruner.prune(this.expression);
        if (r != Expression.nullSet) {
            this.expression = r;
            return true;
        }
        if (refErr == null) {
            return false;
        }
        if (this.expression == Expression.nullSet) {
            refErr.str = this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_CONTENT_MODEL_IS_NULLSET, (Object[]) null);
        } else {
            refErr.str = diagnoseMissingAttribute(sti);
            if (refErr.str == null) {
                refErr.str = this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_GENERIC, sti.qName);
            }
        }
        this.expression = this.expression.visit(this.docDecl.attRemover);
        return true;
    }

    protected boolean stepForward(Token token, StringRef errRef) {
        Expression residual = this.docDecl.resCalc.calcResidual(this.expression, token);
        if (Debug.debug) {
            System.out.println("residual of stepForward(" + token + ")");
            System.out.print(ExpressionPrinter.printContentModel(this.expression));
            System.out.print("   ->   ");
            System.out.println(ExpressionPrinter.printContentModel(residual));
        }
        if (residual == Expression.nullSet) {
            if (errRef != null && (token instanceof StringToken)) {
                errRef.str = diagnoseUnexpectedLiteral((StringToken) token);
                return false;
            }
            return false;
        }
        this.expression = residual;
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public final boolean onText(String literal, IDContextProvider context, StringRef refErr, DatatypeRef refType) {
        return onText2(literal, IDContextProviderWrapper.create(context), refErr, refType);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public boolean onText2(String literal, IDContextProvider2 provider, StringRef refErr, DatatypeRef refType) {
        return stepForward(new StringToken(this.docDecl, literal, provider, refType), refErr);
    }

    public final boolean stepForwardByContinuation(Expression continuation, StringRef errRef) {
        if (continuation == Expression.nullSet) {
            return errRef == null ? false : false;
        }
        if (Debug.debug) {
            System.out.println("stepForwardByCont. :  " + ExpressionPrinter.printContentModel(continuation));
        }
        this.expression = continuation;
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public boolean isAcceptState(StringRef errRef) {
        if (errRef == null) {
            return this.expression.isEpsilonReducible();
        }
        if (this.expression.isEpsilonReducible()) {
            return true;
        }
        errRef.str = diagnoseUncompletedContent();
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public int getStringCareLevel() {
        OptimizationTag ot = (OptimizationTag) this.expression.verifierTag;
        if (ot == null) {
            Expression expression = this.expression;
            OptimizationTag optimizationTag = new OptimizationTag();
            ot = optimizationTag;
            expression.verifierTag = optimizationTag;
        }
        if (ot.stringCareLevel == -1) {
            ot.stringCareLevel = StringCareLevelCalculator.calc(this.expression);
        }
        return ot.stringCareLevel;
    }

    private final Expression mergeContinuation(Expression exp1, Expression exp2) {
        if (exp1 == null && exp2 == null) {
            return null;
        }
        if (exp1 == null || exp1 == Expression.nullSet) {
            return exp2;
        }
        return (exp2 == null || exp2 == Expression.nullSet) ? exp1 : this.docDecl.pool.createChoice(exp1, exp2);
    }

    private final Acceptor createRecoveryAcceptors() {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        CombinedChildContentExpCreator.ExpressionPair combinedEoC = cccc.get(this.expression, null, false);
        Expression eocr = this.docDecl.resCalc.calcResidual(this.expression, AnyElementToken.theInstance);
        Expression continuation = this.docDecl.pool.createChoice(this.expression, eocr);
        Expression contentModel = combinedEoC.content;
        if (Debug.debug) {
            System.out.println("content model of recovery acceptor:" + ExpressionPrinter.printContentModel(contentModel));
            System.out.println("continuation of recovery acceptor:" + ExpressionPrinter.printSmallest(continuation));
        }
        return createAcceptor(contentModel, continuation, null, 0);
    }

    private String concatenateMessages(List items, boolean more, String separatorStr, String moreStr) {
        String r = "";
        String sep = this.docDecl.localizeMessage(separatorStr, (Object[]) null);
        Collections.sort(items, new Comparator() { // from class: com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor.1
            @Override // java.util.Comparator
            public int compare(Object o1, Object o2) {
                return ((String) o1).compareTo((String) o2);
            }
        });
        for (int i = 0; i < items.size(); i++) {
            if (r.length() != 0) {
                r = r + sep;
            }
            r = r + items.get(i);
        }
        if (more) {
            r = r + this.docDecl.localizeMessage(moreStr, (Object[]) null);
        }
        return r;
    }

    private String concatenateMessages(Set items, boolean more, String separatorStr, String moreStr) {
        return concatenateMessages(new Vector(items), more, separatorStr, moreStr);
    }

    private String getDiagnosisFromTypedString(DataOrValueExp exp, StringToken value) {
        try {
            exp.getType().checkValid(value.literal, value.context);
            return null;
        } catch (DatatypeException e) {
            return e.getMessage();
        }
    }

    /* JADX WARN: Code duplicated, block: B:30:0x011a  */
    private String diagnoseBadTagName(StartTagInfo sti) {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        Expression r = cccc.get(this.expression, sti, false).content;
        if (r == Expression.nullSet) {
            return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_ELEMENT_NOT_ALLOWED, sti.qName);
        }
        if (cccc.isComplex()) {
            return null;
        }
        Set s = new HashSet();
        boolean more = false;
        String wrongNamespace = null;
        ElementExp[] eocs = cccc.getMatchedElements();
        int len = cccc.numMatchedElements();
        for (int i = 0; i < len; i++) {
            if (eocs[i].contentModel.getExpandedExp(this.docDecl.pool) != Expression.nullSet) {
                NameClass nc = eocs[i].getNameClass();
                if (nc instanceof SimpleNameClass) {
                    SimpleNameClass snc = (SimpleNameClass) nc;
                    if (snc.localName.equals(sti.localName)) {
                        wrongNamespace = snc.namespaceURI;
                    }
                    s.add(this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_SIMPLE_NAMECLASS, nc.toString()));
                } else if (nc instanceof NamespaceNameClass) {
                    s.add(this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_NAMESPACE_NAMECLASS, ((NamespaceNameClass) nc).namespaceURI));
                } else if (nc instanceof NotNameClass) {
                    NameClass ncc = ((NotNameClass) nc).child;
                    if (ncc instanceof NamespaceNameClass) {
                        s.add(this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_NOT_NAMESPACE_NAMECLASS, ((NamespaceNameClass) ncc).namespaceURI));
                    } else {
                        more = true;
                    }
                } else {
                    more = true;
                }
            }
        }
        if (s.size() == 0) {
            return null;
        }
        if (wrongNamespace != null) {
            if (s.size() == 1) {
                return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_TAGNAME_WRONG_NAMESPACE, sti.localName, wrongNamespace);
            }
            return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_TAGNAME_PROBABLY_WRONG_NAMESPACE, sti.localName, wrongNamespace);
        }
        return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_TAGNAME_WRAPUP, sti.qName, concatenateMessages(s, more, REDocumentDeclaration.DIAG_BAD_TAGNAME_SEPARATOR, REDocumentDeclaration.DIAG_BAD_TAGNAME_MORE));
    }

    private String diagnoseBadAttributeValue(AttributeRecoveryToken rtoken) {
        Object failedExp = rtoken.getFailedExp();
        if (failedExp instanceof DataOrValueExp) {
            DataOrValueExp tse = (DataOrValueExp) failedExp;
            if (tse.getType() == NoneType.theInstance) {
                return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_UNDECLARED_ATTRIBUTE, rtoken.qName);
            }
            String dtMsg = getDiagnosisFromTypedString(tse, rtoken.value);
            if (dtMsg == null) {
                return null;
            }
            return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_DATATYPE, rtoken.qName, dtMsg);
        }
        if (failedExp instanceof ChoiceExp) {
            Set items = new HashSet();
            boolean more = false;
            ChoiceExp ch = (ChoiceExp) failedExp;
            Expression[] children = ch.getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof ValueExp) {
                    items.add(((ValueExp) children[i]).value.toString());
                } else {
                    more = true;
                }
            }
            if (items.size() == 0) {
                return null;
            }
            return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_WRAPUP, rtoken.qName, concatenateMessages(items, more, REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_SEPARATOR, REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_MORE));
        }
        return null;
    }

    private String diagnoseMissingAttribute(StartTagInfo sti) {
        Expression e = this.expression.visit(this.docDecl.attPicker);
        if (e.isEpsilonReducible()) {
            throw new Error();
        }
        Set s = new HashSet();
        boolean more = false;
        while (e instanceof ChoiceExp) {
            ChoiceExp ch = (ChoiceExp) e;
            NameClass nc = ((AttributeExp) ch.exp2).nameClass;
            if (nc instanceof SimpleNameClass) {
                s.add(nc.toString());
            } else {
                more = true;
            }
            e = ch.exp1;
        }
        if (e == Expression.nullSet) {
            return null;
        }
        if (!(e instanceof AttributeExp)) {
            throw new Error(e.toString());
        }
        NameClass nc2 = ((AttributeExp) e).nameClass;
        if (nc2 instanceof SimpleNameClass) {
            s.add(nc2.toString());
        } else {
            more = true;
        }
        if (s.size() == 0) {
            return null;
        }
        if (s.size() == 1 && !more) {
            return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_SIMPLE, sti.qName, s.iterator().next());
        }
        return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_WRAPUP, sti.qName, concatenateMessages(s, more, REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_SEPARATOR, REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_MORE));
    }

    private String diagnoseUnexpectedLiteral(StringToken token) {
        StringRecoveryToken srt = new StringRecoveryToken(token);
        Expression recoveryResidual = this.docDecl.resCalc.calcResidual(this.expression, srt);
        if (recoveryResidual == Expression.nullSet) {
            return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_STRING_NOT_ALLOWED, token.literal.trim());
        }
        this.expression = this.docDecl.pool.createChoice(this.expression, recoveryResidual);
        if (srt.failedExps.size() == 1) {
            DataOrValueExp texp = (DataOrValueExp) srt.failedExps.iterator().next();
            try {
                texp.getType().checkValid(srt.literal, srt.context);
                if (texp instanceof ValueExp) {
                    ValueExp vexp = (ValueExp) texp;
                    if (!vexp.dt.sameValue(vexp.value, vexp.dt.createValue(srt.literal, srt.context))) {
                        return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_LITERAL_INCORRECT_VALUE, vexp.value.toString(), token.literal.trim());
                    }
                    return null;
                }
                return null;
            } catch (DatatypeException de) {
                if (de.getMessage() != null) {
                    return de.getMessage();
                }
                return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_LITERAL_GENERIC, token.literal.trim());
            }
        }
        Set items = new HashSet();
        boolean more = false;
        for (DataOrValueExp texp2 : srt.failedExps) {
            if (texp2 instanceof ValueExp) {
                items.add(((ValueExp) texp2).value.toString());
            } else {
                more = true;
            }
        }
        if (items.size() == 0) {
            return null;
        }
        return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_BAD_LITERAL_WRAPUP, concatenateMessages(items, more, REDocumentDeclaration.DIAG_BAD_LITERAL_SEPARATOR, REDocumentDeclaration.DIAG_BAD_LITERAL_MORE), token.literal.trim());
    }

    /* JADX WARN: Code duplicated, block: B:16:0x00b8  */
    protected String diagnoseUncompletedContent() {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        cccc.get(this.expression, null, false);
        Set s = new HashSet();
        boolean more = false;
        ElementExp[] eocs = cccc.getMatchedElements();
        int len = cccc.numMatchedElements();
        for (int i = 0; i < len; i++) {
            NameClass nc = eocs[i].getNameClass();
            if (nc instanceof SimpleNameClass) {
                s.add(this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_SIMPLE_NAMECLASS, nc.toString()));
            } else if (nc instanceof NamespaceNameClass) {
                s.add(this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_NAMESPACE_NAMECLASS, ((NamespaceNameClass) nc).namespaceURI));
            } else if (nc instanceof NotNameClass) {
                NameClass ncc = ((NotNameClass) nc).child;
                if (ncc instanceof NamespaceNameClass) {
                    s.add(this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_NOT_NAMESPACE_NAMECLASS, ((NamespaceNameClass) ncc).namespaceURI));
                } else {
                    more = true;
                }
            } else {
                more = true;
            }
        }
        if (s.size() == 0) {
            return null;
        }
        return this.docDecl.localizeMessage(REDocumentDeclaration.DIAG_UNCOMPLETED_CONTENT_WRAPUP, null, concatenateMessages(s, more, REDocumentDeclaration.DIAG_UNCOMPLETED_CONTENT_SEPARATOR, REDocumentDeclaration.DIAG_UNCOMPLETED_CONTENT_MORE));
    }
}
