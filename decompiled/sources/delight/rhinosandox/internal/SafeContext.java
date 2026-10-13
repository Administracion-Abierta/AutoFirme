package delight.rhinosandox.internal;

import delight.rhinosandox.exceptions.ScriptCPUAbuseException;
import delight.rhinosandox.exceptions.ScriptDurationException;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/SafeContext.class */
public class SafeContext extends ContextFactory {
    private static final int INSTRUCTION_STEPS = 10000;
    public long maxRuntimeInMs;
    public int maxInstructions;
    public SafeClassShutter classShutter;

    /* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/SafeContext$CountContext.class */
    public static class CountContext extends Context {
        private long startTime;
        private long instructions;

        CountContext(ContextFactory contextFactory) {
            super(contextFactory);
        }
    }

    @Override // org.mozilla.javascript.ContextFactory
    public Context makeContext() {
        CountContext cx = new CountContext(this);
        cx.setOptimizationLevel(-1);
        cx.setInstructionObserverThreshold(10000);
        if (this.classShutter != null) {
            cx.setClassShutter(this.classShutter);
        }
        return cx;
    }

    @Override // org.mozilla.javascript.ContextFactory
    public boolean hasFeature(Context cx, int featureIndex) {
        switch (featureIndex) {
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
            default:
                return super.hasFeature(cx, featureIndex);
            case 5:
                return false;
        }
    }

    @Override // org.mozilla.javascript.ContextFactory
    public void observeInstructionCount(Context cx, int instructionCount) {
        CountContext mcx = (CountContext) cx;
        long currentTime = System.currentTimeMillis();
        if (this.maxRuntimeInMs <= 0 || currentTime - mcx.startTime <= this.maxRuntimeInMs) {
            mcx.instructions += 10000;
            if (this.maxInstructions > 0 && mcx.instructions > this.maxInstructions) {
                throw new ScriptCPUAbuseException();
            }
            return;
        }
        throw new ScriptDurationException();
    }

    @Override // org.mozilla.javascript.ContextFactory
    public Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        CountContext mcx = (CountContext) cx;
        mcx.startTime = System.currentTimeMillis();
        mcx.instructions = 0L;
        return super.doTopCall(callable, cx, scope, thisObj, args);
    }
}
