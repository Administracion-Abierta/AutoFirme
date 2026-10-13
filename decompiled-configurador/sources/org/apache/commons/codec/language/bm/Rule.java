package org.apache.commons.codec.language.bm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.Resources;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Rule.class */
public class Rule {
    public static final String ALL = "ALL";
    private static final String DOUBLE_QUOTE = "\"";
    private final RPattern lContext;
    private final String pattern;
    private final PhonemeExpr phoneme;
    private final RPattern rContext;
    public static final RPattern ALL_STRINGS_RMATCHER = input -> {
        return true;
    };
    private static final String HASH_INCLUDE = "#include";
    private static final int HASH_INCLUDE_LENGTH = HASH_INCLUDE.length();
    private static final Map<NameType, Map<RuleType, Map<String, Map<String, List<Rule>>>>> RULES = new EnumMap(NameType.class);

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Rule$RPattern.class */
    public interface RPattern {
        boolean isMatch(CharSequence charSequence);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Rule$Phoneme.class */
    public static final class Phoneme implements PhonemeExpr {
        public static final Comparator<Phoneme> COMPARATOR = (o1, o2) -> {
            int o1Length = o1.phonemeText.length();
            int o2Length = o2.phonemeText.length();
            for (int i = 0; i < o1Length; i++) {
                if (i >= o2Length) {
                    return 1;
                }
                int c = o1.phonemeText.charAt(i) - o2.phonemeText.charAt(i);
                if (c != 0) {
                    return c;
                }
            }
            if (o1Length < o2Length) {
                return -1;
            }
            return 0;
        };
        private final StringBuilder phonemeText;
        private final Languages.LanguageSet languages;

        public Phoneme(CharSequence phonemeText, Languages.LanguageSet languages) {
            this.phonemeText = new StringBuilder(phonemeText);
            this.languages = languages;
        }

        public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight) {
            this(phonemeLeft.phonemeText, phonemeLeft.languages);
            this.phonemeText.append((CharSequence) phonemeRight.phonemeText);
        }

        public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight, Languages.LanguageSet languages) {
            this(phonemeLeft.phonemeText, languages);
            this.phonemeText.append((CharSequence) phonemeRight.phonemeText);
        }

        public Phoneme append(CharSequence str) {
            this.phonemeText.append(str);
            return this;
        }

        public Languages.LanguageSet getLanguages() {
            return this.languages;
        }

        @Override // org.apache.commons.codec.language.bm.Rule.PhonemeExpr
        public Iterable<Phoneme> getPhonemes() {
            return Collections.singleton(this);
        }

        public CharSequence getPhonemeText() {
            return this.phonemeText;
        }

        @Deprecated
        public Phoneme join(Phoneme right) {
            return new Phoneme(this.phonemeText.toString() + right.phonemeText.toString(), this.languages.restrictTo(right.languages));
        }

        public Phoneme mergeWithLanguage(Languages.LanguageSet lang) {
            return new Phoneme(this.phonemeText.toString(), this.languages.merge(lang));
        }

        @Override // org.apache.commons.codec.language.bm.Rule.PhonemeExpr
        public int size() {
            return 1;
        }

        public String toString() {
            return this.phonemeText.toString() + "[" + this.languages + "]";
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Rule$PhonemeExpr.class */
    public interface PhonemeExpr {
        Iterable<Phoneme> getPhonemes();

        default int size() {
            return (int) Math.min(getPhonemes().spliterator().getExactSizeIfKnown(), 2147483647L);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Rule$PhonemeList.class */
    public static final class PhonemeList implements PhonemeExpr {
        private final List<Phoneme> phonemeList;

        public PhonemeList(List<Phoneme> phonemes) {
            this.phonemeList = phonemes;
        }

        @Override // org.apache.commons.codec.language.bm.Rule.PhonemeExpr
        public List<Phoneme> getPhonemes() {
            return this.phonemeList;
        }

        @Override // org.apache.commons.codec.language.bm.Rule.PhonemeExpr
        public int size() {
            return this.phonemeList.size();
        }
    }

    static {
        for (NameType s : NameType.values()) {
            Map<RuleType, Map<String, Map<String, List<Rule>>>> rts = new EnumMap<>(RuleType.class);
            for (RuleType rt : RuleType.values()) {
                Map<String, Map<String, List<Rule>>> rs = new HashMap<>();
                Languages ls = Languages.getInstance(s);
                ls.getLanguages().forEach(l -> {
                    try {
                        Scanner scanner = createScanner(s, rt, l);
                        try {
                            rs.put(l, parseRules(scanner, createResourceName(s, rt, l)));
                            if (scanner != null) {
                                scanner.close();
                            }
                        } catch (Throwable th) {
                            if (scanner != null) {
                                try {
                                    scanner.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("Problem processing " + createResourceName(s, rt, l), e);
                    }
                });
                if (!rt.equals(RuleType.RULES)) {
                    Scanner scanner = createScanner(s, rt, "common");
                    try {
                        rs.put("common", parseRules(scanner, createResourceName(s, rt, "common")));
                        if (scanner != null) {
                            scanner.close();
                        }
                    } catch (Throwable th) {
                        if (scanner != null) {
                            try {
                                scanner.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                rts.put(rt, Collections.unmodifiableMap(rs));
            }
            RULES.put(s, Collections.unmodifiableMap(rts));
        }
    }

    private static boolean contains(CharSequence chars, char input) {
        return chars.chars().anyMatch(c -> {
            return c == input;
        });
    }

    private static String createResourceName(NameType nameType, RuleType rt, String lang) {
        return String.format("org/apache/commons/codec/language/bm/%s_%s_%s.txt", nameType.getName(), rt.getName(), lang);
    }

    private static Scanner createScanner(NameType nameType, RuleType rt, String lang) {
        String resName = createResourceName(nameType, rt, lang);
        return new Scanner(Resources.getInputStream(resName), ResourceConstants.ENCODING);
    }

    private static Scanner createScanner(String lang) {
        String resName = String.format("org/apache/commons/codec/language/bm/%s.txt", lang);
        return new Scanner(Resources.getInputStream(resName), ResourceConstants.ENCODING);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean endsWith(CharSequence input, CharSequence suffix) {
        int suffixLength = suffix.length();
        int inputLength = input.length();
        if (suffixLength > inputLength) {
            return false;
        }
        int i = inputLength - 1;
        for (int j = suffixLength - 1; j >= 0; j--) {
            if (input.charAt(i) == suffix.charAt(j)) {
                i--;
            } else {
                return false;
            }
        }
        return true;
    }

    public static List<Rule> getInstance(NameType nameType, RuleType rt, Languages.LanguageSet langs) {
        Map<String, List<Rule>> ruleMap = getInstanceMap(nameType, rt, langs);
        List<Rule> allRules = new ArrayList<>();
        ruleMap.values().forEach(rules -> {
            allRules.addAll(rules);
        });
        return allRules;
    }

    public static List<Rule> getInstance(NameType nameType, RuleType rt, String lang) {
        return getInstance(nameType, rt, Languages.LanguageSet.from(new HashSet(Arrays.asList(lang))));
    }

    public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, Languages.LanguageSet langs) {
        return langs.isSingleton() ? getInstanceMap(nameType, rt, langs.getAny()) : getInstanceMap(nameType, rt, Languages.ANY);
    }

    public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, String lang) {
        Map<String, List<Rule>> rules = RULES.get(nameType).get(rt).get(lang);
        if (rules == null) {
            throw new IllegalArgumentException(String.format("No rules found for %s, %s, %s.", nameType.getName(), rt.getName(), lang));
        }
        return rules;
    }

    private static Phoneme parsePhoneme(String ph) {
        int open = ph.indexOf("[");
        if (open >= 0) {
            if (!ph.endsWith("]")) {
                throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
            }
            String before = ph.substring(0, open);
            String in = ph.substring(open + 1, ph.length() - 1);
            Set<String> langs = new HashSet<>(Arrays.asList(in.split("[+]")));
            return new Phoneme(before, Languages.LanguageSet.from(langs));
        }
        return new Phoneme(ph, Languages.ANY_LANGUAGE);
    }

    private static PhonemeExpr parsePhonemeExpr(String ph) {
        if (ph.startsWith("(")) {
            if (!ph.endsWith(")")) {
                throw new IllegalArgumentException("Phoneme starts with '(' so must end with ')'");
            }
            List<Phoneme> phs = new ArrayList<>();
            String body = ph.substring(1, ph.length() - 1);
            for (String part : body.split("[|]")) {
                phs.add(parsePhoneme(part));
            }
            if (body.startsWith("|") || body.endsWith("|")) {
                phs.add(new Phoneme("", Languages.ANY_LANGUAGE));
            }
            return new PhonemeList(phs);
        }
        return parsePhoneme(ph);
    }

    private static Map<String, List<Rule>> parseRules(Scanner scanner, final String location) {
        Map<String, List<Rule>> lines = new HashMap<>();
        final int currentLine = 0;
        boolean inMultilineComment = false;
        while (scanner.hasNextLine()) {
            currentLine++;
            String rawLine = scanner.nextLine();
            String line = rawLine;
            if (inMultilineComment) {
                if (line.endsWith("*/")) {
                    inMultilineComment = false;
                }
            } else if (line.startsWith("/*")) {
                inMultilineComment = true;
            } else {
                int cmtI = line.indexOf("//");
                if (cmtI >= 0) {
                    line = line.substring(0, cmtI);
                }
                String line2 = line.trim();
                if (line2.isEmpty()) {
                    continue;
                } else if (line2.startsWith(HASH_INCLUDE)) {
                    String incl = line2.substring(HASH_INCLUDE_LENGTH).trim();
                    if (incl.contains(" ")) {
                        throw new IllegalArgumentException("Malformed import statement '" + rawLine + "' in " + location);
                    }
                    Scanner hashIncludeScanner = createScanner(incl);
                    try {
                        lines.putAll(parseRules(hashIncludeScanner, location + "->" + incl));
                        if (hashIncludeScanner != null) {
                            hashIncludeScanner.close();
                        }
                    } catch (Throwable th) {
                        if (hashIncludeScanner != null) {
                            try {
                                hashIncludeScanner.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } else {
                    String[] parts = line2.split("\\s+");
                    if (parts.length != 4) {
                        throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
                    }
                    try {
                        final String pat = stripQuotes(parts[0]);
                        final String lCon = stripQuotes(parts[1]);
                        final String rCon = stripQuotes(parts[2]);
                        PhonemeExpr ph = parsePhonemeExpr(stripQuotes(parts[3]));
                        Rule r = new Rule(pat, lCon, rCon, ph) { // from class: org.apache.commons.codec.language.bm.Rule.1
                            private final int myLine;
                            private final String loc;

                            {
                                this.myLine = currentLine;
                                this.loc = location;
                            }

                            public String toString() {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Rule");
                                sb.append("{line=").append(this.myLine);
                                sb.append(", loc='").append(this.loc).append('\'');
                                sb.append(", pat='").append(pat).append('\'');
                                sb.append(", lcon='").append(lCon).append('\'');
                                sb.append(", rcon='").append(rCon).append('\'');
                                sb.append('}');
                                return sb.toString();
                            }
                        };
                        String patternKey = r.pattern.substring(0, 1);
                        List<Rule> rules = lines.computeIfAbsent(patternKey, k -> {
                            return new ArrayList();
                        });
                        rules.add(r);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
                    }
                }
            }
        }
        return lines;
    }

    private static RPattern pattern(final String regex) {
        boolean startsWith = regex.startsWith("^");
        boolean endsWith = regex.endsWith("$");
        String content = regex.substring(startsWith ? 1 : 0, endsWith ? regex.length() - 1 : regex.length());
        boolean boxes = content.contains("[");
        if (!boxes) {
            if (startsWith && endsWith) {
                if (content.isEmpty()) {
                    return input -> {
                        return input.length() == 0;
                    };
                }
                return input2 -> {
                    return input2.equals(content);
                };
            }
            if ((startsWith || endsWith) && content.isEmpty()) {
                return ALL_STRINGS_RMATCHER;
            }
            if (startsWith) {
                return input3 -> {
                    return startsWith(input3, content);
                };
            }
            if (endsWith) {
                return input4 -> {
                    return endsWith(input4, content);
                };
            }
        } else {
            boolean startsWithBox = content.startsWith("[");
            boolean endsWithBox = content.endsWith("]");
            if (startsWithBox && endsWithBox) {
                String boxContent = content.substring(1, content.length() - 1);
                if (!boxContent.contains("[")) {
                    boolean negate = boxContent.startsWith("^");
                    if (negate) {
                        boxContent = boxContent.substring(1);
                    }
                    String bContent = boxContent;
                    boolean shouldMatch = !negate;
                    if (startsWith && endsWith) {
                        return input5 -> {
                            return input5.length() == 1 && contains(bContent, input5.charAt(0)) == shouldMatch;
                        };
                    }
                    if (startsWith) {
                        return input6 -> {
                            return input6.length() > 0 && contains(bContent, input6.charAt(0)) == shouldMatch;
                        };
                    }
                    if (endsWith) {
                        return input7 -> {
                            return input7.length() > 0 && contains(bContent, input7.charAt(input7.length() - 1)) == shouldMatch;
                        };
                    }
                }
            }
        }
        return new RPattern() { // from class: org.apache.commons.codec.language.bm.Rule.2
            final Pattern pattern;

            {
                this.pattern = Pattern.compile(regex);
            }

            @Override // org.apache.commons.codec.language.bm.Rule.RPattern
            public boolean isMatch(CharSequence input8) {
                Matcher matcher = this.pattern.matcher(input8);
                return matcher.find();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean startsWith(CharSequence input, CharSequence prefix) {
        if (prefix.length() > input.length()) {
            return false;
        }
        for (int i = 0; i < prefix.length(); i++) {
            if (input.charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static String stripQuotes(String str) {
        if (str.startsWith(DOUBLE_QUOTE)) {
            str = str.substring(1);
        }
        if (str.endsWith(DOUBLE_QUOTE)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme) {
        this.pattern = pattern;
        this.lContext = pattern(lContext + "$");
        this.rContext = pattern("^" + rContext);
        this.phoneme = phoneme;
    }

    public RPattern getLContext() {
        return this.lContext;
    }

    public String getPattern() {
        return this.pattern;
    }

    public PhonemeExpr getPhoneme() {
        return this.phoneme;
    }

    public RPattern getRContext() {
        return this.rContext;
    }

    public boolean patternAndContextMatches(CharSequence input, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("Can not match pattern at negative indexes");
        }
        int patternLength = this.pattern.length();
        int ipl = i + patternLength;
        if (ipl > input.length() || !input.subSequence(i, ipl).equals(this.pattern) || !this.rContext.isMatch(input.subSequence(ipl, input.length()))) {
            return false;
        }
        return this.lContext.isMatch(input.subSequence(0, i));
    }
}
