package com.ctc.wstx.api;

import com.ctc.wstx.util.ArgUtil;
import java.util.HashMap;
import org.codehaus.stax2.XMLStreamProperties;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/api/CommonConfig.class */
public abstract class CommonConfig implements XMLStreamProperties {
    protected static final String IMPL_NAME = "woodstox";
    protected static final String IMPL_VERSION = "5.0";
    static final int CPROP_IMPL_NAME = 1;
    static final int CPROP_IMPL_VERSION = 2;
    static final int CPROP_SUPPORTS_XML11 = 3;
    static final int CPROP_SUPPORT_XMLID = 4;
    static final int CPROP_RETURN_NULL_FOR_DEFAULT_NAMESPACE = 5;
    static final HashMap<String, Integer> sStdProperties = new HashMap<>(16);
    protected boolean mReturnNullForDefaultNamespace;

    protected abstract int findPropertyId(String str);

    protected abstract Object getProperty(int i);

    protected abstract boolean setProperty(String str, int i, Object obj);

    static {
        sStdProperties.put("org.codehaus.stax2.implName", 1);
        sStdProperties.put("org.codehaus.stax2.implVersion", 2);
        sStdProperties.put(XMLStreamProperties.XSP_SUPPORTS_XML11, 3);
        sStdProperties.put(XMLStreamProperties.XSP_SUPPORT_XMLID, 4);
        sStdProperties.put(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE, 5);
        sStdProperties.put("http://java.sun.com/xml/stream/properties/implementation-name", 1);
    }

    protected CommonConfig(CommonConfig base) {
        this.mReturnNullForDefaultNamespace = base == null ? Boolean.getBoolean(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE) : base.mReturnNullForDefaultNamespace;
    }

    public Object getProperty(String propName) {
        int id = findPropertyId(propName);
        if (id >= 0) {
            return getProperty(id);
        }
        int id2 = findStdPropertyId(propName);
        if (id2 < 0) {
            reportUnknownProperty(propName);
            return null;
        }
        return getStdProperty(id2);
    }

    public boolean isPropertySupported(String propName) {
        return findPropertyId(propName) >= 0 || findStdPropertyId(propName) >= 0;
    }

    public boolean setProperty(String propName, Object value) {
        int id = findPropertyId(propName);
        if (id >= 0) {
            return setProperty(propName, id, value);
        }
        int id2 = findStdPropertyId(propName);
        if (id2 < 0) {
            reportUnknownProperty(propName);
            return false;
        }
        return setStdProperty(propName, id2, value);
    }

    protected void reportUnknownProperty(String propName) {
        throw new IllegalArgumentException("Unrecognized property '" + propName + "'");
    }

    public final Object safeGetProperty(String propName) {
        int id = findPropertyId(propName);
        if (id >= 0) {
            return getProperty(id);
        }
        int id2 = findStdPropertyId(propName);
        if (id2 < 0) {
            return null;
        }
        return getStdProperty(id2);
    }

    public static String getImplName() {
        return IMPL_NAME;
    }

    public static String getImplVersion() {
        return IMPL_VERSION;
    }

    public boolean doesSupportXml11() {
        return true;
    }

    public boolean doesSupportXmlId() {
        return true;
    }

    public boolean returnNullForDefaultNamespace() {
        return this.mReturnNullForDefaultNamespace;
    }

    protected int findStdPropertyId(String propName) {
        Integer I = sStdProperties.get(propName);
        if (I == null) {
            return -1;
        }
        return I.intValue();
    }

    protected boolean setStdProperty(String propName, int id, Object value) {
        switch (id) {
            case 5:
                this.mReturnNullForDefaultNamespace = ArgUtil.convertToBoolean(propName, value);
                return true;
            default:
                return false;
        }
    }

    protected Object getStdProperty(int id) {
        switch (id) {
            case 1:
                return IMPL_NAME;
            case 2:
                return IMPL_VERSION;
            case 3:
                return doesSupportXml11() ? Boolean.TRUE : Boolean.FALSE;
            case 4:
                return doesSupportXmlId() ? Boolean.TRUE : Boolean.FALSE;
            case 5:
                return returnNullForDefaultNamespace() ? Boolean.TRUE : Boolean.FALSE;
            default:
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
        }
    }
}
