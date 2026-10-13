package org.mozilla.javascript.xml;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xml/XMLLib.class */
public abstract class XMLLib {
    private static final Object XML_LIB_KEY = new Object();

    public abstract boolean isXMLName(Context context, Object obj);

    public abstract Ref nameRef(Context context, Object obj, Scriptable scriptable, int i);

    public abstract Ref nameRef(Context context, Object obj, Object obj2, Scriptable scriptable, int i);

    public abstract String escapeAttributeValue(Object obj);

    public abstract String escapeTextValue(Object obj);

    public abstract Object toDefaultXmlNamespace(Context context, Object obj);

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xml/XMLLib$Factory.class */
    public static abstract class Factory {
        public abstract String getImplementationClassName();

        public static Factory create(final String className) {
            return new Factory() { // from class: org.mozilla.javascript.xml.XMLLib.Factory.1
                @Override // org.mozilla.javascript.xml.XMLLib.Factory
                public String getImplementationClassName() {
                    return className;
                }
            };
        }
    }

    public static XMLLib extractFromScopeOrNull(Scriptable scope) {
        ScriptableObject so = ScriptRuntime.getLibraryScopeOrNull(scope);
        if (so == null) {
            return null;
        }
        ScriptableObject.getProperty(so, "XML");
        return (XMLLib) so.getAssociatedValue(XML_LIB_KEY);
    }

    public static XMLLib extractFromScope(Scriptable scope) {
        XMLLib lib = extractFromScopeOrNull(scope);
        if (lib != null) {
            return lib;
        }
        String msg = ScriptRuntime.getMessage0("msg.XML.not.available");
        throw Context.reportRuntimeError(msg);
    }

    protected final XMLLib bindToScope(Scriptable scope) {
        ScriptableObject so = ScriptRuntime.getLibraryScopeOrNull(scope);
        if (so == null) {
            throw new IllegalStateException();
        }
        return (XMLLib) so.associateValue(XML_LIB_KEY, this);
    }

    public void setIgnoreComments(boolean b) {
        throw new UnsupportedOperationException();
    }

    public void setIgnoreWhitespace(boolean b) {
        throw new UnsupportedOperationException();
    }

    public void setIgnoreProcessingInstructions(boolean b) {
        throw new UnsupportedOperationException();
    }

    public void setPrettyPrinting(boolean b) {
        throw new UnsupportedOperationException();
    }

    public void setPrettyIndent(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean isIgnoreComments() {
        throw new UnsupportedOperationException();
    }

    public boolean isIgnoreProcessingInstructions() {
        throw new UnsupportedOperationException();
    }

    public boolean isIgnoreWhitespace() {
        throw new UnsupportedOperationException();
    }

    public boolean isPrettyPrinting() {
        throw new UnsupportedOperationException();
    }

    public int getPrettyIndent() {
        throw new UnsupportedOperationException();
    }
}
