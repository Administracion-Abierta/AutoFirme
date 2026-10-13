package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/DocumentContextImpl.class */
public class DocumentContextImpl implements DocumentContext, Cloneable {
    private String encoding;
    private String baseURI;
    private final Map<Integer, XMLSecurityConstants.ContentType> contentTypeMap = new TreeMap();
    private final Map<Object, Integer> processorToIndexMap = new HashMap();

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public String getBaseURI() {
        return this.baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public synchronized void setIsInEncryptedContent(int index, Object key) {
        this.contentTypeMap.put(Integer.valueOf(index), XMLSecurityConstants.ContentType.ENCRYPTION);
        this.processorToIndexMap.put(key, Integer.valueOf(index));
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public synchronized void unsetIsInEncryptedContent(Object key) {
        Integer index = this.processorToIndexMap.remove(key);
        this.contentTypeMap.remove(index);
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public boolean isInEncryptedContent() {
        return this.contentTypeMap.containsValue(XMLSecurityConstants.ContentType.ENCRYPTION);
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public synchronized void setIsInSignedContent(int index, Object key) {
        this.contentTypeMap.put(Integer.valueOf(index), XMLSecurityConstants.ContentType.SIGNATURE);
        this.processorToIndexMap.put(key, Integer.valueOf(index));
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public synchronized void unsetIsInSignedContent(Object key) {
        Integer index = this.processorToIndexMap.remove(key);
        this.contentTypeMap.remove(index);
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public boolean isInSignedContent() {
        return this.contentTypeMap.containsValue(XMLSecurityConstants.ContentType.SIGNATURE);
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public List<XMLSecurityConstants.ContentType> getProtectionOrder() {
        return new ArrayList(this.contentTypeMap.values());
    }

    @Override // org.apache.xml.security.stax.ext.DocumentContext
    public Map<Integer, XMLSecurityConstants.ContentType> getContentTypeMap() {
        return Collections.unmodifiableMap(this.contentTypeMap);
    }

    protected void setContentTypeMap(Map<Integer, XMLSecurityConstants.ContentType> contentTypeMap) {
        this.contentTypeMap.putAll(contentTypeMap);
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public DocumentContextImpl m1377clone() throws CloneNotSupportedException {
        DocumentContextImpl documentContext = (DocumentContextImpl) super.clone();
        documentContext.setEncoding(this.encoding);
        documentContext.setBaseURI(this.baseURI);
        documentContext.setContentTypeMap(getContentTypeMap());
        return documentContext;
    }
}
