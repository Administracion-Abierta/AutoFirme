package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/FunctionParameterContext.class */
public class FunctionParameterContext extends ToNativeContext {
    private Function function;
    private Object[] args;
    private int index;

    FunctionParameterContext(Function f, Object[] args, int index) {
        this.function = f;
        this.args = args;
        this.index = index;
    }

    public Function getFunction() {
        return this.function;
    }

    public Object[] getParameters() {
        return this.args;
    }

    public int getParameterIndex() {
        return this.index;
    }
}
