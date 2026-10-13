package org.apache.commons.codec.language.bm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/bm/PhoneticEngine.class */
public class PhoneticEngine {
    private static final int DEFAULT_MAX_PHONEMES = 20;
    private static final Map<NameType, Set<String>> NAME_PREFIXES = new EnumMap(NameType.class);
    private final Lang lang;
    private final NameType nameType;
    private final RuleType ruleType;
    private final boolean concat;
    private final int maxPhonemes;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/bm/PhoneticEngine$PhonemeBuilder.class */
    static final class PhonemeBuilder {
        private final Set<Rule.Phoneme> phonemes;

        public static PhonemeBuilder empty(Languages.LanguageSet languages) {
            return new PhonemeBuilder(new Rule.Phoneme("", languages));
        }

        private PhonemeBuilder(Rule.Phoneme phoneme) {
            this.phonemes = new LinkedHashSet();
            this.phonemes.add(phoneme);
        }

        private PhonemeBuilder(Set<Rule.Phoneme> phonemes) {
            this.phonemes = phonemes;
        }

        public void append(CharSequence str) {
            this.phonemes.forEach(ph -> {
                ph.append(str);
            });
        }

        public void apply(Rule.PhonemeExpr phonemeExpr, int maxPhonemes) {
            Set<Rule.Phoneme> newPhonemes = new LinkedHashSet<>(Math.min(this.phonemes.size() * phonemeExpr.size(), maxPhonemes));
            loop0: for (Rule.Phoneme left : this.phonemes) {
                for (Rule.Phoneme right : phonemeExpr.getPhonemes()) {
                    Languages.LanguageSet languages = left.getLanguages().restrictTo(right.getLanguages());
                    if (!languages.isEmpty()) {
                        Rule.Phoneme join = new Rule.Phoneme(left, right, languages);
                        if (newPhonemes.size() < maxPhonemes) {
                            newPhonemes.add(join);
                            if (newPhonemes.size() >= maxPhonemes) {
                                break loop0;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            this.phonemes.clear();
            this.phonemes.addAll(newPhonemes);
        }

        public Set<Rule.Phoneme> getPhonemes() {
            return this.phonemes;
        }

        public String makeString() {
            return (String) this.phonemes.stream().map((v0) -> {
                return v0.getPhonemeText();
            }).collect(Collectors.joining("|"));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/bm/PhoneticEngine$RulesApplication.class */
    private static final class RulesApplication {
        private final Map<String, List<Rule>> finalRules;
        private final CharSequence input;
        private final PhonemeBuilder phonemeBuilder;
        private int i;
        private final int maxPhonemes;
        private boolean found;

        public RulesApplication(Map<String, List<Rule>> finalRules, CharSequence input, PhonemeBuilder phonemeBuilder, int i, int maxPhonemes) {
            Objects.requireNonNull(finalRules, "finalRules");
            this.finalRules = finalRules;
            this.phonemeBuilder = phonemeBuilder;
            this.input = input;
            this.i = i;
            this.maxPhonemes = maxPhonemes;
        }

        public int getI() {
            return this.i;
        }

        public PhonemeBuilder getPhonemeBuilder() {
            return this.phonemeBuilder;
        }

        public RulesApplication invoke() {
            this.found = false;
            int patternLength = 1;
            List<Rule> rules = this.finalRules.get(this.input.subSequence(this.i, this.i + 1));
            if (rules != null) {
                for (Rule rule : rules) {
                    String pattern = rule.getPattern();
                    patternLength = pattern.length();
                    if (rule.patternAndContextMatches(this.input, this.i)) {
                        this.phonemeBuilder.apply(rule.getPhoneme(), this.maxPhonemes);
                        this.found = true;
                        break;
                    }
                }
            }
            if (!this.found) {
                patternLength = 1;
            }
            this.i += patternLength;
            return this;
        }

        public boolean isFound() {
            return this.found;
        }
    }

    static {
        NAME_PREFIXES.put(NameType.ASHKENAZI, Collections.unmodifiableSet(new HashSet(Arrays.asList("bar", "ben", "da", "de", "van", "von"))));
        NAME_PREFIXES.put(NameType.SEPHARDIC, Collections.unmodifiableSet(new HashSet(Arrays.asList("al", "el", "da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))));
        NAME_PREFIXES.put(NameType.GENERIC, Collections.unmodifiableSet(new HashSet(Arrays.asList("da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))));
    }

    private static String join(List<String> strings, String sep) {
        return (String) strings.stream().collect(Collectors.joining(sep));
    }

    public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concatenate) {
        this(nameType, ruleType, concatenate, 20);
    }

    public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concatenate, int maxPhonemes) {
        if (ruleType == RuleType.RULES) {
            throw new IllegalArgumentException("ruleType must not be " + RuleType.RULES);
        }
        this.nameType = nameType;
        this.ruleType = ruleType;
        this.concat = concatenate;
        this.lang = Lang.instance(nameType);
        this.maxPhonemes = maxPhonemes;
    }

    private PhonemeBuilder applyFinalRules(PhonemeBuilder phonemeBuilder, Map<String, List<Rule>> finalRules) {
        Objects.requireNonNull(finalRules, "finalRules");
        if (finalRules.isEmpty()) {
            return phonemeBuilder;
        }
        Map<Rule.Phoneme, Rule.Phoneme> phonemes = new TreeMap<>(Rule.Phoneme.COMPARATOR);
        phonemeBuilder.getPhonemes().forEach(phoneme -> {
            PhonemeBuilder subBuilder = PhonemeBuilder.empty(phoneme.getLanguages());
            String phonemeText = phoneme.getPhonemeText().toString();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < phonemeText.length()) {
                    RulesApplication rulesApplication = new RulesApplication(finalRules, phonemeText, subBuilder, i2, this.maxPhonemes).invoke();
                    boolean found = rulesApplication.isFound();
                    subBuilder = rulesApplication.getPhonemeBuilder();
                    if (!found) {
                        subBuilder.append(phonemeText.subSequence(i2, i2 + 1));
                    }
                    i = rulesApplication.getI();
                } else {
                    subBuilder.getPhonemes().forEach(newPhoneme -> {
                        if (phonemes.containsKey(newPhoneme)) {
                            Rule.Phoneme oldPhoneme = (Rule.Phoneme) phonemes.remove(newPhoneme);
                            Rule.Phoneme mergedPhoneme = oldPhoneme.mergeWithLanguage(newPhoneme.getLanguages());
                            phonemes.put(mergedPhoneme, mergedPhoneme);
                            return;
                        }
                        phonemes.put(newPhoneme, newPhoneme);
                    });
                    return;
                }
            }
        });
        return new PhonemeBuilder(phonemes.keySet());
    }

    public String encode(String input) {
        Languages.LanguageSet languageSet = this.lang.guessLanguages(input);
        return encode(input, languageSet);
    }

    public String encode(String input, Languages.LanguageSet languageSet) {
        Map<String, List<Rule>> rules = Rule.getInstanceMap(this.nameType, RuleType.RULES, languageSet);
        Map<String, List<Rule>> finalRules1 = Rule.getInstanceMap(this.nameType, this.ruleType, "common");
        Map<String, List<Rule>> finalRules2 = Rule.getInstanceMap(this.nameType, this.ruleType, languageSet);
        String input2 = input.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();
        if (this.nameType == NameType.GENERIC) {
            if (input2.startsWith("d'")) {
                String remainder = input2.substring(2);
                String combined = OperatorName.SET_LINE_DASHPATTERN + remainder;
                return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
            }
            for (String l : NAME_PREFIXES.get(this.nameType)) {
                if (input2.startsWith(l + " ")) {
                    String remainder2 = input2.substring(l.length() + 1);
                    String combined2 = l + remainder2;
                    return "(" + encode(remainder2) + ")-(" + encode(combined2) + ")";
                }
            }
        }
        List<String> words = Arrays.asList(input2.split("\\s+"));
        List<String> words2 = new ArrayList<>();
        switch (this.nameType) {
            case SEPHARDIC:
                words.forEach(aWord -> {
                    String[] parts = aWord.split(OperatorName.SHOW_TEXT_LINE, -1);
                    words2.add(parts[parts.length - 1]);
                });
                words2.removeAll(NAME_PREFIXES.get(this.nameType));
                break;
            case ASHKENAZI:
                words2.addAll(words);
                words2.removeAll(NAME_PREFIXES.get(this.nameType));
                break;
            case GENERIC:
                words2.addAll(words);
                break;
            default:
                throw new IllegalStateException("Unreachable case: " + this.nameType);
        }
        if (this.concat) {
            input2 = join(words2, " ");
        } else if (words2.size() == 1) {
            input2 = words.iterator().next();
        } else if (!words2.isEmpty()) {
            StringBuilder result = new StringBuilder();
            words2.forEach(word -> {
                result.append("-").append(encode(word));
            });
            return result.substring(1);
        }
        PhonemeBuilder phonemeBuilder = PhonemeBuilder.empty(languageSet);
        int i = 0;
        while (i < input2.length()) {
            RulesApplication rulesApplication = new RulesApplication(rules, input2, phonemeBuilder, i, this.maxPhonemes).invoke();
            i = rulesApplication.getI();
            phonemeBuilder = rulesApplication.getPhonemeBuilder();
        }
        return applyFinalRules(applyFinalRules(phonemeBuilder, finalRules1), finalRules2).makeString();
    }

    public Lang getLang() {
        return this.lang;
    }

    public int getMaxPhonemes() {
        return this.maxPhonemes;
    }

    public NameType getNameType() {
        return this.nameType;
    }

    public RuleType getRuleType() {
        return this.ruleType;
    }

    public boolean isConcat() {
        return this.concat;
    }
}
