package org.apache.xml.security.stax.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.configuration.TransformAlgorithmType;
import org.apache.xml.security.configuration.TransformAlgorithmsType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.utils.ClassLoaderUtils;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/config/TransformerAlgorithmMapper.class */
public final class TransformerAlgorithmMapper {
    private static Map<String, Class<?>> algorithmsClassMapInOut;
    private static Map<String, Class<?>> algorithmsClassMapIn;
    private static Map<String, Class<?>> algorithmsClassMapOut;

    private TransformerAlgorithmMapper() {
    }

    protected static synchronized void init(TransformAlgorithmsType transformAlgorithms, Class<?> callingClass) throws Exception {
        List<TransformAlgorithmType> algorithms = transformAlgorithms.getTransformAlgorithm();
        algorithmsClassMapInOut = new HashMap();
        algorithmsClassMapIn = new HashMap();
        algorithmsClassMapOut = new HashMap();
        for (TransformAlgorithmType algorithmType : algorithms) {
            if (algorithmType.getINOUT() == null) {
                algorithmsClassMapInOut.put(algorithmType.getURI(), ClassLoaderUtils.loadClass(algorithmType.getJAVACLASS(), callingClass));
            } else if ("IN".equals(algorithmType.getINOUT().value())) {
                algorithmsClassMapIn.put(algorithmType.getURI(), ClassLoaderUtils.loadClass(algorithmType.getJAVACLASS(), callingClass));
            } else if ("OUT".equals(algorithmType.getINOUT().value())) {
                algorithmsClassMapOut.put(algorithmType.getURI(), ClassLoaderUtils.loadClass(algorithmType.getJAVACLASS(), callingClass));
            } else {
                throw new IllegalArgumentException("INOUT parameter " + algorithmType.getINOUT().value() + " unsupported");
            }
        }
    }

    public static Class<?> getTransformerClass(String algoURI, XMLSecurityConstants.DIRECTION direction) throws XMLSecurityException {
        Class<?> clazz = null;
        if (XMLSecurityConstants.DIRECTION.IN == direction) {
            clazz = algorithmsClassMapIn.get(algoURI);
        } else if (XMLSecurityConstants.DIRECTION.OUT == direction) {
            clazz = algorithmsClassMapOut.get(algoURI);
        }
        if (clazz == null) {
            clazz = algorithmsClassMapInOut.get(algoURI);
        }
        if (clazz == null) {
            throw new XMLSecurityException("signature.Transform.UnknownTransform", new Object[]{algoURI});
        }
        return clazz;
    }
}
