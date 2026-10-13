package nu.xom.jaxen.saxpath;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/XPathHandler.class */
public interface XPathHandler {
    void startXPath() throws SAXPathException;

    void endXPath() throws SAXPathException;

    void startPathExpr() throws SAXPathException;

    void endPathExpr() throws SAXPathException;

    void startAbsoluteLocationPath() throws SAXPathException;

    void endAbsoluteLocationPath() throws SAXPathException;

    void startRelativeLocationPath() throws SAXPathException;

    void endRelativeLocationPath() throws SAXPathException;

    void startNameStep(int i, String str, String str2) throws SAXPathException;

    void endNameStep() throws SAXPathException;

    void startTextNodeStep(int i) throws SAXPathException;

    void endTextNodeStep() throws SAXPathException;

    void startCommentNodeStep(int i) throws SAXPathException;

    void endCommentNodeStep() throws SAXPathException;

    void startAllNodeStep(int i) throws SAXPathException;

    void endAllNodeStep() throws SAXPathException;

    void startProcessingInstructionNodeStep(int i, String str) throws SAXPathException;

    void endProcessingInstructionNodeStep() throws SAXPathException;

    void startPredicate() throws SAXPathException;

    void endPredicate() throws SAXPathException;

    void startFilterExpr() throws SAXPathException;

    void endFilterExpr() throws SAXPathException;

    void startOrExpr() throws SAXPathException;

    void endOrExpr(boolean z) throws SAXPathException;

    void startAndExpr() throws SAXPathException;

    void endAndExpr(boolean z) throws SAXPathException;

    void startEqualityExpr() throws SAXPathException;

    void endEqualityExpr(int i) throws SAXPathException;

    void startRelationalExpr() throws SAXPathException;

    void endRelationalExpr(int i) throws SAXPathException;

    void startAdditiveExpr() throws SAXPathException;

    void endAdditiveExpr(int i) throws SAXPathException;

    void startMultiplicativeExpr() throws SAXPathException;

    void endMultiplicativeExpr(int i) throws SAXPathException;

    void startUnaryExpr() throws SAXPathException;

    void endUnaryExpr(int i) throws SAXPathException;

    void startUnionExpr() throws SAXPathException;

    void endUnionExpr(boolean z) throws SAXPathException;

    void number(int i) throws SAXPathException;

    void number(double d) throws SAXPathException;

    void literal(String str) throws SAXPathException;

    void variableReference(String str, String str2) throws SAXPathException;

    void startFunction(String str, String str2) throws SAXPathException;

    void endFunction() throws SAXPathException;
}
