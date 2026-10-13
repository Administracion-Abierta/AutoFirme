package com.sun.java.help.impl;

import java.io.InputStream;
import java.io.Reader;
import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/Parser.class */
public class Parser extends DocumentParser {
    protected ParserListener listenerList;

    public Parser(Reader reader) {
        super(reader);
    }

    public Parser(InputStream inputStream) {
        super(inputStream);
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected void tag(String str, TagProperties tagProperties, boolean z, boolean z2) {
        this.listenerList.tagFound(new ParserEvent(this, new Tag(str, tagProperties, z, z2)));
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected void pi(String str, String str2) {
        this.listenerList.piFound(new ParserEvent(this, str, str2));
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected void doctype(String str, String str2, String str3) {
        this.listenerList.doctypeFound(new ParserEvent(this, str, str2, str3));
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected void flush(char[] cArr, int i, int i2) {
        if (i2 == 1 && (cArr[i] == '\n' || cArr[i] == '\r')) {
            return;
        }
        this.listenerList.textFound(new ParserEvent(this, new String(cArr, i, i2)));
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected void comment(String str) {
        this.listenerList.commentFound(new ParserEvent(this, str));
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected void errorString(String str) {
        this.listenerList.errorFound(new ParserEvent(this, str));
    }

    @Override // com.sun.java.help.impl.DocumentParser
    protected String documentAttribute(String str) {
        return null;
    }

    public void addParserListener(ParserListener parserListener) {
        this.listenerList = ParserMulticaster.add(this.listenerList, parserListener);
    }

    public void removeParserListener(ParserListener parserListener) {
        this.listenerList = ParserMulticaster.remove(this.listenerList, parserListener);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/Parser$ParserMulticaster.class */
    protected static class ParserMulticaster implements ParserListener {
        protected final EventListener a;
        protected final EventListener b;

        protected ParserMulticaster(EventListener eventListener, EventListener eventListener2) {
            this.a = eventListener;
            this.b = eventListener2;
        }

        protected EventListener remove(EventListener eventListener) {
            if (eventListener == this.a) {
                return this.b;
            }
            if (eventListener == this.b) {
                return this.a;
            }
            EventListener eventListenerRemoveInternal = removeInternal(this.a, eventListener);
            EventListener eventListenerRemoveInternal2 = removeInternal(this.b, eventListener);
            if (eventListenerRemoveInternal == this.a && eventListenerRemoveInternal2 == this.b) {
                return this;
            }
            return addInternal(eventListenerRemoveInternal, eventListenerRemoveInternal2);
        }

        protected static EventListener addInternal(EventListener eventListener, EventListener eventListener2) {
            if (eventListener == null) {
                return eventListener2;
            }
            return eventListener2 == null ? eventListener : new ParserMulticaster(eventListener, eventListener2);
        }

        protected static EventListener removeInternal(EventListener eventListener, EventListener eventListener2) {
            if (eventListener == eventListener2 || eventListener == null) {
                return null;
            }
            if (eventListener instanceof ParserMulticaster) {
                return ((ParserMulticaster) eventListener).remove(eventListener2);
            }
            return eventListener;
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void tagFound(ParserEvent parserEvent) {
            ((ParserListener) this.a).tagFound(parserEvent);
            ((ParserListener) this.b).tagFound(parserEvent);
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void piFound(ParserEvent parserEvent) {
            ((ParserListener) this.a).piFound(parserEvent);
            ((ParserListener) this.b).piFound(parserEvent);
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void doctypeFound(ParserEvent parserEvent) {
            ((ParserListener) this.a).doctypeFound(parserEvent);
            ((ParserListener) this.b).doctypeFound(parserEvent);
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void textFound(ParserEvent parserEvent) {
            ((ParserListener) this.a).textFound(parserEvent);
            ((ParserListener) this.b).textFound(parserEvent);
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void commentFound(ParserEvent parserEvent) {
            ((ParserListener) this.a).commentFound(parserEvent);
            ((ParserListener) this.b).commentFound(parserEvent);
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void errorFound(ParserEvent parserEvent) {
            ((ParserListener) this.a).errorFound(parserEvent);
            ((ParserListener) this.b).errorFound(parserEvent);
        }

        public static ParserListener add(ParserListener parserListener, ParserListener parserListener2) {
            return (ParserListener) addInternal(parserListener, parserListener2);
        }

        public static ParserListener remove(ParserListener parserListener, ParserListener parserListener2) {
            return (ParserListener) removeInternal(parserListener, parserListener2);
        }
    }
}
