package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XPath;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/PathMatcher.class */
public abstract class PathMatcher extends MatcherBundle {
    private boolean matchFound;

    protected abstract void onElementMatched(String str, String str2) throws SAXException;

    protected abstract void onAttributeMatched(String str, String str2, String str3, Datatype datatype) throws SAXException;

    protected PathMatcher(IDConstraintChecker owner, XPath[] paths) {
        super(owner);
        this.matchFound = false;
        this.children = new Matcher[paths.length];
        for (int i = 0; i < paths.length; i++) {
            this.children[i] = new SinglePathMatcher(paths[i]);
        }
    }

    protected void start(String namespaceURI, String localName) throws SAXException {
        if (this.matchFound) {
            onElementMatched(namespaceURI, localName);
        }
        this.matchFound = false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.MatcherBundle, com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
    protected void startElement(String namespaceURI, String localName) throws SAXException {
        super.startElement(namespaceURI, localName);
        if (this.matchFound) {
            onElementMatched(namespaceURI, localName);
        }
        this.matchFound = false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.MatcherBundle, com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
    protected void onAttribute(String namespaceURI, String localName, String value, Datatype type) throws SAXException {
        super.onAttribute(namespaceURI, localName, value, type);
        if (this.matchFound) {
            onAttributeMatched(namespaceURI, localName, value, type);
        }
        this.matchFound = false;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/PathMatcher$SinglePathMatcher.class */
    private class SinglePathMatcher extends Matcher {
        private boolean[][] activeSteps;
        protected final XPath path;
        private boolean elementMatched;

        protected SinglePathMatcher(XPath path) {
            super(PathMatcher.this.owner);
            this.elementMatched = false;
            this.path = path;
            this.activeSteps = new boolean[4][];
            this.activeSteps[0] = new boolean[path.steps.length + 1];
            this.activeSteps[0][0] = true;
            if (path.steps.length == 0) {
                if (path.attributeStep == null) {
                    PathMatcher.this.matchFound = true;
                } else {
                    this.elementMatched = true;
                }
            }
        }

        @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
        protected void startElement(String namespaceURI, String localName) throws SAXException {
            this.elementMatched = false;
            int depth = PathMatcher.this.getDepth();
            if (depth == this.activeSteps.length - 1) {
                boolean[][] newBuf = new boolean[depth * 2][];
                System.arraycopy(this.activeSteps, 0, newBuf, 0, this.activeSteps.length);
                this.activeSteps = newBuf;
            }
            int len = this.path.steps.length;
            boolean[] prvBuf = this.activeSteps[depth - 1];
            boolean[] curBuf = this.activeSteps[depth];
            if (curBuf == null) {
                boolean[] zArr = new boolean[len + 1];
                curBuf = zArr;
                this.activeSteps[depth] = zArr;
            }
            if (len != 0) {
                System.arraycopy(prvBuf, 0, curBuf, 1, len);
                curBuf[0] = this.path.isAnyDescendant;
            }
            for (int i = 1; i <= len; i++) {
                if (curBuf[i] && !this.path.steps[i - 1].accepts(namespaceURI, localName)) {
                    curBuf[i] = false;
                }
            }
            if (curBuf[len]) {
                if (this.path.attributeStep == null) {
                    PathMatcher.this.matchFound = true;
                } else {
                    this.elementMatched = true;
                }
            }
        }

        @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
        protected void onAttribute(String namespaceURI, String localName, String value, Datatype type) throws SAXException {
            if (this.elementMatched && this.path.attributeStep.accepts(namespaceURI, localName)) {
                PathMatcher.this.matchFound = true;
            }
        }

        @Override // com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher
        protected void endElement(Datatype dt) {
            this.elementMatched = false;
        }
    }
}
