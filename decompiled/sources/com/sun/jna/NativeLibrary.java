package com.sun.jna;

import com.sun.jna.internal.Cleaner;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/NativeLibrary.class */
public class NativeLibrary implements Closeable {
    private static final Logger LOG = Logger.getLogger(NativeLibrary.class.getName());
    private static final Level DEBUG_LOAD_LEVEL;
    private static final SymbolProvider NATIVE_SYMBOL_PROVIDER;
    private Cleaner.Cleanable cleanable;
    private long handle;
    private final String libraryName;
    private final String libraryPath;
    private final Map<String, Function> functions = new HashMap();
    private final SymbolProvider symbolProvider;
    final int callFlags;
    private String encoding;
    final Map<String, ?> options;
    private static final Map<String, Reference<NativeLibrary>> libraries;
    private static final Map<String, List<String>> searchPaths;
    private static final LinkedHashSet<String> librarySearchPath;
    private static final int DEFAULT_OPEN_OPTIONS = -1;
    private static Method addSuppressedMethod;

    static {
        DEBUG_LOAD_LEVEL = Native.DEBUG_LOAD ? Level.INFO : Level.FINE;
        NATIVE_SYMBOL_PROVIDER = new SymbolProvider() { // from class: com.sun.jna.NativeLibrary.1
            @Override // com.sun.jna.SymbolProvider
            public long getSymbolAddress(long handle, String name, SymbolProvider parent) {
                return Native.findSymbol(handle, name);
            }
        };
        libraries = new HashMap();
        searchPaths = new ConcurrentHashMap();
        librarySearchPath = new LinkedHashSet<>();
        if (Native.POINTER_SIZE == 0) {
            throw new Error("Native library not initialized");
        }
        addSuppressedMethod = null;
        try {
            addSuppressedMethod = Throwable.class.getMethod("addSuppressed", Throwable.class);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException ex) {
            Logger.getLogger(NativeLibrary.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", (Throwable) ex);
        }
        String webstartPath = Native.getWebStartLibraryPath("jnidispatch");
        if (webstartPath != null) {
            librarySearchPath.add(webstartPath);
        }
        if (System.getProperty("jna.platform.library.path") == null && !Platform.isWindows()) {
            String platformPath = "";
            String sep = "";
            String archPath = "";
            if (Platform.isLinux() || Platform.isSolaris() || Platform.isFreeBSD() || Platform.iskFreeBSD()) {
                archPath = (Platform.isSolaris() ? "/" : "") + (Native.POINTER_SIZE * 8);
            }
            String[] paths = {"/usr/lib" + archPath, "/lib" + archPath, "/usr/lib", "/lib"};
            if (Platform.isLinux() || Platform.iskFreeBSD() || Platform.isGNU()) {
                String multiArchPath = getMultiArchPath();
                paths = new String[]{"/usr/lib/" + multiArchPath, "/lib/" + multiArchPath, "/usr/lib" + archPath, "/lib" + archPath, "/usr/lib", "/lib"};
            }
            if (Platform.isLinux()) {
                ArrayList<String> ldPaths = getLinuxLdPaths();
                for (int i = paths.length - 1; 0 <= i; i--) {
                    int found = ldPaths.indexOf(paths[i]);
                    if (found != -1) {
                        ldPaths.remove(found);
                    }
                    ldPaths.add(0, paths[i]);
                }
                paths = (String[]) ldPaths.toArray(new String[0]);
            }
            for (int i2 = 0; i2 < paths.length; i2++) {
                File dir = new File(paths[i2]);
                if (dir.exists() && dir.isDirectory()) {
                    platformPath = platformPath + sep + paths[i2];
                    sep = File.pathSeparator;
                }
            }
            if (!"".equals(platformPath)) {
                System.setProperty("jna.platform.library.path", platformPath);
            }
        }
        librarySearchPath.addAll(initPaths("jna.platform.library.path"));
    }

    private static String functionKey(String name, int flags, String encoding) {
        return name + "|" + flags + "|" + encoding;
    }

    private NativeLibrary(String libraryName, String libraryPath, long handle, Map<String, ?> options) {
        this.libraryName = getLibraryName(libraryName);
        this.libraryPath = libraryPath;
        this.handle = handle;
        this.cleanable = Cleaner.getCleaner().register(this, new NativeLibraryDisposer(handle));
        Object option = options.get(Library.OPTION_CALLING_CONVENTION);
        int callingConvention = option instanceof Number ? ((Number) option).intValue() : 0;
        this.callFlags = callingConvention;
        this.options = options;
        this.encoding = (String) options.get(Library.OPTION_STRING_ENCODING);
        SymbolProvider optionSymbolProvider = (SymbolProvider) options.get(Library.OPTION_SYMBOL_PROVIDER);
        if (optionSymbolProvider == null) {
            this.symbolProvider = NATIVE_SYMBOL_PROVIDER;
        } else {
            this.symbolProvider = optionSymbolProvider;
        }
        if (this.encoding == null) {
            this.encoding = Native.getDefaultStringEncoding();
        }
        if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase())) {
            synchronized (this.functions) {
                Function f = new Function(this, "GetLastError", 63, this.encoding) { // from class: com.sun.jna.NativeLibrary.2
                    @Override // com.sun.jna.Function
                    Object invoke(Object[] args, Class<?> returnType, boolean b, int fixedArgs) {
                        return Integer.valueOf(Native.getLastError());
                    }

                    @Override // com.sun.jna.Function
                    Object invoke(Method invokingMethod, Class<?>[] paramTypes, Class<?> returnType, Object[] inArgs, Map<String, ?> options2) {
                        return Integer.valueOf(Native.getLastError());
                    }
                };
                this.functions.put(functionKey("GetLastError", this.callFlags, this.encoding), f);
            }
        }
    }

    private static int openFlags(Map<String, ?> options) {
        Object opt = options.get(Library.OPTION_OPEN_FLAGS);
        if (opt instanceof Number) {
            return ((Number) opt).intValue();
        }
        return -1;
    }

    /* JADX WARN: Code duplicated, block: B:86:0x0452  */
    /* JADX WARN: Code duplicated, block: B:89:0x0484 A[LOOP:1: B:87:0x047a->B:89:0x0484, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:93:0x04c6 A[LOOP:2: B:91:0x04bc->B:93:0x04c6, LOOP_END] */
    private static NativeLibrary loadLibrary(String libraryName, Map<String, ?> options) {
        StringBuilder sb;
        UnsatisfiedLinkError res;
        LOG.log(DEBUG_LOAD_LEVEL, "Looking for library '" + libraryName + OperatorName.SHOW_TEXT_LINE);
        List<Throwable> exceptions = new ArrayList<>();
        boolean isAbsolutePath = new File(libraryName).isAbsolute();
        LinkedHashSet<String> searchPath = new LinkedHashSet<>();
        int openFlags = openFlags(options);
        List<String> customPaths = searchPaths.get(libraryName);
        if (customPaths != null) {
            synchronized (customPaths) {
                searchPath.addAll(customPaths);
            }
        }
        String webstartPath = Native.getWebStartLibraryPath(libraryName);
        if (webstartPath != null) {
            LOG.log(DEBUG_LOAD_LEVEL, "Adding web start path " + webstartPath);
            searchPath.add(webstartPath);
        }
        LOG.log(DEBUG_LOAD_LEVEL, "Adding paths from jna.library.path: " + System.getProperty("jna.library.path"));
        searchPath.addAll(initPaths("jna.library.path"));
        String libraryPath = findLibraryPath(libraryName, searchPath);
        long handle = 0;
        try {
            LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);
            handle = Native.open(libraryPath, openFlags);
        } catch (UnsatisfiedLinkError e) {
            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + e.getMessage());
            LOG.log(DEBUG_LOAD_LEVEL, "Adding system paths: " + librarySearchPath);
            exceptions.add(e);
            searchPath.addAll(librarySearchPath);
        }
        if (handle == 0) {
            try {
                libraryPath = findLibraryPath(libraryName, searchPath);
                LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);
                handle = Native.open(libraryPath, openFlags);
                if (handle == 0) {
                    throw new UnsatisfiedLinkError("Failed to load library '" + libraryName + OperatorName.SHOW_TEXT_LINE);
                }
            } catch (UnsatisfiedLinkError ule) {
                LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + ule.getMessage());
                exceptions.add(ule);
                if (Platform.isAndroid()) {
                    try {
                        LOG.log(DEBUG_LOAD_LEVEL, "Preload (via System.loadLibrary) " + libraryName);
                        System.loadLibrary(libraryName);
                        handle = Native.open(libraryPath, openFlags);
                    } catch (UnsatisfiedLinkError e2) {
                        LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + e2.getMessage());
                        exceptions.add(e2);
                    }
                } else if (Platform.isLinux() || Platform.isFreeBSD()) {
                    LOG.log(DEBUG_LOAD_LEVEL, "Looking for version variants");
                    libraryPath = matchLibrary(libraryName, searchPath);
                    if (libraryPath != null) {
                        LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);
                        try {
                            handle = Native.open(libraryPath, openFlags);
                        } catch (UnsatisfiedLinkError e3) {
                            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + e3.getMessage());
                            exceptions.add(e3);
                        }
                    }
                } else if (Platform.isMac() && !libraryName.endsWith(".dylib")) {
                    for (String frameworkName : matchFramework(libraryName)) {
                        try {
                            LOG.log(DEBUG_LOAD_LEVEL, "Trying " + frameworkName);
                            handle = Native.open(frameworkName, openFlags);
                            break;
                        } catch (UnsatisfiedLinkError e4) {
                            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + e4.getMessage());
                            exceptions.add(e4);
                        }
                    }
                } else if (Platform.isWindows() && !isAbsolutePath) {
                    LOG.log(DEBUG_LOAD_LEVEL, "Looking for lib- prefix");
                    libraryPath = findLibraryPath("lib" + libraryName, searchPath);
                    if (libraryPath != null) {
                        LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);
                        try {
                            handle = Native.open(libraryPath, openFlags);
                        } catch (UnsatisfiedLinkError e5) {
                            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + e5.getMessage());
                            exceptions.add(e5);
                        }
                    }
                }
                if (handle == 0) {
                    try {
                        File embedded = Native.extractFromResourcePath(libraryName, (ClassLoader) options.get(Library.OPTION_CLASSLOADER));
                        if (embedded != null) {
                            try {
                                handle = Native.open(embedded.getAbsolutePath(), openFlags);
                                libraryPath = embedded.getAbsolutePath();
                                if (Native.isUnpacked(embedded)) {
                                    Native.deleteLibrary(embedded);
                                }
                            } catch (Throwable th) {
                                if (Native.isUnpacked(embedded)) {
                                    Native.deleteLibrary(embedded);
                                }
                                throw th;
                            }
                        }
                    } catch (IOException e6) {
                        LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + e6.getMessage());
                        exceptions.add(e6);
                        if (handle == 0) {
                            sb = new StringBuilder();
                            sb.append("Unable to load library '");
                            sb.append(libraryName);
                            sb.append("':");
                            for (Throwable t : exceptions) {
                                sb.append("\n");
                                sb.append(t.getMessage());
                            }
                            res = new UnsatisfiedLinkError(sb.toString());
                            for (Throwable t2 : exceptions) {
                                addSuppressedReflected(res, t2);
                            }
                            throw res;
                        }
                        LOG.log(DEBUG_LOAD_LEVEL, "Found library '" + libraryName + "' at " + libraryPath);
                        return new NativeLibrary(libraryName, libraryPath, handle, options);
                    }
                }
                if (handle == 0) {
                    sb = new StringBuilder();
                    sb.append("Unable to load library '");
                    sb.append(libraryName);
                    sb.append("':");
                    while (r0.hasNext()) {
                        sb.append("\n");
                        sb.append(t.getMessage());
                    }
                    res = new UnsatisfiedLinkError(sb.toString());
                    while (r0.hasNext()) {
                        addSuppressedReflected(res, t2);
                    }
                    throw res;
                }
            }
        }
        LOG.log(DEBUG_LOAD_LEVEL, "Found library '" + libraryName + "' at " + libraryPath);
        return new NativeLibrary(libraryName, libraryPath, handle, options);
    }

    private static void addSuppressedReflected(Throwable target, Throwable suppressed) {
        if (addSuppressedMethod == null) {
            return;
        }
        try {
            addSuppressedMethod.invoke(target, suppressed);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex);
        } catch (IllegalArgumentException ex2) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex2);
        } catch (InvocationTargetException ex3) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex3);
        }
    }

    static String[] matchFramework(String libraryName) {
        Set<String> paths = new LinkedHashSet<>();
        File framework = new File(libraryName);
        if (framework.isAbsolute()) {
            if (libraryName.contains(".framework")) {
                if (framework.exists()) {
                    return new String[]{framework.getAbsolutePath()};
                }
                paths.add(framework.getAbsolutePath());
            } else {
                File framework2 = new File(new File(framework.getParentFile(), framework.getName() + ".framework"), framework.getName());
                if (framework2.exists()) {
                    return new String[]{framework2.getAbsolutePath()};
                }
                paths.add(framework2.getAbsolutePath());
            }
        } else {
            String[] PREFIXES = {System.getProperty("user.home"), "", "/System"};
            String suffix = !libraryName.contains(".framework") ? libraryName + ".framework/" + libraryName : libraryName;
            for (String prefix : PREFIXES) {
                File framework3 = new File(prefix + "/Library/Frameworks/" + suffix);
                if (framework3.exists()) {
                    return new String[]{framework3.getAbsolutePath()};
                }
                paths.add(framework3.getAbsolutePath());
            }
        }
        return (String[]) paths.toArray(new String[0]);
    }

    private String getLibraryName(String libraryName) {
        String simplified = libraryName;
        String template = mapSharedLibraryName("---");
        int prefixEnd = template.indexOf("---");
        if (prefixEnd > 0 && simplified.startsWith(template.substring(0, prefixEnd))) {
            simplified = simplified.substring(prefixEnd);
        }
        String suffix = template.substring(prefixEnd + "---".length());
        int suffixStart = simplified.indexOf(suffix);
        if (suffixStart != -1) {
            simplified = simplified.substring(0, suffixStart);
        }
        return simplified;
    }

    public static final NativeLibrary getInstance(String libraryName) {
        return getInstance(libraryName, (Map<String, ?>) Collections.emptyMap());
    }

    public static final NativeLibrary getInstance(String libraryName, ClassLoader classLoader) {
        return getInstance(libraryName, (Map<String, ?>) Collections.singletonMap(Library.OPTION_CLASSLOADER, classLoader));
    }

    public static final NativeLibrary getInstance(String libraryName, Map<String, ?> libraryOptions) {
        NativeLibrary nativeLibrary;
        Map<String, Object> options = new HashMap<>(libraryOptions);
        if (options.get(Library.OPTION_CALLING_CONVENTION) == null) {
            options.put(Library.OPTION_CALLING_CONVENTION, 0);
        }
        if ((Platform.isLinux() || Platform.isFreeBSD() || Platform.isAIX()) && Platform.C_LIBRARY_NAME.equals(libraryName)) {
            libraryName = null;
        }
        synchronized (libraries) {
            Reference<NativeLibrary> ref = libraries.get(libraryName + options);
            NativeLibrary library = ref != null ? ref.get() : null;
            if (library == null) {
                if (libraryName == null) {
                    library = new NativeLibrary("<process>", null, Native.open(null, openFlags(options)), options);
                } else {
                    library = loadLibrary(libraryName, options);
                }
                Reference<NativeLibrary> ref2 = new WeakReference<>(library);
                libraries.put(library.getName() + options, ref2);
                File file = library.getFile();
                if (file != null) {
                    libraries.put(file.getAbsolutePath() + options, ref2);
                    libraries.put(file.getName() + options, ref2);
                }
            }
            nativeLibrary = library;
        }
        return nativeLibrary;
    }

    public static final synchronized NativeLibrary getProcess() {
        return getInstance(null);
    }

    public static final synchronized NativeLibrary getProcess(Map<String, ?> options) {
        return getInstance((String) null, options);
    }

    public static final void addSearchPath(String libraryName, String path) {
        List<String> customPaths = searchPaths.get(libraryName);
        if (customPaths == null) {
            customPaths = Collections.synchronizedList(new ArrayList());
            searchPaths.put(libraryName, customPaths);
        }
        customPaths.add(path);
    }

    public Function getFunction(String functionName) {
        return getFunction(functionName, this.callFlags);
    }

    Function getFunction(String name, Method method) {
        FunctionMapper mapper = (FunctionMapper) this.options.get(Library.OPTION_FUNCTION_MAPPER);
        if (mapper != null) {
            name = mapper.getFunctionName(this, method);
        }
        String prefix = System.getProperty("jna.profiler.prefix", "$$YJP$$");
        if (name.startsWith(prefix)) {
            name = name.substring(prefix.length());
        }
        int flags = this.callFlags;
        Class<?>[] etypes = method.getExceptionTypes();
        for (Class<?> cls : etypes) {
            if (LastErrorException.class.isAssignableFrom(cls)) {
                flags |= 64;
            }
        }
        return getFunction(name, flags);
    }

    public Function getFunction(String functionName, int callFlags) {
        return getFunction(functionName, callFlags, this.encoding);
    }

    public Function getFunction(String functionName, int callFlags, String encoding) {
        Function function;
        if (functionName == null) {
            throw new NullPointerException("Function name may not be null");
        }
        synchronized (this.functions) {
            String key = functionKey(functionName, callFlags, encoding);
            Function function2 = this.functions.get(key);
            if (function2 == null) {
                function2 = new Function(this, functionName, callFlags, encoding);
                this.functions.put(key, function2);
            }
            function = function2;
        }
        return function;
    }

    public Map<String, ?> getOptions() {
        return this.options;
    }

    public Pointer getGlobalVariableAddress(String symbolName) {
        try {
            return new Pointer(getSymbolAddress(symbolName));
        } catch (UnsatisfiedLinkError e) {
            throw new UnsatisfiedLinkError("Error looking up '" + symbolName + "': " + e.getMessage());
        }
    }

    long getSymbolAddress(String name) {
        if (this.handle == 0) {
            throw new UnsatisfiedLinkError("Library has been unloaded");
        }
        return this.symbolProvider.getSymbolAddress(this.handle, name, NATIVE_SYMBOL_PROVIDER);
    }

    public String toString() {
        return "Native Library <" + this.libraryPath + Registry.Key.DEFAULT_NAME + this.handle + ">";
    }

    public String getName() {
        return this.libraryName;
    }

    public File getFile() {
        if (this.libraryPath == null) {
            return null;
        }
        return new File(this.libraryPath);
    }

    static void disposeAll() {
        Set<Reference<NativeLibrary>> values;
        synchronized (libraries) {
            values = new LinkedHashSet<>(libraries.values());
        }
        for (Reference<NativeLibrary> ref : values) {
            NativeLibrary lib = ref.get();
            if (lib != null) {
                lib.close();
            }
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        Set<String> keys = new HashSet<>();
        synchronized (libraries) {
            for (Map.Entry<String, Reference<NativeLibrary>> e : libraries.entrySet()) {
                Reference<NativeLibrary> ref = e.getValue();
                if (ref.get() == this) {
                    keys.add(e.getKey());
                }
            }
            for (String k : keys) {
                libraries.remove(k);
            }
        }
        synchronized (this) {
            if (this.handle != 0) {
                this.cleanable.clean();
                this.handle = 0L;
            }
        }
    }

    @Deprecated
    public void dispose() {
        close();
    }

    private static List<String> initPaths(String key) {
        String value = System.getProperty(key, "");
        if ("".equals(value)) {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
        List<String> list = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            if (!"".equals(path)) {
                list.add(path);
            }
        }
        return list;
    }

    private static String findLibraryPath(String libName, Collection<String> searchPath) {
        if (new File(libName).isAbsolute()) {
            return libName;
        }
        String name = mapSharedLibraryName(libName);
        for (String path : searchPath) {
            File file = new File(path, name);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
            if (Platform.isMac() && name.endsWith(".dylib")) {
                File file2 = new File(path, name.substring(0, name.lastIndexOf(".dylib")) + ".jnilib");
                if (file2.exists()) {
                    return file2.getAbsolutePath();
                }
            }
        }
        return name;
    }

    static String mapSharedLibraryName(String libName) {
        if (Platform.isMac()) {
            if (libName.startsWith("lib") && (libName.endsWith(".dylib") || libName.endsWith(".jnilib"))) {
                return libName;
            }
            String name = System.mapLibraryName(libName);
            if (name.endsWith(".jnilib")) {
                return name.substring(0, name.lastIndexOf(".jnilib")) + ".dylib";
            }
            return name;
        }
        if (Platform.isLinux() || Platform.isFreeBSD()) {
            if (isVersionedName(libName) || libName.endsWith(".so")) {
                return libName;
            }
        } else if (Platform.isAIX()) {
            if (isVersionedName(libName) || libName.endsWith(".so") || libName.startsWith("lib") || libName.endsWith(".a")) {
                return libName;
            }
        } else if (Platform.isWindows() && (libName.endsWith(".drv") || libName.endsWith(".dll") || libName.endsWith(".ocx"))) {
            return libName;
        }
        String mappedName = System.mapLibraryName(libName);
        if (Platform.isAIX() && mappedName.endsWith(".so")) {
            return mappedName.replaceAll(".so$", ".a");
        }
        return mappedName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isVersionedName(String name) {
        int so;
        if (name.startsWith("lib") && (so = name.lastIndexOf(".so.")) != -1 && so + 4 < name.length()) {
            for (int i = so + 4; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (!Character.isDigit(ch) && ch != '.') {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static String matchLibrary(final String libName, Collection<String> searchPath) {
        File lib = new File(libName);
        if (lib.isAbsolute()) {
            searchPath = Arrays.asList(lib.getParent());
        }
        FilenameFilter filter = new FilenameFilter() { // from class: com.sun.jna.NativeLibrary.3
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String filename) {
                return (filename.startsWith(new StringBuilder().append("lib").append(libName).append(".so").toString()) || (filename.startsWith(new StringBuilder().append(libName).append(".so").toString()) && libName.startsWith("lib"))) && NativeLibrary.isVersionedName(filename);
            }
        };
        Collection<File> matches = new LinkedList<>();
        Iterator<String> it = searchPath.iterator();
        while (it.hasNext()) {
            File[] files = new File(it.next()).listFiles(filter);
            if (files != null && files.length > 0) {
                matches.addAll(Arrays.asList(files));
            }
        }
        double bestVersion = -1.0d;
        String bestMatch = null;
        for (File f : matches) {
            String path = f.getAbsolutePath();
            String ver = path.substring(path.lastIndexOf(".so.") + 4);
            double version = parseVersion(ver);
            if (version > bestVersion) {
                bestVersion = version;
                bestMatch = path;
            }
        }
        return bestMatch;
    }

    static double parseVersion(String ver) {
        String num;
        double v = 0.0d;
        double divisor = 1.0d;
        int dot = ver.indexOf(".");
        while (ver != null) {
            if (dot != -1) {
                num = ver.substring(0, dot);
                ver = ver.substring(dot + 1);
                dot = ver.indexOf(".");
            } else {
                num = ver;
                ver = null;
            }
            try {
                v += ((double) Integer.parseInt(num)) / divisor;
                divisor *= 100.0d;
            } catch (NumberFormatException e) {
                return 0.0d;
            }
        }
        return v;
    }

    private static String getMultiArchPath() {
        String str;
        String cpu = Platform.ARCH;
        if (Platform.iskFreeBSD()) {
            str = "-kfreebsd";
        } else {
            str = Platform.isGNU() ? "" : "-linux";
        }
        String kernel = str;
        String libc = "-gnu";
        if (Platform.isIntel()) {
            cpu = Platform.is64Bit() ? "x86_64" : "i386";
        } else if (Platform.isPPC()) {
            cpu = Platform.is64Bit() ? "powerpc64" : "powerpc";
        } else if (Platform.isARM()) {
            cpu = "arm";
            libc = "-gnueabi";
        } else if (Platform.ARCH.equals("mips64el")) {
            libc = "-gnuabi64";
        }
        return cpu + kernel + libc;
    }

    private static ArrayList<String> getLinuxLdPaths() {
        ArrayList<String> ldPaths = new ArrayList<>();
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("/sbin/ldconfig -p");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                String buffer = reader.readLine();
                if (buffer == null) {
                    break;
                }
                int startPath = buffer.indexOf(" => ");
                int endPath = buffer.lastIndexOf(47);
                if (startPath != -1 && endPath != -1 && startPath < endPath) {
                    String path = buffer.substring(startPath + 4, endPath);
                    if (ldPaths.contains(path)) {
                        continue;
                    } else {
                        ldPaths.add(path);
                    }
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (process != null) {
                try {
                    process.waitFor();
                } catch (InterruptedException e2) {
                }
            }
        } catch (Exception e3) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                }
            }
            if (process != null) {
                try {
                    process.waitFor();
                } catch (InterruptedException e5) {
                }
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e6) {
                }
            }
            if (process != null) {
                try {
                    process.waitFor();
                } catch (InterruptedException e7) {
                }
            }
            throw th;
        }
        return ldPaths;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/NativeLibrary$NativeLibraryDisposer.class */
    private static final class NativeLibraryDisposer implements Runnable {
        private long handle;

        public NativeLibraryDisposer(long handle) {
            this.handle = handle;
        }

        @Override // java.lang.Runnable
        public synchronized void run() {
            if (this.handle != 0) {
                try {
                    Native.close(this.handle);
                } finally {
                    this.handle = 0L;
                }
            }
        }
    }
}
