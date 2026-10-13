package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/CharactersEventImpl.class */
public class CharactersEventImpl extends BaseEventImpl implements Characters {
    final String mContent;
    final boolean mIsCData;
    final boolean mIgnorableWS;
    boolean mWhitespaceChecked;
    boolean mIsWhitespace;

    public CharactersEventImpl(Location loc, String content, boolean cdata) {
        super(loc);
        this.mWhitespaceChecked = false;
        this.mIsWhitespace = false;
        this.mContent = content;
        this.mIsCData = cdata;
        this.mIgnorableWS = false;
    }

    private CharactersEventImpl(Location loc, String content, boolean cdata, boolean allWS, boolean ignorableWS) {
        super(loc);
        this.mWhitespaceChecked = false;
        this.mIsWhitespace = false;
        this.mContent = content;
        this.mIsCData = cdata;
        this.mIsWhitespace = allWS;
        if (allWS) {
            this.mWhitespaceChecked = true;
            this.mIgnorableWS = ignorableWS;
        } else {
            this.mWhitespaceChecked = false;
            this.mIgnorableWS = false;
        }
    }

    public static final CharactersEventImpl createIgnorableWS(Location loc, String content) {
        return new CharactersEventImpl(loc, content, false, true, true);
    }

    public static final CharactersEventImpl createNonIgnorableWS(Location loc, String content) {
        return new CharactersEventImpl(loc, content, false, true, false);
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public Characters asCharacters() {
        return this;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return this.mIsCData ? 12 : 4;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean isCharacters() {
        return true;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            if (this.mIsCData) {
                w.write("<![CDATA[");
                w.write(this.mContent);
                w.write("]]>");
            } else {
                writeEscapedXMLText(w, this.mContent);
            }
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        if (this.mIsCData) {
            w.writeCData(this.mContent);
        } else {
            w.writeCharacters(this.mContent);
        }
    }

    public String getData() {
        return this.mContent;
    }

    public boolean isCData() {
        return this.mIsCData;
    }

    public boolean isIgnorableWhiteSpace() {
        return this.mIgnorableWS;
    }

    public boolean isWhiteSpace() {
        if (!this.mWhitespaceChecked) {
            this.mWhitespaceChecked = true;
            String str = this.mContent;
            int i = 0;
            int len = str.length();
            while (i < len && str.charAt(i) <= ' ') {
                i++;
            }
            this.mIsWhitespace = i == len;
        }
        return this.mIsWhitespace;
    }

    public void setWhitespaceStatus(boolean status) {
        this.mWhitespaceChecked = true;
        this.mIsWhitespace = status;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof Characters)) {
            return false;
        }
        Characters other = (Characters) o;
        return this.mContent.equals(other.getData()) && isCData() == other.isCData();
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        return this.mContent.hashCode();
    }

    protected static void writeEscapedXMLText(Writer w, String text) throws IOException {
        int len = text.length();
        int i = 0;
        while (i < len) {
            int start = i;
            char c = 0;
            while (i < len) {
                c = text.charAt(i);
                if (c == '<' || c == '&' || (c == '>' && i >= 2 && text.charAt(i - 1) == ']' && text.charAt(i - 2) == ']')) {
                    break;
                } else {
                    i++;
                }
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(text, start, outLen);
            }
            if (i < len) {
                if (c == '<') {
                    w.write("&lt;");
                } else if (c == '&') {
                    w.write("&amp;");
                } else if (c == '>') {
                    w.write("&gt;");
                }
            }
            i++;
        }
    }
}
