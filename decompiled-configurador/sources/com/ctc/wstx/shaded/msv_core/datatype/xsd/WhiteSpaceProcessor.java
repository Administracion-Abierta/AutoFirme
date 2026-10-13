package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import java.io.InvalidObjectException;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/WhiteSpaceProcessor.class */
public abstract class WhiteSpaceProcessor implements Serializable {
    public static final WhiteSpaceProcessor thePreserve = new Preserve();
    public static final WhiteSpaceProcessor theReplace = new Replace();
    public static final WhiteSpaceProcessor theCollapse = new Collapse();
    private static final WhiteSpaceProcessor backwardCompatibiliyHook1 = new WhiteSpaceProcessor() { // from class: com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor.1
        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String process(String text) {
            throw new UnsupportedOperationException();
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        int tightness() {
            return 0;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String getName() {
            return "preserve";
        }
    };
    private static final WhiteSpaceProcessor backwardCompatibiliyHook2 = new WhiteSpaceProcessor() { // from class: com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor.2
        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String process(String text) {
            throw new UnsupportedOperationException();
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        int tightness() {
            return 2;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String getName() {
            return "collapse";
        }
    };
    private static final WhiteSpaceProcessor backwardCompatibiliyHook3 = new WhiteSpaceProcessor() { // from class: com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor.3
        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String process(String text) {
            throw new UnsupportedOperationException();
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        int tightness() {
            return 1;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String getName() {
            return "replace";
        }
    };
    private static final long serialVersionUID = 1;

    public abstract String process(String str);

    abstract int tightness();

    public abstract String getName();

    protected static WhiteSpaceProcessor get(String name) throws DatatypeException {
        String name2 = theCollapse.process(name);
        if (name2.equals("preserve")) {
            return thePreserve;
        }
        if (name2.equals("collapse")) {
            return theCollapse;
        }
        if (name2.equals("replace")) {
            return theReplace;
        }
        throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INVALID_WHITESPACE_VALUE, name2));
    }

    protected static final boolean isWhiteSpace(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
    }

    protected Object readResolve() throws InvalidObjectException {
        try {
            if (getClass() == backwardCompatibiliyHook1.getClass()) {
                return thePreserve;
            }
            if (getClass() == backwardCompatibiliyHook2.getClass()) {
                return theCollapse;
            }
            if (getClass() == backwardCompatibiliyHook3.getClass()) {
                return theReplace;
            }
            return get(getName());
        } catch (DatatypeException e) {
            throw new InvalidObjectException("Unknown Processing Mode");
        }
    }

    public static String replace(String str) {
        return theReplace.process(str);
    }

    public static String collapse(String str) {
        return theCollapse.process(str);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/WhiteSpaceProcessor$Preserve.class */
    private static class Preserve extends WhiteSpaceProcessor {
        private static final long serialVersionUID = 1;

        private Preserve() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String process(String text) {
            return text;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        int tightness() {
            return 0;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String getName() {
            return "preserve";
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/WhiteSpaceProcessor$Replace.class */
    private static class Replace extends WhiteSpaceProcessor {
        private static final long serialVersionUID = 1;

        private Replace() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String process(String text) {
            int len = text.length();
            StringBuffer result = new StringBuffer(len);
            for (int i = 0; i < len; i++) {
                char ch = text.charAt(i);
                if (WhiteSpaceProcessor.isWhiteSpace(ch)) {
                    result.append(' ');
                } else {
                    result.append(ch);
                }
            }
            return result.toString();
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        int tightness() {
            return 1;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String getName() {
            return "replace";
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/WhiteSpaceProcessor$Collapse.class */
    private static class Collapse extends WhiteSpaceProcessor {
        private static final long serialVersionUID = 1;

        private Collapse() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String process(String text) {
            int len = text.length();
            StringBuffer result = new StringBuffer(len);
            boolean inStripMode = true;
            for (int i = 0; i < len; i++) {
                char ch = text.charAt(i);
                boolean b = WhiteSpaceProcessor.isWhiteSpace(ch);
                if (!inStripMode || !b) {
                    inStripMode = b;
                    if (inStripMode) {
                        result.append(' ');
                    } else {
                        result.append(ch);
                    }
                }
            }
            int len2 = result.length();
            if (len2 > 0 && result.charAt(len2 - 1) == ' ') {
                result.setLength(len2 - 1);
            }
            return result.toString();
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        int tightness() {
            return 2;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor
        public String getName() {
            return "collapse";
        }
    }
}
