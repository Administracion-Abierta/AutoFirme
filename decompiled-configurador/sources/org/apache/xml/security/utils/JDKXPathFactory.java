package org.apache.xml.security.utils;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/JDKXPathFactory.class */
public class JDKXPathFactory extends XPathFactory {
    @Override // org.apache.xml.security.utils.XPathFactory
    public XPathAPI newXPathAPI() {
        return new JDKXPathAPI();
    }
}
