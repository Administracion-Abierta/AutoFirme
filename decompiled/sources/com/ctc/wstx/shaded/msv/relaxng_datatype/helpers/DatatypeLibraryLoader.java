package com.ctc.wstx.shaded.msv.relaxng_datatype.helpers;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.twelvemonkeys.util.service.ServiceRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader.class */
public class DatatypeLibraryLoader implements DatatypeLibraryFactory {
    private final Service service;
    static Class class$org$relaxng$datatype$DatatypeLibraryFactory;

    /* JADX INFO: renamed from: com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader$1.class */
    class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader$Service.class */
    private static class Service {
        private final Class serviceClass;
        private final Enumeration configFiles;
        private Enumeration classNames = null;
        private final Vector providers = new Vector();
        private Loader loader;
        private static final int START = 0;
        private static final int IN_NAME = 1;
        private static final int IN_COMMENT = 2;

        /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader$Service$Loader.class */
        private static class Loader {
            static Class class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader;

            private Loader() {
            }

            Enumeration getResources(String str) {
                Class clsClass$;
                if (class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader == null) {
                    clsClass$ = class$("com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader$Service$Loader");
                    class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader = clsClass$;
                } else {
                    clsClass$ = class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader;
                }
                ClassLoader classLoader = clsClass$.getClassLoader();
                return new Singleton(classLoader == null ? ClassLoader.getSystemResource(str) : classLoader.getResource(str), null);
            }

            Class loadClass(String str) throws ClassNotFoundException {
                return Class.forName(str);
            }

            static Class class$(String str) {
                try {
                    return Class.forName(str);
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }

            Loader(AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader$Service$Loader2.class */
        private static class Loader2 extends Loader {
            private ClassLoader cl;
            static Class class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2;

            Loader2() {
                Class clsClass$;
                super(null);
                if (class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2 == null) {
                    clsClass$ = class$("com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader$Service$Loader2");
                    class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2 = clsClass$;
                } else {
                    clsClass$ = class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2;
                }
                this.cl = clsClass$.getClassLoader();
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                ClassLoader parent = contextClassLoader;
                while (true) {
                    ClassLoader classLoader = parent;
                    if (classLoader == null) {
                        return;
                    }
                    if (classLoader == this.cl) {
                        this.cl = contextClassLoader;
                        return;
                    }
                    parent = classLoader.getParent();
                }
            }

            @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader.Service.Loader
            Enumeration getResources(String str) {
                try {
                    return this.cl.getResources(str);
                } catch (IOException e) {
                    return new Singleton(null, null);
                }
            }

            @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader.Service.Loader
            Class loadClass(String str) throws ClassNotFoundException {
                return Class.forName(str, true, this.cl);
            }

            static Class class$(String str) {
                try {
                    return Class.forName(str);
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader$Service$ProviderEnumeration.class */
        private class ProviderEnumeration implements Enumeration {
            private int nextIndex;
            private final Service this$0;

            private ProviderEnumeration(Service service) {
                this.this$0 = service;
                this.nextIndex = 0;
            }

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.nextIndex < this.this$0.providers.size() || this.this$0.moreProviders();
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                try {
                    Vector vector = this.this$0.providers;
                    int i = this.nextIndex;
                    this.nextIndex = i + 1;
                    return vector.elementAt(i);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }

            ProviderEnumeration(Service service, AnonymousClass1 anonymousClass1) {
                this(service);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/DatatypeLibraryLoader$Service$Singleton.class */
        private static class Singleton implements Enumeration {
            private Object obj;

            private Singleton(Object obj) {
                this.obj = obj;
            }

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.obj != null;
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                if (this.obj == null) {
                    throw new NoSuchElementException();
                }
                Object obj = this.obj;
                this.obj = null;
                return obj;
            }

            Singleton(Object obj, AnonymousClass1 anonymousClass1) {
                this(obj);
            }
        }

        public Service(Class cls) {
            try {
                this.loader = new Loader2();
            } catch (NoSuchMethodError e) {
                this.loader = new Loader(null);
            }
            this.serviceClass = cls;
            this.configFiles = this.loader.getResources(new StringBuffer().append(ServiceRegistry.SERVICES).append(this.serviceClass.getName()).toString());
        }

        public Enumeration getProviders() {
            return new ProviderEnumeration(this, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized boolean moreProviders() {
            while (true) {
                if (this.classNames != null) {
                    while (this.classNames.hasMoreElements()) {
                        try {
                            Object objNewInstance = this.loader.loadClass((String) this.classNames.nextElement()).newInstance();
                            if (this.serviceClass.isInstance(objNewInstance)) {
                                this.providers.addElement(objNewInstance);
                                return true;
                            }
                        } catch (ClassNotFoundException e) {
                        } catch (IllegalAccessException e2) {
                        } catch (InstantiationException e3) {
                        } catch (LinkageError e4) {
                        }
                    }
                    this.classNames = null;
                } else {
                    if (!this.configFiles.hasMoreElements()) {
                        return false;
                    }
                    this.classNames = parseConfigFile((URL) this.configFiles.nextElement());
                }
            }
        }

        private static Enumeration parseConfigFile(URL url) {
            InputStreamReader inputStreamReader;
            BufferedReader bufferedReader;
            Vector vector;
            StringBuffer stringBuffer;
            char c;
            try {
                InputStream inputStreamOpenStream = url.openStream();
                try {
                    inputStreamReader = new InputStreamReader(inputStreamOpenStream, "UTF-8");
                    while (true) {
                        int i = bufferedReader.read();
                        if (i < 0) {
                            if (stringBuffer.length() != 0) {
                                vector.addElement(stringBuffer.toString());
                            }
                            return vector.elements();
                        }
                        char c2 = (char) i;
                        switch (c2) {
                            case '\t':
                            case ' ':
                                break;
                            case '\n':
                            case '\r':
                                c = 0;
                                break;
                            case '#':
                                c = 2;
                                break;
                            default:
                                if (c != 2) {
                                    c = 1;
                                    stringBuffer.append(c2);
                                }
                                break;
                        }
                        if (stringBuffer.length() != 0 && c != 1) {
                            vector.addElement(stringBuffer.toString());
                            stringBuffer.setLength(0);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    inputStreamReader = new InputStreamReader(inputStreamOpenStream, "UTF8");
                }
                bufferedReader = new BufferedReader(inputStreamReader);
                vector = new Vector();
                stringBuffer = new StringBuffer();
                c = 0;
            } catch (IOException e2) {
                return null;
            }
        }
    }

    public DatatypeLibraryLoader() {
        Class clsClass$;
        if (class$org$relaxng$datatype$DatatypeLibraryFactory == null) {
            clsClass$ = class$("com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory");
            class$org$relaxng$datatype$DatatypeLibraryFactory = clsClass$;
        } else {
            clsClass$ = class$org$relaxng$datatype$DatatypeLibraryFactory;
        }
        this.service = new Service(clsClass$);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory
    public DatatypeLibrary createDatatypeLibrary(String str) {
        Enumeration providers = this.service.getProviders();
        while (providers.hasMoreElements()) {
            DatatypeLibrary datatypeLibraryCreateDatatypeLibrary = ((DatatypeLibraryFactory) providers.nextElement()).createDatatypeLibrary(str);
            if (datatypeLibraryCreateDatatypeLibrary != null) {
                return datatypeLibraryCreateDatatypeLibrary;
            }
        }
        return null;
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}
