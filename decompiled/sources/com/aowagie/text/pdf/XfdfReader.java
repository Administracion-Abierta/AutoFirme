package com.aowagie.text.pdf;

import com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfdfReader.class */
class XfdfReader implements SimpleXMLDocHandler {
    private boolean foundRoot = false;
    private final Stack fieldNames = new Stack();
    private final Stack fieldValues = new Stack();
    private HashMap fields;
    private HashMap listFields;
    private String fileSpec;

    XfdfReader() {
    }

    public HashMap getFields() {
        return this.fields;
    }

    String getFieldValue(String name) {
        String field = (String) this.fields.get(name);
        if (field == null) {
            return null;
        }
        return field;
    }

    List getListValues(String name) {
        return (List) this.listFields.get(name);
    }

    public String getFileSpec() {
        return this.fileSpec;
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startElement(String tag, HashMap h) {
        if (!this.foundRoot) {
            if (!tag.equals("xfdf")) {
                throw new RuntimeException("Root element is not Bookmark.");
            }
            this.foundRoot = true;
        }
        if (!tag.equals("xfdf")) {
            if (tag.equals(OperatorName.FILL_NON_ZERO)) {
                this.fileSpec = (String) h.get("href");
                return;
            }
            if (tag.equals("fields")) {
                this.fields = new HashMap();
                this.listFields = new HashMap();
            } else if (tag.equals("field")) {
                String fName = (String) h.get("name");
                this.fieldNames.push(fName);
            } else if (tag.equals(ES6Iterator.VALUE_PROPERTY)) {
                this.fieldValues.push("");
            }
        }
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endElement(String tag) {
        if (tag.equals(ES6Iterator.VALUE_PROPERTY)) {
            String fName = "";
            for (int k = 0; k < this.fieldNames.size(); k++) {
                fName = fName + "." + ((String) this.fieldNames.elementAt(k));
            }
            if (fName.startsWith(".")) {
                fName = fName.substring(1);
            }
            String fVal = (String) this.fieldValues.pop();
            String old = (String) this.fields.put(fName, fVal);
            if (old != null) {
                List l = (List) this.listFields.get(fName);
                if (l == null) {
                    l = new ArrayList();
                    l.add(old);
                }
                l.add(fVal);
                this.listFields.put(fName, l);
                return;
            }
            return;
        }
        if (tag.equals("field") && !this.fieldNames.isEmpty()) {
            this.fieldNames.pop();
        }
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void startDocument() {
        this.fileSpec = "";
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void endDocument() {
    }

    @Override // com.aowagie.text.xml.simpleparser.SimpleXMLDocHandler
    public void text(String str) {
        if (this.fieldNames.isEmpty() || this.fieldValues.isEmpty()) {
            return;
        }
        String val = (String) this.fieldValues.pop();
        this.fieldValues.push(val + str);
    }
}
