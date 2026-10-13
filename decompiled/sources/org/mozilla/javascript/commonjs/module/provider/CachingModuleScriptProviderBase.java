package org.mozilla.javascript.commonjs.module.provider;

import java.io.Serializable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/CachingModuleScriptProviderBase.class */
public abstract class CachingModuleScriptProviderBase implements ModuleScriptProvider, Serializable {
    private static final long serialVersionUID = -1;
    private static final int loadConcurrencyLevel = Runtime.getRuntime().availableProcessors() * 8;
    private static final int loadLockShift;
    private static final int loadLockMask;
    private static final int loadLockCount;
    private final Object[] loadLocks = new Object[loadLockCount];
    private final ModuleSourceProvider moduleSourceProvider;

    protected abstract void putLoadedModule(String str, ModuleScript moduleScript, Object obj);

    protected abstract CachedModuleScript getLoadedModule(String str);

    static {
        int sshift = 0;
        int i = 1;
        while (true) {
            int ssize = i;
            if (ssize < loadConcurrencyLevel) {
                sshift++;
                i = ssize << 1;
            } else {
                loadLockShift = 32 - sshift;
                loadLockMask = ssize - 1;
                loadLockCount = ssize;
                return;
            }
        }
    }

    protected CachingModuleScriptProviderBase(ModuleSourceProvider moduleSourceProvider) {
        for (int i = 0; i < this.loadLocks.length; i++) {
            this.loadLocks[i] = new Object();
        }
        this.moduleSourceProvider = moduleSourceProvider;
    }

    /*  JADX ERROR: NullPointerException in pass: AttachTryCatchVisitor
        java.lang.NullPointerException: Cannot invoke "String.charAt(int)" because "obj" is null
        	at jadx.core.utils.Utils.cleanObjectName(Utils.java:44)
        	at jadx.core.dex.instructions.args.ArgType.object(ArgType.java:94)
        	at jadx.core.dex.info.ClassInfo.fromName(ClassInfo.java:43)
        	at jadx.core.dex.visitors.AttachTryCatchVisitor.convertToHandlers(AttachTryCatchVisitor.java:113)
        	at jadx.core.dex.visitors.AttachTryCatchVisitor.initTryCatches(AttachTryCatchVisitor.java:54)
        	at jadx.core.dex.visitors.AttachTryCatchVisitor.visit(AttachTryCatchVisitor.java:42)
        */
    @Override // org.mozilla.javascript.commonjs.module.ModuleScriptProvider
    public org.mozilla.javascript.commonjs.module.ModuleScript getModuleScript(org.mozilla.javascript.Context r9, java.lang.String r10, java.net.URI r11, java.net.URI r12, org.mozilla.javascript.Scriptable r13) throws java.lang.Exception {
        /*
            Method dump skipped, instruction units count: 334
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase.getModuleScript(org.mozilla.javascript.Context, java.lang.String, java.net.URI, java.net.URI, org.mozilla.javascript.Scriptable):org.mozilla.javascript.commonjs.module.ModuleScript");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/CachingModuleScriptProviderBase$CachedModuleScript.class */
    public static class CachedModuleScript {
        private final ModuleScript moduleScript;
        private final Object validator;

        public CachedModuleScript(ModuleScript moduleScript, Object validator) {
            this.moduleScript = moduleScript;
            this.validator = validator;
        }

        ModuleScript getModule() {
            return this.moduleScript;
        }

        Object getValidator() {
            return this.validator;
        }
    }

    private static Object getValidator(CachedModuleScript cachedModule) {
        if (cachedModule == null) {
            return null;
        }
        return cachedModule.getValidator();
    }

    private static boolean equal(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    protected static int getConcurrencyLevel() {
        return loadLockCount;
    }
}
