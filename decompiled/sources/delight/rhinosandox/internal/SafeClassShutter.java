package delight.rhinosandox.internal;

import java.util.HashSet;
import java.util.Set;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.EcmaError;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/SafeClassShutter.class */
public class SafeClassShutter implements ClassShutter {
    public final Set<String> allowedClasses;

    @Override // org.mozilla.javascript.ClassShutter
    public boolean visibleToScripts(String fullClassName) {
        boolean _startsWith = fullClassName.startsWith("adapter");
        if (_startsWith) {
            return true;
        }
        return this.allowedClasses.contains(fullClassName);
    }

    public SafeClassShutter() {
        HashSet<String> _hashSet = new HashSet<>();
        this.allowedClasses = _hashSet;
        this.allowedClasses.add(EcmaError.class.getName());
    }
}
