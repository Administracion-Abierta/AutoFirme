package org.apache.oro.text;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.util.Cache;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/GenericPatternCache.class */
public abstract class GenericPatternCache implements PatternCache {
    PatternCompiler _compiler;
    Cache _cache;
    public static final int DEFAULT_CAPACITY = 20;

    GenericPatternCache(Cache cache, PatternCompiler compiler) {
        this._cache = cache;
        this._compiler = compiler;
    }

    @Override // org.apache.oro.text.PatternCache
    public final synchronized Pattern addPattern(String expression, int options) throws MalformedPatternException {
        Object obj = this._cache.getElement(expression);
        if (obj != null) {
            Pattern pattern = (Pattern) obj;
            if (pattern.getOptions() == options) {
                return pattern;
            }
        }
        Pattern pattern2 = this._compiler.compile(expression, options);
        this._cache.addElement(expression, pattern2);
        return pattern2;
    }

    final synchronized Pattern addPattern(String expression) throws MalformedPatternException {
        return addPattern(expression, 0);
    }

    @Override // org.apache.oro.text.PatternCache
    public final synchronized Pattern getPattern(String expression, int options) throws MalformedCachePatternException {
        try {
            Pattern result = addPattern(expression, options);
            return result;
        } catch (MalformedPatternException e) {
            throw new MalformedCachePatternException("Invalid expression: " + expression + "\n" + e.getMessage());
        }
    }

    @Override // org.apache.oro.text.PatternCache
    public final synchronized Pattern getPattern(String expression) throws MalformedCachePatternException {
        return getPattern(expression, 0);
    }

    public final int size() {
        return this._cache.size();
    }

    @Override // org.apache.oro.text.PatternCache
    public final int capacity() {
        return this._cache.capacity();
    }
}
