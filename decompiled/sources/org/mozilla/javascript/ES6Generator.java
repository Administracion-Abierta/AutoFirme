package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ES6Generator.class */
public final class ES6Generator extends IdScriptableObject {
    private static final long serialVersionUID = 1645892441041347273L;
    private static final Object GENERATOR_TAG = "Generator";
    private static final int Id_next = 1;
    private static final int Id_return = 2;
    private static final int Id_throw = 3;
    private static final int SymbolId_iterator = 4;
    private static final int MAX_PROTOTYPE_ID = 4;
    private NativeFunction function;
    private Object savedState;
    private String lineSource;
    private int lineNumber;
    private State state = State.SUSPENDED_START;
    private Object delegee;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ES6Generator$State.class */
    enum State {
        SUSPENDED_START,
        SUSPENDED_YIELD,
        EXECUTING,
        COMPLETED
    }

    static ES6Generator init(ScriptableObject scope, boolean sealed) {
        ES6Generator prototype = new ES6Generator();
        if (scope != null) {
            prototype.setParentScope(scope);
            prototype.setPrototype(getObjectPrototype(scope));
        }
        prototype.activatePrototypeMap(4);
        if (sealed) {
            prototype.sealObject();
        }
        if (scope != null) {
            scope.associateValue(GENERATOR_TAG, prototype);
        }
        return prototype;
    }

    private ES6Generator() {
    }

    public ES6Generator(Scriptable scope, NativeFunction function, Object savedState) {
        this.function = function;
        this.savedState = savedState;
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        setParentScope(top);
        ES6Generator prototype = (ES6Generator) ScriptableObject.getTopScopeValue(top, GENERATOR_TAG);
        setPrototype(prototype);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Generator";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        if (id == 4) {
            initPrototypeMethod(GENERATOR_TAG, id, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        switch (id) {
            case 1:
                arity = 1;
                s = ES6Iterator.NEXT_METHOD;
                break;
            case 2:
                arity = 1;
                s = "return";
                break;
            case 3:
                arity = 1;
                s = "throw";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(GENERATOR_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(GENERATOR_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (!(thisObj instanceof ES6Generator)) {
            throw incompatibleCallError(f);
        }
        ES6Generator generator = (ES6Generator) thisObj;
        Object value = args.length >= 1 ? args[0] : Undefined.instance;
        switch (id) {
            case 1:
                if (generator.delegee == null) {
                    return generator.resumeLocal(cx, scope, value);
                }
                return generator.resumeDelegee(cx, scope, value);
            case 2:
                if (generator.delegee == null) {
                    return generator.resumeAbruptLocal(cx, scope, 2, value);
                }
                return generator.resumeDelegeeReturn(cx, scope, value);
            case 3:
                if (generator.delegee == null) {
                    return generator.resumeAbruptLocal(cx, scope, 1, value);
                }
                return generator.resumeDelegeeThrow(cx, scope, value);
            case 4:
                return thisObj;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private Scriptable resumeDelegee(Context cx, Scriptable scope, Object value) {
        try {
            Object[] nextArgs = Undefined.instance.equals(value) ? ScriptRuntime.emptyArgs : new Object[]{value};
            Callable nextFn = ScriptRuntime.getPropFunctionAndThis(this.delegee, ES6Iterator.NEXT_METHOD, cx, scope);
            Scriptable nextThis = ScriptRuntime.lastStoredScriptable(cx);
            Object nr = nextFn.call(cx, scope, nextThis, nextArgs);
            Scriptable nextResult = ScriptableObject.ensureScriptable(nr);
            if (ScriptRuntime.isIteratorDone(cx, nextResult)) {
                this.delegee = null;
                return resumeLocal(cx, scope, ScriptableObject.getProperty(nextResult, ES6Iterator.VALUE_PROPERTY));
            }
            return nextResult;
        } catch (RhinoException re) {
            this.delegee = null;
            return resumeAbruptLocal(cx, scope, 1, re);
        }
    }

    private Scriptable resumeDelegeeThrow(Context cx, Scriptable scope, Object value) {
        boolean returnCalled = false;
        try {
            Callable throwFn = ScriptRuntime.getPropFunctionAndThis(this.delegee, "throw", cx, scope);
            Scriptable nextThis = ScriptRuntime.lastStoredScriptable(cx);
            Object throwResult = throwFn.call(cx, scope, nextThis, new Object[]{value});
            if (ScriptRuntime.isIteratorDone(cx, throwResult)) {
                try {
                    returnCalled = true;
                    callReturnOptionally(cx, scope, Undefined.instance);
                    return resumeLocal(cx, scope, ScriptRuntime.getObjectProp(throwResult, ES6Iterator.VALUE_PROPERTY, cx, scope));
                } finally {
                    this.delegee = null;
                }
            }
            return ensureScriptable(throwResult);
        } catch (RhinoException re) {
            try {
                if (!returnCalled) {
                    callReturnOptionally(cx, scope, Undefined.instance);
                }
                return resumeAbruptLocal(cx, scope, 1, re);
            } catch (RhinoException re2) {
                return resumeAbruptLocal(cx, scope, 1, re2);
            } finally {
                this.delegee = null;
            }
        }
    }

    private Scriptable resumeDelegeeReturn(Context cx, Scriptable scope, Object value) {
        try {
            Object retResult = callReturnOptionally(cx, scope, value);
            if (retResult != null) {
                if (ScriptRuntime.isIteratorDone(cx, retResult)) {
                    this.delegee = null;
                    return resumeAbruptLocal(cx, scope, 2, ScriptRuntime.getObjectPropNoWarn(retResult, ES6Iterator.VALUE_PROPERTY, cx, scope));
                }
                return ensureScriptable(retResult);
            }
            this.delegee = null;
            return resumeAbruptLocal(cx, scope, 2, value);
        } catch (RhinoException re) {
            this.delegee = null;
            return resumeAbruptLocal(cx, scope, 1, re);
        }
    }

    private Scriptable resumeLocal(Context cx, Scriptable scope, Object value) {
        if (this.state == State.COMPLETED) {
            return ES6Iterator.makeIteratorResult(cx, scope, Boolean.TRUE);
        }
        if (this.state == State.EXECUTING) {
            throw ScriptRuntime.typeError0("msg.generator.executing");
        }
        Scriptable result = ES6Iterator.makeIteratorResult(cx, scope, Boolean.FALSE);
        this.state = State.EXECUTING;
        try {
            try {
                Object r = this.function.resumeGenerator(cx, scope, 0, this.savedState, value);
                if (!(r instanceof YieldStarResult)) {
                    ScriptableObject.putProperty(result, ES6Iterator.VALUE_PROPERTY, r);
                    if (this.state == State.COMPLETED) {
                        ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                    } else {
                        this.state = State.SUSPENDED_YIELD;
                    }
                    return result;
                }
                this.state = State.SUSPENDED_YIELD;
                YieldStarResult ysResult = (YieldStarResult) r;
                try {
                    this.delegee = ScriptRuntime.callIterator(ysResult.getResult(), cx, scope);
                    try {
                        Scriptable delResult = resumeDelegee(cx, scope, Undefined.instance);
                        this.state = State.EXECUTING;
                        if (ScriptRuntime.isIteratorDone(cx, delResult)) {
                            this.state = State.COMPLETED;
                        }
                        if (this.state == State.COMPLETED) {
                            ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                        } else {
                            this.state = State.SUSPENDED_YIELD;
                        }
                        return delResult;
                    } catch (Throwable th) {
                        this.state = State.EXECUTING;
                        throw th;
                    }
                } catch (RhinoException re) {
                    Scriptable scriptableResumeAbruptLocal = resumeAbruptLocal(cx, scope, 1, re);
                    if (this.state == State.COMPLETED) {
                        ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                    } else {
                        this.state = State.SUSPENDED_YIELD;
                    }
                    return scriptableResumeAbruptLocal;
                }
            } catch (Throwable th2) {
                if (this.state == State.COMPLETED) {
                    ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                } else {
                    this.state = State.SUSPENDED_YIELD;
                }
                throw th2;
            }
        } catch (JavaScriptException jse) {
            this.state = State.COMPLETED;
            if (!(jse.getValue() instanceof NativeIterator.StopIteration)) {
                this.lineNumber = jse.lineNumber();
                this.lineSource = jse.lineSource();
                if (jse.getValue() instanceof RhinoException) {
                    throw ((RhinoException) jse.getValue());
                }
                throw jse;
            }
            ScriptableObject.putProperty(result, ES6Iterator.VALUE_PROPERTY, ((NativeIterator.StopIteration) jse.getValue()).getValue());
            if (this.state == State.COMPLETED) {
                ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
            } else {
                this.state = State.SUSPENDED_YIELD;
            }
        } catch (NativeGenerator.GeneratorClosedException e) {
            this.state = State.COMPLETED;
            if (this.state == State.COMPLETED) {
                ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
            } else {
                this.state = State.SUSPENDED_YIELD;
            }
        } catch (RhinoException re2) {
            this.lineNumber = re2.lineNumber();
            this.lineSource = re2.lineSource();
            throw re2;
        }
    }

    private Scriptable resumeAbruptLocal(Context cx, Scriptable scope, int op, Object value) {
        if (this.state == State.EXECUTING) {
            throw ScriptRuntime.typeError0("msg.generator.executing");
        }
        if (this.state == State.SUSPENDED_START) {
            this.state = State.COMPLETED;
        }
        Scriptable result = ES6Iterator.makeIteratorResult(cx, scope, Boolean.FALSE);
        if (this.state == State.COMPLETED) {
            if (op == 1) {
                throw new JavaScriptException(value, this.lineSource, this.lineNumber);
            }
            ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
            return result;
        }
        this.state = State.EXECUTING;
        Object throwValue = value;
        if (op == 2) {
            if (!(value instanceof NativeGenerator.GeneratorClosedException)) {
                throwValue = new NativeGenerator.GeneratorClosedException();
            }
        } else if (value instanceof JavaScriptException) {
            throwValue = ((JavaScriptException) value).getValue();
        } else if (value instanceof RhinoException) {
            throwValue = ScriptRuntime.wrapException((Throwable) value, scope, cx);
        }
        try {
            try {
                try {
                    Object r = this.function.resumeGenerator(cx, scope, op, this.savedState, throwValue);
                    ScriptableObject.putProperty(result, ES6Iterator.VALUE_PROPERTY, r);
                    this.state = State.SUSPENDED_YIELD;
                    if (this.state == State.COMPLETED) {
                        this.delegee = null;
                        ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                    }
                } catch (NativeGenerator.GeneratorClosedException e) {
                    this.state = State.COMPLETED;
                    if (this.state == State.COMPLETED) {
                        this.delegee = null;
                        ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                    }
                }
            } catch (JavaScriptException jse) {
                this.state = State.COMPLETED;
                if (!(jse.getValue() instanceof NativeIterator.StopIteration)) {
                    this.lineNumber = jse.lineNumber();
                    this.lineSource = jse.lineSource();
                    if (jse.getValue() instanceof RhinoException) {
                        throw ((RhinoException) jse.getValue());
                    }
                    throw jse;
                }
                ScriptableObject.putProperty(result, ES6Iterator.VALUE_PROPERTY, ((NativeIterator.StopIteration) jse.getValue()).getValue());
                if (this.state == State.COMPLETED) {
                    this.delegee = null;
                    ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
                }
            } catch (RhinoException re) {
                this.state = State.COMPLETED;
                this.lineNumber = re.lineNumber();
                this.lineSource = re.lineSource();
                throw re;
            }
            return result;
        } catch (Throwable th) {
            if (this.state == State.COMPLETED) {
                this.delegee = null;
                ScriptableObject.putProperty(result, ES6Iterator.DONE_PROPERTY, Boolean.TRUE);
            }
            throw th;
        }
    }

    private Object callReturnOptionally(Context cx, Scriptable scope, Object value) {
        Object[] retArgs = Undefined.instance.equals(value) ? ScriptRuntime.emptyArgs : new Object[]{value};
        Object retFnObj = ScriptRuntime.getObjectPropNoWarn(this.delegee, "return", cx, scope);
        if (!Undefined.instance.equals(retFnObj)) {
            if (!(retFnObj instanceof Callable)) {
                throw ScriptRuntime.typeError2("msg.isnt.function", "return", ScriptRuntime.typeof(retFnObj));
            }
            return ((Callable) retFnObj).call(cx, scope, ensureScriptable(this.delegee), retArgs);
        }
        return null;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 4;
        }
        return 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 4) {
            X = ES6Iterator.NEXT_METHOD;
            id = 1;
        } else if (s_length == 5) {
            X = "throw";
            id = 3;
        } else if (s_length == 6) {
            X = "return";
            id = 2;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ES6Generator$YieldStarResult.class */
    public static final class YieldStarResult {
        private Object result;

        public YieldStarResult(Object result) {
            this.result = result;
        }

        Object getResult() {
            return this.result;
        }
    }
}
