package delight.rhinosandox;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ScriptableObject;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/RhinoSandbox.class */
public interface RhinoSandbox {
    RhinoSandbox allow(Class<?> cls);

    RhinoSandbox inject(String str, Object obj);

    RhinoSandbox inject(Class<ScriptableObject> cls);

    RhinoSandbox setInstructionLimit(int i);

    RhinoSandbox setMaxDuration(int i);

    RhinoSandbox setUseSafeStandardObjects(boolean z);

    RhinoSandbox setUseSealedScope(boolean z);

    Object evalWithGlobalScope(String str, String str2);

    Object evalWithGlobalScope(String str, Reader reader) throws IOException;

    Object eval(String str, String str2);

    Object callFunction(NativeFunction nativeFunction, Object[] objArr);

    Object eval(String str, Reader reader) throws IOException;

    Object eval(String str, String str2, Map<String, Object> map);

    Object eval(String str, Reader reader, Map<String, Object> map) throws IOException;
}
