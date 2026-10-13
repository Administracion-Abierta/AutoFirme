package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/REDocumentDeclaration.class */
public class REDocumentDeclaration implements DocumentDeclaration {
    protected final Expression topLevel;
    public final ExpressionPool pool;
    protected final ResidualCalculator resCalc;
    protected final CombinedChildContentExpCreator cccec;
    protected final AttributeFeeder attFeeder;
    protected final AttributePruner attPruner;
    protected final AttributePicker attPicker;
    protected final AttributeRemover attRemover;
    protected final ElementsOfConcernCollector ecc;
    public final AttributeToken attToken;
    public static final String DIAG_ELEMENT_NOT_ALLOWED = "Diagnosis.ElementNotAllowed";
    public static final String DIAG_CONTENT_MODEL_IS_NULLSET = "Diagnosis.ContentModelIsNullset";
    public static final String DIAG_BAD_TAGNAME_GENERIC = "Diagnosis.BadTagName.Generic";
    public static final String DIAG_BAD_TAGNAME_WRAPUP = "Diagnosis.BadTagName.WrapUp";
    public static final String DIAG_BAD_TAGNAME_SEPARATOR = "Diagnosis.BadTagName.Separator";
    public static final String DIAG_BAD_TAGNAME_MORE = "Diagnosis.BadTagName.More";
    public static final String DIAG_BAD_TAGNAME_WRONG_NAMESPACE = "Diagnosis.BadTagName.WrongNamespace";
    public static final String DIAG_BAD_TAGNAME_PROBABLY_WRONG_NAMESPACE = "Diagnosis.BadTagName.ProbablyWrongNamespace";
    public static final String DIAG_UNDECLARED_ATTRIBUTE = "Diagnosis.UndeclaredAttribute";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_GENERIC = "Diagnosis.BadAttributeValue.Generic";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_DATATYPE = "Diagnosis.BadAttributeValue.DataType";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_WRAPUP = "Diagnosis.BadAttributeValue.WrapUp";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_SEPARATOR = "Diagnosis.BadAttributeValue.Separator";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_MORE = "Diagnosis.BadAttributeValue.More";
    public static final String DIAG_MISSING_ATTRIBUTE_SIMPLE = "Diagnosis.MissingAttribute.Simple";
    public static final String DIAG_MISSING_ATTRIBUTE_GENERIC = "Diagnosis.MissingAttribute.Generic";
    public static final String DIAG_MISSING_ATTRIBUTE_WRAPUP = "Diagnosis.MissingAttribute.WrapUp";
    public static final String DIAG_MISSING_ATTRIBUTE_SEPARATOR = "Diagnosis.MissingAttribute.Separator";
    public static final String DIAG_MISSING_ATTRIBUTE_MORE = "Diagnosis.MissingAttribute.More";
    public static final String DIAG_UNCOMPLETED_CONTENT_WRAPUP = "Diagnosis.UncompletedContent.WrapUp";
    public static final String DIAG_UNCOMPLETED_CONTENT_SEPARATOR = "Diagnosis.UncompletedContent.Separator";
    public static final String DIAG_UNCOMPLETED_CONTENT_MORE = "Diagnosis.UncompletedContent.More";
    public static final String DIAG_BAD_LITERAL_WRAPUP = "Diagnosis.BadLiteral.WrapUp";
    public static final String DIAG_BAD_LITERAL_SEPARATOR = "Diagnosis.BadLiteral.Separator";
    public static final String DIAG_BAD_LITERAL_MORE = "Diagnosis.BadLiteral.More";
    public static final String DIAG_BAD_LITERAL_GENERIC = "Diagnosis.BadLiteral.Generic";
    public static final String DIAG_BAD_LITERAL_INCORRECT_VALUE = "Diagnosis.BadLiteral.IncorrectValue";
    public static final String DIAG_SIMPLE_NAMECLASS = "Diagnosis.SimpleNameClass";
    public static final String DIAG_NAMESPACE_NAMECLASS = "Diagnosis.NamespaceNameClass";
    public static final String DIAG_NOT_NAMESPACE_NAMECLASS = "Diagnosis.NotNamespaceNameClass";
    public static final String DIAG_STRING_NOT_ALLOWED = "Diagnosis.StringNotAllowed";
    public static final String DIAG_BAD_KEY_VALUE = "Diagnosis.BadKeyValue";
    public static final String DIAG_BAD_KEY_VALUE2 = "Diagnosis.BadKeyValue2";

    public REDocumentDeclaration(Grammar grammar) {
        this(grammar.getTopLevel(), grammar.getPool());
    }

    public REDocumentDeclaration(Expression topLevel, ExpressionPool pool) {
        this.topLevel = topLevel;
        this.pool = pool;
        this.resCalc = new ResidualCalculator(pool);
        this.attFeeder = new AttributeFeeder(this);
        this.attPicker = new AttributePicker(pool);
        this.attPruner = new AttributePruner(pool);
        this.attRemover = new AttributeRemover(pool);
        this.cccec = new CombinedChildContentExpCreator(pool);
        this.ecc = new ElementsOfConcernCollector();
        this.attToken = new AttributeToken(this, null, null, null, null);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration
    public Acceptor createAcceptor() {
        return new SimpleAcceptor(this, this.topLevel, null, Expression.epsilon);
    }

    public String localizeMessage(String propertyName, Object[] args) {
        String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.verifier.regexp.Messages").getString(propertyName);
        return MessageFormat.format(format, args);
    }

    public final String localizeMessage(String propName, Object arg1) {
        return localizeMessage(propName, new Object[]{arg1});
    }

    public final String localizeMessage(String propName, Object arg1, Object arg2) {
        return localizeMessage(propName, new Object[]{arg1, arg2});
    }
}
