package nu.xom.jaxen.saxpath.base;

import java.util.ArrayList;
import nu.xom.jaxen.saxpath.Axis;
import nu.xom.jaxen.saxpath.SAXPathException;
import nu.xom.jaxen.saxpath.XPathHandler;
import nu.xom.jaxen.saxpath.XPathSyntaxException;
import nu.xom.jaxen.saxpath.helpers.DefaultXPathHandler;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/base/XPathReader.class */
public class XPathReader implements nu.xom.jaxen.saxpath.XPathReader {
    private ArrayList tokens;
    private XPathLexer lexer;
    private XPathHandler handler;
    private static XPathHandler defaultHandler = new DefaultXPathHandler();

    public XPathReader() {
        setXPathHandler(defaultHandler);
    }

    @Override // nu.xom.jaxen.saxpath.SAXPathEventSource
    public void setXPathHandler(XPathHandler xPathHandler) {
        this.handler = xPathHandler;
    }

    @Override // nu.xom.jaxen.saxpath.SAXPathEventSource
    public XPathHandler getXPathHandler() {
        return this.handler;
    }

    @Override // nu.xom.jaxen.saxpath.XPathReader
    public void parse(String str) throws SAXPathException {
        setUpParse(str);
        getXPathHandler().startXPath();
        expr();
        getXPathHandler().endXPath();
        if (LA(1) != -1) {
            throw createSyntaxException(new StringBuffer().append("Unexpected '").append(LT(1).getTokenText()).append(OperatorName.SHOW_TEXT_LINE).toString());
        }
        this.lexer = null;
        this.tokens = null;
    }

    void setUpParse(String str) {
        this.tokens = new ArrayList();
        this.lexer = new XPathLexer(str);
    }

    private void pathExpr() throws SAXPathException {
        getXPathHandler().startPathExpr();
        switch (LA(1)) {
            case 9:
            case 14:
            case 15:
            case 17:
                locationPath(false);
                break;
            case 10:
            case 11:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 24:
            case 27:
            case 28:
            default:
                throw createSyntaxException(new StringBuffer().append("Unexpected '").append(LT(1).getTokenText()).append(OperatorName.SHOW_TEXT_LINE).toString());
            case 12:
            case 13:
                locationPath(true);
                break;
            case 16:
                if ((LA(2) == 23 && !isNodeTypeName(LT(1))) || (LA(2) == 19 && LA(4) == 23)) {
                    filterExpr();
                    if (LA(1) == 12 || LA(1) == 13) {
                        locationPath(false);
                    }
                } else {
                    locationPath(false);
                }
                break;
            case 23:
            case 25:
                filterExpr();
                if (LA(1) == 12 || LA(1) == 13) {
                    locationPath(false);
                }
                break;
            case 26:
            case 29:
                filterExpr();
                if (LA(1) == 12 || LA(1) == 13) {
                    throw createSyntaxException("Node-set expected");
                }
                break;
        }
        getXPathHandler().endPathExpr();
    }

    private void literal() throws SAXPathException {
        getXPathHandler().literal(match(26).getTokenText());
    }

    private void functionCall() throws SAXPathException {
        String tokenText;
        if (LA(2) == 19) {
            tokenText = match(16).getTokenText();
            match(19);
        } else {
            tokenText = "";
        }
        getXPathHandler().startFunction(tokenText, match(16).getTokenText());
        match(23);
        arguments();
        match(24);
        getXPathHandler().endFunction();
    }

    private void arguments() throws SAXPathException {
        while (LA(1) != 24) {
            expr();
            if (LA(1) != 30) {
                return;
            } else {
                match(30);
            }
        }
    }

    private void filterExpr() throws SAXPathException {
        getXPathHandler().startFilterExpr();
        switch (LA(1)) {
            case 16:
                functionCall();
                break;
            case 23:
                match(23);
                expr();
                match(24);
                break;
            case 25:
                variableReference();
                break;
            case 26:
                literal();
                break;
            case 29:
                getXPathHandler().number(Double.parseDouble(match(29).getTokenText()));
                break;
        }
        predicates();
        getXPathHandler().endFilterExpr();
    }

    private void variableReference() throws SAXPathException {
        String tokenText;
        match(25);
        if (LA(2) == 19) {
            tokenText = match(16).getTokenText();
            match(19);
        } else {
            tokenText = "";
        }
        getXPathHandler().variableReference(tokenText, match(16).getTokenText());
    }

    void locationPath(boolean z) throws SAXPathException {
        switch (LA(1)) {
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
                relativeLocationPath();
                return;
            case 10:
            case 11:
            default:
                throw createSyntaxException(new StringBuffer().append("Unexpected '").append(LT(1).getTokenText()).append(OperatorName.SHOW_TEXT_LINE).toString());
            case 12:
            case 13:
                if (z) {
                    absoluteLocationPath();
                    return;
                } else {
                    relativeLocationPath();
                    return;
                }
        }
    }

    private void absoluteLocationPath() throws SAXPathException {
        getXPathHandler().startAbsoluteLocationPath();
        switch (LA(1)) {
            case 12:
                match(12);
                switch (LA(1)) {
                    case 9:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        steps();
                        break;
                }
                break;
            case 13:
                getXPathHandler().startAllNodeStep(12);
                getXPathHandler().endAllNodeStep();
                match(13);
                switch (LA(1)) {
                    case 9:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        steps();
                        break;
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    default:
                        throw createSyntaxException("Location path cannot end with //");
                }
                break;
        }
        getXPathHandler().endAbsoluteLocationPath();
    }

    private void relativeLocationPath() throws SAXPathException {
        getXPathHandler().startRelativeLocationPath();
        switch (LA(1)) {
            case 12:
                match(12);
                break;
            case 13:
                getXPathHandler().startAllNodeStep(12);
                getXPathHandler().endAllNodeStep();
                match(13);
                break;
        }
        steps();
        getXPathHandler().endRelativeLocationPath();
    }

    private void steps() throws SAXPathException {
        switch (LA(1)) {
            case -1:
                return;
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                throw createSyntaxException("Expected one of '.', '..', '@', '*', <QName>");
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
                step();
                while (true) {
                    if (LA(1) == 12 || LA(1) == 13) {
                        switch (LA(1)) {
                            case 12:
                                match(12);
                                break;
                            case 13:
                                getXPathHandler().startAllNodeStep(12);
                                getXPathHandler().endAllNodeStep();
                                match(13);
                                break;
                        }
                        switch (LA(1)) {
                            case 9:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                                step();
                                break;
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            default:
                                throw createSyntaxException("Expected one of '.', '..', '@', '*', <QName>");
                        }
                    } else {
                        return;
                    }
                }
                break;
        }
    }

    void step() throws SAXPathException {
        int iAxisSpecifier = 0;
        switch (LA(1)) {
            case 9:
                iAxisSpecifier = 1;
                break;
            case 14:
            case 15:
                abbrStep();
                return;
            case 16:
                iAxisSpecifier = LA(2) != 20 ? 1 : axisSpecifier();
                break;
            case 17:
                iAxisSpecifier = axisSpecifier();
                break;
        }
        nodeTest(iAxisSpecifier);
    }

    private int axisSpecifier() throws SAXPathException {
        int iLookup = 0;
        switch (LA(1)) {
            case 16:
                Token tokenLT = LT(1);
                iLookup = Axis.lookup(tokenLT.getTokenText());
                if (iLookup == 0) {
                    throwInvalidAxis(tokenLT.getTokenText());
                }
                match(16);
                match(20);
                break;
            case 17:
                match(17);
                iLookup = 9;
                break;
        }
        return iLookup;
    }

    private void nodeTest(int i) throws SAXPathException {
        switch (LA(1)) {
            case 9:
                nameTest(i);
                return;
            case 16:
                switch (LA(2)) {
                    case 23:
                        nodeTypeTest(i);
                        return;
                    default:
                        nameTest(i);
                        return;
                }
            default:
                throw createSyntaxException("Expected <QName> or *");
        }
    }

    private void nodeTypeTest(int i) throws SAXPathException {
        String tokenText = match(16).getTokenText();
        match(23);
        if ("processing-instruction".equals(tokenText)) {
            String tokenText2 = LA(1) == 26 ? match(26).getTokenText() : "";
            match(24);
            getXPathHandler().startProcessingInstructionNodeStep(i, tokenText2);
            predicates();
            getXPathHandler().endProcessingInstructionNodeStep();
            return;
        }
        if ("node".equals(tokenText)) {
            match(24);
            getXPathHandler().startAllNodeStep(i);
            predicates();
            getXPathHandler().endAllNodeStep();
            return;
        }
        if ("text".equals(tokenText)) {
            match(24);
            getXPathHandler().startTextNodeStep(i);
            predicates();
            getXPathHandler().endTextNodeStep();
            return;
        }
        if (!Config.PROP_COMMENT.equals(tokenText)) {
            throw createSyntaxException("Expected node-type");
        }
        match(24);
        getXPathHandler().startCommentNodeStep(i);
        predicates();
        getXPathHandler().endCommentNodeStep();
    }

    private void nameTest(int i) throws SAXPathException {
        String tokenText = null;
        String tokenText2 = null;
        switch (LA(2)) {
            case 19:
                switch (LA(1)) {
                    case 16:
                        tokenText = match(16).getTokenText();
                        match(19);
                        break;
                }
                break;
        }
        switch (LA(1)) {
            case 9:
                match(9);
                tokenText2 = "*";
                break;
            case 16:
                tokenText2 = match(16).getTokenText();
                break;
        }
        if (tokenText == null) {
            tokenText = "";
        }
        getXPathHandler().startNameStep(i, tokenText, tokenText2);
        predicates();
        getXPathHandler().endNameStep();
    }

    private void abbrStep() throws SAXPathException {
        switch (LA(1)) {
            case 14:
                match(14);
                getXPathHandler().startAllNodeStep(11);
                predicates();
                getXPathHandler().endAllNodeStep();
                break;
            case 15:
                match(15);
                getXPathHandler().startAllNodeStep(3);
                predicates();
                getXPathHandler().endAllNodeStep();
                break;
        }
    }

    private void predicates() throws SAXPathException {
        while (LA(1) == 21) {
            predicate();
        }
    }

    void predicate() throws SAXPathException {
        getXPathHandler().startPredicate();
        match(21);
        predicateExpr();
        match(22);
        getXPathHandler().endPredicate();
    }

    private void predicateExpr() throws SAXPathException {
        expr();
    }

    private void expr() throws SAXPathException {
        orExpr();
    }

    private void orExpr() throws SAXPathException {
        getXPathHandler().startOrExpr();
        andExpr();
        boolean z = false;
        switch (LA(1)) {
            case 28:
                z = true;
                match(28);
                orExpr();
                break;
        }
        getXPathHandler().endOrExpr(z);
    }

    private void andExpr() throws SAXPathException {
        getXPathHandler().startAndExpr();
        equalityExpr();
        boolean z = false;
        switch (LA(1)) {
            case 27:
                z = true;
                match(27);
                andExpr();
                break;
        }
        getXPathHandler().endAndExpr(z);
    }

    private void equalityExpr() throws SAXPathException {
        relationalExpr();
        int iLA = LA(1);
        while (true) {
            int i = iLA;
            if (i != 1 && i != 2) {
                return;
            }
            switch (i) {
                case 1:
                    match(1);
                    getXPathHandler().startEqualityExpr();
                    relationalExpr();
                    getXPathHandler().endEqualityExpr(1);
                    break;
                case 2:
                    match(2);
                    getXPathHandler().startEqualityExpr();
                    relationalExpr();
                    getXPathHandler().endEqualityExpr(2);
                    break;
            }
            iLA = LA(1);
        }
    }

    private void relationalExpr() throws SAXPathException {
        additiveExpr();
        int iLA = LA(1);
        while (true) {
            int i = iLA;
            if (i != 3 && i != 5 && i != 4 && i != 6) {
                return;
            }
            switch (i) {
                case 3:
                    match(3);
                    getXPathHandler().startRelationalExpr();
                    additiveExpr();
                    getXPathHandler().endRelationalExpr(3);
                    break;
                case 4:
                    match(4);
                    getXPathHandler().startRelationalExpr();
                    additiveExpr();
                    getXPathHandler().endRelationalExpr(4);
                    break;
                case 5:
                    match(5);
                    getXPathHandler().startRelationalExpr();
                    additiveExpr();
                    getXPathHandler().endRelationalExpr(5);
                    break;
                case 6:
                    match(6);
                    getXPathHandler().startRelationalExpr();
                    additiveExpr();
                    getXPathHandler().endRelationalExpr(6);
                    break;
            }
            iLA = LA(1);
        }
    }

    private void additiveExpr() throws SAXPathException {
        multiplicativeExpr();
        int iLA = LA(1);
        while (true) {
            int i = iLA;
            if (i != 7 && i != 8) {
                return;
            }
            switch (i) {
                case 7:
                    match(7);
                    getXPathHandler().startAdditiveExpr();
                    multiplicativeExpr();
                    getXPathHandler().endAdditiveExpr(7);
                    break;
                case 8:
                    match(8);
                    getXPathHandler().startAdditiveExpr();
                    multiplicativeExpr();
                    getXPathHandler().endAdditiveExpr(8);
                    break;
            }
            iLA = LA(1);
        }
    }

    private void multiplicativeExpr() throws SAXPathException {
        unaryExpr();
        int iLA = LA(1);
        while (true) {
            int i = iLA;
            if (i != 31 && i != 11 && i != 10) {
                return;
            }
            switch (i) {
                case 9:
                case 31:
                    match(31);
                    getXPathHandler().startMultiplicativeExpr();
                    unaryExpr();
                    getXPathHandler().endMultiplicativeExpr(9);
                    break;
                case 10:
                    match(10);
                    getXPathHandler().startMultiplicativeExpr();
                    unaryExpr();
                    getXPathHandler().endMultiplicativeExpr(10);
                    break;
                case 11:
                    match(11);
                    getXPathHandler().startMultiplicativeExpr();
                    unaryExpr();
                    getXPathHandler().endMultiplicativeExpr(11);
                    break;
            }
            iLA = LA(1);
        }
    }

    private void unaryExpr() throws SAXPathException {
        switch (LA(1)) {
            case 8:
                getXPathHandler().startUnaryExpr();
                match(8);
                unaryExpr();
                getXPathHandler().endUnaryExpr(12);
                break;
            default:
                unionExpr();
                break;
        }
    }

    private void unionExpr() throws SAXPathException {
        getXPathHandler().startUnionExpr();
        pathExpr();
        boolean z = false;
        switch (LA(1)) {
            case 18:
                match(18);
                z = true;
                expr();
                break;
        }
        getXPathHandler().endUnionExpr(z);
    }

    private Token match(int i) throws XPathSyntaxException {
        LT(1);
        Token token = (Token) this.tokens.get(0);
        if (token.getTokenType() != i) {
            throw createSyntaxException(new StringBuffer().append("Expected: ").append(TokenTypes.getTokenText(i)).toString());
        }
        this.tokens.remove(0);
        return token;
    }

    private int LA(int i) {
        return LT(i).getTokenType();
    }

    private Token LT(int i) {
        if (this.tokens.size() <= i - 1) {
            for (int i2 = 0; i2 < i; i2++) {
                this.tokens.add(this.lexer.nextToken());
            }
        }
        return (Token) this.tokens.get(i - 1);
    }

    private boolean isNodeTypeName(Token token) {
        String tokenText = token.getTokenText();
        return "node".equals(tokenText) || Config.PROP_COMMENT.equals(tokenText) || "text".equals(tokenText) || "processing-instruction".equals(tokenText);
    }

    private XPathSyntaxException createSyntaxException(String str) {
        return new XPathSyntaxException(this.lexer.getXPath(), LT(1).getTokenBegin(), str);
    }

    private void throwInvalidAxis(String str) throws SAXPathException {
        throw new XPathSyntaxException(this.lexer.getXPath(), LT(1).getTokenBegin(), new StringBuffer().append("Expected valid axis name instead of [").append(str).append("]").toString());
    }
}
