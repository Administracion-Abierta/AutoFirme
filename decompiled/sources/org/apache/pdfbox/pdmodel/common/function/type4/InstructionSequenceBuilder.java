package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/type4/InstructionSequenceBuilder.class */
public final class InstructionSequenceBuilder extends Parser.AbstractSyntaxHandler {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("[\\+\\-]?\\d+");
    private static final Pattern REAL_PATTERN = Pattern.compile("[\\-]?\\d*\\.\\d*([Ee]\\-?\\d+)?");
    private final InstructionSequence mainSequence = new InstructionSequence();
    private final Stack<InstructionSequence> seqStack = new Stack<>();

    private InstructionSequenceBuilder() {
        this.seqStack.push(this.mainSequence);
    }

    public InstructionSequence getInstructionSequence() {
        return this.mainSequence;
    }

    public static InstructionSequence parse(CharSequence text) {
        InstructionSequenceBuilder builder = new InstructionSequenceBuilder();
        Parser.parse(text, builder);
        return builder.getInstructionSequence();
    }

    private InstructionSequence getCurrentSequence() {
        return this.seqStack.peek();
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.type4.Parser.SyntaxHandler
    public void token(CharSequence text) {
        String token = text.toString();
        token(token);
    }

    private void token(String token) {
        if ("{".equals(token)) {
            InstructionSequence child = new InstructionSequence();
            getCurrentSequence().addProc(child);
            this.seqStack.push(child);
        } else {
            if ("}".equals(token)) {
                this.seqStack.pop();
                return;
            }
            Matcher m = INTEGER_PATTERN.matcher(token);
            if (m.matches()) {
                getCurrentSequence().addInteger(parseInt(token));
                return;
            }
            Matcher m2 = REAL_PATTERN.matcher(token);
            if (m2.matches()) {
                getCurrentSequence().addReal(parseReal(token));
            } else {
                getCurrentSequence().addName(token);
            }
        }
    }

    public static int parseInt(String token) {
        return Integer.parseInt(token.startsWith(Marker.ANY_NON_NULL_MARKER) ? token.substring(1) : token);
    }

    public static float parseReal(String token) {
        return Float.parseFloat(token);
    }
}
