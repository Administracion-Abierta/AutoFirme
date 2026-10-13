package delight.rhinosandox.internal;

import delight.rhinosandox.RhinoSandbox;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/RhinoSandboxImpl.class */
public class RhinoSandboxImpl implements RhinoSandbox {
    private SafeContext contextFactory;
    private ScriptableObject globalScope;
    private ScriptableObject safeScope;
    private int instructionLimit;
    private long maxDuration;
    private boolean useSafeStandardObjects;
    private boolean sealScope;
    private final Map<String, Object> inScope;
    private SafeClassShutter classShutter;
    private static final Object ctxFactoryLock = new Object();

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.Throwable] */
    public void assertContextFactory() {
        if (this.contextFactory != null) {
            return;
        }
        SafeContext _safeContext = new SafeContext();
        this.contextFactory = _safeContext;
        ?? r0 = ctxFactoryLock;
        synchronized (r0) {
            try {
                boolean _hasExplicitGlobal = ContextFactory.hasExplicitGlobal();
                boolean _not = !_hasExplicitGlobal;
                if (_not) {
                    ContextFactory.initGlobal(this.contextFactory);
                }
                r0 = r0;
                this.contextFactory.maxInstructions = this.instructionLimit;
                this.contextFactory.maxRuntimeInMs = this.maxDuration;
                try {
                    Context context = this.contextFactory.enterContext();
                    this.globalScope = context.initStandardObjects(null, false);
                    Set<Map.Entry<String, Object>> _entrySet = this.inScope.entrySet();
                    for (Map.Entry<String, Object> entry : _entrySet) {
                        this.globalScope.put(entry.getKey(), this.globalScope, Context.toObject(entry.getValue(), this.globalScope));
                    }
                    Class[] parameters = {String.class};
                    try {
                        Method dealMethod = RhinoEvalDummy.class.getMethod("eval", parameters);
                        RhinoEval _rhinoEval = new RhinoEval("eval", dealMethod, this.globalScope);
                        this.globalScope.defineProperty("eval", _rhinoEval, 2);
                        Context.exit();
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    } catch (SecurityException e2) {
                        throw new RuntimeException(e2);
                    }
                } catch (Throwable th) {
                    Context.exit();
                    throw th;
                }
            } catch (Throwable th2) {
                throw r0;
            }
        }
    }

    public void assertSafeScope(Context context) {
        if (this.safeScope != null) {
            context.setClassShutter(this.classShutter);
            return;
        }
        if (this.useSafeStandardObjects) {
            this.safeScope = context.initSafeStandardObjects(this.globalScope, true);
            return;
        }
        context.setClassShutter(this.classShutter);
        SafeWrapFactory _safeWrapFactory = new SafeWrapFactory();
        context.setWrapFactory(_safeWrapFactory);
        this.safeScope = this.globalScope;
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object evalWithGlobalScope(String sourceName, String js) {
        assertContextFactory();
        try {
            Context context = this.contextFactory.enterContext();
            return context.evaluateString(this.globalScope, js, sourceName, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object evalWithGlobalScope(String sourceName, Reader js) throws IOException {
        assertContextFactory();
        try {
            Context context = this.contextFactory.enterContext();
            return context.evaluateReader(this.globalScope, js, sourceName, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object eval(String sourceName, String js, Map<String, Object> variables) {
        assertContextFactory();
        try {
            Context context = this.contextFactory.enterContext();
            assertSafeScope(context);
            if (this.sealScope) {
                this.globalScope.sealObject();
            }
            Scriptable instanceScope = context.newObject(this.safeScope);
            instanceScope.setPrototype(this.safeScope);
            instanceScope.setParentScope(null);
            Set<Map.Entry<String, Object>> _entrySet = variables.entrySet();
            for (Map.Entry<String, Object> entry : _entrySet) {
                allow(entry.getValue().getClass());
                instanceScope.put(entry.getKey(), instanceScope, Context.toObject(entry.getValue(), instanceScope));
            }
            return context.evaluateString(instanceScope, js, sourceName, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object eval(String sourceName, Reader js, Map<String, Object> variables) throws IOException {
        assertContextFactory();
        try {
            Context context = this.contextFactory.enterContext();
            assertSafeScope(context);
            if (this.sealScope) {
                this.globalScope.sealObject();
            }
            Scriptable instanceScope = context.newObject(this.safeScope);
            instanceScope.setPrototype(this.safeScope);
            instanceScope.setParentScope(null);
            Set<Map.Entry<String, Object>> _entrySet = variables.entrySet();
            for (Map.Entry<String, Object> entry : _entrySet) {
                allow(entry.getValue().getClass());
                instanceScope.put(entry.getKey(), instanceScope, Context.toObject(entry.getValue(), instanceScope));
            }
            return context.evaluateReader(instanceScope, js, sourceName, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object eval(String sourceName, String js) {
        HashMap<String, Object> _hashMap = new HashMap<>();
        return eval(sourceName, js, _hashMap);
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object callFunction(NativeFunction function, Object[] args) {
        assertContextFactory();
        try {
            Context context = this.contextFactory.enterContext();
            assertSafeScope(context);
            if (this.sealScope) {
                this.globalScope.sealObject();
            }
            Scriptable instanceScope = context.newObject(this.safeScope);
            instanceScope.setPrototype(this.safeScope);
            instanceScope.setParentScope(null);
            for (Object argument : args) {
                allow(argument.getClass());
            }
            return function.call(context, instanceScope, instanceScope, args);
        } finally {
            Context.exit();
        }
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public Object eval(String sourceName, Reader js) throws IOException {
        HashMap<String, Object> _hashMap = new HashMap<>();
        return eval(sourceName, js, _hashMap);
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox setInstructionLimit(int limit) {
        this.instructionLimit = limit;
        if (this.contextFactory != null) {
            this.contextFactory.maxInstructions = this.instructionLimit;
        }
        return this;
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox setMaxDuration(int limitInMs) {
        this.maxDuration = limitInMs;
        if (this.contextFactory != null) {
            this.contextFactory.maxRuntimeInMs = this.maxDuration;
        }
        return this;
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox setUseSafeStandardObjects(boolean useSafeStandardObjects) {
        this.useSafeStandardObjects = useSafeStandardObjects;
        return this;
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox allow(Class<?> clazz) {
        this.classShutter.allowedClasses.add(clazz.getName());
        return this;
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox inject(Class<ScriptableObject> clazz) {
        try {
            ScriptableObject.defineClass(this.globalScope, clazz);
            allow(clazz);
            return this;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e2) {
            throw new RuntimeException(e2);
        } catch (InvocationTargetException e3) {
            throw new RuntimeException(e3);
        }
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox inject(String variableName, Object object) {
        injectInt(variableName, object);
        allow(object.getClass());
        return this;
    }

    private void injectInt(String variableName, Object object) {
        boolean _containsKey = this.inScope.containsKey(variableName);
        if (_containsKey) {
            throw new IllegalArgumentException("A variable with the name [" + variableName + "] has already been defined.");
        }
        if (this.contextFactory == null) {
            this.inScope.put(variableName, object);
            return;
        }
        try {
            this.contextFactory.enterContext();
            this.globalScope.put(variableName, this.globalScope, Context.toObject(object, this.globalScope));
        } finally {
            Context.exit();
        }
    }

    public RhinoSandboxImpl() {
        HashMap<String, Object> _hashMap = new HashMap<>();
        this.inScope = _hashMap;
        this.useSafeStandardObjects = false;
        this.sealScope = true;
        SafeClassShutter _safeClassShutter = new SafeClassShutter();
        this.classShutter = _safeClassShutter;
    }

    @Override // delight.rhinosandox.RhinoSandbox
    public RhinoSandbox setUseSealedScope(boolean value) {
        this.sealScope = value;
        return this;
    }
}
