package com.ctc.wstx.evt;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.sr.StreamScanner;
import java.util.NoSuchElementException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamReader2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/evt/WstxEventReader.class */
public class WstxEventReader implements XMLEventReader2, XMLStreamConstants {
    protected static final int STATE_INITIAL = 1;
    protected static final int STATE_END_OF_INPUT = 2;
    protected static final int STATE_CONTENT = 3;
    protected static final int ERR_GETELEMTEXT_NOT_START_ELEM = 1;
    protected static final int ERR_GETELEMTEXT_NON_TEXT_EVENT = 2;
    protected static final int ERR_NEXTTAG_NON_WS_TEXT = 3;
    protected static final int ERR_NEXTTAG_WRONG_TYPE = 4;
    protected final XMLEventAllocator mAllocator;
    protected final XMLStreamReader2 mReader;
    protected XMLEvent mPeekedEvent = null;
    protected int mState = 1;
    protected int mPrePeekEvent = 7;
    protected final boolean mCfgMultiDocMode;

    /* JADX WARN: Multi-variable type inference failed */
    public WstxEventReader(XMLEventAllocator a, XMLStreamReader2 xMLStreamReader2) {
        this.mAllocator = a;
        this.mReader = xMLStreamReader2;
        this.mCfgMultiDocMode = (xMLStreamReader2 instanceof StreamScanner) && ((StreamScanner) xMLStreamReader2).getConfig().inputParsingModeDocuments();
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean isPropertySupported(String name) {
        return ((XMLStreamReader2) getStreamReader()).isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean setProperty(String name, Object value) {
        return ((XMLStreamReader2) getStreamReader()).setProperty(name, value);
    }

    protected String getErrorDesc(int errorType, int currEvent) {
        switch (errorType) {
            case 1:
                return ErrorConsts.ERR_STATE_NOT_STELEM + ", got " + ErrorConsts.tokenTypeDesc(currEvent);
            case 2:
                return "Expected a text token, got " + ErrorConsts.tokenTypeDesc(currEvent);
            case 3:
                return "Only all-whitespace CHARACTERS/CDATA (or SPACE) allowed for nextTag(), got " + ErrorConsts.tokenTypeDesc(currEvent);
            case 4:
                return "Got " + ErrorConsts.tokenTypeDesc(currEvent) + ", instead of START_ELEMENT, END_ELEMENT or SPACE";
            default:
                return null;
        }
    }

    public void close() throws XMLStreamException {
        this.mReader.close();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public String getElementText() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            return this.mReader.getElementText();
        }
        XMLEvent evt = this.mPeekedEvent;
        this.mPeekedEvent = null;
        if (this.mPrePeekEvent != 1) {
            reportProblem(findErrorDesc(1, this.mPrePeekEvent));
        }
        String str = null;
        StringBuffer sb = null;
        while (!evt.isEndElement()) {
            int type = evt.getEventType();
            if (type != 5 && type != 3) {
                if (!evt.isCharacters()) {
                    reportProblem(findErrorDesc(2, type));
                }
                String curr = evt.asCharacters().getData();
                if (str == null) {
                    str = curr;
                } else {
                    if (sb == null) {
                        sb = new StringBuffer(str.length() + curr.length());
                        sb.append(str);
                    }
                    sb.append(curr);
                }
            }
            evt = nextEvent();
        }
        if (sb != null) {
            return sb.toString();
        }
        return str == null ? "" : str;
    }

    public Object getProperty(String name) {
        return this.mReader.getProperty(name);
    }

    public boolean hasNext() {
        return this.mState != 2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.mState == 2) {
            throwEndOfInput();
        } else if (this.mState == 1) {
            this.mState = 3;
            return createStartDocumentEvent();
        }
        if (this.mPeekedEvent != null) {
            XMLEvent evt = this.mPeekedEvent;
            this.mPeekedEvent = null;
            if (evt.isEndDocument()) {
                updateStateEndDocument();
            }
            return evt;
        }
        return createNextEvent(true, this.mReader.next());
    }

    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException sex) {
            throwUnchecked(sex);
            return null;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLEvent nextTag() throws XMLStreamException {
        if (this.mPeekedEvent != null) {
            Characters characters = this.mPeekedEvent;
            this.mPeekedEvent = null;
            int type = characters.getEventType();
            switch (type) {
                case 1:
                case 2:
                    return characters;
                case 3:
                case 5:
                case 6:
                case 7:
                    break;
                case 4:
                case 12:
                    if (!characters.isWhiteSpace()) {
                        reportProblem(findErrorDesc(3, type));
                    }
                    break;
                case 8:
                    return null;
                case 9:
                case 10:
                case 11:
                default:
                    reportProblem(findErrorDesc(4, type));
                    break;
            }
        } else if (this.mState == 1) {
            this.mState = 3;
        }
        while (true) {
            int next = this.mReader.next();
            switch (next) {
                case 1:
                case 2:
                    return createNextEvent(false, next);
                case 3:
                case 5:
                case 6:
                    break;
                case 4:
                case 12:
                    if (!this.mReader.isWhiteSpace()) {
                        reportProblem(findErrorDesc(3, next));
                    }
                    break;
                case 7:
                case 9:
                case 10:
                case 11:
                default:
                    reportProblem(findErrorDesc(4, next));
                    break;
                case 8:
                    return null;
            }
        }
    }

    public XMLEvent peek() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            if (this.mState == 2) {
                return null;
            }
            if (this.mState == 1) {
                this.mPrePeekEvent = 7;
                this.mPeekedEvent = createStartDocumentEvent();
                this.mState = 3;
            } else {
                this.mPrePeekEvent = this.mReader.getEventType();
                this.mPeekedEvent = createNextEvent(false, this.mReader.next());
            }
        }
        return this.mPeekedEvent;
    }

    public void remove() {
        throw new UnsupportedOperationException("Can not remove events from XMLEventReader.");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void updateStateEndDocument() throws XMLStreamException {
        if (this.mCfgMultiDocMode && this.mReader.hasNext()) {
            int next = this.mReader.next();
            if (next == 7) {
                this.mPrePeekEvent = 7;
                this.mPeekedEvent = createStartDocumentEvent();
                this.mState = 3;
                return;
            }
            reportProblem("Unexpected token (" + ErrorConsts.tokenTypeDesc(next) + ") after END_DOCUMENT in multi-document mode, XMLStreamReader.hasNext() returning true");
        }
        this.mState = 2;
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean hasNextEvent() throws XMLStreamException {
        return this.mState != 2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected XMLEvent createNextEvent(boolean checkEOD, int type) throws XMLStreamException {
        try {
            XMLEvent evt = this.mAllocator.allocate(this.mReader);
            if (checkEOD && type == 8) {
                updateStateEndDocument();
            }
            return evt;
        } catch (RuntimeException rex) {
            throw _checkUnwrap(rex);
        }
    }

    protected XMLStreamException _checkUnwrap(RuntimeException rex) {
        Throwable cause = rex.getCause();
        while (true) {
            Throwable t = cause;
            if (t != null) {
                if (t instanceof XMLStreamException) {
                    return (XMLStreamException) t;
                }
                cause = t.getCause();
            } else {
                throw rex;
            }
        }
    }

    protected XMLEvent createStartDocumentEvent() throws XMLStreamException {
        XMLEvent start = this.mAllocator.allocate(this.mReader);
        return start;
    }

    protected void throwEndOfInput() {
        throw new NoSuchElementException();
    }

    protected void throwUnchecked(XMLStreamException sex) {
        XMLStreamException nestedException = sex.getNestedException() == null ? sex : sex.getNestedException();
        if (nestedException instanceof RuntimeException) {
            throw ((RuntimeException) nestedException);
        }
        if (nestedException instanceof Error) {
            throw ((Error) nestedException);
        }
        throw new RuntimeException("[was " + nestedException.getClass() + "] " + nestedException.getMessage(), nestedException);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void reportProblem(String msg) throws XMLStreamException {
        reportProblem(msg, this.mReader.getLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void reportProblem(String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            throw new WstxParsingException(msg);
        }
        throw new WstxParsingException(msg, loc);
    }

    protected XMLStreamReader getStreamReader() {
        return this.mReader;
    }

    protected final String findErrorDesc(int errorType, int currEvent) {
        String msg = getErrorDesc(errorType, currEvent);
        if (msg != null) {
            return msg;
        }
        switch (errorType) {
            case 1:
                return "Current state not START_ELEMENT when calling getElementText()";
            case 2:
                return "Expected a text token";
            case 3:
                return "Only all-whitespace CHARACTERS/CDATA (or SPACE) allowed for nextTag()";
            case 4:
                return "Should only encounter START_ELEMENT/END_ELEMENT, SPACE, or all-white-space CHARACTERS";
            default:
                return "Internal error (unrecognized error type: " + errorType + ")";
        }
    }
}
