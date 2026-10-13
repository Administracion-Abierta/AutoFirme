package org.apache.xml.security.parser;

import com.aowagie.text.xml.XMLConstants;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/parser/XMLParserImpl.class */
public class XMLParserImpl implements XMLParser {
    private static int parserPoolSize = ((Integer) AccessController.doPrivileged(() -> {
        return Integer.getInteger("org.apache.xml.security.parser.pool-size", 20);
    })).intValue();
    private static final Map<ClassLoader, Queue<DocumentBuilder>> DOCUMENT_BUILDERS = Collections.synchronizedMap(new WeakHashMap());
    private static final Map<ClassLoader, Queue<DocumentBuilder>> DOCUMENT_BUILDERS_DISALLOW_DOCTYPE = Collections.synchronizedMap(new WeakHashMap());

    @Override // org.apache.xml.security.parser.XMLParser
    public Document parse(InputStream inputStream, boolean disallowDocTypeDeclarations) throws XMLParserException {
        try {
            ClassLoader loader = getContextClassLoader();
            if (loader == null) {
                loader = getClassLoader(XMLUtils.class);
            }
            if (loader == null) {
                return createDocumentBuilder(disallowDocTypeDeclarations).parse(inputStream);
            }
            Queue<DocumentBuilder> queue = getDocumentBuilderQueue(disallowDocTypeDeclarations, loader);
            DocumentBuilder documentBuilder = getDocumentBuilder(disallowDocTypeDeclarations, queue);
            Document doc = documentBuilder.parse(inputStream);
            repoolDocumentBuilder(documentBuilder, queue);
            return doc;
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            throw new XMLParserException(ex, "empty", new Object[]{"Error parsing the inputstream"});
        }
    }

    private static Queue<DocumentBuilder> getDocumentBuilderQueue(boolean disallowDocTypeDeclarations, ClassLoader loader) throws ParserConfigurationException {
        Map<ClassLoader, Queue<DocumentBuilder>> docBuilderCache = disallowDocTypeDeclarations ? DOCUMENT_BUILDERS_DISALLOW_DOCTYPE : DOCUMENT_BUILDERS;
        Queue<DocumentBuilder> queue = docBuilderCache.get(loader);
        if (queue == null) {
            queue = new ArrayBlockingQueue(parserPoolSize);
            docBuilderCache.put(loader, queue);
        }
        return queue;
    }

    private static DocumentBuilder getDocumentBuilder(boolean disallowDocTypeDeclarations, Queue<DocumentBuilder> queue) throws ParserConfigurationException {
        DocumentBuilder db = queue.poll();
        if (db == null) {
            db = createDocumentBuilder(disallowDocTypeDeclarations);
        }
        return db;
    }

    private static DocumentBuilder createDocumentBuilder(boolean disallowDocTypeDeclarations) throws ParserConfigurationException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disallowDocTypeDeclarations);
        return f.newDocumentBuilder();
    }

    private static void repoolDocumentBuilder(DocumentBuilder db, Queue<DocumentBuilder> queue) {
        if (queue != null) {
            db.reset();
            queue.offer(db);
        }
    }

    private static ClassLoader getContextClassLoader() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() { // from class: org.apache.xml.security.parser.XMLParserImpl.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
        return Thread.currentThread().getContextClassLoader();
    }

    private static ClassLoader getClassLoader(final Class<?> clazz) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() { // from class: org.apache.xml.security.parser.XMLParserImpl.2
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public ClassLoader run() {
                    return clazz.getClassLoader();
                }
            });
        }
        return clazz.getClassLoader();
    }
}
