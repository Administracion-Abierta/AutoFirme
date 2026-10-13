package com.github.markusbernhardt.proxy.selector.pac;

import com.github.markusbernhardt.proxy.util.Logger;
import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;
import java.lang.reflect.Method;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/JavaxPacScriptParser.class */
public class JavaxPacScriptParser implements PacScriptParser {
    static final String SCRIPT_METHODS_OBJECT = "__pacutil";
    static final String SOURCE_NAME = JavaxPacScriptParser.class.getName();
    private final PacScriptSource source;
    private final RhinoSandbox engine = setupEngine();

    public JavaxPacScriptParser(PacScriptSource source) throws ProxyEvaluationException {
        this.source = source;
    }

    private RhinoSandbox setupEngine() throws ProxyEvaluationException {
        RhinoSandbox rhinoSandbox = RhinoSandboxes.create();
        rhinoSandbox.inject(SCRIPT_METHODS_OBJECT, new PacScriptMethods());
        rhinoSandbox.allow(String.class);
        Method[] scriptMethods = ScriptMethods.class.getMethods();
        for (Method method : scriptMethods) {
            String name = method.getName();
            int args = method.getParameterTypes().length;
            StringBuilder toEval = new StringBuilder(name).append(" = function(");
            for (int i = 0; i < args; i++) {
                if (i > 0) {
                    toEval.append(",");
                }
                toEval.append("arg").append(i);
            }
            toEval.append(") {return ");
            String functionCall = buildFunctionCallCode(name, args);
            if (String.class.isAssignableFrom(method.getReturnType())) {
                functionCall = "String(" + functionCall + ")";
            }
            toEval.append(functionCall).append("; }");
            try {
                rhinoSandbox.evalWithGlobalScope(SOURCE_NAME, toEval.toString());
            } catch (Exception e) {
                Logger.log(getClass(), Logger.LogLevel.ERROR, "JS evaluation error when creating alias for " + name + ".", e);
                throw new ProxyEvaluationException("Error setting up script engine", e);
            }
        }
        return rhinoSandbox;
    }

    private String buildFunctionCallCode(String functionName, int args) {
        StringBuilder functionCall = new StringBuilder();
        functionCall.append(SCRIPT_METHODS_OBJECT).append(".").append(functionName).append("(");
        for (int i = 0; i < args; i++) {
            if (i > 0) {
                functionCall.append(",");
            }
            functionCall.append("arg").append(i);
        }
        functionCall.append(")");
        return functionCall.toString();
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.PacScriptParser
    public PacScriptSource getScriptSource() {
        return this.source;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.PacScriptParser
    public String evaluate(String url, String host) throws ProxyEvaluationException {
        try {
            StringBuilder script = new StringBuilder(this.source.getScriptContent());
            String evalMethod = " ;FindProxyForURL (\"" + url + "\",\"" + host + "\")";
            script.append(evalMethod);
            Object result = this.engine.eval(SOURCE_NAME, script.toString());
            if (Undefined.isUndefined(result)) {
                return null;
            }
            return (String) result;
        } catch (Exception e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "JS evaluation error.", e);
            throw new ProxyEvaluationException("Error while executing PAC script: " + e.getMessage(), e);
        }
    }
}
