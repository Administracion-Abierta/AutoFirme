package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/Dispatcher.class */
public interface Dispatcher {
    void attachXMLReader(XMLReader xMLReader);

    void switchVerifier(IslandVerifier islandVerifier) throws SAXException;

    void setErrorHandler(ErrorHandler errorHandler);

    ErrorHandler getErrorHandler();

    SchemaProvider getSchemaProvider();

    int countNotationDecls();

    NotationDecl getNotationDecl(int i);

    int countUnparsedEntityDecls();

    UnparsedEntityDecl getUnparsedEntityDecl(int i);

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/Dispatcher$NotationDecl.class */
    public static class NotationDecl {
        public final String name;
        public final String publicId;
        public final String systemId;

        public NotationDecl(String name, String publicId, String systemId) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/Dispatcher$UnparsedEntityDecl.class */
    public static class UnparsedEntityDecl {
        public final String name;
        public final String publicId;
        public final String systemId;
        public final String notation;

        public UnparsedEntityDecl(String name, String publicId, String systemId, String notation) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
            this.notation = notation;
        }
    }
}
