package org.apache.commons.codec.language.bm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/bm/RuleType.class */
public enum RuleType {
    APPROX("approx"),
    EXACT("exact"),
    RULES("rules");

    private final String name;

    RuleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
