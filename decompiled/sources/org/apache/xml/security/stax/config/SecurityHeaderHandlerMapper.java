package org.apache.xml.security.stax.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xml.security.configuration.HandlerType;
import org.apache.xml.security.configuration.SecurityHeaderHandlersType;
import org.apache.xml.security.utils.ClassLoaderUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/config/SecurityHeaderHandlerMapper.class */
public final class SecurityHeaderHandlerMapper {
    private static Map<QName, Class<?>> handlerClassMap;

    private SecurityHeaderHandlerMapper() {
    }

    protected static synchronized void init(SecurityHeaderHandlersType securityHeaderHandlersType, Class<?> callingClass) throws Exception {
        List<HandlerType> handlerList = securityHeaderHandlersType.getHandler();
        int handlerListSize = handlerList.size();
        handlerClassMap = new HashMap((int) Math.ceil(((double) handlerListSize) / 0.75d));
        for (int i = 0; i < handlerListSize; i++) {
            HandlerType handlerType = handlerList.get(i);
            QName qName = new QName(handlerType.getURI(), handlerType.getNAME());
            handlerClassMap.put(qName, ClassLoaderUtils.loadClass(handlerType.getJAVACLASS(), callingClass));
        }
    }

    public static Class<?> getSecurityHeaderHandler(QName name) {
        return handlerClassMap.get(name);
    }
}
