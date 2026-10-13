package org.apache.oro.text.perl;

import org.apache.oro.text.PatternCache;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.util.Cache;
import org.apache.oro.util.CacheLRU;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/perl/Perl5Util.class */
public final class Perl5Util implements MatchResult {
    private static final String __matchExpression = "m?(\\W)(.*)\\1([imsx]*)";
    private final PatternCache __patternCache;
    private final Cache __expressionCache;
    private final Perl5Matcher __matcher;
    private Pattern __matchPattern;
    private MatchResult __lastMatch;

    Perl5Util(PatternCache cache) {
        this.__matcher = new Perl5Matcher();
        this.__patternCache = cache;
        this.__expressionCache = new CacheLRU(cache.capacity());
        __compilePatterns();
    }

    public Perl5Util() {
        this(new PatternCacheLRU());
    }

    private void __compilePatterns() {
        Perl5Compiler compiler = new Perl5Compiler();
        try {
            this.__matchPattern = compiler.compile(__matchExpression, 16);
        } catch (MalformedPatternException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Pattern __parseMatchExpression(String pat) throws MalformedPerl5PatternException {
        if (pat == null) {
            throw new IllegalArgumentException("La expresion regular no puede ser nula");
        }
        String pattern = pat.trim();
        Object obj = this.__expressionCache.getElement(pattern);
        if (obj != null) {
            try {
                return (Pattern) obj;
            } catch (ClassCastException e) {
            }
        }
        if (!this.__matcher.matches(pattern, this.__matchPattern)) {
            throw new MalformedPerl5PatternException("Invalid expression: " + pattern);
        }
        MatchResult result = this.__matcher.getMatch();
        String regex = result.group(2);
        int compileOptions = 0;
        String options = result.group(3);
        if (options != null) {
            int index = options.length();
            while (true) {
                int i = index;
                index--;
                if (i > 0) {
                    switch (options.charAt(index)) {
                        case 'i':
                            compileOptions |= 1;
                            break;
                        case 'm':
                            compileOptions |= 8;
                            break;
                        case 's':
                            compileOptions |= 16;
                            break;
                        case 'x':
                            compileOptions |= 32;
                            break;
                        default:
                            throw new MalformedPerl5PatternException("Invalid options: " + options);
                    }
                }
            }
        }
        Pattern ret = this.__patternCache.getPattern(regex, compileOptions);
        this.__expressionCache.addElement(pattern, ret);
        return ret;
    }

    public synchronized boolean match(String pattern, char[] input) throws MalformedPerl5PatternException {
        __parseMatchExpression(pattern);
        boolean result = this.__matcher.contains(input, __parseMatchExpression(pattern));
        if (result) {
            this.__lastMatch = this.__matcher.getMatch();
        }
        return result;
    }

    public synchronized boolean match(String pattern, String input) throws MalformedPerl5PatternException {
        return match(pattern, input.toCharArray());
    }

    public synchronized MatchResult getMatch() {
        return this.__lastMatch;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized int length() {
        return this.__lastMatch.length();
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized int groups() {
        return this.__lastMatch.groups();
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized String group(int group) {
        return this.__lastMatch.group(group);
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized int begin(int group) {
        return this.__lastMatch.begin(group);
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized int end(int group) {
        return this.__lastMatch.end(group);
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized int beginOffset(int group) {
        return this.__lastMatch.beginOffset(group);
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized int endOffset(int group) {
        return this.__lastMatch.endOffset(group);
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public synchronized String toString() {
        if (this.__lastMatch == null) {
            return null;
        }
        return this.__lastMatch.toString();
    }
}
