package com.ctc.wstx.api;

import java.util.Set;
import java.util.TreeSet;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/EmptyElementHandler.class */
public interface EmptyElementHandler {
    boolean allowEmptyElement(String str, String str2, String str3, boolean z);

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/EmptyElementHandler$SetEmptyElementHandler.class */
    public static class SetEmptyElementHandler implements EmptyElementHandler {
        protected final Set<String> mEmptyElements;

        public SetEmptyElementHandler(Set<String> emptyElements) {
            this.mEmptyElements = emptyElements;
        }

        @Override // com.ctc.wstx.api.EmptyElementHandler
        public boolean allowEmptyElement(String prefix, String localName, String nsURI, boolean allowEmpty) {
            return this.mEmptyElements.contains(localName);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/EmptyElementHandler$HtmlEmptyElementHandler.class */
    public static class HtmlEmptyElementHandler extends SetEmptyElementHandler {
        private static final HtmlEmptyElementHandler sInstance = new HtmlEmptyElementHandler();

        public static HtmlEmptyElementHandler getInstance() {
            return sInstance;
        }

        protected HtmlEmptyElementHandler() {
            super(new TreeSet(String.CASE_INSENSITIVE_ORDER));
            this.mEmptyElements.add("area");
            this.mEmptyElements.add("base");
            this.mEmptyElements.add("basefont");
            this.mEmptyElements.add("br");
            this.mEmptyElements.add("col");
            this.mEmptyElements.add("frame");
            this.mEmptyElements.add("hr");
            this.mEmptyElements.add("input");
            this.mEmptyElements.add("img");
            this.mEmptyElements.add("isindex");
            this.mEmptyElements.add("link");
            this.mEmptyElements.add("meta");
            this.mEmptyElements.add("param");
        }
    }
}
