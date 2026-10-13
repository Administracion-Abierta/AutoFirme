package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/WbemcliUtil.class */
public class WbemcliUtil {
    public static final WbemcliUtil INSTANCE = new WbemcliUtil();
    public static final String DEFAULT_NAMESPACE = "ROOT\\CIMV2";

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/WbemcliUtil$NamespaceProperty.class */
    private enum NamespaceProperty {
        NAME
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/WbemcliUtil$WmiQuery.class */
    public static class WmiQuery<T extends Enum<T>> {
        private String nameSpace;
        private String wmiClassName;
        private Class<T> propertyEnum;

        public WmiQuery(String nameSpace, String wmiClassName, Class<T> propertyEnum) {
            this.nameSpace = nameSpace;
            this.wmiClassName = wmiClassName;
            this.propertyEnum = propertyEnum;
        }

        public WmiQuery(String wmiClassName, Class<T> propertyEnum) {
            this(WbemcliUtil.DEFAULT_NAMESPACE, wmiClassName, propertyEnum);
        }

        public Class<T> getPropertyEnum() {
            return this.propertyEnum;
        }

        public String getNameSpace() {
            return this.nameSpace;
        }

        public void setNameSpace(String nameSpace) {
            this.nameSpace = nameSpace;
        }

        public String getWmiClassName() {
            return this.wmiClassName;
        }

        public void setWmiClassName(String wmiClassName) {
            this.wmiClassName = wmiClassName;
        }

        public WmiResult<T> execute() {
            try {
                return execute(-1);
            } catch (TimeoutException e) {
                throw new COMException("Got a WMI timeout when infinite wait was specified. This should never happen.");
            }
        }

        public WmiResult<T> execute(int timeout) throws TimeoutException {
            if (getPropertyEnum().getEnumConstants().length < 1) {
                throw new IllegalArgumentException("The query's property enum has no values.");
            }
            Wbemcli.IWbemServices svc = WbemcliUtil.connectServer(getNameSpace());
            try {
                Wbemcli.IEnumWbemClassObject enumerator = selectProperties(svc, this);
                try {
                    WmiResult<T> wmiResultEnumerateProperties = enumerateProperties(enumerator, getPropertyEnum(), timeout);
                    enumerator.Release();
                    svc.Release();
                    return wmiResultEnumerateProperties;
                } catch (Throwable th) {
                    enumerator.Release();
                    throw th;
                }
            } catch (Throwable th2) {
                svc.Release();
                throw th2;
            }
        }

        private static <T extends Enum<T>> Wbemcli.IEnumWbemClassObject selectProperties(Wbemcli.IWbemServices svc, WmiQuery<T> query) {
            T[] props = query.getPropertyEnum().getEnumConstants();
            StringBuilder sb = new StringBuilder("SELECT ");
            sb.append(props[0].name());
            for (int i = 1; i < props.length; i++) {
                sb.append(',').append(props[i].name());
            }
            sb.append(" FROM ").append(query.getWmiClassName());
            return svc.ExecQuery("WQL", sb.toString().replaceAll("\\\\", "\\\\\\\\"), 48, null);
        }

        private static <T extends Enum<T>> WmiResult<T> enumerateProperties(Wbemcli.IEnumWbemClassObject enumerator, Class<T> propertyEnum, int timeout) throws TimeoutException {
            WbemcliUtil wbemcliUtil = WbemcliUtil.INSTANCE;
            wbemcliUtil.getClass();
            WmiResult<T> values = wbemcliUtil.new WmiResult<>(propertyEnum);
            Pointer[] pclsObj = new Pointer[1];
            IntByReference uReturn = new IntByReference(0);
            Map<T, WString> wstrMap = new HashMap<>();
            for (T property : propertyEnum.getEnumConstants()) {
                wstrMap.put(property, new WString(property.name()));
            }
            while (enumerator.getPointer() != Pointer.NULL) {
                WinNT.HRESULT hres = enumerator.Next(timeout, pclsObj.length, pclsObj, uReturn);
                if (hres.intValue() != 1 && hres.intValue() != 262149) {
                    if (hres.intValue() == 262148) {
                        throw new TimeoutException("No results after " + timeout + " ms.");
                    }
                    if (COMUtils.FAILED(hres)) {
                        throw new COMException("Failed to enumerate results.", hres);
                    }
                    Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();
                    IntByReference pType = new IntByReference();
                    Wbemcli.IWbemClassObject clsObj = new Wbemcli.IWbemClassObject(pclsObj[0]);
                    for (T property2 : propertyEnum.getEnumConstants()) {
                        clsObj.Get(wstrMap.get(property2), 0, pVal, pType, (IntByReference) null);
                        int vtType = (pVal.getValue() == null ? 1 : pVal.getVarType()).intValue();
                        int cimType = pType.getValue();
                        switch (vtType) {
                            case 0:
                            case 1:
                                values.add(vtType, cimType, property2, null);
                                break;
                            case 2:
                                values.add(vtType, cimType, property2, Short.valueOf(pVal.shortValue()));
                                break;
                            case 3:
                                values.add(vtType, cimType, property2, Integer.valueOf(pVal.intValue()));
                                break;
                            case 4:
                                values.add(vtType, cimType, property2, Float.valueOf(pVal.floatValue()));
                                break;
                            case 5:
                                values.add(vtType, cimType, property2, Double.valueOf(pVal.doubleValue()));
                                break;
                            case 6:
                            case 7:
                            case 9:
                            case 10:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            default:
                                if ((vtType & 8192) == 8192 || (vtType & 13) == 13 || (vtType & 9) == 9 || (vtType & 4096) == 4096) {
                                    values.add(vtType, cimType, property2, null);
                                } else {
                                    values.add(vtType, cimType, property2, pVal.getValue());
                                }
                                break;
                            case 8:
                                values.add(vtType, cimType, property2, pVal.stringValue());
                                break;
                            case 11:
                                values.add(vtType, cimType, property2, Boolean.valueOf(pVal.booleanValue()));
                                break;
                            case 17:
                                values.add(vtType, cimType, property2, Byte.valueOf(pVal.byteValue()));
                                break;
                        }
                        OleAuto.INSTANCE.VariantClear(pVal);
                    }
                    clsObj.Release();
                    values.incrementResultCount();
                } else {
                    return values;
                }
            }
            return values;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/WbemcliUtil$WmiResult.class */
    public class WmiResult<T extends Enum<T>> {
        private Map<T, List<Object>> propertyMap;
        private Map<T, Integer> vtTypeMap;
        private Map<T, Integer> cimTypeMap;
        private int resultCount = 0;

        public WmiResult(Class<T> propertyEnum) {
            this.propertyMap = new EnumMap(propertyEnum);
            this.vtTypeMap = new EnumMap(propertyEnum);
            this.cimTypeMap = new EnumMap(propertyEnum);
            for (T prop : propertyEnum.getEnumConstants()) {
                this.propertyMap.put(prop, new ArrayList());
                this.vtTypeMap.put(prop, 1);
                this.cimTypeMap.put(prop, 0);
            }
        }

        public Object getValue(T property, int index) {
            return this.propertyMap.get(property).get(index);
        }

        public int getVtType(T property) {
            return this.vtTypeMap.get(property).intValue();
        }

        public int getCIMType(T property) {
            return this.cimTypeMap.get(property).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void add(int vtType, int cimType, T property, Object o) {
            this.propertyMap.get(property).add(o);
            if (vtType != 1 && this.vtTypeMap.get(property).equals(1)) {
                this.vtTypeMap.put(property, Integer.valueOf(vtType));
            }
            if (this.cimTypeMap.get(property).equals(0)) {
                this.cimTypeMap.put(property, Integer.valueOf(cimType));
            }
        }

        public int getResultCount() {
            return this.resultCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void incrementResultCount() {
            this.resultCount++;
        }
    }

    public static boolean hasNamespace(String namespace) {
        String ns = namespace;
        if (namespace.toUpperCase().startsWith("ROOT\\")) {
            ns = namespace.substring(5);
        }
        WmiQuery<NamespaceProperty> namespaceQuery = new WmiQuery<>(Logger.ROOT_LOGGER_NAME, "__NAMESPACE", NamespaceProperty.class);
        WmiResult wmiResultExecute = namespaceQuery.execute();
        for (int i = 0; i < wmiResultExecute.getResultCount(); i++) {
            if (ns.equalsIgnoreCase((String) wmiResultExecute.getValue(NamespaceProperty.NAME, i))) {
                return true;
            }
        }
        return false;
    }

    public static Wbemcli.IWbemServices connectServer(String namespace) {
        Wbemcli.IWbemLocator loc = Wbemcli.IWbemLocator.create();
        if (loc == null) {
            throw new COMException("Failed to create WbemLocator object.");
        }
        Wbemcli.IWbemServices services = loc.ConnectServer(namespace, null, null, null, 0, null, null);
        loc.Release();
        WinNT.HRESULT hres = Ole32.INSTANCE.CoSetProxyBlanket(services, 10, 0, null, 3, 3, null, 0);
        if (COMUtils.FAILED(hres)) {
            services.Release();
            throw new COMException("Could not set proxy blanket.", hres);
        }
        return services;
    }
}
