package javax.help.search;

import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/SearchItem.class */
public class SearchItem {
    private URL base;
    private String title;
    private String lang;
    private String filename;
    private double confidence;
    private int begin;
    private int end;
    private Vector concepts;

    public SearchItem(URL url, String str, String str2, String str3, double d, int i, int i2, Vector vector) {
        if (url == null) {
            throw new NullPointerException("base");
        }
        this.base = url;
        if (str == null) {
            throw new NullPointerException("title");
        }
        this.title = str;
        this.lang = str2;
        if (str3 == null) {
            throw new NullPointerException("fileName");
        }
        this.filename = str3;
        this.confidence = d;
        this.begin = i;
        this.end = i2;
        if (vector == null) {
            throw new NullPointerException("concepts");
        }
        this.concepts = vector;
    }

    public URL getBase() {
        return this.base;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLang() {
        return this.lang;
    }

    public String getFilename() {
        return this.filename;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public int getBegin() {
        return this.begin;
    }

    public int getEnd() {
        return this.end;
    }

    public Enumeration getConcepts() {
        return this.concepts.elements();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(new StringBuffer().append(this.confidence).append(" ").append(this.title).append(":").append(this.base).append(this.filename).append(" [").append(this.begin).append(",").append(this.end).append("], {").toString());
        if (this.concepts == null) {
            stringBuffer.append("}");
            return stringBuffer.toString();
        }
        Enumeration enumerationElements = this.concepts.elements();
        while (enumerationElements.hasMoreElements()) {
            stringBuffer.append((String) enumerationElements.nextElement());
            if (enumerationElements.hasMoreElements()) {
                stringBuffer.append(",");
            }
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }
}
